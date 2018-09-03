package com.gp.gp_dlib.detection;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PointF;

import com.google.android.gms.vision.face.Face;
import com.google.android.gms.vision.face.Landmark;
import com.gp.gp_dlib.views.GraphicOverlay;

import java.util.List;


public class FaceGraphic extends GraphicOverlay.Graphic {
    private static final float FACE_POSITION_RADIUS = 5.0f;
    private static final float ID_TEXT_SIZE = 40.0f;
    private static final float ID_Y_OFFSET = 50.0f;
    private static final float ID_X_OFFSET = -50.0f;
    private static final float BOX_STROKE_WIDTH = 5.0f;

    private static final int COLOR_CHOICES[] = {
            Color.BLUE,
            Color.CYAN,
            Color.GREEN,
            Color.MAGENTA,
            Color.RED,
            Color.WHITE,
            Color.YELLOW
    };
    private static int mCurrentColorIndex = 0;

    private Paint mFacePositionPaint;
    private Paint mIdPaint;
    private Paint mBoxPaint;

    //    private volatile Face mFace;
    private volatile PointF leftMouthPoint;
    private volatile PointF rightMouthPoint;
    private volatile PointF bottomMouthPoint;
    private int mFaceId;
    private float mFaceHappiness;

    public FaceGraphic(GraphicOverlay overlay) {
        super(overlay);

        mCurrentColorIndex = (mCurrentColorIndex + 1) % COLOR_CHOICES.length;
        final int selectedColor = COLOR_CHOICES[mCurrentColorIndex];

        mFacePositionPaint = new Paint();
        mFacePositionPaint.setColor(selectedColor);

        mIdPaint = new Paint();
        mIdPaint.setColor(selectedColor);
        mIdPaint.setTextSize(ID_TEXT_SIZE);

        mBoxPaint = new Paint();
        mBoxPaint.setColor(selectedColor);
        mBoxPaint.setStyle(Paint.Style.STROKE);
        mBoxPaint.setStrokeWidth(20.0f);

//        mBoxPaint.setStrokeWidth(BOX_STROKE_WIDTH);
    }

    public void setId(int id) {
        mFaceId = id;
    }


    /**
     * Updates the face instance from the detection of the most recent frame.  Invalidates the
     * relevant portions of the overlay to trigger a redraw.
     */
    public void updateMouth(PointF leftMouthPoint, PointF rightMouthPoint, PointF bottomMouthPoint) {
//        mFace = face;
        this.leftMouthPoint = leftMouthPoint;
        this.rightMouthPoint = rightMouthPoint;
        this.bottomMouthPoint = bottomMouthPoint;
        postInvalidate();
    }

    /**
     * Draws the face annotations for position on the supplied canvas.
     */
    @Override
    public void draw(Canvas canvas) {
        PointF lMouthPoint = leftMouthPoint;
        PointF rMouthPoint = rightMouthPoint;
        PointF bMouthPoint = bottomMouthPoint;
        if (lMouthPoint == null || rMouthPoint == null || bMouthPoint == null) {
            return;
        }
        System.out.println("POINTS ARE NOT NULL");

        canvas.drawCircle(translateX(lMouthPoint.x), translateY(lMouthPoint.y), FACE_POSITION_RADIUS, mBoxPaint);
        canvas.drawCircle(translateX(rMouthPoint.x), translateY(rMouthPoint.y), FACE_POSITION_RADIUS, mBoxPaint);
        canvas.drawCircle(translateX(bMouthPoint.x), translateY(bMouthPoint.y), FACE_POSITION_RADIUS, mBoxPaint);
    }
}