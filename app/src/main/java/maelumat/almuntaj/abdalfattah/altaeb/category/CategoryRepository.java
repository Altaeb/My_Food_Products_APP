package maelumat.almuntaj.abdalfattah.altaeb.category;

import java.util.List;
import java.util.concurrent.atomic.AtomicReference;

import android.util.Log;
import io.reactivex.Single;
import io.reactivex.schedulers.Schedulers;
import maelumat.almuntaj.abdalfattah.altaeb.category.mapper.CategoryMapper;
import maelumat.almuntaj.abdalfattah.altaeb.category.model.Category;
import maelumat.almuntaj.abdalfattah.altaeb.category.network.CategoryNetworkService;

public class CategoryRepository {
    private final CategoryNetworkService networkService;
    private final CategoryMapper mapper;
    private final AtomicReference<List<Category>> memoryCache;

    public CategoryRepository(CategoryNetworkService networkService, CategoryMapper mapper) {
        this.networkService = networkService;
        this.mapper = mapper;
        memoryCache = new AtomicReference<>();
    }

    public Single<List<Category>> retrieveAll() {
        if (memoryCache.get() != null) {
            return Single.just(memoryCache.get());
        }
        return networkService.getCategories()
                .map(categoryResponse -> mapper.fromNetwork(categoryResponse.getTags()))
                .doOnSuccess(memoryCache::set)
                .doOnError(throwable-> Log.w(CategoryRepository.class.getSimpleName(),"Can't get categories",throwable))
                .subscribeOn(Schedulers.io());
    }
}
