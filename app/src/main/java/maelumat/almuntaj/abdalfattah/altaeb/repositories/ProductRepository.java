package maelumat.almuntaj.abdalfattah.altaeb.repositories;

import android.util.Log;
import org.greenrobot.greendao.AbstractDao;
import org.greenrobot.greendao.database.Database;

import java.util.ArrayList;
import java.util.List;

import io.reactivex.Single;
import maelumat.almuntaj.abdalfattah.altaeb.models.Additive;
import maelumat.almuntaj.abdalfattah.altaeb.models.AdditiveDao;
import maelumat.almuntaj.abdalfattah.altaeb.models.AdditiveName;
import maelumat.almuntaj.abdalfattah.altaeb.models.AdditiveNameDao;
import maelumat.almuntaj.abdalfattah.altaeb.models.AdditivesWrapper;
import maelumat.almuntaj.abdalfattah.altaeb.models.Allergen;
import maelumat.almuntaj.abdalfattah.altaeb.models.AllergenDao;
import maelumat.almuntaj.abdalfattah.altaeb.models.AllergenName;
import maelumat.almuntaj.abdalfattah.altaeb.models.AllergenNameDao;
import maelumat.almuntaj.abdalfattah.altaeb.models.AllergensWrapper;
import maelumat.almuntaj.abdalfattah.altaeb.models.CategoriesWrapper;
import maelumat.almuntaj.abdalfattah.altaeb.models.Category;
import maelumat.almuntaj.abdalfattah.altaeb.models.CategoryDao;
import maelumat.almuntaj.abdalfattah.altaeb.models.CategoryName;
import maelumat.almuntaj.abdalfattah.altaeb.models.CategoryNameDao;
import maelumat.almuntaj.abdalfattah.altaeb.models.CountriesWrapper;
import maelumat.almuntaj.abdalfattah.altaeb.models.Country;
import maelumat.almuntaj.abdalfattah.altaeb.models.CountryDao;
import maelumat.almuntaj.abdalfattah.altaeb.models.CountryName;
import maelumat.almuntaj.abdalfattah.altaeb.models.CountryNameDao;
import maelumat.almuntaj.abdalfattah.altaeb.models.DaoSession;
import maelumat.almuntaj.abdalfattah.altaeb.models.Ingredient;
import maelumat.almuntaj.abdalfattah.altaeb.models.IngredientDao;
import maelumat.almuntaj.abdalfattah.altaeb.models.IngredientName;
import maelumat.almuntaj.abdalfattah.altaeb.models.IngredientNameDao;
import maelumat.almuntaj.abdalfattah.altaeb.models.IngredientsRelation;
import maelumat.almuntaj.abdalfattah.altaeb.models.IngredientsRelationDao;
import maelumat.almuntaj.abdalfattah.altaeb.models.IngredientsWrapper;
import maelumat.almuntaj.abdalfattah.altaeb.models.InsightAnnotationResponse;
import maelumat.almuntaj.abdalfattah.altaeb.models.Label;
import maelumat.almuntaj.abdalfattah.altaeb.models.LabelDao;
import maelumat.almuntaj.abdalfattah.altaeb.models.LabelName;
import maelumat.almuntaj.abdalfattah.altaeb.models.LabelNameDao;
import maelumat.almuntaj.abdalfattah.altaeb.models.LabelsWrapper;
import maelumat.almuntaj.abdalfattah.altaeb.models.Question;
import maelumat.almuntaj.abdalfattah.altaeb.models.QuestionsState;
import maelumat.almuntaj.abdalfattah.altaeb.models.Tag;
import maelumat.almuntaj.abdalfattah.altaeb.models.TagDao;
import maelumat.almuntaj.abdalfattah.altaeb.models.TagsWrapper;
import maelumat.almuntaj.abdalfattah.altaeb.network.CommonApiManager;
import maelumat.almuntaj.abdalfattah.altaeb.network.OpenFoodAPIService;
import maelumat.almuntaj.abdalfattah.altaeb.network.ProductApiService;
import maelumat.almuntaj.abdalfattah.altaeb.network.RobotoffAPIService;
import maelumat.almuntaj.abdalfattah.altaeb.views.OFFApplication;

/**
 * This is a repository class which implements repository interface.
 * @author Lobster
 * @since 03.03.18
 */

public class ProductRepository implements IProductRepository {

    private static final String DEFAULT_LANGUAGE = "en";
    private static final String TAG= ProductRepository.class.getSimpleName();

