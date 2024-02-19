package com.tiktok.app.constant;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.chat.ui.contact.ContactsFragment;
import com.chat.ui.message.MessageFragment;
import com.metamedia.ui.MetaMediaFragment;
import com.metamedia.ui.publish.PublishFragment;
import com.person.ui.PersonFragment;
import com.tiktok.app.R;

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
    public static final String KEY_POSITION = "Position";
    public static final int LAUNCH_TIME = 3; // s
    public static final int BACKED_TIME = 3; // s

    public static final int[] GUIDES = new int[]{
            R.mipmap.ic_guide_1,
            R.mipmap.ic_guide_2,
            R.mipmap.ic_guide_3,
    };

    @NonNull
    public static final String[] TITLES = new String[]{
            "首页",
            "朋友",
            "创作",
            "消息",
            "我",
    };

    @NonNull
    public static final Class<? extends Fragment>[] FRAGMENTS = new Class[]{
            MetaMediaFragment.class,
            ContactsFragment.class,
            PublishFragment.class,
            MessageFragment.class,
            PersonFragment.class,
    };
}
