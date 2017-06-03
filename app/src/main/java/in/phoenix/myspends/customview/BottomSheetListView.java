package in.phoenix.myspends.customview;

import android.content.Context;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.widget.AbsListView;
import android.widget.ListView;

/**
 * Created by Charan.Br on 2/24/2017.
 */
public final class BottomSheetListView extends ListView {

    public BottomSheetListView(Context context) {
        super(context);
    }

    public BottomSheetListView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public BottomSheetListView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    /*@Override
    public void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int heightMeasureSpec_custom = MeasureSpec.makeMeasureSpec(
                Integer.MAX_VALUE >> 2, MeasureSpec.AT_MOST);
        super.onMeasure(widthMeasureSpec, heightMeasureSpec_custom);
        ViewGroup.LayoutParams params = getLayoutParams();
        params.height = getMeasuredHeight();
    }*/

    @Override
    public boolean onInterceptTouchEvent(MotionEvent motionEvent) {
        View view = getChildAt(getChildCount() - 1);

        int diffBottom = (view.getBottom() - (getHeight() + getScrollY()));
        if (motionEvent.getAction() == MotionEvent.ACTION_DOWN) {
            if (diffBottom == 0) {
                return false;
            }
        }

     /*//Need more improvement on this logic. Do not uncomment
    int diffTop = (view.getTop() - (getHeight() + getScrollY()));
    if (motionEvent.getAction() == MotionEvent.ACTION_DOWN) {
        if (diffTop < 0) {
            return true;
        }
    }*/

        return super.onInterceptTouchEvent(motionEvent);
    }

    @Override
    public boolean onTouchEvent(MotionEvent motionEvent) {
        if (canScrollVertically(this)) {
            getParent().requestDisallowInterceptTouchEvent(true);
        }
        return super.onTouchEvent(motionEvent);
    }

    private boolean canScrollVertically(AbsListView absListView) {

        boolean canScroll = false;

        if (absListView != null && absListView.getChildCount() > 0) {

            boolean isOnTop = absListView.getFirstVisiblePosition() != 0 || absListView.getChildAt(0).getTop() != 0;
            boolean isAllItemsVisible = isOnTop && getLastVisiblePosition() == absListView.getChildCount();

            if (isOnTop || isAllItemsVisible)
                canScroll = true;
        }

        return canScroll;
    }

}
