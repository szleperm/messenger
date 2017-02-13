package pl.szleperm.messenger.unit.web

import org.springframework.validation.BindingResult
import org.springframework.validation.FieldError
import org.springframework.web.bind.MethodArgumentNotValidException
import pl.szleperm.messenger.infrastructure.exception.ResourceNotFoundException
import pl.szleperm.messenger.web.rest.GlobalExceptionHandler
import spock.lang.Specification

class GlobalExceptionHandlerSpec extends Specification {
    GlobalExceptionHandler handler = new GlobalExceptionHandler()

    def "should handle validation exception"(){
        given:
        def fieldError = Stub(FieldError){
            getField() >>> ["name","name","email","email"]
            getDefaultMessage() >> "equals not valid"
        }
        def fieldErrors = [fieldError, fieldError]
        def bindingResult = Stub(BindingResult){
            getFieldErrors() >> fieldErrors
        }
        def exception = Stub(MethodArgumentNotValidException){
            getBindingResult() >> bindingResult
        }
        when:
        def models = handler.handleValidationException(exception)
        then:
        models.size() == 2
        models[0].field == "name"
        models[1].field == "email"
        models[0].message == "Name equals not valid"
        models[1].message == "Email equals not valid"
    }
    def "should handle not found exception"(){
        given:
        def exception = Stub(ResourceNotFoundException){
            getMessage() >> "message"
        }
        when:
        def map = handler.handleNotFoundException(exception)
        then:
        map.containsKey("404: NOT FOUND")
        map.containsValue("message")
    }
}