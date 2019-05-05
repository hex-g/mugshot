package hive.mugshot.exception;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(code = HttpStatus.PAYLOAD_TOO_LARGE,reason = "Invalid size of file")
public class FileSizeException extends RuntimeException{
}
