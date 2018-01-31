package com.mymenu.android;

import android.content.Context;
import android.graphics.Color;
import android.graphics.Point;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.animation.DecelerateInterpolator;
import android.widget.FrameLayout;
import android.widget.RelativeLayout;
import android.widget.Scroller;

/**
 * Created by 小巷有狗 on 2018/1/30.
 */

public class MainUI extends RelativeLayout{

    private Context context;
    private FrameLayout leftMenu;
    private FrameLayout middleMenu;
    private FrameLayout rightMenu;
    private Scroller mScroller;
    private FrameLayout middleMask;

    public static final int LEFT_ID=0xaabbcc;
    public static final int MIDDLE_ID=0xbbaacc;
    public static final int RIGHT_ID=0Xccaabb;

    public MainUI(Context context){
        super(context);
        initView(context);
    }

    public MainUI(Context context, AttributeSet attrs) {
        super(context, attrs);
        initView(context);
    }

    private void initView(Context context){
        this.context=context;
        mScroller=new Scroller(context,new DecelerateInterpolator());
        leftMenu=new FrameLayout(context);
        middleMenu=new FrameLayout(context);
        middleMask=new FrameLayout(context);
        rightMenu=new FrameLayout(context);
        leftMenu.setBackgroundColor(Color.RED);
        middleMenu.setBackgroundColor(Color.GREEN);
        middleMask.setBackgroundColor(0x88000000);
        rightMenu.setBackgroundColor(Color.RED);


        //添加ID
        leftMenu.setId(LEFT_ID);
        middleMenu.setId(MIDDLE_ID);
        rightMenu.setId(RIGHT_ID);

        //将菜单添加到界面中
        addView(leftMenu);
        addView(middleMenu);
        addView(rightMenu);
        addView(middleMask);

        middleMask.setAlpha(0);//设置可见度为完全透明
    }

    @Override
    public void scrollTo(int x, int y) {
        super.scrollTo(x, y);
        int curX=Math.abs(getScrollX());
        float scale=curX/(float)leftMenu.getMeasuredWidth();
        middleMask.setAlpha(scale);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        middleMenu.measure(widthMeasureSpec,heightMeasureSpec);
        middleMask.measure(widthMeasureSpec,heightMeasureSpec);
        int realWidth=MeasureSpec.getSize(widthMeasureSpec);
        int tempWidthMeasure=MeasureSpec.makeMeasureSpec(
                (int)(realWidth*0.6f),MeasureSpec.EXACTLY);
        leftMenu.measure(tempWidthMeasure,heightMeasureSpec);
        rightMenu.measure(tempWidthMeasure,heightMeasureSpec);
    }

    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {
        super.onLayout(changed, l, t, r, b);
        middleMenu.layout(l,t,r,b);
        middleMask.layout(l,t,r,b);
        leftMenu.layout(l-leftMenu.getMeasuredWidth(),t,r,b);
        rightMenu.layout(l+middleMenu.getMeasuredWidth(),t,
                l+middleMenu.getMeasuredWidth()+rightMenu.getMeasuredWidth(),b);

    }

    private boolean isTestCompete=false;
    private boolean isLeftRightEvent=false;

    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
        if (!isTestCompete){
            getEventType(ev);
            return true;
        }
        if (isLeftRightEvent){
            switch (ev.getActionMasked()){
                case MotionEvent.ACTION_MOVE:
                    int curScrollX=getScrollX();//滚动距离
                    int dis_x= (int) (ev.getX()-point.x);//滑动距离
                    int expectX=-dis_x+curScrollX;
                    int finalX=0;
                    if (expectX<0){
                        finalX=Math.max(expectX,-leftMenu.getMeasuredWidth());
                    }else{
                        finalX=Math.min(expectX,rightMenu.getMeasuredWidth());
                    }
                    scrollTo(finalX,0);
                    point.x= (int) ev.getX();
                case MotionEvent.ACTION_UP:
                case MotionEvent.ACTION_CANCEL:
                    curScrollX=getScrollX();
                    if (Math.abs(curScrollX)>leftMenu.getMeasuredWidth()>>1){
                        if (curScrollX<0){
                            mScroller.startScroll(curScrollX,0,
                                    -leftMenu.getMeasuredWidth()-curScrollX,0,200);
                        }else{
                            mScroller.startScroll(curScrollX,0,
                                    leftMenu.getMeasuredWidth()-curScrollX,0,200);
                        }
                    }else {
                        mScroller.startScroll(curScrollX,0,-curScrollX,0,200);
                    }
                    invalidate();

                    isLeftRightEvent=true;
                    isTestCompete=false;

            }
        }else{
            switch (ev.getActionMasked()){
                case MotionEvent.ACTION_UP:
                    isLeftRightEvent=false;
                    isTestCompete=false;
                    break;
            }
        }

        return super.dispatchTouchEvent(ev);
    }

    @Override
    public void computeScroll() {
        super.computeScroll();
        if (!mScroller.computeScrollOffset()){
            return;
        }
        int tempX=mScroller.getCurrX();
        scrollTo(tempX,0);
    }

    private Point point=new Point();
    private static final int TEST_DIS=20;

    private void getEventType(MotionEvent ev) {
        switch (ev.getActionMasked()){
            case MotionEvent.ACTION_DOWN:
                point.x= (int) ev.getX();
                point.y= (int) ev.getY();
                super.dispatchTouchEvent(ev);
                break;

            case MotionEvent.ACTION_MOVE:
                int dx= (int) Math.abs(ev.getX()-point.x);
                int dy= (int) Math.abs(ev.getY()-point.y);
                if(dx>dy&&dx>=TEST_DIS){//左右滑动
                    isLeftRightEvent=true;
                    isTestCompete=true;
                    point.x= (int) ev.getX();
                    point.y= (int) ev.getY();
                }else if (dy>dx&&dy>=TEST_DIS){//上下滑动
                    isLeftRightEvent=false;
                    isTestCompete=true;
                    point.x= (int) ev.getX();
                    point.y= (int) ev.getY();
                }
                break;

            case MotionEvent.ACTION_UP:
            case MotionEvent.ACTION_CANCEL:
                super.dispatchTouchEvent(ev);
                isLeftRightEvent=false;
                isTestCompete=false;
                break;
        }
    }
}
