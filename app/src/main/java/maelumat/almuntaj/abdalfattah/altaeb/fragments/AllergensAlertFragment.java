package maelumat.almuntaj.abdalfattah.altaeb.fragments;

import android.content.Context;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import com.afollestad.materialdialogs.MaterialDialog;

import net.steamcrafted.loadtoast.LoadToast;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import butterknife.OnClick;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;
import maelumat.almuntaj.abdalfattah.altaeb.R;
import maelumat.almuntaj.abdalfattah.altaeb.models.AllergenName;
import maelumat.almuntaj.abdalfattah.altaeb.repositories.IProductRepository;
import maelumat.almuntaj.abdalfattah.altaeb.repositories.ProductRepository;
import maelumat.almuntaj.abdalfattah.altaeb.utils.LocaleHelper;
import maelumat.almuntaj.abdalfattah.altaeb.utils.NavigationDrawerListener.NavigationDrawerType;
import maelumat.almuntaj.abdalfattah.altaeb.views.adapters.AllergensAdapter;
import maelumat.almuntaj.abdalfattah.altaeb.views.listeners.BottomNavigationListenerInstaller;

import static maelumat.almuntaj.abdalfattah.altaeb.utils.NavigationDrawerListener.ITEM_ALERT;

/**
 * @see R.layout#fragment_alert_allergens
 */
public class AllergensAlertFragment extends NavigationBaseFragment {

    private List<AllergenName> mAllergensEnabled;
    private List<AllergenName> mAllergensFromDao;
    private AllergensAdapter mAdapter;
    private RecyclerView mRvAllergens;
    private SharedPreferences mSettings;
    private IProductRepository productRepository;
    private View currentView;
    private LinearLayout mEmptyMessageView;                                         // Empty View containing the message that will be shown if the list is empty
    private DataObserver mDataObserver;
    private BottomNavigationView bottomNavigationView;
    public static Integer getKey(Map<Integer, String> map, String value) {
        Integer key = null;
        for (Map.Entry<Integer, String> entry : map.entrySet()) {
            if ((value == null && entry.getValue() == null) || (value != null && value.equals(entry.getValue()))) {
                key = entry.getKey();
                break;
            }
        }
        return key;
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        setHasOptionsMenu(true);
        return createView(inflater, container, R.layout.fragment_alert_allergens);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        MenuItem item = menu.findItem(R.id.action_search);
        item.setVisible(false);
    }

    @Override
    public void onViewCreated(@NonNull final View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        mRvAllergens = view.findViewById(R.id.allergens_recycle);
        mEmptyMessageView = view.findViewById(R.id.emptyAllergensView);
        productRepository = ProductRepository.getInstance();
        mDataObserver = new DataObserver();
        bottomNavigationView  = view.findViewById((R.id.bottom_navigation));
        BottomNavigationListenerInstaller.install(bottomNavigationView,getActivity(),getContext());
        productRepository.getAllergensByEnabledAndLanguageCode(true, Locale.getDefault().getLanguage());

        final String language = LocaleHelper.getLanguage(getContext());
        productRepository.getAllergensByEnabledAndLanguageCode(true, language)

                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(allergens -> {
                    mAllergensEnabled = allergens;
                    mAdapter = new AllergensAdapter(productRepository, mAllergensEnabled);
                    mRvAllergens.setAdapter(mAdapter);
                    mRvAllergens.setLayoutManager(new LinearLayoutManager(view.getContext()));
                    mRvAllergens.setHasFixedSize(true);
                    mAdapter.registerAdapterDataObserver(mDataObserver);
                    mDataObserver.onChanged();
                }, e->Log.e(AllergensAlertFragment.class.getSimpleName(),"getAllergensByEnabledAndLanguageCode",e));

        productRepository.getAllergensByLanguageCode(language)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(allergens -> {
                    mAllergensFromDao = allergens;
                }, e->Log.e(AllergensAlertFragment.class.getSimpleName(),"getAllergensByLanguageCode",e));


        currentView = view;
        mSettings = getActivity().getSharedPreferences("prefs", 0);
    }

