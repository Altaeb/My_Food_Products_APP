
package maelumat.almuntaj.abdalfattah.altaeb.utils;

import androidx.annotation.StringDef;

import java.lang.annotation.Retention;

import static java.lang.annotation.RetentionPolicy.SOURCE;
import static maelumat.almuntaj.abdalfattah.altaeb.utils.ProductInfoState.EMPTY;
import static maelumat.almuntaj.abdalfattah.altaeb.utils.ProductInfoState.LOADING;

/**
 * Created by Lobster on 10.03.18.
 */

@Retention(SOURCE)
@StringDef({
        LOADING,
        EMPTY
})
public @interface ProductInfoState {
    String LOADING = "loading";
    String EMPTY = "empty";
}
