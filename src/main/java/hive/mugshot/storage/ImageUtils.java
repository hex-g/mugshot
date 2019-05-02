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
    var pattern = Pattern.compile(IMAGE_PATTERN);
    var matcher = pattern.matcher(image);
    return matcher.matches();
  }

  public static BufferedImage resizeImageToSquare(BufferedImage inputtedImage,int imageSizeInPixels) {
    // multi-pass bilinear div 2
    var bufferedImageWithNewSize = new BufferedImage(imageSizeInPixels, imageSizeInPixels, BufferedImage.TYPE_INT_RGB);
    var reSizer = bufferedImageWithNewSize.createGraphics();
    var resizingMode=
        (inputtedImage.getHeight()<imageSizeInPixels)
            ? RenderingHints.VALUE_INTERPOLATION_NEAREST_NEIGHBOR
            : RenderingHints.VALUE_INTERPOLATION_BILINEAR;
    reSizer.setRenderingHint(RenderingHints.KEY_INTERPOLATION, resizingMode);
    reSizer.drawImage(inputtedImage, 0, 0, imageSizeInPixels, imageSizeInPixels, null);
    reSizer.dispose();
    return bufferedImageWithNewSize;
  }

  public static BufferedImage generateRandomImage(){
    var yellow=0xF6BD60;
    var orange=0xE9724C;
    var gray = 0xE8E9EB;
    var blue = 0x5C9EAD;
    var black = 0x313638;
    var colorsCombinations=new int[][]{
        {yellow,black},
        {orange,black},
        {blue,black},
        {blue,gray},
        {orange,yellow}
    };
    var combinationIndex=new Random().nextInt(colorsCombinations.length);
    var img=new BufferedImage(9,9,BufferedImage.TYPE_INT_RGB);
    var maxH=img.getHeight();
    var maxV=img.getWidth();
    for(int vertical=0;vertical<maxV;vertical++){
      for(int horizontal=0;horizontal<maxH;horizontal++){
        var pixel=0;
        if(Math.random()<0.7){
          pixel=colorsCombinations[combinationIndex][0];
        }else{
          pixel=colorsCombinations[combinationIndex][1];
        }
        img.setRGB(horizontal,vertical,pixel);
      }
    }
    return img;
  }

}
