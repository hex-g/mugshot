package hive.mugshot.controller;

import hive.mugshot.storage.ImageStorer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;
import java.util.Base64;

import static hive.pandora.constant.HiveInternalHeaders.AUTHENTICATED_USER_ID;

@RestController
@RequestMapping("/base64/banner")
public class BannerBase64Controller {
  private final ImageStorer imageStorer;
  @Value("${hive.mugshot.banner-image-name}")
  private String imageName;

  @Autowired
  public BannerBase64Controller(final ImageStorer imageStorer) {
    this.imageStorer = imageStorer;
  }

  @GetMapping
  public ResponseEntity<String> searchBannerImageByUserId(
      @RequestHeader(name = AUTHENTICATED_USER_ID) final String userId
  ) throws IOException {
      var resource=imageStorer.loadImage(userId, imageName);
      var encodedImageInBase64 = Base64.getEncoder()
                                .encodeToString(resource.getInputStream().readAllBytes());
      return ResponseEntity.ok().header(
              HttpHeaders.CONTENT_DISPOSITION,
              "attachment; filename=\"" + resource.getFilename() + "\""
          ).body(encodedImageInBase64);
  }
}
