package cn.keiss.menufab.view;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.util.AttributeSet;
import android.util.Log;
import android.util.TypedValue;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import cn.keiss.menufab.R;
import cn.keiss.menufab.listener.MenuItemClickListener;

import static cn.keiss.menufab.fields.Finals.ON_LEFT;
import static cn.keiss.menufab.fields.Finals.ON_RIGHT;


/**
 * Created by hekai on 2017/10/13.
 * 菜单item的类
 */

public class MenuView extends ViewGroup {
    private TextView tvMenuText;
    private ImageView ivMenuIcon;

    private String mItemText;
    private Drawable mItemIcon;


    private int lRPosition = ON_LEFT;

    private MenuItemClickListener onClickListener;

    public MenuView(Context context) {
        super(context);
    }

    public MenuView(Context context, AttributeSet attrs) {
        this(context, attrs,0);
    }

    public MenuView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        getAttrs(context,attrs);
        setViews(context);
    }

    private void getAttrs(Context context, AttributeSet attributeSet){
        TypedArray typedArray = context.obtainStyledAttributes(attributeSet, R.styleable.MenuView);
        mItemText = typedArray.getString(R.styleable.MenuView_item_text);
        mItemIcon = typedArray.getDrawable(R.styleable.MenuView_item_icon);
        typedArray.recycle();
    }

    private void setViews(Context context){
        tvMenuText = new TextView(context);
        ivMenuIcon = new ImageView(context);
        tvMenuText.setText(mItemText);
        ivMenuIcon.setImageDrawable(mItemIcon);
        addView(tvMenuText);
        addView(ivMenuIcon);
    }

    @Override
    protected void onLayout(boolean b, int i, int i1, int i2, int i3) {



        if (b){
            View textView = getChildAt(0);
            View imageView = getChildAt(1);

            int tvWidth = textView.getMeasuredWidth();
            int tvHeight =textView.getMeasuredHeight();
            int ivWidth = imageView.getMeasuredWidth();
            int ivHeight = imageView.getMeasuredHeight();


            int tvl = 0; int tvt = 0; int tvr = 0; int tvb = 0;
            int ivl = 0; int ivt = 0; int ivr = 0; int ivb = 0;
            switch (lRPosition){
                case ON_LEFT:
                    ivl = tvWidth + px2Dp(16);
                    ivr = ivl+ ivWidth;
                    ivb = ivHeight;
                    tvl = px2Dp(8);
                    tvr = tvl+tvWidth;
                    tvt = (ivHeight - tvHeight) /2;
                    tvb = tvt + tvHeight;
                    break;
                case ON_RIGHT:
                    ivl = px2Dp(8);
                    ivr = ivl+ivWidth;
                    ivb = ivHeight;
                    tvl = ivr+px2Dp(8);
                    tvr = tvl+tvWidth;
                    tvt = (ivHeight - tvHeight) /2;
                    tvb = tvt+tvHeight;
                    break;
            }
            textView.layout(tvl,tvt,tvr,tvb);
            imageView.layout(ivl,ivt,ivr,ivb);
            setListener();
        }

    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int ChildCount = getChildCount();

        for (int i = 0; i < ChildCount; i++) {
            measureChild(getChildAt(i), widthMeasureSpec, heightMeasureSpec);
        }

        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        int groupWidth = getChildAt(0).getMeasuredWidth() + getChildAt(1).getMeasuredWidth() + px2Dp(24 + 8 + 8);
        int groupHeight = Math.max(getChildAt(0).getMeasuredHeight(), getChildAt(1).getMeasuredHeight())+px2Dp(12);
        ViewGroup.LayoutParams params =  getLayoutParams();
        params.width = groupWidth;
        params.height = groupHeight;
        setLayoutParams(params);

    }

    /**
     * 给打开的菜单item设置文字，在xml用itemText设置
     * @param text item文字
     */
    public void setMenuItemText(String text){
        try {
            tvMenuText.setText(text);
        }catch(NullPointerException o)
        {
            Log.e("设置的菜单String为null",o.toString());
        }

    }

    /**
     * 给菜单item设置icon
     * @param drawable item图片资源
     */
    public void setMenuItemIcon(Drawable drawable){
        try {
            ivMenuIcon.setImageDrawable(drawable);
        }catch (NullPointerException o){
            Log.e("设置的菜单item的Drawable为null",o.toString());
        }

    }

    /**
     * 获取菜单item的textView
     * @return textView
     */
    public TextView getMenuItemTextView(){
        return tvMenuText;
    }

    /**
     * 获取菜单item的imageView
     * @return imageView
     */
    public ImageView getMenuItemImageView(){
        return ivMenuIcon;
    }

    /**
     * 设置item的点击监听
     * @param listener 监听点击的listener
     */
    public void setMenuItemClickListener(MenuItemClickListener listener){
        this.onClickListener = listener;
    }

    /**
     * 设置文字显示在图片左方或右方
     * @param position 文字显示在图片左:ON_LEFT,否则ON_RIGHT,默认ON_LEFT
     */
    public void setTextPosition(int position){
        lRPosition = position;
        invalidate();
    }

    private int px2Dp(int value) {
        if (Build.VERSION.SDK_INT > Build.VERSION_CODES.LOLLIPOP) {
            return (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, value, getResources().getDisplayMetrics());
        } else {
            return 0;
        }
    }

    private void setListener(){
        this.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                if (onClickListener !=null){
                    onClickListener.onClick();
                }
            }
        });
    }
}
