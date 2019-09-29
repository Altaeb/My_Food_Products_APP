package maelumat.almuntaj.abdalfattah.altaeb.dagger.component;

import javax.inject.Singleton;

import dagger.Component;
import maelumat.almuntaj.abdalfattah.altaeb.dagger.module.ActivityModule;
import maelumat.almuntaj.abdalfattah.altaeb.dagger.module.AppModule;
import maelumat.almuntaj.abdalfattah.altaeb.views.OFFApplication;

@Component(modules = {AppModule.class})
@Singleton
public interface AppComponent {

    ActivityComponent plusActivityComponent(ActivityModule activityModule);

    void inject(OFFApplication application);

    void inject(maelumat.almuntaj.abdalfattah.altaeb.views.ContinuousScanActivity activity);

    void inject(maelumat.almuntaj.abdalfattah.altaeb.views.AddProductActivity activity);

    final class Initializer {

        private Initializer() {
            //empty
        }

        public static synchronized AppComponent init(AppModule appModule) {
            return DaggerAppComponent.builder()
                    .appModule(appModule)
                    .build();
        }
    }
}
