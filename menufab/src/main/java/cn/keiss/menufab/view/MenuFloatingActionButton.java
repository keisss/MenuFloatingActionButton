package cn.keiss.menufab.view;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.content.Context;
import android.content.res.ColorStateList;
import android.content.res.TypedArray;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.support.design.widget.FloatingActionButton;
import android.util.AttributeSet;
import android.util.Log;
import android.util.TypedValue;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.BounceInterpolator;
import android.view.animation.LinearInterpolator;

import java.util.ArrayList;
import java.util.List;

import cn.keiss.menufab.R;
import cn.keiss.menufab.listener.MenuItemClickListener;
import cn.keiss.menufab.listener.OnFloatActionButtonClickListener;
import cn.keiss.menufab.listener.OnMenuItemClickListener;


/**
 * Created by hekai on 2017/10/13.
 * MenuFloatActionButton View的类
 */

public class MenuFloatingActionButton extends ViewGroup{
    //TODO 添加item List
    private List<MenuView> mMenuViews = new ArrayList<>();
    private View mBackView;
    private FloatingActionButton mFab;


    //菜单打开关闭的状态 true打开
    private boolean menuStatus;

    private ColorStateList mFabBackgroundColor;
    private Drawable mFabSrc;
    private int mAnimationDuration;
    private float mFabRotateVal = 45F;
    private int mBackgroundColor;

    private OnFloatActionButtonClickListener mFabClickListener;
    private OnMenuItemClickListener mMenuItemClickListener;


    public MenuFloatingActionButton(Context context) {
        super(context);
    }

    public MenuFloatingActionButton(Context context, AttributeSet attrs) {
        this(context, attrs,0);

    }

    public MenuFloatingActionButton(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        getAttrs(context,attrs);
        setViews(context);
    }



    private void getAttrs(Context context, AttributeSet attributeSet){
        TypedArray typedArray = context.obtainStyledAttributes(attributeSet, R.styleable.MenuFloatingActionButton);
        mBackgroundColor = typedArray.getColor(R.styleable.MenuFloatingActionButton_background_color,Color.WHITE);
        mFabBackgroundColor = typedArray.getColorStateList(R.styleable.MenuFloatingActionButton_fab_background_color);
        mFabSrc = typedArray.getDrawable(R.styleable.MenuFloatingActionButton_fab_src);
        mAnimationDuration = typedArray.getInt(R.styleable.MenuFloatingActionButton_animator_duration,150);
        typedArray.recycle();
    }

    private void setViews(Context context){
        mBackView = new View(context);
        mBackView.setBackgroundColor(mBackgroundColor);
        mBackView.setAlpha(0);
        addView(mBackView);

        mFab = new FloatingActionButton(context);
        mFab.setBackgroundTintList(mFabBackgroundColor);
        mFab.setImageDrawable(mFabSrc);
        addView(mFab);



    }

    @Override
    public ViewGroup.LayoutParams generateLayoutParams(AttributeSet attrs)
    {
        return new MarginLayoutParams(getContext(), attrs);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int childCount = getChildCount();
        for (int i =0;i<childCount;i++){
            measureChild(getChildAt(i),widthMeasureSpec,heightMeasureSpec);
        }

        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
    }

    @Override
    protected void onLayout(boolean b, int i, int i1, int i2, int i3) {


        if (b){
            layoutFab();
            layoutBackView();
            layoutChild();
        }

    }

    private void layoutFab(){
        int l = px2Dp(16);
        int t = px2Dp(16);
        int fabWidth = mFab.getMeasuredWidth();
        int fabHeight = mFab.getMeasuredHeight();

        t = getMeasuredHeight()- fabHeight -t;
        l = getMeasuredWidth()-fabWidth-l;

        mFab.layout(l,t,l+fabWidth,t+fabHeight);
        setFabListener();
    }

    private void layoutBackView(){
        mBackView.layout(0,0,getMeasuredWidth(),getMeasuredHeight());
    }

    private void layoutChild(){
        int childCount = getChildCount() ;
        int center = mFab.getRight() -(mFab.getRight()-mFab.getLeft())/2;
        for (int i=2;i<childCount;i++){
            MenuView menuView = (MenuView) getChildAt(i);

            menuView.setVisibility(INVISIBLE);

            int childHeight = menuView.getMeasuredHeight();
            int childWidth = menuView.getMeasuredWidth();
            int fabHeight = mFab.getMeasuredHeight();
            int l; int t ;
            t = getMeasuredHeight() -( fabHeight +childHeight*(i-1))-px2Dp(24);
            l = getMeasuredWidth()-childWidth -px2Dp(16);
            menuView.setCenterOfMainFabLeft(center);
            menuView.layout(l,t,l+childWidth,t+childHeight);
            setMenuItemClickListener(menuView,i);
            setAnimStart(menuView);
        }

    }

