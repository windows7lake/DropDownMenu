package com.yyydjk.library;

import android.content.Context;
import android.content.res.TypedArray;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.TextView;

import java.util.List;

/**
 * Changed by lio lin on 2020/3/24.
 */
public class DropDownMenu extends LinearLayout {

    // 顶部菜单布局
    private LinearLayout tabMenuView;
    // 弹出菜单栏
    private PopupWindow popupWindow;
    // 底部容器，包含popupMenuViews，maskView
    private FrameLayout popupContainerView;
    // 弹出菜单父布局
    private FrameLayout popupMenuViews;
    // tabMenuView里面选中的tab位置，-1表示未选中
    private int current_tab_position = -1;

    // 分割线颜色
    private int dividerColor = 0xffcccccc;
    // tab选中颜色
    private int textSelectedColor = 0xff890c85;
    // tab未选中颜色
    private int textUnselectedColor = 0xff111111;
    // 遮罩颜色
    private int maskColor = 0x88888888;
    // tab字体大小
    private int menuTextSize = 14;
    // tab高度
    private int popWindowHeight = 50;

    // tab padding left
    private int menuPaddingLeft;
    // tab padding right
    private int menuPaddingRight;

    // tab选中图标
    private int menuSelectedIcon;
    // tab未选中图标
    private int menuUnselectedIcon;
    // tab状态变化监听(包括dismiss，tab切换)
    private OnTabChangedListener onTabChangedListener;

    public DropDownMenu(Context context) {
        super(context, null);
    }

    public DropDownMenu(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public DropDownMenu(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);

        setOrientation(VERTICAL);

        // 为DropDownMenu添加自定义属性
        int menuBackgroundColor = 0xffffffff;
        int underlineColor = 0xffcccccc;
        TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.DropDownMenu);
        underlineColor = a.getColor(R.styleable.DropDownMenu_ddunderlineColor, underlineColor);
        dividerColor = a.getColor(R.styleable.DropDownMenu_dddividerColor, dividerColor);
        textSelectedColor = a.getColor(R.styleable.DropDownMenu_ddtextSelectedColor, textSelectedColor);
        textUnselectedColor = a.getColor(R.styleable.DropDownMenu_ddtextUnselectedColor, textUnselectedColor);
        menuBackgroundColor = a.getColor(R.styleable.DropDownMenu_ddmenuBackgroundColor, menuBackgroundColor);
        maskColor = a.getColor(R.styleable.DropDownMenu_ddmaskColor, maskColor);
        menuTextSize = a.getDimensionPixelSize(R.styleable.DropDownMenu_ddmenuTextSize, menuTextSize);
        menuPaddingLeft = a.getDimensionPixelSize(R.styleable.DropDownMenu_ddmenuPaddingLeft, dpTpPx(5));
        menuPaddingRight = a.getDimensionPixelSize(R.styleable.DropDownMenu_ddmenuPaddingRight, dpTpPx(5));
        menuSelectedIcon = a.getResourceId(R.styleable.DropDownMenu_ddmenuSelectedIcon, menuSelectedIcon);
        menuUnselectedIcon = a.getResourceId(R.styleable.DropDownMenu_ddmenuUnselectedIcon, menuUnselectedIcon);
        popWindowHeight = a.getDimensionPixelSize(R.styleable.DropDownMenu_ddpopWindowHeight, DeviceUtils.getScreenSize(getContext()).y);
        a.recycle();

        // 初始化tabMenuView并添加到tabMenuView
        tabMenuView = new LinearLayout(context);
        LayoutParams params = new LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
        tabMenuView.setOrientation(HORIZONTAL);
        tabMenuView.setBackgroundColor(menuBackgroundColor);
        tabMenuView.setLayoutParams(params);
        addView(tabMenuView, 0);

