package maelumat.almuntaj.abdalfattah.altaeb.network;

/**
 * Created by Lobster on 03.03.18.
 */

public interface ICommonApiManager {

    ProductApiService getProductApiService();

    OpenFoodAPIService getOpenFoodApiService();

    RobotoffAPIService getRobotoffApiService();

}
