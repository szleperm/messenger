package pl.szleperm.messenger.integration

import groovy.json.JsonOutput
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

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf
import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status

@SpringBootTest
@AutoConfigureMockMvc
@Transactional(propagation = Propagation.REQUIRES_NEW)
class AccountIntegrationSpec extends Specification {
    @Autowired
    MockMvc mvc
    @Autowired
    WebApplicationContext context
    JsonSlurper json = new JsonSlurper()

    def setup() {
        mvc = MockMvcBuilders
                .webAppContextSetup(context)
                .apply(springSecurity())
                .build()
    }

    @WithAnonymousUser
    "should redirect when user is anonymous"() {
        expect:
        mvc.perform(get("/api/account")).andExpect(status().is3xxRedirection())
    }

    @WithMockUser(username = "user", password = "user")
    "should get account data"() {
        when:
        MockHttpServletResponse response = mvc.perform(get("/api/account")).andReturn().getResponse()
        def content = json.parseText(response.contentAsString) as Map
        def embedded = content["_embedded"] as Map
        def links = content["_links"] as Map
        then:
        response.status == HttpStatus.OK.value()
        content["username"] as String == "user"
        content["email"] as String == "user@user"
        (embedded["roles"] as List).size() == 1
        (embedded["roles"] as List).contains([name: "ROLE_USER"])
        links.containsKey("self")
        links.containsKey("users")
    }

    @WithAnonymousUser
    "should return remote validation response"() {
        given: "prepare json request with username: 'user' and email 'user@user'"
        def requestBody = JsonOutput.toJson([username: "user", email: "user@user"])
        when:
        def response = mvc.perform(post("/api/account/register/available")
                .content(requestBody)
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .with(csrf().asHeader()))
                .andReturn().response
        def content = json.parseText(response.contentAsString) as Map
        then:
        response.status == HttpStatus.OK.value()
        content.size() == 2
        content["username"] as String == "false"
        content["email"] as String == "false"
    }

    @WithMockUser(username = "user", password = "user")
    "should change password"() {
        given:
        def requestBody = JsonOutput.toJson([username          : "user",
                                             oldPassword       : "user",
                                             newPassword       : "1234",
                                             confirmNewPassword: "1234"])

        when:
        def response = mvc.perform(patch("/api/account/change_password")
                .content(requestBody)
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .with(csrf().asHeader()))
                .andReturn().response
        then:
        response.status == HttpStatus.OK.value()
    }

    @WithAnonymousUser
    "should not change password when user is anonymous"() {
        given:
        def requestBody = JsonOutput.toJson([username          : "user",
                                             oldPassword       : "user",
                                             newPassword       : "1234",
                                             confirmNewPassword: "1234"])
        when:
        def response = mvc.perform(patch("/api/account/change_password")
                .content(requestBody)
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .with(csrf().asHeader()))
                .andReturn().response
        then:
        response.status == HttpStatus.FOUND.value()
    }

    @WithMockUser(username = "admin", password = "admin")
    "should not change password when other user is authorized"() {
        given:
        def requestBody = JsonOutput.toJson([username          : "user",
                                             oldPassword       : "user",
                                             newPassword       : "1234",
                                             confirmNewPassword: "1234"])
        when:
        def response = mvc.perform(patch("/api/account/change_password")
                .content(requestBody)
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .with(csrf().asHeader()))
                .andReturn().response
        then:
        response.status == HttpStatus.FORBIDDEN.value()
    }

    @WithAnonymousUser
    "should register user"() {
        given:
        def requestBody = JsonOutput.toJson([username       : "new_user",
                                             email          : "email@email",
                                             password       : "1234",
                                             confirmPassword: "1234"])
        when:
        def response = mvc.perform(post("/api/account/register")
                .content(requestBody)
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .with(csrf().asHeader()))
                .andReturn().response
        then:
        response.status == HttpStatus.CREATED.value()
        response.getHeader("location").endsWith("api/account")
    }

    @WithAnonymousUser
    "should not register user when username already in use"() {
        given:
        def requestBody = JsonOutput.toJson([username       : "user",
                                             email          : "email@email",
                                             password       : "1234",
                                             confirmPassword: "1234"])
        when:
        def response = mvc.perform(post("/api/account/register")
                .content(requestBody)
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .with(csrf().asHeader()))
                .andReturn().response
        def content = json.parseText(response.contentAsString) as Map
        then:
        response.status == HttpStatus.BAD_REQUEST.value()
        (content["field"] as List).contains("username")
        (content["message"] as List).contains("Username user already in use")
    }
}