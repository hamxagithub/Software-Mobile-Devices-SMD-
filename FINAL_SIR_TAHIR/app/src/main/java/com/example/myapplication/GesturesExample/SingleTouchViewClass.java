package com.example.myapplication.GesturesExample;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Path;
import android.util.AttributeSet;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;



public class SingleTouchViewClass extends View {


    public Paint paint=new Paint();
    public Path path=new Path();
    GestureDetector gestureDetector;
    Context context;

    public SingleTouchViewClass(Context context, @Nullable AttributeSet attrs) {

        super(context, attrs);
        this.context=context;
        gestureDetector=new GestureDetector(context,new GestureListener());
    }

    @Override
    protected void onDraw(@NonNull Canvas canvas) {
        canvas.drawPath(path,paint);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        float x=event.getX();
        float y=event.getY();

        switch (event.getAction()){
            case MotionEvent.ACTION_DOWN:
                path.moveTo(x,y);
             return true;
            case MotionEvent.ACTION_MOVE:
                path.lineTo(x,y);
                return true;
            case MotionEvent.ACTION_UP:

                break;
        }

         gestureDetector.onTouchEvent(event);
        invalidate();
        return true;
    }

    private  class GestureListener extends GestureDetector.SimpleOnGestureListener{
        @Override
        public boolean onSingleTapConfirmed(MotionEvent e) {
            return super.onSingleTapConfirmed(e);
        }

        @Override
        public boolean onDoubleTap(MotionEvent e) {

            path.reset();
            return true;
        }

        @Override
        public boolean onDown(MotionEvent e) {
            return super.onDown(e);
        }
    }
}
