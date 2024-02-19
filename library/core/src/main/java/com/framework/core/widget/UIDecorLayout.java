package com.framework.core.widget;

import android.animation.Animator;
import android.animation.AnimatorInflater;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Parcel;
import android.os.Parcelable;
import android.util.AttributeSet;
import android.util.SparseArray;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.FrameLayout;

import androidx.annotation.AnimRes;
import androidx.annotation.AnimatorRes;
import androidx.annotation.CallSuper;
import androidx.annotation.IdRes;
import androidx.annotation.LayoutRes;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.view.ViewCompat;
import androidx.customview.view.AbsSavedState;

import com.framework.core.R;
import com.framework.core.listener.OnAnimationListener;
import com.framework.core.listener.OnAnimatorListener;

import java.lang.annotation.ElementType;
import java.lang.annotation.Inherited;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.lang.ref.WeakReference;
import java.util.ArrayList;

/**
 * @Author create by Zhengzelong on 2022/9/26
 * @Email : 171905184@qq.com
 * @Description :
 */
public class UIDecorLayout extends ViewGroup {
    private static final boolean DEBUG = false;
    private static final String TAG = "UIDecorLayout";

    @Inherited
    @Target(ElementType.TYPE)
    @Retention(RetentionPolicy.RUNTIME)
    @interface DecorActionView {
    }

    public static final int INVALID_DECOR = 0;
    public static final int DECOR_CONTENT = 1;
    public static final int DECOR_LOADING = 2;
    public static final int DECOR_EMPTY = 3;
    public static final int DECOR_ERROR = 4;

    private final DecorLayoutExec mDecorLayoutExec;
    private final ArrayList<View> mMatchParentChildren = new ArrayList<>();
    private final SparseArray<View> mDecoratedChildren = new SparseArray<>();
    private final DecorLayoutAnim.Callback mExitAnimCallback = this::exitAnimCompleted;
    private final DecorLayoutAnim.Callback mEnterAnimCallback = this::enterAnimCompleted;
    private ArrayList<OnLayoutChangedListener> mOnLayoutChangedListeners;

    @AnimRes
    @AnimatorRes
    private int mExitAnimation;
    @AnimRes
    @AnimatorRes
    private int mEnterAnimation;
    private int mCurrentDecorKey = INVALID_DECOR;
    private DecorLayoutRecord mDecorLayoutRecord;
    private ActionBarContainer mActionBarContainer;

    public UIDecorLayout(@NonNull Context context) {
        this(context, null);
    }

    public UIDecorLayout(@NonNull Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public UIDecorLayout(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        this.mDecorLayoutExec = new DecorLayoutExec();

        final TypedArray typedArray = context.obtainStyledAttributes(attrs, R.styleable.UIDecorLayout);
        final int preDecorKey = typedArray.getInt(R.styleable.UIDecorLayout_preDecorKey, 0);
        final int actionBar = typedArray.getResourceId(R.styleable.UIDecorLayout_actionBar, 0);
        final int emptyView = typedArray.getResourceId(R.styleable.UIDecorLayout_emptyView, 0);
        final int errorView = typedArray.getResourceId(R.styleable.UIDecorLayout_errorView, 0);
        final int contentView = typedArray.getResourceId(R.styleable.UIDecorLayout_contentView, 0);
        final int loadingView = typedArray.getResourceId(R.styleable.UIDecorLayout_loadingView, 0);
        final int exitAnimation = typedArray.getResourceId(R.styleable.UIDecorLayout_exitAnimation, 0);
        final int enterAnimation = typedArray.getResourceId(R.styleable.UIDecorLayout_enterAnimation, 0);
        typedArray.recycle();

        this.layoutBy(preDecorKey);
        this.setActionBar(actionBar);
        this.setEmptyView(emptyView);
        this.setErrorView(errorView);
        this.setContentView(contentView);
        this.setLoadingView(loadingView);
        this.setExitAnimation(exitAnimation);
        this.setEnterAnimation(enterAnimation);
    }

    @Override
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();
    }

    @Override
    protected void onDetachedFromWindow() {
        this.cancelTransaction();
        super.onDetachedFromWindow();
    }

    @Override
    public boolean checkLayoutParams(@Nullable ViewGroup.LayoutParams layoutParams) {
        return layoutParams instanceof LayoutParams;
    }

