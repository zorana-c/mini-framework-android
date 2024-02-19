package com.framework.core.content;

import androidx.annotation.NonNull;
import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;

/**
 * @Author create by Zhengzelong on 2021/11/26
 * @Email : 171905184@qq.com
 * @Description :
 */
public class UIViewModelProvider extends ViewModelProvider {

    public UIViewModelProvider(@NonNull UIPageController uiPageController) {
        this(uiPageController, UIViewModelFactory.create(uiPageController));
    }

    public UIViewModelProvider(@NonNull UIPageController uiPageController, @NonNull Factory factory) {
        super(uiPageController.<UIPageController.UIComponent>getUIComponent(), factory);
    }

    public static class UIViewModelFactory extends ViewModelProvider.NewInstanceFactory {

        @NonNull
        public static UIViewModelFactory create(@NonNull UIPageController uiPageController) {
            return new UIViewModelFactory(uiPageController);
        }

        private final UIPageController mUIPageController;

        public UIViewModelFactory(@NonNull UIPageController uiPageController) {
            this.mUIPageController = uiPageController;
        }

        @NonNull
        @Override
        public <T extends ViewModel> T create(@NonNull Class<T> modelClass) {
            final T viewModel = super.create(modelClass);
            if (viewModel instanceof UIViewModel) {
                ((UIViewModel) viewModel).dispatchOnCreated(this.mUIPageController);
            }
            return viewModel;
        }
    }
}
