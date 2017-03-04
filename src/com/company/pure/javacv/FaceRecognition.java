package com.company.pure.javacv;

import org.bytedeco.javacpp.opencv_core;
import org.bytedeco.javacpp.opencv_face;
import org.bytedeco.javacpp.opencv_imgcodecs;

import java.io.File;
import java.io.FilenameFilter;
import java.nio.IntBuffer;

import static org.bytedeco.javacpp.opencv_core.CV_32SC1;

/**
 * Created by Titan on 08.02.2017.
 */
public class FaceRecognition {
    private static final int CV_LOAD_IMAGE_GRAYSCALE = 0;
    private String trainingDir;
    private RecognizerType recognizerType;

    public FaceRecognition(String trainingDir, RecognizerType recognizerType) {
        this.trainingDir = trainingDir;
        this.recognizerType = recognizerType;
    }

    public void faceRecognition(String inputFileName) {
        opencv_core.Mat testImage = opencv_imgcodecs.imread(inputFileName, CV_LOAD_IMAGE_GRAYSCALE);

        File root = new File(trainingDir);

        FilenameFilter imgFilter = new FilenameFilter() {
            public boolean accept(File dir, String name) {
                name = name.toLowerCase();
                return name.endsWith(".jpg") || name.endsWith(".pgm") || name.endsWith(".png");
            }
        };

        File[] imageFiles = root.listFiles(imgFilter);

        opencv_core.MatVector images = new opencv_core.MatVector(imageFiles.length);

        opencv_core.Mat labels = new opencv_core.Mat(imageFiles.length, 1, CV_32SC1);
        IntBuffer labelsBuf = labels.createBuffer();

        int counter = 0;

        for (File image : imageFiles) {
            opencv_core.Mat img = opencv_imgcodecs.imread(image.getAbsolutePath(), CV_LOAD_IMAGE_GRAYSCALE);

            int label = Integer.parseInt(image.getName().split("\\-")[0]);

            images.put(counter, img);

            labelsBuf.put(counter, label);

            counter++;
        }

        opencv_face.FaceRecognizer faceRecognizer = recognizerType.createRecognizer();

//        faceRecognizer.

        faceRecognizer.train(images, labels);

        int predictedLabel = faceRecognizer.predict(testImage);

        System.out.println("Predicted label: " + predictedLabel);
    }
}
