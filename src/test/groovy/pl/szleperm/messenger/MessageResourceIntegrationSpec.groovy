package pl.szleperm.messenger

import groovy.json.JsonSlurper
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.http.HttpStatus
import org.springframework.http.MediaType
import org.springframework.mock.web.MockHttpServletResponse
import org.springframework.security.test.context.support.WithAnonymousUser
import org.springframework.security.test.context.support.WithMockUser
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.setup.MockMvcBuilders
import org.springframework.transaction.annotation.Propagation
import org.springframework.transaction.annotation.Transactional
import org.springframework.web.context.WebApplicationContext
import spock.lang.Specification

import java.util.stream.Collectors

import static groovy.json.JsonOutput.toJson
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf
import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put

@SpringBootTest
@AutoConfigureMockMvc
@Transactional(propagation=Propagation.REQUIRES_NEW )
class MessageResourceIntegrationSpec extends Specification{
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
	@WithAnonymousUser
	"should get all messages"(){
        when:
		MockHttpServletResponse response = mvc.perform(get("/api/messages")).andReturn().getResponse()
		def content = json.parseText(response.contentAsString) as Map
		then:
		response.status == HttpStatus.OK.value()
        content.size() == 1
        (content.get(0)["author"] as String) == "user"
	}
    @WithAnonymousUser
    "should get message"(){
        when:
        MockHttpServletResponse response = mvc.perform(get("/api/messages/1")).andReturn().getResponse()
        def content = json.parseText(response.contentAsString) as Map
        then:
        response.status == HttpStatus.OK.value()
        content["content"] as String == "Content"
    }
    @WithMockUser(username = "user", password = "user")
    "should create new message"(){
        when:
        def requestBody = toJson([title: "new message", content: "very funny message! have fun"])
        MockHttpServletResponse response = mvc
                .perform(post("/api/messages")
                    .content(requestBody)
                    .contentType(MediaType.APPLICATION_JSON_VALUE)
                    .with(csrf().asHeader()))
                .andReturn()
                .getResponse()
        then:
        response.status == HttpStatus.OK.value()
    }
    @WithMockUser(username = "user", password = "user")
    "should not create new message when validation fails"(){
        when:
        def requestBody = toJson([title: "", content: ""])
        MockHttpServletResponse response = mvc
                .perform(post("/api/messages")
                .content(requestBody)
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .with(csrf().asHeader()))
                .andReturn()
                .getResponse()
        def content = (json.parseText(response.contentAsString) as List).stream()
                .map({e -> (e as Map)["field"]})
                .collect(Collectors.toList()) as List
        then:
        response.status == HttpStatus.BAD_REQUEST.value()
        content.contains("title")
        content.contains("content")
    }
    @WithAnonymousUser
    "should not create new message when user is anonymous"(){
        when:
        def requestBody = toJson([title: "title", content: "content"])
        MockHttpServletResponse response = mvc
                .perform(post("/api/messages")
                .content(requestBody)
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .with(csrf().asHeader()))
                .andReturn()
                .getResponse()
        then:
        response.status == HttpStatus.FOUND.value()
    }
    @WithMockUser(username = "user", password = "user")
    "should update message when user is author"(){
        when:
        def requestBody = toJson([title: "updated title", content: "updated content"])
        MockHttpServletResponse response = mvc
                .perform(put("/api/messages/1")
                .content(requestBody)
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .with(csrf().asHeader()))
                .andReturn()
                .getResponse()
        def content = json.parseText( mvc.perform(get("/api/messages/1"))
                .andReturn()
                .getResponse()
                .contentAsString) as Map
        then:
        response.status == HttpStatus.OK.value()
        content["id"] as int== 1
        content["title"] as String == "updated title"
        content["content"] as String == "updated content"
        content["author"] as String == "user"
    }
    @WithMockUser(username = "admin", password = "admin", roles = ["ADMIN"])
    "should update message when user is admin"(){
        when:
        def requestBody = toJson([title: "updated title", content: "updated content"])
        MockHttpServletResponse response = mvc
                .perform(put("/api/messages/1")
                .content(requestBody)
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .with(csrf().asHeader()))
                .andReturn()
                .getResponse()
        def content = json.parseText( response.contentAsString) as Map
        then:
        response.status == HttpStatus.OK.value()
        content["id"] as int== 1
        content["title"] as String == "updated title"
        content["content"] as String == "updated content"
        content["author"] as String == "user"
    }
    @WithMockUser(username = "other", password = "other", roles = ["USER"])
    "should not update message when user isn't admin or author"(){
        when:
        def requestBody = toJson([title: "updated title", content: "updated content"])
        MockHttpServletResponse response = mvc
                .perform(put("/api/messages/1")
                .content(requestBody)
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .with(csrf().asHeader()))
                .andReturn()
                .getResponse()
        then:
        response.status == HttpStatus.FORBIDDEN.value()
    }
    @WithMockUser(username = "other", password = "other", roles = ["USER"])
    "should not update message when message not found"(){
        when:
        def requestBody = toJson([title: "updated title", content: "updated content"])
        MockHttpServletResponse response = mvc
                .perform(put("/api/messages/999")
                .content(requestBody)
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .with(csrf().asHeader()))
                .andReturn()
                .getResponse()
        then:
        response.status == HttpStatus.NOT_FOUND.value()
    }
}

