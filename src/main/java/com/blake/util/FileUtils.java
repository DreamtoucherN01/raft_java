package com.blake.util;

import java.io.File;
import java.io.IOException;

public class FileUtils {

    public static void makePathIfNotExist(String path) {

        File f = new File(path);
        if(!f.exists() && !f.isDirectory()) {
            f.mkdir();
        }
    }

    public static void createFile(String path, String fileName) throws IOException {

        File f = new File(path, fileName);
        if(!f.exists()) {
            f.createNewFile();
        }
    }


}
