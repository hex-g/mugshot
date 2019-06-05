package hive.mugshot.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(code = HttpStatus.BAD_REQUEST, reason = "The user does not exist")
public class UserNotFoundException extends RuntimeException {
}
