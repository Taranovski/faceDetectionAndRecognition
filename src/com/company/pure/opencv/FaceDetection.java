package com.company.pure.opencv;

import org.opencv.core.*;
import org.opencv.imgcodecs.Imgcodecs;
import org.opencv.imgproc.Imgproc;
import org.opencv.objdetect.CascadeClassifier;

/**
 * Created by Titan on 08.02.2017.
 */
public class FaceDetection {

    static {
        System.loadLibrary(Core.NATIVE_LIBRARY_NAME);
    }

    private CascadeClassifier faceDetector;

    public FaceDetection(String filename) {
        faceDetector = new CascadeClassifier(filename);
    }

    public void faceDetection(String inputFileName, String outputFileName) {

        System.out.println("Running DetectFaceDemo");
        // Create a face detector from the cascade file in the resources directory.
        Mat image = Imgcodecs.imread(inputFileName);
        // Detect faces in the image. MatOfRect is a special container class for Rect.
        MatOfRect faceDetections = new MatOfRect();
        faceDetector.detectMultiScale(image, faceDetections);

//        faceDetector.

        System.out.println(String.format("Detected %s faces", faceDetections.toArray().length));
        // Draw a bounding box around each face.
        for (Rect rect : faceDetections.toArray()) {
            Imgproc.rectangle(image, new Point(rect.x, rect.y), new Point(rect.x + rect.width, rect.y + rect.height), new Scalar(0, 255, 0));
        }
        // Save the visualized detection.
        System.out.println(String.format("Writing %s", outputFileName));
        Imgcodecs.imwrite(outputFileName, image);
    }
}
