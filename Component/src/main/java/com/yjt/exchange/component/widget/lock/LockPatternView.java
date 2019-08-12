package com.hynet.heebit.components.widget.lock;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.os.Handler;
import android.os.Vibrator;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;

import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import com.hynet.heebit.components.constant.Constant;
import com.hynet.heebit.components.constant.Regex;
import com.hynet.heebit.components.utils.LogUtil;
import com.hynet.heebit.components.utils.MessageUtil;
import com.hynet.heebit.components.utils.StringUtil;
import com.hynet.heebit.components.utils.ThreadPoolUtil;
import com.hynet.heebit.components.widget.lock.listener.OnPointSelectedListener;
import com.hynet.heebit.components.widget.lock.listener.implement.NoneHighLightMode;

import java.util.Iterator;
import java.util.List;
import java.util.Set;

public class LockPatternView extends View {

    private int lengthPx;
    private int cellLength;
    protected NodeDrawable[][] nodeDrawables;
    private Paint paint;
    private OnPointSelectedListener onPointSelectedListener;
    private boolean isDisplay;
    private boolean isExtension;
    private boolean isTactileFeedback;
    private boolean isOnce;
    private boolean isTouchEnable;
    private Point touchPoint;
    private Point touchCell;
    private int touchThresHold;
    private List<Point> points;
    private Set<Point> pointsPool;
    private Handler handler;
    private Vibrator vibrator;

    // private Matrix mMatrix;

    public LockPatternView(Context context, AttributeSet attrs) {
        super(context, attrs, 0);
        lengthPx = Constant.Lock.DEFAULT_LENGTH_PX;
        nodeDrawables = new NodeDrawable[0][0];
        paint = new Paint();
        paint.setColor(Constant.Lock.EDGE_COLOR);
        paint.setStrokeCap(Paint.Cap.ROUND);
        paint.setFlags(Paint.ANTI_ALIAS_FLAG);
        onPointSelectedListener = new NoneHighLightMode();
        isDisplay = false;
        isExtension = false;
        touchPoint = new Point(-1, -1);
        touchCell = new Point(-1, -1);
        vibrator = (Vibrator) getContext().getSystemService(Context.VIBRATOR_SERVICE);
        // mMatrix = new Matrix();
    }

    public Handler getHandler() {
        return handler;
    }

    public void setHandler(Handler handler) {
        this.handler = handler;
    }

    protected void clearMark(List<Point> pattern) {
        for (Point e : pattern) {
            nodeDrawables[e.x][e.y].setNodeState(Constant.Lock.STATE_UNSELECTED);
        }
    }

    private void loadResult(List<Point> result, OnPointSelectedListener listener) {
        for (int ii = 0; ii < result.size(); ii++) {
            Point e = result.get(ii);
            NodeDrawable node = nodeDrawables[e.x][e.y];
            node.setNodeState(listener.setOnPointSelectedListener(node, ii, result.size(), e.x, e.y, Constant.Lock.DEFAULT_LENGTH_NODES));
            if (ii < result.size() - 1) {
                Point f = result.get(ii + 1);
                Point centerE = nodeDrawables[e.x][e.y].getPoint();
                Point centerF = nodeDrawables[f.x][f.y].getPoint();
                nodeDrawables[e.x][e.y].setAngle((float) Math.atan2(centerE.y - centerF.y, centerE.x - centerF.x));
            }
        }
    }

    private void appendPattern(List<Point> result, Point node) {
        NodeDrawable nodeDraw = nodeDrawables[node.x][node.y];
        nodeDraw.setNodeState(Constant.Lock.STATE_SELECTED);
        if (result.size() > 0) {
            Point tailNode = result.get(result.size() - 1);
            NodeDrawable tailDraw = nodeDrawables[tailNode.x][tailNode.y];
            Point tailCenter = tailDraw.getPoint();
            Point nodeCenter = nodeDraw.getPoint();
            tailDraw.setAngle((float) Math.atan2(tailCenter.y - nodeCenter.y, tailCenter.x - nodeCenter.x));
        }
        result.add(node);
    }

    private void handleResult() {
        isDisplay = true;
        ThreadPoolUtil.Companion.execute(new Runnable() {

            @Override
            public void run() {
                loadResult(points, onPointSelectedListener);
                if (isOnce) {
                    handler.sendMessage(MessageUtil.Companion.getInstance().getMessage(Constant.Lock.PRACTICE_RESULT_ONCE, getResult(points)));
                } else {
                    handler.sendMessage(MessageUtil.Companion.getInstance().getMessage(Constant.Lock.PRACTICE_RESULT_TWICE, getResult(points)));
                }
            }
        });
    }

