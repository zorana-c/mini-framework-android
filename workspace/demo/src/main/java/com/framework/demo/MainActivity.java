package com.framework.demo;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.palette.graphics.Palette;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.os.Process;
import android.view.KeyEvent;
import android.view.View;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.common.route.IChatRoute;
import com.framework.common.palette.GlidePalette;
import com.framework.common.palette.PaletteListener;
import com.framework.common.palette.Profile;
import com.framework.common.ui.picker.UICountryDialogFragment;
import com.framework.common.ui.picker.UICountryViewModel;
import com.framework.common.ui.picker.UIDateTimeDialogFragment;
import com.framework.common.ui.picker.UIDateTimeViewModel;
import com.framework.common.util.DateUtils;
import com.framework.core.annotation.UIPageConfigure;
import com.framework.core.compat.UILog;
import com.framework.core.compat.UIToast;
import com.framework.core.content.UIViewModelProviders;
import com.framework.core.rx.permission.RxPermission;
import com.framework.core.ui.abs.UIDecorFragmentActivity;
import com.framework.core.widget.UIDecorLayout;
import com.framework.core.widget.UISliverScrollView;
import com.framework.demo.content.CustomDecorOptions;
import com.framework.demo.http.repository.TestServiceRepository;
import com.navigation.UINavigatorController;

/**
 * @Author create by Zhengzelong on 2022/7/19
 * @Email : 171905184@qq.com
 * @Description :
 */
@UINavigatorController.ActivityRoute(
        launchFlags = Intent.FLAG_ACTIVITY_CLEAR_TOP
                | Intent.FLAG_ACTIVITY_CLEAR_TASK
)
@UIPageConfigure(uiPageOptionsClass = CustomDecorOptions.class)
public class MainActivity extends UIDecorFragmentActivity {

    @Override
    public int onUILayoutId(@Nullable Bundle savedInstanceState) {
        return R.layout.activity_main;
    }

    @SuppressLint("CheckResult")
    @Override
    public void onUICreated(@Nullable Bundle savedInstanceState) {
        this.getUIActionBarController()
                .setTitleText("Main")
                .setMenuText("Reset")
                .setBackEnabled(false)
                .setMenuEnabled(false)
                .setMenuClickListener(view -> {
                    this.getUIPageController().postContentOnAnimation();
                });

        this.getUIPageController()
                .addOnLayoutChangedListener((uiDecorLayout, decorLayoutKey) -> {
                    UILog.e("========= Main Layout Key: " + decorLayoutKey);
                    this.getUIActionBarController()
                            .setMenuEnabled(!(UIDecorLayout.DECOR_CONTENT == decorLayoutKey));
                });

        RxPermission.of(this)
                .requestEach(Manifest.permission.CAMERA,
                        Manifest.permission.INTERNET,
                        Manifest.permission.READ_EXTERNAL_STORAGE,
                        Manifest.permission.WRITE_EXTERNAL_STORAGE,
                        Manifest.permission.ACCESS_FINE_LOCATION,
                        Manifest.permission.ACCESS_COARSE_LOCATION)
                .subscribe(t -> {
                    UILog.e("SUCCESS: " + t.toString());
                }, UILog::e);

        this.getUIPageController()
                .<UISliverScrollView>requireViewById(R.id.sliverScrollView)
                .setZoomImageUrl("https://img2.baidu.com/it/u=364539099,1287245682&fm=253&fmt=auto&app=138&f=JPEG?w=800&h=500");

        UIViewModelProviders.of(this)
                .get(UICountryViewModel.class)
                .withCountryObserve(UIToast::asyncToast);

        UIViewModelProviders.of(this)
                .get(UIDateTimeViewModel.class)
                .withTimestampObserve(timestamp -> UIToast.asyncToast(DateUtils.formatTimeMillis(timestamp)));

        this.randomPalette();
    }

    @SuppressLint("CheckResult")
    @Override
    public void onUIRefresh(@Nullable Bundle savedInstanceState) {
        TestServiceRepository
                .get()
                .test()
                .subscribeOnIO()
                .observeOnMainThread()
                .subscribeWithLayout(this)
                .delay(2000)
                .subscribe(UIToast::asyncToast, UILog::e);
    }

    @SuppressLint("CheckResult")
    public void joinAPIService(@NonNull View view) {
        TestServiceRepository
                .get()
                .test2()
                .subscribeOnIO()
                .observeOnMainThread()
                .doOnErrorWithLayout(this)
                .doOnDisposeWithLayout(this)
                .doOnCompleteWithLayout(this)
                .doOnSubscribeWithLayout(this)
                .subscribeWithDialog(this)
                .delay(2000)
                .subscribe(UIToast::asyncToast, UILog::e);
    }

    public void joinListFragment(@NonNull View view) {
        this.getUINavigatorController().startFragment(ListFragment.class);
    }

    public void joinLayoutError(@NonNull View view) {
        this.getUIPageController().postErrorOnAnimation();
    }

    public void joinLayoutEmpty(@NonNull View view) {
        this.getUIPageController().postEmptyOnAnimation();
    }

    public void joinListPopupFragment(@NonNull View view) {
        this.getUINavigatorController().showAsDropDown(ListPopupFragment.class,
                this.getUIActionBarController().requireActionBar());
    }

