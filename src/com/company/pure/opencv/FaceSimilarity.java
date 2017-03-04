package com.company.pure.opencv;

import org.opencv.core.Core;
import org.opencv.core.Mat;
import org.opencv.core.MatOfRect;
import org.opencv.core.Rect;
import org.opencv.imgcodecs.Imgcodecs;
import org.opencv.objdetect.CascadeClassifier;

import java.io.File;
import java.io.IOException;
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by Titan on 19.02.2017.
 */
public class FaceSimilarity {

    static {
        System.loadLibrary(Core.NATIVE_LIBRARY_NAME);
    }

    private final Map<String, CascadeClassifier> classifiers = new HashMap<>();
    private final Map<String, Mat> mats = new HashMap<>();
    private Map<String, Map<String, MatOfRect>> featureCache = new HashMap<>();

    public FaceSimilarity(String cascadesFolderPath) throws IOException {
        Path path = Paths.get(cascadesFolderPath);

        DirectoryStream<Path> paths = Files.newDirectoryStream(path);

        for (Path filter : paths) {
            File file = filter.toFile();
            classifiers.put(file.getName(), new CascadeClassifier(file.getAbsolutePath()));
        }

    }

    public double getSimilarity(String absolutePath1, String absolutePath2) {

        Map<String, MatOfRect> features1 = getFeatures(absolutePath1);
        Map<String, MatOfRect> features2 = getFeatures(absolutePath2);

        double similarity = 0;

        for (String filterName : classifiers.keySet()) {
            similarity = similarity + filterWeight(filterName) * getSimilarityByFeature(features1.get(filterName), features2.get(filterName));
        }

        return similarity;
    }

    private Mat getMat(String absolutePath1) {
        if (mats.get(absolutePath1) == null) {
            mats.put(absolutePath1, Imgcodecs.imread(absolutePath1));
        }
        return mats.get(absolutePath1);
    }

    private double getSimilarityByFeature(MatOfRect matOfRect1, MatOfRect matOfRect2) {
        Rect[] rects1 = matOfRect1.toArray();
        Rect[] rects2 = matOfRect2.toArray();

        Arrays.sort(rects1, new RectComparator());
        Arrays.sort(rects2, new RectComparator());

        double similarity = 0;

        similarity = similarity + stepWeights().get("detectedFeatureCount") * getSimilarityByFeatureCount(rects1.length, rects2.length);
        similarity = similarity + stepWeights().get("detectedFeatureSize") * getSimilarityByFeatureSize(rects1, rects2);
        similarity = similarity + stepWeights().get("detectedFeaturePosition") * getSimilarityByFeaturePosition(rects1, rects2);


        return similarity;
    }

    //can be overriden
    private double getSimilarityByFeatureCount(int length1, int length2) {
        return ((double) Math.min(length1, length2)) / Math.max(length1, length2);
    }

    //can be overriden
    private double getSimilarityByFeatureSize(Rect[] rects1, Rect[] rects2) {
        int commonLength = Math.min(rects1.length, rects2.length);

        double similarity = 0;
        double weight = 1.0 / commonLength;

        for (int i = 0; i < commonLength; i++) {
            double area1 = rects1[i].area();
            double area2 = rects2[i].area();

            similarity = similarity + weight * (Math.min(area1, area2) / Math.max(area1, area2));
        }

        return similarity;
    }

    //can be overriden
    private double getSimilarityByFeaturePosition(Rect[] rects1, Rect[] rects2) {
        int commonLength = Math.min(rects1.length, rects2.length);

        double similarity = 0;
        double weight = 1.0 / commonLength;

        for (int i = 0; i < commonLength; i++) {
            double position1 = rects1[i].x * rects1[i].y;
            double position2 = rects2[i].x * rects2[i].y;

            similarity = similarity + weight * (Math.min(position1, position2) / Math.max(position1, position2));
        }

        return similarity;
    }

    //can be overriden
    private Map<String, Double> stepWeights() {
        HashMap<String, Double> stringDoubleHashMap = new HashMap<>();

        stringDoubleHashMap.put("detectedFeatureCount", 0.2);
        stringDoubleHashMap.put("detectedFeatureSize", 0.4);
        stringDoubleHashMap.put("detectedFeaturePosition", 0.4);

        return stringDoubleHashMap;
    }

    //can be overriden
    private double filterWeight(String cascadeName) {
        return 1.0 / classifiers.size();
    }

    private Map<String, MatOfRect> getFeatures(String image) {

        if (featureCache.get(image) == null) {


            Map<String, MatOfRect> features = new HashMap<>();

            for (Map.Entry<String, CascadeClassifier> classifierEntry : classifiers.entrySet()) {
                MatOfRect matOfRect = new MatOfRect();
                classifierEntry.getValue().detectMultiScale(getMat(image), matOfRect);
                features.put(classifierEntry.getKey(), matOfRect);
            }

            featureCache.put(image, features);

        }
        return featureCache.get(image);
    }

    private static class RectComparator implements Comparator<Rect> {
        @Override
        public int compare(Rect r1, Rect r2) {
            int compareX = Integer.compare(r1.x, r2.x);
            return compareX == 0 ? Integer.compare(r1.y, r2.y) : compareX;
        }
    }
}
