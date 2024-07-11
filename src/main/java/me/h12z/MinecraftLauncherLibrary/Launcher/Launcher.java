package me.h12z.MinecraftLauncherLibrary.Launcher;

import me.h12z.MinecraftLauncherLibrary.Launcher.config.ExtractJsonContents;
import me.h12z.MinecraftLauncherLibrary.Launcher.utils.StringUtils;

import java.io.File;
import java.io.IOException;

public class Launcher {

    public Launcher(String versionName, String gameDir, String launcherName, Type type) {
        new Launcher(versionName, gameDir, launcherName, type, 0, 0);
    }

    public Launcher(String versionName, String gameDir, String launcherName, Type type, int x, int y) {

        Var.configJson = new File( StringUtils.cleanup_path(gameDir) + "versions\\" + versionName + "\\" + versionName + ".json");
        Var.gameJar = new File(StringUtils.cleanup_path(gameDir) + "versions\\" + versionName + "\\" + versionName + ".jar");

        if(!Var.configJson.exists()) {
            System.err.println("Sorry but the json File for your Version doesn't exist or you put in the wrong version name or game Directory.");
            System.exit(0);
        }
        if(!Var.gameJar.exists()) {
            System.err.println("Sorry but the jar File for your Version doesn't exist or you put in the wrong version name or game Directory.");
            System.exit(0);
        }

        Var.versionType = type;

        if(type.equals(Type.FABRIC)) {
            String inheritedVersionName = ExtractJsonContents.getInheritedVersionName();
            Var.inheritedJson = new File(StringUtils.cleanup_path(gameDir) + "versions\\" + inheritedVersionName + "\\" + inheritedVersionName + ".json");
        }

        Var.libraries = ExtractJsonContents.getLibraries();
        Var.gameDir = StringUtils.cleanup_path(gameDir);
        Var.launcherName = launcherName;
        Var.name = versionName;

    }

    public void launch(String username, String accessToken, String uuid) {

        ExtractJsonContents.downloadMissing();
        Var.playerName = username;
        Var.uuid = uuid;
        Var.accessToken = accessToken;
        String gameArgs = ExtractJsonContents.getGameArgs();
        String jvmArgs = ExtractJsonContents.getJVMArgs();
        String command = "java " + jvmArgs + " " + gameArgs;

        System.out.println(command);

        try {
            Process process = Runtime.getRuntime().exec(command);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

    }

}
