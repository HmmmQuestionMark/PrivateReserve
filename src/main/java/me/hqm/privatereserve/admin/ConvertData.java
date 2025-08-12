package me.hqm.privatereserve.admin;

import me.hqm.document.DocumentDatabase;
import me.hqm.document.SupportedFormat;
import me.hqm.privatereserve.Settings;

import java.io.File;

public class ConvertData {
    public static DocumentDatabase convert(DocumentDatabase old) {
        return null;
    }

    public static boolean databaseExists(SupportedFormat format) {
        File datafolder = new File(Settings.FILE_FOLDER.getString());
        for (File dbFolder : datafolder.listFiles(File::isDirectory)) {
            for (String file : dbFolder.list()) {
                if (file.toLowerCase().endsWith("." + format.getExt())) {
                    return true;
                }
            }
        }

        return false;
    }

    public static void backup(SupportedFormat format) {
        File datafolder = new File(Settings.FILE_FOLDER.getString());
        for (File dbFolder : datafolder.listFiles(File::isDirectory)) {
            for (String file : dbFolder.list()) {
                if (file.toLowerCase().endsWith("." + format.getExt())) {

                }
            }
        }

        /*if (list != null) {
            for (File fil : list) {
                if (fil.isDirectory()) {
                    findFile(name, fil);
                } else if (name.equalsIgnoreCase(fil.getName())) {
                    System.out.println(fil.getParentFile());
                }
            }
        }*/


    }
}
