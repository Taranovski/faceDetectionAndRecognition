package com.company.pure.javacv;

import org.bytedeco.javacpp.opencv_face;

import static org.bytedeco.javacpp.opencv_face.createEigenFaceRecognizer;
import static org.bytedeco.javacpp.opencv_face.createFisherFaceRecognizer;
import static org.bytedeco.javacpp.opencv_face.createLBPHFaceRecognizer;

/**
 * Created by Titan on 08.02.2017.
 */
public enum RecognizerType {
    FISHER() {
        @Override
        public opencv_face.FaceRecognizer createRecognizer() {
            return createFisherFaceRecognizer();
        }
    },
    EIGEN() {
        @Override
        public opencv_face.FaceRecognizer createRecognizer() {
            return createEigenFaceRecognizer();
        }
    },
    LBPH() {
        @Override
        public opencv_face.FaceRecognizer createRecognizer() {
            return createLBPHFaceRecognizer();
        }
    };

    public abstract opencv_face.FaceRecognizer createRecognizer();
}
