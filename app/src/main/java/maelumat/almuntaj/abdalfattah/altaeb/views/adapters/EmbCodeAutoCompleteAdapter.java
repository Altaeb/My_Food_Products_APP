package maelumat.almuntaj.abdalfattah.altaeb.views.adapters;

import android.content.Context;
import androidx.annotation.NonNull;
import android.util.Log;
import android.widget.ArrayAdapter;
import android.widget.Filter;
import android.widget.Filterable;

import java.util.ArrayList;

import io.reactivex.SingleObserver;
import io.reactivex.disposables.Disposable;
import maelumat.almuntaj.abdalfattah.altaeb.network.CommonApiManager;
import maelumat.almuntaj.abdalfattah.altaeb.network.OpenFoodAPIService;
import org.apache.commons.lang.StringUtils;

public class EmbCodeAutoCompleteAdapter extends ArrayAdapter<String> implements Filterable {

    private static OpenFoodAPIService client;
    private ArrayList<String> mEMBCodeList;

    public EmbCodeAutoCompleteAdapter(Context context, int textViewResourceId) {
        super(context, textViewResourceId);
        mEMBCodeList = new ArrayList<>();
    }

    @Override
    public int getCount() {
        return mEMBCodeList.size();
    }

    @Override
    public String getItem(int position) {
        if(position<0|| position>=mEMBCodeList.size()){
            return StringUtils.EMPTY;
        }
        return mEMBCodeList.get(position);
    }

    @NonNull
    @Override
    public Filter getFilter() {
        Filter filter;
        filter = new Filter() {
            @Override
            protected FilterResults performFiltering(CharSequence constraint) {
                FilterResults filterResults = new FilterResults();
                if (constraint != null) {
                    // Retrieve the autocomplete results from server.
                    client = CommonApiManager.getInstance().getOpenFoodApiService();
                    client.getEMBCodeSuggestions(constraint.toString())
                            .subscribe(new SingleObserver<ArrayList<String>>() {
                                @Override
                                public void onSubscribe(Disposable d) {

                                }

                                @Override
                                public void onSuccess(ArrayList<String> strings) {
                                    mEMBCodeList.clear();
                                    mEMBCodeList.addAll(strings);
                                }

                                @Override
                                public void onError(Throwable e) {
                                    Log.e(EmbCodeAutoCompleteAdapter.class.getSimpleName(), e.getMessage());
                                }
                            });

                    // Assign the data to the FilterResults
                    filterResults.values = mEMBCodeList;
                    filterResults.count = mEMBCodeList.size();
                }
                return filterResults;
            }

            @Override
            protected void publishResults(CharSequence constraint, FilterResults results) {
                if (results != null && results.count > 0) {
                    notifyDataSetChanged();
                } else {
                    notifyDataSetInvalidated();
                }
            }
        };
        return filter;
    }
}