    private static IProductRepository instance;

    private ProductApiService productApi;
    private OpenFoodAPIService openFooApi;
    private RobotoffAPIService robotoffApi;

    private Database db;
    private LabelDao labelDao;
    private LabelNameDao labelNameDao;
    private TagDao tagDao;
    private AllergenDao allergenDao;
    private AllergenNameDao allergenNameDao;
    private AdditiveDao additiveDao;
    private AdditiveNameDao additiveNameDao;
    private CountryDao countryDao;
    private CountryNameDao countryNameDao;
    private CategoryDao categoryDao;
    private CategoryNameDao categoryNameDao;
    private IngredientDao ingredientDao;
    private IngredientNameDao ingredientNameDao;
    private IngredientsRelationDao ingredientsRelationDao;

    /**
     * A method used to get instance from the repository.
     * @return : instance of the repository
     */
    public static IProductRepository getInstance() {
        if (instance == null) {
            instance = new ProductRepository();
        }

        return instance;
    }

    /**
     * Constructor of the class which is used to initialize objects.
     */
    private ProductRepository() {
        productApi = CommonApiManager.getInstance().getProductApiService();
        openFooApi = CommonApiManager.getInstance().getOpenFoodApiService();
        robotoffApi = CommonApiManager.getInstance().getRobotoffApiService();

        DaoSession daoSession = OFFApplication.getInstance().getDaoSession();
        db = daoSession.getDatabase();
        labelDao = daoSession.getLabelDao();
        labelNameDao = daoSession.getLabelNameDao();
        tagDao = daoSession.getTagDao();
        allergenDao = daoSession.getAllergenDao();
        allergenNameDao = daoSession.getAllergenNameDao();
        additiveDao = daoSession.getAdditiveDao();
        additiveNameDao = daoSession.getAdditiveNameDao();
        countryDao = daoSession.getCountryDao();
        countryNameDao = daoSession.getCountryNameDao();
        categoryDao = daoSession.getCategoryDao();
        categoryNameDao = daoSession.getCategoryNameDao();
        ingredientDao = daoSession.getIngredientDao();
        ingredientNameDao = daoSession.getIngredientNameDao();
        ingredientsRelationDao = daoSession.getIngredientsRelationDao();
    }

    /**
     * Load labels from the server or local database
     *
     * @param refresh defines the source of data.
     *                If refresh is true (or local database is empty) than load it from the server,
     *                else from the local database.
     * @return The list of Labels.
     */
    @Override
    public Single<List<Label>> getLabels(Boolean refresh) {
        if (refresh || tableIsEmpty(labelDao)) {
            return productApi.getLabels()
                    .map(LabelsWrapper::map);
        } else {
            return Single.fromCallable(() -> labelDao.loadAll());
        }
    }

    /**
     * Load tags from the server or local database
     *
     * @param refresh defines the source of data.
     *                If refresh is true (or local database is empty) than load it from the server,
     *                else from the local database.
     * @return The list of Tags.
     */
    @Override
    public Single<List<Tag>> getTags(Boolean refresh) {
        if (refresh || tableIsEmpty(labelDao)) {
            return openFooApi.getTags()
                    .map(TagsWrapper::getTags);
        } else {
            return Single.fromCallable(() -> tagDao.loadAll());
        }
    }

    /**
     * Load allergens from the server or local database
     *
     * @param refresh defines the source of data.
     *                If refresh is true (or local database is empty) than load it from the server,
     *                else from the local database.
     * @return The allergens in the product.
     */
    @Override
    public Single<List<Allergen>> getAllergens(Boolean refresh) {
        if (refresh || tableIsEmpty(allergenDao)) {
            return productApi.getAllergens()
                    .map(AllergensWrapper::map);
        } else {
            return Single.fromCallable(() -> allergenDao.loadAll());
        }
    }

    /**
     * Load countries from the server or local database
     *
     * @param refresh defines the source of data.
     *                If refresh is true (or local database is empty) than load it from the server,
     *                else from the local database.
     * @return The list of countries.
     */
    @Override
    public Single<List<Country>> getCountries(Boolean refresh) {
        if (refresh || tableIsEmpty(countryDao)) {
            return productApi.getCountries()
                    .map(CountriesWrapper::map);
        } else {
            return Single.fromCallable(() -> countryDao.loadAll());
        }
    }