    /**
     * Add an allergen to be checked for when browsing products.
     */
    @OnClick(R.id.btn_add)
    protected void onAddAllergens() {
        if (mAllergensEnabled != null && mAllergensFromDao != null && mAllergensFromDao.size() > 0) {
            productRepository.getAllergensByEnabledAndLanguageCode(false, LocaleHelper.getLanguage(getContext()))
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe((List<AllergenName> allergens) -> {
                        Collections.sort(allergens, (a1, a2) -> a1.getName().compareToIgnoreCase(a2.getName()));
                        List<String> allergensNames = new ArrayList<>();
                        for (AllergenName allergenName : allergens) {
                            allergensNames.add(allergenName.getName());
                        }
                        new MaterialDialog.Builder(currentView.getContext())
                                .title(R.string.title_dialog_alert)
                                .items(allergensNames)
                                .itemsCallback((dialog, view, position, text) -> {
                                    productRepository.setAllergenEnabled(allergens.get(position).getAllergenTag(), true);
                                    mAllergensEnabled.add(allergens.get(position));
                                    mAdapter.notifyItemInserted(mAllergensEnabled.size() - 1);
                                    mRvAllergens.scrollToPosition(mAdapter.getItemCount() - 1);
                                })
                                .show();
                    }, Throwable::printStackTrace);
        } else {
            ConnectivityManager cm = (ConnectivityManager) getActivity().getSystemService(Context.CONNECTIVITY_SERVICE);
            NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
            boolean isConnected = activeNetwork != null && activeNetwork.isConnectedOrConnecting();
            if (isConnected) {
                final LoadToast lt = new LoadToast(getContext());
                lt.setText(getContext().getString(R.string.toast_retrieving));
                lt.setBackgroundColor(getContext().getResources().getColor(R.color.blue));
                lt.setTextColor(getContext().getResources().getColor(R.color.white));
                lt.show();
                final SharedPreferences.Editor editor = mSettings.edit();
                productRepository.getAllergens(true)
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .toObservable()
                        .subscribe(allergens -> {
                            editor.putBoolean("errorAllergens", false).apply();
                            productRepository.saveAllergens(allergens);
                            mAdapter.setAllergens(mAllergensEnabled);
                            mAdapter.notifyDataSetChanged();
                            updateAllergenDao();
                            onAddAllergens();
                            lt.success();
                        }, e -> {
                            editor.putBoolean("errorAllergens", true).apply();
                            lt.error();
                        });
            } else {
                new MaterialDialog.Builder(currentView.getContext())
                        .title(R.string.title_dialog_alert)
                        .content(R.string.info_download_data_connection)
                        .neutralText(R.string.txtOk)
                        .show();
            }
        }
    }

    private void updateAllergenDao() {
        final String language = LocaleHelper.getLanguage(getContext());
        productRepository.getAllergensByEnabledAndLanguageCode(true, language)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(allergens -> {
                    mAllergensEnabled = allergens;
                }, e->Log.e(AllergensAlertFragment.class.getSimpleName(),"getAllergensByEnabledAndLanguageCode",e));

        productRepository.getAllergensByLanguageCode(language)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(allergens -> {
                    mAllergensFromDao = allergens;
                },  e->Log.e(AllergensAlertFragment.class.getSimpleName(),"getAllergensByLanguageCode",e));
    }

    @Override
    @NavigationDrawerType
    public int getNavigationDrawerType() {
        return ITEM_ALERT;
    }

    @Override
    public void onResume() {
        super.onResume();
        try {
            ((AppCompatActivity) getActivity()).getSupportActionBar().setTitle(getString(R.string.alert_drawer));
        } catch (NullPointerException e) {
            Log.e(AllergensAlertFragment.class.getSimpleName(),"onResume",e);
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        if(mRvAllergens != null){
            mRvAllergens.getAdapter().unregisterAdapterDataObserver(mDataObserver);
        }
    }

    class DataObserver extends RecyclerView.AdapterDataObserver{
        DataObserver() {
            super();
        }

        private void setAppropriateView() {
            if (mEmptyMessageView != null && mAdapter != null) {
                boolean isListEmpty = mAdapter.getItemCount() == 0;
                mEmptyMessageView.setVisibility(isListEmpty ? View.VISIBLE : View.GONE);
                mRvAllergens.setVisibility(isListEmpty ? View.GONE : View.VISIBLE);
            }
        }
        @Override
        public void onChanged() {
            super.onChanged();
            setAppropriateView();
        }

        @Override
        public void onItemRangeInserted(int positionStart, int itemCount) {
            super.onItemRangeInserted(positionStart, itemCount);
            setAppropriateView();
        }

        @Override
        public void onItemRangeRemoved(int positionStart, int itemCount) {
            super.onItemRangeRemoved(positionStart, itemCount);
            setAppropriateView();
        }
    }
}
