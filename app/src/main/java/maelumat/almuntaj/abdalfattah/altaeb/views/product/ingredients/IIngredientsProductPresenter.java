package maelumat.almuntaj.abdalfattah.altaeb.views.product.ingredients;

import java.util.List;

import maelumat.almuntaj.abdalfattah.altaeb.models.AdditiveName;
import maelumat.almuntaj.abdalfattah.altaeb.models.AllergenName;
import maelumat.almuntaj.abdalfattah.altaeb.utils.ProductInfoState;

/**
 * Created by Lobster on 17.03.18.
 */

public interface IIngredientsProductPresenter {

    interface Actions {
        void loadAdditives();
        void loadAllergens();
        void dispose();
    }

    interface View {
        void showAdditives(List<AdditiveName> additives);
        void showAdditivesState(@ProductInfoState String state);

        void showAllergens(List<AllergenName> allergens);
        void showAllergensState(@ProductInfoState String state);
    }

}