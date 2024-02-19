package com.metamedia.constant;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.common.ui.PlaceholderFragment;
import com.mall.ui.MallFragment;
import com.metamedia.R;
import com.metamedia.bean.Video;
import com.metamedia.ui.recommend.RecommendFragment;

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
    public static final String KEY_COMMENT_ID = "commentId";

    @NonNull
    public static final String[] TITLES = new String[]{
            "热点",
            "直播",
            "团购",
            "深圳",
            "经验",
            "关注",
            "商城",
            "推荐",
    };

    @NonNull
    public static final Class<? extends Fragment>[] FRAGMENTS = new Class[]{
            PlaceholderFragment.class,
            PlaceholderFragment.class,
            PlaceholderFragment.class,
            PlaceholderFragment.class,
            PlaceholderFragment.class,
            PlaceholderFragment.class,
            MallFragment.class,
            RecommendFragment.class,
    };

    @NonNull
    public static final Video[] VIDEOS = new Video[]{
            new Video(R.raw.test1),
            new Video(R.raw.test2),
            new Video(R.raw.test3),
            new Video(R.raw.test4),
            new Video(R.raw.test5),
            new Video(R.raw.test6),
            new Video(R.raw.test7),
            new Video(R.raw.test8),
            new Video(R.raw.test9)
    };
}
