package maelumat.almuntaj.abdalfattah.altaeb.dagger.module;

import dagger.Module;
import dagger.Provides;
import maelumat.almuntaj.abdalfattah.altaeb.dagger.FragmentScope;
import maelumat.almuntaj.abdalfattah.altaeb.views.viewmodel.category.CategoryFragmentViewModel;

@Module
public class FragmentModule {
    @FragmentScope
    @Provides
    CategoryFragmentViewModel provideCategoryFragmentViewModel() {
        return new CategoryFragmentViewModel();
    }
}
