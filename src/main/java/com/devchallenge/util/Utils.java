package com.devchallenge.util;

import java.io.File;
import java.util.Arrays;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;


public class Utils {
    /**
     * Returns last digits in string if exist, else -1
     */
    public static Integer getLastDigits(String str){
        Pattern p = Pattern.compile("[0-9]+$");
        Matcher m = p.matcher(str);
        if(m.find()) {
            return Integer.parseInt(m.group());
        }
        return -1;
    }
    /**
     * Returns list of files  absolute path inside folder
     */
    public static List<String> listFilesForFolder(String folderPath) {
        File folder = new File(folderPath);
        return Arrays.stream(folder.listFiles())
                .filter(File::isFile)
                .map(File::getAbsolutePath)
                .collect(Collectors.toList());
    }


}
