package maelumat.almuntaj.abdalfattah.altaeb;

import androidx.test.runner.AndroidJUnit4;
import maelumat.almuntaj.abdalfattah.altaeb.test.ScreenshotActivityTestRule;
import maelumat.almuntaj.abdalfattah.altaeb.views.MainActivity;
import maelumat.almuntaj.abdalfattah.altaeb.views.WelcomeActivity;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

/**
 * Take screenshots...
 */
@RunWith(AndroidJUnit4.class)
public class TakeScreenshotMainActivityTest extends AbstractScreenshotTest {
    @Rule
    public ScreenshotActivityTestRule<MainActivity> activityRule = new ScreenshotActivityTestRule<>(MainActivity.class);

    @Rule
    public ScreenshotActivityTestRule<WelcomeActivity> welcomeActivityRule = new ScreenshotActivityTestRule<>(WelcomeActivity.class);

    @Test
    public void testTakeScreenshotMainActivity() {
        welcomeActivityRule.setFirstTimeLaunched(true);
        startForAllLocales(welcomeActivityRule,activityRule);
    }
}
