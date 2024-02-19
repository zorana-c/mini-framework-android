package com.framework.demo;

import androidx.annotation.NonNull;
import androidx.lifecycle.Observer;

import com.common.http.Resp;
import com.common.http.RespLiveData;
import com.framework.core.content.UIViewModel;
import com.framework.core.lifecycle.UILiveData;
import com.framework.core.rx.UIObservable;
import com.framework.demo.rx.function.RespFunctions;
import com.framework.demo.rx.function.TestObservables;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;

import java.util.List;
import java.util.Map;

/**
 * @Author create by Zhengzelong on 2023-06-29
 * @Email : 171905184@qq.com
 * @Description :
 */
public class TestViewModel extends UIViewModel {
    @NonNull
    private final UILiveData<String> body = new UILiveData<>();
    @NonNull
    private final RespLiveData<String> test = new RespLiveData<>();
    @NonNull
    private final RespLiveData<List<String>> testList = new RespLiveData<>();
    @NonNull
    private final RespLiveData<Map<String, String>> testMap = new RespLiveData<>();

    public void bodyObserve(@NonNull Observer<String> observer) {
        this.body.observe(this, observer);
    }

    public void testObserve(@NonNull RespLiveData.Observer<String> observer) {
        this.test.observe(this, observer);
    }

    public void testListObserve(@NonNull RespLiveData.Observer<List<String>> observer) {
        this.testList.observe(this, observer);
    }

    public void testMapObserve(@NonNull RespLiveData.Observer<Map<String, String>> observer) {
        this.testMap.observe(this, observer);
    }

    public void setText(@NonNull String text) {
        UIObservable.just(Resp.success(text, null))
                .to(TestObservables.asObservable())
                .subscribeSet(this.test)
//                .map(RespFunctions.toBody("Default"))
//                .to(TestObservables.asObservable())
//                .subscribeSet(this.body)
        ;
    }

    public void insertText(@NonNull String text) {
        UIObservable.just(Resp.success(ImmutableList.of(text)))
                .map(RespFunctions.mergeList(this.testList))
                .to(TestObservables.asObservable())
                .subscribeSet(this.testList);
    }

    public void insertText(@NonNull String key,
                           @NonNull String value) {
        UIObservable.just(Resp.success(ImmutableMap.of(key, value)))
                .map(RespFunctions.mergeMap(this.testMap))
                .to(TestObservables.asObservable())
                .subscribeSet(this.testMap);
    }
}
