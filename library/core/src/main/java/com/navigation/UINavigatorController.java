package com.navigation;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;

import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.ActivityResultRegistry;
import androidx.activity.result.contract.ActivityResultContract;
import androidx.annotation.AnimRes;
import androidx.annotation.AnimatorRes;
import androidx.annotation.CallSuper;
import androidx.annotation.IdRes;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.fragment.app.FragmentFactory;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.lifecycle.DefaultLifecycleObserver;
import androidx.lifecycle.Lifecycle;
import androidx.lifecycle.LifecycleOwner;

import com.framework.core.content.UIActivityCallback;
import com.framework.core.content.UIActivityDispatcher;
import com.framework.core.content.UIPageController;
import com.framework.core.content.UIPageControllerOwner;
import com.framework.core.ui.abs.UIFragmentActivity;
import com.navigation.floating.UIDialogFragment;
import com.navigation.floating.UIPopupFragment;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.util.Objects;

/**
 * @Author create by Zhengzelong on 2022/5/10
 * @Email : 171905184@qq.com
 * @Description :
 */
public class UINavigatorController {
    @NonNull
    private static final String KEY_NAV_TAG = "androidx.navigator.key:tag";
    @NonNull
    private static final String KEY_NAV_DATA = "androidx.navigator.key:data";
    @NonNull
    private static final String KEY_NAV_CLASS = "androidx.navigator.key:class";
    @NonNull
    private static final String KEY_POP_ENTER_ANIM = "androidx.navigator.key:popEnterAnim";
    @NonNull
    private static final String KEY_POP_EXIT_ANIM = "androidx.navigator.key:popExitAnim";
    @NonNull
    private static final String FRAGMENT_PREFIX_NAME = "androidx.navigator.prefix.name";
    @NonNull
    private static final String FRAGMENT_PREFIX_TAG = "androidx.navigator.prefix.tag";

    /**
     * <pre>
     * @ActivityRoute(launchFlags = Intent.FLAG_ACTIVITY_NEW_TASK)
     * public class Foo extends UIFragmentActivity {
     *     // TODO
     * }
     * </pre>
     */
    @Target(ElementType.TYPE)
    @Retention(RetentionPolicy.RUNTIME)
    public @interface ActivityRoute {
        /**
         * Intent flags
         * {@link Intent#FLAG_ACTIVITY_NEW_TASK}
         */
        int launchFlags() default 0;
    }

    /**
     * <pre>
     * @FragmentRoute(hostClass = UINavigatorAbility.class)
     * public class Foo extends UIFragment {
     *     // TODO
     * }
     * </pre>
     */
    @Target(ElementType.TYPE)
    @Retention(RetentionPolicy.RUNTIME)
    public @interface FragmentRoute {
        /**
         * Host class
         * {@link FragmentActivity}
         * {@link UIFragmentActivity}
         */
        @NonNull
        Class<? extends FragmentActivity> hostClass()
                default UINavigatorAbility.class;

        /**
         * Fragment tag
         */
        @Nullable
        String tag() default "";

        /**
         * Intent flags
         * {@link Intent#FLAG_ACTIVITY_NEW_TASK}
         */
        int launchFlags() default 0;
    }

    @NonNull
    public static Bundle getArguments(@NonNull UIPageControllerOwner owner) {
        return getArguments(owner.<UIPageController>getUIPageController());
    }

    @NonNull
    public static Bundle getArguments(@NonNull UIPageController uiPageController) {
        final UIPageController.UIComponent uiComponent = uiPageController.getUIComponent();
        Bundle arguments = null;
        if (uiComponent instanceof Fragment) {
            arguments = ((Fragment) uiComponent).getArguments();
        }
        if (uiComponent instanceof FragmentActivity) {
            final Intent intent = ((FragmentActivity) uiComponent).getIntent();
            arguments = intent.getBundleExtra(KEY_NAV_DATA);
        }
        if (arguments == null) {
            arguments = Bundle.EMPTY;
        }
        return arguments;
    }

