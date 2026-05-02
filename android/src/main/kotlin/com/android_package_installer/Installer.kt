package com.android_package_installer

import android.Manifest
import android.app.Activity
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.content.pm.PackageInstaller
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import androidx.core.content.FileProvider
import io.flutter.plugin.common.MethodChannel.Result
import java.io.File
import java.io.FileInputStream
import java.io.IOException

internal class Installer(private val context: Context, private var activity: Activity?) {
    private var sessionId: Int = 0
    private lateinit var session: PackageInstaller.Session
    private lateinit var packageManager: PackageManager
    private lateinit var packageInstaller: PackageInstaller

    fun setActivity(activity: Activity?) {
        this.activity = activity
    }

    fun installPackage(apkPath: String) {
        try {
            session = createSession(activity!!)
            loadAPKFile(apkPath, session)
            val intent = Intent(context, activity!!.javaClass)
            intent.action = packageInstalledAction
            val pendingIntent = PendingIntent.getActivity(context, 0, intent, PendingIntent.FLAG_MUTABLE)
            val statusReceiver = pendingIntent.intentSender
            session.commit(statusReceiver)
            session.close()
        } catch (e: IOException) {
            throw RuntimeException("IO exception", e)
        } catch (e: Exception) {
            session.abandon()
            throw e
        }
    }

    private fun createSession(activity: Activity): PackageInstaller.Session {
        try {
            packageManager = activity.packageManager
            packageInstaller = packageManager.packageInstaller
            val params = PackageInstaller.SessionParams(PackageInstaller.SessionParams.MODE_FULL_INSTALL)

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                params.setInstallReason(PackageManager.INSTALL_REASON_USER)
            }

            sessionId = packageInstaller.createSession(params)
            session = packageInstaller.openSession(sessionId)
        } catch (e: Exception) {
            throw e
        }
        return session
    }

    @Throws(IOException::class)
    private fun loadAPKFile(apkPath: String, session: PackageInstaller.Session) {
        session.openWrite("package", 0, -1).use { packageInSession ->
            FileInputStream(apkPath).use { `is` ->
                val buffer = ByteArray(16384)
                var n: Int
                var o = 1
                while (`is`.read(buffer).also { n = it } >= 0) {
                    packageInSession.write(buffer, 0, n)
                    o++
                }
            }
        }
    }

    /**
     * 打开应用市场-指定应用详情页
     */
    fun openAppMarket(arguments: Map<*, *>?, result: Result) {
        val targetMarketPackageName = arguments?.get("targetMarketPackageName") as String? ?: ""
        val isOpenSystemMarket = arguments?.get("isOpenSystemMarket") as Boolean? ?: true
        val applicationPackageName = arguments?.get("applicationPackageName") as String? ?: ""

        val openResult = AppHelper.openAppMarket(
            activity,
            applicationPackageName = applicationPackageName,
            targetMarketPackageName = targetMarketPackageName,
            isOpenSystemMarket = isOpenSystemMarket
        )

        if (openResult) {
            result.success(true)
        } else {
            result.error("openAppMarket", "open market failed!", "")
        }
    }

    /**
     * 打开设置-指定应用详情页
     */
    fun openSettingAppDetails(arguments: Map<*, *>?, result: Result) {
        val applicationPackageName = arguments?.get("applicationPackageName") as String? ?: ""

        val openAppSettingDetail =
            AppHelper.openAppSettingDetail(
                activity,
                applicationPackageName = applicationPackageName
            )
        if (openAppSettingDetail) {
            result.success(true)
        } else {
            result.error("openSettingAppDetails", "open failed", "")
        }
    }

    /**
     * 安装apk
     * 安卓7.0 需要androidx.core.content.FileProvider
     * 安卓8.0 需要请求安装权限 Manifest.permission.REQUEST_INSTALL_PACKAGES
     *
     * GooglePlay策略
     * 请主动声明[Manifest.permission.REQUEST_INSTALL_PACKAGES]权限
     *
     * https://github.com/AnyLifeZLB/DownloadInstaller
     * https://github.com/hgncxzy/InstallApk
     * */
    /*@RequiresApi(Build.VERSION_CODES.M)
    @RequiresPermission(value = Manifest.permission.REQUEST_INSTALL_PACKAGES)*/
    fun installApk(file: File?) {
        if (file == null || !file.canRead()) return
        //兼容8.0
        /*if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val hasInstallPermission: Boolean =
                context.packageManager.canRequestPackageInstalls()
            if (!hasInstallPermission) {
                //注意这个是8.0新API
                val intent = Intent(Settings.ACTION_MANAGE_UNKNOWN_APP_SOURCES)
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                context.startActivity(intent)
                return
            }
        }*/
        val intent = Intent(Intent.ACTION_VIEW)
        val type = "application/vnd.android.package-archive"
        val uri = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
            intent.addFlags(Intent.FLAG_GRANT_WRITE_URI_PERMISSION)
            try {
                FileProvider.getUriForFile(
                    activity ?: context,
                    "${(activity ?: context).packageName}.fileProvider",
                    file
                )
            } catch (e: Exception) {
                try {
                    FileProvider.getUriForFile(
                        activity ?: context,
                        "${(activity ?: context).packageName}.fileprovider",
                        file
                    )
                } catch (e: Exception) {
                    FileProvider.getUriForFile(
                        activity ?: context,
                        (activity ?: context).packageName,
                        file
                    )
                }
            }
        } else {
            Uri.fromFile(file)
        }
        intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
        intent.setDataAndType(uri, type)
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        (activity ?: context).startActivity(intent)
    }
}

