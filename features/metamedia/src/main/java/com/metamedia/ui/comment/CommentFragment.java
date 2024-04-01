package com.metamedia.ui.comment;

import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;

import com.common.route.IAppRoute;
import com.framework.core.compat.UILog;
import com.framework.core.compat.UIRes;
import com.framework.core.content.UIActionBarController;
import com.framework.core.content.UIDecorController;
import com.framework.core.content.UIListController;
import com.framework.core.content.UIPageController;
import com.framework.core.content.UIThemeEnforcement;
import com.framework.core.rx.view.RxView;
import com.framework.core.ui.abs.UIListFragment;
import com.framework.core.ui.abs.UIViewHolder;
import com.framework.core.util.UITestUtils;
import com.framework.core.widget.UIImageView;
import com.framework.widget.compat.UIViewCompat;
import com.metamedia.R;
import com.metamedia.constant.Constants;

/**
 * @Author create by Zhengzelong on 2024-01-16
 * @Email : 171905184@qq.com
 * @Description :
 */
public class CommentFragment extends UIListFragment<String>
        implements IAppRoute.IDrawerCallback {
    @NonNull
    public static Bundle asBundle(@NonNull String commentId) {
        final Bundle args = new Bundle();
        args.putString(Constants.KEY_COMMENT_ID, commentId);
        return args;
    }

    @Override
    public void onUIRefresh(@Nullable Bundle savedInstanceState, int page, int limit) {
        final Bundle args = this.requireArguments();
        final String commentId = args.getString(Constants.KEY_COMMENT_ID);
        UILog.e("CommentId: " + commentId);

        this.getUIPageController().postDelayed(() -> {
            this.getUIPageController().putAll(UITestUtils.obtain(page, limit));
        }, 1500);
    }

    @NonNull
    @Override
    public View onCreateItemView(@NonNull LayoutInflater inflater,
                                 @NonNull ViewGroup parent,
                                 int itemViewType) {
        return inflater.inflate(R.layout.item_comment_layout, parent, false);
    }

    @Override
    public void onBindViewHolder(@NonNull UIViewHolder<String> holder,
                                 int position) {
        holder.<UIImageView>requireViewById(R.id.picImageView)
                .setImageUrl("https://t14.baidu.com/it/u=2451480495," +
                        "981697655&fm=224&app=112&size=w931&n=0&f=JPEG" +
                        "&fmt=auto?sec=1683306000&t=7ec3e1d891d6cfd8e76654c3c72fe339");
        holder.<UIImageView>requireViewById(R.id.contentImageView)
                .setImageUrl("https://t14.baidu.com/it/u=2451480495," +
                        "981697655&fm=224&app=112&size=w931&n=0&f=JPEG" +
                        "&fmt=auto?sec=1683306000&t=7ec3e1d891d6cfd8e76654c3c72fe339");
    }

    @Override
    public long getItemId(int position) {
        return super.getItemId(position);
    }

    @NonNull
    @Override
    public View onCreateChildItemView(@NonNull LayoutInflater inflater,
                                      @NonNull ViewGroup parent,
                                      int itemViewType) {
        return inflater.inflate(R.layout.item_comment_reply_layout, parent, false);
    }

    @Override
    public void onBindChildViewHolder(@NonNull UIViewHolder<String> holder,
                                      int groupPosition,
                                      int childPosition) {
        holder.<UIImageView>requireViewById(R.id.picImageView)
                .setImageUrl("https://t14.baidu.com/it/u=2451480495," +
                        "981697655&fm=224&app=112&size=w931&n=0&f=JPEG" +
                        "&fmt=auto?sec=1683306000&t=7ec3e1d891d6cfd8e76654c3c72fe339");
        holder.<UIImageView>requireViewById(R.id.contentImageView)
                .setImageUrl("https://t14.baidu.com/it/u=2451480495," +
                        "981697655&fm=224&app=112&size=w931&n=0&f=JPEG" +
                        "&fmt=auto?sec=1683306000&t=7ec3e1d891d6cfd8e76654c3c72fe339");
    }

    @Override
    public long getChildItemId(int groupPosition, int childPosition) {
        return super.getChildItemId(groupPosition, childPosition);
    }

    @Override
    public int getChildItemCount(int groupPosition) {
        return 3;
    }

    @Override
    public int onUILayoutId(@Nullable Bundle savedInstanceState) {
        return R.layout.fragment_comment_layout;
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @Override
    public void onUICreated(@Nullable Bundle savedInstanceState) {
        super.onUICreated(savedInstanceState);
        final UIActionBarController uiActionBarController;
        uiActionBarController = this.getUIActionBarController();
        uiActionBarController.setStatusBarEnabled(false);
        uiActionBarController.setBackTintColor(Color.BLACK);
        uiActionBarController.setTitleText("Comment");
        uiActionBarController.setTitleTextColor(Color.BLACK);
        uiActionBarController.setBackgroundColor(Color.WHITE);
        uiActionBarController.setBackClickListener(widget -> {
            IAppRoute
                    .get()
                    .getDrawerController(this)
                    .closeDrawerComponent(this);
        });

        final int padding = UIRes.dip2px(6);
        final UIListController<?> uiListController;
        uiListController = this.getUIPageController();
        uiListController.setBackgroundColor(Color.WHITE);
        uiListController.setPaddingTop(padding);
        uiListController.setPaddingBottom(padding);
        uiListController.setClipToPadding(false);
        uiListController.setGroupExpanded(true);

        final int radius = UIRes.dip2px(10);
        UIThemeEnforcement.with(this)
                .setTopRadius(radius)
                .applyToPageBackground();

        final RxView rxView;
        rxView = RxView.of(this);
        rxView.click(this::onClick, R.id.reply);

        final int height;
        height = (int) (UIViewCompat.getScreenHeight(this.requireContext()) * 0.7f);
        final View contentView = this.requireView();
        final ViewGroup.LayoutParams layoutParams;
        layoutParams = contentView.getLayoutParams();
        layoutParams.height = height;
        contentView.setLayoutParams(layoutParams);
    }

    private void onClick(@NonNull View view) {
        this.getUINavigatorController()
                .showDialogFragment(CommentEditingDialog.class);
    }

    @Override
    public void onPause() {
        super.onPause();
        final UIPageController uiPageController;
        uiPageController = this.getUIPageController();
        uiPageController.removeCallbacksAndMessages();
    }

    @Override
    public void onNewArguments(@NonNull Bundle arguments) {
        // Resets arguments.
        this.setArguments(arguments);
        // Resets refreshed.
        final UIDecorController uiDecorController;
        uiDecorController = this.getUIPageController();
        uiDecorController.layoutLoading();
        uiDecorController.notifyDataSetRefresh();
        // After see onUIRefresh.
    }
}
