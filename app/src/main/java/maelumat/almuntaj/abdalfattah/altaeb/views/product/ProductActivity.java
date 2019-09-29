package maelumat.almuntaj.abdalfattah.altaeb.views.product;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.hardware.Sensor;
import android.hardware.SensorManager;
import android.os.Build;
import android.os.Bundle;
import android.preference.PreferenceManager;
import androidx.annotation.RequiresApi;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.tabs.TabLayout;
import androidx.viewpager.widget.ViewPager;
import androidx.appcompat.widget.Toolbar;
import android.view.MenuItem;
import butterknife.BindView;
import maelumat.almuntaj.abdalfattah.altaeb.BuildConfig;
import maelumat.almuntaj.abdalfattah.altaeb.R;
import maelumat.almuntaj.abdalfattah.altaeb.fragments.ContributorsFragment;
import maelumat.almuntaj.abdalfattah.altaeb.fragments.ProductPhotosFragment;
import maelumat.almuntaj.abdalfattah.altaeb.models.Nutriments;
import maelumat.almuntaj.abdalfattah.altaeb.models.State;
import maelumat.almuntaj.abdalfattah.altaeb.network.OpenFoodAPIClient;
import maelumat.almuntaj.abdalfattah.altaeb.utils.ShakeDetector;
import maelumat.almuntaj.abdalfattah.altaeb.utils.Utils;
import maelumat.almuntaj.abdalfattah.altaeb.views.AddProductActivity;
import maelumat.almuntaj.abdalfattah.altaeb.views.BaseActivity;
import maelumat.almuntaj.abdalfattah.altaeb.views.MainActivity;
import maelumat.almuntaj.abdalfattah.altaeb.views.adapters.ProductFragmentPagerAdapter;
import maelumat.almuntaj.abdalfattah.altaeb.views.listeners.BottomNavigationListenerInstaller;
import maelumat.almuntaj.abdalfattah.altaeb.views.listeners.OnRefreshListener;
import maelumat.almuntaj.abdalfattah.altaeb.views.product.environment.EnvironmentProductFragment;
import maelumat.almuntaj.abdalfattah.altaeb.views.product.ingredients.IngredientsProductFragment;
import maelumat.almuntaj.abdalfattah.altaeb.views.product.ingredients_analysis.IngredientsAnalysisProductFragment;
import maelumat.almuntaj.abdalfattah.altaeb.views.product.nutrition.NutritionProductFragment;
import maelumat.almuntaj.abdalfattah.altaeb.views.product.summary.SummaryProductFragment;

public class ProductActivity extends BaseActivity implements OnRefreshListener {

	private static final int LOGIN_ACTIVITY_REQUEST_CODE = 1;
	@BindView( R.id.pager )
	ViewPager viewPager;
	@BindView( R.id.toolbar )
	Toolbar toolbar;
	@BindView( R.id.tabs )
	TabLayout tabLayout;
    @BindView( R.id.bottom_navigation )
	BottomNavigationView bottomNavigationView;

    private ProductFragmentPagerAdapter adapterResult;

