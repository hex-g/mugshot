package hive.mugshot.storage;
import hive.mugshot.exception.ImageNotFoundException;
import org.apache.commons.lang.RandomStringUtils;
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
import java.nio.file.InvalidPathException;
import java.nio.file.Path;
import java.nio.file.Paths;

import static org.junit.Assert.*;

@RunWith(SpringRunner.class)
@SpringBootTest
public class ImageStorerTest {

  private final String userId= RandomStringUtils.randomAlphabetic(8);
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
    imageStorer.storeImageProfile(userId,multipartFile,imageName);
    Path path=Paths.get(rootDir,userId,imageName);
    assertTrue(Files.exists(path));
  }

  @Test
  public void uploadImage_WhenBufferedImageIsProvided_expectFileExists(){
    imageStorer.storeImageProfile(userId,initialImage,imageName);
    Path path=Paths.get(rootDir,userId,imageName);
    assertTrue(Files.exists(path));
  }

  @Test
  public void deleteImage_WhenImageNameAndUserDirectoryIsNotNull_expectFileDoesNotExists(){
    imageStorer.deleteImage(userId,imageName);
    Path path=Paths.get(rootDir,userId,imageName);
    assertFalse(Files.exists(path));
  }

  @Test
  public void loadImage_WhenMultipartIsNotNull_expectConfiguredImageName() throws IOException {
    imageStorer.storeImageProfile(userId,multipartFile,imageName);
    var resource=imageStorer.loadImage(userId,imageName);
    assertEquals(imageName,resource.getFile().getName());
  }

  @Test
  public void loadImage_WhenMultipartIsNotNull_expectConfiguredImageSize() throws IOException {
    imageStorer.storeImageProfile(userId,multipartFile,imageName);
    var resource=imageStorer.loadImage(userId,imageName);
    var image=ImageIO.read(resource.getFile());
    assertEquals(imageSizeInPixels,image.getHeight());
    assertEquals(imageSizeInPixels,image.getWidth());
  }
  @Test(expected = ImageNotFoundException.class)
  public void loadImage_WhenFileNotExists_expectImageNotFoundException() {
    imageStorer.loadImage(userId,imageName);
  }
  @Test(expected = InvalidPathException.class)
  public void loadImage_WhenUserDirectoryNameContainsInvalidCharacter_expectInvalidPathException() {
    var invalidCharacters="></\\^\"*?".toCharArray();
    var oneInvalidCharacter= RandomStringUtils.random(1,invalidCharacters);
    imageStorer.loadImage(userId+oneInvalidCharacter,imageName);
  }
  @After
  public void deleteCreatedDirectoryAndFile() throws IOException {
    Path createdDirectoryPath=Paths.get(rootDir,userId);
    Path createdImagePath=createdDirectoryPath.resolve(imageName);
    Files.deleteIfExists(createdImagePath);
    Files.deleteIfExists(createdDirectoryPath);
  }

}


