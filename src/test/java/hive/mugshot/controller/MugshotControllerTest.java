package hive.mugshot.controller;

import hive.mugshot.exception.ImageNotFoundException;
import hive.mugshot.storage.ImageStorer;

import org.junit.*;
import org.apache.commons.lang.RandomStringUtils;
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

import java.io.*;
import java.nio.file.*;
import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;

import static hive.pandora.constant.HiveInternalHeaders.AUTHENTICATED_USER_ID;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;

@RunWith(SpringRunner.class)
@SpringBootTest
public class MugshotControllerTest {

  private final String userId = RandomStringUtils.randomAlphabetic(8);
  private String validDirectoryName;

  private MockMvc mockMvc;
  private MockMultipartFile multipartFile;

  @Value("${hive.mugshot.image-directory-path}")
  private String rootDir;
  @Value("${hive.mugshot.profile-image-name}")
  private String imageName;

  @Mock
  private ImageStorer imageStorer;

  private Resource createImageForTest(final String directoryName) throws Exception{
    final var file=new File(directoryName + "/ProfileImage.jpg");
    ImageIO.write(new BufferedImage(512,512,BufferedImage.TYPE_INT_RGB),"jpg",file);
    return new UrlResource(file.toURI());
  }

  private void createDirectoryForTest(final String directoryName) throws Exception{
    Files.createDirectories(Paths.get(directoryName));
  }

  @Before
  public void setup() {
    MockitoAnnotations.initMocks(this);
    final var mugshotController = new MugshotController(imageStorer);
    ReflectionTestUtils.setField(mugshotController, "imageName", imageName);
    mockMvc = MockMvcBuilders.standaloneSetup(mugshotController).build();
    validDirectoryName = (rootDir + "/" + userId + "/");
  }

  @Test
  public void givenValidImage_WhenImageRetrieved_then200andJpegImageTypeIsReturned() throws Exception{
    createDirectoryForTest(validDirectoryName);
    final var resourceImage=createImageForTest(validDirectoryName);
    given(imageStorer.loadImage(userId,imageName))
        .willReturn(resourceImage);
      mockMvc.perform(
          get("/")
          .header(AUTHENTICATED_USER_ID, userId))
          .andExpect(status().isOk())
          .andExpect(content().contentType(MediaType.IMAGE_JPEG))
          .andDo(print());
  }

  @Test
  public void givenValidImage_WhenImageUploaded_then200isReturned() throws Exception{
    multipartFile = new MockMultipartFile("image", "Profile Image.jpg", MediaType.IMAGE_JPEG_VALUE, "Spring Framework".getBytes());
    mockMvc.perform(
        multipart("/").file(multipartFile).header(AUTHENTICATED_USER_ID, userId))
        .andExpect(status().isOk())
        .andDo(print());
  }

  @Test
  public void givenValidImage_WhenImageDeleted_then204isReturned() throws Exception{
    mockMvc.perform(
        delete("/").header(AUTHENTICATED_USER_ID, userId)
    ).andExpect(status().isNoContent());
  }

  @Test
  public void givenFileNotFound_WhenImageRetrieved_then404isReturned() throws Exception{
    given(imageStorer.loadImage(userId,imageName)).willThrow(new ImageNotFoundException());
    createDirectoryForTest(validDirectoryName);
    mockMvc.perform(
        get("/")
        .header(AUTHENTICATED_USER_ID, userId))
        .andExpect(status().isNotFound())
        .andDo(print());
  }

  @Test
  public void givenUnsupportedMediaType_WhenImageUploaded_then415isReturned()throws Exception{
      final var originalImage = new BufferedImage(512, 512, BufferedImage.TYPE_INT_RGB);
      final var byteArrayOutputStream = new ByteArrayOutputStream();
      ImageIO.write(originalImage, "jpg", byteArrayOutputStream);
      multipartFile = new MockMultipartFile("image", "Unsupported.Extension.wmv", MediaType.APPLICATION_PDF_VALUE, byteArrayOutputStream.toByteArray());
      mockMvc.perform(multipart("/")
          .file(multipartFile)
          .header(AUTHENTICATED_USER_ID, userId))
          .andExpect(status().isUnsupportedMediaType())
          .andDo(print());
  }

  @Test
  public void givenWrongRequestBodyKey_WhenImageUploaded_then400isReturned() throws Exception{
      multipartFile = new MockMultipartFile("wrongBodyKey", "ProfileImage.jpeg", MediaType.IMAGE_JPEG_VALUE, new byte[0]);
      mockMvc.perform(multipart("/")
          .file(multipartFile).header(AUTHENTICATED_USER_ID, userId))
          .andExpect(status().isBadRequest())
          .andDo(print());
  }

  @After
  public void deleteCreatedDirectory() throws IOException {
    Path createdDirectoryPath=Paths.get(rootDir,userId);
    Path createdImagePath=createdDirectoryPath.resolve(imageName);
    Files.deleteIfExists(createdImagePath);
    Files.deleteIfExists(createdDirectoryPath);
  }

}
