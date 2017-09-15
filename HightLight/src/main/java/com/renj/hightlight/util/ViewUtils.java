package com.renj.hightlight.util;

import android.app.Activity;
import android.content.Context;
import android.graphics.Rect;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;

/**
 * ======================================================================
 * <p/>
 * 作者：Renj
 * <p/>
 * 创建时间：2016-08-02    17:24
 * <p/>
 * 描述：操作View的工具类，采用单例设计模式
 * <p/>
 * 修订历史：
 * <p/>
 * ======================================================================
 */
public class ViewUtils {
    private static final String FRAGMENT_CON = "NoSaveStateFrameLayout";
    private static ViewUtils viewUtils = new ViewUtils();
    private static Activity mActivity;

    private OnViewClickListener clickLisstener;

    /**
     * 设置点击监听
     *
     * @param clickLisstener
     */
    public void setOnViewClickListener(OnViewClickListener clickLisstener) {
        this.clickLisstener = clickLisstener;
    }

    private ViewUtils() {
    }

    /**
     * 获取ViewUtils的实例
     *
     * @param activity
     * @return
     */
    public static ViewUtils getInstance(Activity activity) {
        mActivity = activity;
        return viewUtils;
    }

    /**
     * @return 返回最顶层视图
     */
    public ViewGroup getDeCorView() {
        return (ViewGroup) mActivity.getWindow().getDecorView();
    }

    /**
     * @return 返回内容区域根视图
     */
    public ViewGroup getRootView() {
        return (ViewGroup) mActivity.findViewById(android.R.id.content);
    }

    /**
     * 在整个窗体上面增加一层布局
     *
     * @param layoutId 布局id
     */
    public void addView(int layoutId) {
        final View view = View.inflate(mActivity, layoutId, null);
        FrameLayout frameLayout = (FrameLayout) getRootView();
        frameLayout.addView(view);

        // 设置整个布局的单击监听
        view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                removeView(view);
                if (clickLisstener != null) {
                    clickLisstener.onClick(view);
                }
            }
        });
    }

    /**
     * 移除View
     *
     * @param view 需要移出的视图
     */
    public void removeView(View view) {
        FrameLayout frameLayout = (FrameLayout) getRootView();
        frameLayout.removeView(view);
    }

    /**
     * 获取子View在父View中的位置
     *
     * @param parent 父View
     * @param child  子View
     * @return Rect对象
     */
    public Rect getLocationInView(View parent, View child) {
        if (child == null || parent == null) {
            throw new IllegalArgumentException(
                    "parent and child can not be null .");
        }

        View decorView = null;
        Context context = child.getContext();
        if (context instanceof Activity) {
            decorView = ((Activity) context).getWindow().getDecorView();
        }

        Rect result = new Rect();
        Rect tmpRect = new Rect();

        View tmp = child;

        if (child == parent) {
            child.getHitRect(result);
            return result;
        }

        while (tmp != decorView && tmp != parent) {
            tmp.getHitRect(tmpRect);
            if (!tmp.getClass().equals(FRAGMENT_CON)) {
                result.left += tmpRect.left;
                result.top += tmpRect.top;
            }
            tmp = (View) tmp.getParent();
        }

        result.right = result.left + child.getMeasuredWidth();
        result.bottom = result.top + child.getMeasuredHeight();
        return result;
    }

    /**
     * 单击视图监听，用于多个引导页面时连续调用
     */
    public interface OnViewClickListener {
        /**
         * 单击监听回调
         *
         * @param view
         */
        void onClick(View view);
    }
}
