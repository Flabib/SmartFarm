package academy.bangkit.c22.px442.smartfarm.presentation.utils;

import android.graphics.Bitmap;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;

public class JavaHelper {
    public static ByteBuffer imageToByteBuffer(Bitmap image, int imageSize) {
        ByteBuffer byteBuffer = ByteBuffer.allocateDirect(4 * imageSize * imageSize * 3);
        byteBuffer.order(ByteOrder.nativeOrder());

        int[] intValues  = new int[imageSize * imageSize];
        image.getPixels(intValues, 0, image.getWidth(), 0, 0, image.getWidth(), image.getWidth());

        int pixel = 0;
        for (int i = 0; i < imageSize; i++) {
            for (int j = 0; j < imageSize; j++) {
                int val = intValues[pixel++]; // RGB
                byteBuffer.putFloat(((val >> 16) & 0xFF) * (1.f / 1));
                byteBuffer.putFloat(((val >> 8) & 0xFF) * (1.f / 1));
                byteBuffer.putFloat((val & 0xFF) * (1.f / 1));
            }
        }

        return byteBuffer;
    }
}
