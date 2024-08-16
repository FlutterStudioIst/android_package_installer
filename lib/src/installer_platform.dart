import 'package:android_package_installer/src/method_channel.dart';
import 'package:plugin_platform_interface/plugin_platform_interface.dart';

abstract class AndroidPackageInstallerPlatform extends PlatformInterface {
  AndroidPackageInstallerPlatform() : super(token: _token);

  static final Object _token = Object();

  static AndroidPackageInstallerPlatform _instance =
      MethodChannelAndroidPackageInstaller();

  static AndroidPackageInstallerPlatform get instance => _instance;

  static set instance(AndroidPackageInstallerPlatform instance) {
    PlatformInterface.verifyToken(instance, _token);
    _instance = instance;
  }

  Future<int?> installApkSession(String path) {
    throw UnimplementedError('installApkSession() has not been implemented.');
  }

  Future<int?> installApk(String path) {
    throw UnimplementedError('installApk() has not been implemented.');
  }

  Future<String?> platformVersion() {
    throw UnimplementedError('platformVersion() has not been implemented.');
  }

  Future<bool> openAppMarket({
    String applicationPackageName = "",
    String targetMarketPackageName = "",
    bool isOpenSystemMarket = true,
  }) {
    throw UnimplementedError('openAppMarket() has not been implemented.');
  }

  Future<bool> openAppSettingDetails({
    String applicationPackageName = "",
  }) {
    throw UnimplementedError('openAppSettingDetails() has not been implemented.');
  }
}
