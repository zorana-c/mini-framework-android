package com.framework.core.cache;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.SharedPreferences;
import android.text.TextUtils;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.framework.core.UIFramework;
import com.framework.core.bean.UIModelInterface;
import com.google.gson.Gson;

/**
 * @Author create by Zhengzelong on 2022/2/24
 * @Email : 171905184@qq.com
 * @Description :
 */
public class UISharedCaches {
    static volatile UISharedCaches sUISharedCaches;

    @NonNull
    public static UISharedCaches get() {
        if (sUISharedCaches != null) {
            return sUISharedCaches;
        }
        synchronized (UISharedCaches.class) {
            if (sUISharedCaches == null) {
                sUISharedCaches = new UISharedCaches();
            }
        }
        return sUISharedCaches;
    }

    @NonNull
    public static <T extends UIModelInterface> String key(@NonNull Class<T> tClass) {
        final StringBuilder builder;
        builder = new StringBuilder("<?Key ");
        builder.append(tClass.getName());
        return builder.toString();
    }

    private final SharedPreferences mSharedPreferences;

    public UISharedCaches() {
        final Context context = UIFramework.getApplicationContext();
        this.mSharedPreferences =
                context.getSharedPreferences(context.getPackageName(), Context.MODE_PRIVATE);
    }

    public final int getInt(@NonNull String key) {
        return this.getInt(key, 0);
    }

    public int getInt(@NonNull String key, int defValue) {
        return this.mSharedPreferences.getInt(key, defValue);
    }

    public final long getLong(@NonNull String key) {
        return this.getLong(key, 0L);
    }

    public long getLong(@NonNull String key, long defValue) {
        return this.mSharedPreferences.getLong(key, defValue);
    }

    public final float getFloat(@NonNull String key) {
        return this.getFloat(key, 0F);
    }

    public float getFloat(@NonNull String key, float defValue) {
        return this.mSharedPreferences.getFloat(key, defValue);
    }

    public final boolean getBoolean(@NonNull String key) {
        return this.getBoolean(key, false);
    }

    public boolean getBoolean(@NonNull String key, boolean defValue) {
        return this.mSharedPreferences.getBoolean(key, defValue);
    }

    @Nullable
    public final String getString(@NonNull String key) {
        return this.getString(key, null);
    }

    @Nullable
    public String getString(@NonNull String key, @Nullable String defValue) {
        return this.mSharedPreferences.getString(key, defValue);
    }

    @Nullable
    public final <T extends UIModelInterface> T getModel(@NonNull Class<T> tClass) {
        return this.getModel(tClass, null);
    }

    @Nullable
    public <T extends UIModelInterface> T getModel(@NonNull Class<T> tClass, @Nullable T defValue) {
        return this.getModel(key(tClass), tClass, defValue);
    }

    @Nullable
    public final <T extends UIModelInterface> T getModel(@NonNull String key,
                                                         @NonNull Class<T> tClass) {
        return this.getModel(key, tClass, null);
    }

    @Nullable
    public <T extends UIModelInterface> T getModel(@NonNull String key,
                                                   @NonNull Class<T> tClass, @Nullable T defValue) {
        final String objString = this.getString(key);
        if (TextUtils.isEmpty(objString)) {
            return defValue;
        }
        return new Gson().fromJson(objString, tClass);
    }

    @NonNull
    @SuppressLint("CommitPrefEdits")
    public UISharedCaches put(@NonNull String key, @NonNull Object object) {
        final SharedPreferences.Editor editor = this.mSharedPreferences.edit();
        if (object instanceof Integer) {
            editor.putInt(key, (int) object);
        } else if (object instanceof Long) {
            editor.putLong(key, (long) object);
        } else if (object instanceof Float) {
            editor.putFloat(key, (float) object);
        } else if (object instanceof Boolean) {
            editor.putBoolean(key, (boolean) object);
        } else if (object instanceof String) {
            editor.putString(key, String.valueOf(object));
        } else {
            editor.putString(key, new Gson().toJson(object));
        }
        editor.apply();
        return this;
    }

    @NonNull
    public final <T extends UIModelInterface> UISharedCaches putModel(@NonNull T model) {
        return this.putModel(key(model.getClass()), model);
    }

    @NonNull
    public <T extends UIModelInterface> UISharedCaches putModel(@NonNull String key, @NonNull T model) {
        return this.put(key, new Gson().toJson(model));
    }

    @NonNull
    public final <T extends UIModelInterface> UISharedCaches remove(@NonNull T model) {
        return this.remove(model.getClass());
    }

    @NonNull
    public <T extends UIModelInterface> UISharedCaches remove(@NonNull Class<T> tClass) {
        return this.remove(key(tClass));
    }

    @NonNull
    public UISharedCaches remove(@NonNull String key) {
        this.mSharedPreferences.edit().remove(key).apply();
        return this;
    }

    @NonNull
    public UISharedCaches clear() {
        this.mSharedPreferences.edit().clear().apply();
        return this;
    }
}
