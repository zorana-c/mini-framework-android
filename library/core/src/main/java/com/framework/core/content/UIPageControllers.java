package com.framework.core.content;

import android.app.Activity;
import android.app.Application;
import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.fragment.app.FragmentManager;

import com.framework.core.UIFramework;
import com.navigation.UINavigatorController;

/**
 * @Author create by Zhengzelong on 2021/11/25
 * @Email : 171905184@qq.com
 * @Description :
 */
public final class UIPageControllers {
    private UIPageControllers() {
        throw new IllegalStateException("No instances!");
    }

    public static void init(@NonNull Context context) {
        final Application application;
        if (context instanceof Application) {
            application = (Application) context;
        } else {
            application = (Application) context.getApplicationContext();
        }
        application.registerActivityLifecycleCallbacks(new ActivityLifecycleCallbacks());
    }

    @Nullable
    static Context getContext(@NonNull UIPageController uiPageController) {
        final UIPageController.UIComponent uiComponent = uiPageController.getUIComponent();
        if (uiComponent instanceof Fragment) {
            return ((Fragment) uiComponent).getContext();
        }
        if (uiComponent instanceof FragmentActivity) {
            return (FragmentActivity) uiComponent;
        }
        return null;
    }

    @NonNull
    static Context requireContext(@NonNull UIPageController uiPageController) {
        final Context context = getContext(uiPageController);
        if (context == null) {
            throw new IllegalStateException("ERROR UIComponent");
        }
        return context;
    }

    @Nullable
    static FragmentActivity getFragmentActivity(@NonNull UIPageController uiPageController) {
        final UIPageController.UIComponent uiComponent = uiPageController.getUIComponent();
        if (uiComponent instanceof Fragment) {
            return ((Fragment) uiComponent).getActivity();
        }
        if (uiComponent instanceof FragmentActivity) {
            return (FragmentActivity) uiComponent;
        }
        return null;
    }

    @NonNull
    static FragmentActivity requireFragmentActivity(@NonNull UIPageController uiPageController) {
        final FragmentActivity fragmentActivity = getFragmentActivity(uiPageController);
        if (fragmentActivity == null) {
            throw new IllegalStateException("ERROR UIComponent");
        }
        return fragmentActivity;
    }

    @Nullable
    static UIPageController getUIHostController(@NonNull UIPageController uiPageController) {
        final FragmentActivity fragmentActivity = getFragmentActivity(uiPageController);
        if (fragmentActivity instanceof UIPageControllerOwner) {
            return ((UIPageControllerOwner) fragmentActivity).getUIPageController();
        }
        return null;
    }

    @NonNull
    static UIPageController requireUIHostController(@NonNull UIPageController uiPageController) {
        final UIPageController uiHostController = getUIHostController(uiPageController);
        if (uiHostController == null) {
            throw new IllegalStateException("ERROR UIComponent");
        }
        return uiHostController;
    }

    @Nullable
    static UIPageController getUIParentController(@NonNull UIPageController uiPageController) {
        final UIPageController.UIComponent uiComponent = uiPageController.getUIComponent();
        FragmentActivity fragmentActivity = null;
        if (uiComponent instanceof Fragment) {
            final Fragment childFragment = (Fragment) uiComponent;
            final Fragment parentFragment = childFragment.getParentFragment();

            if (parentFragment instanceof UIPageControllerOwner) {
                return ((UIPageControllerOwner) parentFragment).getUIPageController();
            }
            if (parentFragment != null) {
                return null;
            }
            fragmentActivity = childFragment.getActivity();
        }
        if (fragmentActivity instanceof UIPageControllerOwner) {
            return ((UIPageControllerOwner) fragmentActivity).getUIPageController();
        }
        return null;
    }

    @NonNull
    static UIPageController requireUIParentController(@NonNull UIPageController uiPageController) {
        final UIPageController uiParentController = getUIParentController(uiPageController);
        if (uiParentController == null) {
            throw new IllegalStateException("ERROR UIComponent");
        }
        return uiParentController;
    }

