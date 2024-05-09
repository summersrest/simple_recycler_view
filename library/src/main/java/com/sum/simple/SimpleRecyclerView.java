package com.sum.simple;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;

import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.SimpleItemAnimator;
import androidx.recyclerview.widget.StaggeredGridLayoutManager;

public class SimpleRecyclerView extends RecyclerView {

    /*------------------ 常量 begin ------------------*/
    //类型
    public static final int TYPE_LIST = 0;
    public static final int TYPE_GRID = 1;
    public static final int TYPE_STAGGER = 2;
    //方向
    public static final int ORIENTATION_HORIZONTAL = 0;
    public static final int ORIENTATION_VERTICAL = 1;
    /*------------------ 常量 end ------------------*/

    //类型、方向、列数
    private int type = TYPE_LIST;
    private int orientation = ORIENTATION_VERTICAL;
    private int crossAxisCount = 1;

    //分割线
    private float dividerSize = 0;
    private float dividerPadding = 0;
    private float dividerPaddingStart = -1f;
    private float dividerPaddingEnd = -1f;
    private int dividerColor = Color.WHITE;
    private Drawable dividerDrawable = null;
    private boolean isLastItemShowDivider = true;

    //动画
    private boolean isDefaultAnimatorOpen = false;

    private boolean move = false;
    private int mIndex = 0;

    private Context mContext;
    private LayoutManager mLayoutManager;
    private SimpleItemDecoration mItemDecoration;

    public SimpleRecyclerView(Context context) {
        super(context, null);
    }

    public SimpleRecyclerView(Context context, AttributeSet attrs) {
        super(context, attrs);

        mContext = context;

        /*================== 获取自定义属性 begin ==================*/
        TypedArray typedArray = context.obtainStyledAttributes(attrs, R.styleable.SimpleRecyclerView);
        for (int i = 0; i < typedArray.getIndexCount(); i++) {
            int attr = typedArray.getIndex(i);
            if (attr == R.styleable.SimpleRecyclerView_rv_type) {
                type = typedArray.getInt(attr, 0);
            } else if (attr == R.styleable.SimpleRecyclerView_rv_orientation) {
                orientation = typedArray.getInt(attr, ORIENTATION_VERTICAL);
            } else if (attr == R.styleable.SimpleRecyclerView_rv_cross_axis) {
                crossAxisCount = typedArray.getInt(attr, 1);
            } else if (attr == R.styleable.SimpleRecyclerView_rv_divider_size) {
                dividerSize = typedArray.getDimension(attr, 0f);
            } else if (attr == R.styleable.SimpleRecyclerView_rv_divider_drawable) {
                dividerDrawable = typedArray.getDrawable(attr);
            } else if (attr == R.styleable.SimpleRecyclerView_rv_divider_color) {
                dividerColor = typedArray.getColor(attr, Color.WHITE);
            } else if (attr == R.styleable.SimpleRecyclerView_rv_default_animator_open) {
                isDefaultAnimatorOpen = typedArray.getBoolean(attr, false);
            } else if (attr == R.styleable.SimpleRecyclerView_rv_divider_last_show) {
                isLastItemShowDivider = typedArray.getBoolean(attr, true);
            } else if (attr == R.styleable.SimpleRecyclerView_rv_divider_padding) {
                dividerPadding = typedArray.getDimension(attr, 0f);
            } else if (attr == R.styleable.SimpleRecyclerView_rv_divider_padding_start) {
                dividerPaddingStart = typedArray.getDimension(attr, -1f);
            } else if (attr == R.styleable.SimpleRecyclerView_rv_divider_padding_end) {
                dividerPaddingEnd = typedArray.getDimension(attr, -1f);
            }
        }
        typedArray.recycle();
        /*================== 获取自定义属性 end ==================*/
        init();
    }

