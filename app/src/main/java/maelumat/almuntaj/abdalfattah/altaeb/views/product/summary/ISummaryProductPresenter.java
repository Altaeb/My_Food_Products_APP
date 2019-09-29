
package maelumat.almuntaj.abdalfattah.altaeb.views.product.summary;

import java.util.List;

import maelumat.almuntaj.abdalfattah.altaeb.models.AdditiveName;
import maelumat.almuntaj.abdalfattah.altaeb.models.AllergenName;
import maelumat.almuntaj.abdalfattah.altaeb.models.CategoryName;
import maelumat.almuntaj.abdalfattah.altaeb.models.InsightAnnotationResponse;
import maelumat.almuntaj.abdalfattah.altaeb.models.LabelName;
import maelumat.almuntaj.abdalfattah.altaeb.models.Question;
import maelumat.almuntaj.abdalfattah.altaeb.utils.ProductInfoState;

/**
 * Created by Lobster on 17.03.18.
 */

public interface ISummaryProductPresenter {

    interface Actions {
        void loadProductQuestion();

        void annotateInsight(String insightId, int annotation);

        void loadAllergens(Runnable runIfError);

        void loadCategories();

        void loadLabels();

        void dispose();

        void loadAdditives();
    }

    interface View {
        void showAllergens(List<AllergenName> allergens);

        void showProductQuestion(Question question);

        void showAnnotatedInsightToast(InsightAnnotationResponse insightAnnotationResponse);

        void showCategories(List<CategoryName> categories);

        void showLabels(List<LabelName> labels);

        void showCategoriesState(@ProductInfoState String state);

        void showLabelsState(@ProductInfoState String state);

        void showAdditives(List<AdditiveName> additives);

        void showAdditivesState(@ProductInfoState String state);
    }

}