    /**
     * Load categories from the server or local database
     *
     * @param refresh defines the source of data.
     *                If refresh is true (or local database is empty) than load it from the server,
     *                else from the local database.
     * @return The list of categories.
     */
    @Override
    public Single<List<Category>> getCategories(Boolean refresh) {
        if (refresh || tableIsEmpty(categoryDao)) {
            return productApi.getCategories()
                    .map(CategoriesWrapper::map);
        } else {
            return Single.fromCallable(() -> categoryDao.loadAll());
        }
    }

    /**
     * Load allergens which user selected earlier (i.e user's allergens)
     * @return The list of allergens.
     */
    @Override
    public List<Allergen> getEnabledAllergens() {
        return allergenDao.queryBuilder().where(AllergenDao.Properties.Enabled.eq("true")).list();
    }

    /**
     * Load additives from the server or local database
     *
     * @param refresh defines the source of data.
     *                If refresh is true (or local database is empty) than load it from the server,
     *                else from the local database.
     * @return The list of additives.
     */
    @Override
    public Single<List<Additive>> getAdditives(Boolean refresh) {
        if (refresh || tableIsEmpty(additiveDao)) {
            return productApi.getAdditives()
                    .map(AdditivesWrapper::map);
        } else {
            return Single.fromCallable(() -> additiveDao.loadAll());
        }
    }

    /**
     * TODO to be improved by loading only if required and only in the user language
     * Load ingredients from (the server or) local database
     *
     * @param refresh defines the source of data.
     *                If refresh is true (or local database is empty) than load it from the server,
     *                else from the local database.
     * @return The ingredients in the product.
     * Pour le moment, pas de question a se poser, les données ne sont que locales.
     */
    @Override
    public Single<List<Ingredient>> getIngredients(Boolean refresh) {
        if (refresh) {
            deleteIngredientCascade();
            return productApi.getIngredients()
                    .map(IngredientsWrapper::map);
            //Check ingredient from other tables
        } else if (tableIsEmpty(ingredientDao)) {
            return productApi.getIngredients()
                    .map(IngredientsWrapper::map);
        } else {
            return Single.fromCallable(() -> ingredientDao.loadAll());
        }
    }

    /**
     * Labels saving to local database
     * @param labels The list of labels to be saved.
     * <p>
     * Label and LabelName has One-To-Many relationship, therefore we need to save them separately.
     */
    @Override
    public void saveLabels(List<Label> labels) {
        db.beginTransaction();
        try {
            for (Label label : labels) {
                labelDao.insertOrReplace(label);
                for (LabelName labelName : label.getNames()) {
                    labelNameDao.insertOrReplace(labelName);
                }
            }

            db.setTransactionSuccessful();
        } catch (Exception e) {
            Log.e(TAG,"saveLabels",e);
        } finally {
            db.endTransaction();
        }
    }

    /**
     * Tags saving to local database
     * @param tags The list of tags to be saved.
     */
    @Override
    public void saveTags(List<Tag> tags) {
        tagDao.insertOrReplaceInTx(tags);
    }


    /**
     * Allergens saving to local database
     * @param allergens The list of allergens to be saved.
     * <p>
     * Allergen and AllergenName has One-To-Many relationship, therefore we need to save them separately.
     */
    @Override
    public void saveAllergens(List<Allergen> allergens) {
        db.beginTransaction();
        try {
            for (Allergen allergen : allergens) {
                allergenDao.insertOrReplace(allergen);
                for (AllergenName allergenName : allergen.getNames()) {
                    allergenNameDao.insertOrReplace(allergenName);
                }
            }

            db.setTransactionSuccessful();
        } catch (Exception e) {
            Log.e(TAG,"saveAllergens",e);
        } finally {
            db.endTransaction();
        }
    }


    /**
     * Additives saving to local database
     * @param additives The list of additives to be saved.
     * <p>
     * Additive and AdditiveName has One-To-Many relationship, therefore we need to save them separately.
     */
    @Override
    public void saveAdditives(List<Additive> additives) {
        db.beginTransaction();
        try {
            for (Additive additive : additives) {
                additiveDao.insertOrReplace(additive);
                for (AdditiveName allergenName : additive.getNames()) {
                    additiveNameDao.insertOrReplace(allergenName);
                }
            }

            db.setTransactionSuccessful();
        } catch (Exception e) {
            Log.e(TAG,"saveAdditives",e);
        } finally {
            db.endTransaction();
        }
    }

