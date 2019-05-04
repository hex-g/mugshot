package hive.mugshot.exception;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(code = HttpStatus.INTERNAL_SERVER_ERROR, reason = "Failed to store the image due to some I/O problem or permission")
public class ImageProfileIOException extends RuntimeException{
  public ImageProfileIOException(Throwable cause){
    super(cause);
  }
}