    private String getResult(List<Point> points) {
        List<String> results = Lists.newArrayList();
        for (Point point : points) {
//             results.add(point.toString());
            String[] indexs = point.toString().split(Regex.COMMA.getRegext());
            results.add(String.valueOf(Integer.parseInt(indexs[0]) + (Integer.parseInt(indexs[1]) * 2 + Integer.parseInt(indexs[1]))));
             LogUtil.Companion.getInstance().print(results);
        }
        return StringUtil.append(false, results);
    }

    public void resetOldResult() {
        clearMark(points);
        points.clear();
        pointsPool.clear();
        isDisplay = false;
    }

    @SuppressLint("DrawAllocation")
    // @Override
    // protected void onDraw(Canvas canvas) {
    // drawView(canvas);
    // canvas.save();
    // mMatrix.preScale(Constant.Lock.WIDTH_ZOOM_SCALE, Constant.Lock.HEIGHT_ZOOM_SCALE);
    // canvas.setDensity(DensityUtil.getDensityDpi(getContext()));
    // mMatrix.postTranslate(canvas.getWidth()
    // * Constant.Lock.WIDTH_TRANSLATE_SCALE, canvas.getHeight()
    // * Constant.Lock.HEIGHT_TRANSLATE_SCALE);
    // canvas.setMatrix(mMatrix);
    // drawView(canvas);
    // canvas.restore();
    // mMatrix.reset();
    // }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        drawView(canvas);
    }

    private void drawView(Canvas canvas) {
        // draw pattern edges first
        Point edgeStart, edgeEnd;
        Iterator<Point> iterator = new CenterIterator(points.iterator());

        if (iterator.hasNext()) {
            edgeStart = iterator.next();
            while (iterator.hasNext()) {
                edgeEnd = iterator.next();
                canvas.drawLine(edgeStart.x, edgeStart.y, edgeEnd.x, edgeEnd.y, paint);
                edgeStart = edgeEnd;
            }
            if (isExtension) {
                canvas.drawLine(edgeStart.x, edgeStart.y, touchPoint.x, touchPoint.y, paint);
            }
        }

        // then draw nodes
        for (int y = 0; y < Constant.Lock.DEFAULT_LENGTH_NODES; y++) {
            for (int x = 0; x < Constant.Lock.DEFAULT_LENGTH_NODES; x++) {
                nodeDrawables[x][y].draw(canvas);
            }
        }
    }

    // private int getMeasuredInfo(int measureSpec) {
    // int result = 0;
    // int size = MeasureSpec.getSize(measureSpec);
    // switch (MeasureSpec.getMode(measureSpec)) {
    // case MeasureSpec.UNSPECIFIED:
    // result = Constant.Lock.DEFAULT_LENGTH_PX;
    // break;
    // case MeasureSpec.AT_MOST:
    // result = Math.max(size, Constant.Lock.DEFAULT_LENGTH_PX);
    // break;
    // case MeasureSpec.EXACTLY:
    // default:
    // result = size;
    // }
    // return result;
    // }

    // @Override
    // protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
    // setMeasuredDimension(getMeasuredInfo(widthMeasureSpec),
    // getMeasuredInfo(heightMeasureSpec));
    // }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int length;
        int width = MeasureSpec.getSize(widthMeasureSpec);
        int wMode = MeasureSpec.getMode(widthMeasureSpec);
        int height = MeasureSpec.getSize(heightMeasureSpec);
        int hMode = MeasureSpec.getMode(heightMeasureSpec);
        if (wMode == MeasureSpec.UNSPECIFIED && hMode == MeasureSpec.UNSPECIFIED) {
            length = Constant.Lock.DEFAULT_LENGTH_PX;
            setMeasuredDimension(length, length);
        } else if (wMode == MeasureSpec.UNSPECIFIED) {
            length = height;
        } else if (hMode == MeasureSpec.UNSPECIFIED) {
            length = width;
        } else {
            length = Math.min(width, height);
        }
        setMeasuredDimension(length, length);
    }

    @SuppressLint("ClickableViewAccessibility")
    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if (isTouchEnable) {
            switch (event.getAction()) {
                case MotionEvent.ACTION_DOWN:
                    isExtension = true;
                    if (isDisplay) {
                        resetOldResult();
                    }
                case MotionEvent.ACTION_MOVE:
                    float x = event.getX();
                    float y = event.getY();
                    touchPoint.x = (int) x;
                    touchPoint.y = (int) y;
                    touchCell.x = (int) x / cellLength;
                    touchCell.y = (int) y / cellLength;
                    if (touchCell.x < 0 || touchCell.x >= Constant.Lock.DEFAULT_LENGTH_NODES || touchCell.y < 0 || touchCell.y >= Constant.Lock.DEFAULT_LENGTH_NODES) {
                        break;
                    }
                    Point nearestCenter = nodeDrawables[touchCell.x][touchCell.y].getPoint();
                    int dist = (int) Math.sqrt(Math.pow(x - nearestCenter.x, 2) + Math.pow(y - nearestCenter.y, 2));
                    if (dist < touchThresHold && !pointsPool.contains(touchCell)) {
                        if (isTactileFeedback) {
                            vibrator.vibrate(Constant.Lock.TACTILE_FEEDBACK_DURATION);
                        }
                        Point newPoint = new Point(touchCell);
                        if (points.size() > 0) {
                            Point tail = points.get(points.size() - 1);
                            Point delta = new Point(newPoint.x - tail.x, newPoint.y - tail.y);
                            int gcd = Math.abs(PatternGenerator.computeGcd(delta.x, delta.y));
                            if (gcd > 1) {
                                for (int ii = 1; ii < gcd; ii++) {
                                    Point inside = new Point(tail.x + delta.x / gcd * ii, tail.y + delta.y / gcd * ii);
                                    if (!pointsPool.contains(inside)) {
                                        appendPattern(points, inside);
                                        pointsPool.add(inside);
                                    }
                                }
                            }
                        }
                        appendPattern(points, newPoint);
                        pointsPool.add(newPoint);
                    }
                    break;
                case MotionEvent.ACTION_UP:
                    isExtension = false;
                    handleResult();
                    break;
                default:
                    return super.onTouchEvent(event);
            }
            invalidate();
        }
        return true;
    }

    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        lengthPx = Math.min(w, h);
        cellLength = lengthPx / Constant.Lock.DEFAULT_LENGTH_NODES;
        nodeDrawables = new NodeDrawable[Constant.Lock.DEFAULT_LENGTH_NODES][Constant.Lock.DEFAULT_LENGTH_NODES];
        // float nodeDiameter = ((float) cellLength) *
        // Constant.Lock.CELL_NODE_RATIO;
        // paint.setStrokeWidth(nodeDiameter * Constant.Lock.NODE_EDGE_RATIO);
        float diameter = (float) (cellLength * Constant.Lock.CELL_NODE_RATIO / 1.25);
        // paint.setStrokeWidth(diameter * Constant.Lock.NODE_EDGE_RATIO / 2);
        paint.setStrokeWidth(5);
        touchThresHold = (int) (diameter / 2);

        for (int y = 0; y < Constant.Lock.DEFAULT_LENGTH_NODES; y++) {
            for (int x = 0; x < Constant.Lock.DEFAULT_LENGTH_NODES; x++) {
                Point center = new Point(x * cellLength + cellLength / 2, y * cellLength + cellLength / 2);
                nodeDrawables[x][y] = new NodeDrawable(diameter, center);
            }
        }
        if (!isOnce) {
            loadResult(points, onPointSelectedListener);
        }
    }

    public OnPointSelectedListener getOnPointSelectedListener() {
        return onPointSelectedListener;
    }

    public void setResultMode(boolean isOnce) {
        isDisplay = false;
        this.isOnce = isOnce;
        points = Lists.newArrayList();
        pointsPool = Sets.newHashSet();
    }

    public void setTactileFeedbackEnabled(boolean enabled) {
        isTactileFeedback = enabled;
    }

    public void setTouchEnabled(boolean enabled) {
        isTouchEnable = enabled;
    }

    public boolean getTactileFeedbackEnabled() {
        return isTactileFeedback;
    }

    private class CenterIterator implements Iterator<Point> {
        private Iterator<Point> mIterator;

        public CenterIterator(Iterator<Point> iterator) {
            mIterator = iterator;
        }

        @Override
        public boolean hasNext() {
            return mIterator.hasNext();
        }

        @Override
        public Point next() {
            Point node = mIterator.next();
            return nodeDrawables[node.x][node.y].getPoint();
        }

        @Override
        public void remove() {
            throw new UnsupportedOperationException();
        }
    }

}
