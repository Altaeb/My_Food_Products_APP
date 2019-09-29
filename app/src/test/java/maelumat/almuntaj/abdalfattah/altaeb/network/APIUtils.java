package maelumat.almuntaj.abdalfattah.altaeb.network;

import okhttp3.OkHttpClient;
import maelumat.almuntaj.abdalfattah.altaeb.utils.Utils;

public interface APIUtils {

    String GET_API = "https://world.openfoodfacts.org";


    default OkHttpClient HttpClientBuilder() {
        return Utils.HttpClientBuilder();
    }
}