    /**
     * Countries saving to local database
     * @param countries The list of countries to be saved.
     * <p>
     * Country and CountryName has One-To-Many relationship, therefore we need to save them separately.
     */
    @Override
    public void saveCountries(List<Country> countries) {
        db.beginTransaction();
        try {
            for (Country country : countries) {
                countryDao.insertOrReplace(country);
                for (CountryName countryName : country.getNames()) {
                    countryNameDao.insertOrReplace(countryName);
                }
            }

            db.setTransactionSuccessful();
        } catch (Exception e) {
            Log.e(TAG,"saveCountries",e);
        } finally {
            db.endTransaction();
        }
    }

    /**
     * Categories saving to local database
     * @param categories The list of categories to be saved.
     * <p>
     * Category and CategoryName has One-To-Many relationship, therefore we need to save them separately.
     */
    @Override
    public void saveCategories(List<Category> categories) {
        db.beginTransaction();
        try {
            for (Category category : categories) {
                categoryDao.insertOrReplace(category);
                for (CategoryName categoryName : category.getNames()) {
                    categoryNameDao.insertOrReplace(categoryName);
                }
            }

            db.setTransactionSuccessful();
        } catch (Exception e) {
            Log.e(TAG,"saveCategories",e);
        } finally {
            db.endTransaction();
        }
    }

    /**
     * Delete rows from Ingredient, IngredientName and IngredientsRelation
     * set the autoincrement to 0
     */
    @Override
    public void deleteIngredientCascade(){
        ingredientDao.deleteAll();
        ingredientNameDao.deleteAll();
        ingredientsRelationDao.deleteAll();
        DaoSession daoSession = OFFApplication.getInstance().getDaoSession();
        daoSession.getDatabase().execSQL("update sqlite_sequence set seq=0 where name in ('" + ingredientDao.getTablename() + "', '" + ingredientNameDao.getTablename() + "', '" + ingredientsRelationDao.getTablename() + "')");
    }

    /**
     * TODO to be improved by loading only if required and only in the user language
     * Ingredients saving to local database
     * @param ingredients The list of ingredients to be saved.
     * <p>
     * Ingredient and IngredientName has One-To-Many relationship, therefore we need to save them separately.
     */
    @Override
    public void saveIngredients(List<Ingredient> ingredients) {
        db.beginTransaction();
        try {
            for (Ingredient ingredient : ingredients) {
                ingredientDao.insertOrReplace(ingredient);
                for (IngredientName ingredientName : ingredient.getNames()) {
                    ingredientNameDao.insertOrReplace(ingredientName);
                }
                for (IngredientsRelation ingredientsRelation : ingredient.getParents()) {
                    ingredientsRelationDao.insertOrReplace(ingredientsRelation);
                }
                for (IngredientsRelation ingredientsRelation : ingredient.getChildren()) {
                    ingredientsRelationDao.insertOrReplace(ingredientsRelation);
                }
            }

            db.setTransactionSuccessful();
        } catch (Exception e) {
            Log.e(TAG,"saveIngredients",e);
        } finally {
            db.endTransaction();
        }
    }

    /**
     * Ingredient saving to local database
     * @param ingredient The ingredient to be saved.
     */
    @Override
    public void saveIngredient(Ingredient ingredient) {
        List<Ingredient> ingredients = new ArrayList<>();
        ingredients.add(ingredient);
        saveIngredients(ingredients);
    }

    /**
     * Changes enabled field of allergen and updates it.
     *
     * @param isEnabled   depends on whether user selected or unselected the allergen
     * @param allergenTag is unique Id of allergen
     */
    @Override
    public void setAllergenEnabled(String allergenTag, Boolean isEnabled) {
        Allergen allergen = allergenDao.queryBuilder()
                .where(AllergenDao.Properties.Tag.eq(allergenTag))
                .unique();

        if (allergen != null) {
            allergen.setEnabled(isEnabled);
            allergenDao.update(allergen);
        }
    }


    /**
     * Loads translated label from the local database by unique tag of label and language code
     *
     * @param labelTag     is a unique Id of label
     * @param languageCode is a 2-digit language code
     * @return The translated label
     */
    @Override
    public Single<LabelName> getLabelByTagAndLanguageCode(String labelTag, String languageCode) {
        return Single.fromCallable(() -> {
            LabelName labelName = labelNameDao.queryBuilder()
                    .where(
                            LabelNameDao.Properties.LabelTag.eq(labelTag),
                            LabelNameDao.Properties.LanguageCode.eq(languageCode)
                    ).unique();

            return labelName != null ? labelName : new LabelName();
        });
    }

