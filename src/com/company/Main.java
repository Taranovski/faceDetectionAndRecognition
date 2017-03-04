package com.company;

import com.company.pure.javacv.FaceRecognition;
import com.company.pure.javacv.RecognizerType;
import com.company.pure.opencv.FaceDetection;
import com.company.pure.opencv.FaceSimilarity;

import java.io.File;
import java.io.IOException;
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class Main {

    public static void main(String[] args) throws IOException {
//        String inputFileName = "c:\\projects\\faces\\WIN_20170204_15_59_11_Pro.jpg";
        String inputFileName = "c:\\projects\\faces\\16754625_976501972450171_1330113844_n.jpg";

        String pathToFaceDetectionConfiguration = "c:\\projects\\opencv31\\build\\etc\\lbpcascades\\lbpcascade_frontalface.xml";
        String outputFileName = "c:\\projects\\faceDetection.png";

        String cascadesFolderPath = "c:\\projects\\faces\\cascades\\";
        String samplesFolder = "c:\\projects\\faces\\sample_faces\\";







//        mainFaceDetection(inputFileName, pathToFaceDetectionConfiguration, outputFileName);
//        mainFaceSimilarity(cascadesFolderPath, samplesFolder);
//        mainDetection(inputFileName);
//        mainFaceRecognitionWithDB(inputFileName);
    }

    private static void mainFaceRecognitionWithDB(String inputFileName) {
        String trainingDir = "c:\\projects\\faces\\photos\\";
        new FaceRecognition(trainingDir, RecognizerType.FISHER).faceRecognition(inputFileName);
    }

    private static void mainDetection(String inputFileName) {
        String base = "c:\\projects\\opencv32\\build\\etc\\haarcascades\\";
        String[] filters = new String[]{
                base + "haarcascade_eye.xml",
                base + "haarcascade_eye_tree_eyeglasses.xml",
                base + "haarcascade_frontalcatface.xml",
                base + "haarcascade_frontalcatface_extended.xml",
                base + "haarcascade_frontalface_alt.xml",
                base + "haarcascade_frontalface_alt_tree.xml",
                base + "haarcascade_frontalface_alt2.xml",
                base + "haarcascade_frontalface_default.xml",
                base + "haarcascade_fullbody.xml",
                base + "haarcascade_lefteye_2splits.xml",
                base + "haarcascade_licence_plate_rus_16stages.xml",
                base + "haarcascade_lowerbody.xml",
                base + "haarcascade_profileface.xml",
                base + "haarcascade_righteye_2splits.xml",
                base + "haarcascade_russian_plate_number.xml",
                base + "haarcascade_smile.xml",
                base + "haarcascade_upperbody.xml",
        };

        String prefix = "c:\\projects\\faceDetection_filter";

        String suffix  =".png";

        int i = 0;
        for (String filter : filters) {
            new FaceDetection(filter).faceDetection(inputFileName, prefix + i + suffix);
            i++;
        }
    }

    private static void mainFaceDetection(String inputFileName, String pathToFaceDetectionConfiguration, String outputFileName) {
        new FaceDetection(pathToFaceDetectionConfiguration).faceDetection(inputFileName, outputFileName);
    }

    private static void mainFaceSimilarity(String cascadesFolderPath, String samplesFolder) throws IOException {
        FaceSimilarity faceSimilarity = new FaceSimilarity(cascadesFolderPath);
        List<Path> samplesPaths = getPaths(samplesFolder);

        for (Path sample1 : samplesPaths) {
            System.out.print(sample1.toFile().getName() + "\t");
        }
        System.out.println();

        for (Path sample1 : samplesPaths) {
            System.out.print(sample1.toFile().getName() + "\t");
            for (Path sample2 : samplesPaths) {
                File file1 = sample1.toFile();
                File file2 = sample2.toFile();
                System.out.print((float)faceSimilarity.getSimilarity(file1.getAbsolutePath(), file2.getAbsolutePath()) + "\t");
            }
            System.out.println();
        }
        System.out.println();
    }

    private static List<Path> getPaths(String samplesFolder) throws IOException {
        Path samples = Paths.get(samplesFolder);

        List<Path> samplesPaths = new ArrayList<>();

        DirectoryStream<Path> samples1 = Files.newDirectoryStream(samples);
        for (Path sample1 : samples1) {
            samplesPaths.add(sample1);
        }
        samples1.close();
        return samplesPaths;
    }

}
