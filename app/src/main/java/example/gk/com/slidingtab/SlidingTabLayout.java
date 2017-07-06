package example.gk.com.slidingtab;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.support.v4.view.ViewPager;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.View;
import android.view.WindowManager;
import android.widget.HorizontalScrollView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.support.v4.view.ViewPager.OnPageChangeListener;

import java.util.List;

/**
 * 首页滑动条
 *
 * @author gaok
 * @date 2017/7/6
 */
public class SlidingTabLayout extends HorizontalScrollView {
    private Context mContext;
    private int selection = 0;
    /**
     * tab容器
     */
    private LinearLayout mItemsLayout;
    /**
     * 指示器
     */
    private Bitmap mSlideIcon;
    /**
     * 指示器初始X偏移量
     */
    private int mInitTranslationX;
    /**
     * 指示器初始Y偏移量
     */
    private int mInitTranslationY;
    /**
     * 滑动过程中指示器的水平偏移量
     */
    private int mTranslationX;
    /**
     * tab总数
     */
    private int mTotalItemsCount;
    /**
     * 指示器绘制数据的初始化标志
     */
    private boolean isFirstTime = true;
    /**
     * 页面可见的tab数量，默认4个
     */
    private int VISIBLE_TAB_COUNT = 4;
    /**
     * 移动到倒数第几个，容器开始滑动
     */
    private int START_SCROLL = 2;
    /**
     * 标题正常时的颜色
     */
    private static final int COLOR_TEXT_NORMAL = 0x77FFFFFF;
    /**
     * 标题选中时的颜色
     */
    private static final int COLOR_TEXT_HIGHLIGHTCOLOR = 0xFFFFFFFF;
    private ViewPager mViewPager;
    private List<String> mTabTitles;
    private int mTabVisibleCount = VISIBLE_TAB_COUNT;
    private OnPageChangeListener onPageChangeListener;

    public SlidingTabLayout(Context context) {
        this(context, null);
    }

