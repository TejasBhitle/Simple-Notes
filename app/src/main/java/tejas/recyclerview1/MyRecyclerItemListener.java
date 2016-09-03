package tejas.recyclerview1;

import android.content.Context;

import android.support.v7.widget.RecyclerView;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;

public class MyRecyclerItemListener implements RecyclerView.OnItemTouchListener {


    public interface OnClickItemInterface{
         void  onItemClick(View view ,int position);
        void onLongPress(View view ,int position);
    }

    private OnClickItemInterface onClickItemInterface;
    GestureDetector gestureDetector;

    @Override
    public void onRequestDisallowInterceptTouchEvent(boolean disallowIntercept) {}

    @Override
    public void onTouchEvent(RecyclerView rv, MotionEvent e) {}


    @Override
    public boolean onInterceptTouchEvent(RecyclerView rv, MotionEvent e) {
        View childView = rv.findChildViewUnder(e.getX(),e.getY());
        if(childView != null && onClickItemInterface !=null && gestureDetector.onTouchEvent(e)){
                onClickItemInterface.onItemClick(childView,rv.getChildAdapterPosition(childView));
            return true;
        }
        return false;
    }

    //Constructor
    public MyRecyclerItemListener(Context context, OnClickItemInterface listener){
        onClickItemInterface = listener;
        gestureDetector = new GestureDetector(context, new GestureDetector.OnGestureListener()
        {
            @Override
            public boolean onSingleTapUp(MotionEvent motionEvent) {
                return true;
            }

            @Override
            public void onLongPress(MotionEvent motionEvent) {

            }

            @Override
            public boolean onScroll(MotionEvent motionEvent, MotionEvent motionEvent1, float v, float v1) {
                return false;
            }

            @Override
            public void onShowPress(MotionEvent motionEvent) {

            }
            @Override
            public boolean onDown(MotionEvent motionEvent) {
                return false;
            }

            @Override
            public boolean onFling(MotionEvent motionEvent, MotionEvent motionEvent1, float v, float v1) {
                return false;
            }
        }

        );
    }
}