    public void joinListDialogFragment(@NonNull View view) {
        this.getUINavigatorController().showDialogFragment(ListDialogFragment.class);
    }

    public void joinPushFragment(@NonNull View view) {
        this.getUINavigatorController().startFragment(PushFragment.class);
    }

    public void joinPushPopupFragment(@NonNull View view) {
        this.getUINavigatorController().showAsDropDown(PushPopupFragment.class,
                this.getUIActionBarController().requireActionBar());
    }

    public void joinPushDialogFragment(@NonNull View view) {
        this.getUINavigatorController().showDialogFragment(PushDialogFragment.class);
    }

    public void joinPickerDialogFragment(@NonNull View view) {
        this.getUINavigatorController().showDialogFragment(PickerDialogFragment.class);
    }

    public void joinPickerTimestampDialogFragment(@NonNull View view) {
        this.getUINavigatorController().showDialogFragment(UIDateTimeDialogFragment.class);
    }

    public void joinPickerCountryDialogFragment(@NonNull View view) {
        this.getUINavigatorController().showDialogFragment(UICountryDialogFragment.class);
    }

    public void joinChatFragmentActivity(@NonNull View view) {
        IChatRoute.get().getNavigator().pushChat(this);
    }

    public void joinSliverFragment(@NonNull View view) {
        this.getUINavigatorController().startFragment(SliverFragment.class);
    }

    public void joinSliverFragment2(@NonNull View view) {
        this.getUINavigatorController().startFragment(SliverFragment2.class);
    }

    public void joinSliverFragment3(@NonNull View view) {
        this.getUINavigatorController().startFragment(SliverFragment3.class);
    }

    public void joinTestFragment(@NonNull View view) {
//        this.randomPalette();
        this.getUINavigatorController().startFragment(ListFragment2.class);
    }

    private void randomPalette() {
        final String[] _urls = new String[]{
                "https://img2.baidu.com/it/u=364539099,1287245682&fm=253&fmt=auto&app=138&f=JPEG?w=800&h=500",
                "https://img0.baidu.com/it/u=1483946392,80414132&fm=253&fmt=auto&app=138&f=JPEG?w=1200&h=500",
                "https://img0.baidu.com/it/u=2604863857,3097058401&fm=253&fmt=auto&app=138&f=JPEG?w=1280&h=433",
                "https://img2.baidu.com/it/u=1961302154,246146198&fm=253&fmt=auto&app=138&f=JPEG?w=1600&h=500",
                "https://img1.baidu.com/it/u=2757935014,2054677376&fm=253&fmt=auto&app=138&f=JPEG?w=1280&h=367",
                "https://img1.baidu.com/it/u=4064761558,1446081750&fm=253&fmt=auto&app=138&f=JPEG?w=1053&h=390",
                "https://img2.baidu.com/it/u=3169179090,881607947&fm=253&fmt=auto&app=138&f=JPEG?w=1440&h=500",
                "https://img2.baidu.com/it/u=1524389572,4044551254&fm=253&fmt=auto&app=138&f=JPEG?w=1338&h=500",
                "https://img2.baidu.com/it/u=192846542,3866316605&fm=253&fmt=auto&app=138&f=JPEG?w=1209&h=500",
        };
        final String url = _urls[((int) (Math.random() * 10)) % _urls.length];
        final ImageView it =
                this.getUIPageController().requireViewById(R.id.floatingImage);
        Glide.with(this)
                .load(url)
                .diskCacheStrategy(DiskCacheStrategy.ALL)
                .centerCrop()
                .listener(GlidePalette
                        .with(url)
                        .asDrawable()
                        .addListener(new PaletteListener() {
                            @NonNull
                            @Override
                            public Palette.Builder onIntercept(@NonNull Palette.Builder builder) {
                                return builder.addFilter((rgb, hsl) -> {
                                    // UILog.e("====Filter " + rgb + " " + Arrays.toString(hsl));
                                    return true;
                                });
                            }

                            @Override
                            public void onPaletteLoaded(@Nullable Palette palette) {
                                UILog.e("====onPaletteLoaded " + (palette == null ? "null" : palette.toString()));
                            }
                        })

                        .use(Profile.MUTED)
                        .intoBackground(it)
                        .crossFade()

                        .use(Profile.VIBRANT)
                        .intoBackground(it)
                        .crossFade()
                )
                .into(it);
    }

    private long mLastTimeMillis = 0;

    @Override
    public boolean onKeyDown(int keyCode, @NonNull KeyEvent event) {
        if (KeyEvent.KEYCODE_BACK == keyCode
                && KeyEvent.ACTION_DOWN == event.getAction()) {
            final long currentTimeMillis = System.currentTimeMillis();
            if ((currentTimeMillis - this.mLastTimeMillis) >= 3000) {
                this.mLastTimeMillis = currentTimeMillis;
                UIToast.toast("再按一次退出程序");
            } else {
                // 结束界面
                this.getUINavigatorController().navigateUp();
                // 通知回收
                System.gc();
                // 退出虚拟机
                System.exit(0);
                // 杀掉当前进程
                Process.killProcess(Process.myPid());
            }
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }
}