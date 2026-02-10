package com.example.cuby.ui.views;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;

public class DrawingView extends View {

    private Path drawPath;
    private Paint drawPaint;
    private Paint canvasPaint;
    private Canvas drawCanvas;
    private Bitmap canvasBitmap;

    private int paintColor = 0xFF4CAF50; // default green
    private boolean isEraser = false;

    public DrawingView(Context context, AttributeSet attrs) {
        super(context, attrs);
        setupDrawing();
    }

    private void setupDrawing() {
        drawPath = new Path();

        drawPaint = new Paint();
        drawPaint.setAntiAlias(true);
        drawPaint.setStrokeWidth(20);
        drawPaint.setStyle(Paint.Style.STROKE);
        drawPaint.setStrokeJoin(Paint.Join.ROUND);
        drawPaint.setStrokeCap(Paint.Cap.ROUND);
        drawPaint.setColor(paintColor);

        canvasPaint = new Paint(Paint.DITHER_FLAG);
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        canvasBitmap = Bitmap.createBitmap(w, h, Bitmap.Config.ARGB_8888);
        drawCanvas = new Canvas(canvasBitmap);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        canvas.drawBitmap(canvasBitmap, 0, 0, canvasPaint);
        canvas.drawPath(drawPath, drawPaint);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        float x = event.getX();
        float y = event.getY();

        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                drawPath.moveTo(x, y);
                break;

            case MotionEvent.ACTION_MOVE:
                drawPath.lineTo(x, y);
                break;

            case MotionEvent.ACTION_UP:
                drawCanvas.drawPath(drawPath, drawPaint);
                drawPath.reset();
                break;
        }

        invalidate();
        return true;
    }

    // 🎨 NORMAL DRAW MODE
    public void setColor(String color) {
        isEraser = false;
        paintColor = Color.parseColor(color);
        drawPaint.setColor(paintColor);
        drawPaint.setXfermode(null);
    }

    // 🧽 ERASER MODE
    public void enableEraser() {
        isEraser = true;
        drawPaint.setXfermode(
                new PorterDuffXfermode(PorterDuff.Mode.CLEAR)
        );
    }

    public void startNew() {
        drawCanvas.drawColor(0, PorterDuff.Mode.CLEAR);
        invalidate();
    }

    public Bitmap getBitmap() {
        return canvasBitmap;
    }
}
