package hive.mugshot.controller;

import hive.mugshot.exception.UnsupportedFileFormatException;
import hive.mugshot.storage.ImageStorer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.imageio.ImageIO;
import javax.xml.bind.DatatypeConverter;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.util.Base64;

import static hive.mugshot.storage.ImageUtils.validateIfHasAnImageAsExtension;
import static hive.pandora.constant.HiveInternalHeaders.AUTHENTICATED_USER_ID;

@RestController
@RequestMapping("/base64")
public class MugshotBase64Controller {
  private final ImageStorer imageStorer;
  @Value("${hive.mugshot.profile-image-name}")
  private String imageName;

  @Autowired
  public MugshotBase64Controller(final ImageStorer imageStorer) {
    this.imageStorer = imageStorer;
  }

  @GetMapping
  public ResponseEntity<String> searchProfileImageByUserId(
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
