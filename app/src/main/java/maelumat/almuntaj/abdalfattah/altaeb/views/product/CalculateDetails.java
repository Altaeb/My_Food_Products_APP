package maelumat.almuntaj.abdalfattah.altaeb.views.product;

import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.appcompat.widget.Toolbar;
import android.view.MenuItem;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import maelumat.almuntaj.abdalfattah.altaeb.R;
import maelumat.almuntaj.abdalfattah.altaeb.models.HeaderNutrimentItem;
import maelumat.almuntaj.abdalfattah.altaeb.models.NutrimentItem;
import maelumat.almuntaj.abdalfattah.altaeb.models.Nutriments;
import maelumat.almuntaj.abdalfattah.altaeb.models.Product;
import maelumat.almuntaj.abdalfattah.altaeb.utils.ProductUtils;
import maelumat.almuntaj.abdalfattah.altaeb.utils.UnitUtils;
import maelumat.almuntaj.abdalfattah.altaeb.utils.Utils;
import maelumat.almuntaj.abdalfattah.altaeb.views.BaseActivity;
import maelumat.almuntaj.abdalfattah.altaeb.views.adapters.CalculateAdapter;

import static androidx.recyclerview.widget.DividerItemDecoration.VERTICAL;
import static maelumat.almuntaj.abdalfattah.altaeb.models.Nutriments.CARBOHYDRATES;
import static maelumat.almuntaj.abdalfattah.altaeb.models.Nutriments.CARBO_MAP;
import static maelumat.almuntaj.abdalfattah.altaeb.models.Nutriments.ENERGY;
import static maelumat.almuntaj.abdalfattah.altaeb.models.Nutriments.FAT;
import static maelumat.almuntaj.abdalfattah.altaeb.models.Nutriments.FAT_MAP;
import static maelumat.almuntaj.abdalfattah.altaeb.models.Nutriments.MINERALS_MAP;
import static maelumat.almuntaj.abdalfattah.altaeb.models.Nutriments.PROTEINS;
import static maelumat.almuntaj.abdalfattah.altaeb.models.Nutriments.PROT_MAP;
import static maelumat.almuntaj.abdalfattah.altaeb.models.Nutriments.VITAMINS_MAP;

public class CalculateDetails extends BaseActivity {

