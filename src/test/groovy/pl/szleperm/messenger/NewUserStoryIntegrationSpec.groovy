package pl.szleperm.messenger

import groovy.json.JsonSlurper
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.http.HttpStatus
import org.springframework.http.MediaType
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.setup.MockMvcBuilders
import org.springframework.transaction.annotation.Propagation
import org.springframework.transaction.annotation.Transactional
import org.springframework.web.context.WebApplicationContext
import spock.lang.Specification

import static groovy.json.JsonOutput.toJson
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.*
import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post

@SpringBootTest
@AutoConfigureMockMvc
@Transactional(propagation=Propagation.REQUIRES_NEW )
class NewUserStoryIntegrationSpec extends Specification{
	@Autowired
	MockMvc mvc
	@Autowired
	WebApplicationContext context
	JsonSlurper json = new JsonSlurper()
	def setup(){
		mvc = MockMvcBuilders
				.webAppContextSetup(context)
				.apply(springSecurity())
				.build()
	}
    def "Testing user story"() {
        given:
        def registerData = toJson(username: "johndoe",
                email: "johndoe@example",
                password: "12345",
                confirmPassword: "12345")
        def messageCreateData = toJson(title: "My title", content: "Hello, john doe here!")
        def messageUpdateData
        when: "New user sign up"
        def response = mvc
                .perform(post("/api/register")
                .content(registerData)
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .with(csrf().asHeader()))
                .andReturn()
                .getResponse()
        then: "Success response"
        response.status == HttpStatus.OK.value()
        when: "create new message"
        response = mvc.perform(post("/api/messages")
                .content(messageCreateData)
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .with(csrf().asHeader())
                .with(user("johndoe").password("12345")))
                .andReturn()
                .getResponse()
        then: "success response"
        response.status == HttpStatus.OK.value()
        when:"get account info"
        response = mvc
                .perform(get("/api/account")
                .with(user("johndoe").password("12345"))
                .with(csrf().asHeader()))
                .andReturn()
                .getResponse()
        def body = json.parseText(response.contentAsString) as Map
        def userId = body["id"]
        def messageId = ((body["messages"] as List).get(0) as Map).keySet().asList().get(0)
        then: "success response and check data"
        response.status == HttpStatus.OK.value()
        body["name"] == "johndoe"
        (body["messages"] as List).size() == 1
        when:
        response = mvc
                .perform(get("/api/messages/".concat(messageId as String))
                .with(user("johndoe").password("12345"))
                .with(csrf().asHeader()))
                .andReturn()
                .getResponse()
        body = json.parseText(response.contentAsString) as Map
        then:
        response.status == HttpStatus.OK.value()
        !body.isEmpty()
    }
}

