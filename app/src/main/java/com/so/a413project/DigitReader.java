package com.so.a413project;

import android.app.Activity;
import android.content.res.AssetFileDescriptor;
import android.graphics.Bitmap;
import android.util.Log;
import org.tensorflow.lite.Interpreter;

import java.io.FileInputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.MappedByteBuffer;
import java.nio.channels.FileChannel;

public class DigitReader {
    private static final String TAG = "DigitReader";
    //MLM path file
    private final String MODEL_PATH = "digit.tflite";
    //TF Interpreter
    private Interpreter tflite;

    //Input buffer
    private ByteBuffer inputBuffer = null;
    //Output
    private float[][] mnistOutput = null;

    //Model parameters for the bitmap image
    private static final int NUMBER_LENGTH = 10;
    private static final int DIM_BATCH_SIZE = 1;
    private static final int DIM_IMG_SIZE_X = 28;
    private static final int DIM_IMG_SIZE_Y = 28;
    private static final int DIM_PIXEL_SIZE = 1;
    private static final int BYTE_SIZE_OF_FLOAT = 4;

    //Constructor to initialize interpreter and buffer
    public DigitReader(Activity activity) {
        try {
            tflite = new Interpreter(loadModelFile(activity));
            inputBuffer = ByteBuffer.allocateDirect(BYTE_SIZE_OF_FLOAT * DIM_BATCH_SIZE * DIM_IMG_SIZE_X * DIM_IMG_SIZE_Y * DIM_PIXEL_SIZE);
            inputBuffer.order(ByteOrder.nativeOrder());
            mnistOutput = new float[DIM_BATCH_SIZE][NUMBER_LENGTH];
        } catch (IOException e) {
            Log.e(TAG, "IOException loading the tflite file", e);
        }
    }

    //Method to load the TFlite file
    private MappedByteBuffer loadModelFile(Activity activity) throws IOException {
        AssetFileDescriptor fileDescriptor = activity.getAssets().openFd(MODEL_PATH);
        FileInputStream inputStream = new FileInputStream(fileDescriptor.getFileDescriptor());
        FileChannel fileChannel = inputStream.getChannel();
        long startOffset = fileDescriptor.getStartOffset();
        long declaredLength = fileDescriptor.getDeclaredLength();
        return fileChannel.map(FileChannel.MapMode.READ_ONLY, startOffset, declaredLength);
    }

    //Detects digit from bitmap
    public int detectDigit(Bitmap bitmap) {
        preprocess(bitmap);
        runInference();
        return postprocess();
    }

    //Preprocesses the bitmap
    private void preprocess(Bitmap bitmap) {
        Bitmap resizedBitmap = Bitmap.createScaledBitmap(bitmap, DIM_IMG_SIZE_X, DIM_IMG_SIZE_Y, true);
        int width = resizedBitmap.getWidth();
        int height = resizedBitmap.getHeight();
        int[] pixels = new int[width * height];

        resizedBitmap.getPixels(pixels, 0, width, 0, 0, width, height);

        inputBuffer.rewind();
        for (int pixel : pixels) {
            int channel = pixel & 0xff;
            inputBuffer.putFloat((0xff - channel) / 255.0f);
        }
    }

    //Method to run model inference
    protected void runInference() {
        tflite.run(inputBuffer, mnistOutput);
    }

    //Method to postprocess the output and get the predicted digit
    private int postprocess() {
        int maxIndex = -1;
        float maxValue = -1;
        for (int i = 0; i < mnistOutput[0].length; i++) {
            if (mnistOutput[0][i] > maxValue) {
                maxValue = mnistOutput[0][i];
                maxIndex = i;
            }
            Log.d(TAG, "Output for " + i + ": " + mnistOutput[0][i]);
        }
        return maxIndex;
    }
}
