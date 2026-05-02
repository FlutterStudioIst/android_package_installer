# 0.0.3+1

- 原库的`installApk`方法调用的是`PackageInstaller.Session`方法实现的. [PackageInstaller](https://developer.android.com/reference/android/content/pm/PackageInstaller) 我这里测试就没成功过. 所以这里更改了此方法的默认实现为`Intent.ACTION_VIEW`
- 原库的`installApk`方法改名为`installApkSession`
- 修改`compileSdk 34`
- 新增`getPlatformVersion`方法
- 新增`openAppMarket`方法
- 新增`openAppSettingDetail`方法

## 0.0.3.
- **UPD**: android plugin project
- **FIX**: README.md
- **UPD**: example project

## 0.0.2.
- **FIX**: usage code in README
- **UPD**: description in pubspec.yaml

## 0.0.1
* Initial release.
