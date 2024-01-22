package me.h12z.LauncherLib;

import me.h12z.LauncherLib.Args.LaunchArgs;
import me.h12z.LauncherLib.Args.Libraries;
import org.json.simple.parser.ParseException;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintStream;

public class Launcher {

    private File jar;
    private File json;
    private String launchArgs;

    public Launcher(String gameDirectory, String versionName, String playerName, String uuid, String accessToken) throws IOException, ParseException {

        boolean downloadLibs = true;

        String sep = File.separator;
        jar = new File(gameDirectory + sep + "versions" + sep + versionName + sep + versionName + ".jar");
        json = new File(gameDirectory + sep + "versions" + sep + versionName + sep + versionName + ".json");

        File mkdirs = new File(gameDirectory + sep + "clientLibs");
        mkdirs.mkdirs();

        if(downloadLibs) {
            Libraries.downloadLibraries(Libraries.getLibraryDownloads(json), Libraries.getLibraryPaths(json, gameDirectory + File.separator + "clientLibs"));
        }

        String[] libsArray = Libraries.getLibraryPaths(json, gameDirectory + File.separator + "clientLibs");
        String libs = "";
        for(int i = 0; i < libsArray.length; i++) {
            libs = libs + libsArray[i] + ";";
        }

        launchArgs = "java -cp " + libs + gameDirectory + "/versions/" + versionName + "/" + versionName + ".jar " + LaunchArgs.getMainClassPath(json) + LaunchArgs.getLaunchArgs(json);

        launchArgs = launchArgs.replace("${natives_directory}", gameDirectory + "/bin/natives");
        launchArgs = launchArgs.replace("${launcher_name}", "Launcher");
        launchArgs = launchArgs.replace("${launcher_version}", "1");
        launchArgs = launchArgs.replace("${auth_player_name}", playerName);
        launchArgs = launchArgs.replace("${auth_access_token}", accessToken);
        launchArgs = launchArgs.replace("${auth_uuid}", uuid);
        launchArgs = launchArgs.replace("${assets_root}", gameDirectory + sep + "assets");
        launchArgs = launchArgs.replace("${assets_index_name}", "1.20");
        launchArgs = launchArgs.replace("${user_type}", "microsoft");
        launchArgs = launchArgs.replace("${version_name}", versionName);
        launchArgs = launchArgs.replace("${game_directory}", gameDirectory);
        launchArgs = launchArgs.replace("--clientId ${clientid} ", "");
        launchArgs = launchArgs.replace("--xuid ${auth_xuid} ", "");
        launchArgs = launchArgs.replace("--versionType ${version_type} ", "");
        launchArgs = launchArgs.replace("//", "/");


        System.out.println(launchArgs);
        System.out.println(LaunchArgs.getLaunchArgs(json));

    }

    public void launch() throws IOException {

        Runtime rt = Runtime.getRuntime();
        Process pr = rt.exec(launchArgs);

    }

}
