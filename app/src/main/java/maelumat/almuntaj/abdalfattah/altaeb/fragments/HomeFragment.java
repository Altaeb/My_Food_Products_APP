package maelumat.almuntaj.abdalfattah.altaeb.fragments;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.preference.PreferenceManager;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.browser.customtabs.CustomTabsIntent;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.fragment.app.FragmentManager;

import android.provider.ContactsContract;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.afollestad.materialdialogs.MaterialDialog;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Locale;

import butterknife.BindView;
import butterknife.OnClick;
import io.reactivex.SingleObserver;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;
import maelumat.almuntaj.abdalfattah.altaeb.utils.SearchType;
import maelumat.almuntaj.abdalfattah.altaeb.utils.Utils;
import maelumat.almuntaj.abdalfattah.altaeb.views.BaseActivity;
import maelumat.almuntaj.abdalfattah.altaeb.views.ContinuousScanActivity;
import maelumat.almuntaj.abdalfattah.altaeb.views.HistoryScanActivity;
import maelumat.almuntaj.abdalfattah.altaeb.views.LoginActivity;
import maelumat.almuntaj.abdalfattah.altaeb.views.MainActivity;
import maelumat.almuntaj.abdalfattah.altaeb.views.ProductBrowsingListActivity;
import maelumat.almuntaj.abdalfattah.altaeb.views.category.activity.CategoryActivity;
import okhttp3.ResponseBody;
import maelumat.almuntaj.abdalfattah.altaeb.R;
import maelumat.almuntaj.abdalfattah.altaeb.models.Search;
import maelumat.almuntaj.abdalfattah.altaeb.models.TaglineLanguageModel;
import maelumat.almuntaj.abdalfattah.altaeb.network.OpenFoodAPIClient;
import maelumat.almuntaj.abdalfattah.altaeb.network.OpenFoodAPIService;
import maelumat.almuntaj.abdalfattah.altaeb.utils.LocaleHelper;
import maelumat.almuntaj.abdalfattah.altaeb.utils.NavigationDrawerListener.NavigationDrawerType;
import maelumat.almuntaj.abdalfattah.altaeb.views.OFFApplication;
import maelumat.almuntaj.abdalfattah.altaeb.views.customtabs.CustomTabActivityHelper;
import maelumat.almuntaj.abdalfattah.altaeb.views.customtabs.CustomTabsHelper;
import maelumat.almuntaj.abdalfattah.altaeb.views.customtabs.WebViewFallback;
import maelumat.almuntaj.abdalfattah.altaeb.views.listeners.BottomNavigationListenerInstaller;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import static maelumat.almuntaj.abdalfattah.altaeb.utils.NavigationDrawerListener.ITEM_HOME;