    @NonNull
    static FragmentManager getChildFragmentManager(@NonNull UIPageController uiPageController) {
        final UIPageController.UIComponent uiComponent = uiPageController.getUIComponent();
        if (uiComponent instanceof Fragment) {
            return ((Fragment) uiComponent).getChildFragmentManager();
        }
        if (uiComponent instanceof FragmentActivity) {
            return ((FragmentActivity) uiComponent).getSupportFragmentManager();
        }
        throw new IllegalStateException("ERROR UIComponent");
    }

    @NonNull
    static FragmentManager getParentFragmentManager(@NonNull UIPageController uiPageController) {
        final UIPageController.UIComponent uiComponent = uiPageController.getUIComponent();
        if (uiComponent instanceof Fragment) {
            return ((Fragment) uiComponent).getParentFragmentManager();
        }
        if (uiComponent instanceof FragmentActivity) {
            return ((FragmentActivity) uiComponent).getSupportFragmentManager();
        }
        throw new IllegalStateException("ERROR UIComponent");
    }

    @Nullable
    static UIActivityDispatcher getUIActivityDispatcher(@NonNull UIPageController uiPageController) {
        final FragmentActivity fragmentActivity = getFragmentActivity(uiPageController);
        if (fragmentActivity instanceof UIActivityDispatcherOwner) {
            return ((UIActivityDispatcherOwner) fragmentActivity).getUIActivityDispatcher();
        }
        return null;
    }

    @NonNull
    static UIActivityDispatcher requireUIActivityDispatcher(@NonNull UIPageController uiPageController) {
        final UIActivityDispatcher uiActivityDispatcher = getUIActivityDispatcher(uiPageController);
        if (uiActivityDispatcher == null) {
            throw new IllegalStateException("ERROR UIComponent");
        }
        return uiActivityDispatcher;
    }

    /*
     * view:
     * 1.init
     * 2.view created
     * 3.created
     * 4.restore state
     * 5.start
     * 6.resume
     * 7.save state
     * 8.pause
     * 9.stop
     * 10.destroy (destroy view)
     * */

    static void init(@NonNull Object target, @Nullable Bundle savedInstanceState) {
        if (target instanceof UIPageControllerOwner) {
            ((UIPageControllerOwner) target).getUIPageController().performInit(target, savedInstanceState);
        }
    }

    static void viewCreated(@NonNull Object target, @NonNull View view, @Nullable Bundle savedInstanceState) {
        if (target instanceof UIPageControllerOwner) {
            final UIPageController uiPageController = ((UIPageControllerOwner) target).getUIPageController();
            if (uiPageController.isCreatedView()) {
                return;
            }
            uiPageController.performViewCreated(view, savedInstanceState);
        }
    }

    static void created(@NonNull Object target, @Nullable Bundle savedInstanceState) {
        if (target instanceof UIPageControllerOwner) {
            final UIPageController uiPageController = ((UIPageControllerOwner) target).getUIPageController();
            if (uiPageController.isCreated()) {
                return;
            }
            uiPageController.performCreated(savedInstanceState);
        }
    }

    static void saveInstanceState(@NonNull Object target, @NonNull Bundle outInstanceState) {
        if (target instanceof UIPageControllerOwner) {
            ((UIPageControllerOwner) target).getUIPageController().performSaveInstanceState(outInstanceState);
        }
    }

    static void restoreInstanceState(@NonNull Object target, @NonNull Bundle savedInstanceState) {
        if (target instanceof UIPageControllerOwner) {
            ((UIPageControllerOwner) target).getUIPageController().performRestoreInstanceState(savedInstanceState);
        }
    }

    static void start(@NonNull Object target) {
        if (target instanceof UIPageControllerOwner) {
            ((UIPageControllerOwner) target).getUIPageController().performStart();
        }
    }

