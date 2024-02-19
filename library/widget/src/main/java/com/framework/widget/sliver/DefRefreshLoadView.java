package com.framework.widget.sliver;

import android.content.Context;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.framework.widget.CircleProgressView;
import com.framework.widget.R;

/**
 * @Author create by Zhengzelong on 2023-03-03
 * @Email : 171905184@qq.com
 * @Description :
 */
public class DefRefreshLoadView extends SliverRefreshLoadLayout {
    private static final int[] ATTRS = new int[]{
            android.R.attr.orientation,
    };
    private final TextView mTextView;
    private final CircleProgressView mProgressView;

    public DefRefreshLoadView(@NonNull Context context) {
        this(context, null);
    }

    public DefRefreshLoadView(@NonNull Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public DefRefreshLoadView(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        final int orientation;
        final TypedArray typedArray = context.obtainStyledAttributes(attrs, ATTRS);
        orientation = typedArray.getInt(0, SliverRefreshLayout.VERTICAL);
        typedArray.recycle();

        final int layoutId;
        if (SliverRefreshLayout.VERTICAL == orientation) {
            layoutId = R.layout.ui_refresh_loading_val_layout;
        } else {
            layoutId = R.layout.ui_refresh_loading_hor_layout;
        }
        LayoutInflater.from(context).inflate(layoutId, this);
        this.mTextView =
                this.findViewById(R.id.swipeRefreshLoadingTextView);
        this.mProgressView =
                this.findViewById(R.id.swipeRefreshCircleProgressView);
    }

    // 是否启用刷新提示语
    private boolean mIsRefreshHintEnabled;

    @Override
    public int getRefreshDecorSize(@NonNull SliverRefreshLayout parent) {
        if (this.mIsRefreshHintEnabled) {
            return 0;
        }
        return super.getRefreshDecorSize(parent);
    }

    @Override
    public void onRefreshStateChanged(@NonNull SliverRefreshLayout parent, int refreshState) {
        super.onRefreshStateChanged(parent, refreshState);
        final int locate = parent.getChildLocate(this);

        if (SliverRefreshLayout.SCROLL_LOCATE_HEAD == locate
                && SliverRefreshLayout.REFRESH_STATE_PROGRESS == refreshState) {
            this.mProgressView.setVisibility(View.VISIBLE);
            this.mProgressView.startAnimation();
        } else {
            this.mProgressView.setVisibility(View.GONE);
            this.mProgressView.clearAnimation();
        }
        CharSequence text = null;

        if (parent.canRefreshScroll(locate)) {
            // 已开启刷新/加载功能
            if (this.mIsRefreshHintEnabled) {
                if (SliverRefreshLayout.SCROLL_LOCATE_HEAD == locate) {
                    text = "";
                } else {
                    text = "没有更多了~";
                }
            } else {
                if (SliverRefreshLayout.REFRESH_STATE_NONE == refreshState
                        || SliverRefreshLayout.REFRESH_STATE_DRAG == refreshState) {
                    if (SliverRefreshLayout.SCROLL_LOCATE_HEAD == locate) {
                        text = "下拉刷新";
                    } else {
                        text = "上拉加载";
                    }
                } else if (SliverRefreshLayout.REFRESH_STATE_READY == refreshState) {
                    if (SliverRefreshLayout.SCROLL_LOCATE_HEAD == locate) {
                        text = "释放刷新";
                    } else {
                        text = "释放加载";
                    }
                } else if (SliverRefreshLayout.REFRESH_STATE_PROGRESS == refreshState) {
                    if (SliverRefreshLayout.SCROLL_LOCATE_HEAD == locate) {
                        text = "刷新中";
                    } else {
                        text = "加载中";
                    }
                } else if (SliverRefreshLayout.REFRESH_STATE_COMPLETE == refreshState) {
                    if (SliverRefreshLayout.SCROLL_LOCATE_HEAD == locate) {
                        text = "刷新完成";
                    } else {
                        text = "加载完成";
                    }
                }
            }
        }
        this.mTextView.setText(text);
    }

    public boolean isRefreshHintEnabled() {
        return this.mIsRefreshHintEnabled;
    }

    public void setRefreshHintEnabled(boolean enabled) {
        this.mIsRefreshHintEnabled = enabled;
    }
}