public class HomeFragment extends NavigationBaseFragment implements CustomTabActivityHelper.ConnectionCallback {
    @BindView(R.id.tvDailyFoodFact)
    TextView tvDailyFoodFact;
    @BindView(R.id.textHome)
    TextView textHome;
    @BindView(R.id.bottom_navigation)
    BottomNavigationView bottomNavigationView;
    private OpenFoodAPIService apiClient;
    private SharedPreferences sp;
    private String taglineURL;
    private Disposable disposable;
    private Uri userContributeUri;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return createView(inflater, container, R.layout.fragment_home);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        apiClient = new OpenFoodAPIClient(getActivity()).getAPIService();
        checkUserCredentials();
        sp = PreferenceManager.getDefaultSharedPreferences(getContext());
        BottomNavigationListenerInstaller.install(bottomNavigationView, getActivity(), getContext());
    }
    @OnClick(R.id.offlineEditLLn)
    protected void gotoOflineEditFragment() {
        FragmentActivity activity=getActivity();
        activity.getSupportFragmentManager()
            .beginTransaction()
            .replace(R.id.fragment_container, new OfflineEditFragment()).addToBackStack("HomeFragment").commit();
    }

    @OnClick(R.id.typeBarcodeLLn)
    protected void findBarcodeFragment() {
        FragmentActivity activity=getActivity();
        activity.getSupportFragmentManager()
            .beginTransaction()
            .replace(R.id.fragment_container, new FindProductFragment()).addToBackStack("HomeFragment").commit();
    }

    @OnClick(R.id.historyLLn)
    protected void HistoryScanActivity() {
        startActivity(new Intent(getContext(), HistoryScanActivity.class));
    }

    @OnClick(R.id.categoryLLn)
    protected void CategoryActivity() {
        startActivity(CategoryActivity.getIntent(getContext()));
    }
    @OnClick(R.id.contributionsLLn)
    protected void contributionsActivity() {

        /**
         * Search and display the products to be completed by moving to ProductBrowsingListActivity
         */
        ProductBrowsingListActivity.startActivity(getContext(), "", SearchType.INCOMPLETE_PRODUCT);
    }

        @OnClick(R.id.scan_searchLLn)
    protected void scanSearchActivity() {
       scan();
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
    }

    @OnClick(R.id.tvDailyFoodFact)
    protected void setDailyFoodFact() {
        // chrome custom tab init
        CustomTabsIntent customTabsIntent;
        CustomTabActivityHelper customTabActivityHelper = new CustomTabActivityHelper();
        customTabActivityHelper.setConnectionCallback(this);
        Uri dailyFoodFactUri = Uri.parse(taglineURL);
        customTabActivityHelper.mayLaunchUrl(dailyFoodFactUri, null, null);

        customTabsIntent = CustomTabsHelper.getCustomTabsIntent(getContext(),
            customTabActivityHelper.getSession());
        CustomTabActivityHelper.openCustomTab(getActivity(),
            customTabsIntent, dailyFoodFactUri, new WebViewFallback());
    }

    @Override
    @NavigationDrawerType
    public int getNavigationDrawerType() {
        return ITEM_HOME;
    }

    private void checkUserCredentials() {
        final SharedPreferences settings = OFFApplication.getInstance().getSharedPreferences("login", 0);
        String login = settings.getString("user", "");
        String password = settings.getString("pass", "");

        if (!login.isEmpty() && !password.isEmpty()) {
            apiClient.signIn(login, password, "Sign-in").enqueue(new Callback<ResponseBody>() {
                @Override
                public void onResponse(@NonNull Call<ResponseBody> call, @NonNull Response<ResponseBody> response) {
                    String htmlNoParsed = null;
                    try {
                        htmlNoParsed = response.body().string();
                    } catch (IOException e) {
                        Log.e(HomeFragment.class.getSimpleName(), "signin", e);
                    }
                    if (htmlNoParsed != null && (htmlNoParsed.contains("Incorrect user name or password.")
                        || htmlNoParsed.contains("See you soon!"))) {
                        settings.edit()
                            .putString("user", "")
                            .putString("pass", "")
                            .apply();

                        if(getActivity()!=null) {
                            new MaterialDialog.Builder(getActivity())
                                .title(R.string.alert_dialog_warning_title)
                                .content(R.string.alert_dialog_warning_msg_user)
                                .positiveText(R.string.txtOk)
                                .show();
                        }
                    }
                }

                @Override
                public void onFailure(@NonNull Call<ResponseBody> call, @NonNull Throwable t) {
                    Log.e(HomeFragment.class.getName(), "Unable to Sign-in");
                }
            });
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        //stop the call to off to get total product counts:
        if (disposable != null && !disposable.isDisposed()) {
            disposable.dispose();
        }
    }

    public void onResume() {

        super.onResume();

        int productCount = sp.getInt("productCount", 0);
        apiClient.getTotalProductCount()
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(new SingleObserver<Search>() {
                @Override
                public void onSubscribe(Disposable d) {
                    disposable=d;
                    if(isAdded()) {
                        updateTextHome(productCount);
                    }
                }

                @Override
                public void onSuccess(Search search) {
                    if(isAdded()) {
                        int totalProductCount = productCount;
                        try {
                            totalProductCount = Integer.parseInt(search.getCount());
                        } catch (NumberFormatException e) {
                            Log.w(HomeFragment.class.getSimpleName(), "can parse " + search.getCount() + " as int", e);
                        }
                        updateTextHome(totalProductCount);
                        SharedPreferences.Editor editor = sp.edit();
                        editor.putInt("productCount", totalProductCount);
                        editor.apply();
                    }
                }

                @Override
                public void onError(Throwable e) {
                    if(isAdded()) {
                        updateTextHome(productCount);
                    }
                }
            });

        getTagline();

        if (getActivity() instanceof AppCompatActivity) {
            ActionBar actionBar = ((AppCompatActivity) getActivity()).getSupportActionBar();
            if (actionBar != null) {
                actionBar.setTitle("");
            }
        }
    }

    private void updateTextHome(int totalProductCount) {
        try {
            textHome.setText(R.string.txtHome);
            if (totalProductCount != 0) {
                String txtHomeOnline = getResources().getString(R.string.txtHomeOnline);

                textHome.setText(String.format(txtHomeOnline, totalProductCount));
            }
        } catch (Exception e) {
            Log.w(HomeFragment.class.getSimpleName(), "can format text for home", e);
        }
    }

    @Override
    public void onCustomTabsConnected() {

    }

    @Override
    public void onCustomTabsDisconnected() {

    }

    private void getTagline() {
        OpenFoodAPIService openFoodAPIService = new OpenFoodAPIClient(getActivity(), "https://ssl-api.openfoodfacts.org").getAPIService();
        Call<ArrayList<TaglineLanguageModel>> call = openFoodAPIService.getTagline();
        call.enqueue(new Callback<ArrayList<TaglineLanguageModel>>() {
            @Override
            public void onResponse(Call<ArrayList<TaglineLanguageModel>> call, Response<ArrayList<TaglineLanguageModel>> response) {
                if (response.isSuccessful()) {
                    final Locale locale = LocaleHelper.getLocale(getContext());
                    String localAsString = locale.toString();
                    boolean isLanguageFound = false;
                    boolean isExactLanguageFound = false;
                    for (int i = 0; i < response.body().size(); i++) {
                        final String languageCountry = response.body().get(i).getLanguage();
                        if (!isExactLanguageFound && (languageCountry.equals(localAsString) || languageCountry.contains(localAsString))) {
                            isExactLanguageFound = languageCountry.equals(localAsString);
                            taglineURL = response.body().get(i).getTaglineModel().getUrl();
                            tvDailyFoodFact.setText(response.body().get(i).getTaglineModel().getMessage());
                            tvDailyFoodFact.setVisibility(View.VISIBLE);
                            isLanguageFound = true;
                        }
                    }
                    if (!isLanguageFound) {
                        taglineURL = response.body().get(response.body().size() - 1).getTaglineModel().getUrl();
                        tvDailyFoodFact.setText(response.body().get(response.body().size() - 1).getTaglineModel().getMessage());
                        tvDailyFoodFact.setVisibility(View.VISIBLE);
                    }
                }
            }

            @Override
            public void onFailure(Call<ArrayList<TaglineLanguageModel>> call, Throwable t) {
            }
        });
    }
    private void scan() {
        if (ContextCompat.checkSelfPermission(getContext(), Manifest.permission.CAMERA) !=
            PackageManager.PERMISSION_GRANTED) {
            if (ActivityCompat.shouldShowRequestPermissionRationale(getActivity(), Manifest
                .permission.CAMERA)) {
                new MaterialDialog.Builder(getContext())
                    .title(R.string.action_about)
                    .content(R.string.permission_camera)
                    .neutralText(R.string.txtOk)
                    .show().setOnDismissListener(dialogInterface -> ActivityCompat.requestPermissions(getActivity(),
                    new String[]{Manifest.permission.CAMERA},
                    Utils.MY_PERMISSIONS_REQUEST_CAMERA));
            } else {
                ActivityCompat.requestPermissions(getActivity(), new String[]{Manifest
                    .permission.CAMERA}, Utils.MY_PERMISSIONS_REQUEST_CAMERA);
            }
        } else {
            Intent intent = new Intent(getContext(), ContinuousScanActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(intent);
        }
    }


}