    static void resume(@NonNull Object target) {
        if (target instanceof UIPageControllerOwner) {
            ((UIPageControllerOwner) target).getUIPageController().performResume();
        }
    }

    static void pause(@NonNull Object target) {
        if (target instanceof UIPageControllerOwner) {
            ((UIPageControllerOwner) target).getUIPageController().performPause();
        }
    }

    static void stop(@NonNull Object target) {
        if (target instanceof UIPageControllerOwner) {
            ((UIPageControllerOwner) target).getUIPageController().performStop();
        }
    }

    static void destroy(@NonNull Object target) {
        if (target instanceof UIPageControllerOwner) {
            ((UIPageControllerOwner) target).getUIPageController().performDestroy();
        }
    }

    static void destroyView(@NonNull Object target) {
        if (target instanceof UIPageControllerOwner) {
            ((UIPageControllerOwner) target).getUIPageController().performDestroyView();
        }
    }

    static void destroyViewWhenNonUseCache(@NonNull Object target) {
        if (target instanceof UIPageControllerOwner) {
            final UIPageController uiPageController = ((UIPageControllerOwner) target).getUIPageController();
            if (uiPageController.isUseViewCache()) {
                return;
            }
            uiPageController.performDestroyView();
        }
    }

    static final class ActivityLifecycleCallbacks extends UIFramework.ActivityLifecycleCallbacks {
        @Nullable
        private FragmentLifecycleCallbacks mFragmentLifecycleCallbacks;

        @Override
        public void onActivityCreated(@NonNull Activity activity, @Nullable Bundle savedInstanceState) {
            FragmentActivity fragmentActivity = null;
            if (activity instanceof FragmentActivity) {
                if (this.mFragmentLifecycleCallbacks == null) {
                    this.mFragmentLifecycleCallbacks = new FragmentLifecycleCallbacks();
                }
                fragmentActivity = (FragmentActivity) activity;
                fragmentActivity
                        .getSupportFragmentManager()
                        .registerFragmentLifecycleCallbacks(this.mFragmentLifecycleCallbacks, false);
            }
            UIPageControllers.init(activity, savedInstanceState);

            if (fragmentActivity != null) {
                UINavigatorController.initMainRoute(fragmentActivity);
            }

            if (activity instanceof UIPageController.UIComponent) {
                final LayoutInflater inflater = activity.getLayoutInflater();
                final Window window = activity.getWindow();
                final View container = window.getDecorView();
                final View contentView = ((UIPageController.UIComponent) activity)
                        .createView(inflater, (ViewGroup) container, savedInstanceState);

                if (contentView != null) {
                    activity.setContentView(contentView);
                    UIPageControllers.viewCreated(activity, contentView, savedInstanceState);
                }
            }
            UIPageControllers.created(activity, savedInstanceState);

            if (savedInstanceState != null) {
                UIPageControllers.restoreInstanceState(activity, savedInstanceState);
            }
        }

        @Override
        public void onActivitySaveInstanceState(@NonNull Activity activity, @NonNull Bundle outInstanceState) {
            UIPageControllers.saveInstanceState(activity, outInstanceState);
        }

        @Override
        public void onActivityStarted(@NonNull Activity activity) {
            UIPageControllers.start(activity);
        }

        @Override
        public void onActivityResumed(@NonNull Activity activity) {
            UIPageControllers.resume(activity);
        }

        @Override
        public void onActivityPaused(@NonNull Activity activity) {
            UIPageControllers.pause(activity);
        }

        @Override
        public void onActivityStopped(@NonNull Activity activity) {
            UIPageControllers.stop(activity);
        }

        @Override
        public void onActivityDestroyed(@NonNull Activity activity) {
            UIPageControllers.destroyView(activity);
            UIPageControllers.destroy(activity);

            if (this.mFragmentLifecycleCallbacks == null) {
                return;
            }
            final FragmentActivity fragmentActivity = (FragmentActivity) activity;
            fragmentActivity
                    .getSupportFragmentManager()
                    .unregisterFragmentLifecycleCallbacks(this.mFragmentLifecycleCallbacks);
            this.mFragmentLifecycleCallbacks = null;
        }
    }

