package pl.szleperm.messenger.web.rest;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import pl.szleperm.messenger.web.vm.FieldErrorMessageVM;

import java.util.List;
import java.util.stream.Collectors;

@ControllerAdvice
public class GlobalExceptionHandler {
	@ExceptionHandler(MethodArgumentNotValidException.class)
	@ResponseStatus(HttpStatus.BAD_REQUEST)
	@ResponseBody
	public List<FieldErrorMessageVM> handleValidationException(MethodArgumentNotValidException exception) {
        return exception
                .getBindingResult()
                .getFieldErrors()
                .stream()
                .map(FieldErrorMessageVM::new)
                .collect(Collectors.toList());
	}
}
