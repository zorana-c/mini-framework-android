package com.metamedia.ui.recommend;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.RecyclerView;

import com.common.route.IAppRoute;
import com.framework.core.content.UIActionBarController;
import com.framework.core.content.UIListController;
import com.framework.core.ui.abs.UIListFragment;
import com.framework.core.ui.abs.UIViewHolder;
import com.framework.widget.recycler.pager.PagerLayoutManager;
import com.metamedia.R;
import com.metamedia.bean.Video;
import com.metamedia.constant.Constants;
import com.metamedia.ui.comment.CommentFragment;
import com.metamedia.ui.recommend.adapter.RecommendAdapter;
import com.metamedia.ui.recommend.adapter.RecommendViewHolder;
import com.person.ui.EnemyFragment;

import java.util.Arrays;

/**
 * @Author create by Zhengzelong on 2024-01-25
 * @Email : 171905184@qq.com
 * @Description : 推荐视频
 */
public class RecommendFragment extends UIListFragment<Video> {
    @Override
    public void onUIRefresh(@Nullable Bundle savedInstanceState, int page, int limit) {
        if (savedInstanceState != null) {
            return;
        }
        this.getUIPageController().postDelayed(() -> {
            this.getUIPageController().putAllWithClear(Arrays.asList(Constants.VIDEOS));
        }, 1500);
    }

    @NonNull
    @Override
    public UIViewHolder onCreateViewHolder(@NonNull LayoutInflater inflater,
                                                  @NonNull ViewGroup parent,
                                                  int itemViewType) {
        return new RecommendViewHolder(inflater, parent) {
        };
    }

    @Override
    public void onBindViewHolder(@NonNull UIViewHolder holder, int position) {
    }

    @Override
    public int onUILayoutId(@Nullable Bundle savedInstanceState) {
        return R.layout.fragment_recommend;
    }

    @Override
    public void onUICreated(@Nullable Bundle savedInstanceState) {
        super.onUICreated(savedInstanceState);
        if (savedInstanceState != null) {
            return;
        }
        final UIActionBarController uiActionBarController;
        uiActionBarController = this.getUIActionBarController();
        uiActionBarController.setEnabled(false);

        final UIListController<Video> uiListController;
        uiListController = this.getUIPageController();
        uiListController.setAdapter(new RecommendAdapter(this));

        final PagerLayoutManager layoutManager;
        layoutManager = uiListController.getLayoutManager();
        layoutManager.addOnPageChangeListener(new ComponentListener());
    }

    private void setPersonDetailDrawer(@NonNull Video video) {
        Bundle args;
        args = EnemyFragment.asBundle(String.valueOf(video.nanoId()));
        IAppRoute
                .get()
                .getDrawerController(this)
                .setPersonComponent(EnemyFragment.class, args);

        args = CommentFragment.asBundle(String.valueOf(video.nanoId()));
        IAppRoute
                .get()
                .getDrawerController(this)
                .setCommentComponent(CommentFragment.class, args);
    }

    final class ComponentListener implements PagerLayoutManager.OnPageChangeListener {
        private long nanoId;

        @Override
        public void onPageSelected(@NonNull RecyclerView recyclerView, int position) {
            final Video video = RecommendFragment.this.requireDataBy(position);

            final long nanoId = video.nanoId();
            if (this.nanoId != nanoId) {
                this.nanoId = nanoId;
                RecommendFragment.this.setPersonDetailDrawer(video);
            }
        }
    }
}
