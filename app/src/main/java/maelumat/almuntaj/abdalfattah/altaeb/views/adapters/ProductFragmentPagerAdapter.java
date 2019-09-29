package maelumat.almuntaj.abdalfattah.altaeb.views.adapters;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;

import java.util.ArrayList;
import java.util.List;

import maelumat.almuntaj.abdalfattah.altaeb.fragments.BaseFragment;
import maelumat.almuntaj.abdalfattah.altaeb.models.State;

public class ProductFragmentPagerAdapter extends FragmentPagerAdapter {

    private List<String> navMenuTitles;
    private List<BaseFragment> fragments;

    public ProductFragmentPagerAdapter(FragmentManager fm) {
        super(fm);
        this.fragments = new ArrayList<>();
        this.navMenuTitles = new ArrayList<>();
    }

    public void addFragment(BaseFragment fragment, String title) {
        this.fragments.add(fragment);
        this.navMenuTitles.add(title);
    }

    @Override
    public Fragment getItem(int i) {
        return fragments.get(i);
    }

    @Override
    public int getCount() {
        return fragments.size();
    }

    @Override
    public CharSequence getPageTitle(int position) {
        return navMenuTitles.get(position);
    }

    public void refresh(State state) {
        for (BaseFragment f : fragments) {
            if (f.isAdded()) {
                f.refreshView(state);
            }
        }
    }
}