    /**
     * 根据属性初始化RecyclerView
     */
    private void init() {
        if (dividerPaddingStart == -1f) dividerPaddingStart = dividerPadding;
        if (dividerPaddingEnd == -1f) dividerPaddingEnd = dividerPadding;
        //1、设置RecyclerView的类型和方向
        switch (type) {
            case TYPE_LIST -> {
                switch (orientation) {
                    case ORIENTATION_VERTICAL ->
                            mLayoutManager = new LinearLayoutManager(mContext, LinearLayoutManager.VERTICAL, false);
                    case ORIENTATION_HORIZONTAL -> mLayoutManager = new LinearLayoutManager(mContext,
                            LinearLayoutManager.HORIZONTAL, false);
                }
            }
            case TYPE_GRID -> {
                switch (orientation) {
                    case ORIENTATION_VERTICAL -> mLayoutManager = new GridLayoutManager(mContext, crossAxisCount,
                            GridLayoutManager.VERTICAL, false);
                    case ORIENTATION_HORIZONTAL ->
                            mLayoutManager = new GridLayoutManager(mContext, crossAxisCount, GridLayoutManager.HORIZONTAL, false);
                }
            }
            case TYPE_STAGGER -> {
                switch (orientation) {
                    case ORIENTATION_VERTICAL ->
                            mLayoutManager = new StaggeredGridLayoutManager(crossAxisCount, StaggeredGridLayoutManager.VERTICAL);
                    case ORIENTATION_HORIZONTAL ->
                            mLayoutManager = new StaggeredGridLayoutManager(crossAxisCount, StaggeredGridLayoutManager.HORIZONTAL);
                }
            }
        }
        this.setLayoutManager(mLayoutManager);

        //2、设置RecyclerView的分割线样式
        this.removeItemDecoration(mItemDecoration);
        mItemDecoration = new SimpleItemDecoration(mContext, orientation, dividerSize, dividerColor, dividerDrawable);
        this.addItemDecoration(mItemDecoration);

        //3、设置默认动画是否开启
        if (!isDefaultAnimatorOpen) {
            //关闭默认动画
            closeItemAnimator();
        } else {
            //打开默认动画
            openItemAnimator();
        }

        //4、设置滚动监听（用于平滑滚动）
        addOnScrollListener(new RecyclerViewListener());

    }

    /**
     * 提醒SimpleRecyclerView，当前的列表样式已经更改（一般是用代码动态修改了type和orientation后调用，如果修改了分割线样式，也需要调用该方法进行刷新）
     */
    public void notifyViewChanged() {
        init();
        //重新设置布局管理器后需要设置适配器
        Adapter adapter = this.getAdapter();
        if (adapter != null)
            this.setAdapter(adapter);
    }


    /**
     * 平滑滚动到指定位置（注意：对瀑布流无效果）
     */
    public void smoothMoveToPosition(int position) {
        if (type == TYPE_STAGGER) {
            return;
        }

        if (position < 0 || position >= getAdapter().getItemCount()) {
            Log.e("CSDN_SimpleRecyclerView", "超出范围了");
            return;
        }
        mIndex = position;
        stopScroll();

        GridLayoutManager glm = (GridLayoutManager) mLayoutManager;
        int firstItem = glm.findFirstVisibleItemPosition();
        int lastItem = glm.findLastVisibleItemPosition();
        if (position <= firstItem) {
            this.smoothScrollToPosition(position);
        } else if (position <= lastItem) {
            int top = this.getChildAt(position - firstItem).getTop();
            this.smoothScrollBy(0, top);
        } else {
            this.smoothScrollToPosition(position);
            move = true;
        }

    }

    /**
     * 滚动到指定位置（注意：对瀑布流无效果）
     */
    public void moveToPosition(int position) {
        if (type == TYPE_STAGGER) {
            return;
        }

        if (position < 0 || position >= getAdapter().getItemCount()) {
            Log.e("CSDN_LQR", "超出范围了");
            return;
        }
        mIndex = position;
        stopScroll();

        GridLayoutManager glm = (GridLayoutManager) mLayoutManager;
        int firstItem = glm.findFirstVisibleItemPosition();
        int lastItem = glm.findLastVisibleItemPosition();
        if (position <= firstItem) {
            this.scrollToPosition(position);
        } else if (position <= lastItem) {
            int top = this.getChildAt(position - firstItem).getTop();
            this.scrollBy(0, top);
        } else {
            this.scrollToPosition(position);
            move = true;
        }

    }