        // 为tabMenuView添加下划线
        View underLine = new View(getContext());
        underLine.setLayoutParams(new LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, dpTpPx(1.0f)));
        underLine.setBackgroundColor(underlineColor);
        addView(underLine, 1);

        // 初始化PopupWindow，并设置对应的子布局
        popupContainerView = new FrameLayout(context);
        popupContainerView.setLayoutParams(new FrameLayout.LayoutParams(FrameLayout.LayoutParams.MATCH_PARENT, FrameLayout.LayoutParams.MATCH_PARENT));
        popupWindow = new PopupWindow(popupContainerView, ViewGroup.LayoutParams.MATCH_PARENT, popWindowHeight, false);
        popupWindow.setOnDismissListener(new PopupWindow.OnDismissListener() {
            @Override
            public void onDismiss() {
                if (onTabChangedListener != null) onTabChangedListener.onTabChanged();
            }
        });
    }

    /**
     * 初始化DropDownMenu
     *
     * @param tabTexts   tab默认标签
     * @param popupViews 需要切换的自定义视图集合
     * @param weightList Tab占比，默认均为 1:1
     */
    public void setDropDownMenu(@NonNull List<String> tabTexts, @NonNull List<View> popupViews, @Nullable List<Integer> weightList) {
        if (tabTexts.size() != popupViews.size()) {
            throw new IllegalArgumentException("params not match, tabTexts.size() should be equal popupViews.size()");
        }

        for (int i = 0; i < tabTexts.size(); i++) {
            addTab(tabTexts, weightList, i);
        }

        popupContainerView.removeAllViews();

        // 遮罩半透明View，点击可关闭DropDownMenu
        View maskView = new View(getContext());
        maskView.setLayoutParams(new FrameLayout.LayoutParams(FrameLayout.LayoutParams.MATCH_PARENT, FrameLayout.LayoutParams.MATCH_PARENT));
        maskView.setBackgroundColor(maskColor);
        maskView.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                closeMenu();
            }
        });
        popupContainerView.addView(maskView, 0);

        popupMenuViews = new FrameLayout(getContext());
        popupMenuViews.setLayoutParams(new FrameLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
        for (int i = 0; i < popupViews.size(); i++) {
            popupViews.get(i).setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
            popupMenuViews.addView(popupViews.get(i), i);
        }
        popupContainerView.addView(popupMenuViews, 1);

        popupWindow.setAnimationStyle(R.style.PopupAnimation);
        popupWindow.setContentView(popupContainerView);
    }

    private void addTab(@NonNull List<String> tabTexts, List<Integer> weightList, int index) {
        final TextView tab = new TextView(getContext());
        tab.setSingleLine();
        tab.setEllipsize(TextUtils.TruncateAt.END);
        tab.setGravity(Gravity.CENTER);
        tab.setTextSize(TypedValue.COMPLEX_UNIT_PX, menuTextSize);
        tab.setLayoutParams(new LayoutParams(0, ViewGroup.LayoutParams.MATCH_PARENT, weightList == null ? 1.0f : weightList.get(index)));
        tab.setTextColor(textUnselectedColor);
        tab.setCompoundDrawablesWithIntrinsicBounds(null, null, getResources().getDrawable(menuUnselectedIcon), null);
        tab.setText(tabTexts.get(index));
        tab.setPadding(menuPaddingLeft, dpTpPx(12), menuPaddingRight, dpTpPx(12));
        // 添加点击事件
        tab.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                switchMenu(tab);
            }
        });
        tabMenuView.addView(tab);
        // 添加分割线
        if (index < tabTexts.size() - 1) {
            View view = new View(getContext());
            view.setLayoutParams(new LayoutParams(dpTpPx(0.5f), ViewGroup.LayoutParams.MATCH_PARENT));
            view.setBackgroundColor(dividerColor);
            tabMenuView.addView(view);
        }
    }

    /**
     * 改变tab文字
     *
     * @param text 切换Tab时显示的文本
     */
    public void setTabText(String text) {
        if (current_tab_position != -1) {
            ((TextView) tabMenuView.getChildAt(current_tab_position)).setText(text);
        }
    }

    public void setTabText(int tabIndex, String text) {
        ((TextView) tabMenuView.getChildAt(tabIndex)).setText(text);
    }

    public void setTabClickable(boolean clickable) {
        for (int i = 0; i < tabMenuView.getChildCount(); i = i + 2) {
            tabMenuView.getChildAt(i).setClickable(clickable);
        }
    }

    /**
     * 关闭菜单
     */
    public void closeMenu() {
        if (current_tab_position != -1) {
            ((TextView) tabMenuView.getChildAt(current_tab_position)).setTextColor(textUnselectedColor);
            ((TextView) tabMenuView.getChildAt(current_tab_position)).setCompoundDrawablesWithIntrinsicBounds(null, null,
                    getResources().getDrawable(menuUnselectedIcon), null);
            current_tab_position = -1;
            popupWindow.dismiss();
        }
    }

    /**
     * DropDownMenu是否处于可见状态
     */
    public boolean isShowing() {
        return current_tab_position != -1;
    }

    /**
     * 切换菜单
     *
     * @param target 当前所点击的Tab
     */
    private void switchMenu(View target) {
        for (int i = 0; i < tabMenuView.getChildCount(); i = i + 2) {
            if (target == tabMenuView.getChildAt(i)) { // 当前点击的Tab
                if (current_tab_position == i) {
                    closeMenu();
                } else {
                    popupMenuViews.getChildAt(i / 2).setVisibility(View.VISIBLE);
                    current_tab_position = i;
                    ((TextView) tabMenuView.getChildAt(i)).setTextColor(textSelectedColor);
                    ((TextView) tabMenuView.getChildAt(i)).setCompoundDrawablesWithIntrinsicBounds(null, null,
                            getResources().getDrawable(menuSelectedIcon), null);

                    // 显示方式
                    popupWindow.showAsDropDown(target);
                }
            } else { // 非当前点击的Tab
                ((TextView) tabMenuView.getChildAt(i)).setTextColor(textUnselectedColor);
                ((TextView) tabMenuView.getChildAt(i)).setCompoundDrawablesWithIntrinsicBounds(null, null,
                        getResources().getDrawable(menuUnselectedIcon), null);
                popupMenuViews.getChildAt(i / 2).setVisibility(View.GONE);
            }
        }
        if (onTabChangedListener != null) onTabChangedListener.onTabChanged();
    }

    public int dpTpPx(float value) {
        DisplayMetrics dm = getResources().getDisplayMetrics();
        return (int) (TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, value, dm) + 0.5);
    }

    /**
     * 指定PopupWindow的高度，默认全屏
     *
     * @param height 高度
     */
    public void setPopupWindowHeight(int height) {
        popupWindow.setHeight(height);
    }

    public void setOnTabChangedListener(OnTabChangedListener onTabChangedListener) {
        this.onTabChangedListener = onTabChangedListener;
    }

    public interface OnTabChangedListener {
        void onTabChanged();
    }
}
