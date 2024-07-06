package me.h12z.LauncherLib.Launcher.config;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import me.h12z.LauncherLib.Downloader.jdl.Downloader;
import me.h12z.LauncherLib.Launcher.Library;
import me.h12z.LauncherLib.Launcher.Var;
import me.h12z.LauncherLib.Launcher.utils.FileUtils;
import me.h12z.LauncherLib.Launcher.utils.StringUtils;
import org.apache.commons.lang3.ArrayUtils;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.concurrent.atomic.AtomicInteger;

public class ExtractJsonContents {

    public static String getInheritedVersionName() {

        JsonObject json = JsonParser.parseString(FileUtils.readFile(Var.configJson)).getAsJsonObject();

        if(!json.has("inheritsFrom")) {
            System.err.println("Sorry but you maybe have put in the wrong version type.");
            System.exit(0);
        }

        return json.get("inheritsFrom").getAsString();

    }

    public static Library[] getLibraries() {

        JsonObject json = JsonParser.parseString(FileUtils.readFile(Var.configJson)).getAsJsonObject();
        JsonArray libraries = json.get("libraries").getAsJsonArray();

        Library[] result = new Library[libraries.size()];
        AtomicInteger i = new AtomicInteger();

        libraries.forEach(l -> {
            JsonObject library = l.getAsJsonObject();
            if(library.has("downloads")) {
                Library entry = new Library() {
                    @Override
                    public String getPath() {
                        return StringUtils.cleanup_path(library.getAsJsonObject("downloads").getAsJsonObject("artifact").get("path").getAsString());
                    }

                    @Override
                    public String getURL() {
                        return library.getAsJsonObject("downloads").getAsJsonObject("artifact").get("url").getAsString();
                    }

                    @Override
                    public String getName() {
                        return library.get("name").getAsString();
                    }
                };
                result[i.get()] = entry;
            } else if(library.has("artifact")){
                Library entry = new Library() {
                    @Override
                    public String getPath() {
                        return StringUtils.cleanup_path(library.getAsJsonObject("artifact").get("path").getAsString());
                    }

                    @Override
                    public String getURL() {
                        return library.getAsJsonObject("artifact").get("url").getAsString();
                    }

                    @Override
                    public String getName() {
                        return library.get("name").getAsString();
                    }
                };
                result[i.get()] = entry;
            } else {
                String name = library.get("name").getAsString();
                String url = library.get("url").getAsString();
                String path = "";
                for(int j = 0; j < name.split(":").length; j++) {
                    if(j == 0) {
                        for(int m = 0; m < name.split(":")[0].split("\\.").length; m++) {
                            path += name.split(":")[0].split("\\.")[m] + "/";
                        }
                    } else {
                        path += name.split(":")[j] + "/";
                    }
                }
                path += name.split(":")[1] + "-" + name.split(":")[2] + ".jar";

                String finalPath = path;
                Library entry = new Library() {
                    @Override
                    public String getPath() {
                        return StringUtils.cleanup_path(finalPath);
                    }

                    @Override
                    public String getURL() {
                        return url + finalPath;
                    }

                    @Override
                    public String getName() {
                        return name;
                    }
                };
                result[i.get()] = entry;
            }
            i.getAndIncrement();
        });

        if(Var.inheritedJson != null) {

            System.out.println(Var.inheritedJson.getAbsolutePath());

            JsonObject inheritedJson = JsonParser.parseString(FileUtils.readFile(Var.inheritedJson)).getAsJsonObject();
            JsonArray inheritedLibraries = inheritedJson.get("libraries").getAsJsonArray();

            Library[] result2 = new Library[inheritedLibraries.size()];

            for(int j = 0; j < inheritedLibraries.size(); j++) {
                JsonObject library = inheritedLibraries.get(j).getAsJsonObject();
                Library entry = new Library() {
                    @Override
                    public String getPath() {
                        return StringUtils.cleanup_path(library.getAsJsonObject("downloads").getAsJsonObject("artifact").get("path").getAsString());
                    }

                    @Override
                    public String getURL() {
                        return library.getAsJsonObject("downloads").getAsJsonObject("artifact").get("url").getAsString();
                    }

                    @Override
                    public String getName() {
                        return library.get("name").getAsString();
                    }
                };
                result2[j] = entry;
            }

            return ArrayUtils.addAll(result, result2);

        }

        return result;

    }

