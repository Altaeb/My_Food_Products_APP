package maelumat.almuntaj.abdalfattah.altaeb.views.product.summary;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.SpannableStringBuilder;
import android.text.method.LinkMovementMethod;
import android.text.style.ClickableSpan;
import android.text.style.StyleSpan;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.*;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.browser.customtabs.CustomTabsIntent;
import androidx.cardview.widget.CardView;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.widget.NestedScrollView;
import androidx.fragment.app.FragmentActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import butterknife.BindView;
import butterknife.OnClick;
import com.afollestad.materialdialogs.MaterialDialog;
import com.squareup.picasso.Picasso;
import maelumat.almuntaj.abdalfattah.altaeb.BuildConfig;
import maelumat.almuntaj.abdalfattah.altaeb.R;
import maelumat.almuntaj.abdalfattah.altaeb.fragments.AdditiveFragmentHelper;
import maelumat.almuntaj.abdalfattah.altaeb.fragments.BaseFragment;
import maelumat.almuntaj.abdalfattah.altaeb.images.PhotoReceiver;
import maelumat.almuntaj.abdalfattah.altaeb.images.ProductImage;
import maelumat.almuntaj.abdalfattah.altaeb.jobs.PhotoReceiverHandler;
import maelumat.almuntaj.abdalfattah.altaeb.models.*;
import maelumat.almuntaj.abdalfattah.altaeb.network.OpenFoodAPIClient;
import maelumat.almuntaj.abdalfattah.altaeb.network.WikidataApiClient;
import maelumat.almuntaj.abdalfattah.altaeb.utils.*;
import maelumat.almuntaj.abdalfattah.altaeb.views.*;
import maelumat.almuntaj.abdalfattah.altaeb.views.adapters.DialogAddToListAdapter;
import maelumat.almuntaj.abdalfattah.altaeb.views.adapters.NutrientLevelListAdapter;
import maelumat.almuntaj.abdalfattah.altaeb.views.customtabs.CustomTabActivityHelper;
import maelumat.almuntaj.abdalfattah.altaeb.views.customtabs.CustomTabsHelper;
import maelumat.almuntaj.abdalfattah.altaeb.views.customtabs.WebViewFallback;
import maelumat.almuntaj.abdalfattah.altaeb.views.product.ProductActivity;
import org.apache.commons.lang.StringUtils;

import java.io.File;
import java.net.URI;
import java.util.ArrayList;
import java.util.List;

import static android.app.Activity.RESULT_OK;
import static android.text.Spanned.SPAN_EXCLUSIVE_EXCLUSIVE;
import static maelumat.almuntaj.abdalfattah.altaeb.models.ProductImageField.FRONT;
import static maelumat.almuntaj.abdalfattah.altaeb.models.ProductImageField.OTHER;
import static maelumat.almuntaj.abdalfattah.altaeb.utils.ProductInfoState.EMPTY;
import static maelumat.almuntaj.abdalfattah.altaeb.utils.ProductInfoState.LOADING;
import static maelumat.almuntaj.abdalfattah.altaeb.utils.Utils.bold;
import static org.apache.commons.lang.StringUtils.isNotBlank;

