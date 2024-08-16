import 'package:flutter/foundation.dart';
import 'package:flutter/services.dart';

import 'installer_platform.dart';

class MethodChannelAndroidPackageInstaller
    extends AndroidPackageInstallerPlatform {
  final methodChannel = const MethodChannel('android_package_installer');

  @override
  Future<int?> installApkSession(String path) async {
    final result =
        await methodChannel.invokeMethod<int>('installApkSession', path);
    return result;
  }

  @override
  Future<int?> installApk(String path) async {
    final result = await methodChannel.invokeMethod<int>('installApk', path);
    return result;
  }

  @override
  Future<String?> platformVersion() async {
    final String? version =
        await methodChannel.invokeMethod('getPlatformVersion');
    return version;
  }

  /// 打开应用市场-当前应用详情页面
  ///
  /// 仅支持Android
  @override
  Future<bool> openAppMarket({
    String applicationPackageName = "",
    String targetMarketPackageName = "",
    bool isOpenSystemMarket = true,
  }) async {
    if (defaultTargetPlatform != TargetPlatform.android) {
      return false;
    }
    final arguments = <String, dynamic>{
      "applicationPackageName": applicationPackageName,
      "targetMarketPackageName": targetMarketPackageName,
      "isOpenSystemMarket": isOpenSystemMarket,
    };
    try {
      final result =
          await methodChannel.invokeMethod("openAppMarket", arguments);
      if (result is bool) {
        return true;
      }
      return false;
    } catch (e) {
      debugPrint("EasyAppInstaller.openAppMarket: $e");
      return false;
    }
  }

  /// 打开设置-应用详情页
  ///
  /// iOS 仅支持打开当前应用设置页面，无需传值
  /// Android 支持打开指定应用详情页，传入对应包名即可；不传默认打开当前应用的设置页
  @override
  Future<bool> openAppSettingDetails({
    String applicationPackageName = "",
  }) async {
    final arguments = <String, dynamic>{
      "applicationPackageName": applicationPackageName,
    };
    try {
      final result =
          await methodChannel.invokeMethod("openAppSettingDetail", arguments);
      if (result is bool) {
        return true;
      }
      return false;
    } catch (e) {
      debugPrint("EasyAppInstaller.openAppSettingDetails: $e");
      return false;
    }
  }
}
