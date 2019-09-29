package maelumat.almuntaj.abdalfattah.altaeb.dagger.component;

import dagger.Subcomponent;
import maelumat.almuntaj.abdalfattah.altaeb.dagger.FragmentScope;
import maelumat.almuntaj.abdalfattah.altaeb.dagger.module.FragmentModule;
import maelumat.almuntaj.abdalfattah.altaeb.views.category.fragment.CategoryListFragment;

@Subcomponent(modules = {FragmentModule.class})
@FragmentScope
public interface FragmentComponent {
    void inject(CategoryListFragment categoryListFragment);
}