    /**
     * RecyclerView的滚动监听, 用于平滑滚动条目置顶
     */
    class RecyclerViewListener extends OnScrollListener {
        @Override
        public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
            super.onScrollStateChanged(recyclerView, newState);

            if (mOnScrollListenerExtension != null) {
                mOnScrollListenerExtension.onScrollStateChanged(recyclerView, newState);
            }

            if (type != TYPE_GRID) {
                return;
            }
            GridLayoutManager glm = (GridLayoutManager) mLayoutManager;

            if (move && newState == RecyclerView.SCROLL_STATE_IDLE) {
                move = false;
                int n = mIndex - glm.findFirstVisibleItemPosition();
                if (0 <= n && n < SimpleRecyclerView.this.getChildCount()) {
                    int top = SimpleRecyclerView.this.getChildAt(n).getTop();
                    SimpleRecyclerView.this.smoothScrollBy(0, top);
                }

            }
        }

        @Override
        public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
            super.onScrolled(recyclerView, dx, dy);

            if (mOnScrollListenerExtension != null) {
                mOnScrollListenerExtension.onScrolled(recyclerView, dx, dy);
            }

            if (type != TYPE_GRID) {
                return;
            }
            GridLayoutManager glm = (GridLayoutManager) mLayoutManager;
            if (move) {
                move = false;
                int n = mIndex - glm.findFirstVisibleItemPosition();
                if (0 <= n && n < SimpleRecyclerView.this.getChildCount()) {
                    int top = SimpleRecyclerView.this.getChildAt(n).getTop();
                    SimpleRecyclerView.this.scrollBy(0, top);
                }
            }
        }
    }

    /*================== SimpleRecyclerView的滚动事件拓展 begin ==================*/
    private OnScrollListenerExtension mOnScrollListenerExtension;

    public OnScrollListenerExtension getOnScrollListenerExtension() {
        return mOnScrollListenerExtension;
    }

    public void setOnScrollListenerExtension(OnScrollListenerExtension onScrollListenerExtension) {
        mOnScrollListenerExtension = onScrollListenerExtension;
    }

    /**
     * SimpleRecyclerView的滚动事件拓展（原滚动事件被用于平滑滚动）
     */
    public interface OnScrollListenerExtension {
        void onScrollStateChanged(RecyclerView recyclerView, int newState);

        void onScrolled(RecyclerView recyclerView, int dx, int dy);
    }
    /*================== SimpleRecyclerView的滚动事件拓展 end ==================*/

    /**
     * 分割线
     * <p>
     * 当同时设置了颜色和图片时，以图片为主
     * 当不设置size时，分割线以图片的厚度为标准或不显示分割线（size默认为0）。
     */
    class SimpleItemDecoration extends ItemDecoration {
        private Context mContext;
        private int mOrientation;
        private float mDividerSize = 0f;
        private int mDividerColor = Color.WHITE;
        private Drawable mDividerDrawable;
        private Paint mPaint;

        public SimpleItemDecoration(Context context, int orientation, float dividerSize, int dividerColor,
                                    Drawable dividerDrawable) {
            mContext = context;
            mOrientation = orientation;
            mDividerSize = dividerSize;
            mDividerColor = dividerColor;
            mDividerDrawable = dividerDrawable;

            //绘制纯色分割线
            if (dividerDrawable == null) {
                //初始化画笔(抗锯齿)并设置画笔颜色和画笔样式为填充
                mPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
                mPaint.setColor(mDividerColor);
                mPaint.setStyle(Paint.Style.FILL);
                //绘制图片分割线
            } else {
                //如果没有指定分割线的size，则默认是图片的厚度
                if (mDividerSize == 0) {
                    if (mOrientation == SimpleRecyclerView.ORIENTATION_VERTICAL) {
                        mDividerSize = dividerDrawable.getIntrinsicHeight();
                    } else {
                        mDividerSize = dividerDrawable.getIntrinsicWidth();
                    }
                }
            }

        }

        /**
         * 绘制item分割线
         */
        @Override
        public void onDraw(Canvas c, RecyclerView parent, State state) {
            //列表
            if (type == TYPE_LIST) {
                drawListDivider(c, parent, state);
            } else {
                //表格，瀑布流
                drawGridDivider(c, parent, state);
            }
        }

        /**
         * 绘制List分割线
         *
         * @param c
         * @param parent
         * @param state
         */
        private void drawListDivider(Canvas c, RecyclerView parent, State state) {
            int childCount = isLastItemShowDivider ? parent.getChildCount() : parent.getChildCount() - 1;
            //垂直方向
            if (orientation == ORIENTATION_VERTICAL) {
                for (int i = 0; i < childCount; i++) {
                    View child = parent.getChildAt(i);
                    LayoutParams params = (LayoutParams) child.getLayoutParams();
                    float top = child.getBottom();
                    float bottom = top + dividerSize;
                    float right = parent.getMeasuredWidth() - parent.getPaddingRight() - dividerPaddingEnd;
                    float left = parent.getPaddingLeft() + dividerPaddingStart;
                    //得到四个点后开始画
                    if (mDividerDrawable == null) {
                        c.drawRect(left, top, right, bottom, mPaint);
                    } else {
                        mDividerDrawable.setBounds((int) left, (int) top, (int) right, (int) bottom);
                        mDividerDrawable.draw(c);
                    }
                }
            } else {
                //水平方向
                for (int i = 0; i < childCount; i++) {
                    View child = parent.getChildAt(i);
                    LayoutParams params = (LayoutParams) child.getLayoutParams();
                    float top = parent.getPaddingTop() + params.bottomMargin + dividerPaddingStart;
                    float bottom = parent.getMeasuredHeight() + top - dividerPaddingEnd;
                    float left = child.getRight() + params.rightMargin;
                    float right = left + dividerSize;
                    //得到四个点后开始画
                    if (mDividerDrawable == null) {
                        c.drawRect(left, top, right, bottom, mPaint);
                    } else {
                        mDividerDrawable.setBounds((int) left, (int) top, (int) right, (int) bottom);
                        mDividerDrawable.draw(c);
                    }
                }
            }
        }

        /**
         * 绘制Grid/stagger分割线
         *
         * @param c
         * @param parent
         * @param state
         */
        private void drawGridDivider(Canvas c, RecyclerView parent, State state) {
            int childCount = parent.getChildCount();
            //垂直方向
            if (orientation == ORIENTATION_VERTICAL) {
                for (int i = 0; i < childCount; i++) {
                    View child = parent.getChildAt(i);
                    LayoutParams params = (LayoutParams) child.getLayoutParams();
                    //绘制竖线
                    //是最后一列，并且isLastItemShowDivider为false的情况下，不绘制竖向分割线。
                    if (isLastCrossAxis(i, crossAxisCount) && !isLastItemShowDivider) {
                        //不绘制分割线
                    } else {
                        //绘制分割线
                        float top = child.getTop() - params.topMargin + dividerPaddingStart;
                        float left = child.getRight() + params.rightMargin;
                        float bottom = child.getBottom() + params.bottomMargin + dividerSize - dividerPaddingEnd;
                        float right = left + dividerSize;
                        if (mDividerDrawable == null) {
                            c.drawRect(left, top, right, bottom, mPaint);
                        } else {
                            mDividerDrawable.setBounds((int) left, (int) top, (int) right, (int) bottom);
                            mDividerDrawable.draw(c);
                        }
                    }
                    //绘制横线
                    //如果是最后一行，并且isLastItemShowDivider为false的情况下，不绘制横向分割线。
                    if (isLastMainAxis(i, crossAxisCount, childCount) && !isLastItemShowDivider) {
                        //不绘制
                    } else {
                        float top = child.getBottom() + params.bottomMargin;
                        float left = child.getLeft() - params.leftMargin + dividerPaddingStart;
                        float bottom = top + dividerSize;
                        float right = child.getRight() + params.rightMargin + dividerSize - dividerPaddingEnd;
                        //得到四个点后开始画
                        if (mDividerDrawable == null) {
                            c.drawRect(left, top, right, bottom, mPaint);
                        } else {
                            mDividerDrawable.setBounds((int) left, (int) top, (int) right, (int) bottom);
                            mDividerDrawable.draw(c);
                        }
                    }
                }
            } else {
                //水平方向
                for (int i = 0; i < childCount; i++) {
                    View child = parent.getChildAt(i);
                    LayoutParams params = (LayoutParams) child.getLayoutParams();
                    //绘制竖线
                    //是最后一列，并且isLastItemShowDivider为false的情况下，不绘制竖向分割线。
                    if (isLastMainAxis(i, crossAxisCount, childCount) && !isLastItemShowDivider) {
                        //不绘制
                    } else {
                        //绘制分割线
                        float top = child.getTop() - params.topMargin + dividerPaddingStart;
                        float left = child.getRight() + params.rightMargin;
                        float bottom = child.getBottom() + params.bottomMargin + dividerSize - dividerPaddingEnd;
                        float right = left + dividerSize;
                        if (mDividerDrawable == null) {
                            c.drawRect(left, top, right, bottom, mPaint);
                        } else {
                            mDividerDrawable.setBounds((int) left, (int) top, (int) right, (int) bottom);
                            mDividerDrawable.draw(c);
                        }
                    }
                    //绘制横线
                    //如果是最后一行，并且isLastItemShowDivider为false的情况下，不绘制横向分割线。
                    if (isLastCrossAxis(i, crossAxisCount) && !isLastItemShowDivider) {
                        //不绘制
                    } else {
                        float top = child.getBottom() + params.bottomMargin;
                        float left = child.getLeft() - params.leftMargin + dividerPaddingStart;
                        float bottom = top + dividerSize;
                        float right = child.getRight() + params.rightMargin + dividerSize - dividerPaddingEnd;
                        //得到四个点后开始画
                        if (mDividerDrawable == null) {
                            c.drawRect(left, top, right, bottom, mPaint);
                        } else {
                            mDividerDrawable.setBounds((int) left, (int) top, (int) right, (int) bottom);
                            mDividerDrawable.draw(c);
                        }
                    }
                }
            }
        }

        /**
         * 根据分割线的size设置item偏移量
         */
        @Override
        public void getItemOffsets(Rect outRect, View view, RecyclerView parent, State state) {
            //当前index
            int currentIndex = parent.getChildAdapterPosition(view);
            int allCount = 0;
            if (parent.getAdapter() != null) {
                allCount = parent.getAdapter().getItemCount();
            }
            if (type == TYPE_LIST) {//List
                //垂直方向
                if (orientation == ORIENTATION_VERTICAL) {
                    if (isLastItemShowDivider) {
                        //全部向下偏移
                        outRect.set(0, 0, 0, (int) mDividerSize);
                    } else {
                        //最后一个item不绘制分割线，所以最后一个item不偏移
                        if (currentIndex == allCount - 1)
                            outRect.set(0, 0, 0, 0);
                        else
                            outRect.set(0, 0, 0, (int) mDividerSize);
                    }
                } else {
                    //水平方向
                    if (isLastItemShowDivider) {
                        //全部向右偏移
                        outRect.set(0, 0, (int) mDividerSize, 0);
                    } else {
                        //最后一个item不绘制分割线，所以最后一个item不偏移
                        if (currentIndex == allCount - 1)
                            outRect.set(0, 0, 0, 0);
                        else
                            outRect.set(0, 0, (int) mDividerSize, 0);
                    }
                }
            } else {//Grid
                //最后的行或者列显示分割线
                if (isLastItemShowDivider) {
                    //全部偏移
                    outRect.set(0, 0, (int) mDividerSize, (int) mDividerSize);
                } else {
                    //最后的行或者列不显示分割线
                    //垂直方向
                    if (orientation == ORIENTATION_VERTICAL) {
                        //当前item是否为最后一行
                        boolean isLastRow = isLastMainAxis(currentIndex, crossAxisCount, allCount);
                        //当前item是否为最后一列
                        boolean isLastColumn = isLastCrossAxis(currentIndex, crossAxisCount);
                        outRect.set(0, 0,
                                isLastColumn ? 0 : (int) mDividerSize,
                                isLastRow ? 0 : (int) mDividerSize);
                    } else {
                        //水平方向
                        //当前item是否为最后一列
                        boolean isLastColumn = isLastMainAxis(currentIndex, crossAxisCount, allCount);
                        //当前item是否为最后一行
                        boolean isLastRow = isLastCrossAxis(currentIndex, crossAxisCount);
                        outRect.set(0, 0,
                                isLastColumn ? 0 : (int) mDividerSize,
                                isLastRow ? 0 : (int) mDividerSize);
                    }

                }
            }
        }

        /**
         * 判断是否主轴最后一行/列
         *
         * @param currentIndex
         * @param crossAxisCount
         * @param childCount
         * @return
         */
        private boolean isLastMainAxis(int currentIndex, int crossAxisCount, int childCount) {
            //总数小于列数，一定为最后一行
            if (childCount <= crossAxisCount) {
                return true;
            } else {
                //总数大于列数，需要判断当前item是否为最后一行
                int row = currentIndex / crossAxisCount;
                int columnCount = childCount % crossAxisCount == 0 ? childCount / crossAxisCount : childCount / crossAxisCount + 1;
                return row == columnCount - 1;
            }
        }

        /**
         * 判断是否交叉轴最后一行/列
         *
         * @param currentIndex
         * @param crossAxisCount
         * @return
         */
        private boolean isLastCrossAxis(int currentIndex, int crossAxisCount) {
            return (currentIndex + 1) % crossAxisCount == 0;
        }
    }


    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public int getOrientation() {
        return orientation;
    }

    public void setOrientation(int orientation) {
        this.orientation = orientation;
    }

    public int getCrossAxisCount() {
        return crossAxisCount;
    }

    public void setCrossAxisCount(int crossAxisCount) {
        this.crossAxisCount = crossAxisCount;
    }

    public float getDividerSize() {
        return dividerSize;
    }

    public void setDividerSize(float dividerSize) {
        this.dividerSize = dividerSize;
    }

    public int getDividerColor() {
        return dividerColor;
    }

    public void setDividerColor(int dividerColor) {
        this.dividerColor = dividerColor;
    }

    public Drawable getDividerDrawable() {
        return dividerDrawable;
    }

    public void setDividerDrawable(Drawable dividerDrawable) {
        this.dividerDrawable = dividerDrawable;
    }

    public boolean isDefaultAnimatorOpen() {
        return isDefaultAnimatorOpen;
    }

    /**
     * 打开局部刷新动画
     */
    public void openItemAnimator() {
        isDefaultAnimatorOpen = true;
        this.getItemAnimator().setAddDuration(120);
        this.getItemAnimator().setChangeDuration(250);
        this.getItemAnimator().setMoveDuration(250);
        this.getItemAnimator().setRemoveDuration(120);
        ((SimpleItemAnimator) this.getItemAnimator()).setSupportsChangeAnimations(true);
    }

    /**
     * 关闭局部刷新动画
     */
    public void closeItemAnimator() {
        isDefaultAnimatorOpen = false;
        this.getItemAnimator().setAddDuration(0);
        this.getItemAnimator().setChangeDuration(0);
        this.getItemAnimator().setMoveDuration(0);
        this.getItemAnimator().setRemoveDuration(0);
        ((SimpleItemAnimator) this.getItemAnimator()).setSupportsChangeAnimations(false);
    }

    @Override
    public LayoutManager getLayoutManager() {
        return mLayoutManager;
    }
}
