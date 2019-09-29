package maelumat.almuntaj.abdalfattah.altaeb.views.category.adapter;

import androidx.recyclerview.widget.RecyclerView;

import maelumat.almuntaj.abdalfattah.altaeb.databinding.CategoryRecyclerItemBinding;
import maelumat.almuntaj.abdalfattah.altaeb.models.CategoryName;
import maelumat.almuntaj.abdalfattah.altaeb.utils.SearchType;
import maelumat.almuntaj.abdalfattah.altaeb.views.ProductBrowsingListActivity;

/**
 * Created by Abdelali Eramli on 27/12/2017.
 */

public class CategoryViewHolder extends RecyclerView.ViewHolder {
    private final CategoryRecyclerItemBinding binding;

    public CategoryViewHolder(CategoryRecyclerItemBinding binding) {
        super(binding.getRoot());
        this.binding = binding;
    }

    public void bind(CategoryName category) {
        binding.setCategory(category);
        binding.getRoot().setOnClickListener(v ->
                ProductBrowsingListActivity.startActivity(v.getContext(),
                        category.getCategoryTag(),
                        category.getName(),
                        SearchType.CATEGORY));
        binding.executePendingBindings();
    }
}
