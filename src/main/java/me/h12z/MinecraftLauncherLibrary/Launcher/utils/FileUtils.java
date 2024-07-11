package me.h12z.MinecraftLauncherLibrary.Launcher.utils;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.Scanner;

public class FileUtils {

    public static String readFile(File file) {

        StringBuilder result = new StringBuilder();
        Scanner scanner = null;
        try {
            scanner = new Scanner(file);
        } catch (FileNotFoundException e) {
            System.err.println("Sorry but the File " + file.getAbsoluteFile() + " doesn't exist");
            System.exit(0);
        }

        while(scanner.hasNextLine()) {
            result.append(scanner.nextLine());
        }

        return result.toString();

    }

}
