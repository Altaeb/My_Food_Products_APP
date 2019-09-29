package maelumat.almuntaj.abdalfattah.altaeb.utils;

import maelumat.almuntaj.abdalfattah.altaeb.BuildConfig;

import java.util.HashMap;

import static maelumat.almuntaj.abdalfattah.altaeb.utils.SearchType.*;

public class SearchTypeUrls {
   private static final HashMap<String, String> URLS = new HashMap<>();

    static {
        URLS.put(ALLERGEN, BuildConfig.OFWEBSITE + "allergens/");
        URLS.put(EMB, BuildConfig.OFWEBSITE + "packager-code/");
        URLS.put(TRACE, BuildConfig.OFWEBSITE + "trace/");
    }


    public static String  getUrl( @SearchType String type){
        return URLS.get(type);
    }
}
