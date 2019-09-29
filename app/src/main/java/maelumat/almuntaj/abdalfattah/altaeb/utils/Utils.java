package maelumat.almuntaj.abdalfattah.altaeb.utils;

import android.Manifest;
import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Typeface;
import android.graphics.drawable.Drawable;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.BatteryManager;
import android.os.Build;
import android.os.Environment;
import android.preference.PreferenceManager;
import android.provider.Settings;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.SpannableStringBuilder;
import android.text.Spanned;
import android.text.style.ClickableSpan;
import android.text.style.StyleSpan;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import androidx.annotation.DrawableRes;
import androidx.annotation.NonNull;
import androidx.appcompat.content.res.AppCompatResources;
import androidx.browser.customtabs.CustomTabsIntent;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import com.afollestad.materialdialogs.MaterialDialog;
import com.firebase.jobdispatcher.*;
import okhttp3.CipherSuite;
import okhttp3.ConnectionSpec;
import okhttp3.OkHttpClient;
import okhttp3.TlsVersion;
import okhttp3.logging.HttpLoggingInterceptor;
import maelumat.almuntaj.abdalfattah.altaeb.R;
import maelumat.almuntaj.abdalfattah.altaeb.jobs.SavedProductUploadJob;
import maelumat.almuntaj.abdalfattah.altaeb.models.DaoSession;
import maelumat.almuntaj.abdalfattah.altaeb.models.Product;
import maelumat.almuntaj.abdalfattah.altaeb.views.ContinuousScanActivity;
import maelumat.almuntaj.abdalfattah.altaeb.views.OFFApplication;
import maelumat.almuntaj.abdalfattah.altaeb.views.ProductBrowsingListActivity;
import maelumat.almuntaj.abdalfattah.altaeb.views.customtabs.CustomTabActivityHelper;
import maelumat.almuntaj.abdalfattah.altaeb.views.customtabs.WebViewFallback;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.*;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.TimeUnit;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static android.text.TextUtils.isEmpty;

public class Utils {
    public static final String SPACE = " ";
    public static final int MY_PERMISSIONS_REQUEST_CAMERA = 1;
    public static final int MY_PERMISSIONS_REQUEST_STORAGE = 2;
    private static final String UPLOAD_JOB_TAG = "upload_saved_product_job";
    private static boolean isUploadJobInitialised;
    public static final String LAST_REFRESH_DATE = "last_refresh_date_of_taxonomies";
    public static final String HEADER_USER_AGENT_SCAN = "Scan";
    public static final String HEADER_USER_AGENT_SEARCH = "Search";
    public static final int NO_DRAWABLE_RESOURCE = 0;

    /**
     * Returns a CharSequence that concatenates the specified array of CharSequence
     * objects and then applies a list of zero or more tags to the entire range.
     *
     * @param content an array of character sequences to apply a style to
     * @param tags the styled span objects to apply to the content
     *     such as android.text.style.StyleSpan
     */
    private static CharSequence apply(CharSequence[] content, Object... tags) {
        SpannableStringBuilder text = new SpannableStringBuilder();
        openTags(text, tags);
        for (CharSequence item : content) {
            text.append(item);
        }
        closeTags(text, tags);
        return text;
    }

    /**
     * Iterates over an array of tags and applies them to the beginning of the specified
     * Spannable object so that future text appended to the text will have the styling
     * applied to it. Do not call this method directly.
     */
    private static void openTags(Spannable text, Object[] tags) {
        for (Object tag : tags) {
            text.setSpan(tag, 0, 0, Spannable.SPAN_MARK_MARK);
        }
    }

