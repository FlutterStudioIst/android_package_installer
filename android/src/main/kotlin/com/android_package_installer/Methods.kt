package com.android_package_installer

import io.flutter.plugin.common.MethodCall
import io.flutter.plugin.common.MethodChannel
import java.io.File

internal class MethodCallHandler(private val installer: Installer) :
    MethodChannel.MethodCallHandler {
    companion object {
        lateinit var callResult: MethodChannel.Result
        fun resultSuccess(data: Any) {
            callResult.success(data)
        }

        fun nothing() {
            callResult.notImplemented()
        }

        /*
        fun resultError(s0: String, s1: String, o: Any) {
            callResult.error(s0, s1, o)
        }
        */
    }

    override fun onMethodCall(call: MethodCall, result: MethodChannel.Result) {
        callResult = result
        when (call.method) {
            "installApkSession" -> {
                try {
                    val apkFilePath = call.arguments.toString()
                    installer.installPackage(apkFilePath)
                    resultSuccess(0)
                } catch (e: Exception) {
                    resultSuccess(installStatusUnknown)
                }
            }

            "getPlatformVersion" -> {
                result.success("Android ${android.os.Build.VERSION.RELEASE}")
            }

            "installApk" -> {
                try {
                    val apkFilePath = call.arguments.toString()
                    installer.installApk(File(apkFilePath))
                    resultSuccess(0)
                } catch (e: Exception) {
                    resultSuccess(installStatusUnknown)
                }
            }

            "openAppMarket" -> {
                val arguments = call.arguments as Map<*, *>?
                installer.openAppMarket(arguments, result)
            }

            "openAppSettingDetail" -> {
                val arguments = call.arguments as Map<*, *>?
                installer.openSettingAppDetails(arguments, result)
            }

            else -> {
                nothing()
            }
        }
    }
}
