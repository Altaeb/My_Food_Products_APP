
package maelumat.almuntaj.abdalfattah.altaeb.repositories;

import java.util.List;

import io.reactivex.Single;
import maelumat.almuntaj.abdalfattah.altaeb.models.Additive;
import maelumat.almuntaj.abdalfattah.altaeb.models.AdditiveName;
import maelumat.almuntaj.abdalfattah.altaeb.models.Allergen;
import maelumat.almuntaj.abdalfattah.altaeb.models.AllergenName;
import maelumat.almuntaj.abdalfattah.altaeb.models.Category;
import maelumat.almuntaj.abdalfattah.altaeb.models.CategoryName;
import maelumat.almuntaj.abdalfattah.altaeb.models.Country;
import maelumat.almuntaj.abdalfattah.altaeb.models.CountryName;
import maelumat.almuntaj.abdalfattah.altaeb.models.Ingredient;
import maelumat.almuntaj.abdalfattah.altaeb.models.InsightAnnotationResponse;
import maelumat.almuntaj.abdalfattah.altaeb.models.Label;
import maelumat.almuntaj.abdalfattah.altaeb.models.LabelName;
import maelumat.almuntaj.abdalfattah.altaeb.models.Question;
import maelumat.almuntaj.abdalfattah.altaeb.models.Tag;

/**
 * This is a repository class working as an Interface.
 * It defines all the functions in Repository component.
 * @author Lobster
 * @since 03.03.18
 */

public interface IProductRepository {

    Single<List<Label>> getLabels(Boolean refresh);

    Single<List<Allergen>> getAllergens(Boolean refresh);

    Single<List<Tag>> getTags(Boolean refresh);

    Single<List<Additive>> getAdditives(Boolean refresh);

    Single<List<Country>> getCountries(Boolean refresh);

    Single<List<Category>> getCategories(Boolean refresh);

    Single<List<Ingredient>> getIngredients(Boolean refresh);

    void saveLabels(List<Label> labels);

    void saveTags(List<Tag> tags);

    void saveAdditives(List<Additive> additives);

    void saveCountries(List<Country> countries);

    void saveAllergens(List<Allergen> allergens);

    void saveCategories(List<Category> categories);

    void deleteIngredientCascade();

    void saveIngredients(List<Ingredient> ingredients);

    void saveIngredient(Ingredient ingredient);

    void setAllergenEnabled(String allergenTag, Boolean isEnabled);

    Single<LabelName> getLabelByTagAndLanguageCode(String labelTag, String languageCode);

    Single<LabelName> getLabelByTagAndDefaultLanguageCode(String labelTag);

    Single<CountryName> getCountryByTagAndLanguageCode(String labelTag, String languageCode);

    Single<CountryName> getCountryByTagAndDefaultLanguageCode(String labelTag);

    Single<AdditiveName> getAdditiveByTagAndLanguageCode(String additiveTag, String languageCode);

    Single<AdditiveName> getAdditiveByTagAndDefaultLanguageCode(String additiveTag);

    Single<CategoryName> getCategoryByTagAndLanguageCode(String categoryTag, String languageCode);

    Single<CategoryName> getCategoryByTagAndDefaultLanguageCode(String categoryTag);

    Single<List<CategoryName>> getAllCategoriesByLanguageCode(String languageCode);

    Single<List<CategoryName>> getAllCategoriesByDefaultLanguageCode();

    List<Allergen> getEnabledAllergens();

    Single<List<AllergenName>> getAllergensByEnabledAndLanguageCode(Boolean isEnabled, String languageCode);

    Single<List<AllergenName>> getAllergensByLanguageCode(String languageCode);

    Single<AllergenName> getAllergenByTagAndLanguageCode(String allergenTag, String languageCode);

    Single<AllergenName> getAllergenByTagAndDefaultLanguageCode(String allergenTag);

    Boolean additivesIsEmpty();

    Single<Question> getSingleProductQuestion(String code, String lang);

    Single<InsightAnnotationResponse> annotateInsight(String insightId, int annotation);
}
