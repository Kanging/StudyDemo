package acase.zhoukang.com.studydemo.view;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PointF;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.MotionEvent;
import android.view.View;

/**
 * 贝瑟尔曲线，qq拖拽效果
 * Created by zhoukang on 2017/9/21.
 */

public class BezierView extends View {

    private Paint mPaint;
    //拖拽圆圆心
    private PointF PressPoint;
    //固定圆圆心
    private PointF unPressPoint;
    //拖拽圆半径
    private int mPressRadius = 20;
    //固定圆半径
    private int mUnPressRadius = 15;
    //固定圆的最大半径
    private int MIX_UNPRESSRADIUS = 15;
    //固定圆的最小半径
    private int MIN_UNPRESSRADIUS = 6;



    public BezierView(Context context) {
        this(context,null);
    }

    public BezierView(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs,0);
    }

    public BezierView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);

        //初始化画笔
        mPaint = new Paint();
        mPaint.setDither(true);
        mPaint.setColor(Color.RED);
        mPaint.setAntiAlias(true);

        //格式转换
        mPressRadius = dip2dp(mPressRadius);
        mUnPressRadius = dip2dp(mUnPressRadius);
        MIN_UNPRESSRADIUS = dip2dp(MIN_UNPRESSRADIUS);
        MIX_UNPRESSRADIUS = dip2dp(MIX_UNPRESSRADIUS);

    }

    @Override
    protected void onDraw(Canvas canvas) {
        if(PressPoint==null&&unPressPoint==null){
            return;
        }

        //绘制拖拽圆
        canvas.drawCircle(PressPoint.x,PressPoint.y,mPressRadius,mPaint);

        //计算两圆的距离
        int distence = (int) Math.sqrt((PressPoint.x-unPressPoint.x)*(PressPoint.x-unPressPoint.x)+(PressPoint.y-unPressPoint.y)*(PressPoint.y-unPressPoint.y));
        mUnPressRadius = MIX_UNPRESSRADIUS-distence/20;
        Path bazier = getBazierPath();//

        //绘制固定圆
        if(bazier!=null){
            canvas.drawCircle(unPressPoint.x,unPressPoint.y,mUnPressRadius,mPaint);

            canvas.drawPath(bazier,mPaint);
        }
    }

    //绘制曲线
    private Path getBazierPath() {

        if(mUnPressRadius<MIN_UNPRESSRADIUS){//固定圆半径小于最小半径时，不需要再绘制曲线
            return null;
        }

        Path bazier = new Path();

        //角度a
        float a = (float) Math.atan((PressPoint.y-unPressPoint.y)/(PressPoint.x-unPressPoint.x));

        //四个点的位置
        float p0x = (float) (mUnPressRadius*Math.sin(a)+unPressPoint.x);
        float p0y = (float) (unPressPoint.y-mUnPressRadius*Math.cos(a));

        float p1x = (float) (mPressRadius*Math.sin(a)+PressPoint.x);
        float p1y = (float) (PressPoint.y-mPressRadius*Math.cos(a));

        float p2x = (float) (PressPoint.x-mPressRadius*Math.sin(a));
        float p2y = (float) (PressPoint.y+mPressRadius*Math.cos(a));


        float p3x = (float) (unPressPoint.x-mUnPressRadius*Math.sin(a));
        float p3y = (float) (unPressPoint.y+mUnPressRadius*Math.cos(a));


        //获取控制点，取两个圆心的中点为控制点
        PointF contrlPoint = new PointF((PressPoint.x+unPressPoint.x)/2,(PressPoint.y+unPressPoint.y)/2);

        bazier.moveTo(p0x,p0y);//移动到p0点

        bazier.quadTo(contrlPoint.x,contrlPoint.y,p1x,p1y);//曲线路径

        bazier.lineTo(p2x,p2y);

        bazier.quadTo(contrlPoint.x,contrlPoint.y,p3x,p3y);

        bazier.close();

        return bazier;
    }


    @Override
    public boolean onTouchEvent(MotionEvent event) {
        switch (event.getAction()){

            case MotionEvent.ACTION_DOWN:
                iniPoint(event.getX(),event.getY());

                break;
            case MotionEvent.ACTION_MOVE:
                upPoint(event.getX(),event.getY());
                break;

            case MotionEvent.ACTION_UP:
                break;

        }

        invalidate();

        return true;
    }


    /**
     * 移动该点
     * @param x
     * @param y
     */
    private void upPoint(float x, float y) {
        PressPoint.x = x;
        PressPoint.y = y;
    }

    /**
     * 按下便初始化该点
     * @param x
     * @param y
     */
    private void iniPoint(float x, float y) {
        PressPoint = new PointF(x,y);
        unPressPoint = new PointF(x,y);
    }

    /**
     * dip转dp
     * @param dip
     * @return
     */
    private int dip2dp(int dip){
        return (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP,dip,getResources().getDisplayMetrics());
    }
}
