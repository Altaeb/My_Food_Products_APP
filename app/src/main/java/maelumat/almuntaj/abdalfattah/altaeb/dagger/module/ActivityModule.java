package maelumat.almuntaj.abdalfattah.altaeb.dagger.module;

import android.content.Context;
import androidx.appcompat.app.AppCompatActivity;

import dagger.Module;
import dagger.Provides;
import maelumat.almuntaj.abdalfattah.altaeb.dagger.ActivityScope;
import maelumat.almuntaj.abdalfattah.altaeb.dagger.Qualifiers;

@Module
public class ActivityModule {
    private AppCompatActivity activity;

    public ActivityModule(AppCompatActivity activity) {
        this.activity = activity;
    }

    @Provides
    @Qualifiers.ForActivity
    @ActivityScope
    Context provideActivityContext() {
        return activity;
    }

}