    public static void initMainRoute(@NonNull FragmentActivity fragmentActivity) {
        final Intent intent = fragmentActivity.getIntent();
        final String tag = intent.getStringExtra(KEY_NAV_TAG);
        final Bundle args = intent.getBundleExtra(KEY_NAV_DATA);
        final String name = intent.getStringExtra(KEY_NAV_CLASS);
        if (TextUtils.isEmpty(name)) {
            return;
        }
        final FragmentManager fragmentManager = fragmentActivity.getSupportFragmentManager();
        if (fragmentManager.isStateSaved()) {
            return;
        }
        Fragment fragment;
        fragment = fragmentManager.findFragmentById(android.R.id.content);
        if (fragment instanceof UIPageControllerOwner) {
            final UIPageController uiPageController;
            uiPageController = ((UIPageControllerOwner) fragment).getUIPageController();
            uiPageController.setUseViewCache(false);
        }
        final FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        final FragmentFactory fragmentFactory = fragmentManager.getFragmentFactory();
        fragment = fragmentFactory.instantiate(fragmentActivity.getClassLoader(), name);
        fragment.setArguments(args);
        fragmentTransaction.replace(android.R.id.content, fragment, tag);
        fragmentTransaction.setPrimaryNavigationFragment(fragment);
        fragmentTransaction.commitNow();
    }

    public static boolean finish(@NonNull UIPageControllerOwner owner) {
        return finish(owner.<UIPageController>getUIPageController());
    }

    public static boolean finish(@NonNull UIPageController uiPageController) {
        final FragmentActivity fragmentActivity;
        fragmentActivity = uiPageController.getFragmentActivity();
        if (fragmentActivity == null) {
            return false;
        }
        ActivityCompat.finishAfterTransition(fragmentActivity);
        overridePendingTransition(fragmentActivity);
        return true;
    }

    public static boolean navigateFragmentUp(@NonNull UIPageControllerOwner owner) {
        return navigateFragmentUp(owner.<UIPageController>getUIPageController());
    }

    public static boolean navigateFragmentUp(@NonNull UIPageControllerOwner owner, int flags) {
        return navigateFragmentUp(owner.<UIPageController>getUIPageController(), flags);
    }

    public static boolean navigateFragmentUp(@NonNull UIPageController uiPageController) {
        return navigateFragmentUp(uiPageController, FragmentManager.POP_BACK_STACK_INCLUSIVE);
    }

    public static boolean navigateFragmentUp(@NonNull UIPageController uiPageController, int flags) {
        final UIPageController.UIComponent uiComponent = uiPageController.getUIComponent();
        Fragment fragment = null;
        if (uiComponent instanceof Fragment) {
            fragment = (Fragment) uiComponent;
        }
        if (fragment == null) {
            return false;
        }
        final int fragmentIdInt = findFragmentIdFromTag(fragment);
        if (fragmentIdInt == View.NO_ID) {
            return popFragmentUp(fragment);
        }
        final FragmentManager fragmentManager = fragment.getParentFragmentManager();
        if (fragmentManager.isStateSaved()) {
            return false;
        }
        fragmentManager.popBackStack(fragmentIdInt, flags);
        return true;
    }

    public static boolean navigateFragmentUpImmediate(@NonNull UIPageControllerOwner owner) {
        return navigateFragmentUpImmediate(owner.<UIPageController>getUIPageController());
    }

    public static boolean navigateFragmentUpImmediate(@NonNull UIPageControllerOwner owner, int flags) {
        return navigateFragmentUpImmediate(owner.<UIPageController>getUIPageController(), flags);
    }

    public static boolean navigateFragmentUpImmediate(@NonNull UIPageController uiPageController) {
        return navigateFragmentUpImmediate(uiPageController, FragmentManager.POP_BACK_STACK_INCLUSIVE);
    }

    public static boolean navigateFragmentUpImmediate(@NonNull UIPageController uiPageController, int flags) {
        final UIPageController.UIComponent uiComponent = uiPageController.getUIComponent();
        Fragment fragment = null;
        if (uiComponent instanceof Fragment) {
            fragment = (Fragment) uiComponent;
        }
        if (fragment == null) {
            return false;
        }
        final int fragmentIdInt = findFragmentIdFromTag(fragment);
        if (fragmentIdInt == View.NO_ID) {
            return popFragmentUpImmediate(fragment);
        }
        final FragmentManager fragmentManager = fragment.getParentFragmentManager();
        if (fragmentManager.isStateSaved()) {
            return false;
        }
        return fragmentManager.popBackStackImmediate(fragmentIdInt, flags);
    }

    public static boolean popFragmentUp(@NonNull Fragment fragment) {
        if (ignoredPopFragmentUp(fragment)) {
            return false;
        }
        final FragmentManager fragmentManager = fragment.getParentFragmentManager();
        if (fragmentManager.isStateSaved()) {
            return false;
        }
        fragmentManager
                .beginTransaction()
                .remove(fragment)
                .commit();
        return true;
    }

