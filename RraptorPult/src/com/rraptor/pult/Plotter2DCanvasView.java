package com.rraptor.pult;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;

import com.rraptor.pult.core.DeviceControlService;
import com.rraptor.pult.model.Line2D;
import com.rraptor.pult.model.Point2D;

public class Plotter2DCanvasView extends View {

    public enum LineDrawingStatus {
        NORMAL, DRAWING_PROGRESS, DRAWING_ERROR, DRAWN
    }

    private final int PAPER_WIDTH = 300;

    private final int PAPER_HEIGHT = 216;

    private DeviceControlService devControlService;

    private final Paint paint = new Paint();
    private final Point2D p1 = new Point2D(0, 0);
    private final Point2D p2 = new Point2D(PAPER_WIDTH - 1, 0);
    private final Point2D p3 = new Point2D(PAPER_WIDTH - 1, PAPER_HEIGHT - 1);
    private final Point2D p4 = new Point2D(0, PAPER_HEIGHT - 1);

    private final List<Line2D> drawingLines = new ArrayList<Line2D>();
    private Point2D canvasP1;
    private Point2D canvasP2;
    private Point2D canvasP3;
    private Point2D canvasP4;

    private final List<Line2D> canvasDrawingLines = new ArrayList<Line2D>();

    private Point2D userPoint1 = null;

    public Plotter2DCanvasView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public void clearDrawing() {
        drawingLines.clear();
        canvasDrawingLines.clear();
        resetLineStatus();
        this.invalidate();
    }

    public List<Line2D> getDrawingLines() {
        return drawingLines;
    }

    public LineDrawingStatus getLineStatus(final Line2D line) {
        return devControlService == null ? LineDrawingStatus.NORMAL
                : devControlService.getDeviceDrawingManager().getLineStatus(
                        line);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        translateDrawing();

        // draw working area
        paint.setColor(Color.WHITE);
        canvas.drawRect((float) canvasP1.getX(), (float) canvasP1.getY(),
                (float) canvasP3.getX(), (float) canvasP3.getY(), paint);

        paint.setColor(Color.RED);
        paint.setStrokeWidth(1);

        canvas.drawLine((float) canvasP1.getX(), (float) canvasP1.getY(),
                (float) canvasP2.getX(), (float) canvasP2.getY(), paint);
        canvas.drawLine((float) canvasP2.getX(), (float) canvasP2.getY(),
                (float) canvasP3.getX(), (float) canvasP3.getY(), paint);
        canvas.drawLine((float) canvasP3.getX(), (float) canvasP3.getY(),
                (float) canvasP4.getX(), (float) canvasP4.getY(), paint);
        canvas.drawLine((float) canvasP4.getX(), (float) canvasP4.getY(),
                (float) canvasP1.getX(), (float) canvasP1.getY(), paint);

        paint.setTextSize(10);
        canvas.drawText("0", (float) canvasP1.getX() + 2,
                (float) canvasP1.getY() + 10, paint);
        canvas.drawText("y", (float) canvasP2.getX() - 10,
                (float) canvasP2.getY() + 10, paint);
        canvas.drawText("x", (float) canvasP4.getX() + 2,
                (float) canvasP4.getY() - 2, paint);

        // draw drawing lines
        int i = 0;
        for (final Line2D line : canvasDrawingLines) {
            final LineDrawingStatus status = getLineStatus(drawingLines.get(i));
            switch (status) {
            case NORMAL:
                paint.setColor(Color.BLUE);
                paint.setStrokeWidth(2);
                break;
            case DRAWING_PROGRESS:
                paint.setColor(Color.YELLOW);
                paint.setStrokeWidth(2);
                break;
            case DRAWING_ERROR:
                paint.setColor(Color.RED);
                paint.setStrokeWidth(2);
                break;
            case DRAWN:
                paint.setColor(Color.GREEN);
                paint.setStrokeWidth(2);
                break;
            }

            final Point2D start = line.getStart();
            final Point2D end = line.getEnd();
            canvas.drawLine((float) start.getX(), (float) start.getY(),
                    (float) end.getX(), (float) end.getY(), paint);
            i++;
        }

        // draw user point if set ()
        if (userPoint1 != null) {
            paint.setColor(Color.BLUE);
            canvas.drawCircle((float) userPoint1.getX(),
                    (float) userPoint1.getY(), 5, paint);
        }
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if (isEnabled()) {
            final Point2D point = new Point2D((int) event.getX(),
                    (int) event.getY());
            if (userPoint1 == null) {
                userPoint1 = point;
            } else {
                drawingLines.add(new Line2D(translateCanvasPoint(userPoint1),
                        translateCanvasPoint((point))));
                userPoint1 = null;
            }
            this.invalidate();
        }
        return super.onTouchEvent(event);
    }

    public void resetLineStatus() {
        postInvalidate();
    }

    public void setDeviceControlService(
            final DeviceControlService devControlService) {
        this.devControlService = devControlService;
    }

    public void setDrawingLines(List<Line2D> lines) {
        drawingLines.clear();
        drawingLines.addAll(lines);
        canvasDrawingLines.clear();
        resetLineStatus();
        this.invalidate();
    }

    @Override
    public void setEnabled(boolean enabled) {
        this.userPoint1 = null;
        super.setEnabled(enabled);
    }

    public void setLineStatus(final Line2D line, final LineDrawingStatus status) {
        postInvalidate();
    }

    /**
     * Convert canvas point to drawing coordinate system.
     * 
     * @param canvasPoint
     * @return
     */
    private Point2D translateCanvasPoint(Point2D canvasPoint) {
        int canvasWidth = this.getWidth();
        int canvasHeight = this.getHeight();

        float scaleFactor = (float) canvasWidth / (float) PAPER_WIDTH;
        int dy = (int) (canvasHeight - PAPER_HEIGHT * scaleFactor) / 2;

        final Point2D drawingPoint = new Point2D(
                (int) (canvasPoint.getX() / scaleFactor), (int) ((canvasHeight
                        - dy - canvasPoint.getY()) / scaleFactor));
        return drawingPoint;
    }

    private void translateDrawing() {
        // translate working area bounds
        canvasP1 = translateDrawingPoint(p1);
        canvasP2 = translateDrawingPoint(p2);
        canvasP3 = translateDrawingPoint(p3);
        canvasP4 = translateDrawingPoint(p4);

        // translate drawing line
        canvasDrawingLines.clear();
        for (Line2D line : drawingLines) {
            canvasDrawingLines.add(new Line2D(translateDrawingPoint(line
                    .getStart()), translateDrawingPoint(line.getEnd())));
        }
    }

    /**
     * Translate point from drawing to canvas coordinate system: drawing (0,0)
     * point is in bottom left corner, and canvas (0,0) point is top left
     * corner. And scale it to working area bounds.
     * 
     * @return
     */
    private Point2D translateDrawingPoint(Point2D drawingPoint) {
        int canvasWidth = this.getWidth();
        int canvasHeight = this.getHeight();

        float scaleFactor = (float) canvasWidth / (float) PAPER_WIDTH;
        int dy = (int) (canvasHeight - PAPER_HEIGHT * scaleFactor) / 2;

        final Point2D canvasPoint = new Point2D(
                (int) (drawingPoint.getX() * scaleFactor), (int) (canvasHeight
                        - dy - drawingPoint.getY() * scaleFactor));
        return canvasPoint;
    }
}
