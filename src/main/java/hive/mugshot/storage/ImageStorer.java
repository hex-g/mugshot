package hive.mugshot.storage;

import hive.mugshot.exception.ImageNotFoundException;
import hive.mugshot.exception.ImageProfileIOException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.net.MalformedURLException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

@Service
public class ImageStorer {
  @Value("${hive.mugshot.image-directory-path}")
  private String rootDir;
  @Value("${hive.mugshot.profile-image-dimension}")
  private int imageSizeInPixels;

  public void storeImageProfile
      (
          final String userDirectoryName,
          final MultipartFile insertedImage,
          final String imageStoredName
      ) {
    createDirectoryIfNotExist(userDirectoryName);
    try {
      final var buff = ImageUtils.resizeImageToSquare(
          ImageIO.read(insertedImage.getInputStream()),
          imageSizeInPixels
      );
      ImageIO.write(
          buff,
          "jpg",
          createFullPathToTheFile(userDirectoryName, imageStoredName).toFile()
      );
    } catch (IOException e) {
      throw new ImageProfileIOException(e);
    }
  }

  private void createDirectoryIfNotExist(final String userDirectoryPath) {
    Path parentDir = Paths.get(rootDir, userDirectoryPath);
    if (!Files.exists(parentDir)) {
      try {
        Files.createDirectories(parentDir);
      } catch (IOException e) {
        throw new ImageProfileIOException(e);
      }
    }
  }

  private Path createFullPathToTheFile(final String userDirectoryName, final String filename) {
    return Paths.get(rootDir).resolve(userDirectoryName).resolve(filename);
  }

  public void storeImageProfile
      (
          final String userDirectoryName,
          final BufferedImage insertedImage,
          final String imageStoredName
      ) {
    createDirectoryIfNotExist(userDirectoryName);
    try {
      final var buff = ImageUtils.resizeImageToSquare(insertedImage, imageSizeInPixels);
      ImageIO.write(
          buff,
          "jpg",
          createFullPathToTheFile(userDirectoryName, imageStoredName).toFile()
      );
    } catch (IOException e) {
      throw new ImageProfileIOException(e);
    }
  }

  public Resource loadImage(final String userDirectoryName, final String imageName) {
    try {
      final var file = createFullPathToTheFile(userDirectoryName, imageName);
      final var resource = new UrlResource(file.toUri());
      if (resource.exists() || resource.isReadable()) {
        return resource;
      } else {
        throw new ImageNotFoundException();
      }
    } catch (MalformedURLException e) {
      throw new RuntimeException();
    }
  }

  public void deleteImage(final String userDirectoryName, final String imageName) {
    final var parentDir = createFullPathToTheFile(userDirectoryName, imageName);
    try {
      Files.deleteIfExists(parentDir);
    } catch (IOException e) {
      throw new ImageProfileIOException(e);
    }
  }
}
