package com.hynet.heebit.components.widget.zxing.view;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.view.View;

import com.google.zxing.ResultPoint;
import com.hynet.heebit.components.utils.ViewUtil;
import com.hynet.heebit.components.widget.zxing.camera.CameraManager;

import java.util.Collection;
import java.util.HashSet;


public final class ViewfinderView extends View {

    private static final int[] SCANNER_ALPHA = {0, 64, 128, 192, 255, 192, 128, 64};
    //    private static final int[] SCANNER_ALPHA = {255, 255, 255, 255, 255, 255, 255, 255};
    private Context context;
    private final Paint paint;
    private int maskColor;
    private int frameColor;
    private int laserColor;
    private int borderColor;
    private int scannerAlpha;
    private String text;
    private int textColor;
    private int textSize;
    private Collection<ResultPoint> possibleResultPoints;
    private boolean laserLinePortrait = true;
    private int i = 0;
    private int laserLineTop;
    private int animationDelay = 0;
    private int laserMoveSpeed = 3;
    private CameraManager cameraManager;

    public ViewfinderView(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.context = context;
        paint = new Paint();
        scannerAlpha = 0;
        possibleResultPoints = new HashSet<>(5);
    }

    public void setMaskColor(int maskColor) {
        this.maskColor = maskColor;
    }

    public void setFrameColor(int frameColor) {
        this.frameColor = frameColor;
    }

    public void setLaserColor(int laserColor) {
        this.laserColor = laserColor;
    }

    public void setBorderColor(int borderColor) {
        this.borderColor = borderColor;
    }

    public void setText(String text) {
        this.text = text;
    }

    public void setTextColor(int textColor) {
        this.textColor = textColor;
    }

    public void setTextSize(int textSize) {
        this.textSize = textSize;
    }

    public void setLaserMoveSpeed(int laserMoveSpeed) {
        this.laserMoveSpeed = laserMoveSpeed;
    }

    public void setCameraManager(CameraManager cameraManager) {
        this.cameraManager = cameraManager;
    }

    @Override
    public void onDraw(Canvas canvas) {
        if (cameraManager == null) {
            return; // not ready yet, early draw before done configuring
        }
        Rect frame = cameraManager.getFramingRect();
        Rect previewFrame = cameraManager.getFramingRectInPreview();
        if (frame == null || previewFrame == null) {
            return;
        }
        int width = canvas.getWidth();
        int height = canvas.getHeight();
        paint.setColor(maskColor);
        canvas.drawRect(0, 0, width, frame.top, paint);
        canvas.drawRect(0, frame.top, frame.left, frame.bottom + 1, paint);
        canvas.drawRect(frame.right + 1, frame.top, width, frame.bottom + 1, paint);
        canvas.drawRect(0, frame.bottom + 1, width, height, paint);
        drawFrame(canvas, frame);
        drawCorner(canvas, frame);
        drawLaserLine(canvas, frame);
        drawText(canvas, frame);
    }

    private void drawFrame(Canvas canvas, Rect frame) {
        paint.setColor(frameColor);
        canvas.drawRect(frame.left, frame.top, frame.right + 1, frame.top + 2, paint);
        canvas.drawRect(frame.left, frame.top + 2, frame.left + 2, frame.bottom - 1, paint);
        canvas.drawRect(frame.right - 1, frame.top, frame.right + 1, frame.bottom - 1, paint);
        canvas.drawRect(frame.left, frame.bottom - 1, frame.right + 1, frame.bottom + 1, paint);
    }

    private void drawCorner(Canvas canvas, Rect frame) {
        paint.setColor(borderColor);
        canvas.drawRect(frame.left, frame.top, frame.left + 30, frame.top + 1, paint);
        canvas.drawRect(frame.left, frame.top, frame.left + 1, frame.top + 30, paint);
        canvas.drawRect(frame.right - 30, frame.top, frame.right, frame.top + 1, paint);
        canvas.drawRect(frame.right - 1, frame.top, frame.right, frame.top + 30, paint);
        canvas.drawRect(frame.left, frame.bottom - 30, frame.left + 1, frame.bottom, paint);
        canvas.drawRect(frame.left, frame.bottom - 1, frame.left + 30, frame.bottom, paint);
        canvas.drawRect(frame.right - 1, frame.bottom - 30, frame.right, frame.bottom, paint);
        canvas.drawRect(frame.right - 30, frame.bottom - 1, frame.right, frame.bottom, paint);
    }

    private void drawLaserLine(Canvas canvas, Rect frame) {
        paint.setColor(laserColor);
        paint.setAlpha(SCANNER_ALPHA[scannerAlpha]);
        scannerAlpha = (scannerAlpha + 1) % SCANNER_ALPHA.length;
        if (laserLinePortrait) {
            if ((i += 5) < frame.bottom - frame.top) {
                canvas.drawRect(frame.left + 2, frame.top - 2 + i, frame.right - 1, frame.top + 2 + i, paint);
                invalidate();
            } else {
                i = 0;
            }
            if (laserLineTop == 0) {
                laserLineTop = frame.top;
            }
            laserLineTop += laserMoveSpeed;
            if (laserLineTop >= frame.bottom) {
                laserLineTop = frame.top;
            }
            if (animationDelay == 0) {
                animationDelay = (int) ((1.0f * 1000 * laserMoveSpeed) / (frame.bottom - frame.top));
            }
            postInvalidateDelayed(animationDelay, frame.left, frame.top, frame.right, frame.bottom);
        } else {
            float left = frame.left + (frame.right - frame.left) / 2 - 2;
            canvas.drawRect(left, frame.top, left + 2, frame.bottom - 2, paint);
        }
    }

    private void drawText(Canvas canvas, Rect frame) {
        paint.setColor(textColor);
        paint.setTextSize(textSize);
        paint.setTextAlign(Paint.Align.CENTER);
        canvas.drawText(text, canvas.getWidth() / 2, frame.bottom + ViewUtil.Companion.getInstance().dp2px(context, 25), paint);
    }

    public void drawViewfinder() {
        invalidate();
    }

    public void addPossibleResultPoint(ResultPoint point) {
        possibleResultPoints.add(point);
    }
}
