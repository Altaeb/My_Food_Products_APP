package maelumat.almuntaj.abdalfattah.altaeb.category.mapper;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import javax.inject.Inject;

import maelumat.almuntaj.abdalfattah.altaeb.category.model.Category;
import maelumat.almuntaj.abdalfattah.altaeb.category.network.CategoryResponse;

/**
 * Created by Abdelali Eramli on 27/12/2017.
 */

public class CategoryMapper {

    @Inject
    public CategoryMapper() {
    }

    public List<Category> fromNetwork(List<CategoryResponse.Tag> tags) {
        List<Category> categories = new ArrayList<>(tags.size());
        for (CategoryResponse.Tag tag : tags) {
            categories.add(new Category(tag.getId(),
                    tag.getName(),
                    tag.getUrl(),
                    tag.getProducts()));
        }
        Collections.sort(categories, (first, second) -> first.getName().compareTo(second.getName()));
        return categories;
    }
}