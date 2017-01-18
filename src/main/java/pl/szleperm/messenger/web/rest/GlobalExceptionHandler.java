package pl.szleperm.messenger.web.rest;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;

import pl.szleperm.messenger.web.DTO.FieldErrorDTO;

@ControllerAdvice
public class GlobalExceptionHandler {
	@ExceptionHandler(MethodArgumentNotValidException.class)
	@ResponseStatus(HttpStatus.BAD_REQUEST)
	@ResponseBody
	public List<FieldErrorDTO> handleValidationException(MethodArgumentNotValidException exception) {
		List<FieldErrorDTO> errors = exception
				.getBindingResult()
				.getFieldErrors()
				.stream()
				.map(e -> new FieldErrorDTO(e))
				.collect(Collectors.toList());
	     return errors;
	}
}