public class SummaryProductFragment extends BaseFragment implements CustomTabActivityHelper.ConnectionCallback, ISummaryProductPresenter.View, ImageUploadListener, PhotoReceiver {
    private static final int EDIT_PRODUCT_AFTER_LOGIN = 1;
    private static final int EDIT_PRODUCT_NUTRITION_AFTER_LOGIN = 3;
    private static final int EDIT_REQUEST_CODE = 2;
    private PhotoReceiverHandler photoReceiverHandler;
    @BindView(R.id.product_allergen_alert_layout)
    LinearLayout productAllergenAlertLayout;
    @BindView(R.id.product_allergen_alert_text)
    TextView productAllergenAlert;
    @BindView(R.id.textNameProduct)
    TextView nameProduct;
    @BindView(R.id.textQuantityProduct)
    TextView quantityProduct;
    @BindView(R.id.textBrandProduct)
    TextView brandProduct;
    @BindView(R.id.textEmbCode)
    TextView embCode;
    @BindView(R.id.textCategoryProduct)
    TextView categoryProduct;
    @BindView(R.id.textLabelProduct)
    TextView labelProduct;
    @BindView(R.id.front_picture_layout)
    LinearLayout frontPictureLayout;
    @BindView(R.id.imageViewFront)
    ImageView mImageFront;
    @BindView(R.id.addPhotoLabel)
    TextView addPhotoLabel;
    @BindView(R.id.uploadingImageProgress)
    ProgressBar uploadingImageProgress;
    @BindView(R.id.uploadingImageProgressText)
    TextView uploadingImageProgressText;
    @BindView(R.id.buttonMorePictures)
    Button addMorePicture;
    @BindView(R.id.add_nutriscore_prompt)
    Button addNutriScorePrompt;
    @BindView(R.id.imageGrade)
    ImageView nutriscoreImage;
    @BindView(R.id.nova_group)
    ImageView novaGroup;
    @BindView(R.id.co2_icon)
    ImageView co2Icon;
    @BindView(R.id.scores_layout)
    ConstraintLayout scoresLayout;
    @BindView(R.id.listNutrientLevels)
    RecyclerView rv;
    @BindView(R.id.textNutrientTxt)
    TextView textNutrientTxt;
    @BindView(R.id.cvNutritionLights)
    CardView nutritionLightsCardView;
    @BindView(R.id.textAdditiveProduct)
    TextView additiveProduct;
    @BindView(R.id.action_compare_button)
    ImageButton compareProductButton;
    @BindView(R.id.scrollView)
    public NestedScrollView scrollView;
    @BindView(R.id.product_question_layout)
    RelativeLayout productQuestionLayout;
    @BindView(R.id.product_question_text)
    TextView productQuestionText;
    @BindView(R.id.product_question_dismiss)
    ImageView productQuestionDismiss;
    private State state;
    private Product product;
    private OpenFoodAPIClient api;
    private WikidataApiClient apiClientForWikiData;
    private String mUrlImage;
    private String barcode;
    private boolean sendOther = false;
    private CustomTabsIntent customTabsIntent;
    private CustomTabActivityHelper customTabActivityHelper;
    private Uri nutritionScoreUri;
    private TagDao mTagDao;
    private ISummaryProductPresenter.Actions presenter;
    //boolean to determine if image should be loaded or not
    private boolean isLowBatteryMode = false;
    //boolean to determine if nutrient prompt should be shown
    private boolean showNutrientPrompt = false;
    //boolean to determine if category prompt should be shown
    private boolean showCategoryPrompt = false;
    private Question productQuestion = null;
    private boolean hasCategoryInsightQuestion = false;

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        customTabActivityHelper = new CustomTabActivityHelper();
        customTabActivityHelper.setConnectionCallback(this);
        customTabsIntent = CustomTabsHelper.getCustomTabsIntent(getContext(), customTabActivityHelper.getSession());