    static final class FragmentLifecycleCallbacks extends UIFramework.FragmentLifecycleCallbacks {
        @Nullable
        private Bundle mPendingInstanceState;
        @Nullable
        private FragmentLifecycleCallbacks mChildFragmentLifecycleCallbacks;

        @Override
        public void onFragmentCreated(@NonNull FragmentManager fragmentManager, @NonNull Fragment fragment, @Nullable Bundle savedInstanceState) {
            this.mPendingInstanceState = savedInstanceState;
            if (this.mChildFragmentLifecycleCallbacks == null) {
                this.mChildFragmentLifecycleCallbacks = new FragmentLifecycleCallbacks();
            }
            fragment.getChildFragmentManager()
                    .registerFragmentLifecycleCallbacks(this.mChildFragmentLifecycleCallbacks, false);
            UIPageControllers.init(fragment, savedInstanceState);
        }

        @Override
        public void onFragmentViewCreated(@NonNull FragmentManager fragmentManager, @NonNull Fragment fragment, @NonNull View view, @Nullable Bundle savedInstanceState) {
            final Bundle pendingInstanceState = this.mPendingInstanceState;
            if (pendingInstanceState != null) {
                savedInstanceState = pendingInstanceState;
            }
            UIPageControllers.viewCreated(fragment, view, savedInstanceState);
        }

        @Override
        public void onFragmentActivityCreated(@NonNull FragmentManager fragmentManager, @NonNull Fragment fragment, @Nullable Bundle savedInstanceState) {
            final Bundle pendingInstanceState = this.mPendingInstanceState;
            if (pendingInstanceState != null) {
                savedInstanceState = pendingInstanceState;
            }
            UIPageControllers.created(fragment, savedInstanceState);

            if (savedInstanceState != null) {
                UIPageControllers.restoreInstanceState(fragment, savedInstanceState);
            }
            this.mPendingInstanceState = null;
        }

        @Override
        public void onFragmentSaveInstanceState(@NonNull FragmentManager fragmentManager, @NonNull Fragment fragment, @NonNull Bundle outInstanceState) {
            UIPageControllers.saveInstanceState(fragment, outInstanceState);
        }

        @Override
        public void onFragmentStarted(@NonNull FragmentManager fragmentManager, @NonNull Fragment fragment) {
            UIPageControllers.start(fragment);
        }

        @Override
        public void onFragmentResumed(@NonNull FragmentManager fragmentManager, @NonNull Fragment fragment) {
            UIPageControllers.resume(fragment);
        }

        @Override
        public void onFragmentPaused(@NonNull FragmentManager fragmentManager, @NonNull Fragment fragment) {
            UIPageControllers.pause(fragment);
        }

        @Override
        public void onFragmentStopped(@NonNull FragmentManager fragmentManager, @NonNull Fragment fragment) {
            UIPageControllers.stop(fragment);
        }

        @Override
        public void onFragmentDestroyed(@NonNull FragmentManager fragmentManager, @NonNull Fragment fragment) {
            UIPageControllers.destroyView(fragment);
            UIPageControllers.destroy(fragment);

            this.mPendingInstanceState = null;
            if (this.mChildFragmentLifecycleCallbacks == null) {
                return;
            }
            fragment.getChildFragmentManager()
                    .unregisterFragmentLifecycleCallbacks(this.mChildFragmentLifecycleCallbacks);
            this.mChildFragmentLifecycleCallbacks = null;
        }

        @Override
        public void onFragmentViewDestroyed(@NonNull FragmentManager fragmentManager, @NonNull Fragment fragment) {
            UIPageControllers.destroyViewWhenNonUseCache(fragment);
        }
    }
}
