package me.h12z.LauncherLib.Launcher.utils;

import java.io.File;

public class StringUtils {

    public static String cleanup_path(String path) {

        path = path.replace("\\", File.separator);
        path = path.replace("/", File.separator);
        if(!path.endsWith("/") || !path.endsWith("\\") || !path.endsWith(File.separator)) path += File.separator;
        return path;

    }

}
