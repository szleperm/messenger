package pl.szleperm.messenger.unit.validator

import org.springframework.validation.BindException
import org.springframework.validation.Errors
import pl.szleperm.messenger.domain.message.form.MessageForm
import pl.szleperm.messenger.domain.message.validator.MessageFormValidator
import pl.szleperm.messenger.domain.user.User
import pl.szleperm.messenger.domain.user.UserService
import spock.lang.Specification
import spock.lang.Unroll

import static pl.szleperm.messenger.testutils.Constants.NOT_VALID_USERNAME
import static pl.szleperm.messenger.testutils.Constants.VALID_USERNAME

class MessageFormValidatorSpec extends Specification {
    @Unroll
    "should add #errorCount error(s) when recipient is #recipient.toUpperCase()"() {
        given:
        UserService userService = Stub(UserService) {
            findByName(VALID_USERNAME) >> Optional.of([] as User)
            findByName(NOT_VALID_USERNAME) >> Optional.empty()
        }
        def validator = new MessageFormValidator(userService)
        def form = [to: recipient] as MessageForm
        Errors errors = new BindException(form, "MessageForm")
        when:
        validator.validate(form, errors)
        then:
        errors.errorCount == errorCount
        where:
        recipient          || errorCount
        VALID_USERNAME     || 0
        NOT_VALID_USERNAME || 1
    }
}