    /**
     * Loads translated label from the local database by unique tag of label and default language code
     *
     * @param labelTag is a unique Id of label
     * @return The translated label
     */
    @Override
    public Single<LabelName> getLabelByTagAndDefaultLanguageCode(String labelTag) {
        return getLabelByTagAndLanguageCode(labelTag, DEFAULT_LANGUAGE);
    }

    /**
     * Loads translated additive from the local database by unique tag of additive and language code
     *
     * @param additiveTag  is a unique Id of additive
     * @param languageCode is a 2-digit language code
     * @return The translated additive name
     */
    @Override
    public Single<AdditiveName> getAdditiveByTagAndLanguageCode(String additiveTag, String languageCode) {
        return Single.fromCallable(() -> {
            AdditiveName additiveName = additiveNameDao.queryBuilder()
                    .where(
                            AdditiveNameDao.Properties.AdditiveTag.eq(additiveTag),
                            AdditiveNameDao.Properties.LanguageCode.eq(languageCode)
                    ).unique();

            return additiveName != null ? additiveName : new AdditiveName();
        });
    }

    /**
     * Loads translated additive from the local database by unique tag of additive and default language code
     *
     * @param additiveTag is a unique Id of additive
     * @return The translated additive tag
     */
    @Override
    public Single<AdditiveName> getAdditiveByTagAndDefaultLanguageCode(String additiveTag) {
        return getAdditiveByTagAndLanguageCode(additiveTag, DEFAULT_LANGUAGE);
    }

    /**
     * Loads translated country from the local database by unique tag of country and language code
     *
     * @param countryTag   is a unique Id of country
     * @param languageCode is a 2-digit language code
     * @return The translated country name
     */
    @Override
    public Single<CountryName> getCountryByTagAndLanguageCode(String countryTag, String languageCode) {
        return Single.fromCallable(() -> {
            CountryName countryName = countryNameDao.queryBuilder()
                    .where(
                            CountryNameDao.Properties.CountyTag.eq(countryTag),
                            CountryNameDao.Properties.LanguageCode.eq(languageCode)
                    ).unique();

            return countryName != null ? countryName : new CountryName();
        });
    }

    /**
     * Loads translated country from the local database by unique tag of country and default language code
     *
     * @param countryTag is a unique Id of country
     * @return The translated country name
     */
    @Override
    public Single<CountryName> getCountryByTagAndDefaultLanguageCode(String countryTag) {
        return getCountryByTagAndLanguageCode(countryTag, DEFAULT_LANGUAGE);
    }

    /**
     * Loads translated category from the local database by unique tag of category and language code
     *
     * @param categoryTag  is a unique Id of category
     * @param languageCode is a 2-digit language code
     * @return The translated category name
     */
    @Override
    public Single<CategoryName> getCategoryByTagAndLanguageCode(String categoryTag, String languageCode) {
        return Single.fromCallable(() -> {
            CategoryName categoryName = categoryNameDao.queryBuilder()
                    .where(
                            CategoryNameDao.Properties.CategoryTag.eq(categoryTag),
                            CategoryNameDao.Properties.LanguageCode.eq(languageCode)
                    ).unique();

            if (categoryName != null) {
                return categoryName;
            } else {
                CategoryName emptyCategoryName = new CategoryName();
                emptyCategoryName.setName(categoryTag);
                emptyCategoryName.setCategoryTag(categoryTag);
                emptyCategoryName.setIsWikiDataIdPresent(false);
                return emptyCategoryName;
            }
        });
    }

    /**
     * Loads translated category from the local database by unique tag of category and default language code
     *
     * @param categoryTag is a unique Id of category
     * @return The translated category name
     */
    @Override
    public Single<CategoryName> getCategoryByTagAndDefaultLanguageCode(String categoryTag) {
        return getCategoryByTagAndLanguageCode(categoryTag, DEFAULT_LANGUAGE);
    }



    /**
     * Loads list of translated category names from the local database by language code
     *
     * @param languageCode is a 2-digit language code
     * @return The translated list of category name
     */
    @Override
    public Single<List<CategoryName>> getAllCategoriesByLanguageCode(String languageCode) {
        return Single.fromCallable(() -> categoryNameDao.queryBuilder()
                .where(CategoryNameDao.Properties.LanguageCode.eq(languageCode))
                .orderAsc(CategoryNameDao.Properties.Name)
                .list());
    }

