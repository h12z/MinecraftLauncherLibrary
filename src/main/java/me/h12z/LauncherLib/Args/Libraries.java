package me.h12z.LauncherLib.Args;

import me.marnic.jdl.DownloadHandler;
import me.marnic.jdl.Downloader;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.Reader;

public class Libraries {

    public static String[] getLibraryDownloads(File json) throws FileNotFoundException {

        String[] result = null;

        JSONParser jsonParser = new JSONParser();

        try(Reader reader = new FileReader(json)) {

            JSONObject jsonObject = (JSONObject) jsonParser.parse(reader);

            JSONArray libraries = (JSONArray) jsonObject.get("libraries");

            result = new String[libraries.size()];

            for(int i = 0; i < libraries.size(); i++) {

                JSONObject lib = (JSONObject) libraries.get(i);
                JSONObject downloads = (JSONObject) lib.get("downloads");
                JSONObject artifact = (JSONObject) downloads.get("artifact");

                result[i] = (String) artifact.get("url");

            }

        } catch (Exception e) {

            e.printStackTrace();

        }

        return result;

    }

    public static String[] getLibraryPaths(File json, String destinationPath) throws FileNotFoundException {

        String[] result = null;

        JSONParser jsonParser = new JSONParser();

        try(Reader reader = new FileReader(json)) {

            JSONObject jsonObject = (JSONObject) jsonParser.parse(reader);

            JSONArray libraries = (JSONArray) jsonObject.get("libraries");

            result = new String[libraries.size()];

            for(int i = 0; i < libraries.size(); i++) {

                JSONObject lib = (JSONObject) libraries.get(i);
                JSONObject downloads = (JSONObject) lib.get("downloads");
                JSONObject artifact = (JSONObject) downloads.get("artifact");

                result[i] = destinationPath + File.separator + artifact.get("path");

            }

        } catch (Exception e) {

            e.printStackTrace();

        }

        return result;

    }

    public static boolean downloaded;

    public static void downloadLibraries(String[] urls, String[] paths) {

        Downloader downloader = new Downloader(false);
        downloader.setDownloadHandler(new DownloadHandler(downloader) {
            @Override
            public void onDownloadStart() {
                downloaded = false;
            }

            @Override
            public void onDownloadFinish() {
                Libraries.downloaded = true;
            }

            @Override
            public void onDownloadError() {

            }
        });

        for(int i = 0; i < urls.length; i++) {

            File folder = new File(paths[i].replace(paths[i].split("/")[paths[i].split("/").length - 1], ""));
            folder.mkdirs();

            downloader.downloadFileToLocation(urls[i], paths[i]);

            while(!downloaded) {

            }

        }

    }

}
