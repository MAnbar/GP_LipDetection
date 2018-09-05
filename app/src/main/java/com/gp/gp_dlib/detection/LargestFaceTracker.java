package com.gp.gp_dlib.detection;

import android.graphics.PointF;
import android.util.SparseArray;

import com.google.android.gms.vision.Tracker;
import com.google.android.gms.vision.face.Face;
import com.google.android.gms.vision.face.FaceDetector;
import com.google.android.gms.vision.face.Landmark;
import com.gp.gp_dlib.views.GraphicOverlay;

/**
 * Tracks the eye positions and state over time, managing an underlying graphic which renders googly
 * eyes over the source video.<p>
 *
 * To improve eye tracking performance, it also helps to keep track of the previous landmark
 * proportions relative to the detected face and to interpolate landmark positions for future
 * updates if the landmarks are missing.  This helps to compensate for intermediate frames where the
 * face was detected but one or both of the eyes were not detected.  Missing landmarks can happen
 * during quick movements due to camera image blurring.
 */
public class LargestFaceTracker extends Tracker<Face> {

    private GraphicOverlay mOverlay;
    private FaceGraphic mFaceGraphic;

    private PointF leftMouthPoint;
    private PointF rightMouthPoint;
    private PointF bottomMouthPoint;


    // Record the previously seen proportions of the landmark locations relative to the bounding box
    // of the face.  These proportions can be used to approximate where the landmarks are within the
    // face bounding box if the eye landmark is missing in a future update.
    private SparseArray<PointF> mPreviousProportions = new SparseArray<>();

    //==============================================================================================
    // Methods
    //==============================================================================================

    public LargestFaceTracker(GraphicOverlay overlay) {
        mOverlay = overlay;
    }

    /**
     * Resets the underlying face graphic and associated physics state.
     */
    @Override
    public void onNewItem(int id, Face face) {
        mFaceGraphic = new FaceGraphic(mOverlay);
        System.out.println("New Face Detected");
    }

    /**
     * Updates the positions to the underlying graphic, according to the most
     * recent face detection results.
     */
    @Override
    public void onUpdate(FaceDetector.Detections<Face> detectionResults, Face face) {
        if(face==null)
        {
            System.out.println("FACE IS NULL");
            return;
        }
        if(mFaceGraphic==null)
        {
            System.out.println("FACEGraphic IS NULL");
            return;
        }
        mOverlay.add(mFaceGraphic);


        updatePreviousProportions(face);

        leftMouthPoint=getLandmarkPosition(face,Landmark.LEFT_MOUTH);
        rightMouthPoint=getLandmarkPosition(face,Landmark.RIGHT_MOUTH);
        bottomMouthPoint=getLandmarkPosition(face,Landmark.BOTTOM_MOUTH);

        if(leftMouthPoint==null||rightMouthPoint==null||bottomMouthPoint==null){
            System.out.println("POINT IS NULL");
            return;
        }
        //Change To CLASS
        mFaceGraphic.updateMouth(leftMouthPoint, rightMouthPoint, bottomMouthPoint);
    }

    /**
     * Hide the graphic when the corresponding face was not detected.  This can happen for
     * intermediate frames temporarily (e.g., if the face was momentarily blocked from
     * view).
     */
    @Override
    public void onMissing(FaceDetector.Detections<Face> detectionResults) {
        mOverlay.remove(mFaceGraphic);
    }

    /**
     * Called when the face is assumed to be gone for good. Remove the googly eyes graphic from
     * the overlay.
     */
    @Override
    public void onDone() {
        mOverlay.remove(mFaceGraphic);
    }

    //==============================================================================================
    // Private
    //==============================================================================================

    private void updatePreviousProportions(Face face) {
        for (Landmark landmark : face.getLandmarks()) {
            PointF position = landmark.getPosition();
            float xProp = (position.x - face.getPosition().x) / face.getWidth();
            float yProp = (position.y - face.getPosition().y) / face.getHeight();
            mPreviousProportions.put(landmark.getType(), new PointF(xProp, yProp));
        }
    }

    /**
     * Finds a specific landmark position, or approximates the position based on past observations
     * if it is not present.
     */
    private PointF getLandmarkPosition(Face face, int landmarkId) {
        for (Landmark landmark : face.getLandmarks()) {
            if (landmark.getType() == landmarkId) {
                return landmark.getPosition();
            }
        }

        PointF prop = mPreviousProportions.get(landmarkId);
        if (prop == null) {
            return null;
        }

        float x = face.getPosition().x + (prop.x * face.getWidth());
        float y = face.getPosition().y + (prop.y * face.getHeight());
        return new PointF(x, y);
    }
}