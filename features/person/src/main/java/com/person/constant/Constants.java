package com.person.constant;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.common.ui.PlaceholderFragment;
import com.person.ui.works.WorksFragment;

/**
 * @Author create by Zhengzelong on 2024-01-25
 * @Email : 171905184@qq.com
 * @Description :
 */
public final class Constants {
    private Constants() {
        throw new IllegalStateException("No instances!");
    }

    @NonNull
    public static final String KEY_PERSON_ID = "personId";

    @NonNull
    public static final String[] TITLES = new String[]{
            "作品",
            "私密",
            "收藏",
            "喜欢",
    };

    @NonNull
    public static final Class<? extends Fragment>[] FRAGMENTS = new Class[]{
            WorksFragment.class,
            PlaceholderFragment.class,
            PlaceholderFragment.class,
            PlaceholderFragment.class,
    };
}
