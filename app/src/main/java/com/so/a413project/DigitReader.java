
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
    // MLM path file. the digit.tflite is in the assets folder
    private final String MODEL_PATH = "digit.tflite";
    // TF Interpreter
    private Interpreter tflite;

    // Input buffer
    private ByteBuffer inputBuffer = null;
    // Output
    private float[][] mnistOutput = null;

    // Model parameters for the bitmap image
    private static final int NUMBER_LENGTH = 10;
    private static final int DIM_BATCH_SIZE = 1;
    private static final int DIM_IMG_SIZE_X = 28;
    private static final int DIM_IMG_SIZE_Y = 28;
    private static final int DIM_PIXEL_SIZE = 1;
    private static final int BYTE_SIZE_OF_FLOAT = 4;

    // Constructor for the interpreter and buffer
    public DigitReader(Activity activity) {
        try {
            // Loading the model and the interpreter
            tflite = new Interpreter(loadModelFile(activity));
            if (tflite == null) {
                Log.e(TAG, "Failed to initialize TensorFlow Lite interpreter.");
            }

            // input buffer with the right size
            inputBuffer = ByteBuffer.allocateDirect(BYTE_SIZE_OF_FLOAT * DIM_BATCH_SIZE * DIM_IMG_SIZE_X * DIM_IMG_SIZE_Y * DIM_PIXEL_SIZE);
            inputBuffer.order(ByteOrder.nativeOrder());

            //  output buffer
            mnistOutput = new float[DIM_BATCH_SIZE][NUMBER_LENGTH];

            //we included log statements to prevent the app from crashing if the program threw an exception
        } catch (IOException e) {
            Log.e(TAG, "IOException error in loading the tflite ", e);
        }

        if (inputBuffer == null) {
            Log.e(TAG, "Input buffer is not properly coded or initialized ");
        }
    }

    // Method to load the TFLite model file
    private MappedByteBuffer loadModelFile(Activity activity) throws IOException {
        AssetFileDescriptor fileDescriptor = activity.getAssets().openFd(MODEL_PATH);
        FileInputStream inputStream = new FileInputStream(fileDescriptor.getFileDescriptor());
        FileChannel fileChannel = inputStream.getChannel();
        long startOffset = fileDescriptor.getStartOffset();
        long declaredLength = fileDescriptor.getDeclaredLength();
        return fileChannel.map(FileChannel.MapMode.READ_ONLY, startOffset, declaredLength);
    }

    // Detects digit from bitmap
    public int detectDigit(Bitmap bitmap) {
        if (bitmap == null) {
            Log.e(TAG, "Received null bitmap for detection.");
            return -1;  // Invalid value to indicate an error
        }

        if (inputBuffer == null) {
            Log.e(TAG, "Input buffer is null.");
            return -1;  // Exit if input buffer is not initialized
        }

        preprocess(bitmap);
        runInference();
        return postprocess();
    }

    // Preprocesses the bitmap
    private void preprocess(Bitmap bitmap) {
        if (bitmap == null || inputBuffer == null) {
            Log.e(TAG, "Bitmap or inputBuffer is null.");
            return;
        }

        Bitmap resizedBitmap = Bitmap.createScaledBitmap(bitmap, DIM_IMG_SIZE_X, DIM_IMG_SIZE_Y, true);
        int width = resizedBitmap.getWidth();
        int height = resizedBitmap.getHeight();
        int[] pixels = new int[width * height];

        resizedBitmap.getPixels(pixels, 0, width, 0, 0, width, height);

        inputBuffer.rewind();  // makes sure that the buffer is ready for new data
        for (int pixel : pixels) {
            int channel = pixel & 0xff;
            inputBuffer.putFloat((0xff - channel) / 255.0f);
        }
    }

    // Method to run model inference
    protected void runInference() {
        if (tflite != null) {
            tflite.run(inputBuffer, mnistOutput);
        } else {
            Log.e(TAG, "TensorFlow Lite interpreter is null. Cannot run inference.");
        }
    }

    // Method to postprocess the output and get the predicted digit
    private int postprocess() {
        if (mnistOutput == null) {
            Log.e(TAG, "Output array is null.");
            return -1;  // Exit if output array is null
        }

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
