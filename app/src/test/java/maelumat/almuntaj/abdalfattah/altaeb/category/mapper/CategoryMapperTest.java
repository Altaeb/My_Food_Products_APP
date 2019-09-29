package maelumat.almuntaj.abdalfattah.altaeb.category.mapper;

import com.fasterxml.jackson.databind.ObjectMapper;

import junit.framework.Assert;

import org.junit.Test;

import java.io.IOException;
import java.util.List;

import maelumat.almuntaj.abdalfattah.altaeb.category.model.Category;
import maelumat.almuntaj.abdalfattah.altaeb.category.network.CategoryResponse;
import maelumat.almuntaj.abdalfattah.altaeb.utils.FileHelperForTests;

/**
 * Created by Abdelali Eramli on 01/01/2018.
 */

public class CategoryMapperTest {

    @Test
    public void fromNetwork_FullResponse_CategoryList() throws IOException {
        ObjectMapper mapper = new ObjectMapper();
        CategoryResponse response = mapper.readValue(FileHelperForTests
                .readTextFileFromResources("mock_categories.json", this.getClass().getClassLoader()), CategoryResponse.class);
        List<Category> categories = new CategoryMapper().fromNetwork(response.getTags());
        Assert.assertEquals(response.getTags().size(), categories.size());
    }
}
