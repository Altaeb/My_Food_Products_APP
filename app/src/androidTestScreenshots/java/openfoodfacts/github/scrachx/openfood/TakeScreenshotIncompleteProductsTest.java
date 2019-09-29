package maelumat.almuntaj.abdalfattah.altaeb;

import android.content.Intent;
import androidx.test.runner.AndroidJUnit4;
import maelumat.almuntaj.abdalfattah.altaeb.test.ScreenshotActivityTestRule;
import maelumat.almuntaj.abdalfattah.altaeb.utils.SearchInfo;
import maelumat.almuntaj.abdalfattah.altaeb.views.OFFApplication;
import maelumat.almuntaj.abdalfattah.altaeb.views.ProductBrowsingListActivity;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

/**
 * Take screenshots...
 */
@RunWith(AndroidJUnit4.class)
public class TakeScreenshotIncompleteProductsTest extends AbstractScreenshotTest {
    @Rule
    public ScreenshotActivityTestRule<ProductBrowsingListActivity> incompleteRule = new ScreenshotActivityTestRule<>(ProductBrowsingListActivity.class, "incompleteProducts",
        createSearchIntent(SearchInfo.emptySearchInfo()));

    private static Intent createSearchIntent(SearchInfo searchInfo) {
        Intent intent = new Intent(OFFApplication.getInstance(), ProductBrowsingListActivity.class);
        intent.putExtra(ProductBrowsingListActivity.SEARCH_INFO, searchInfo);
        return intent;
    }

    @Test
    public void testTakeScreenshot() {
        startForAllLocales(incompleteRule);
    }
}
