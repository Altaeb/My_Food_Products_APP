package maelumat.almuntaj.abdalfattah.altaeb.views.adapters;

import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import maelumat.almuntaj.abdalfattah.altaeb.R;
import maelumat.almuntaj.abdalfattah.altaeb.models.NutrimentItem;
import org.apache.commons.lang.StringUtils;

import java.util.List;

/**
 * @author herau
 */
public class NutrimentsRecyclerViewAdapter extends RecyclerView.Adapter {
    private static final int TYPE_HEADER = 0;
    private static final int TYPE_ITEM = 1;
    private final List<NutrimentItem> nutrimentItems;

    public NutrimentsRecyclerViewAdapter(List<NutrimentItem> nutrimentItems) {
        super();
        this.nutrimentItems = nutrimentItems;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        boolean isViewTypeHeader = viewType == TYPE_HEADER;

        int layoutResourceId = isViewTypeHeader ? R.layout.nutriment_item_list_header : R.layout.nutriment_item_list;
        View v = LayoutInflater.from(parent.getContext()).inflate(layoutResourceId, parent, false);

        return isViewTypeHeader ? new NutrimentHeaderViewHolder(v) : new NutrimentViewHolder(v);
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        if (holder instanceof NutrimentHeaderViewHolder) {
            NutrimentItem item = nutrimentItems.get(position);
            NutrimentHeaderViewHolder nutrimentViewHolder = (NutrimentHeaderViewHolder) holder;
            nutrimentViewHolder.vNutrimentValue.setText(item.isHeaderPerVolume() ? R.string.for_100ml : R.string.for_100g);
        }
        if (!(holder instanceof NutrimentViewHolder)) {
            return;
        }

        NutrimentItem item = nutrimentItems.get(position);

        NutrimentViewHolder nutrimentViewHolder = (NutrimentViewHolder) holder;
        nutrimentViewHolder.fillNutrimentValue(item);
        nutrimentViewHolder.fillServingValue(item);
    }

    @Override
    public int getItemViewType(int position) {
        return isPositionHeader(position) ? TYPE_HEADER : TYPE_ITEM;
    }

    private boolean isPositionHeader(int position) {
        return position == 0;
    }

    @Override
    public int getItemCount() {
        return nutrimentItems.size();
    }

    static class NutrimentViewHolder extends RecyclerView.ViewHolder {
        private TextView vNutrimentName;
        private TextView vNutrimentValue;
        private TextView vNutrimentServingValue;

        public NutrimentViewHolder(View v) {
            super(v);
            vNutrimentName = v.findViewById(R.id.nutriment_name);
            vNutrimentValue = v.findViewById(R.id.nutriment_value);
            vNutrimentServingValue = v.findViewById(R.id.nutriment_serving_value);
        }

        void fillNutrimentValue(NutrimentItem item) {
            vNutrimentName.setText(item.getTitle());
            vNutrimentValue.append(item.getModifier());
            vNutrimentValue.append(item.getValue());
            vNutrimentValue.append(" ");
            vNutrimentValue.append(item.getUnit());
        }

        void fillServingValue(NutrimentItem item) {
            final CharSequence servingValue = item.getServingValue();
            if (StringUtils.isBlank(servingValue.toString())) {
                vNutrimentServingValue.setText(StringUtils.EMPTY);
            } else {
                vNutrimentServingValue.append(item.getModifier());
                vNutrimentServingValue.append(servingValue);
                vNutrimentServingValue.append(" ");
                vNutrimentServingValue.append(item.getUnit());
            }
        }
    }

    class NutrimentHeaderViewHolder extends RecyclerView.ViewHolder {
        TextView vNutrimentValue;

        public NutrimentHeaderViewHolder(View itemView) {
            super(itemView);
            vNutrimentValue = itemView.findViewById(R.id.nutriment_value);
        }
    }
}