    public static void downloadMissing() {

        Downloader downloader = new Downloader(true);

        for(int i = 0; i < Var.libraries.length; i++) {

            Library library = Var.libraries[i];

            File libJar = new File(Var.gameDir + "libraries" + File.separator + library.getPath());

            if(!libJar.exists()) {
                downloader.downloadFileToLocation(library.getURL(), Var.gameDir + "libraries" + File.separator + library.getPath());
            }

        }

    }

    public static String mergeLibs() {

        String result = "";

        for(int i = 0; i < Var.libraries.length; i++) {
            Library lib = Var.libraries[i];
            result += Var.gameDir + "libraries" + File.separator + lib.getPath() + ";";
        }

        return result;

    }

    public static String getAssetIndex() {

        JsonObject json = JsonParser.parseString(FileUtils.readFile(Var.configJson)).getAsJsonObject();

        return json.getAsJsonObject("assetIndex").get("id").getAsString();

    }

    public static String getMainClass() {

        JsonObject json = JsonParser.parseString(FileUtils.readFile(Var.configJson)).getAsJsonObject();

        if(json.has("mainClass")) {
            return json.get("mainClass").getAsString();
        } else if(Var.inheritedJson != null) {
            JsonObject inheritedJson = JsonParser.parseString(FileUtils.readFile(Var.inheritedJson)).getAsJsonObject();
            return inheritedJson.get("mainClass").getAsString();
        } else {
            return "net.minecraft.client.main.Main";
        }

    }

    public static String getVersionType() {

        JsonObject json = JsonParser.parseString(FileUtils.readFile(Var.configJson)).getAsJsonObject();

        if(json.has("type")) {
            return json.get("type").getAsString();
        } else if(Var.inheritedJson != null) {
            JsonObject inheritedJson = JsonParser.parseString(FileUtils.readFile(Var.inheritedJson)).getAsJsonObject();
            return inheritedJson.get("type").getAsString();
        } else {
            return "release";
        }

    }

    public static String getGameArgs() {

        JsonObject json = JsonParser.parseString(FileUtils.readFile(Var.configJson)).getAsJsonObject();
        JsonObject args = json.getAsJsonObject("arguments");
        JsonArray game = args.getAsJsonArray("game");

        String result = "";

        for (int i = 0; i < game.size(); i++) {
            if (game.get(i).isJsonPrimitive()) {
                result += game.get(i).getAsString() + " ";
            } else {
                JsonObject arg = game.get(i).getAsJsonObject();
                if (arg.has("rules")) {
                    JsonArray rules = arg.getAsJsonArray("rules");
                    boolean b = true;
                    for (int j = 0; j < rules.size(); j++) {
                        JsonObject rule = rules.get(j).getAsJsonObject();
                        if (b) b = checkRule(rule);
                    }
                    if (b) {
                        if (arg.has("value")) {
                            if (arg.get("value").isJsonArray()) {
                                JsonArray values = arg.getAsJsonArray("value");
                                for (int f = 0; f < values.size(); f++) {
                                    result += values.get(f).getAsString() + " ";
                                }
                            } else {
                                result += arg.get("value").getAsString() + " ";
                            }
                        } else if (arg.has("values")) {
                            JsonArray values = arg.getAsJsonArray("values");
                            for (int f = 0; f < values.size(); f++) {
                                result += values.get(f).getAsString() + " ";
                            }
                        }
                    }
                } else if (arg.has("values")) {
                    JsonArray values = arg.getAsJsonArray("values");
                    for (int f = 0; f < values.size(); f++) {
                        result += values.get(f).getAsString() + " ";
                    }
                } else if (arg.has("value")) {
                    if (arg.get("value").isJsonArray()) {
                        JsonArray values = arg.getAsJsonArray("value");
                        for (int f = 0; f < values.size(); f++) {
                            result += values.get(f).getAsString() + " ";
                        }
                    } else {
                        result += arg.getAsJsonObject("value").getAsString() + " ";
                    }
                } else {
                    result += arg.getAsString() + " ";
                }
            }
        }

        if (Var.inheritedJson != null) {

            JsonObject inheritedJson = JsonParser.parseString(FileUtils.readFile(Var.inheritedJson)).getAsJsonObject();
            JsonObject inheritedArgs = inheritedJson.getAsJsonObject("arguments");
            JsonArray inheritedGame = inheritedArgs.getAsJsonArray("game");

            for (int i = 0; i < inheritedGame.size(); i++) {
                if (game.get(i).isJsonPrimitive()) {
                    result += game.get(i).getAsString() + " ";
                } else {
                    JsonObject arg = inheritedGame.get(i).getAsJsonObject();
                    if (arg.has("rules")) {
                        JsonArray rules = arg.getAsJsonArray("rules");
                        boolean b = true;
                        for (int j = 0; j < rules.size(); j++) {
                            JsonObject rule = rules.get(j).getAsJsonObject();
                            if (b) b = checkRule(rule);
                        }
                        if (b) {
                            if (arg.has("value")) {
                                if (arg.get("value").isJsonArray()) {
                                    JsonArray values = arg.getAsJsonArray("value");
                                    for (int f = 0; f < values.size(); f++) {
                                        result += values.get(f).getAsString() + " ";
                                    }
                                } else {
                                    result += arg.get("value").getAsString() + " ";
                                }
                            } else if (arg.has("values")) {
                                JsonArray values = arg.getAsJsonArray("values");
                                for (int f = 0; f < values.size(); f++) {
                                    result += values.get(f).getAsString() + " ";
                                }
                            }
                        }
                    } else if (arg.has("values")) {
                        JsonArray values = arg.getAsJsonArray("values");
                        for (int f = 0; f < values.size(); f++) {
                            result += values.get(f).getAsString() + " ";
                        }
                    } else if (arg.has("value")) {
                        if (arg.get("value").isJsonArray()) {
                            JsonArray values = arg.getAsJsonArray("value");
                            for (int f = 0; f < values.size(); f++) {
                                result += values.get(f).getAsString() + " ";
                            }
                        } else {
                            result += arg.getAsJsonObject("value").getAsString() + " ";
                        }
                    } else {
                        result += arg.getAsString() + " ";
                    }
                }
            }

        }


        return fillArgs(result);

    }