    RecyclerView nutrimentsRecyclerView;
    Nutriments nutriments;
    List<NutrimentItem> nutrimentItems;
    String spinnervalue, weight, pname;
    float value;
    Product p;
    TextView result;
    Toolbar toolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getResources().getBoolean(R.bool.portrait_only)) {
            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        }
        setContentView(R.layout.calculate_details);
        setTitle(getString(R.string.app_name_long));
        toolbar = findViewById(R.id.toolbar1);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        result = findViewById(R.id.result_text_view);
        Intent i = getIntent();
        p = (Product) i.getSerializableExtra("sampleObject");
        spinnervalue = i.getStringExtra("spinnervalue");
        weight = i.getStringExtra("weight");
        value = Float.valueOf(weight);
        nutriments = p.getNutriments();
        nutrimentItems = new ArrayList<>();
        nutrimentsRecyclerView = findViewById(R.id.nutriments_recycler_view_calc);
        result.setText(getString(R.string.display_fact, weight + " " + spinnervalue));
        nutrimentsRecyclerView.setHasFixedSize(true);

        // use a linear layout manager
        LinearLayoutManager mLayoutManager = new LinearLayoutManager(getApplicationContext());
        nutrimentsRecyclerView.setLayoutManager(mLayoutManager);

        nutrimentsRecyclerView.setNestedScrollingEnabled(false);

        // use VERTICAL divider
        DividerItemDecoration dividerItemDecoration = new DividerItemDecoration(nutrimentsRecyclerView.getContext(), VERTICAL);
        nutrimentsRecyclerView.addItemDecoration(dividerItemDecoration);

        // Header hack
        nutrimentItems.add(new NutrimentItem(ProductUtils.isPerServingInLiter(p)));

        // Energy
        Nutriments.Nutriment energy = nutriments.get(ENERGY);
        if (energy != null) {
            nutrimentItems.add(new NutrimentItem(getString(R.string.nutrition_energy_short_name),
                                                 calculateCalories(value, spinnervalue),
                                                 Utils.getEnergy(energy.getForServingInUnits()),
                                                 "kcal",
                                                 nutriments.getModifier(ENERGY)));
        }

        // Fat
        Nutriments.Nutriment fat = nutriments.get(FAT);
        if (fat != null) {
            String modifier = nutriments.getModifier(FAT);
            nutrimentItems.add(new HeaderNutrimentItem(getString(R.string.nutrition_fat),
                                                       fat.getForAnyValue(value, spinnervalue),
                                                       fat.getForServingInUnits(),
                                                       fat.getUnit(),
                                                       modifier == null ? "" : modifier));

            nutrimentItems.addAll(getNutrimentItems(nutriments, FAT_MAP));
        }

        // Carbohydrates
        Nutriments.Nutriment carbohydrates = nutriments.get(CARBOHYDRATES);
        if (carbohydrates != null) {
            String modifier = nutriments.getModifier(CARBOHYDRATES);
            nutrimentItems.add(new HeaderNutrimentItem(getString(R.string.nutrition_carbohydrate),
                                                       carbohydrates.getForAnyValue(value, spinnervalue),
                                                       carbohydrates.getForServingInUnits(),
                                                       carbohydrates.getUnit(),
                                                       modifier == null ? "" : modifier));

            nutrimentItems.addAll(getNutrimentItems(nutriments, CARBO_MAP));
        }

        // fiber
        nutrimentItems.addAll(getNutrimentItems(nutriments, Collections.singletonMap(Nutriments.FIBER, R.string.nutrition_fiber)));

        // Proteins
        Nutriments.Nutriment proteins = nutriments.get(PROTEINS);
        if (proteins != null) {
            String modifier = nutriments.getModifier(PROTEINS);
            nutrimentItems.add(
                    new HeaderNutrimentItem(getString(R.string.nutrition_proteins),
                                            proteins.getForAnyValue(value, spinnervalue),
                                            proteins.getForServingInUnits(),
                                            proteins.getUnit(),
                                            modifier == null ? "" : modifier));

            nutrimentItems.addAll(getNutrimentItems(nutriments, PROT_MAP));
        }

        // salt and alcohol
        Map<String, Integer> map = new HashMap<>();
        map.put(Nutriments.SALT, R.string.nutrition_salt);
        map.put(Nutriments.SODIUM, R.string.nutrition_sodium);
        map.put(Nutriments.ALCOHOL, R.string.nutrition_alcohol);
        nutrimentItems.addAll(getNutrimentItems(nutriments, map));

        // Vitamins
        if (nutriments.hasVitamins()) {
            nutrimentItems.add(new HeaderNutrimentItem(getString(R.string.nutrition_vitamins)));

            nutrimentItems.addAll(getNutrimentItems(nutriments, VITAMINS_MAP));
        }

        // Minerals
        if (nutriments.hasMinerals()) {
            nutrimentItems.add(new HeaderNutrimentItem(getString(R.string.nutrition_minerals)));

            nutrimentItems.addAll(getNutrimentItems(nutriments, MINERALS_MAP));
        }
        RecyclerView.Adapter adapter = new CalculateAdapter(nutrimentItems);
        nutrimentsRecyclerView.setAdapter(adapter);

    }


    private List<NutrimentItem> getNutrimentItems(Nutriments nutriments, Map<String, Integer> nutrimentMap) {
        List<NutrimentItem> items = new ArrayList<>();
        for (Map.Entry<String, Integer> entry : nutrimentMap.entrySet()) {
            Nutriments.Nutriment nutriment = nutriments.get(entry.getKey());
            if (nutriment != null) {
                items.add(new NutrimentItem(getString(entry.getValue()),
                                            nutriment.getForAnyValue(value, spinnervalue),
                                            nutriment.getForServingInUnits(),
                                            nutriment.getUnit(),
                                            nutriments.getModifier(entry.getKey())));
            }
        }

        return items;
    }

    private String calculateCalories(float weight, String unit) {
        float caloriePer100g = Float.valueOf(Utils.getEnergy(p.getNutriments().get(Nutriments.ENERGY).getFor100gInUnits()));
        float weightInG= UnitUtils.convertToGrams(weight,unit);
        return Float.toString(((caloriePer100g / 100) * weightInG));
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            // Respond to the action bar's Up/Home button
            case android.R.id.home:
                finish();
                return true;

            default:
                return super.onOptionsItemSelected(item);
        }
    }
}

