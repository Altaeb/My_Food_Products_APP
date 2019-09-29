package maelumat.almuntaj.abdalfattah.altaeb;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import androidx.test.rule.GrantPermissionRule;
import android.util.Log;
import maelumat.almuntaj.abdalfattah.altaeb.fragments.PreferencesFragment;
import maelumat.almuntaj.abdalfattah.altaeb.test.ScreenshotActivityTestRule;
import maelumat.almuntaj.abdalfattah.altaeb.test.ScreenshotParameter;
import maelumat.almuntaj.abdalfattah.altaeb.test.ScreenshotsLocaleProvider;
import maelumat.almuntaj.abdalfattah.altaeb.utils.LocaleHelper;
import maelumat.almuntaj.abdalfattah.altaeb.views.OFFApplication;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Rule;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

import java.util.Collection;
import java.util.Locale;

/**
 * Take screenshots...
 */
@RunWith(JUnit4.class)
public abstract class AbstractScreenshotTest {
    public static final String ACTION_NAME = "actionName";
    private static final String LOG_TAG = AbstractScreenshotTest.class.getSimpleName();
    @Rule
    public GrantPermissionRule permissionRule = GrantPermissionRule.grant(Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.CHANGE_CONFIGURATION);
    private static Locale initLocale;
    ScreenshotsLocaleProvider localeProvider = new ScreenshotsLocaleProvider();

    protected void startScreenshotActivityTestRules(ScreenshotParameter screenshotParameter, ScreenshotActivityTestRule... activityRules) {
        changeLocale(screenshotParameter);
        for (ScreenshotActivityTestRule activityRule : activityRules) {
            activityRule.finishActivity();
            activityRule.setScreenshotParameter(screenshotParameter);
            activityRule.launchActivity(null);
        }
    }

    protected void startScreenshotActivityTestRules(ScreenshotParameter screenshotParameter, ScreenshotActivityTestRule activityRule,
                                                    Collection<Intent> intents) {

        changeLocale(screenshotParameter);
        for (Intent intent : intents) {
            activityRule.finishActivity();
            String title = intent.getStringExtra(ACTION_NAME);
            if (title != null) {
                activityRule.setName(title);
            }
            activityRule.setActivityIntent(intent);
            activityRule.setScreenshotParameter(screenshotParameter);
            activityRule.launchActivity(null);
        }
    }

    protected void changeLocale(ScreenshotParameter parameter) {
        changeLocale(parameter, OFFApplication.getInstance());
    }

    protected void changeLocale(ScreenshotParameter parameter, Context context) {
        Log.d(LOG_TAG, "Change parameters to " + parameter);
        LocaleHelper.setLocale(context, parameter.getLocale());
        final String countryName = parameter.getCountryTag();
    }


    public void startForAllLocales(ScreenshotActivityTestRule... activityRule) {
        for (ScreenshotParameter screenshotParameter : localeProvider.getParameters()) {
            startScreenshotActivityTestRules(screenshotParameter, activityRule);
        }
    }

    @AfterClass
    public static void resetLanguage() {
        LocaleHelper.setLocale(initLocale);
    }

    @BeforeClass
    public static void initLanguage() {
        initLocale = LocaleHelper.getLocale();
    }

}