    /**
     * "Closes" the specified tags on a Spannable by updating the spans to be
     * endpoint-exclusive so that future text appended to the end will not take
     * on the same styling. Do not call this method directly.
     */
    private static void closeTags(Spannable text, Object[] tags) {
        int len = text.length();
        for (Object tag : tags) {
            if (len > 0) {
                text.setSpan(tag, 0, len, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
            } else {
                text.removeSpan(tag);
            }
        }
    }

    /**
     * Returns a CharSequence that applies boldface to the concatenation
     * of the specified CharSequence objects.
     */
    public static CharSequence bold(CharSequence... content) {
        return apply(content, new StyleSpan(Typeface.BOLD));
    }

    public static void hideKeyboard(Activity activity) {
        if (activity == null) {
            return;
        }

        View view = activity.getCurrentFocus();

        if (view != null) {
            InputMethodManager imm = (InputMethodManager) activity.getSystemService(Activity.INPUT_METHOD_SERVICE);
            if (imm != null) {
                imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
            }
        }
    }

    public static String compressImage(String url) {
        File fileFront = new File(url);
        Bitmap bt = decodeFile(fileFront);
        if (bt == null) {
            Log.e("COMPRESS_IMAGE", url + " not found");
            return null;
        }

        File smallFileFront = new File(url.replace(".png", "_small.png"));

        try (OutputStream fOutFront = new FileOutputStream(smallFileFront)) {
            bt.compress(Bitmap.CompressFormat.PNG, 100, fOutFront);
        } catch (IOException e) {
            Log.e("COMPRESS_IMAGE", e.getMessage(), e);
        }
        return smallFileFront.toString();
    }

    public static int getColor(Context context, int id) {
        final int version = Build.VERSION.SDK_INT;
        if (version >= 23) {
            return ContextCompat.getColor(context, id);
        } else {
            return context.getResources().getColor(id);
        }
    }

    // Decodes image and scales it to reduce memory consumption
    private static Bitmap decodeFile(File f) {
        try {
            // Decode image size
            BitmapFactory.Options o = new BitmapFactory.Options();
            o.inJustDecodeBounds = true;
            BitmapFactory.decodeStream(new FileInputStream(f), null, o);

            // The new size we want to scale to
            final int REQUIRED_SIZE = 1200;

            // Find the correct scale value. It should be the power of 2.
            int scale = 1;
            while (o.outWidth / scale / 2 >= REQUIRED_SIZE &&
                o.outHeight / scale / 2 >= REQUIRED_SIZE) {
                scale *= 2;
            }

            // Decode with inSampleSize
            BitmapFactory.Options o2 = new BitmapFactory.Options();
            o2.inSampleSize = scale;
            return BitmapFactory.decodeStream(new FileInputStream(f), null, o2);
        } catch (FileNotFoundException e) {
            Log.e(Utils.class.getSimpleName(), "decodeFile " + f, e);
        }
        return null;
    }

    /**
     * Check if a certain application is installed on a device.
     *
     * @param context the applications context.
     * @param packageName the package name that you want to check.
     * @return true if the application is installed, false otherwise.
     */
    public static boolean isApplicationInstalled(Context context, String packageName) {
        PackageManager pm = context.getPackageManager();
        try {
            // Check if the package name exists, if exception is thrown, package name does not
            // exist.
            pm.getPackageInfo(packageName, PackageManager.GET_ACTIVITIES);
            return true;
        } catch (PackageManager.NameNotFoundException e) {
            return false;
        }
    }

    public static int getImageGrade(String grade) {
        int drawable = NO_DRAWABLE_RESOURCE;

        if (grade == null) {
            return drawable;
        }

        switch (grade.toLowerCase(Locale.getDefault())) {
            case "a":
                drawable = R.drawable.nnc_a;
                break;
            case "b":
                drawable = R.drawable.nnc_b;
                break;
            case "c":
                drawable = R.drawable.nnc_c;
                break;
            case "d":
                drawable = R.drawable.nnc_d;
                break;
            case "e":
                drawable = R.drawable.nnc_e;
                break;
            default:
                break;
        }

        return drawable;
    }

    public static String getNovaGroupExplanation(String novaGroup, Context context) {

        if (novaGroup == null) {
            return "";
        }

        switch (novaGroup) {
            case "1":
                return context.getResources().getString(R.string.nova_grp1_msg);
            case "2":
                return context.getResources().getString(R.string.nova_grp2_msg);
            case "3":
                return context.getResources().getString(R.string.nova_grp3_msg);
            case "4":
                return context.getResources().getString(R.string.nova_grp4_msg);
            default:
                return "";
        }
    }

    public static <T extends View> List<T> getViewsByType(ViewGroup root, Class<T> tClass) {
        final ArrayList<T> result = new ArrayList<>();
        int childCount = root.getChildCount();
        for (int i = 0; i < childCount; i++) {
            final View child = root.getChildAt(i);
            if (child instanceof ViewGroup) {
                result.addAll(getViewsByType((ViewGroup) child, tClass));
            }

            if (tClass.isInstance(child)) {
                result.add(tClass.cast(child));
            }
        }
        return result;
    }

    public static int getNovaGroupDrawable(Product product) {
        return getNovaGroupDrawable(product == null ? null : product.getNovaGroups());
    }

    public static int getNovaGroupDrawable(String novaGroup) {
        int drawable = NO_DRAWABLE_RESOURCE;

        if (novaGroup == null) {
            return drawable;
        }

        switch (novaGroup) {
            case "1":
                drawable = R.drawable.ic_nova_group_1;
                break;
            case "2":
                drawable = R.drawable.ic_nova_group_2;
                break;
            case "3":
                drawable = R.drawable.ic_nova_group_3;
                break;
            case "4":
                drawable = R.drawable.ic_nova_group_4;
                break;
            default:
                break;
        }
        return drawable;
    }

    public static int getSmallImageGrade(Product product) {
        return getSmallImageGrade(product == null ? null : product.getNutritionGradeFr());
    }

    public static int getImageGrade(Product product) {
        return getImageGrade(product == null ? null : product.getNutritionGradeFr());
    }

    public static int getImageEnvironmentImpact(Product product) {
        int drawable = NO_DRAWABLE_RESOURCE;
        if (product == null) {
            return drawable;
        }
        List<String> tags = product.getEnvironmentImpactLevelTags();
        if (CollectionUtils.isEmpty(tags)) {
            return drawable;
        }
        String tag = tags.get(0).replace("\"", "");
        switch (tag) {
            case "en:high":
                return R.drawable.ic_co2_high_24dp;
            case "en:low":
                return R.drawable.ic_co2_low_24dp;
            case "en:medium":
                return R.drawable.ic_co2_medium_24dp;
            default:
                return drawable;
        }
    }

    public static int getSmallImageGrade(String grade) {
        int drawable = NO_DRAWABLE_RESOURCE;

        if (grade == null) {
            return drawable;
        }

        switch (grade.toLowerCase(Locale.getDefault())) {
            case "a":
                drawable = R.drawable.nnc_small_a;
                break;
            case "b":
                drawable = R.drawable.nnc_small_b;
                break;
            case "c":
                drawable = R.drawable.nnc_small_c;
                break;
            case "d":
                drawable = R.drawable.nnc_small_d;
                break;
            case "e":
                drawable = R.drawable.nnc_small_e;
                break;
            default:
                break;
        }

        return drawable;
    }

    public static Bitmap getBitmapFromDrawable(Context context, @DrawableRes int drawableId) {
        Drawable drawable = AppCompatResources.getDrawable(context, drawableId);
        if(drawable==null){return null;}
        Bitmap bitmap = Bitmap.createBitmap(drawable.getIntrinsicWidth(), drawable
            .getIntrinsicHeight(), Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmap);
        drawable.setBounds(0, 0, canvas.getWidth(), canvas.getHeight());
        drawable.draw(canvas);

        return bitmap;
    }

    /**
     * Return a round float value with 2 decimals
     *
     * @param value float value
     * @return round value or 0 if the value is empty or equals to 0
     */
    public static String getRoundNumber(String value) {
        if ("0".equals(value)) {
            return value;
        }

        if (isEmpty(value)) {
            return "?";
        }

        String[] strings = value.split("\\.");
        if (strings.length == 1 || (strings.length == 2 && strings[1].length() <= 2)) {
            return value;
        }

        return String.format(Locale.getDefault(), "%.2f", Double.valueOf(value));
    }

    /**
     * @see Utils#getRoundNumber(String)
     */
    public static String getRoundNumber(float value) {
        return getRoundNumber(Float.toString(value));
    }

    public static DaoSession getAppDaoSession(Context context) {
        return ((OFFApplication) context.getApplicationContext()).getDaoSession();
    }

    public static DaoSession getDaoSession(Context context) {
        return OFFApplication.daoSession;
    }

    /**
     * Check if the device has a camera installed.
     *
     * @return true if installed, false otherwise.
     */
    public static boolean isHardwareCameraInstalled(Context context) {
        if (context == null) {
            return false;
        }
        try {
            if (context.getPackageManager().hasSystemFeature(PackageManager.FEATURE_CAMERA)) {
                return true;
            }
        } catch (NullPointerException e) {
            if (BuildConfig.DEBUG) {
                Log.i(context.getClass().getSimpleName(), e.toString());
            }
            return false;
        }
        return false;
    }

    /**
     * Schedules job to download when network is available
     */
    public static synchronized void scheduleProductUploadJob(Context context) {
        if (isUploadJobInitialised) {
            return;
        }
        final int periodicity = (int) TimeUnit.MINUTES.toSeconds(30);
        final int toleranceInterval = (int) TimeUnit.MINUTES.toSeconds(5);
        Driver driver = new GooglePlayDriver(context);
        FirebaseJobDispatcher jobDispatcher = new FirebaseJobDispatcher(driver);
        Job uploadJob = jobDispatcher.newJobBuilder()
            .setService(SavedProductUploadJob.class)
            .setTag(UPLOAD_JOB_TAG)
            .setConstraints(Constraint.ON_UNMETERED_NETWORK)
            .setLifetime(Lifetime.FOREVER)
            .setRecurring(false)
            .setTrigger(Trigger.executionWindow(periodicity, periodicity + toleranceInterval))
            .setReplaceCurrent(false)
            .build();
        jobDispatcher.schedule(uploadJob);
        isUploadJobInitialised = true;
    }

    public static OkHttpClient HttpClientBuilder() {
        OkHttpClient httpClient;
        if (Build.VERSION.SDK_INT == 24) {
            ConnectionSpec spec = new ConnectionSpec.Builder(ConnectionSpec.MODERN_TLS)
                .tlsVersions(TlsVersion.TLS_1_2)
                .cipherSuites(CipherSuite.TLS_DHE_RSA_WITH_AES_128_GCM_SHA256)
                .build();

            httpClient = new OkHttpClient.Builder()
                .connectTimeout(5000, TimeUnit.MILLISECONDS)
                .readTimeout(30000, TimeUnit.MILLISECONDS)
                .writeTimeout(30000, TimeUnit.MILLISECONDS)
                .connectionSpecs(Collections.singletonList(spec))
                .addInterceptor(new HttpLoggingInterceptor().setLevel(HttpLoggingInterceptor.Level.BODY))
                .build();
        } else {
            httpClient = new OkHttpClient.Builder()
                .connectTimeout(5000, TimeUnit.MILLISECONDS)
                .readTimeout(30000, TimeUnit.MILLISECONDS)
                .writeTimeout(30000, TimeUnit.MILLISECONDS)
                .addInterceptor(new HttpLoggingInterceptor().setLevel(HttpLoggingInterceptor.Level.BODY))
                .build();
        }
        return httpClient;
    }

    /**
     * Check if airplane mode is turned on on the device.
     *
     * @param context of the application.
     * @return true if airplane mode is active.
     */
    @SuppressWarnings("depreciation")
    @TargetApi(Build.VERSION_CODES.JELLY_BEAN_MR1)
    public static boolean isAirplaneModeActive(Context context) {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.JELLY_BEAN_MR1) {
            return Settings.System.getInt(context.getContentResolver(),
                Settings.System.AIRPLANE_MODE_ON, 0) != 0;
        } else {
            return Settings.Global.getInt(context.getContentResolver(),
                Settings.Global.AIRPLANE_MODE_ON, 0) != 0;
        }
    }