    public static boolean popFragmentUpImmediate(@NonNull Fragment fragment) {
        if (ignoredPopFragmentUp(fragment)) {
            return false;
        }
        final FragmentManager fragmentManager = fragment.getParentFragmentManager();
        if (fragmentManager.isStateSaved()) {
            return false;
        }
        fragmentManager
                .beginTransaction()
                .remove(fragment)
                .commitNow();
        return true;
    }

    public static void overridePendingTransition(@NonNull Activity activity) {
        final Intent intent = activity.getIntent();
        if (intent == null) {
            return;
        }
        int popEnterAnim = intent.getIntExtra(KEY_POP_ENTER_ANIM, -1);
        int popExitAnim = intent.getIntExtra(KEY_POP_EXIT_ANIM, -1);

        if (popEnterAnim != -1 || popExitAnim != -1) {
            popEnterAnim = popEnterAnim == -1 ? 0 : popEnterAnim;
            popExitAnim = popExitAnim == -1 ? 0 : popExitAnim;
            activity.overridePendingTransition(popEnterAnim, popExitAnim);
        }
    }

    @NonNull
    private final UIPageController mUIPageController;
    @NonNull
    private UINavigatorOptions mUINavigatorOptions = UINavigatorOptions.EMPTY;

    public UINavigatorController(@NonNull UIPageControllerOwner owner) {
        this(owner.<UIPageController>getUIPageController());
    }

    public UINavigatorController(@NonNull UIPageController uiPageController) {
        this.mUIPageController = uiPageController;

        final UINavigatorEvent uiNavigatorEvent = new UINavigatorEvent();
        final LifecycleOwner owner = uiPageController.getUIComponent();
        final Lifecycle l = owner.getLifecycle();
        l.addObserver(uiNavigatorEvent);

        final UIActivityDispatcher ad = uiPageController.getUIActivityDispatcher();
        if (ad != null) {
            ad.addCallback(l, uiNavigatorEvent);
        }
    }

    @CallSuper
    public void onNewIntent(@NonNull Intent intent) {
        final UIPageController.UIComponent uiComponent;
        uiComponent = this.mUIPageController.getUIComponent();

        if (uiComponent instanceof FragmentActivity) {
            final FragmentActivity fragmentActivity;
            fragmentActivity = (FragmentActivity) uiComponent;
            fragmentActivity.setIntent(intent);
            UINavigatorController.initMainRoute(fragmentActivity);
        }
    }

    @NonNull
    public UINavigatorOptions getUINavigatorOptions() {
        return this.mUINavigatorOptions;
    }

    @NonNull
    public UINavigatorController setUINavigatorOptions(@NonNull UINavigatorOptions options) {
        this.mUINavigatorOptions = options;
        return this;
    }

    @NonNull
    public final UIPageController getUIPageController() {
        return this.mUIPageController;
    }

    @NonNull
    public final FragmentManager getChildFragmentManager() {
        return this.mUIPageController.getChildFragmentManager();
    }

    @NonNull
    public final FragmentManager getParentFragmentManager() {
        return this.mUIPageController.getParentFragmentManager();
    }

    @NonNull
    public final <I, O> ActivityResultLauncher<I> registerForActivityResult(
            @NonNull ActivityResultContract<I, O> contract,
            @NonNull ActivityResultCallback<O> callback) {
        return this.mUIPageController.getUIComponent()
                .registerForActivityResult(contract, callback);
    }

    @NonNull
    public final <I, O> ActivityResultLauncher<I> registerForActivityResult(
            @NonNull ActivityResultContract<I, O> contract,
            @NonNull ActivityResultCallback<O> callback,
            @NonNull ActivityResultRegistry registry) {
        return this.mUIPageController.getUIComponent()
                .registerForActivityResult(contract, registry, callback);
    }

    @NonNull
    public final FragmentTransaction beginTransaction() {
        return this.beginTransaction(this.mUINavigatorOptions);
    }

    @NonNull
    public FragmentTransaction beginTransaction(@NonNull UINavigatorOptions options) {
        final FragmentManager fragmentManager = this.getChildFragmentManager();
        return fragmentManager
                .beginTransaction()
                .setCustomAnimations(
                        options.getEnterAnim(),
                        options.getExitAnim(),
                        options.getPopEnterAnim(),
                        options.getPopExitAnim());
    }

