package pl.szleperm.messenger.domain.message.validator;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;
import pl.szleperm.messenger.domain.message.form.MessageForm;
import pl.szleperm.messenger.domain.user.UserService;

@Component
public class MessageFormValidator implements Validator {

    private final UserService userService;

    @Autowired
    public MessageFormValidator(UserService userService) {
        this.userService = userService;
    }

    @Override
    public boolean supports(Class<?> clazz) {
        return clazz.equals(MessageForm.class);
    }

    @Override
    public void validate(Object target, Errors errors) {
        MessageForm messageForm = (MessageForm) target;
        validateRecipient(errors, messageForm);
    }

    private void validateRecipient(Errors errors, MessageForm messageForm) {
        if(!(userService.findByName(messageForm.getTo()).isPresent()))
            errors.rejectValue(
                    "to",
                    "recipient.notValid",
                    String.format("user %s doesn't exist", messageForm.getTo())
            );
    }
}
