package maelumat.almuntaj.abdalfattah.altaeb;

import androidx.test.runner.AndroidJUnit4;
import maelumat.almuntaj.abdalfattah.altaeb.test.ScreenshotActivityTestRule;
import maelumat.almuntaj.abdalfattah.altaeb.views.ContinuousScanActivity;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

/**
 * Take screenshots...
 */
@RunWith(AndroidJUnit4.class)
public class TakeScreenshotScanActivityTest extends AbstractScreenshotTest {
    public static final int MS_TO_WAIT_TO_DISPLAY_PRODUCT_IN_SCAN = 2000;
    @Rule
    public ScreenshotActivityTestRule<ContinuousScanActivity> activity = new ScreenshotActivityTestRule<>(ContinuousScanActivity.class);

    @Test
    public void testTakeScreenshotScanActivity() {
        activity.setAfterActivityLaunchedAction(screenshotActivityTestRule ->
        {
            try {
                screenshotActivityTestRule.runOnUiThread(() -> {
                    ((ContinuousScanActivity) screenshotActivityTestRule.getActivity()).showProduct(screenshotActivityTestRule.getScreenshotParameter().getProductCodes().get(0));
                });
                Thread.sleep(MS_TO_WAIT_TO_DISPLAY_PRODUCT_IN_SCAN);
            } catch (Throwable throwable) {
                throwable.printStackTrace();
            }
        });

        startForAllLocales(activity);
    }
}