    // showAtLocation

    @NonNull
    public final UINavigatorController showAtLocation(@NonNull Class<? extends UIPopupFragment> uiPopupFragmentClass, @NonNull View parent) {
        return this.showAtLocation(uiPopupFragmentClass, parent, null);
    }

    @NonNull
    public final UINavigatorController showAtLocation(@NonNull Class<? extends UIPopupFragment> uiPopupFragmentClass, @NonNull View parent, @Nullable Bundle arguments) {
        return this.showAtLocation(this.instantiate(uiPopupFragmentClass, arguments), parent);
    }

    @NonNull
    public final UINavigatorController showAtLocation(@NonNull UIPopupFragment uiPopupFragment, @NonNull View parent) {
        uiPopupFragment.setShowAtLocation(parent);
        return this.showFragment(uiPopupFragment);
    }

    // showAsDropDown

    @NonNull
    public final UINavigatorController showAsDropDown(@NonNull Class<? extends UIPopupFragment> uiPopupFragmentClass, @NonNull View anchorView) {
        return this.showAsDropDown(uiPopupFragmentClass, anchorView, null);
    }

    @NonNull
    public final UINavigatorController showAsDropDown(@NonNull Class<? extends UIPopupFragment> uiPopupFragmentClass, @NonNull View anchorView, @Nullable Bundle arguments) {
        return this.showAsDropDown(this.instantiate(uiPopupFragmentClass, arguments), anchorView);
    }

    @NonNull
    public final UINavigatorController showAsDropDown(@NonNull UIPopupFragment uiPopupFragment, @NonNull View anchorView) {
        uiPopupFragment.setShowAsDropDown(anchorView);
        return this.showFragment(uiPopupFragment);
    }

    // showDialogFragment

    @NonNull
    public final UINavigatorController showDialogFragment(@NonNull Class<? extends UIDialogFragment> uiDialogFragmentClass) {
        return this.showDialogFragment(uiDialogFragmentClass, null);
    }

    @NonNull
    public final UINavigatorController showDialogFragment(@NonNull Class<? extends UIDialogFragment> uiDialogFragmentClass, @Nullable Bundle arguments) {
        return this.showDialogFragment(this.instantiate(uiDialogFragmentClass, arguments));
    }

    @NonNull
    public final UINavigatorController showDialogFragment(@NonNull UIDialogFragment uiDialogFragment) {
        return this.showFragment(uiDialogFragment);
    }

    // showFragment

    @NonNull
    public final UINavigatorController showFragment(@NonNull Class<? extends Fragment> fragmentClass) {
        return this.showFragment(fragmentClass, null);
    }

    @NonNull
    public final UINavigatorController showFragment(@NonNull Class<? extends Fragment> fragmentClass, @Nullable Bundle arguments) {
        return this.showFragment(this.instantiate(fragmentClass, arguments));
    }

    @NonNull
    public UINavigatorController showFragment(@NonNull Fragment fragment) {
        final String fragmentTag = this.generateFragmentTag(0);
        this.beginTransaction().add(fragment, fragmentTag)
                .setPrimaryNavigationFragment(fragment)
                .setReorderingAllowed(true)
                .commit();
        return this;
    }

    // showFragmentNow

    @NonNull
    public final UINavigatorController showFragmentNow(@NonNull Class<? extends Fragment> fragmentClass) {
        return this.showFragmentNow(fragmentClass, null);
    }

    @NonNull
    public final UINavigatorController showFragmentNow(@NonNull Class<? extends Fragment> fragmentClass, @Nullable Bundle arguments) {
        return this.showFragmentNow(this.instantiate(fragmentClass, arguments));
    }

    @NonNull
    public UINavigatorController showFragmentNow(@NonNull Fragment fragment) {
        final String fragmentTag = this.generateFragmentTag(0);
        this.beginTransaction().add(fragment, fragmentTag)
                .setPrimaryNavigationFragment(fragment)
                .setReorderingAllowed(true)
                .commitNow();
        return this;
    }

    // pushHostFragment

    @NonNull
    public final UINavigatorController pushHostFragment(@NonNull Class<? extends Fragment> fragmentClass) {
        return this.pushHostFragment(fragmentClass, null);
    }

    @NonNull
    public final UINavigatorController pushHostFragment(@NonNull Class<? extends Fragment> fragmentClass, @Nullable Bundle arguments) {
        return this.pushHostFragment(this.instantiate(fragmentClass, arguments));
    }