    /**
     * Loads list of category names from the local database by default language code
     *
     * @return The list of category name
     */
    @Override
    public Single<List<CategoryName>> getAllCategoriesByDefaultLanguageCode() {
        return getAllCategoriesByLanguageCode(DEFAULT_LANGUAGE);
    }

    /**
     * Loads translated and selected/unselected allergens.
     *
     * @param isEnabled    depends on whether allergen was selected or unselected by user
     * @param languageCode is a 2-digit language code
     * @return The list of allergen names
     */
    @Override
    public Single<List<AllergenName>> getAllergensByEnabledAndLanguageCode(Boolean isEnabled, String languageCode) {
        return Single.fromCallable(() -> {
            List<Allergen> allergens = allergenDao.queryBuilder().where(AllergenDao.Properties.Enabled.eq(isEnabled)).list();
            if (allergens != null) {
                List<AllergenName> allergenNames = new ArrayList<>();
                for (Allergen allergen : allergens) {
                    AllergenName name = allergenNameDao.queryBuilder()
                            .where(
                                    AllergenNameDao.Properties.AllergenTag.eq(allergen.getTag()),
                                    AllergenNameDao.Properties.LanguageCode.eq(languageCode)
                            ).unique();

                    if (name != null) {
                        allergenNames.add(name);
                    }
                }

                return allergenNames;
            }

            return new ArrayList<>();
        });
    }

    /**
     * Loads all translated allergens.
     *
     * @param languageCode is a 2-digit language code
     * @return The list of translated allergen names
     */
    @Override
    public Single<List<AllergenName>> getAllergensByLanguageCode(String languageCode) {
        return Single.fromCallable(() ->
                allergenNameDao.queryBuilder()
                        .where(AllergenNameDao.Properties.LanguageCode.eq(languageCode))
                        .list());
    }

    /**
     * Loads translated allergen from the local database by unique tag of allergen and language code
     *
     * @param allergenTag  is a unique Id of allergen
     * @param languageCode is a 2-digit language code
     * @return The translated allergen name
     */
    @Override
    public Single<AllergenName> getAllergenByTagAndLanguageCode(String allergenTag, String languageCode) {
        return Single.fromCallable(() -> {
            AllergenName allergenName = allergenNameDao.queryBuilder()
                                                       .where(AllergenNameDao.Properties.AllergenTag.eq(allergenTag),
                                                              AllergenNameDao.Properties.LanguageCode.eq(languageCode))
                                                       .unique();

            if (allergenName != null) {
                return allergenName;
            } else {
                AllergenName emptyAllergenName = new AllergenName();
                emptyAllergenName.setName(allergenTag);
                emptyAllergenName.setAllergenTag(allergenTag);
                emptyAllergenName.setIsWikiDataIdPresent(false);
                return emptyAllergenName;
            }
        });
    }

    /**
     * Loads translated allergen from the local database by unique tag of allergen and default language code
     *
     * @param allergenTag is a unique Id of allergen
     * @return The translated allergen name
     */
    @Override
    public Single<AllergenName> getAllergenByTagAndDefaultLanguageCode(String allergenTag) {
        return getAllergenByTagAndLanguageCode(allergenTag, DEFAULT_LANGUAGE);
    }

    /**
     * Checks whether table is empty
     *
     * @param dao checks records count of any table
     */
    private Boolean tableIsEmpty(AbstractDao dao) {
        return dao.count() == 0;
    }


    /**
     * Checks whether table of additives is empty
     */
    @Override
    public Boolean additivesIsEmpty() {
        return tableIsEmpty(additiveDao);
    }


    /**
     * Loads question from the local database by code and lang of question.
     *
     * @param code for the question
     * @param lang is language of the question
     * @return The single question
     */
    @Override
    public Single<Question> getSingleProductQuestion(String code, String lang) {
        return robotoffApi.getProductQuestion(code, lang, 1)
                .map(QuestionsState::getQuestions)
                .map(questions -> {
                    if (!questions.isEmpty()) {
                        return questions.get(0);
                    }
                    return QuestionsState.EMPTY_QUESTION;
                });
    }

    /**
     * Annotate the insight response using insight id and annotation
     * @param insightId is the unique id for the insight
     * @param annotation is the annotation to be used
     * @return The annotated insight response
     */
    @Override
    public Single<InsightAnnotationResponse> annotateInsight(String insightId, int annotation) {
        return robotoffApi.annotateInsight(insightId, annotation);
    }
}
