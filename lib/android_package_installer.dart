import 'package:android_package_installer/src/installer_platform.dart';

export 'package:android_package_installer/src/enums.dart';

class AndroidPackageInstaller {
  /// Installs apk file using Android Package Manager.
  /// Creates Package Installer session, then displays a dialog to confirm the installation.
  /// After installation process is completed, the session is closed.
  /// [apkFilePath] - the path to the apk package file. Example: /sdcard/Download/app.apk
  /// Returns session result code.
  static Future<int?> installApkSession({required String apkFilePath}) {
    Future<int?> code =
        AndroidPackageInstallerPlatform.instance.installApkSession(apkFilePath);
    return code;
  }

  static Future<int?> installApk({required String apkFilePath}) {
    Future<int?> code = AndroidPackageInstallerPlatform.instance.installApk(
      apkFilePath,
    );
    return code;
  }

  static Future<String?> get platformVersion async {
    final String? version =
        await AndroidPackageInstallerPlatform.instance.platformVersion();
    return version;
  }

  static Future<bool> openAppMarket({
    String applicationPackageName = "",
    String targetMarketPackageName = "",
    bool isOpenSystemMarket = true,
  }) async {
    return AndroidPackageInstallerPlatform.instance.openAppMarket(
      applicationPackageName: applicationPackageName,
      targetMarketPackageName: targetMarketPackageName,
      isOpenSystemMarket: isOpenSystemMarket,
    );
  }

  static Future<bool> openAppSettingDetails({
    String applicationPackageName = "",
  }) async {
    return AndroidPackageInstallerPlatform.instance.openAppSettingDetails(
      applicationPackageName: applicationPackageName,
    );
  }
}
