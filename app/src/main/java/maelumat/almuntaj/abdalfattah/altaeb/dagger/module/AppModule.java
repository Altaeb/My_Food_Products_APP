package maelumat.almuntaj.abdalfattah.altaeb.dagger.module;

import android.content.Context;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;
import io.reactivex.schedulers.Schedulers;
import okhttp3.OkHttpClient;
import maelumat.almuntaj.abdalfattah.altaeb.BuildConfig;
import maelumat.almuntaj.abdalfattah.altaeb.category.CategoryRepository;
import maelumat.almuntaj.abdalfattah.altaeb.category.mapper.CategoryMapper;
import maelumat.almuntaj.abdalfattah.altaeb.category.network.CategoryNetworkService;
import maelumat.almuntaj.abdalfattah.altaeb.dagger.Qualifiers;
import maelumat.almuntaj.abdalfattah.altaeb.network.OpenFoodAPIService;
import maelumat.almuntaj.abdalfattah.altaeb.utils.Utils;
import maelumat.almuntaj.abdalfattah.altaeb.views.OFFApplication;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory;
import retrofit2.converter.jackson.JacksonConverterFactory;

@Module
public class AppModule {
    private static final OkHttpClient httpClient = Utils.HttpClientBuilder();
    private OFFApplication application;

    public AppModule(OFFApplication application) {
        this.application = application;
    }

    @Provides
    @Singleton
    OFFApplication provideTrainLineApplication() {
        return application;
    }

    @Provides
    @Qualifiers.ForApplication
    @Singleton
    Context provideApplicationContext() {
        return application;
    }

    @Provides
    @Singleton
    Retrofit provideRetrofit() {
        return new Retrofit.Builder()
                .baseUrl(BuildConfig.OFWEBSITE)
                .client(httpClient)
                .addConverterFactory(JacksonConverterFactory.create())
                .addCallAdapterFactory(RxJava2CallAdapterFactory.createWithScheduler(Schedulers.io()))
                .build();
    }

    @Provides
    CategoryNetworkService provideCategoryNetworkService(Retrofit retrofit) {
        return retrofit.create(CategoryNetworkService.class);
    }

    @Provides
    @Singleton
    CategoryRepository provideCategoryRepository(CategoryNetworkService networkService, CategoryMapper mapper) {
        return new CategoryRepository(networkService, mapper);
    }

    @Provides
    @Singleton
    OpenFoodAPIService provideOpenFactsApiClient() {
        return new Retrofit.Builder()
                .baseUrl(BuildConfig.HOST)
                .client(httpClient)
                .addConverterFactory(JacksonConverterFactory.create())
                .addCallAdapterFactory(RxJava2CallAdapterFactory.createWithScheduler(Schedulers.io()))
                .build()
                .create(OpenFoodAPIService.class);
    }
}
