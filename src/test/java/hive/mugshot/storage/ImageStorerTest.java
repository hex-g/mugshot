package hive.mugshot.storage;
import org.junit.*;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.context.junit4.SpringRunner;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.concurrent.ThreadLocalRandom;

import static org.junit.Assert.*;

@RunWith(SpringRunner.class)
@SpringBootTest
public class ImageStorerTest {

  private Integer userId;
  private MockMultipartFile multipartFile;
  private BufferedImage initialImage;

  @Autowired
  private ImageStorer imageStorer;
  @Value("${hive.mugshot.image-directory-path}")
  private String rootDir;
  @Value("${hive.mugshot.profile-image-name}")
  private String imageName;
  @Value("${hive.mugshot.profile-image-dimension}")
  private int imageSizeInPixels;


  @Before
  public void setUp() throws IOException {
    userId= ThreadLocalRandom.current().nextInt(Integer.MAX_VALUE);
    initialImage=new BufferedImage(512,512,BufferedImage.TYPE_INT_RGB);
    var byteArrayOutputStream = new ByteArrayOutputStream();
    ImageIO.write(initialImage,"jpg",byteArrayOutputStream);
    multipartFile=new MockMultipartFile(
        "image",
        "WhateverName.gif",
        MediaType.IMAGE_JPEG_VALUE,
        byteArrayOutputStream.toByteArray()
    );
  }

  @Test
  public void uploadImage_WhenMultipartFileIsProvided_expectFileExists(){
    imageStorer.storeImageProfile(userId.toString(),multipartFile,imageName);
    Path path=Paths.get(rootDir,userId.toString(),imageName);
    assertTrue(Files.exists(path));
  }

  @Test
  public void uploadImage_WhenBufferedImageIsProvided_expectFileExists(){
    imageStorer.storeImageProfile(userId.toString(),initialImage,imageName);
    Path path=Paths.get(rootDir,userId.toString(),imageName);
    assertTrue(Files.exists(path));
  }

  @Test
  public void deleteImage_WhenImageNameAndUserDirectoryIsNotNull_expectFileDoesNotExists(){
    imageStorer.deleteImage(userId.toString(),imageName);
    Path path=Paths.get(rootDir,userId.toString(),imageName);
    assertFalse(Files.exists(path));
  }

  @Test
  public void loadImage_WhenMultipartIsNotNull_expectConfiguredImageName() throws IOException {
    imageStorer.storeImageProfile(userId.toString(),multipartFile,imageName);
    var resource=imageStorer.loadImage(userId.toString(),imageName);
    assertEquals(imageName,resource.getFile().getName());
  }

  @Test
  public void loadImage_WhenMultipartIsNotNull_expectConfiguredImageSize() throws IOException {
    imageStorer.storeImageProfile(userId.toString(),multipartFile,imageName);
    var resource=imageStorer.loadImage(userId.toString(),imageName);
    var image=ImageIO.read(resource.getFile());
    assertEquals(imageSizeInPixels,image.getHeight());
    assertEquals(imageSizeInPixels,image.getWidth());
  }

  @After
  public void deleteCreatedDirectoryAndFile() throws IOException {
    Path createdDirectoryPath=Paths.get(rootDir,userId.toString());
    Path createdImagePath=createdDirectoryPath.resolve(imageName);
    Files.deleteIfExists(createdImagePath);
    Files.deleteIfExists(createdDirectoryPath);
  }

}