    public static boolean isUserLoggedIn(Context context) {
        if (context == null) {
            return false;
        }
        final SharedPreferences settings = context.getSharedPreferences("login", 0);
        final String login = settings.getString("user", "");
        return StringUtils.isNotEmpty(login);
    }

    /**
     * Check if the user is connected to a network. This can be any network.
     *
     * @param context of the application.
     * @return true if connected or connecting. False otherwise.
     */
    public static boolean isNetworkConnected(Context context) {
        ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetwork = null;
        if (cm != null) {
            activeNetwork = cm.getActiveNetworkInfo();
        }

        return activeNetwork != null && activeNetwork.isConnectedOrConnecting();
    }

    /**
     * Check if the user is connected to a mobile network.
     *
     * @param context of the application.
     * @return true if connected to mobile data.
     */
    public static boolean isConnectedToMobileData(Context context) {
        return getNetworkType(context).equals("Mobile");
    }

    /**
     * Get the type of network that the user is connected to.
     *
     * @param context of the application.
     * @return the type of network that is connected.
     */
    private static String getNetworkType(Context context) {
        ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetwork = cm.getActiveNetworkInfo();

        if (activeNetwork != null && activeNetwork.isConnectedOrConnecting()) {
            switch (activeNetwork.getType()) {
                case ConnectivityManager.TYPE_ETHERNET:
                    return "Ethernet";
                case ConnectivityManager.TYPE_MOBILE:
                    return "Mobile";
                case ConnectivityManager.TYPE_VPN:
                    return "VPN";
                case ConnectivityManager.TYPE_WIFI:
                    return "WiFi";
                case ConnectivityManager.TYPE_WIMAX:
                    return "WiMax";
                default:
                    break;
            }
        }

        return "Other";
    }

