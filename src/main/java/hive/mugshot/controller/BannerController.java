package hive.mugshot.controller;

import hive.mugshot.exception.UnsupportedFileFormatException;
import hive.mugshot.storage.ImageStorer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import static hive.mugshot.storage.ImageUtils.validateIfHasAnImageAsExtension;
import static hive.pandora.constant.HiveInternalHeaders.AUTHENTICATED_USER_ID;

@RestController
@RequestMapping("/banner")
public class BannerController {
  private final ImageStorer imageStorer;
  @Value("${hive.mugshot.banner-image-name}")
  private String imageName;

  @Autowired
  public BannerController(final ImageStorer imageStorer) {
    this.imageStorer = imageStorer;
  }

  @ResponseStatus(code = HttpStatus.OK, reason = "Profile image successfully stored")
  @PostMapping
  public void sendImageBanner(
      @RequestParam("image") final MultipartFile insertedImage,
      @RequestHeader(name = AUTHENTICATED_USER_ID) final String userId
  ) {
    if (!validateIfHasAnImageAsExtension(insertedImage.getOriginalFilename())) {
      throw new UnsupportedFileFormatException();
    }
    imageStorer.storeBannerImage(userId, insertedImage, imageName);
  }

  @GetMapping(produces = MediaType.IMAGE_JPEG_VALUE)
  public ResponseEntity<Resource> searchBannerImage(
      @RequestHeader(name = AUTHENTICATED_USER_ID) final String userId
  ) {
    final var file = imageStorer.loadImage(userId, imageName);
    return ResponseEntity.ok()
        .header(
            HttpHeaders.CONTENT_DISPOSITION,
            "attachment; filename=\"" + file.getFilename() + "\""
        )
        .body(file);
  }

  @ResponseStatus(code = HttpStatus.OK, reason = "Profile image successfully deleted")
  @DeleteMapping
  public void deleteBannerImage(
      @RequestHeader(name = AUTHENTICATED_USER_ID) final String userId
  ) {
    imageStorer.deleteImage(userId, imageName);
  }
}