    @NonNull
    public UINavigatorController pushHostFragment(@NonNull Fragment fragment) {
        this.mUIPageController
                .requireUIHostController()
                .getUINavigatorController()
                .pushFragment(android.R.id.content, fragment);
        return this;
    }

    // pushFragment

    @NonNull
    public final UINavigatorController pushFragment(@IdRes int containerId, @NonNull Class<? extends Fragment> fragmentClass) {
        return this.pushFragment(containerId, fragmentClass, null);
    }

    @NonNull
    public final UINavigatorController pushFragment(@IdRes int containerId, @NonNull Class<? extends Fragment> fragmentClass, @Nullable Bundle arguments) {
        return this.pushFragment(containerId, this.instantiate(fragmentClass, arguments));
    }

    @NonNull
    public UINavigatorController pushFragment(@IdRes int containerId, @NonNull Fragment fragment) {
        final String backStackName = this.generateBackStackName(containerId);
        this.beginTransaction().replace(containerId, fragment, backStackName)
                .setPrimaryNavigationFragment(fragment)
                .addToBackStack(backStackName)
                .setReorderingAllowed(true)
                .commit();
        return this;
    }

    // launch

    @NonNull
    public final UINavigatorController launch(@NonNull String className) {
        return this.launch(className, null);
    }

    @NonNull
    public final UINavigatorController launch(@NonNull String className, @Nullable Bundle arguments) {
        return this.launch(className, arguments, null);
    }

    @NonNull
    public UINavigatorController launch(@NonNull String className, @Nullable Bundle arguments, @Nullable Bundle options) {
        final Class<?> tClass = this.generateClassForName(className);
        if (tClass == null) {
            throw new IllegalStateException("Not found class: " + className);
        }
        if (Fragment.class.isAssignableFrom(tClass)) {
            return this.startFragment((Class<? extends Fragment>) tClass, arguments, options);
        } else if (Activity.class.isAssignableFrom(tClass)) {
            return this.startActivity((Class<? extends Activity>) tClass, arguments, options);
        } else {
            throw new IllegalStateException("Not supported");
        }
    }

    // launchForResult

    @NonNull
    public final UINavigatorController launchForResult(@NonNull String className, int requestCode) {
        return this.launchForResult(className, requestCode, null);
    }

    @NonNull
    public final UINavigatorController launchForResult(@NonNull String className, int requestCode, @Nullable Bundle arguments) {
        return this.launchForResult(className, requestCode, arguments, null);
    }

    @NonNull
    public UINavigatorController launchForResult(@NonNull String className, int requestCode, @Nullable Bundle arguments, @Nullable Bundle options) {
        final Class<?> tClass = this.generateClassForName(className);
        if (tClass == null) {
            throw new IllegalStateException("Not found class: " + className);
        }
        if (Fragment.class.isAssignableFrom(tClass)) {
            return this.startFragmentForResult((Class<? extends Fragment>) tClass, requestCode, arguments, options);
        } else if (Activity.class.isAssignableFrom(tClass)) {
            return this.startActivityForResult((Class<? extends Activity>) tClass, requestCode, arguments, options);
        } else {
            throw new IllegalStateException("Not supported");
        }
    }

    // startFragment

    @NonNull
    public final UINavigatorController startFragment(@NonNull Class<? extends Fragment> fragmentClass) {
        return this.startFragment(fragmentClass, null);
    }

    @NonNull
    public final UINavigatorController startFragment(@NonNull Class<? extends Fragment> fragmentClass, @Nullable Bundle arguments) {
        return this.startFragment(fragmentClass, arguments, null);
    }

    @NonNull
    public UINavigatorController startFragment(@NonNull Class<? extends Fragment> fragmentClass, @Nullable Bundle arguments, @Nullable Bundle options) {
        final Context context = this.mUIPageController.requireContext();
        return this.startActivity(parseFragmentRouteIntent(context, fragmentClass), arguments, options);
    }

    // startFragmentForResult

    @NonNull
    public final UINavigatorController startFragmentForResult(@NonNull Class<? extends Fragment> fragmentClass, int requestCode) {
        return this.startFragmentForResult(fragmentClass, requestCode, null);
    }

    @NonNull
    public final UINavigatorController startFragmentForResult(@NonNull Class<? extends Fragment> fragmentClass, int requestCode, @Nullable Bundle arguments) {
        return this.startFragmentForResult(fragmentClass, requestCode, arguments, null);
    }