    public static String getJVMArgs() {

        JsonObject json = JsonParser.parseString(FileUtils.readFile(Var.configJson)).getAsJsonObject();
        JsonObject args = json.getAsJsonObject("arguments");
        JsonArray jvm = args.getAsJsonArray("jvm");

        String result = "";

        for(int i = 0; i < jvm.size(); i++) {
            if(jvm.get(i).isJsonPrimitive()) {
                result += jvm.get(i).getAsString() + " ";
            } else {
                JsonObject arg = jvm.get(i).getAsJsonObject();
                if (arg.has("rules")) {
                    JsonArray rules = arg.getAsJsonArray("rules");
                    boolean b = true;
                    for (int j = 0; j < rules.size(); j++) {
                        JsonObject rule = rules.get(j).getAsJsonObject();
                        if (b) b = checkRule(rule);
                    }
                    if (b) {
                        if (arg.has("value")) {
                            if (arg.get("value").isJsonArray()) {
                                JsonArray values = arg.getAsJsonArray("value");
                                for (int f = 0; f < values.size(); f++) {
                                    result += values.get(f).getAsString() + " ";
                                }
                            } else {
                                result += arg.get("value").getAsString() + " ";
                            }
                        } else if (arg.has("values")) {
                            JsonArray values = arg.getAsJsonArray("values");
                            for (int f = 0; f < values.size(); f++) {
                                result += values.get(f).getAsString() + " ";
                            }
                        }
                    }
                } else if (arg.has("values")) {
                    JsonArray values = arg.getAsJsonArray("values");
                    for (int f = 0; f < values.size(); f++) {
                        result += values.get(f).getAsString() + " ";
                    }
                } else if (arg.has("value")) {
                    if (arg.get("value").isJsonArray()) {
                        JsonArray values = arg.getAsJsonArray("value");
                        for (int f = 0; f < values.size(); f++) {
                            result += values.get(f).getAsString() + " ";
                        }
                    } else {
                        result += arg.getAsJsonObject("value").getAsString() + " ";
                    }
                } else {
                    result += arg.getAsString() + " ";
                }
            }
        }

        if(Var.inheritedJson != null) {

            JsonObject inheritedJson = JsonParser.parseString(FileUtils.readFile(Var.inheritedJson)).getAsJsonObject();
            JsonObject inheritedArgs = inheritedJson.getAsJsonObject("arguments");
            JsonArray inheritedJvm = inheritedArgs.getAsJsonArray("jvm");

            for (int i = 0; i < inheritedJvm.size(); i++) {
                JsonObject arg = inheritedJvm.get(i).getAsJsonObject();
                if (arg.has("rules")) {
                    JsonArray rules = arg.getAsJsonArray("rules");
                    boolean b = true;
                    for (int j = 0; j < rules.size(); j++) {
                        JsonObject rule = rules.get(j).getAsJsonObject();
                        if (b) b = checkRule(rule);
                    }
                    if (b) {
                        if (arg.has("value")) {
                            if (arg.get("value").isJsonArray()) {
                                JsonArray values = arg.getAsJsonArray("value");
                                for (int f = 0; f < values.size(); f++) {
                                    result += values.get(f).getAsString() + " ";
                                }
                            } else {
                                result += arg.get("value").getAsString() + " ";
                            }
                        } else if (arg.has("values")) {
                            JsonArray values = arg.getAsJsonArray("values");
                            for (int f = 0; f < values.size(); f++) {
                                result += values.get(f).getAsString() + " ";
                            }
                        }
                    }
                } else if (arg.has("values")) {
                    JsonArray values = arg.getAsJsonArray("values");
                    for (int f = 0; f < values.size(); f++) {
                        result += values.get(f).getAsString() + " ";
                    }
                } else if (arg.has("value")) {
                    if (arg.get("value").isJsonArray()) {
                        JsonArray values = arg.getAsJsonArray("value");
                        for (int f = 0; f < values.size(); f++) {
                            result += values.get(f).getAsString() + " ";
                        }
                    } else {
                        result += arg.getAsJsonObject("value").getAsString() + " ";
                    }
                } else {
                    result += arg.getAsString() + " ";
                }
            }

        }

        return fillArgs(result);

    }

