package pl.szleperm.messenger.web.vm;

import org.springframework.validation.FieldError;

import java.util.Arrays;
import java.util.stream.Collectors;

public class FieldErrorMessageVM {
	private String field;
	private String message;
	
	public FieldErrorMessageVM() {
	}
	public FieldErrorMessageVM(FieldError error) {
		this.field = error.getField();
		this.message = Arrays.stream(error.getField().split("(?=[A-Z])"))
								.map(String::toLowerCase)
								.map(s -> Character.toUpperCase(s.charAt(0)) + s.substring(1))
								.collect(Collectors.joining(" "))
								+ " " + error.getDefaultMessage();
	}
	public String getField() {
		return field;
	}
	public void setField(String field) {
		this.field = field;
	}
	public String getMessage() {
		return message;
	}
	public void setMessage(String message) {
		this.message = message;
	}
	
}
