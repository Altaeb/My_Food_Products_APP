package maelumat.almuntaj.abdalfattah.altaeb.fragments;

import android.os.Bundle;
import androidx.annotation.CallSuper;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import io.reactivex.disposables.CompositeDisposable;
import maelumat.almuntaj.abdalfattah.altaeb.views.viewmodel.ViewModel;

public abstract class MvvmFragment<T extends ViewModel, U> extends Fragment {

    private U component;
    private CompositeDisposable compositeDisposable = new CompositeDisposable();

    @CallSuper
    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        inject();
    }

    @CallSuper
    @Override
    public void onStart() {
        super.onStart();
        bindViewModel();
        bindProperties(compositeDisposable);
    }

    protected void bindViewModel() {
        getViewModel().bind();
    }

    @CallSuper
    @Override
    public void onStop() {
        super.onStop();
        compositeDisposable.clear();
        getViewModel().unbind();
    }

    @NonNull
    public U component() {
        if (component == null) {
            component = createComponent();
        }
        return component;
    }

    protected abstract T getViewModel();

    @NonNull
    protected abstract U createComponent();

    protected abstract void inject();

    protected abstract void bindProperties(CompositeDisposable compositeDisposable);
}
