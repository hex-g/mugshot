package hive.mugshot.storage;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.util.Random;
import java.util.regex.Pattern;

public final class ImageUtils {
  private static final String IMAGE_PATTERN = "(^.+\\.(gif|png|bmp|jpeg|jpg)$)";

  private ImageUtils(){
  }

  public static boolean validateIfHasAnImageAsExtension(final String image){
    final var pattern = Pattern.compile(IMAGE_PATTERN);
    final var matcher = pattern.matcher(image);
    return matcher.matches();
  }

  public static BufferedImage resizeImageToSquare(final BufferedImage inputtedImage,final int imageSizeInPixels) {
    // multi-pass bilinear div 2
    final var bufferedImageWithNewSize = new BufferedImage(imageSizeInPixels, imageSizeInPixels, BufferedImage.TYPE_INT_RGB);
    final var reSizer = bufferedImageWithNewSize.createGraphics();
    var resizingMode=
        ( inputtedImage.getHeight() < imageSizeInPixels )
            ? RenderingHints.VALUE_INTERPOLATION_NEAREST_NEIGHBOR
            : RenderingHints.VALUE_INTERPOLATION_BILINEAR;
    reSizer.setRenderingHint(RenderingHints.KEY_INTERPOLATION, resizingMode);
    reSizer.drawImage(inputtedImage, 0, 0, imageSizeInPixels, imageSizeInPixels, null);
    reSizer.dispose();
    return bufferedImageWithNewSize;
  }

  public static BufferedImage generateRandomImage(){
    final int yellow = 0xF6BD60;
    final int orange = 0xE9724C;
    final int gray = 0xE8E9EB;
    final int blue = 0x5C9EAD;
    final int black = 0x313638;
    final var colorsCombinations=new int[][]{
        {yellow, black},
        {orange, black},
        {blue, black},
        {blue, gray},
        {orange, yellow}
    };
    final var combinationIndex = new Random().nextInt(colorsCombinations.length);
    final var img = new BufferedImage(9, 9, BufferedImage.TYPE_INT_RGB);
    final var maxH = img.getHeight();
    final var maxV = img.getWidth();
    for (int vertical = 0; vertical < maxV; vertical++) {
      for (int horizontal = 0; horizontal < maxH; horizontal++) {
        var pixel = 0;
        if (Math.random() < 0.7) {
          pixel = colorsCombinations[combinationIndex][0];
        }else{
          pixel = colorsCombinations[combinationIndex][1];
        }
        img.setRGB(horizontal, vertical, pixel);
      }
    }
    return img;
  }

}
