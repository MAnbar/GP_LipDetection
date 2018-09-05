package com.gp.gp_dlib.detection;


import android.util.SparseArray;

import com.google.android.gms.vision.Detector;
import com.google.android.gms.vision.Frame;
import com.google.android.gms.vision.face.Face;

public class DlibFaceDetector extends Detector<Face> {
    private Detector<Face> mDelegate;

    public DlibFaceDetector(Detector<Face> delegate) {
        mDelegate = delegate;
    }

    public SparseArray<Face> detect(Frame frame) {
        // *** add your custom frame processing code here
        return mDelegate.detect(frame);
    }

    public boolean isOperational() {
        return mDelegate.isOperational();
    }

    public boolean setFocus(int id) {
        return mDelegate.setFocus(id);
    }
}