    private static String timeStamp() {
        return ((Long) System.currentTimeMillis()).toString();
    }

    public static File makeOrGetPictureDirectory(Context context) {
        // determine the profile directory
        File dir = context.getFilesDir();

        if (isExternalStorageWritable()) {
            dir = context.getExternalFilesDir(null);
        }
        File picDir = new File(dir, "Pictures");
        if (picDir.exists()) {
            return picDir;
        }
        // creates the directory if not present yet
        final boolean mkdir = picDir.mkdir();
        if(!mkdir){
            Log.e(Utils.class.getSimpleName(),"Can create dir "+picDir);
        }


        return picDir;
    }

    public static boolean isExternalStorageWritable() {
        String state = Environment.getExternalStorageState();
        return Environment.MEDIA_MOUNTED.equals(state);
    }

    public static Uri getOutputPicUri(Context context) {
        return (Uri.fromFile(new File(Utils.makeOrGetPictureDirectory(context), "/" + Utils.timeStamp() + ".jpg")));
    }

    public static CharSequence getClickableText(String text, String urlParameter, @SearchType String type, Activity activity, CustomTabsIntent customTabsIntent) {
        ClickableSpan clickableSpan;
        String url = SearchTypeUrls.getUrl(type);

        if (url == null) {
            clickableSpan = new ClickableSpan() {
                @Override
                public void onClick(View view) {
                    ProductBrowsingListActivity.startActivity(activity, text, type);
                }
            };
        } else {
            Uri uri = Uri.parse(url + urlParameter);
            clickableSpan = new ClickableSpan() {
                @Override
                public void onClick(View textView) {
                    CustomTabActivityHelper.openCustomTab(activity, customTabsIntent, uri, new WebViewFallback());
                }
            };
        }

        SpannableString spannableText = new SpannableString(text);
        spannableText.setSpan(clickableSpan, 0, text.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        return spannableText;
    }

    /**
     * convert energy from kj to kcal for a product.
     *
     * @param value of energy in kj.
     * @return energy in kcal.
     */
    public static String getEnergy(String value) {
        String defaultValue = StringUtils.EMPTY;
        if (defaultValue.equals(value) || isEmpty(value)) {
            return defaultValue;
        }

        try {
            int energyKcal = convertKjToKcal(Double.parseDouble(value));
            return String.valueOf(energyKcal);
        } catch (NumberFormatException e) {
            return defaultValue;
        }
    }

    private static int convertKjToKcal(double kj) {
        return kj != 0 ? (int) (kj / 4.1868d) : -1;
    }

    /**
     * Function which returns volume in oz if parameter is in cl, ml, or l
     *
     * @param servingSize value to transform
     * @return volume in oz if servingSize is a volume parameter else return the the parameter unchanged
     */
    public static String getServingInOz(String servingSize) {

        Pattern regex = Pattern.compile("(\\d+(?:\\.\\d+)?)");
        Matcher matcher = regex.matcher(servingSize);
        if (servingSize.toLowerCase().contains("ml")) {
            matcher.find();
            Float val = Float.parseFloat(matcher.group(1));
            val *= 0.033814f;
            servingSize = getRoundNumber(val).concat(" oz");
        } else if (servingSize.toLowerCase().contains("cl")) {
            matcher.find();
            Float val = Float.parseFloat(matcher.group(1));
            val *= 0.33814f;
            servingSize = getRoundNumber(val).concat(" oz");
        } else if (servingSize.toLowerCase().contains("l")) {
            matcher.find();
            Float val = Float.parseFloat(matcher.group(1));
            val *= 33.814f;
            servingSize = getRoundNumber(val).concat(" oz");
        }
        return servingSize;
    }

    /**
     * Function that returns the volume in liters if input parameter is in oz
     *
     * @param servingSize the value to transform: not null
     * @return volume in liter if input parameter is a volume parameter else return the parameter unchanged
     */
    public static String getServingInL(@NonNull String servingSize) {

        if (servingSize.toLowerCase().contains("oz")) {
            Pattern regex = Pattern.compile("(\\d+(?:\\.\\d+)?)");
            Matcher matcher = regex.matcher(servingSize);
            matcher.find();
            Float val = Float.parseFloat(matcher.group(1));
            val /= 33.814f;
            servingSize = Float.toString(val).concat(" l");
        }

        return servingSize;
    }

    /**
     * Function which returns true if the battery level is low
     *
     * @param context the context
     * @return true if battery is low or false if battery in not low
     */
    public static boolean getBatteryLevel(Context context) {
        IntentFilter ifilter = new IntentFilter(Intent.ACTION_BATTERY_CHANGED);
        Intent batteryStatus = context.registerReceiver(null, ifilter);
        int level = batteryStatus.getIntExtra(BatteryManager.EXTRA_LEVEL, -1);
        int scale = batteryStatus.getIntExtra(BatteryManager.EXTRA_SCALE, -1);

        float batteryPct = (level / (float) scale);
        Log.i("BATTERYSTATUS", String.valueOf(batteryPct));

        return (int) ((batteryPct) * 100) <= 15;
    }

    public static boolean isDisableImageLoad(Context context) {
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);
        return preferences.getBoolean("disableImageLoad", false);
    }

