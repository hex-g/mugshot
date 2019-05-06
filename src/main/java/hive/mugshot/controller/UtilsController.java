package hive.mugshot.controller;

import hive.mugshot.storage.ImageStorer;
import hive.mugshot.storage.ImageUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import static hive.pandora.constant.HiveInternalHeaders.AUTHENTICATED_USER_ID;

@RestController
@RequestMapping("/utils")
public class UtilsController {
  private final ImageStorer imageStorer;
  @Value("${hive.mugshot.profile-image-name}")
  private String imageName;

  @Autowired
  public UtilsController(final ImageStorer imageStorer) {
    this.imageStorer = imageStorer;
  }

  @ResponseStatus(code = HttpStatus.OK, reason = "Random image generated and successfully stored")
  @PostMapping("/generateRandomImage")
  public void generateRandomImage(
      @RequestHeader(name = AUTHENTICATED_USER_ID) final String userId
  ) {
    final var generatedImage = ImageUtils.generateRandomImage();
    imageStorer.storeImageProfile(userId, generatedImage, imageName);
  }
}
