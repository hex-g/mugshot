package hive.mugshot.storage;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.awt.image.BufferedImage;

@RunWith(SpringRunner.class)
@SpringBootTest
public class ImageUtilsTest {

  @Value("${hive.mugshot.profile-image-dimension}")
  private int imageSizeInPixels;

  @Test
  public void validateIfHasAnImageAsExtension_whenParameterDoesNotMatchPattern_expectFalseValue(){
    Assert.assertFalse(ImageUtils.validateIfHasAnImageAsExtension("a.zip"));
  }

  @Test
  public void validateIfHasAnImageAsExtension_whenParameterMatchPattern_expectTrueValue(){
    Assert.assertTrue(ImageUtils.validateIfHasAnImageAsExtension("a.gif"));
  }

  @Test
  public void resizeImageToSquare_whenInitialImageIsNotNull_expectSquareImageInConfiguredSize(){
    var initialImage=new BufferedImage(10,50,BufferedImage.TYPE_INT_RGB);
    var resizedImage=ImageUtils.resizeImageToSquare(initialImage,imageSizeInPixels);
    Assert.assertEquals(imageSizeInPixels,resizedImage.getWidth());
    Assert.assertEquals(imageSizeInPixels,resizedImage.getHeight());
  }

  @Test
  public void whenGenerateRandomImageAndResizeToSquare_expectSquareImageInConfiguredSize(){
    var generatedImage=ImageUtils.generateRandomImage();
    var imageResized=ImageUtils.resizeImageToSquare(generatedImage,imageSizeInPixels);
    Assert.assertEquals(imageSizeInPixels,imageResized.getWidth());
    Assert.assertEquals(imageSizeInPixels,imageResized.getHeight());
  }

}
