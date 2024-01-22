package me.h12z.LauncherLib.Args;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import java.io.*;

public class LaunchArgs {

    public static String getLaunchArgs(File json) {

        String result = "";

        JSONParser jsonParser = new JSONParser();

        try (Reader reader = new FileReader(json)) {

            JSONObject jsonObject = (JSONObject) jsonParser.parse(reader);

            JSONObject args = (JSONObject) jsonObject.get("arguments");

            JSONArray game = (JSONArray) args.get("game");

            for (int i = 0; i < 22; i++) {
                if(!game.get(i).equals("--versionType") || !game.get(i).equals("${version_type}") || !game.get(i).equals("${auth_xuid}") || !game.get(i).equals("--xuid") || game.get(i).equals("--clientId") || !game.get(i).equals("${clientid}")) {
                    result = result + " " + game.get(i);
                }
            }

            JSONArray jvm = (JSONArray) args.get("jvm");

            String os = System.getProperty("os.name");

            if (os.contains("osx") || os.contains("mac")) {
                result = result + "-XstartOnFirstThread";
            } else if (os.contains("windows")) {
                result = result + "-XX:HeapDumpPath=MojangTricksIntelDriversForPerformance_javaw.exe_minecraft.exe.heapdump";
            } else if (os.contains("x86")) {
                result = result + "-Xss1M";
            }

            for (int i = 3; i < jvm.size() - 2; i++) {
                result = result + " " + jvm.get(i);
            }

        } catch (Exception e) {

            e.printStackTrace();

        }

        return result;

    }

    public static String getMainClassPath(File json) {

        String result = "";
        try {

            JSONParser jsonParser = new JSONParser();

            Reader reader = new FileReader(json);

            JSONObject jsonObject = (JSONObject) jsonParser.parse(reader);
            result = (String) jsonObject.get("mainClass");

        } catch (Exception e) {
            e.printStackTrace();
        }

        return result;

    }

}
