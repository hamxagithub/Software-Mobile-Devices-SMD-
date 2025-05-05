package com.example.myapplication.GesturesExample.GestureExampleTwo;

import android.content.Context;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PointF;
import android.util.SparseArray;
import android.view.MotionEvent;
import android.view.View;

public class MultiTouchViewClass extends View {
    public Paint paint,mypaint;
    public Path path;
    public static final int size =60;
    public SparseArray<PointF> activepoint;
    public int color[]= {Color.GREEN, Color.RED, Color.BLUE, Color.YELLOW, Color.CYAN, Color.MAGENTA, Color.DKGRAY};
    public MultiTouchViewClass(Context context) {
        super(context);
        pointers();
    }

    private void pointers() {
        activepoint=new SparseArray<PointF>();
        paint=new Paint(Paint.ANTI_ALIAS_FLAG);
        paint.setColor(Color.GREEN);
        paint.setStyle(Paint.Style.FILL_AND_STROKE);
        mypaint=new Paint(Paint.ANTI_ALIAS_FLAG);
        mypaint.setTextSize(50);


    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        int pointIndex=event.getActionIndex();
        int pointerId=event.getPointerId(pointIndex);
        int actionMask=event.getActionMasked();
        switch (actionMask){

            case MotionEvent.ACTION_DOWN:
            case MotionEvent.ACTION_POINTER_DOWN: {
                PointF pointF = new PointF();
                pointF.x = event.getX(pointIndex);
                pointF.y = event.getY(pointIndex);
                activepoint.put(pointerId, pointF);
            }   break;
            case MotionEvent.ACTION_MOVE: {
                for (int size=event.getPointerCount(),i = 0; i < size; i++) {
                    PointF pointF=activepoint.get(event.getPointerId(i));
                    if (pointF != null) {
                        pointF.x = event.getX(i);
                        pointF.y = event.getY(i);
                    }

                }
            }
                break;
            case MotionEvent.ACTION_UP:
            case MotionEvent.ACTION_POINTER_UP:
            case MotionEvent.ACTION_CANCEL:
                activepoint.remove(pointerId);
                break;
            default:
                for (int size=event.getPointerCount(),i = 0; i < size; i++)
                {
                    PointF pointF=activepoint.get(event.getPointerId(i));
                    if (pointF != null) {
                        pointF.x = event.getX(i);
                        pointF.y = event.getY(i);
                    }
                }
        }
        return false;
    }
}
