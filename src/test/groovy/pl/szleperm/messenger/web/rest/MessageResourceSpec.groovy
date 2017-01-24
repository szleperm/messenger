package pl.szleperm.messenger.web.rest

import org.springframework.http.HttpStatus
import pl.szleperm.messenger.domain.Message
import pl.szleperm.messenger.domain.User
import pl.szleperm.messenger.domain.projection.MessageSimplifiedProjection
import pl.szleperm.messenger.service.MessageService
import pl.szleperm.messenger.web.vm.MessageFormVM
import spock.lang.Specification

import java.time.LocalDateTime

import static pl.szleperm.messenger.testutils.Constants.*

class MessageResourceSpec extends Specification{
    MessageService messageService
    MessageResource resource

    def setup(){
        messageService = Mock(MessageService)
        resource = new MessageResource(messageService)
    }
    def "should return all messages"(){
        given:
            def messageSimplified = Stub(MessageSimplifiedProjection){
                getTitle() >> TITLE
                getAuthor() >> VALID_USERNAME
                getCreatedDate() >> LocalDateTime.now()
            }
            messageService.getAllSimplified() >> [messageSimplified, messageSimplified]
        when:
            def response = resource.all
        then:
            response.statusCode == HttpStatus.OK
            response.body.size() == 2
    }
    def "should return message"(){
        given:
            def message = [title: TITLE, content: CONTENT, user: new User()] as Message
            messageService.findById(VALID_ID) >> Optional.of(message)
        when:
            def response = resource.getMessage(VALID_ID)
        then:
            response.statusCode == HttpStatus.OK
            response.hasBody()
    }
    def "should not return message"(){
        given:
            messageService.findById(NOT_VALID_ID) >> Optional.ofNullable(null)
        when:
            def response = resource.getMessage(NOT_VALID_ID)
        then:
            response.statusCode == HttpStatus.NOT_FOUND
            !response.hasBody()
    }
    def "should create message"(){
        given:
            def message = [title: TITLE, content: CONTENT] as MessageFormVM
        when:
            def response = resource.createMessage(message)
        then:
            response.statusCode == HttpStatus.OK
    }
    def "should update message"(){
        given:
            def messageDTO = [title: TITLE, content: CONTENT] as MessageFormVM
            def message = [id: VALID_ID, author: VALID_USERNAME] as Message
            messageService.findById(VALID_ID) >> Optional.of(message)
        when:
            def response = resource.updateMessage(VALID_ID, messageDTO)
        then:
            response.getStatusCode() == HttpStatus.OK
            response.getBody().author == VALID_USERNAME
            response.getBody().id == VALID_ID
            response.getBody().content == CONTENT
            response.getBody().title == TITLE
            1 * messageService.update({it.id == VALID_ID} as Message)
    }
    def "should not update message"(){
        given:
            def messageForm = [] as MessageFormVM
            messageService.findById(NOT_VALID_ID) >> Optional.ofNullable(null)
        when:
            def response = resource.updateMessage(NOT_VALID_ID, messageForm)
        then:
            response.getStatusCode() == HttpStatus.NOT_FOUND
            !response.hasBody()
            0 * messageService.update(_ as Message)
    }
}