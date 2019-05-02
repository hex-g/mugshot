package hive.mugshot.exception;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(code = HttpStatus.BAD_REQUEST, reason = "Failed to store the image due to some I/O problem or permission")
public class ImageProfileException extends RuntimeException{
}
