# UpgradeDispatcher 

UpgradeDispatcher is a update app library for Android
为IOT版本




## Getting started

Add the JitPack repository to your build file. Add it in your root build.gradle at the end of repositories:

```java
	allprojects {
		repositories {
			...
			maven { url 'https://jitpack.io' }
		}
	}
```

Add the dependency

```java
	dependencies {
	        implementation 'com.github.Sheedon:UpgradeDispatcher:1.1'
	}
```



## How to use?

1. Use UpgradeInstaller to Initialize Upgrade.

   ```java
   UpgradeInstaller.setUp(App.getInstance(), new InstallListener() {
       @Override
       public void onResultCallback(UpgradeVersionModel model) {
   		// 上一次更新结果
       }
   });
   ```

2. create UpgradeTask .

   ```
   UpgradeTask task = new UpgradeTask.Builder(this, NetVersionModel.build(2, "xxxxx.apk"))
           .build();
   ```

3. Load upgradeTask and listened result.

   ```java
   UpgradeInstaller.onReceiveUpgradeInfo(this, task, new UpgradeListener() {
       @Override
       public void onProgress(int progress) {
           Log.v("UpgradeListener", "progress:" + progress);
       }
   
       @Override
       public void onUpgradeError(String message) {
           Log.v("UpgradeListener", "message:" + message);
       }
   
       @Override
       public void onUpgradeStatus(int status) {
           Log.v("UpgradeListener", "status:" + status);
       }
   });
   ```



Other Operations

```java
// 取消更新操作
UpgradeInstaller.cancel(this);
```

