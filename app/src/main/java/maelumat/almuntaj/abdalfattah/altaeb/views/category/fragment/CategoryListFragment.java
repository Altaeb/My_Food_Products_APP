package maelumat.almuntaj.abdalfattah.altaeb.views.category.fragment;


import android.app.SearchManager;
import android.content.Context;
import androidx.databinding.DataBindingUtil;
import android.os.Bundle;
import android.provider.SearchRecentSuggestions;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.appcompat.widget.SearchView;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import javax.inject.Inject;

import io.reactivex.disposables.CompositeDisposable;
import maelumat.almuntaj.abdalfattah.altaeb.FastScroller;
import maelumat.almuntaj.abdalfattah.altaeb.R;
import maelumat.almuntaj.abdalfattah.altaeb.dagger.component.FragmentComponent;
import maelumat.almuntaj.abdalfattah.altaeb.databinding.FragmentCategoryListBinding;
import maelumat.almuntaj.abdalfattah.altaeb.fragments.MvvmFragment;
import maelumat.almuntaj.abdalfattah.altaeb.utils.SearchSuggestionProvider;
import maelumat.almuntaj.abdalfattah.altaeb.views.BaseActivity;
import maelumat.almuntaj.abdalfattah.altaeb.views.viewmodel.category.CategoryFragmentViewModel;

public class CategoryListFragment extends MvvmFragment<CategoryFragmentViewModel, FragmentComponent> {

    FastScroller fastScroller;

    @Inject
    CategoryFragmentViewModel viewModel;

    private FragmentCategoryListBinding binding;
    private MenuItem searchMenuItem;

    public CategoryListFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_category_list, container, false);
        fastScroller = rootView.findViewById(R.id.fast_scroller);
        return rootView;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        binding = DataBindingUtil.bind(this.getView());
        binding.recycler.setHasFixedSize(true);
        binding.recycler.setLayoutManager(new LinearLayoutManager(getContext()));
        binding.recycler.addItemDecoration(new DividerItemDecoration(getContext(), DividerItemDecoration.VERTICAL));
        binding.setViewModel(getViewModel());
        fastScroller.setRecyclerView(binding.recycler);
        binding.recycler.getViewTreeObserver().addOnGlobalLayoutListener(() -> {
            if (binding.getViewModel().getFilteredCategories().get().isEmpty()) {
                fastScroller.setVisibility(View.GONE);
            } else {
                fastScroller.setVisibility(View.VISIBLE);
                // check for an empty item in the start of the list
                if (viewModel.getFilteredCategories().get().get(0).getName().isEmpty()) {
                    viewModel.getFilteredCategories().get().remove(0);
                    binding.recycler.getAdapter().notifyItemRemoved(0);
                    binding.recycler.getAdapter().notifyItemRangeChanged(0, binding.recycler.getAdapter().getItemCount());
                }
            }
        });
        binding.offlineView.findViewById(R.id.buttonToRefresh).setOnClickListener(v -> viewModel.loadCategories());
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.menu_main, menu);

        SearchManager searchManager = (SearchManager) getActivity().getSystemService(Context.SEARCH_SERVICE);
        searchMenuItem = menu.findItem(R.id.action_search);
        SearchView searchView = (SearchView) searchMenuItem.getActionView();
        searchView.setQueryHint("Search for a food category");
        if (searchManager.getSearchableInfo(getActivity().getComponentName()) != null) {
            searchView.setSearchableInfo(searchManager.getSearchableInfo(getActivity().getComponentName()));
            searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
                @Override
                public boolean onQueryTextSubmit(String query) {
                    SearchRecentSuggestions suggestions = new SearchRecentSuggestions(getContext(),
                            SearchSuggestionProvider.AUTHORITY, SearchSuggestionProvider.MODE);
                    suggestions.saveRecentQuery(query, null);
                    return false;
                }

                @Override
                public boolean onQueryTextChange(String newText) {
                    viewModel.searchCategories(newText.toLowerCase());
                    return false;
                }
            });
        }

        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    protected CategoryFragmentViewModel getViewModel() {
        return viewModel;
    }

    @NonNull
    @Override
    protected FragmentComponent createComponent() {
        return ((BaseActivity) getActivity()).getActivityComponent().plusFragmentComponent();
    }

    @Override
    protected void inject() {
        component().inject(this);
    }

    @Override
    protected void bindProperties(CompositeDisposable compositeDisposable) {
        //Not used here
    }
}
