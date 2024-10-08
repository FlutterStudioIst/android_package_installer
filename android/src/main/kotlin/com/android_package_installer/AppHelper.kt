package com.android_package_installer

import android.app.Activity
import android.content.ActivityNotFoundException
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.os.Handler
import android.os.Looper
import android.provider.Settings
import android.util.Log


/**
 * App 帮助类
 */
object AppHelper {

    private val mHandler = Handler(Looper.getMainLooper())
    private val mMarketPackageNameMap = mapOf(
        "Huawei" to "com.huawei.appmarket", //华为
        "Xiaomi" to "com.xiaomi.market", //小米
        "OPPO" to "com.oppo.market", //OPPO
        "vivo" to "com.bbk.appstore", //VIVO
        "samsung" to "com.sec.android.app.samsungapps", //三星
        "Meizu" to "com.meizu.mstore", //魅族
        "Lenovo" to "com.lenovo.leos.appstore", //联想
        "google" to "com.android.vending", //谷歌
    )

    /**
     * 进入当前应用-设置-详情页面
     *
     * [applicationPackageName] 指定应用包名，如不指定，默认为当前应用
     */
    fun openAppSettingDetail(activity: Activity?, applicationPackageName: String = ""): Boolean {
        if (activity == null) {
            Log.e("Installer", "AppHelper.intoAppSettingDetail: activity is must not be null!")
            return false
        }
        return try {
            val intent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS)
            val packageName = applicationPackageName.ifEmpty {
                activity.packageName
            }
            intent.data = Uri.fromParts("package", packageName, null)
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            activity.startActivity(intent)
            true
        } catch (runtimeException: RuntimeException) {
            //smartLog { runtimeException.printStackTrace() }
            false
        }
    }

    /**
     * 打开应用市场-指定应用页面
     *
     * [applicationPackageName] 指定应用包名，如果为空，则默认打开当前应用
     * [targetMarketPackageName] 指定应用市场包名
     * [isOpenSystemMarket] 如 'targetMarketPackageName' 为空，是否打开本机自带应用市场，
     * 为true时，将直接尝试打开当前厂商系统应用商店，否则系统会弹出弹窗自行选择应用市场。
     *
     * 简单来说，如果你有指定的应用市场，就传递 'targetMarketPackageName' 为对应的包名；
     * 如果你没有指定的应用市场，但是想让大部分机型都打开厂商应用商店，那么就设置 'isOpenSystemMarket' 为true
     *
     * 如果失败，会返回false，可查看控制台异常
     */
    fun openAppMarket(
        activity: Activity?,
        applicationPackageName: String = "",
        targetMarketPackageName: String = "",
        isOpenSystemMarket: Boolean = true
    ): Boolean {
        if (activity == null) {
            Log.e("Installer", "openAppMarket: activity is null!")
            return false
        }

        return try {
            val intent = Intent(Intent.ACTION_VIEW).apply {
                if (targetMarketPackageName.isNotEmpty()) {
                    `package` = targetMarketPackageName
                } else if (isOpenSystemMarket) {
                    `package` = getAccordWithPhoneMarketPackage()
                }
                data = if (applicationPackageName.isNotEmpty()) {
                    Uri.parse("market://details?id=$applicationPackageName")
                } else {
                    Uri.parse("market://details?id=${activity.packageName}")
                }
                addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            }
            activity.startActivity(intent)
            true
        } catch (e: ActivityNotFoundException) {
            //smartLog { e.printStackTrace() }
            false
        }
    }

    /**
     * 检测当前是否在主线程
     */
    fun checkIsInMainThread(): Boolean {
        return Thread.currentThread() == Looper.getMainLooper().thread
    }

    /**
     * 切换到主线程
     */
    fun runOnUiThread(function: () -> Unit) {
        if (!checkIsInMainThread()) {
            mHandler.post {
                function()
            }
        } else {
            function()
        }
    }

    /**
     * 获取本机官方应用市场
     */
    private fun getAccordWithPhoneMarketPackage(): String? {
        for (key in mMarketPackageNameMap.keys) {
            if (Build.BRAND.equals(key, ignoreCase = true)) {
                return mMarketPackageNameMap[key]
            }
        }
        return null
    }

}