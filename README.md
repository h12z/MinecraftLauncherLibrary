Disclaimer: Only tested with 1.19.x Vanilla

The Official Minecraft Launcher has to be installed.

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
