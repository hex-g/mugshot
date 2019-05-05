package hive.mugshot.controller;

import hive.mugshot.storage.ImageStorer;
import org.apache.commons.lang.RandomStringUtils;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.util.concurrent.ThreadLocalRandom;

import static hive.pandora.constant.HiveInternalHeaders.AUTHENTICATED_USER_ID;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@RunWith(SpringRunner.class)
@SpringBootTest
public class UtilsControllerTest {
  private String username = RandomStringUtils.randomAlphabetic(8);
  private Integer userId = ThreadLocalRandom.current().nextInt(Integer.MAX_VALUE);
  private MockMvc mockMvc;
  @Value("${hive.mugshot.profile-image-name}")
  private String imageName;
  @Mock
  private ImageStorer imageStorer;

  @Before
  public void setup() {
    MockitoAnnotations.initMocks(this);
    final var utilsController = new UtilsController(imageStorer);
    ReflectionTestUtils.setField(utilsController, "imageName", imageName);
    mockMvc = MockMvcBuilders.standaloneSetup(utilsController).build();
  }

  @Test
  public void givenUserName_whenRequestGeneratedImage_then200isReturned() throws Exception {
    mockMvc
        .perform(
            post("/utils/generateRandomImage")
                .header(AUTHENTICATED_USER_ID, username)
        )
        .andExpect(status().isOk())
        .andDo(print());
  }
}