    @NonNull
    public UINavigatorController startFragmentForResult(@NonNull Class<? extends Fragment> fragmentClass, int requestCode, @Nullable Bundle arguments, @Nullable Bundle options) {
        final Context context = this.mUIPageController.requireContext();
        return this.startActivityForResult(parseFragmentRouteIntent(context, fragmentClass), requestCode, arguments, options);
    }

    // startActivity

    @NonNull
    public final UINavigatorController startActivity(@NonNull Class<? extends Activity> activityClass) {
        return this.startActivity(activityClass, null);
    }

    @NonNull
    public final UINavigatorController startActivity(@NonNull Class<? extends Activity> activityClass, @Nullable Bundle arguments) {
        return this.startActivity(activityClass, arguments, null);
    }

    @NonNull
    public final UINavigatorController startActivity(@NonNull Class<? extends Activity> activityClass, @Nullable Bundle arguments, @Nullable Bundle options) {
        final Context context = this.mUIPageController.requireContext();
        return this.startActivity(parseActivityRouteIntent(context, activityClass), arguments, options);
    }

    @NonNull
    public final UINavigatorController startActivity(@NonNull Intent intent) {
        return this.startActivity(intent, null);
    }

    @NonNull
    public final UINavigatorController startActivity(@NonNull Intent intent, @Nullable Bundle arguments) {
        return this.startActivity(intent, arguments, null);
    }

    @NonNull
    public UINavigatorController startActivity(@NonNull Intent intent, @Nullable Bundle arguments, @Nullable Bundle options) {
        final UINavigatorOptions uiNavigatorOptions = this.mUINavigatorOptions;
        intent.putExtra(KEY_POP_ENTER_ANIM, uiNavigatorOptions.getPopEnterAnim());
        intent.putExtra(KEY_POP_EXIT_ANIM, uiNavigatorOptions.getPopExitAnim());
        intent.putExtra(KEY_NAV_DATA, arguments);

        final UIPageController.UIComponent uiComponent = this.mUIPageController.getUIComponent();
        if (uiComponent instanceof Fragment) {
            ((Fragment) uiComponent).startActivity(intent, options);
        } else if (uiComponent instanceof FragmentActivity) {
            ((FragmentActivity) uiComponent).startActivity(intent, options);
        } else {
            throw new IllegalStateException("ERROR UIComponent");
        }
        return this.overridePendingTransition();
    }

    // startActivityForResult

    @NonNull
    public final UINavigatorController startActivityForResult(@NonNull Class<? extends Activity> activityClass, int requestCode) {
        return this.startActivityForResult(activityClass, requestCode, null);
    }

    @NonNull
    public final UINavigatorController startActivityForResult(@NonNull Class<? extends Activity> activityClass, int requestCode, @Nullable Bundle arguments) {
        return this.startActivityForResult(activityClass, requestCode, arguments, null);
    }

    @NonNull
    public final UINavigatorController startActivityForResult(@NonNull Class<? extends Activity> activityClass, int requestCode, @Nullable Bundle arguments, @Nullable Bundle options) {
        final Context context = this.mUIPageController.requireContext();
        return this.startActivityForResult(parseActivityRouteIntent(context, activityClass), requestCode, arguments, options);
    }

    @NonNull
    public final UINavigatorController startActivityForResult(@NonNull Intent intent, int requestCode) {
        return this.startActivityForResult(intent, requestCode, null);
    }

    @NonNull
    public final UINavigatorController startActivityForResult(@NonNull Intent intent, int requestCode, @Nullable Bundle arguments) {
        return this.startActivityForResult(intent, requestCode, arguments, null);
    }

    @NonNull
    public UINavigatorController startActivityForResult(@NonNull Intent intent, int requestCode, @Nullable Bundle arguments, @Nullable Bundle options) {
        final UINavigatorOptions uiNavigatorOptions = this.mUINavigatorOptions;
        intent.putExtra(KEY_POP_ENTER_ANIM, uiNavigatorOptions.getPopEnterAnim());
        intent.putExtra(KEY_POP_EXIT_ANIM, uiNavigatorOptions.getPopExitAnim());
        intent.putExtra(KEY_NAV_DATA, arguments);

        final UIPageController.UIComponent uiComponent = this.mUIPageController.getUIComponent();
        if (uiComponent instanceof Fragment) {
            ((Fragment) uiComponent).startActivityForResult(intent, requestCode, options);
        } else if (uiComponent instanceof FragmentActivity) {
            ((FragmentActivity) uiComponent).startActivityForResult(intent, requestCode, options);
        } else {
            throw new IllegalStateException("ERROR UIComponent");
        }
        return this.overridePendingTransition();
    }

