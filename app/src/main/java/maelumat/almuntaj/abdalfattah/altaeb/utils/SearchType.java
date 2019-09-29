package maelumat.almuntaj.abdalfattah.altaeb.utils;

import androidx.annotation.StringDef;

import java.lang.annotation.Retention;

import static java.lang.annotation.RetentionPolicy.SOURCE;
import static maelumat.almuntaj.abdalfattah.altaeb.utils.SearchType.*;

/**
 * Created by Lobster on 10.03.18.
 */

@Retention(SOURCE)
@StringDef({
        ADDITIVE,
        ALLERGEN,
        BRAND,
        CATEGORY,
        COUNTRY,
        EMB,
        LABEL,
        PACKAGING,
        SEARCH,
        STORE,
        TRACE,
        CONTRIBUTOR,
        STATE,
        INCOMPLETE_PRODUCT,
        ORIGIN,
        MANUFACTURING_PLACE
})
public @interface SearchType {

    String ADDITIVE = "additive";
    String ALLERGEN = "allergen";
    String BRAND = "brand";
    String CATEGORY = "category";
    String COUNTRY = "country";
    String EMB = "emb";
    String LABEL = "label";
    String PACKAGING = "packaging";
    String SEARCH = "search";
    String STORE = "store";
    String TRACE = "trace";
    String CONTRIBUTOR = "contributor";
    String INCOMPLETE_PRODUCT = "incomplete_product";
    String STATE = "state";
    String ORIGIN = "origin";
    String MANUFACTURING_PLACE = "manufacturing-place";
}