    //为fab绑定点击事件
    private void setFabListener(){
        mFab.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                if (menuStatus){
                    closeMenu();
                    hideBackView();
                    rotateFabSrc();
                    changeMenuStatus();
                }else {
                    openMenu();
                    showBackView();
                    rotateFabSrc();
                    changeMenuStatus();
                }
                if (mFabClickListener !=null){
                    mFabClickListener.onClick();
                }

            }

        });


    }

    //为item设置点击事件
    private void setMenuItemClickListener(final MenuView view, final int position){
        view.setMenuItemClickListener(new MenuItemClickListener() {
            @Override
            public void onClick() {
                closeMenu();
                hideBackView();
                rotateFabSrc();
                changeMenuStatus();
                if (null !=mMenuItemClickListener){
                    mMenuItemClickListener.onClick(view,position);
                }
            }
        });

    }

    //设置动画起始位置
    private void setAnimStart(MenuView view){
        view.setTranslationY(50);
    }


    private void rotateFabSrc(){
        ObjectAnimator animator = menuStatus ? ObjectAnimator.ofFloat(mFab,"rotation", mFabRotateVal,0f)
                : ObjectAnimator.ofFloat(mFab,"rotation",0f, mFabRotateVal);
        animator.setDuration(mAnimationDuration);
        animator.setInterpolator(new BounceInterpolator());
        animator.start();
    }

    private void openMenu(){
        for (int i=2;i<getChildCount();i++){
            View view = getChildAt(i);
            view.setVisibility(VISIBLE);
            ObjectAnimator yAnimator = ObjectAnimator.ofFloat(view,"translationY",50f,0f);
            ObjectAnimator alphaAnimator = ObjectAnimator.ofFloat(view,"alpha",0f,1f);
            AnimatorSet animatorSet = new AnimatorSet();
            animatorSet.playTogether(yAnimator,alphaAnimator);
            animatorSet.setDuration(mAnimationDuration);
            animatorSet.setInterpolator(new BounceInterpolator());
            animatorSet.start();
        }
    }
    private void closeMenu(){
        for (int i=2;i<getChildCount();i++){
            final View view = getChildAt(i);
            ObjectAnimator yAnimator = ObjectAnimator.ofFloat(view,"translationY",0f,50f);
            ObjectAnimator alphaAnimator = ObjectAnimator.ofFloat(view,"alpha",1f,0f);
            AnimatorSet animatorSet = new AnimatorSet();
            animatorSet.playTogether(yAnimator,alphaAnimator);
            animatorSet.setDuration(mAnimationDuration);
            animatorSet.setInterpolator(new BounceInterpolator());
            animatorSet.start();
            animatorSet.addListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    view.setVisibility(INVISIBLE);
                }
            });
        }
    }

    private void showBackView(){
        ObjectAnimator alpha = ObjectAnimator.ofFloat(mBackView,"alpha",0f,0.8f);
        alpha.setDuration(mAnimationDuration);
        alpha.setInterpolator(new LinearInterpolator());
        alpha.start();

    }
    private void hideBackView(){
        ObjectAnimator alpha = ObjectAnimator.ofFloat(mBackView,"alpha",0.8f,0f);
        alpha.setDuration(mAnimationDuration);
        alpha.setInterpolator(new LinearInterpolator());
        alpha.start();
    }

    private void changeMenuStatus(){
        menuStatus = !menuStatus;
    }
    /**
     * 设置菜单项
     * @param menuItemViews 菜单item的List
     */
    public void setMenuItemViews(List<MenuView> menuItemViews){
        try {
            this.mMenuViews = menuItemViews;
            invalidate();
        }catch (NullPointerException o){
            Log.e("传入List为null",o.toString());
        }

    }


    /**
     * 设置fab的点击监听
     * @param listener fab点击监听
     */
    public void setOnFabClickListener(OnFloatActionButtonClickListener listener){
        this.mFabClickListener = listener;
    }

    /**
     * 设置菜单item的点击事件，
     * 也可以通过获取各个item的对象使用
     * setMenuItemClickListener(MenuItemClickListener listener)
     * 进行对单个item点击进行监听
     * @param listener item点击监听
     */
    public void setOnMenuItemClickListener(OnMenuItemClickListener listener){
        this.mMenuItemClickListener = listener;
    }


    /**
     * 设置fab旋转角度
     * @param rotateVal 角度float，默认45f
     */
    public void setFabRotateVal(float rotateVal){
        this.mFabRotateVal = rotateVal;
    }

    /**
     * 设置打开菜单背景颜色
     * @param color 默认白色
     */
    public void setBackViewColor(int color){
        this.mBackgroundColor = color;
    }

    /**
     * 设置动画时长
     * @param duration 默认150
     */
    public void setAnimationDuration(int duration){
        this.mAnimationDuration = duration;
    }

    /**
     * 设置fab颜色
     * @param color 在xml中fab_background_color
     */
    public void setFabColor(ColorStateList color){
        this.mFabBackgroundColor = color;
    }

    /**
     *  设置fab的src
     * @param drawable 旋转的内容
     */
    public void setFabImageRes(Drawable drawable){
        this.mFabSrc = drawable;
    }


    //将px转dp
    private int px2Dp(int value) {
        if (Build.VERSION.SDK_INT > Build.VERSION_CODES.LOLLIPOP) {
            return (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, value, getResources().getDisplayMetrics());
        } else {
            return 0;
        }
    }
}
