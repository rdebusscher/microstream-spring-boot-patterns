package be.rubus.microstream.spring.example.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(value = HttpStatus.PRECONDITION_FAILED)
public class UserAlreadyExistsException extends BusinessException {
}
