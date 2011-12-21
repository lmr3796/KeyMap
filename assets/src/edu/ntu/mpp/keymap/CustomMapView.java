package edu.ntu.mpp.keymap;

import com.google.android.maps.MapView;
import android.content.Context;
import android.graphics.Canvas;
import android.util.AttributeSet;
import android.view.MotionEvent;

import com.google.android.maps.GeoPoint;

public class CustomMapView extends MapView {

    private int oldZoomLevel = -1;
    private GeoPoint oldCenterGeoPoint;
    private OnPanAndZoomListener mListener;

    public CustomMapView(Context context, String apiKey) {
        super(context, apiKey);
    }

    public CustomMapView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public CustomMapView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    public void setOnPanListener(OnPanAndZoomListener listener) {
        mListener = listener;
    }

    @Override
    public boolean onTouchEvent(MotionEvent ev) {
        if (ev.getAction() == MotionEvent.ACTION_UP) {
            GeoPoint centerGeoPoint = this.getMapCenter();
            if (oldCenterGeoPoint == null || 
                    (oldCenterGeoPoint.getLatitudeE6() != centerGeoPoint.getLatitudeE6()) ||
                    (oldCenterGeoPoint.getLongitudeE6() != centerGeoPoint.getLongitudeE6()) ) {
                mListener.onPan();
            }
            oldCenterGeoPoint = this.getMapCenter();
        }
        return super.onTouchEvent(ev);
    }

    @Override
    protected void dispatchDraw(Canvas canvas) {
        super.dispatchDraw(canvas);
        if (getZoomLevel() != oldZoomLevel) {
            mListener.onZoom();
            oldZoomLevel = getZoomLevel();
        }
    }


}
