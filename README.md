Disclaimer: Only tested with 1.20.x Vanilla

The Official Minecraft Launcher has to be installed.

Implementation:
```gradle
repositories {
  maven { url 'https://jitpack.io' }
}

dependencies {
  implementation 'com.github.h12z:MinecraftLauncherLibrary:1.1.2'
}
```

Usage:
```java
public static void main(String[] args) {

  Launcher launcher;

  try {
    launcher = new Launcher("C:/Users/h12z/AppData/Roaming/.minecraft", versionID, username, uuid, accessToken);
    launcher.launch();
  } catch (Exception e) {
    throw new RuntimeException(e);
  }

}
```
