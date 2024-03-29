package com.tiktok.app.ui;

import android.os.Bundle;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.lifecycle.DefaultLifecycleObserver;
import androidx.lifecycle.Lifecycle;
import androidx.lifecycle.LifecycleOwner;
import androidx.viewpager.widget.ViewPager;

import com.common.route.IAppRoute;
import com.framework.core.content.UIPageController;
import com.framework.core.content.UIPageControllerOwner;
import com.framework.widget.drawer.DrawerLayout;
import com.framework.widget.drawer.NestedFrameLayout;
import com.tiktok.app.R;

/**
 * @Author create by Zhengzelong on 2024-01-23
 * @Email : 171905184@qq.com
 * @Description :
 */
public class MainController implements
        IAppRoute.IDrawerController, DrawerLayout.DrawerListener {
    @NonNull
    public static MainController with(@NonNull UIPageControllerOwner owner) {
        return new MainController(owner.getUIPageController());
    }

    @NonNull
    private final FragmentManager fragmentManager;
    @NonNull
    private final DrawerLayout mainContainer;
    @NonNull
    private final NestedFrameLayout pageContainer;

    private int currentPosition = 0;

    protected MainController(@NonNull UIPageController uiPageController) {
        this.fragmentManager = uiPageController.getChildFragmentManager();
        this.pageContainer = uiPageController.requireViewById(R.id.pageContainer);
        this.mainContainer = uiPageController.requireViewById(R.id.mainContainer);
        this.mainContainer.addDrawerListener(this);
        this.mainContainer.setDraggedEdgeSize(0);
        this.mainContainer.setDrawerNestedLockMode(
                DrawerLayout.EDGE_TOP, DrawerLayout.LOCK_LOCKED_OPENED);
        this.mainContainer.setDrawerNestedLockMode(
                DrawerLayout.EDGE_BOTTOM, DrawerLayout.LOCK_LOCKED_OPENED);

        final ViewPager pageContent;
        pageContent = uiPageController.requireViewById(R.id.pageContent);
        pageContent.addOnPageChangeListener(new ComponentListener(pageContent));
    }

    @Override
    public void onDrawerSlide(@NonNull DrawerLayout parent,
                              @NonNull View drawerView, float screen) {
        float translationX = 0;
        float translationY = 0;
        if (parent.checkDrawerViewEdgeMode(drawerView, DrawerLayout.EDGE_LEFT)) {
            translationX = drawerView.getWidth() * screen;
        }
        if (parent.checkDrawerViewEdgeMode(drawerView, DrawerLayout.EDGE_RIGHT)) {
            translationX = -drawerView.getWidth() * screen;
            // 对方详情
            if (this.currentPosition == 0) {
                translationX = -drawerView.getWidth() / 2.f * screen;
            }
        }
        if (parent.checkDrawerViewEdgeMode(drawerView, DrawerLayout.EDGE_TOP)) {
            translationY = drawerView.getHeight() / 2.f * screen;
        }
        if (parent.checkDrawerViewEdgeMode(drawerView, DrawerLayout.EDGE_BOTTOM)) {
            translationY = -drawerView.getHeight() / 2.f * screen;
        }
        this.pageContainer.setTranslationX(translationX);
        this.pageContainer.setTranslationY(translationY);
    }

    @Override
    public void onDrawerOpened(@NonNull DrawerLayout parent, @NonNull View drawerView) {
        final Fragment fragment;
        fragment = this.fragmentManager.findFragmentById(drawerView.getId());
        if (fragment != null) {
            this.fragmentManager.beginTransaction()
                    .setMaxLifecycle(fragment, Lifecycle.State.RESUMED)
                    .setPrimaryNavigationFragment(fragment)
                    .commit();
        }
    }

    @Override
    public void onDrawerClosed(@NonNull DrawerLayout parent, @NonNull View drawerView) {
        final Fragment fragment;
        fragment = this.fragmentManager.findFragmentById(drawerView.getId());
        if (fragment != null) {
            this.fragmentManager.beginTransaction()
                    .setMaxLifecycle(fragment, Lifecycle.State.STARTED)
                    .commit();
        }
    }

    @Override
    public void setSubtleComponent(@NonNull Class<? extends Fragment> tClass) {
        final int position = 4; // 我的-右边缘
        final String fragmentTag = makeFragmentTag(R.id.rightContainer, position);
        Fragment fragment;
        fragment = this.fragmentManager.findFragmentByTag(fragmentTag);
        if (fragment != null) { // 已存在
            return;
        }
        fragment = this.fragmentManager.getFragmentFactory()
                .instantiate(tClass.getClassLoader(), tClass.getName());
        // 开启业务
        final FragmentTransaction fragmentTransaction;
        fragmentTransaction = this.fragmentManager.beginTransaction();
        fragmentTransaction.add(R.id.rightContainer, fragment, fragmentTag);
        fragmentTransaction.setMaxLifecycle(fragment, Lifecycle.State.STARTED);
        fragmentTransaction.commit(); // 提交业务
    }

    @Override
    public void setSimpleComponent(@NonNull Class<? extends Fragment> tClass) {
        final int position = 0; // 首页-左边缘
        final String fragmentTag = makeFragmentTag(R.id.leftContainer, position);
        Fragment fragment;
        fragment = this.fragmentManager.findFragmentByTag(fragmentTag);
        if (fragment != null) { // 已存在
            return;
        }
        fragment = this.fragmentManager.getFragmentFactory()
                .instantiate(tClass.getClassLoader(), tClass.getName());
        // 开启业务
        final FragmentTransaction fragmentTransaction;
        fragmentTransaction = this.fragmentManager.beginTransaction();
        fragmentTransaction.add(R.id.leftContainer, fragment, fragmentTag);
        fragmentTransaction.setMaxLifecycle(fragment, Lifecycle.State.STARTED);
        fragmentTransaction.commit(); // 提交业务
    }

    @Override
    public void setPersonComponent(@NonNull Class<? extends Fragment> tClass,
                                   @Nullable Bundle arguments) {
        if (arguments == null) {
            arguments = new Bundle();
        }
        final int position = 0; // 首页-右边缘
        final String fragmentTag = makeFragmentTag(R.id.rightContainer, position);
        Fragment fragment;
        fragment = this.fragmentManager.findFragmentByTag(fragmentTag);
        if (fragment != null) { // 已存在
            this.dispatchOnNewArguments(fragment, arguments);
            return;
        }
        fragment = this.fragmentManager.getFragmentFactory()
                .instantiate(tClass.getClassLoader(), tClass.getName());
        fragment.setArguments(arguments);
        // 开启业务
        final FragmentTransaction fragmentTransaction;
        fragmentTransaction = this.fragmentManager.beginTransaction();
        fragmentTransaction.add(R.id.rightContainer, fragment, fragmentTag);
        fragmentTransaction.setMaxLifecycle(fragment, Lifecycle.State.STARTED);
        fragmentTransaction.commit(); // 提交业务
    }

    @Override
    public void setCommentComponent(@NonNull Class<? extends Fragment> tClass,
                                    @Nullable Bundle arguments) {
        if (arguments == null) {
            arguments = new Bundle();
        }
        final int position = 0; // 首页-下边缘
        final String fragmentTag = makeFragmentTag(R.id.bottomContainer, position);
        Fragment fragment;
        fragment = this.fragmentManager.findFragmentByTag(fragmentTag);
        if (fragment != null) { // 已存在
            this.dispatchOnNewArguments(fragment, arguments);
            return;
        }
        fragment = this.fragmentManager.getFragmentFactory()
                .instantiate(tClass.getClassLoader(), tClass.getName());
        fragment.setArguments(arguments);
        // 开启业务
        final FragmentTransaction fragmentTransaction;
        fragmentTransaction = this.fragmentManager.beginTransaction();
        fragmentTransaction.add(R.id.bottomContainer, fragment, fragmentTag);
        fragmentTransaction.setMaxLifecycle(fragment, Lifecycle.State.STARTED);
        fragmentTransaction.commit(); // 提交业务
    }

    @Override
    public void openSubtleComponent() {
        this.mainContainer.openDrawer(DrawerLayout.EDGE_RIGHT);
    }

    @Override
    public void openSimpleComponent() {
        this.mainContainer.openDrawer(DrawerLayout.EDGE_LEFT);
    }

    @Override
    public void openPersonComponent() {
        this.mainContainer.openDrawer(DrawerLayout.EDGE_RIGHT);
    }

    @Override
    public void openCommentComponent() {
        this.mainContainer.openDrawer(DrawerLayout.EDGE_BOTTOM);
    }

    @Override
    public void closeDrawerComponent(@NonNull UIPageControllerOwner owner) {
        final UIPageController uiPageController = owner.getUIPageController();
        // 尝试关闭抽屉, 可能组件并不发生在抽屉盒子里.
        if (DrawerLayout.closeDrawerView(uiPageController.getContentView())) {
            return;
        }
        uiPageController.getUINavigatorController().navigateUp();
    }

    @Override
    public void setDrawerListener(@NonNull LifecycleOwner owner,
                                  @NonNull DrawerLayout.DrawerListener listener) {
        final Lifecycle l = owner.getLifecycle();
        l.addObserver(new ComponentObserver(this.mainContainer, listener));
    }

    public boolean goBack() {
        if (this.mainContainer.hasVisibleDrawer()) {
            this.mainContainer.closeDrawers();
            return true;
        }
        return false;
    }

    private void dispatchOnNewArguments(@NonNull Fragment fragment,
                                        @NonNull Bundle arguments) {
        if (fragment instanceof IAppRoute.IDrawerCallback) {
            ((IAppRoute.IDrawerCallback) fragment).onNewArguments(arguments);
        } else {
            throw new IllegalStateException(
                    "Not has implements AppRoute.DrawerCallback!");
        }
    }

    /*
     * 切换抽屉视图
     * */
    private void onPageSelected(@NonNull ViewPager pageContent, int position) {
        final int oldPosition = this.currentPosition;
        if (oldPosition == position) {
            return;
        }
        final FragmentManager fragmentManager = this.fragmentManager;
        final FragmentTransaction fragmentTransaction;
        fragmentTransaction = fragmentManager.beginTransaction();
        Fragment fragment;

        String fragmentTag;
        fragmentTag = makeFragmentTag(R.id.leftContainer, oldPosition);
        fragment = fragmentManager.findFragmentByTag(fragmentTag);
        if (fragment != null) {
            fragmentTransaction.detach(fragment);
        }
        fragmentTag = makeFragmentTag(R.id.rightContainer, oldPosition);
        fragment = fragmentManager.findFragmentByTag(fragmentTag);
        if (fragment != null) {
            fragmentTransaction.detach(fragment);
        }
        fragmentTag = makeFragmentTag(R.id.bottomContainer, oldPosition);
        fragment = fragmentManager.findFragmentByTag(fragmentTag);
        if (fragment != null) {
            fragmentTransaction.detach(fragment);
        }
        this.currentPosition = position;
        fragmentTag = makeFragmentTag(R.id.leftContainer, position);
        fragment = fragmentManager.findFragmentByTag(fragmentTag);
        if (fragment != null) {
            fragmentTransaction.attach(fragment);
            fragmentTransaction.setMaxLifecycle(fragment, Lifecycle.State.STARTED);
        }
        fragmentTag = makeFragmentTag(R.id.rightContainer, position);
        fragment = fragmentManager.findFragmentByTag(fragmentTag);
        if (fragment != null) {
            fragmentTransaction.attach(fragment);
            fragmentTransaction.setMaxLifecycle(fragment, Lifecycle.State.STARTED);
        }
        fragmentTag = makeFragmentTag(R.id.bottomContainer, position);
        fragment = fragmentManager.findFragmentByTag(fragmentTag);
        if (fragment != null) {
            fragmentTransaction.attach(fragment);
            fragmentTransaction.setMaxLifecycle(fragment, Lifecycle.State.STARTED);
        }
        fragmentTransaction.commit();
    }

    @NonNull
    private static String makeFragmentTag(int containerId, int position) {
        return "android:switcher:" + containerId + ":" + position;
    }

    final class ComponentListener extends ViewPager.SimpleOnPageChangeListener {
        @NonNull
        private final ViewPager pageContent;

        public ComponentListener(@NonNull ViewPager pageContent) {
            this.pageContent = pageContent;
        }

        @Override
        public void onPageSelected(int position) {
            MainController.this.onPageSelected(this.pageContent, position);
        }
    }

    private final static class ComponentObserver implements DefaultLifecycleObserver {
        @NonNull
        private final DrawerLayout drawerLayout;
        @NonNull
        private final DrawerLayout.DrawerListener listener;

        public ComponentObserver(@NonNull DrawerLayout drawerLayout,
                                 @NonNull DrawerLayout.DrawerListener listener) {
            this.listener = listener;
            this.drawerLayout = drawerLayout;
            this.drawerLayout.addDrawerListener(listener);
        }

        @Override
        public void onDestroy(@NonNull LifecycleOwner owner) {
            this.drawerLayout.removeDrawerListener(this.listener);
        }
    }
}
