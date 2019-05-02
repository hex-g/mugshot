package hive.mugshot.storage;

import hive.mugshot.exception.*;
import hive.mugshot.exception.InvalidPathException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.net.MalformedURLException;
import java.nio.file.*;

@Service
public class ImageStorer {
  @Value("${hive.mugshot.image-directory-path}")
  private String rootDir;
  @Value("${hive.mugshot.profile-image-dimension}")
  private int imageSizeInPixels;

  public void storeImageProfile(String userDirectoryName, MultipartFile insertedImage, String imageStoredName){
    createDirectoryIfNotExist(userDirectoryName);
    try {
      var buff = ImageUtils.resizeImageToSquare(ImageIO.read(insertedImage.getInputStream()),imageSizeInPixels);
      ImageIO.write(buff, "jpg", createFullPathToTheFile(userDirectoryName, imageStoredName).toFile());
    } catch (IOException e) {
      throw new RuntimeException(e);
    }
  }

  public void storeImageProfile(String userDirectoryName, BufferedImage insertedImage, String imageStoredName){
    createDirectoryIfNotExist(userDirectoryName);
    try {
      var buff = ImageUtils.resizeImageToSquare(insertedImage,imageSizeInPixels);
      ImageIO.write(buff, "jpg", createFullPathToTheFile(userDirectoryName, imageStoredName).toFile());
    } catch (IOException e) {
      throw new RuntimeException(e);
    }
  }
  public Resource loadImage(String userDirectoryName,String imageName) {
    try {
      var file = createFullPathToTheFile(userDirectoryName,imageName);
      var resource = new UrlResource(file.toUri());
      if (resource.exists() || resource.isReadable()) {
        return resource;
      }else{
        throw new ImageNotFound();
      }
    } catch (MalformedURLException e) {
      e.printStackTrace();
      throw new InvalidPathException();
    }
  }

  public void deleteImage(String userDirectoryName, String imageName) {
    var parentDir = createFullPathToTheFile(userDirectoryName,imageName);
    try {
      Files.deleteIfExists(parentDir);
    } catch (IOException e) {
      e.printStackTrace();
      throw new RuntimeException("Unable to delete the directory.\n"+e);
    }
  }

  private void createDirectoryIfNotExist(String userDirectoryPath){
    Path parentDir = Paths.get(rootDir,userDirectoryPath);
    if (!Files.exists(parentDir)) {
      try {
        Files.createDirectories(parentDir);
      } catch (IOException e) {
        throw new RuntimeException("Unable to create the directory.\n"+e);
      }
    }
  }

  private Path createFullPathToTheFile(String userDirectoryName, String filename) {
    return Paths.get(rootDir).resolve(userDirectoryName).resolve(filename);
  }

}
