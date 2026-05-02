
# flutter_android_package_installer

本库基于[android_package_installer](https://pub.dev/packages/android_package_installer)改造, 适配最新`Flutter 3.24.0`版本, 并修改了以下内容:

- 原库的`installApk`方法调用的是`PackageInstaller.Session`方法实现的. [PackageInstaller](https://developer.android.com/reference/android/content/pm/PackageInstaller) 我这里测试就没成功过. 所以这里更改了此方法的默认实现为`Intent.ACTION_VIEW`
- 原库的`installApk`方法改名为`installApkSession`
- 修改`compileSdk 35`
- 新增`getPlatformVersion`方法
- 新增`openAppMarket`方法
- 新增`openAppSettingDetail`方法

## 补充一点

`FileProvider`资源配置需要包含`root-path`:

```xml
<?xml version="1.0" encoding="utf-8"?>
<paths xmlns:android="http://schemas.android.com/apk/res/android">
    <external-path name="external_files" path="."/>
    <root-path name="root" path="" />
</paths>
```

---

# android_package_installer
A Flutter plugin for installing Android package from apk file. Plugin uses Android Package Installer and
requires **minimum API Level version 21**.

## Using
```dart
import 'package:flutter_android_package_installer/android_package_installer.dart';

  int? statusCode = await AndroidPackageInstaller.installApk(apkFilePath: '/sdcard/Download/com.example.apk');
  if (statusCode != null) {
    PackageInstallerStatus installationStatus = PackageInstallerStatus.byCode(statusCode);
    print(installationStatus.name);
  }
```
To install the Android package the application will need permissions.
You can use the `permission_handler` package to request them.

## Setup
1. Add the permissions to your AndroidManifest.xml file in `<projectDir>/android/app/src/main/AndroidManifest.xml`:
    * `android.permission.REQUEST_INSTALL_PACKAGES` - required for installing android packages.
    * `android.permission.READ_EXTERNAL_STORAGE` - required to access the external storage where the apk file is located.

```xml
<manifest xmlns:tools="http://schemas.android.com/tools" ...>

  <!-- ADD THESE PERMISSIONS: -->
  <!-- Android 10 Permissions -->
<uses-permission android:name="android.permission.READ_EXTERNAL_STORAGE"/>
  <!-- Android 11+ Permissions -->
<uses-permission android:name="android.permission.REQUEST_INSTALL_PACKAGES"/>
<uses-permission
android:name="android.permission.MANAGE_EXTERNAL_STORAGE"
tools:ignore="ScopedStorage"/>
  
  <application ...>
    <activity ...>

      ...

      <!-- ADD THIS INTENT FILTER -->
      <intent-filter>
        <action android:name="com.android_package_installer.content.SESSION_API_PACKAGE_INSTALLED"/>
      </intent-filter>
    </activity>

    <!-- ADD THIS PROVIDER -->
    <provider
      android:name="androidx.core.content.FileProvider"
      android:authorities="${applicationId}"
      android:grantUriPermissions="true">
      <meta-data
        android:name="android.support.FILE_PROVIDER_PATHS"
        android:resource="@xml/file_paths"/>
    </provider>
  </application>
</manifest>
```

2. Check external path in your custom paths file. If it doesn't exist, create it in `<projectDir>/android/app/src/main/res/xml/file_paths.xml`:
```xml
<?xml version="1.0" encoding="utf-8"?>
<paths xmlns:android="http://schemas.android.com/apk/res/android">
    <external-path name="external_files" path="."/>
</paths>
```

