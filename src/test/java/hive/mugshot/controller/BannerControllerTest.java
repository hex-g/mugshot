package hive.mugshot.controller;

import hive.mugshot.exception.ImageNotFoundException;
import hive.mugshot.storage.ImageStorer;
import org.apache.commons.lang.RandomStringUtils;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import static hive.pandora.constant.HiveInternalHeaders.AUTHENTICATED_USER_ID;
import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@RunWith(SpringRunner.class)
@SpringBootTest
public class BannerControllerTest {
  private final String userId = RandomStringUtils.randomAlphabetic(8);
  private final String ENDPOINT = "/banner";

  @Value("${hive.mugshot.image-directory-path}")
  private String rootDir;
  @Value("${hive.mugshot.banner-image-name}")
  private String imageName;
  private Path validUserDirectory;
  private MockMvc mockMvc;
  private MockMultipartFile multipartFile;

  @Mock
  private ImageStorer imageStorer;

  @Before
  public void setup() {
    MockitoAnnotations.initMocks(this);
    final var bannerController = new BannerController(imageStorer);
    ReflectionTestUtils.setField(bannerController, "imageName", imageName);
    mockMvc = MockMvcBuilders.standaloneSetup(bannerController).build();
    validUserDirectory = Paths.get(rootDir, userId);
  }

  @Test
  public void givenValidImage_WhenImageRetrieved_then200andJpegImageTypeIsReturned() throws Exception {
    createDirectoryForTest(validUserDirectory);
    final var resourceImage = createImageForTest(validUserDirectory);
    given(imageStorer.loadImage(userId, imageName))
        .willReturn(resourceImage);
    mockMvc.perform(
        get(ENDPOINT)
            .header(AUTHENTICATED_USER_ID, userId))
        .andExpect(status().isOk())
        .andExpect(content().contentType(MediaType.IMAGE_JPEG))
        .andDo(print());
  }

  private void createDirectoryForTest(final Path directory) throws Exception {
    Files.createDirectories(directory);
  }

  private Resource createImageForTest(final Path directory) throws Exception {
    final var file = directory.resolve(imageName).toFile();
    ImageIO.write(new BufferedImage(512, 512, BufferedImage.TYPE_INT_RGB), "jpg", file);
    return new UrlResource(file.toURI());
  }

  @Test
  public void givenValidImage_WhenImageUploaded_then200isReturned() throws Exception {
    multipartFile = new MockMultipartFile(
        "image",
        "Profile Image.jpg",
        MediaType.IMAGE_JPEG_VALUE,
        "Spring Framework".getBytes()
    );
    mockMvc.perform(
        multipart(ENDPOINT).file(multipartFile).header(AUTHENTICATED_USER_ID, userId))
        .andExpect(status().isOk())
        .andDo(print());
  }

  @Test
  public void givenValidImage_WhenImageDeleted_then200isReturned() throws Exception {
    mockMvc.perform(
        delete(ENDPOINT).header(AUTHENTICATED_USER_ID, userId)
    ).andExpect(status().isOk());
  }

  @Test
  public void givenFileNotFound_WhenImageRetrieved_then404isReturned() throws Exception {
    given(imageStorer.loadImage(userId, imageName)).willThrow(new ImageNotFoundException());
    createDirectoryForTest(validUserDirectory);
    mockMvc.perform(
        get(ENDPOINT)
            .header(AUTHENTICATED_USER_ID, userId))
        .andExpect(status().isNotFound())
        .andDo(print());
  }

  @Test
  public void givenUnsupportedMediaType_WhenImageUploaded_then415isReturned() throws Exception {
    final var originalImage = new BufferedImage(512, 512, BufferedImage.TYPE_INT_RGB);
    final var byteArrayOutputStream = new ByteArrayOutputStream();
    ImageIO.write(originalImage, "jpg", byteArrayOutputStream);
    multipartFile = new MockMultipartFile(
        "image",
        "Unsupported.Extension.wmv",
        MediaType.APPLICATION_PDF_VALUE,
        byteArrayOutputStream.toByteArray()
    );
    mockMvc.perform(multipart(ENDPOINT)
        .file(multipartFile)
        .header(AUTHENTICATED_USER_ID, userId))
        .andExpect(status().isUnsupportedMediaType())
        .andDo(print());
  }

  @Test
  public void givenWrongRequestBodyKey_WhenImageUploaded_then400isReturned() throws Exception {
    multipartFile = new MockMultipartFile(
        "wrongBodyKey",
        "originalFileName.jpeg",
        MediaType.IMAGE_JPEG_VALUE,
        new byte[0]
    );
    mockMvc.perform(multipart(ENDPOINT)
        .file(multipartFile).header(AUTHENTICATED_USER_ID, userId))
        .andExpect(status().isBadRequest())
        .andDo(print());
  }

  @After
  public void deleteCreatedDirectory() throws IOException {
    Path createdDirectoryPath = Paths.get(rootDir, userId);
    Path createdImagePath = createdDirectoryPath.resolve(imageName);
    Files.deleteIfExists(createdImagePath);
    Files.deleteIfExists(createdDirectoryPath);
  }
}
