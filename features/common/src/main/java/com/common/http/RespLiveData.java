package com.common.http;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.framework.core.lifecycle.UILiveData;

/**
 * @Author create by Zhengzelong on 2023-05-31
 * @Email : 171905184@qq.com
 * @Description :
 */
public class RespLiveData<T> extends UILiveData<Resp<T>> {

    public interface Observer<T>
            extends androidx.lifecycle.Observer<Resp<T>> {
        // no-op
    }

    public RespLiveData() {
        super();
    }

    public RespLiveData(@NonNull T body) {
        this(Resp.success(body));
    }

    public RespLiveData(@Nullable Resp<T> value) {
        super(value);
    }

    @Nullable
    public T getBody() {
        final Resp<T> r = this.getValue();
        if (r == null) {
            return null;
        }
        return r.body();
    }

    @NonNull
    public final T requireBody() {
        final T r = this.getBody();
        if (r == null) {
            throw new NullPointerException("ERROR");
        }
        return r;
    }
}