    /*
     * Function to open ContinuousScanActivity to facilitate scanning
     * @param activity
     */
    public static void scan(Activity activity) {

        if (ContextCompat.checkSelfPermission(activity, Manifest.permission.CAMERA) !=
            PackageManager.PERMISSION_GRANTED) {
            if (ActivityCompat.shouldShowRequestPermissionRationale(activity, Manifest
                .permission.CAMERA)) {
                new MaterialDialog.Builder(activity)
                    .title(R.string.action_about)
                    .content(R.string.permission_camera)
                    .neutralText(R.string.txtOk)
                    .show().setOnDismissListener(dialogInterface -> ActivityCompat.requestPermissions(activity,
                    new String[]{Manifest.permission.CAMERA},
                    Utils.MY_PERMISSIONS_REQUEST_CAMERA));
            } else {
                ActivityCompat.requestPermissions(activity, new String[]{Manifest
                    .permission.CAMERA}, Utils.MY_PERMISSIONS_REQUEST_CAMERA);
            }
        } else {
            Intent intent = new Intent(activity, ContinuousScanActivity.class);
            activity.startActivity(intent);
        }
    }

    /**
     * @param context The context
     * @return Returns the version name of the app
     */
    public static String getVersionName(Context context) {
        try {
            PackageInfo pInfo = context.getPackageManager().getPackageInfo(context.getPackageName(), 0);
            return pInfo.versionName;
        } catch (PackageManager.NameNotFoundException e) {
            Log.e(Utils.class.getSimpleName(), "getVersionName", e);
        }
        return "(version unknown)";
    }

    /**
     * @param type Type of call (Search or Scan)
     * @return Returns the header to be put in network call
     */
    public static String getUserAgent(String type) {
        final String prefix = "Official Android App ";
        if (type.equals(HEADER_USER_AGENT_SCAN)) {
            return prefix + BuildConfig.VERSION_NAME + " " + HEADER_USER_AGENT_SCAN;
        } else if (type.equals(HEADER_USER_AGENT_SEARCH)) {
            return prefix + BuildConfig.VERSION_NAME + " " + HEADER_USER_AGENT_SEARCH;
        }
        return prefix + BuildConfig.VERSION_NAME;
    }

     /*
     @param Takes a string
     @return Returns a Json object
      */

    public static JSONObject createJsonObject(String response) {
        JSONObject jsonObject = null;
        try {
            jsonObject = new JSONObject(response);
        } catch (JSONException e) {
            Log.e(Utils.class.getSimpleName(), "createJsonObject", e);
        }
        return jsonObject;
    }
}