    @NonNull
    public final UINavigatorController overridePendingTransition() {
        final UINavigatorOptions options = this.mUINavigatorOptions;
        final int enterAnim = options.getEnterAnim();
        final int exitAnim = options.getExitAnim();
        return this.overridePendingTransition(enterAnim, exitAnim);
    }

    @NonNull
    public UINavigatorController overridePendingTransition(@AnimatorRes @AnimRes int enterAnim, @AnimatorRes @AnimRes int exitAnim) {
        if (enterAnim != -1 || exitAnim != -1) {
            enterAnim = enterAnim == -1 ? 0 : enterAnim;
            exitAnim = exitAnim == -1 ? 0 : exitAnim;

            final FragmentActivity fragmentActivity;
            fragmentActivity = this.mUIPageController.requireFragmentActivity();
            fragmentActivity.overridePendingTransition(enterAnim, exitAnim);
        }
        return this;
    }

    /**
     * @see Activity#RESULT_CANCELED
     * @see Activity#RESULT_OK
     * @see Activity#RESULT_FIRST_USER
     * @see #setResult(int)
     */
    @NonNull
    public final UINavigatorController setResult(int resultCode) {
        return this.setResult(resultCode, null);
    }

    /**
     * @see Activity#RESULT_CANCELED
     * @see Activity#RESULT_OK
     * @see Activity#RESULT_FIRST_USER
     * @see #setResult(int, Intent)
     */
    @NonNull
    public UINavigatorController setResult(int resultCode, @Nullable Intent data) {
        final FragmentActivity fragmentActivity;
        fragmentActivity = this.mUIPageController.requireFragmentActivity();
        fragmentActivity.setResult(resultCode, data);
        return this;
    }

    public boolean popBackStack() {
        final FragmentManager fragmentManager = this.getChildFragmentManager();
        final int N = fragmentManager.getBackStackEntryCount();
        if (N == 0) {
            return false;
        }
        fragmentManager.popBackStack();
        return true;
    }

    public boolean popBackStackImmediate() {
        final FragmentManager fragmentManager = this.getChildFragmentManager();
        final int N = fragmentManager.getBackStackEntryCount();
        if (N == 0) {
            return false;
        }
        return fragmentManager.popBackStackImmediate();
    }

    public final boolean navigateUp() {
        return this.navigateUp(false);
    }

    public boolean navigateUp(boolean forced) {
        if (!forced && this.popBackStack()) {
            return true;
        }
        if (navigateFragmentUp(this.mUIPageController)) {
            return true;
        }
        return finish(this.mUIPageController);
    }

    public final boolean navigateUpImmediate() {
        return this.navigateUpImmediate(false);
    }

    public boolean navigateUpImmediate(boolean forced) {
        if (!forced && this.popBackStackImmediate()) {
            return true;
        }
        if (navigateFragmentUpImmediate(this.mUIPageController)) {
            return true;
        }
        return finish(this.mUIPageController);
    }

    @NonNull
    public final <T extends Fragment> T instantiate(@NonNull Class<T> fragmentClass) {
        return this.instantiate(fragmentClass, null);
    }

    @NonNull
    public <T extends Fragment> T instantiate(@NonNull Class<T> fragmentClass,
                                              @Nullable Bundle arguments) {
        ClassLoader classLoader = this.getClass().getClassLoader();
        if (classLoader == null) {
            classLoader = Thread.currentThread().getContextClassLoader();
        }
        final FragmentManager fragmentManager = this.getChildFragmentManager();
        final FragmentFactory fragmentFactory = fragmentManager.getFragmentFactory();
        final T fragment;
        fragment = (T) fragmentFactory.instantiate(classLoader, fragmentClass.getName());
        fragment.setArguments(arguments);
        return fragment;
    }

    @Nullable
    public <T> Class<T> generateClassForName(@Nullable String className) {
        if (className == null) {
            return null;
        }
        try {
            ClassLoader classLoader = this.getClass().getClassLoader();
            if (classLoader == null) {
                classLoader = Thread.currentThread().getContextClassLoader();
            }
            return (Class<T>) Class.forName(className, true, classLoader);
        } catch (@NonNull ClassNotFoundException e) {
            e.printStackTrace();
            return null;
        }
    }

