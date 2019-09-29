package maelumat.almuntaj.abdalfattah.altaeb.utils;

import maelumat.almuntaj.abdalfattah.altaeb.BuildConfig;

public class FileUtils {
    public static final String LOCALE_FILE_SCHEME = "file://";

    public static boolean isLocaleFile(String url) {
        return url != null && url.startsWith(LOCALE_FILE_SCHEME);
    }
    public static boolean isAbsolute(String url) {
        return url != null && url.startsWith("/");
    }

    public static String getCsvFolderName() {
           String folderMain;
           if ((BuildConfig.FLAVOR.equals("off"))) {
               folderMain = "Application of food products";
           } else if ((BuildConfig.FLAVOR.equals("opff"))) {
               folderMain = "Applications of pet food";
           } else if ((BuildConfig.FLAVOR.equals("opf"))) {
               folderMain = "Application of other products";
           } else {
               folderMain = "Application of cosmetic products";
           }
           return folderMain;
       }
}