    @Override
    public LayoutParams generateDefaultLayoutParams() {
        return new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT);
    }

    @NonNull
    public LayoutParams generateLayoutParams(@NonNull View child) {
        ViewGroup.LayoutParams layoutParams;
        layoutParams = child.getLayoutParams();
        if (layoutParams == null) {
            layoutParams = this.generateDefaultLayoutParams();
        }
        if (!this.checkLayoutParams(layoutParams)) {
            layoutParams = this.generateLayoutParams(layoutParams);
        }
        return (LayoutParams) layoutParams;
    }

    @Override
    public LayoutParams generateLayoutParams(@NonNull AttributeSet attrs) {
        return new LayoutParams(this.getContext(), attrs);
    }

    @Override
    public LayoutParams generateLayoutParams(@NonNull ViewGroup.LayoutParams layoutParams) {
        if (layoutParams instanceof LayoutParams) {
            return new LayoutParams((LayoutParams) layoutParams);
        }
        if (layoutParams instanceof MarginLayoutParams) {
            return new LayoutParams((MarginLayoutParams) layoutParams);
        }
        return new LayoutParams(layoutParams);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        this.mMatchParentChildren.clear();
        int N = this.getChildCount();
        int measuredState = 0;
        int measuredWidth = 0;
        int measuredHeight = 0;
        int decorActionSize = 0;

        for (int index = 0; index < N; index++) {
            final View child = this.getChildAt(index);
            if (child == null) {
                continue;
            }
            final LayoutParams layoutParams;
            layoutParams = (LayoutParams) child.getLayoutParams();
            this.measureChildWithMargins(child,
                    widthMeasureSpec, 0,
                    heightMeasureSpec, 0);
            final int childMeasuredState = child.getMeasuredState();
            final int childMeasuredWidth = child.getMeasuredWidth()
                    + layoutParams.leftMargin
                    + layoutParams.rightMargin;
            final int childMeasuredHeight = child.getMeasuredHeight()
                    + layoutParams.topMargin
                    + layoutParams.bottomMargin;
            measuredState = combineMeasuredStates(measuredState, childMeasuredState);
            measuredWidth = Math.max(measuredWidth, childMeasuredWidth);

            if (this.isDecorActionView(child)) {
                if (decorActionSize == 0) {
                    decorActionSize = childMeasuredHeight;
                } else {
                    throw new IllegalStateException("Unknown decor action");
                }
            } else {
                measuredHeight = Math.max(measuredHeight, childMeasuredHeight);
            }
            this.mMatchParentChildren.add(child);
        }

        // Plus for decorate action size
        measuredHeight += decorActionSize;

        // Account for padding too
        measuredWidth += this.getPaddingLeft() + this.getPaddingRight();
        measuredHeight += this.getPaddingTop() + this.getPaddingBottom();

        // Check against our minimum width and height
        measuredWidth = Math.max(measuredWidth, this.getSuggestedMinimumWidth());
        measuredHeight = Math.max(measuredHeight, this.getSuggestedMinimumHeight());

        // Check against our foreground's minimum height and width
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            final Drawable drawable = this.getForeground();
            if (drawable != null) {
                measuredWidth = Math.max(measuredWidth, drawable.getMinimumWidth());
                measuredHeight = Math.max(measuredHeight, drawable.getMinimumHeight());
            }
        }

        // Set it
        this.setMeasuredDimension(
                resolveSizeAndState(measuredWidth, widthMeasureSpec, measuredState),
                resolveSizeAndState(measuredHeight, heightMeasureSpec,
                        measuredState << MEASURED_HEIGHT_STATE_SHIFT));

        N = this.mMatchParentChildren.size();
        if (N > 0) {
            for (int index = 0; index < N; index++) {
                final View child = this.mMatchParentChildren.get(index);
                if (child == null) {
                    continue;
                }
                final LayoutParams layoutParams;
                final int childWidthMeasureSpec;
                final int childHeightMeasureSpec;
                layoutParams = (LayoutParams) child.getLayoutParams();

                int widthUsed = 0;
                int heightUsed = this.isDecorActionView(child) ? 0 : decorActionSize;
                if (layoutParams.fitsParentLayouts) {
                    heightUsed = 0;
                }
                layoutParams.decorLeft = widthUsed;
                layoutParams.decorTop = heightUsed;

                if (LayoutParams.MATCH_PARENT == layoutParams.width) {
                    final int width = Math.max(0, this.getMeasuredWidth()
                            - this.getPaddingLeft()
                            - this.getPaddingRight()
                            - layoutParams.leftMargin
                            - layoutParams.rightMargin
                            - widthUsed);
                    childWidthMeasureSpec = MeasureSpec.makeMeasureSpec(width, MeasureSpec.EXACTLY);
                } else {
                    childWidthMeasureSpec = getChildMeasureSpec(widthMeasureSpec,
                            widthUsed
                                    + this.getPaddingLeft()
                                    + this.getPaddingRight()
                                    + layoutParams.leftMargin
                                    + layoutParams.rightMargin, layoutParams.width);
                }
                if (LayoutParams.MATCH_PARENT == layoutParams.height) {
                    final int height = Math.max(0, this.getMeasuredHeight()
                            - this.getPaddingTop()
                            - this.getPaddingBottom()
                            - layoutParams.topMargin
                            - layoutParams.bottomMargin
                            - heightUsed);
                    childHeightMeasureSpec = MeasureSpec.makeMeasureSpec(height, MeasureSpec.EXACTLY);
                } else {
                    childHeightMeasureSpec = getChildMeasureSpec(heightMeasureSpec,
                            heightUsed
                                    + this.getPaddingTop()
                                    + this.getPaddingBottom()
                                    + layoutParams.topMargin
                                    + layoutParams.bottomMargin, layoutParams.height);
                }
                child.measure(childWidthMeasureSpec, childHeightMeasureSpec);
            }
        }
    }

    @Override
    protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
        final int layoutTop = this.getPaddingTop();
        final int layoutLeft = this.getPaddingLeft();
        final int layoutRight = right - left - this.getPaddingRight();
        final int layoutBottom = bottom - top - this.getPaddingBottom();
        for (int index = 0; index < this.getChildCount(); index++) {
            final View child = this.getChildAt(index);
            if (child == null
                    || child.getVisibility() == View.GONE) {
                continue;
            }
            this.layoutChild(child, layoutLeft, layoutTop, layoutRight, layoutBottom);
        }
    }

    @SuppressLint("RtlHardcoded")
    protected void layoutChild(@NonNull View child, int left, int top, int right, int bottom) {
        final LayoutParams layoutParams;
        layoutParams = (LayoutParams) child.getLayoutParams();
        int gravity = layoutParams.gravity;
        if (gravity == Gravity.NO_GRAVITY) {
            gravity = LayoutParams.DEFAULT_GRAVITY;
        }
        final int measuredWidth = child.getMeasuredWidth();
        final int measuredHeight = child.getMeasuredHeight();
        final int layoutDirection = this.getLayoutDirection();
        final int absoluteGravity;
        absoluteGravity = Gravity.getAbsoluteGravity(gravity, layoutDirection);
        int layoutLeft;
        int layoutTop;
        top += layoutParams.decorTop;
        left += layoutParams.decorLeft;

        switch (absoluteGravity & Gravity.HORIZONTAL_GRAVITY_MASK) {
            case Gravity.CENTER_HORIZONTAL:
                layoutLeft = left + (right - left - measuredWidth) / 2
                        + layoutParams.leftMargin - layoutParams.rightMargin;
                break;
            case Gravity.RIGHT:
                layoutLeft = right - measuredWidth - layoutParams.rightMargin;
                break;
            case Gravity.LEFT:
            default:
                layoutLeft = left + layoutParams.leftMargin;
        }
        switch (absoluteGravity & Gravity.VERTICAL_GRAVITY_MASK) {
            case Gravity.CENTER_VERTICAL:
                layoutTop = top + (bottom - top - measuredHeight) / 2
                        + layoutParams.topMargin - layoutParams.bottomMargin;
                break;
            case Gravity.BOTTOM:
                layoutTop = bottom - measuredHeight - layoutParams.bottomMargin;
                break;
            case Gravity.TOP:
            default:
                layoutTop = top + layoutParams.topMargin;
        }
        child.layout(layoutLeft, layoutTop,
                layoutLeft + measuredWidth, layoutTop + measuredHeight);
    }

    @NonNull
    @Override
    @CallSuper
    public Parcelable onSaveInstanceState() {
        final Parcelable superState = super.onSaveInstanceState();
        final SavedState savedState = new SavedState(superState);
        final int pendingDecorKey = this.getPendingDecorKey();
        if (pendingDecorKey == INVALID_DECOR) {
            savedState.mCurrentDecorKey = this.mCurrentDecorKey;
        } else {
            savedState.mCurrentDecorKey = pendingDecorKey;
        }
        return savedState;
    }

    @Override
    @CallSuper
    public void onRestoreInstanceState(@NonNull Parcelable state) {
        if (state instanceof SavedState) {
            final SavedState savedState = (SavedState) state;
            super.onRestoreInstanceState(savedState.getSuperState());
            this.layoutBy(savedState.mCurrentDecorKey);
        } else {
            super.onRestoreInstanceState(state);
        }
    }

    public final void bringActionBarToFront() {
        if (this.mActionBarContainer != null) {
            this.mActionBarContainer.bringToFront();
        }
    }

    @Nullable
    public <V extends View> V getActionBar() {
        final ActionBarContainer actionBarContainer;
        actionBarContainer = this.mActionBarContainer;
        if (actionBarContainer == null) {
            return null;
        }
        final int N = actionBarContainer.getChildCount();
        if (N == 0) {
            return null;
        }
        return (V) actionBarContainer.getChildAt(N - 1);
    }

    @NonNull
    public final <V extends View> V requireActionBar() {
        final V actionBar = this.getActionBar();
        if (actionBar == null) {
            throw new NullPointerException("Unknown null");
        }
        return actionBar;
    }

    public void setActionBar(@LayoutRes int layoutId) {
        if (layoutId == 0) {
            return;
        }
        ActionBarContainer actionBarContainer;
        actionBarContainer = this.mActionBarContainer;
        if (actionBarContainer == null) {
            actionBarContainer = this.createActionBarContainer();
        }
        final LayoutInflater inflater;
        inflater = LayoutInflater.from(actionBarContainer.getContext());
        final View child;
        child = inflater.inflate(layoutId, actionBarContainer, false);
        this.setActionBar(child);
    }

    public void setActionBar(@Nullable View actionBar) {
        ActionBarContainer actionBarContainer;
        actionBarContainer = this.mActionBarContainer;
        if (actionBarContainer != null) {
            actionBarContainer.removeAllViews();
        }
        if (actionBar == null) {
            return;
        }
        if (actionBarContainer == null) {
            actionBarContainer = this.createActionBarContainer();
        }
        actionBarContainer.addView(actionBar);
    }

    @NonNull
    private ActionBarContainer createActionBarContainer() {
        ActionBarContainer actionBarContainer;
        actionBarContainer = this.mActionBarContainer;
        if (actionBarContainer == null) {
            actionBarContainer = new ActionBarContainer(this.getContext());
            actionBarContainer.setBackgroundColor(Color.TRANSPARENT);
            actionBarContainer.inject(this);
            this.mActionBarContainer = actionBarContainer;
        }
        return actionBarContainer;
    }

    public final void setContentView(@LayoutRes int layoutId) {
        this.addDecorView(DECOR_CONTENT, layoutId);
    }

    public final void setContentView(@Nullable View contentView) {
        this.addDecorView(DECOR_CONTENT, contentView);
    }

    public final void setLoadingView(@LayoutRes int layoutId) {
        this.addDecorView(DECOR_LOADING, layoutId);
    }

    public final void setLoadingView(@Nullable View loadingView) {
        this.addDecorView(DECOR_LOADING, loadingView);
    }

    public final void setEmptyView(@LayoutRes int layoutId) {
        this.addDecorView(DECOR_EMPTY, layoutId);
    }

    public final void setEmptyView(@Nullable View emptyView) {
        this.addDecorView(DECOR_EMPTY, emptyView);
    }

    public final void setErrorView(@LayoutRes int layoutId) {
        this.addDecorView(DECOR_ERROR, layoutId);
    }

    public final void setErrorView(@Nullable View errorView) {
        this.addDecorView(DECOR_ERROR, errorView);
    }

    public void addDecorView(int decorKey, @LayoutRes int layoutId) {
        if (layoutId == 0) {
            return;
        }
        final LayoutInflater inflater;
        inflater = LayoutInflater.from(this.getContext());
        final View child;
        child = inflater.inflate(layoutId, this, false);
        this.addDecorView(decorKey, child);
    }

    public void addDecorView(int decorKey, @Nullable View child) {
        this.setDecorView(decorKey, child);
    }

    private void setDecorView(int decorKey, @Nullable View child) {
        final View oldChild;
        oldChild = this.mDecoratedChildren.get(decorKey);
        if (oldChild == child) {
            return;
        }
        if (oldChild != null) {
            this.clearDecorLayoutAnim(oldChild);
            this.removeView(oldChild);
        }
        this.mDecoratedChildren.put(decorKey, child);
        if (child == null) {
            return;
        }
        final LayoutParams layoutParams;
        layoutParams = this.generateLayoutParams(child);
        layoutParams.gravity = Gravity.CENTER;
        layoutParams.decorKey = decorKey;
        child.setLayoutParams(layoutParams);
        child.setVisibility(View.GONE);

        if (DECOR_CONTENT == decorKey) {
            this.addView(child, 0);
        } else if (this.mCurrentDecorKey == decorKey) {
            this.addView(child);
        }
        this.bringActionBarToFront();

        if (this.mCurrentDecorKey == decorKey) {
            child.setVisibility(View.VISIBLE);
        } else if (DECOR_CONTENT == decorKey) {
            child.setVisibility(View.INVISIBLE);
        }
    }

    public boolean containsBy(int decorKey) {
        return this.mDecoratedChildren.get(decorKey) != null;
    }

    public void removeDecorView(@NonNull View child) {
        final int decorKey;
        decorKey = this.getChildDecorKey(child);
        if (decorKey == INVALID_DECOR) {
            this.removeView(child);
            return;
        }
        this.removeDecorViewByKey(decorKey);
    }

    public void removeDecorViewByKey(int decorKey) {
        this.setDecorView(decorKey, null);
    }

    @Nullable
    public <V extends View> V findDecorViewByKey(int decorKey) {
        return (V) this.mDecoratedChildren.get(decorKey);
    }

    @NonNull
    public final <V extends View> V requireDecorViewByKey(int decorKey) {
        final V child = this.findDecorViewByKey(decorKey);
        if (child == null) {
            throw new NullPointerException("Unknown null");
        }
        return child;
    }

    @Nullable
    public <V extends View> V findViewTraversalBy(@IdRes int id) {
        View child;
        child = this.findViewById(id);
        if (child == null) {
            final int N = this.mDecoratedChildren.size();
            for (int index = 0; index < N; index++) {
                final View decorChild;
                decorChild = this.mDecoratedChildren.valueAt(index);
                child = decorChild.findViewById(id);

                if (child != null) {
                    break;
                }
            }
        }
        return (V) child;
    }

    public final boolean getFitsParentLayouts(int decorKey) {
        final View child;
        child = this.findDecorViewByKey(decorKey);
        if (child == null) {
            return false;
        }
        return this.getFitsParentLayouts(child);
    }

    public boolean getFitsParentLayouts(@NonNull View child) {
        final LayoutParams layoutParams;
        layoutParams = (LayoutParams) child.getLayoutParams();
        return layoutParams.fitsParentLayouts;
    }

    public final void fitsParentLayouts(int decorKey) {
        this.setFitsParentLayouts(decorKey, true);
    }

    public final void setFitsParentLayouts(int decorKey, boolean fitsParentLayouts) {
        final View child;
        child = this.findDecorViewByKey(decorKey);
        if (child == null) {
            return;
        }
        this.setFitsParentLayouts(child, fitsParentLayouts);
    }

    public void setFitsParentLayouts(@NonNull View child, boolean fitsParentLayouts) {
        final LayoutParams layoutParams;
        layoutParams = this.generateLayoutParams(child);
        layoutParams.fitsParentLayouts = fitsParentLayouts;
        child.setLayoutParams(layoutParams);
    }

    public final void layoutBy(int decorKey) {
        this.beginTransaction(decorKey)
                .setEnterAnimation(0)
                .setExitAnimation(0)
                .commitNow();
    }

    public void layoutOnAnimationBy(int decorKey) {
        this.beginTransaction(decorKey).commitNow();
    }

    public final void postLayoutBy(int decorKey) {
        this.postLayoutBy(decorKey, 0L);
    }

    public void postLayoutBy(int decorKey, long delayedMillis) {
        this.beginTransaction(decorKey)
                .setEnterAnimation(0)
                .setExitAnimation(0)
                .commit(delayedMillis);
    }

    public final void postLayoutOnAnimationBy(int decorKey) {
        this.postLayoutOnAnimationBy(decorKey, 0L);
    }

    public void postLayoutOnAnimationBy(int decorKey, long delayedMillis) {
        this.beginTransaction(decorKey).commit(delayedMillis);
    }

    @NonNull
    public final DecorLayoutRecord beginTransaction() {
        return this.beginTransaction(INVALID_DECOR);
    }

    @NonNull
    public DecorLayoutRecord beginTransaction(int decorKey) {
        return new DecorLayoutRecord(this)
                .setDecorKey(decorKey)
                .setExitAnimation(this.mExitAnimation)
                .setEnterAnimation(this.mEnterAnimation);
    }

    public final int cancelTransaction() {
        final DecorLayoutRecord decorLayoutRecord;
        synchronized (UIDecorLayout.class) {
            decorLayoutRecord = this.mDecorLayoutRecord;
            this.removeCallbacks(this.mDecorLayoutExec);
            this.mDecorLayoutRecord = null;
        }
        if (decorLayoutRecord == null) {
            return INVALID_DECOR;
        }
        return decorLayoutRecord.decorKey;
    }

    public int getChildDecorKey(@Nullable View child) {
        if (child == null) {
            return INVALID_DECOR;
        }
        final LayoutParams layoutParams;
        layoutParams = (LayoutParams) child.getLayoutParams();
        return layoutParams.decorKey;
    }

    public int getCurrentDecorKey() {
        return this.mCurrentDecorKey;
    }

    public int getPendingDecorKey() {
        final DecorLayoutRecord decorLayoutRecord;
        synchronized (UIDecorLayout.class) {
            decorLayoutRecord = this.mDecorLayoutRecord;
        }
        if (decorLayoutRecord == null) {
            return INVALID_DECOR;
        }
        return decorLayoutRecord.decorKey;
    }

    @AnimRes
    @AnimatorRes
    public int getExitAnimation() {
        return this.mExitAnimation;
    }

    public void setExitAnimation(@AnimRes @AnimatorRes int animId) {
        this.mExitAnimation = animId;
    }

    @AnimRes
    @AnimatorRes
    public int getEnterAnimation() {
        return this.mEnterAnimation;
    }

    public void setEnterAnimation(@AnimRes @AnimatorRes int animId) {
        this.mEnterAnimation = animId;
    }

    public void addOnLayoutChangedListener(@NonNull OnLayoutChangedListener listener) {
        if (this.mOnLayoutChangedListeners == null) {
            this.mOnLayoutChangedListeners = new ArrayList<>();
        }
        this.mOnLayoutChangedListeners.add(listener);
    }

    public void removeOnLayoutChangedListener(@NonNull OnLayoutChangedListener listener) {
        if (this.mOnLayoutChangedListeners != null) {
            this.mOnLayoutChangedListeners.remove(listener);
        }
    }

    private void execSingleAction(@NonNull DecorLayoutRecord decorLayoutRecord,
                                  long delayedMillis /* ms */) {
        synchronized (UIDecorLayout.class) {
            this.mDecorLayoutRecord = decorLayoutRecord;
            this.removeCallbacks(this.mDecorLayoutExec);
            this.postOnAnimationDelayed(this.mDecorLayoutExec, delayedMillis);
        }
    }

    private void execSingleActionNow(@NonNull DecorLayoutRecord decorLayoutRecord) {
        synchronized (UIDecorLayout.class) {
            this.mDecorLayoutRecord = decorLayoutRecord;
            this.removeCallbacks(this.mDecorLayoutExec);
            this.executeOpsTogether();
        }
    }

    private void executeOpsTogether() {
        final DecorLayoutRecord decorLayoutRecord;
        synchronized (UIDecorLayout.class) {
            decorLayoutRecord = this.mDecorLayoutRecord;
            this.removeCallbacks(this.mDecorLayoutExec);
            this.mDecorLayoutRecord = null;
        }
        if (decorLayoutRecord != null) {
            this.execTransaction(decorLayoutRecord);
        }
    }

    private void execTransaction(@NonNull DecorLayoutRecord decorLayoutRecord) {
        final int newDecorKey = decorLayoutRecord.decorKey;
        final int oldDecorKey = this.mCurrentDecorKey;
        if (oldDecorKey == newDecorKey) {
            return;
        }
        if (newDecorKey == INVALID_DECOR) {
            return;
        }
        View child;
        child = this.findDecorViewByKey(oldDecorKey);
        if (child != null) {
            this.clearDecorLayoutAnim(child);

            final DecorLayoutAnim decorLayoutAnim;
            decorLayoutAnim = this.createAnim(oldDecorKey, decorLayoutRecord.exitAnimation);
            decorLayoutAnim.setCallback(this.mExitAnimCallback);
            decorLayoutAnim.setTarget(child);
            decorLayoutAnim.start();
        }
        this.mCurrentDecorKey = newDecorKey;
        child = this.findDecorViewByKey(newDecorKey);
        if (child != null) {
            this.clearDecorLayoutAnim(child);

            if (child.getParent() == null) {
                this.addView(child);
            }
            child.setVisibility(View.VISIBLE);

            final DecorLayoutAnim decorLayoutAnim;
            decorLayoutAnim = this.createAnim(newDecorKey, decorLayoutRecord.enterAnimation);
            decorLayoutAnim.setCallback(this.mEnterAnimCallback);
            decorLayoutAnim.setTarget(child);
            decorLayoutAnim.start();

            this.bringActionBarToFront();
        }
        this.notifyOnLayoutChangedListener(newDecorKey);
    }

    private void enterAnimCompleted(@NonNull DecorLayoutAnim decorLayoutAnim) {
        final View child = decorLayoutAnim.getTarget();
        if (child == null) {
            return;
        }
        this.onDecorChildEnter(child);
    }

    private void exitAnimCompleted(@NonNull DecorLayoutAnim decorLayoutAnim) {
        final View child = decorLayoutAnim.getTarget();
        if (child == null) {
            return;
        }
        this.onDecorChildExit(child);
    }

    @CallSuper
    protected void onDecorChildEnter(@NonNull View child) {
        // nothing
    }

    @CallSuper
    protected void onDecorChildExit(@NonNull View child) {
        final int decorKey = this.getChildDecorKey(child);
        if (DECOR_CONTENT == decorKey) {
            child.setVisibility(View.INVISIBLE);
        } else {
            child.setVisibility(View.GONE);
            this.removeView(child);
        }
    }

    @NonNull
    private DecorLayoutAnim createAnim(int decorKey,
                                       @AnimatorRes @AnimRes int animId) {
        final Animation animation = this.onCreateAnimation(decorKey, animId);
        if (animation != null) {
            return new DecorLayoutAnim(animation);
        }
        final Animator animator = this.onCreateAnimator(decorKey, animId);
        if (animator != null) {
            return new DecorLayoutAnim(animator);
        }
        return new DecorLayoutAnim();
    }

    @Nullable
    protected Animator onCreateAnimator(int decorKey,
                                        @AnimatorRes int animator) {
        try {
            return AnimatorInflater.loadAnimator(this.getContext(), animator);
        } catch (Exception e) {
            return null;
        }
    }

    @Nullable
    protected Animation onCreateAnimation(int decorKey,
                                          @AnimRes int animation) {
        try {
            return AnimationUtils.loadAnimation(this.getContext(), animation);
        } catch (Exception e) {
            return null;
        }
    }

    public void clearDecorLayoutAnim(@NonNull View child) {
        final DecorLayoutAnim decorLayoutAnim = DecorLayoutAnim.obtain(child);
        if (decorLayoutAnim != null) {
            decorLayoutAnim.cancel();
        }
    }

    private boolean isDecorActionView(@NonNull View child) {
        final Class<View> childClass = (Class<View>) child.getClass();
        final DecorActionView decorActionView;
        decorActionView = childClass.getAnnotation(DecorActionView.class);
        return decorActionView != null;
    }

    private void notifyOnLayoutChangedListener(int decorLayoutKey) {
        if (this.mOnLayoutChangedListeners == null) {
            return;
        }
        for (OnLayoutChangedListener listener : this.mOnLayoutChangedListeners) {
            listener.onLayoutChanged(this, decorLayoutKey);
        }
    }

    static void postOnAnimation(@NonNull View target, @NonNull Runnable action) {
        ViewCompat.postOnAnimation(target, action);
    }

    static void postOnAnimationDelayed(@NonNull View target,
                                       @NonNull Runnable action, long delayMillis) {
        ViewCompat.postOnAnimationDelayed(target, action, delayMillis);
    }

    private final class DecorLayoutExec implements Runnable {
        @Override
        public void run() {
            UIDecorLayout.this.executeOpsTogether();
        }
    }

    private static class DecorLayoutAnim implements Runnable {
        private static final int ANIM_ID = 2 << 24;

        @Nullable
        static DecorLayoutAnim obtain(@NonNull View child) {
            return (DecorLayoutAnim) child.getTag(ANIM_ID);
        }

        @Nullable
        private Callback callback;
        @Nullable
        private Animator animator;
        @Nullable
        private Animation animation;
        @Nullable
        private WeakReference<View> target;

        public DecorLayoutAnim() {
        }

        public DecorLayoutAnim(@Nullable Animator animator) {
            this.animator = animator;
        }

        public DecorLayoutAnim(@Nullable Animation animation) {
            this.animation = animation;
        }

        @Override
        public void run() {
            final View target;
            synchronized (UIDecorLayout.class) {
                target = this.getTarget();
            }
            if (target != null) {
                this.performAnimCompleted(target);
            }
        }

        @Nullable
        public <T extends View> T getTarget() {
            if (this.target == null) {
                return null;
            }
            return (T) this.target.get();
        }

        public void setTarget(@NonNull View target) {
            this.target = new WeakReference<>(target);
        }

        public void setCallback(@Nullable Callback callback) {
            this.callback = callback;
        }

        public void start() {
            final View target = this.getTarget();
            if (target != null) {
                target.setTag(ANIM_ID, this);
            } else throw new NullPointerException("Unknown error");

            // wait delay millis.
            long duration = 0L;

            // or Animator.
            final Animator animator = this.animator;
            if (animator != null) {
                duration = animator.getDuration();
                this.startAnimator(target, animator);
            }

            // or Animation.
            final Animation animation = this.animation;
            if (animation != null) {
                duration = animation.getDuration();
                this.startAnimation(target, animation);
            }
            // begin.
            this.startInner(duration);
        }

        public synchronized void cancel() {
            this.callback = null;
            if (this.animator != null) {
                this.animator.cancel();
            }
            if (this.animation != null) {
                this.animation.cancel();
            }
        }

        private void startAnimator(@NonNull View target,
                                   @NonNull Animator animator) {
            animator.addListener(new OnAnimatorListener() {
                @Override
                public void onAnimationEnd(@NonNull Animator animator) {
                    animator.removeListener(this);
                    startInner(0L);
                }
            });
            animator.setTarget(target);
            animator.start();
        }

        private void startAnimation(@NonNull View target,
                                    @NonNull Animation animation) {
            animation.setAnimationListener(new OnAnimationListener() {
                @Override
                public void onAnimationEnd(@NonNull Animation animation) {
                    animation.setAnimationListener(null);
                    startInner(0L);
                }
            });
            target.startAnimation(animation);
        }

        private void startInner(long delayMillis) {
            final View target;
            synchronized (UIDecorLayout.class) {
                target = this.getTarget();
            }
            if (target == null) {
                return;
            }
            target.removeCallbacks(this);
            if (delayMillis == 0L) {
                postOnAnimation(target, this);
            } else {
                postOnAnimationDelayed(target, this, delayMillis);
            }
        }

        private void performAnimCompleted(@NonNull View target) {
            final Callback callback;
            synchronized (UIDecorLayout.class) {
                callback = this.callback;
                // done clear it.
                target.setTag(ANIM_ID, null);
                // done remove it.
                target.removeCallbacks(this);
                // done release it.
                this.callback = null;
            }
            if (callback != null) {
                callback.onCompleted(this);
            }
        }

        interface Callback {

            void onCompleted(@NonNull DecorLayoutAnim decorLayoutAnim);
        }
    }

    @DecorActionView
    private static final class ActionBarContainer extends FrameLayout {

        public ActionBarContainer(@NonNull Context context) {
            super(context);
        }

        public void inject(@NonNull UIDecorLayout parent) {
            final UIDecorLayout.LayoutParams layoutParams;
            layoutParams = parent.generateDefaultLayoutParams();
            layoutParams.width = ViewGroup.LayoutParams.MATCH_PARENT;
            layoutParams.height = ViewGroup.LayoutParams.WRAP_CONTENT;
            parent.addView(this, layoutParams);
        }
    }

    public static final class DecorLayoutRecord {
        private final UIDecorLayout uiDecorLayout;

        private int decorKey;
        @AnimRes
        @AnimatorRes
        private int exitAnimation;
        @AnimRes
        @AnimatorRes
        private int enterAnimation;

        DecorLayoutRecord(@NonNull UIDecorLayout uiDecorLayout) {
            this.uiDecorLayout = uiDecorLayout;
        }

        @NonNull
        public DecorLayoutRecord setDecorKey(int decorKey) {
            this.decorKey = decorKey;
            return this;
        }

        @NonNull
        public DecorLayoutRecord setExitAnimation(@AnimRes
                                                  @AnimatorRes int exitAnimation) {
            this.exitAnimation = exitAnimation;
            return this;
        }

        @NonNull
        public DecorLayoutRecord setEnterAnimation(@AnimRes
                                                   @AnimatorRes int enterAnimation) {
            this.enterAnimation = enterAnimation;
            return this;
        }

        public void commit() {
            this.uiDecorLayout.execSingleAction(this, 0L);
        }

        public void commit(long delayedMillis) {
            this.uiDecorLayout.execSingleAction(this, delayedMillis);
        }

        public void commitNow() {
            this.uiDecorLayout.execSingleActionNow(this);
        }
    }

    public static class LayoutParams extends ViewGroup.MarginLayoutParams {
        public static final int DEFAULT_GRAVITY = Gravity.START | Gravity.TOP;

        public int gravity = Gravity.NO_GRAVITY;
        private int decorKey = INVALID_DECOR;
        private int decorTop;
        private int decorLeft;
        private boolean fitsParentLayouts;

        public LayoutParams(int width, int height) {
            super(width, height);
        }

        public LayoutParams(@NonNull LayoutParams layoutParams) {
            super(layoutParams);
            this.gravity = layoutParams.gravity;
            this.decorKey = layoutParams.decorKey;
            this.decorTop = layoutParams.decorTop;
            this.decorLeft = layoutParams.decorLeft;
            this.fitsParentLayouts = layoutParams.fitsParentLayouts;
        }

        public LayoutParams(@NonNull MarginLayoutParams layoutParams) {
            super(layoutParams);
        }

        public LayoutParams(@NonNull ViewGroup.LayoutParams layoutParams) {
            super(layoutParams);
        }

        public LayoutParams(@NonNull Context context, @NonNull AttributeSet attrs) {
            super(context, attrs);
            final TypedArray typedArray;
            typedArray = context.obtainStyledAttributes(attrs, R.styleable.UIDecorLayout_Layout);
            this.gravity = typedArray.getInt(R.styleable.UIDecorLayout_Layout_android_layout_gravity, this.gravity);
            this.fitsParentLayouts =
                    typedArray.getBoolean(R.styleable.UIDecorLayout_Layout_fitsParentLayouts, this.fitsParentLayouts);
            typedArray.recycle();
        }
    }

    public static class SavedState extends AbsSavedState {
        private int mCurrentDecorKey;

        public SavedState(@NonNull Parcelable superState) {
            super(superState);
            this.mCurrentDecorKey = INVALID_DECOR;
        }

        public SavedState(@NonNull Parcel in, @Nullable ClassLoader loader) {
            super(in, loader);
            this.mCurrentDecorKey = in.readInt();
        }

        @Override
        public void writeToParcel(@NonNull Parcel out, int flags) {
            super.writeToParcel(out, flags);
            out.writeInt(this.mCurrentDecorKey);
        }

        @NonNull
        public static final Creator<SavedState> CREATOR = new ClassLoaderCreator<SavedState>() {
            @Override
            public SavedState createFromParcel(@NonNull Parcel in, @Nullable ClassLoader classLoader) {
                return new SavedState(in, classLoader);
            }

            @Override
            public SavedState createFromParcel(@NonNull Parcel in) {
                return new SavedState(in, null);
            }

            @Override
            public SavedState[] newArray(int size) {
                return new SavedState[size];
            }
        };
    }

    public interface OnLayoutChangedListener {

        void onLayoutChanged(@NonNull UIDecorLayout uiDecorLayout, int decorKey);
    }
}