        presenter = new SummaryProductPresenter(product, this);
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        api = new OpenFoodAPIClient(getActivity());
        apiClientForWikiData = new WikidataApiClient();
        return createView(inflater, container, R.layout.fragment_summary_product);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        //done here for android 4 compatibility.
        //a better solution could be to use https://developer.android.com/jetpack/androidx/releases/ but weird issue with it..
        addNutriScorePrompt.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_add_box_blue_18dp, 0, 0, 0);
        addMorePicture.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_add_a_photo_blue_18dp, 0, 0, 0);
        photoReceiverHandler = new PhotoReceiverHandler(this);
        state = getStateFromActivityIntent();
        refreshView(state);
    }

    @Override
    public void refreshView(State state) {
        //no state-> we can't display anything.
        if (state == null) {
            return;
        }
        super.refreshView(state);
        this.state = state;
        product = state.getProduct();
        presenter = new SummaryProductPresenter(product, this);
        categoryProduct.setText(bold(getString(R.string.txtCategories)));
        labelProduct.setText(bold(getString(R.string.txtLabels)));

        //refresh visibility of UI components
        labelProduct.setVisibility(View.VISIBLE);
        brandProduct.setVisibility(View.VISIBLE);
        quantityProduct.setVisibility(View.VISIBLE);
        embCode.setVisibility(View.VISIBLE);
        nameProduct.setVisibility(View.VISIBLE);

        // If Battery Level is low and the user has checked the Disable Image in Preferences , then set isLowBatteryMode to true
        if (Utils.isDisableImageLoad(getContext()) && Utils.getBatteryLevel(getContext())) {
            isLowBatteryMode = true;
        }

        //checks the product states_tags to determine which prompt to be shown
        refreshNutriscorePrompt();

        presenter.loadAllergens(null);
        presenter.loadCategories();
        presenter.loadLabels();
        presenter.loadProductQuestion();
        additiveProduct.setText(bold(getString(R.string.txtAdditives)));
        presenter.loadAdditives();

        mTagDao = Utils.getAppDaoSession(getActivity()).getTagDao();
        barcode = product.getCode();
        String langCode = LocaleHelper.getLanguage(getContext());

        final String imageUrl = product.getImageUrl(langCode);
        if (isNotBlank(imageUrl)) {
            addPhotoLabel.setVisibility(View.GONE);

            // Load Image if isLowBatteryMode is false
            if (!isLowBatteryMode) {
                Picasso.get()
                    .load(imageUrl)
                    .into(mImageFront);
            } else {
                mImageFront.setVisibility(View.GONE);
            }

            mUrlImage = imageUrl;
        }

        //TODO use OpenFoodApiService to fetch product by packaging, brands, categories etc

        if (product.getProductName(langCode) != null) {
            nameProduct.setText(product.getProductName(langCode));
        } else {
            nameProduct.setVisibility(View.GONE);
        }

        if (isNotBlank(product.getQuantity())) {
            quantityProduct.setText(product.getQuantity());
        } else {
            quantityProduct.setVisibility(View.GONE);
        }

        if (isNotBlank(product.getBrands())) {
            brandProduct.setClickable(true);
            brandProduct.setMovementMethod(LinkMovementMethod.getInstance());
            brandProduct.setText("");

            String[] brands = product.getBrands().split(",");
            for (int i = 0; i < brands.length; i++) {
                if (i > 0) {
                    brandProduct.append(", ");
                }
                brandProduct.append(Utils.getClickableText(brands[i].trim(), "", SearchType.BRAND, getActivity(), customTabsIntent));
            }
        } else {
            brandProduct.setVisibility(View.GONE);
        }

        if (product.getEmbTags() != null && !product.getEmbTags().toString().trim().equals("[]")) {
            embCode.setMovementMethod(LinkMovementMethod.getInstance());
            embCode.setText(bold(getString(R.string.txtEMB)));
            embCode.append(" ");

            String[] embTags = product.getEmbTags().toString().replace("[", "").replace("]", "").split(", ");
            for (int i = 0; i < embTags.length; i++) {
                if (i > 0) {
                    embCode.append(", ");
                }
                String embTag = embTags[i];
                embCode.append(Utils.getClickableText(getEmbCode(embTag).trim(), getEmbUrl(embTag), SearchType.EMB, getActivity(), customTabsIntent));
            }
        } else {
            embCode.setVisibility(View.GONE);
        }

        // if the device does not have a camera, hide the button
        try {
            if (!Utils.isHardwareCameraInstalled(getContext())) {
                addMorePicture.setVisibility(View.GONE);
            }
        } catch (NullPointerException e) {
            if (BuildConfig.DEBUG) {
                Log.i(getClass().getSimpleName(), e.toString());
            }
        }

        if (BuildConfig.FLAVOR.equals("off")) {
            scoresLayout.setVisibility(View.VISIBLE);
            List<NutrientLevelItem> levelItem = new ArrayList<>();
            Nutriments nutriments = product.getNutriments();

            NutrientLevels nutrientLevels = product.getNutrientLevels();
            NutrimentLevel fat = null;
            NutrimentLevel saturatedFat = null;
            NutrimentLevel sugars = null;
            NutrimentLevel salt = null;
            if (nutrientLevels != null) {
                fat = nutrientLevels.getFat();
                saturatedFat = nutrientLevels.getSaturatedFat();
                sugars = nutrientLevels.getSugars();
                salt = nutrientLevels.getSalt();
            }

            final boolean inVolume = ProductUtils.isPerServingInLiter(product);
            textNutrientTxt.setText(inVolume ? R.string.txtNutrientLevel100ml : R.string.txtNutrientLevel100g);

            if (!(fat == null && salt == null && saturatedFat == null && sugars == null)) {
                // prefetch the uri
                // currently only available in french translations
                nutritionScoreUri = Uri.parse(getString(R.string.nutriscore_uri));
                customTabActivityHelper.mayLaunchUrl(nutritionScoreUri, null, null);
                Context context = this.getContext();

                if (nutriments != null) {
                    nutritionLightsCardView.setVisibility(View.VISIBLE);
                    Nutriments.Nutriment fatNutriment = nutriments.get(Nutriments.FAT);
                    if (fat != null && fatNutriment != null) {
                        String fatNutrimentLevel = fat.getLocalize(context);
                        levelItem.add(new NutrientLevelItem(getString(R.string.txtFat),
                            fatNutriment.getDisplayStringFor100g(),
                            fatNutrimentLevel,
                            fat.getImageLevel()));
                    }

                    Nutriments.Nutriment saturatedFatNutriment = nutriments.get(Nutriments.SATURATED_FAT);
                    if (saturatedFat != null && saturatedFatNutriment != null) {
                        String saturatedFatLocalize = saturatedFat.getLocalize(context);
                        levelItem.add(new NutrientLevelItem(getString(R.string.txtSaturatedFat), saturatedFatNutriment.getDisplayStringFor100g(),
                            saturatedFatLocalize,
                            saturatedFat.getImageLevel()));
                    }

                    Nutriments.Nutriment sugarsNutriment = nutriments.get(Nutriments.SUGARS);
                    if (sugars != null && sugarsNutriment != null) {
                        String sugarsLocalize = sugars.getLocalize(context);
                        levelItem.add(new NutrientLevelItem(getString(R.string.txtSugars),
                            sugarsNutriment.getDisplayStringFor100g(),
                            sugarsLocalize,
                            sugars.getImageLevel()));
                    }

                    Nutriments.Nutriment saltNutriment = nutriments.get(Nutriments.SALT);
                    if (salt != null && saltNutriment != null) {
                        String saltLocalize = salt.getLocalize(context);
                        levelItem.add(new NutrientLevelItem(getString(R.string.txtSalt),
                            saltNutriment.getDisplayStringFor100g(),
                            saltLocalize,
                            salt.getImageLevel()));
                    }
                }
            } else {
                nutritionLightsCardView.setVisibility(View.GONE);
            }

            rv.setLayoutManager(new LinearLayoutManager(getContext()));
            rv.setAdapter(new NutrientLevelListAdapter(getContext(), levelItem));

            refreshNutriscore();
            refreshNovaIcon();
            refreshCo2Icon();
            refreshScoresLayout();
        } else {
            scoresLayout.setVisibility(View.GONE);
        }
        //to be sure that top of the product view is visible at start
        nameProduct.requestFocus();
        nameProduct.clearFocus();
    }

    private void refreshScoresLayout() {
        if (novaGroup.getVisibility() == View.GONE &&
            co2Icon.getVisibility() == View.GONE &&
            nutriscoreImage.getVisibility() == View.GONE &&
            addNutriScorePrompt.getVisibility() == View.GONE) {
            scoresLayout.setVisibility(View.GONE);
        } else {
            scoresLayout.setVisibility(View.VISIBLE);
        }
    }

    private void refreshNutriscore() {
        int nutritionGradeResource = Utils.getImageGrade(product);
        if (nutritionGradeResource != Utils.NO_DRAWABLE_RESOURCE) {
            nutriscoreImage.setVisibility(View.VISIBLE);
            nutriscoreImage.setImageResource(nutritionGradeResource);
            nutriscoreImage.setOnClickListener(view1 -> {
                CustomTabsIntent customTabsIntent = CustomTabsHelper.getCustomTabsIntent(getContext(), customTabActivityHelper.getSession());
                CustomTabActivityHelper.openCustomTab(SummaryProductFragment.this.getActivity(), customTabsIntent, nutritionScoreUri, new WebViewFallback());
            });
        } else {
            nutriscoreImage.setVisibility(View.GONE);
        }
    }

    private void refreshNovaIcon() {
        if (product.getNovaGroups() != null) {
            novaGroup.setVisibility(View.VISIBLE);
            novaGroup.setImageResource(Utils.getNovaGroupDrawable(product.getNovaGroups()));
            novaGroup.setOnClickListener(view1 -> {
                Uri uri = Uri.parse(getString(R.string.url_nova_groups));
                CustomTabsIntent customTabsIntent = CustomTabsHelper.getCustomTabsIntent(getContext(), customTabActivityHelper.getSession());
                CustomTabActivityHelper.openCustomTab(SummaryProductFragment.this.getActivity(), customTabsIntent, uri, new WebViewFallback());
            });
        } else {
            novaGroup.setVisibility(View.GONE);
            novaGroup.setImageResource(0);
        }
    }

    private void refreshCo2Icon() {
        int environmentImpactResource = Utils.getImageEnvironmentImpact(product);
        if (environmentImpactResource != Utils.NO_DRAWABLE_RESOURCE) {
            co2Icon.setVisibility(View.VISIBLE);
            co2Icon.setImageResource(environmentImpactResource);
        } else {
            co2Icon.setVisibility(View.GONE);
        }
    }

    private void refreshNutriscorePrompt() {
        //checks the product states_tags to determine which prompt to be shown
        List<String> statesTags = product.getStatesTags();
        showCategoryPrompt = (statesTags.contains("en:categories-to-be-completed") &&
            !hasCategoryInsightQuestion);
        Log.e("showCat", String.valueOf(showCategoryPrompt));

        if (product.getNoNutritionData() != null && product.getNoNutritionData().equals("on")) {
            showNutrientPrompt = false;
        } else {
            if (statesTags.contains("en:nutrition-facts-to-be-completed")) {
                showNutrientPrompt = true;
            }
        }

        if (showNutrientPrompt || showCategoryPrompt) {
            addNutriScorePrompt.setVisibility(View.VISIBLE);
            if (showNutrientPrompt && showCategoryPrompt) {
                addNutriScorePrompt.setText(getString(R.string.add_nutrient_category_prompt_text));
            } else if (showNutrientPrompt) {
                addNutriScorePrompt.setText(getString(R.string.add_nutrient_prompt_text));
            } else if (showCategoryPrompt) {
                addNutriScorePrompt.setText(getString(R.string.add_category_prompt_text));
            }
        } else {
            addNutriScorePrompt.setVisibility(View.GONE);
        }
    }

    @Override
    public void showAdditives(List<AdditiveName> additives) {
        AdditiveFragmentHelper.showAdditives(additives, additiveProduct, apiClientForWikiData, this);
    }

    @Override
    public void showAdditivesState(String state) {
        getActivity().runOnUiThread(() -> {
            switch (state) {
                case LOADING: {
                    additiveProduct.append(getString(R.string.txtLoading));
                    additiveProduct.setVisibility(View.VISIBLE);
                    break;
                }
                case EMPTY: {
                    additiveProduct.setVisibility(View.GONE);
                    break;
                }
            }
        });
    }

    @Override
    public void showAllergens(List<AllergenName> allergens) {
        final AllergenHelper.Data data = AllergenHelper.computeUserAllergen(product, allergens);
        if (data.isEmpty()) {
            return;
        }

        if (data.isIncomplete()) {
            productAllergenAlert.setText(R.string.product_incomplete_message);
            productAllergenAlertLayout.setVisibility(View.VISIBLE);
            return;
        }

        String text = String.format("%s\n", getResources().getString(R.string.product_allergen_prompt)) +
            StringUtils.join(data.getAllergens(), ", ");
        productAllergenAlert.setText(text);
        productAllergenAlertLayout.setVisibility(View.VISIBLE);
    }

    @Override
    public void showCategories(List<CategoryName> categories) {
        categoryProduct.setText(bold(getString(R.string.txtCategories)));
        categoryProduct.setMovementMethod(LinkMovementMethod.getInstance());
        categoryProduct.append(" ");
        categoryProduct.setClickable(true);
        categoryProduct.setMovementMethod(LinkMovementMethod.getInstance());

        if (categories.isEmpty()) {
            categoryProduct.setVisibility(View.GONE);
        } else {
            categoryProduct.setVisibility(View.VISIBLE);
            // Add all the categories to text view and link them to wikidata is possible
            for (int i = 0, lastIndex = categories.size() - 1; i <= lastIndex; i++) {
                CategoryName category = categories.get(i);
                CharSequence categoryName = getCategoriesTag(category);
                // Add category name to text view
                categoryProduct.append(categoryName);
                // Add a comma if not the last item
                if (i != lastIndex) {
                    categoryProduct.append(", ");
                }
            }
        }
    }

    @Override
    public void showProductQuestion(Question question) {
        if (Utils.isUserLoggedIn(getContext()) && question != null && !question.isEmpty()) {
            productQuestion = question;
            productQuestionText.setText(String.format("%s\n%s",
                question.getQuestion(), question.getValue()));
            productQuestionLayout.setVisibility(View.VISIBLE);
            hasCategoryInsightQuestion = question.getInsightType().equals("category");
        } else {
            productQuestionLayout.setVisibility(View.GONE);
            productQuestion = null;
        }
        refreshNutriscorePrompt();
        refreshScoresLayout();
    }

    @OnClick(R.id.product_question_layout)
    public void onProductQuestionClick() {
        if (productQuestion == null && !Utils.isUserLoggedIn(getContext())) {
            return;
        }
        new QuestionDialog(getActivity())
            .setBackgroundColor(R.color.colorPrimaryDark)
            .setQuestion(productQuestion.getQuestion())
            .setValue(productQuestion.getValue())
            .setOnReviewClickListener(new QuestionActionListeners() {
                @Override
                public void onPositiveFeedback(QuestionDialog dialog) {
                    //init POST request
                    sendProductInsights(productQuestion.getInsightId(), 1);
                    dialog.dismiss();
                }

                @Override
                public void onNegativeFeedback(QuestionDialog dialog) {
                    sendProductInsights(productQuestion.getInsightId(), 0);
                    dialog.dismiss();
                }

                @Override
                public void onAmbiguityFeedback(QuestionDialog dialog) {
                    sendProductInsights(productQuestion.getInsightId(), -1);
                    dialog.dismiss();
                }

                @Override
                public void onCancelListener(DialogInterface dialog) {
                    dialog.dismiss();
                }
            })
            .show();
    }

    public void sendProductInsights(String insightId, int annotation) {
        Log.d("SummaryProductFragment",
            String.format("Annotation %d received for insight %s", annotation, insightId));
        presenter.annotateInsight(insightId, annotation);
        productQuestionLayout.setVisibility(View.GONE);
        productQuestion = null;
    }

    public void showAnnotatedInsightToast(InsightAnnotationResponse response) {
        if (response.getStatus().equals("updated") && getActivity() != null) {
            Toast toast = Toast.makeText(getActivity(), R.string.product_question_submit_message, Toast.LENGTH_LONG);
            toast.setGravity(Gravity.CENTER, 0, 500);
            toast.setDuration(Toast.LENGTH_SHORT);
            toast.show();
        }
    }

    @OnClick(R.id.product_question_dismiss)
    public void productQuestionDismiss() {
        productQuestionLayout.setVisibility(View.GONE);
    }

    @Override
    public void showLabels(List<LabelName> labels) {
        labelProduct.setText(bold(getString(R.string.txtLabels)));
        labelProduct.setClickable(true);
        labelProduct.setMovementMethod(LinkMovementMethod.getInstance());
        labelProduct.append(" ");

        for (int i = 0; i < labels.size() - 1; i++) {
            labelProduct.append(getLabelTag(labels.get(i)));
            labelProduct.append(", ");
        }

        labelProduct.append(getLabelTag(labels.get(labels.size() - 1)));
    }

    @Override
    public void showCategoriesState(String state) {
        getActivity().runOnUiThread(() -> {
            switch (state) {
                case LOADING: {
                    categoryProduct.append(getString(R.string.txtLoading));
                    break;
                }
                case EMPTY: {
                    categoryProduct.setVisibility(View.GONE);
                    break;
                }
                default:
                    break;
            }
        });
    }

    @Override
    public void showLabelsState(String state) {
        getActivity().runOnUiThread(() -> {
            switch (state) {
                case LOADING: {
                    labelProduct.append(getString(R.string.txtLoading));
                    break;
                }
                case EMPTY: {
                    labelProduct.setVisibility(View.GONE);
                    break;
                }
                default:
                    break;
            }
        });
    }

    private String getEmbUrl(String embTag) {
        Tag tag = mTagDao.queryBuilder().where(TagDao.Properties.Id.eq(embTag)).unique();
        if (tag != null) {
            return tag.getName();
        }
        return null;
    }

    private String getEmbCode(String embTag) {
        Tag tag = mTagDao.queryBuilder().where(TagDao.Properties.Id.eq(embTag)).unique();
        if (tag != null) {
            return tag.getName();
        }
        return embTag;
    }

    private CharSequence getCategoriesTag(CategoryName category) {
        SpannableStringBuilder spannableStringBuilder = new SpannableStringBuilder();
        ClickableSpan clickableSpan = new ClickableSpan() {
            @Override
            public void onClick(View view) {
                if (category.getIsWikiDataIdPresent()) {
                    apiClientForWikiData.doSomeThing(category.getWikiDataId(), (value, result) -> {
                        if (value) {
                            FragmentActivity activity = getActivity();

                            if (activity != null && !activity.isFinishing()) {
                                BottomScreenCommon.showBottomScreen(result, category,
                                    activity.getSupportFragmentManager());
                            }
                        } else {
                            ProductBrowsingListActivity.startActivity(getContext(),
                                category.getCategoryTag(),
                                category.getName(),
                                SearchType.CATEGORY);
                        }
                    });
                } else {
                    ProductBrowsingListActivity.startActivity(getContext(),
                        category.getCategoryTag(),
                        category.getName(),
                        SearchType.CATEGORY);
                }
            }
        };
        spannableStringBuilder.append(category.getName());
        spannableStringBuilder.setSpan(clickableSpan, 0, spannableStringBuilder.length(), SPAN_EXCLUSIVE_EXCLUSIVE);
        if (!category.isNotNull()) {
            StyleSpan iss = new StyleSpan(android.graphics.Typeface.ITALIC); //Span to make text italic
            spannableStringBuilder.setSpan(iss, 0, spannableStringBuilder.length(), SPAN_EXCLUSIVE_EXCLUSIVE);
        }
        return spannableStringBuilder;
    }

    private CharSequence getLabelTag(LabelName label) {
        SpannableStringBuilder spannableStringBuilder = new SpannableStringBuilder();
        ClickableSpan clickableSpan = new ClickableSpan() {
            @Override
            public void onClick(View view) {

                if (label.getIsWikiDataIdPresent()) {
                    apiClientForWikiData.doSomeThing(label.getWikiDataId(), (value, result) -> {
                        if (value) {
                            FragmentActivity activity = getActivity();
                            if (activity != null && !activity.isFinishing()) {
                                BottomScreenCommon.showBottomScreen(result, label,
                                    activity.getSupportFragmentManager());
                            }
                        } else {
                            ProductBrowsingListActivity.startActivity(getContext(),
                                label.getLabelTag(),
                                label.getName(),
                                SearchType.LABEL);
                        }
                    });
                } else {
                    ProductBrowsingListActivity.startActivity(getContext(),
                        label.getLabelTag(),
                        label.getName(),
                        SearchType.LABEL);
                }
            }
        };

        spannableStringBuilder.append(label.getName());

        spannableStringBuilder.setSpan(clickableSpan, 0, spannableStringBuilder.length(), SPAN_EXCLUSIVE_EXCLUSIVE);
        return spannableStringBuilder;
    }

    @OnClick(R.id.add_nutriscore_prompt)
    public void onAddNutriScorePromptClick() {
        if (BuildConfig.FLAVOR.equals("off")) {
            if (isUserNotLoggedIn()) {
                startLoginToEditAnd(EDIT_PRODUCT_NUTRITION_AFTER_LOGIN);
            } else {
                editProductNutriscore();
            }
        }
    }

    private void editProductNutriscore() {
        Intent intent = new Intent(getActivity(), AddProductActivity.class);
        intent.putExtra(AddProductActivity.KEY_EDIT_PRODUCT, product);
        //adds the information about the prompt when navigating the user to the edit the product
        intent.putExtra(AddProductActivity.MODIFY_CATEGORY_PROMPT, showCategoryPrompt);
        intent.putExtra(AddProductActivity.MODIFY_NUTRITION_PROMPT, showNutrientPrompt);
        startActivity(intent);
    }

    @OnClick(R.id.action_compare_button)
    public void onCompareProductButtonClick() {
        Intent intent = new Intent(getActivity(), ProductComparisonActivity.class);
        intent.putExtra("product_found", true);
        ArrayList<Product> productsToCompare = new ArrayList<>();
        productsToCompare.add(product);
        intent.putExtra("products_to_compare", productsToCompare);
        startActivity(intent);
    }

    @OnClick(R.id.action_share_button)
    public void onShareProductButtonClick() {
        String shareUrl = " " + getString(R.string.website_product) + product.getCode();
        Intent sharingIntent = new Intent();
        sharingIntent.setAction(Intent.ACTION_SEND);
        sharingIntent.setType(OpenFoodAPIClient.TEXT_PLAIN);
        String shareBody = getResources().getString(R.string.msg_share) + shareUrl;
        String shareSub = "\n\n";
        sharingIntent.putExtra(Intent.EXTRA_SUBJECT, shareSub);
        sharingIntent.putExtra(Intent.EXTRA_TEXT, shareBody);
        startActivity(Intent.createChooser(sharingIntent, "Share using"));
    }

    @OnClick(R.id.action_edit_button)
    public void onEditProductButtonClick() {
        if (isUserNotLoggedIn()) {
            startLoginToEditAnd(EDIT_PRODUCT_AFTER_LOGIN);
        } else {
            editProduct();
        }
    }

    private void editProduct() {
        Intent intent = new Intent(getActivity(), AddProductActivity.class);
        intent.putExtra(AddProductActivity.KEY_EDIT_PRODUCT, product);
        startActivityForResult(intent, EDIT_REQUEST_CODE);
    }

    @OnClick(R.id.action_add_to_list_button)
    public void onBookmarkProductButtonClick() {
        Activity activity = getActivity();

        String productBarcode = product.getCode();
        String productName = product.getProductName();
        String imageUrl = product.getImageSmallUrl(LocaleHelper.getLanguage(getContext()));
        String productDetails = YourListedProducts.getProductBrandsQuantityDetails(product);

        MaterialDialog.Builder addToListBuilder = new MaterialDialog.Builder(activity)
            .title(R.string.add_to_product_lists)
            .customView(R.layout.dialog_add_to_list, true);
        MaterialDialog addToListDialog = addToListBuilder.build();
        addToListDialog.show();
        View addToListView = addToListDialog.getCustomView();
        if (addToListView != null) {
            ProductListsDao productListsDao = ProductListsActivity.getProducListsDaoWithDefaultList(this.getContext());
            List<ProductLists> productLists = productListsDao.loadAll();

            RecyclerView addToListRecyclerView =
                addToListView.findViewById(R.id.rv_dialogAddToList);
            DialogAddToListAdapter addToListAdapter =
                new DialogAddToListAdapter(activity, productLists, productBarcode, productName, productDetails, imageUrl);
            addToListRecyclerView.setLayoutManager(new LinearLayoutManager(activity));
            addToListRecyclerView.setAdapter(addToListAdapter);
            TextView tvAddToList = addToListView.findViewById(R.id.tvAddToNewList);
            tvAddToList.setOnClickListener(view -> {
                Intent intent = new Intent(activity, ProductListsActivity.class);
                intent.putExtra("product", product);
                activity.startActivity(intent);
            });
        }
    }

    @Override
    public void onCustomTabsConnected() {
        nutriscoreImage.setClickable(true);
    }

    @Override
    public void onCustomTabsDisconnected() {
        nutriscoreImage.setClickable(false);
    }

    @OnClick(R.id.buttonMorePictures)
    public void takeMorePicture() {
        sendOther = true;
        doChooseOrTakePhotos(getString(R.string.take_more_pictures));
    }

    @OnClick(R.id.imageViewFront)
    public void openFullScreen(View v) {
        if (mUrlImage != null) {
            FullScreenActivityOpener.openForUrl(this, product, FRONT, mUrlImage, mImageFront);
        } else {
            // take a picture
            newFrontImage();
        }
    }

    void newFrontImage() {
        // add front image.
        sendOther = false;
        doChooseOrTakePhotos(getString(R.string.set_img_front));
    }

    private void loadPhoto(File photoFile) {
        ProductImage image = new ProductImage(barcode, FRONT, photoFile);
        image.setFilePath(photoFile.getAbsolutePath());
        api.postImg(getContext(), image, this);
        addPhotoLabel.setVisibility(View.GONE);
        mUrlImage = photoFile.getAbsolutePath();

        Picasso.get()
            .load(photoFile)
            .fit()
            .into(mImageFront);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        photoReceiverHandler.onActivityResult(this, requestCode, resultCode, data);
        boolean shouldRefresh = (requestCode == EDIT_REQUEST_CODE && resultCode == RESULT_OK && data.getBooleanExtra(AddProductActivity.UPLOADED_TO_SERVER, false));
        if (ProductImageManagementActivity.isImageModified(requestCode, resultCode)) {
            shouldRefresh = true;
        }
        if (shouldRefresh) {
            if (getActivity() instanceof ProductActivity) {
                ((ProductActivity) getActivity()).onRefresh();
            }
        }
        if (resultCode == RESULT_OK) {
            if (requestCode == EDIT_PRODUCT_AFTER_LOGIN && isUserLoggedIn()) {
                editProduct();
            }
            if (requestCode == EDIT_PRODUCT_NUTRITION_AFTER_LOGIN && isUserLoggedIn()) {
                editProductNutriscore();
            }
        }
    }

    public void onPhotoReturned(File newPhotoFile) {
        URI resultUri = newPhotoFile.toURI();
        //the booleans are checked to determine if the picture uploaded was due to a prompt click
        //the pictures are uploaded with the correct path
        if (!sendOther) {
            loadPhoto(new File(resultUri.getPath()));
        } else {
            ProductImage image = new ProductImage(barcode, OTHER, newPhotoFile);
            image.setFilePath(resultUri.getPath());
            showOtherImageProgress();
            api.postImg(getContext(), image, this);
        }
    }

    @Override
    protected void doOnPhotosPermissionGranted() {
        if (sendOther) {
            takeMorePicture();
        } else {
            newFrontImage();
        }
    }

    @Override
    public void onDestroyView() {
        presenter.dispose();
        super.onDestroyView();
    }

    public void showOtherImageProgress() {
        uploadingImageProgress.setVisibility(View.VISIBLE);
        uploadingImageProgressText.setVisibility(View.VISIBLE);
        uploadingImageProgressText.setText(R.string.toastSending);
    }

    @Override
    public void onSuccess() {
        uploadingImageProgress.setVisibility(View.GONE);
        uploadingImageProgressText.setText(R.string.image_uploaded_successfully);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }

    @Override
    public void onFailure(String message) {
        uploadingImageProgress.setVisibility(View.GONE);
        uploadingImageProgressText.setVisibility(View.GONE);
        Context context = getContext();
        if (context == null) {
            context = OFFApplication.getInstance();
        }
        Toast.makeText(context, message, Toast.LENGTH_SHORT).show();
    }
}
