package com.company.pure.opencv;

import org.opencv.core.Core;
import org.opencv.core.Mat;
import org.opencv.core.MatOfRect;
import org.opencv.features2d.DescriptorExtractor;
import org.opencv.features2d.DescriptorMatcher;
import org.opencv.imgcodecs.Imgcodecs;
import org.opencv.objdetect.CascadeClassifier;

import java.io.File;
import java.io.IOException;
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by Titan on 03.03.2017.
 */
public class AnotherFaceSimilarity {

    static {
        System.loadLibrary(Core.NATIVE_LIBRARY_NAME);
    }

    private final Map<String, CascadeClassifier> classifiers = new HashMap<>();
    private final Map<String, Mat> imageCache = new HashMap<>();

    public AnotherFaceSimilarity(String cascadesFolderPath) throws IOException {
        Path path = Paths.get(cascadesFolderPath);

        DirectoryStream<Path> paths = Files.newDirectoryStream(path);

        for (Path filter : paths) {
            File file = filter.toFile();
            classifiers.put(file.getName(), new CascadeClassifier(file.getAbsolutePath()));
        }

    }

    public double getSimilarity(String absolutePath1, String absolutePath2) {

        Mat mat1 = getMat(absolutePath1);
        Mat mat2 = getMat(absolutePath2);

        Map<String, MatOfRect> featureLocations1 = getStringMatOfRectMap(mat1);
        Map<String, MatOfRect> featureLocations2 = getStringMatOfRectMap(mat2);

        Map<String, Mat> featureCarvings1 = getFeatureCarvings(mat1, featureLocations1);
        Map<String, Mat> featureCarvings2 = getFeatureCarvings(mat2, featureLocations2);


        DescriptorExtractor descriptorExtractor = DescriptorExtractor.create(DescriptorExtractor.SURF);

        DescriptorMatcher descriptorMatcher = DescriptorMatcher.create(DescriptorMatcher.FLANNBASED);

        descriptorMatcher.


    }

    private Map<String, Mat> getFeatureCarvings(Mat mat, Map<String, MatOfRect> featureLocations) {
        Map<String, Mat> stringMatMap = new HashMap<>();

        for (Map.Entry<String, MatOfRect> entry : featureLocations.entrySet()) {
            stringMatMap.put(entry.getKey(), mat.submat(entry.getValue().toArray()[0]));
        }

        return stringMatMap;
    }

    private Map<String, MatOfRect> getStringMatOfRectMap(Mat imageMat) {
        Map<String, MatOfRect> featureLocations = new HashMap<>();

        for (Map.Entry<String, CascadeClassifier> classifierEntry : classifiers.entrySet()) {
            MatOfRect matOfRect = new MatOfRect();
            classifierEntry.getValue().detectMultiScale(imageMat, matOfRect);
            featureLocations.put(classifierEntry.getKey(), matOfRect);
        }
        return featureLocations;
    }


    private Mat getMat(String absolutePath1) {
        if (imageCache.get(absolutePath1) == null) {
            imageCache.put(absolutePath1, Imgcodecs.imread(absolutePath1));
        }
        return imageCache.get(absolutePath1);
    }


}