    @NonNull
    public final String generateFragmentTag(@IdRes int containerId) {
        final FragmentManager fragmentManager = this.getChildFragmentManager();
        final int fragmentIndex = fragmentManager.getFragments().size() + 1;
        return this.generateKey(FRAGMENT_PREFIX_TAG, containerId, fragmentIndex);
    }

    @NonNull
    public final String generateBackStackName(@IdRes int containerId) {
        final FragmentManager fragmentManager = this.getChildFragmentManager();
        final int backStackIndex = fragmentManager.getBackStackEntryCount() + 1;
        return this.generateKey(FRAGMENT_PREFIX_NAME, containerId, backStackIndex);
    }

    @NonNull
    private String generateKey(@NonNull String prefix,
                               int containerId,
                               int fragmentIndex) {
        return String.format("%s: %s-%s", prefix, containerId, fragmentIndex);
    }

    private static boolean ignoredPopFragmentUp(@NonNull Fragment fragment) {
        final String fragmentTag = fragment.getTag();
        if (TextUtils.isEmpty(fragmentTag)) {
            return true;
        }
        assert fragmentTag != null;
        return !fragmentTag.startsWith(FRAGMENT_PREFIX_TAG);
    }

    @IdRes
    private static int findFragmentIdFromTag(@NonNull Fragment fragment) {
        final FragmentManager fragmentManager = fragment.getParentFragmentManager();
        if (fragmentManager.isStateSaved()) {
            return View.NO_ID;
        }
        final String backStackName = fragment.getTag();
        if (backStackName == null || !backStackName.startsWith(FRAGMENT_PREFIX_NAME)) {
            return View.NO_ID;
        }
        FragmentManager.BackStackEntry backStackEntry;
        for (int indexInt = 0; indexInt < fragmentManager.getBackStackEntryCount(); indexInt++) {
            backStackEntry = fragmentManager.getBackStackEntryAt(indexInt);
            if (Objects.equals(backStackName, backStackEntry.getName())) {
                return backStackEntry.getId();
            }
        }
        return View.NO_ID;
    }

    @NonNull
    private static Intent parseActivityRouteIntent(@NonNull Context context,
                                                   @NonNull Class<? extends Activity> activityClass) {
        final ActivityRoute activityRoute = activityClass.getAnnotation(ActivityRoute.class);
        final int activityFlags;
        if (activityRoute == null) {
            activityFlags = 0;
        } else {
            activityFlags = activityRoute.launchFlags();
        }
        final Intent intent = new Intent(context, activityClass);
        intent.addFlags(activityFlags);
        return intent;
    }

    @NonNull
    private static Intent parseFragmentRouteIntent(@NonNull Context context,
                                                   @NonNull Class<? extends Fragment> fragmentClass) {
        final FragmentRoute fragmentRoute = fragmentClass.getAnnotation(FragmentRoute.class);
        final Class<? extends FragmentActivity> activityClass;
        final int activityFlags;
        String fragmentTag = null;
        if (fragmentRoute == null) {
            activityClass = UINavigatorAbility.class;
            activityFlags = 0;
        } else {
            fragmentTag = fragmentRoute.tag();
            activityClass = fragmentRoute.hostClass();
            activityFlags = fragmentRoute.launchFlags();
        }
        if (TextUtils.isEmpty(fragmentTag)) {
            fragmentTag = fragmentClass.getName();
        }
        final Intent intent = new Intent(context, activityClass);
        intent.putExtra(KEY_NAV_CLASS, fragmentClass.getName());
        intent.putExtra(KEY_NAV_TAG, fragmentTag);
        intent.addFlags(activityFlags);
        return intent;
    }

    private final class UINavigatorEvent extends UIActivityCallback implements DefaultLifecycleObserver {
        private UINavigatorEvent() {
            super(/* Enabled */ true);
        }

        @Override
        public void onNewIntent(@NonNull Intent intent) {
            UINavigatorController.this.onNewIntent(intent);
        }

        @Override
        public boolean onBackPressed() {
            return UINavigatorController.this.navigateUp();
        }

        @Override
        public void onDestroy(@NonNull LifecycleOwner owner) {
            final Lifecycle l = owner.getLifecycle();
            l.removeObserver(this);
        }
    }
}
