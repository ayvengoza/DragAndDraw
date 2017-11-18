package com.ayvengoza.draganddraw;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.PointF;
import android.graphics.Rect;
import android.os.Bundle;
import android.os.Parcelable;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by ayven on 18.11.2017.
 */

public class BoxDrawingView extends View {
    private static final String TAG = "BoxDrawingView.java";
    private static final String ARG_PARENT_STATE = "PARENT_STATE";
    private static final String ARG_BOXES_LIST = "BOXES_LIST";

    private Box mCurrentBox;
    private List<Box> mBoxen = new ArrayList<>();
    private Paint mBoxPaint;
    private Paint mBackgroundPaint;

    public BoxDrawingView(Context context){
        this(context, null);
    }

    public BoxDrawingView(Context context, AttributeSet attrs){
        super(context, attrs);

        mBoxPaint = new Paint();
        mBoxPaint.setColor(0x22ff0000);

        mBackgroundPaint = new Paint();
        mBackgroundPaint.setColor(0xfff8efe0);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        PointF current = new PointF(event.getX(), event.getY());
        String action = "";
        switch(event.getAction()){
            case MotionEvent.ACTION_DOWN:
                action = "Action_DOWN";
                mCurrentBox = new Box(current);
                mBoxen.add(mCurrentBox);
                break;
            case MotionEvent.ACTION_MOVE:
                action = "ACTION_MOVE";
                if(mCurrentBox!=null){
                    mCurrentBox.setCurrent(current);
                    invalidate();
                }
                break;
            case MotionEvent.ACTION_UP:
                action = "ACTION_UP";
                mCurrentBox = null;
                break;
            case MotionEvent.ACTION_CANCEL:
                action = "ACTION_CANCEL";
                mCurrentBox = null;
                break;
        }

        Log.i(TAG, action + " at x=" + current.x
        + ", y=" + current.y);
        return true;
    }

    @Override
    protected void onDraw(Canvas canvas) {
        canvas.drawPaint(mBackgroundPaint);

        for(Box box : mBoxen){
            float left = Math.min(box.getOrigin().x, box.getCurrent().x);
            float right = Math.max(box.getOrigin().x, box.getCurrent().x);
            float top = Math.min(box.getOrigin().y, box.getCurrent().y);
            float bottom = Math.max(box.getOrigin().y, box.getCurrent().y);
            canvas.drawRect(left,top, right, bottom, mBoxPaint);
        }
    }

    @Nullable
    @Override
    protected Parcelable onSaveInstanceState() {
        Bundle bundle = new Bundle();
        bundle.putParcelable(ARG_PARENT_STATE, super.onSaveInstanceState());
        ArrayList<Rect> rects = new ArrayList<>();
        for(Box box : mBoxen){
            rects.add(boxToRect(box));
        }
        bundle.putParcelableArrayList(ARG_BOXES_LIST, rects);
        return bundle;
    }

    @Override
    protected void onRestoreInstanceState(Parcelable state) {
        Bundle bundle = (Bundle) state;
        Parcelable parentState = bundle.getParcelable(ARG_PARENT_STATE);
        super.onRestoreInstanceState(parentState);
        ArrayList<Rect> rects = new ArrayList<>();
        rects = bundle.getParcelableArrayList(ARG_BOXES_LIST);
        for(Rect rect : rects){
            mBoxen.add(rectToBox(rect));
        }

    }

    private Rect boxToRect(Box box){
        float left = Math.min(box.getOrigin().x, box.getCurrent().x);
        float right = Math.max(box.getOrigin().x, box.getCurrent().x);
        float top = Math.min(box.getOrigin().y, box.getCurrent().y);
        float bottom = Math.max(box.getOrigin().y, box.getCurrent().y);
        Rect rect = new Rect((int)left,(int)top, (int)right, (int)bottom);
        return rect;
    }

    private Box rectToBox(Rect rect){
        Box box = new Box(new PointF(rect.left, rect.bottom));
        box.setCurrent(new PointF(rect.right, rect.top));
        return box;
    }
}
