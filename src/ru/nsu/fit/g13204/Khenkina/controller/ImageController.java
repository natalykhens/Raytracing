package ru.nsu.fit.g13204.Khenkina.controller;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.awt.image.ColorModel;
import java.io.File;
import java.io.IOException;

/**
 * Created by Natalia on 01.06.16.
 */
public class ImageController {

    public static void saveImage(BufferedImage image, File file) throws IOException {
        ImageIO.write(image, "BMP", file);
    }

    public static BufferedImage gammaCorrection(BufferedImage image, double gamma){
        BufferedImage newImage = new BufferedImage(image.getWidth(), image.getHeight(), image.getType());

        ColorModel colorModel = image.getColorModel();
        for(int i = 0; i < image.getHeight(); ++i){
            for(int j = 0; j < image.getWidth(); ++j){
                int color = image.getRGB(j, i);
                int red = gammaFunction(colorModel.getRed(color), gamma);
                int green = gammaFunction(colorModel.getGreen(color), gamma);
                int blue = gammaFunction(colorModel.getBlue(color), gamma);
                int rgb = new Color(red, green, blue).getRGB();
                newImage.setRGB(j, i, rgb);
            }
        }
        return newImage;
    }

    private static int gammaFunction(int argument, double gamma){
        int result = (int)(Math.pow((double)argument / 255.0, 1.0 / gamma) * 255);
        return checkBoundaries(result);
    }

    private static int checkBoundaries(int color){
        if(color < 0){
            return 0;
        }
        if(color > 255){
            return 255;
        }
        return color;
    }
}
