package hive.mugshot.exception;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(code = HttpStatus.UNSUPPORTED_MEDIA_TYPE, reason = "Media type unsupported")
public class NotAcceptedFileFormatException extends RuntimeException{
}