    private static boolean checkRule(JsonObject rule) {
        switch (rule.get("action").getAsString()) {
            case "allow" -> {
                if (rule.has("features")) {
                    JsonObject features = rule.getAsJsonObject("features");
                    if (features.has("is_demo_user")) return false;
                    if (features.has("has_custom_resolution")) return Var.x != 0;
                    if (features.has("has_quick_plays_support")) return false;
                    if (features.has("is_quick_play_singleplayer")) return false;
                    if (features.has("is_quick_play_multiplayer")) return false;
                    if (features.has("is_quick_play_realms")) return false;
                } else if (rule.has("os")) {
                    JsonObject os = rule.getAsJsonObject("os");
                    if (os.has("arch"))
                        return System.getProperty("os.name").toLowerCase().contains(os.get("arch").getAsString().toLowerCase());
                    if (os.has("name"))
                        return System.getProperty("os.name").toLowerCase().contains(os.get("name").getAsString().toLowerCase());
                }
            }
            case "deny" -> {
                if (rule.has("features")) {
                    JsonObject features = rule.getAsJsonObject("features");
                    if (features.has("is_demo_user")) return true;
                    if (features.has("has_custom_resolution")) return Var.x == 0;
                    if (features.has("has_quick_plays_support")) return true;
                    if (features.has("is_quick_play_singleplayer")) return true;
                    if (features.has("is_quick_play_multiplayer")) return true;
                    if (features.has("is_quick_play_realms")) return true;
                } else if (rule.has("os")) {
                    JsonObject os = rule.getAsJsonObject("os");
                    if (os.has("arch"))
                        return !System.getProperty("os.name").toLowerCase().contains(os.get("arch").getAsString().toLowerCase());
                    if (os.has("name"))
                        return !System.getProperty("os.name").toLowerCase().contains(os.get("name").getAsString().toLowerCase());
                }
            }
        }
        return false;
    }

    private static String fillArgs(String args) {

        args = args.replace("${auth_player_name}", Var.playerName);
        args = args.replace("${version_name}", Var.name);
        args = args.replace("${game_directory}", Var.gameDir);
        args = args.replace("${assets_root}", Var.gameDir + "assets" + File.separator);
        args = args.replace("${assets_index_name}", ExtractJsonContents.getAssetIndex());
        args = args.replace("${auth_uuid}", Var.uuid);
        args = args.replace("${auth_access_token}", Var.accessToken);
        args = args.replace("--xuid ${auth_xuid} ", "");
        args = args.replace("--userType ${user_type} ", "");
        args = args.replace("${version_type} ", ExtractJsonContents.getVersionType());
        args = args.replace("${resolution_width}", String.valueOf(Var.x));
        args = args.replace("${resolution_height}", String.valueOf(Var.y));
        args = args.replace("${natives_directory}", Var.gameDir + "versions" + File.separator + Var.name + File.separator + ".natives" + File.separator);
        args = args.replace("${launcher_name}", Var.launcherName);
        args = args.replace("${launcher_version}", "1");
        args = args.replace("${classpath}", ExtractJsonContents.mergeLibs() + Var.gameJar.getAbsolutePath() + " " + getMainClass());
        args = args.replace("--clientId ${clientid} ", "");

        return args;

    }

}
