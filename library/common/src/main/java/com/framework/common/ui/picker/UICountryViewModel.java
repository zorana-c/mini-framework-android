package com.framework.common.ui.picker;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.lifecycle.LifecycleOwner;
import androidx.lifecycle.Observer;

import com.framework.common.ui.picker.factory.UICountrySources;
import com.framework.common.ui.picker.factory.UICountrySourcesFactory;
import com.framework.common.ui.picker.bean.UICountryNode;
import com.framework.core.content.UIPageController;
import com.framework.core.content.UIPageControllerOwner;
import com.framework.core.content.UIViewModel;
import com.framework.core.lifecycle.UILiveData;
import com.framework.core.rx.function.UIObservables;

import java.util.List;

import io.reactivex.rxjava3.core.Observable;

/**
 * @Author create by Zhengzelong on 2022/5/31
 * @Email : 171905184@qq.com
 * @Description :
 */
public class UICountryViewModel extends UIViewModel {
    private final Object TARGET = new Object();
    @NonNull
    private final UILiveData<String> mCountryStringLiveData = new UILiveData<>();
    @NonNull
    private final UILiveData<UICountryNode> mLinkedCountryLiveData = new UILiveData<>();
    @Nullable
    private UICountrySources mUICountrySources;

    public void withCountryObserve(@NonNull Observer<String> observer) {
        this.mCountryStringLiveData.observe(this, observer);
    }

    public void withCountryObserve(@NonNull LifecycleOwner owner,
                                   @NonNull Observer<String> observer) {
        this.mCountryStringLiveData.observe(owner, observer);
    }

    public void queryCountryListObserve(@NonNull Observer<UICountryNode> observer) {
        this.mLinkedCountryLiveData.observe(this, observer);
    }

    public void queryCountryListObserve(@NonNull LifecycleOwner owner,
                                        @NonNull Observer<UICountryNode> observer) {
        this.mLinkedCountryLiveData.observe(owner, observer);
    }

    public void queryCountryList(@NonNull UIPageControllerOwner owner) {
        this.queryCountryList(owner.<UIPageController>getUIPageController());
    }

    public void queryCountryList(@NonNull UIPageController uiPageController) {
        Observable.just(TARGET)
                .map(this::mapInner)
                .to(UIObservables.asObservable())
                .subscribeOnIO()
                .observeOnMainThread()
                .subscribeWithLayout(uiPageController)
                .subscribeSet(this.mLinkedCountryLiveData);
    }

    @Nullable
    public String getCountryString() {
        return this.mCountryStringLiveData.getValue();
    }

    public void setCountryString(@Nullable String countryString) {
        this.mCountryStringLiveData.postValue(countryString);
    }

    @NonNull
    private UICountryNode mapInner(@NonNull Object target)
            throws NullPointerException, IndexOutOfBoundsException {
        final UICountryNode upstream = this.mLinkedCountryLiveData.getValue();
        if (upstream != null) {
            return upstream;
        }
        final UICountrySources uiCountrySources = this.getUICountrySources();
        final List<UICountryNode> linkedCountryList = uiCountrySources.queryCountryList();
        if (linkedCountryList.isEmpty()) {
            throw new NullPointerException("ERROR");
        }
        return linkedCountryList.get(0);
    }

    @NonNull
    private UICountrySources getUICountrySources() {
        if (this.mUICountrySources == null) {
            this.mUICountrySources = UICountrySourcesFactory.get();
        }
        return this.mUICountrySources;
    }
}
