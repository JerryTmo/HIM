package com.example.util;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.IOException;

public class ImageUtils {

    public static ImageInfo getImageInfo(byte[] imageBytes) throws IOException {
        ImageInfo imageInfo = new ImageInfo();

        try (ByteArrayInputStream bis = new ByteArrayInputStream(imageBytes)) {
            BufferedImage bufferedImage = ImageIO.read(bis);
            if (bufferedImage != null) {
                imageInfo.setWidth(bufferedImage.getWidth());
                imageInfo.setHeight(bufferedImage.getHeight());
            }
        }

        return imageInfo;
    }

    public static class ImageInfo {
        private int width;
        private int height;

        public int getWidth() {
            return width;
        }

        public void setWidth(int width) {
            this.width = width;
        }

        public int getHeight() {
            return height;
        }

        public void setHeight(int height) {
            this.height = height;
        }
    }
}