    private OpenFoodAPIClient api;
    private State mState;
    private SensorManager mSensorManager;
    private Sensor mAccelerometer;
    private ShakeDetector mShakeDetector;
    // boolean to determine if scan on shake feature should be enabled
    private boolean scanOnShake;

    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getResources().getBoolean(R.bool.portrait_only)) {
            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        }
        setContentView(R.layout.activity_product);
        setTitle(getString(R.string.app_name_long));

        setSupportActionBar(toolbar);
        if(getSupportActionBar()!=null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

		api = new OpenFoodAPIClient( this );

		mState = (State) getIntent().getSerializableExtra("state" );
        //no state-> we can't display anything. we go back to home.
        if(mState==null){
            final Intent intent = new Intent(getApplicationContext(), MainActivity.class);
            startActivity(intent);
        }

		setupViewPager( viewPager );

		tabLayout.setupWithViewPager( viewPager );

        // Get the user preference for scan on shake feature and open ContinuousScanActivity if the user has enabled the feature
        mSensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
        if (mSensorManager != null) {
            mAccelerometer = mSensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        }
        mShakeDetector = new ShakeDetector();

        SharedPreferences shakePreference = PreferenceManager.getDefaultSharedPreferences(this);
        scanOnShake = shakePreference.getBoolean("shakeScanMode", false);

        mShakeDetector.setOnShakeListener(count -> {

            if (scanOnShake) {
                Utils.scan(ProductActivity.this);
            }
        });
        BottomNavigationListenerInstaller.install(bottomNavigationView,this,this);
	}

	@Override
	protected void onActivityResult( int requestCode, int resultCode, Intent data )
	{
		super.onActivityResult( requestCode, resultCode, data );
		if( requestCode == LOGIN_ACTIVITY_REQUEST_CODE && resultCode == RESULT_OK )
		{
			Intent intent = new Intent( ProductActivity.this, AddProductActivity.class );
			intent.putExtra( AddProductActivity.KEY_EDIT_PRODUCT, mState.getProduct() );
			startActivity( intent );
		}

	}


	private void setupViewPager( ViewPager viewPager )
	{
		adapterResult = setupViewPager(viewPager, new ProductFragmentPagerAdapter(getSupportFragmentManager()), mState, this);
    }

    /**
     * CAREFUL ! YOU MUST INSTANTIATE YOUR OWN ADAPTERRESULT BEFORE CALLING THIS METHOD
     * @param viewPager
     * @param adapterResult
     * @param mState
     * @param activity
     * @return
     */
    public static ProductFragmentPagerAdapter setupViewPager (ViewPager viewPager, ProductFragmentPagerAdapter adapterResult, State mState, Activity activity) {
        String[] menuTitles = activity.getResources().getStringArray( R.array.nav_drawer_items_product );
        String[] newMenuTitles=activity.getResources().getStringArray(R.array.nav_drawer_new_items_product);

        adapterResult.addFragment( new SummaryProductFragment(), menuTitles[0] );
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences( activity );
        if( BuildConfig.FLAVOR.equals( "off" ) || BuildConfig.FLAVOR.equals( "obf" ) || BuildConfig.FLAVOR.equals( "opff" ) )
        {
            adapterResult.addFragment( new IngredientsProductFragment(), menuTitles[1] );
        }
        if( BuildConfig.FLAVOR.equals( "off" ) )
        {
            adapterResult.addFragment( new NutritionProductFragment(), menuTitles[2] );
            if( (mState.getProduct().getNutriments() != null &&
                    mState.getProduct().getNutriments().contains(Nutriments.CARBON_FOOTPRINT)) ||
                    (mState.getProduct().getEnvironmentInfocard() != null && !mState.getProduct().getEnvironmentInfocard().isEmpty()))
            {
                adapterResult.addFragment(new EnvironmentProductFragment(), "Environment");
            }
            if(isPhotoMode(activity))
            {
                adapterResult.addFragment( new ProductPhotosFragment(), newMenuTitles[0] );
            }
        }
        if( BuildConfig.FLAVOR.equals( "opff" ) )
        {
            adapterResult.addFragment( new NutritionProductFragment(), menuTitles[2] );
            if(isPhotoMode(activity))
            {
                adapterResult.addFragment( new ProductPhotosFragment(), newMenuTitles[0] );
            }
        }

        if( BuildConfig.FLAVOR.equals( "obf" ) )
        {
            if(isPhotoMode(activity))
            {
                adapterResult.addFragment( new ProductPhotosFragment(), newMenuTitles[0] );
            }
            adapterResult.addFragment( new IngredientsAnalysisProductFragment(), newMenuTitles[1] );
        }

        if( BuildConfig.FLAVOR.equals( "opf" ) )
        {
            adapterResult.addFragment( new ProductPhotosFragment(), newMenuTitles[0] );
        }
        if( preferences.getBoolean( "contributionTab", false ) )
        {
            adapterResult.addFragment( new ContributorsFragment(), activity.getString( R.string.contribution_tab ) );
        }

        viewPager.setAdapter(adapterResult);
        return adapterResult;
    }

    private static boolean isPhotoMode(Activity activity) {
        return PreferenceManager.getDefaultSharedPreferences( activity ).getBoolean( "photoMode", false );
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        return onOptionsItemSelected(item, this);
    }

    public static boolean onOptionsItemSelected(MenuItem item, Activity activity) {
        // Respond to the action bar's Up/Home button
        if (item.getItemId() == android.R.id.home) {
            activity.finish();
        }
        return true;
    }


    @Override
    public void onRefresh() {
        api.getProduct(mState.getProduct().getCode(), this);
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        setIntent(intent);
        mState = (State) intent.getSerializableExtra("state");
        adapterResult.refresh(mState);
    }

    @Override
    public void onPause() {
        super.onPause();
        if (scanOnShake) {
            //unregister the listener
            mSensorManager.unregisterListener(mShakeDetector, mAccelerometer);
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        if (scanOnShake) {
            //register the listener
            mSensorManager.registerListener(mShakeDetector, mAccelerometer, SensorManager.SENSOR_DELAY_UI);
        }
    }
}
