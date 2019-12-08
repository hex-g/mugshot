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
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
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
public class MugshotBase64ControllerTest {
  private String userId;
  private Path validUserDirectory;
  private MockMvc mockMvc;
  private final String ROOT_URL_ENDPOINT = "/base64";

  @Value("${hive.mugshot.image-directory-path}")
  private String rootDir;
  @Value("${hive.mugshot.profile-image-name}")
  private String imageName;
  @Mock
  private ImageStorer imageStorer;

  private void createDirectoryForTest(final Path directory) throws Exception {
    Files.createDirectories(directory);
  }

  private Resource createImageForTest(final Path directory) throws Exception {
    final var file = directory.resolve(imageName).toFile();
    ImageIO.write(new BufferedImage(512, 512, BufferedImage.TYPE_INT_RGB), "jpg", file);
    return new UrlResource(file.toURI());
  }

  @Before
  public void setup() {
    userId= RandomStringUtils.randomAlphabetic(8);
    MockitoAnnotations.initMocks(this);
    final var mugshotBase64Controller = new MugshotBase64Controller(imageStorer);
    ReflectionTestUtils.setField(mugshotBase64Controller, "imageName", imageName);
    mockMvc = MockMvcBuilders.standaloneSetup(mugshotBase64Controller).build();
    validUserDirectory = Paths.get(rootDir, userId);
  }

  @Test
  public void givenValidImage_WhenImageRetrieved_then200andStringTypeIsReturned() throws Exception {
    createDirectoryForTest(validUserDirectory);
    final var resourceImage = createImageForTest(validUserDirectory);
    given(imageStorer.loadImage(userId, imageName)).willReturn(resourceImage);
    mockMvc.perform(
              get(ROOT_URL_ENDPOINT)
              .header(AUTHENTICATED_USER_ID, userId))
              .andExpect(status().isOk())
              .andExpect(content().contentType("text/plain;charset=ISO-8859-1"))
              .andDo(print());
  }

  @Test
  public void givenFileNotFound_WhenImageRetrieved_then404isReturned() throws Exception {
    given(imageStorer.loadImage(userId, imageName)).willThrow(new ImageNotFoundException());
    createDirectoryForTest(validUserDirectory);
    mockMvc.perform(
            get(ROOT_URL_ENDPOINT)
            .header(AUTHENTICATED_USER_ID, userId))
            .andExpect(status().isNotFound())
            .andDo(print());
  }

  @After
  public void deleteCreatedDirectory(){
    try {
      Path createdDirectoryPath = Paths.get(rootDir, userId);
      Path createdImagePath = createdDirectoryPath.resolve(imageName);
      Files.deleteIfExists(createdImagePath);
      Files.deleteIfExists(createdDirectoryPath);
    }catch (IOException ex){
      System.err.println("DELETE TEST FOLDER FAILED,PLEASE DELETE IT MANUALLY");
      System.err.println("AT:"+ex);
    }
  }
}