    public SlidingTabLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.mContext = context;
        this.mSlideIcon = BitmapFactory.decodeResource(getResources(), R.drawable.home_jiaobiao);
        this.mItemsLayout = new LinearLayout(context);
        initTabStripParams();
        addView(mItemsLayout, 0, new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT));
    }

    public void scroll(int position, float positionOffset) {
        int tabWidth = mItemsLayout.getChildAt(position).getWidth();
        mTranslationX = (int) ((position + positionOffset) * tabWidth);

        // 容器滚动，当移动到倒数最后一个的时候，开始滚动
        if (positionOffset > 0 && position >= (VISIBLE_TAB_COUNT - START_SCROLL) && mTotalItemsCount > VISIBLE_TAB_COUNT) {
            if (VISIBLE_TAB_COUNT != 1) {
                //注意这里是整体滑动，使得tabs跟指示器保持相对静止
                this.scrollTo((position - (VISIBLE_TAB_COUNT - START_SCROLL)) * tabWidth + (int) (tabWidth * positionOffset), 0);
            } else
            // 为count为1时 的特殊处理
            {
                this.scrollTo(position * tabWidth + (int) (tabWidth * positionOffset), 0);
            }
        }
        invalidate();
    }

    private void initTabStripParams() {
        mItemsLayout.setClipChildren(false);
        mItemsLayout.setClipToPadding(false);
        mItemsLayout.setGravity(Gravity.BOTTOM);
        mItemsLayout.setPadding(0, 0, 0, 0);
    }

    /**
     * 绘制指示器
     */
    @Override
    protected void dispatchDraw(Canvas canvas) {
        canvas.save();
        // 平移到正确的位置
        canvas.translate(mInitTranslationX + mTranslationX, this.mInitTranslationY);//修正tabs的平移量
        canvas.drawBitmap(this.mSlideIcon, 0, 0, null);
        canvas.restore();
        super.dispatchDraw(canvas);
    }

    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {
        super.onLayout(changed, l, t, r, b);
        if (isFirstTime && (mTotalItemsCount > 0) && getItemView(this.selection) != null) {
            View currentItemView = getItemView(this.selection);
            this.mInitTranslationX = (currentItemView.getLeft() + currentItemView.getWidth() / 2 - this.mSlideIcon.getWidth() / 2);
            this.mInitTranslationY = (b - t - this.mSlideIcon.getHeight());
            isFirstTime = false;
        }
    }

    public void setVisibleTabCount(int VISIBLE_TAB_COUNT) {
        this.VISIBLE_TAB_COUNT = VISIBLE_TAB_COUNT;
    }

    public void setStartScroll(int START_SCROLL) {
        this.START_SCROLL = START_SCROLL;
    }

    public View getItemView(int itemPosition) {
        if ((itemPosition >= 0) && (itemPosition < this.mTotalItemsCount)) {
            return this.mItemsLayout.getChildAt(itemPosition);
        }
        return null;
    }

    public void setData(List<String> datas) {
        mTotalItemsCount = 0;
        // 如果传入的list有值，则移除布局文件中设置的view
        if (datas != null && datas.size() > 0) {
            this.mItemsLayout.removeAllViews();
            this.mTabTitles = datas;

            for (String title : mTabTitles) {
                // 添加view
                this.mItemsLayout.addView(generateTextView(title));
                mTotalItemsCount++;
            }
            // 设置item的click事件
            setItemClickEvent();
        }
    }

    /**
     * 对外的ViewPager的回调接口
     *
     * @author zhy
     */
    public interface PageChangeListener {
        void onPageScrolled(int position, float positionOffset, int positionOffsetPixels);

        void onPageSelected(int position);

        void onPageScrollStateChanged(int state);
    }

    // 设置关联的ViewPager
    public void setViewPager(ViewPager mViewPager, int pos) {
        this.mViewPager = mViewPager;

        mViewPager.addOnPageChangeListener(new OnPageChangeListener() {
            @Override
            public void onPageSelected(int position) {
                // 设置字体颜色高亮
                resetTextViewColor();
                highLightTextView(position);

                // 回调
                if (onPageChangeListener != null) {
                    onPageChangeListener.onPageSelected(position);
                }
            }

            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
                // 滚动
                scroll(position, positionOffset);

                // 回调
                if (onPageChangeListener != null) {
                    onPageChangeListener.onPageScrolled(position, positionOffset, positionOffsetPixels);
                }

            }

            @Override
            public void onPageScrollStateChanged(int state) {
                // 回调
                if (onPageChangeListener != null) {
                    onPageChangeListener.onPageScrollStateChanged(state);
                }

            }
        });
        // 设置当前页
        mViewPager.setCurrentItem(pos);
        // 高亮
        highLightTextView(pos);
    }

    /**
     * 重置文本颜色
     */
    private void resetTextViewColor()
    {
        for (int i = 0; i < mItemsLayout.getChildCount(); i++)
        {
            View view = mItemsLayout.getChildAt(i);
            if (view instanceof TextView)
            {
                ((TextView) view).setTextColor(COLOR_TEXT_NORMAL);
            }
        }
    }

    /**
     * 高亮文本
     *
     * @param position
     */
    protected void highLightTextView(int position)
    {
        View view = mItemsLayout.getChildAt(position);
        if (view instanceof TextView)
        {
            ((TextView) view).setTextColor(COLOR_TEXT_HIGHLIGHTCOLOR);
        }

    }

    private void setItemClickEvent() {
        int cCount = mItemsLayout.getChildCount();
        for (int i = 0; i < cCount; i++) {
            final int j = i;
            View view = mItemsLayout.getChildAt(i);
            view.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {
                    mViewPager.setCurrentItem(j);
                }
            });
        }
    }

    /**
     * 根据标题生成我们的TextView
     *
     * @param text
     * @return
     */
    private TextView generateTextView(String text) {
        TextView tv = new TextView(getContext());
        LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT);
        lp.width = getScreenWidth() / mTabVisibleCount;
        tv.setGravity(Gravity.CENTER);
        tv.setTextColor(0x77FFFFFF);
        tv.setText(text);
        tv.setTextSize(TypedValue.COMPLEX_UNIT_SP, 16);
        tv.setLayoutParams(lp);
        return tv;
    }

    /**
     * 获得屏幕的宽度
     *
     * @return
     */
    public int getScreenWidth() {
        WindowManager wm = (WindowManager) getContext().getSystemService(Context.WINDOW_SERVICE);
        DisplayMetrics outMetrics = new DisplayMetrics();
        wm.getDefaultDisplay().getMetrics(outMetrics);
        return outMetrics.widthPixels;
    }

    public LinearLayout getLinearLayout() {
        return mItemsLayout;
    }
}