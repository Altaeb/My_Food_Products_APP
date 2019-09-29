package maelumat.almuntaj.abdalfattah.altaeb.dagger.component;

import dagger.Subcomponent;
import maelumat.almuntaj.abdalfattah.altaeb.dagger.ActivityScope;
import maelumat.almuntaj.abdalfattah.altaeb.dagger.module.ActivityModule;
import maelumat.almuntaj.abdalfattah.altaeb.views.BaseActivity;

@Subcomponent(modules = {ActivityModule.class})
@ActivityScope
public interface ActivityComponent {

    FragmentComponent plusFragmentComponent();

    void inject(BaseActivity baseActivity);
}
