package maelumat.almuntaj.abdalfattah.altaeb.views.adapters;


import android.app.Activity;
import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import androidx.annotation.NonNull;
import androidx.appcompat.widget.AppCompatImageView;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;


import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;

import java.util.List;

import maelumat.almuntaj.abdalfattah.altaeb.R;
import maelumat.almuntaj.abdalfattah.altaeb.models.YourListedProduct;
import maelumat.almuntaj.abdalfattah.altaeb.network.OpenFoodAPIClient;
import maelumat.almuntaj.abdalfattah.altaeb.utils.CustomTextView;

public class YourListedProductsAdapter extends RecyclerView.Adapter<YourListedProductsAdapter.ViewHolder> {
    Context mContext;
    List<YourListedProduct> products;
    Boolean isLowBatteryMode;

    public YourListedProductsAdapter(Context context, List<YourListedProduct> products,Boolean isLowBatteryMode)
    {
        this.mContext=context;
        this.products=products;
        this.isLowBatteryMode=isLowBatteryMode;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view=LayoutInflater.from(mContext)
                .inflate(R.layout.your_listed_products_item,parent,false);
        ViewHolder viewHolder = new ViewHolder(view);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        holder.imgProgressBar.setVisibility(View.VISIBLE);

        String productName=products.get(position).getProductName();
        String barcode=products.get(position).getBarcode();
        holder.tvTitle.setText(productName);
        holder.tvDetails.setText(products.get(position).getProductDetails());
        holder.tvBarcode.setText(barcode);

        if (!isLowBatteryMode) {
            Picasso.get()
                    .load(products.get(position).getImageUrl())
                    .placeholder(R.drawable.placeholder)
                    .error(R.drawable.ic_no_red_24dp)
                    .fit()
                    .centerCrop()
                    .into(holder.imgProduct, new Callback() {
                        @Override
                        public void onSuccess() {
                            holder.imgProgressBar.setVisibility(View.GONE);
                        }

                        @Override
                        public void onError(Exception ex) {
                            holder.imgProgressBar.setVisibility(View.GONE);
                        }
                    });
        } else {
            holder.imgProduct.setBackground(mContext.getResources().getDrawable(R.drawable.placeholder));
            holder.imgProgressBar.setVisibility(View.INVISIBLE);
        }


        holder.itemView.setOnClickListener(v-> {
            ConnectivityManager cm = (ConnectivityManager) v.getContext().getSystemService(Context.CONNECTIVITY_SERVICE);
            NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
            boolean isConnected = activeNetwork != null && activeNetwork.isConnectedOrConnecting();
            if (isConnected) {
                OpenFoodAPIClient api = new OpenFoodAPIClient((Activity) v.getContext());
                api.getProduct(barcode, (Activity) v.getContext());
            }
        });
    }

    public void remove(YourListedProduct data) {
        int position = products.indexOf(data);
        products.remove(position);
        notifyItemRemoved(position);
    }

    @Override
    public int getItemCount() {
        return products.size();
    }


    public class ViewHolder extends RecyclerView.ViewHolder {
        TextView tvTitle,tvDetails;
        CustomTextView tvBarcode;
        AppCompatImageView imgProduct;
        ProgressBar imgProgressBar;

        public ViewHolder(View itemView) {
            super(itemView);
            tvTitle=itemView.findViewById(R.id.titleYourListedProduct);
            tvDetails=itemView.findViewById(R.id.productDetailsYourListedProduct);
            tvBarcode=itemView.findViewById(R.id.barcodeYourListedProduct);
            imgProduct=itemView.findViewById(R.id.imgProductYourListedProduct);
            imgProgressBar=itemView.findViewById(R.id.imageProgressbarYourListedProduct);
        }
    }
}

