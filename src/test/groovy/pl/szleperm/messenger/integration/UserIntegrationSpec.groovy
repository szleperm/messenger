package pl.szleperm.messenger.integration

import groovy.json.JsonSlurper
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.http.HttpStatus
import org.springframework.http.MediaType
import org.springframework.mock.web.MockHttpServletResponse
import org.springframework.security.test.context.support.WithMockUser
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.setup.MockMvcBuilders
import org.springframework.transaction.annotation.Propagation
import org.springframework.transaction.annotation.Transactional
import org.springframework.web.context.WebApplicationContext
import spock.lang.Specification

import static groovy.json.JsonOutput.toJson
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf
import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*

@SpringBootTest
@AutoConfigureMockMvc
@Transactional(propagation = Propagation.REQUIRES_NEW)
class UserIntegrationSpec extends Specification {
    @Autowired
    MockMvc mvc
    @Autowired
    WebApplicationContext context
    JsonSlurper json = new JsonSlurper()
    def id = Base64.urlEncoder.encodeToString("user".getBytes())

    def setup() {
        mvc = MockMvcBuilders
                .webAppContextSetup(context)
                .apply(springSecurity())
                .build()
    }
    @WithMockUser(username = "user", password = "user", roles = ["USER"])
    def "should return all users"() {
        when:
        MockHttpServletResponse response = mvc.perform(get("/api/users"))
                .andReturn()
                .getResponse()
        def content = json.parseText(response.contentAsString) as Map
        def users = (content["_embedded"] as Map)["users"] as List
        def links = (content["_links"] as Map)
        then:
        response.status == HttpStatus.OK.value()
        users.size() == 2
        (links["self"]["href"] as String).endsWith("/api/users")
    }
    @WithMockUser(username = "admin", password = "admin", roles = ["ADMIN"])
    def "should not return user when doesn't exist"() {
        when:
        MockHttpServletResponse response = mvc.perform(get("/api/users/999"))
                .andReturn()
                .getResponse()
        then:
        response.status == HttpStatus.NOT_FOUND.value()
    }
    @WithMockUser(username = "admin", password = "admin", roles = ["ADMIN"])
    def "should return user"() {
        when:
        MockHttpServletResponse response = mvc.perform(get("/api/users/" + id))
                .andReturn()
                .getResponse()
        def content = json.parseText(response.contentAsString) as Map
        def roles = content["_embedded"]["roles"] as List
        def links = content["_links"] as Map
        then:
        response.status == HttpStatus.OK.value()
        content["username"] == "user"
        roles.contains([name: "ROLE_USER"])
        (links["self"]["href"] as String).endsWith("api/users/"+id)
    }
    @WithMockUser(username = "user", password = "user", roles = ["USER"])
    def "should return forbidden when user isn't admin"() {
        when:
        MockHttpServletResponse response = mvc.perform(get("/api/users/" + id))
                .andReturn()
                .getResponse()
        then:
        response.status == HttpStatus.FORBIDDEN.value()
    }

    @WithMockUser(username = "admin", password = "admin", roles = ["ADMIN"])
    "should update user"() {
        given:
        def requestBody = toJson([roles: [
                "ROLE_USER",
                "ROLE_ADMIN"
        ]])
        when:
        def response = mvc.perform(patch("/api/users/"+id)
                .content(requestBody)
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .with(csrf().asHeader()))
                .andReturn().response
        def content = json.parseText(response.contentAsString) as Map
        def roles = content["_embedded"]["roles"] as List
        def links = content["_links"] as Map
        then:
        response.status == HttpStatus.OK.value()
        roles.size() == 2
        roles.contains([name: "ROLE_ADMIN"])
        (links["self"]["href"] as String).endsWith("api/users/"+id)
    }

    @WithMockUser(username = "user", password = "user", roles = ["USER"])
    "should not update user when user is not admin"() {
        given:
        def requestBody = toJson([roles: [
                "ROLE_USER",
                "ROLE_ADMIN"
        ]])
        when:
        def response = mvc.perform(patch("/api/users/"+id)
                .content(requestBody)
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .with(csrf().asHeader()))
                .andReturn().response
        then:
        response.status == HttpStatus.FORBIDDEN.value()
    }

    @WithMockUser(username = "admin", password = "admin", roles = ["ADMIN"])
    "should not update user when validation fails"() {
        given:
        def requestBody = toJson([roles: [
                "ROLE_NOBODY",
                "ROLE_ADMIN"
        ]])
        when:
        def response = mvc.perform(patch("/api/users/"+id)
                .content(requestBody)
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .with(csrf().asHeader()))
                .andReturn().response
        then:
        response.status == HttpStatus.BAD_REQUEST.value()
    }
    @WithMockUser(username = "admin", password = "admin", roles = ["ADMIN"])
    "should not update user when doesn't exist"() {
        given:
        def requestBody = toJson([roles: [
                "ROLE_USER",
                "ROLE_ADMIN"
        ]])
        when:
        def response = mvc.perform(patch("/api/users/999")
                .content(requestBody)
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .with(csrf().asHeader()))
                .andReturn().response
        then:
        response.status == HttpStatus.NOT_FOUND.value()
    }

    @WithMockUser(username = "admin", password = "admin", roles = ["ADMIN"])
    "should delete user when user is admin"() {
        when:
        MockHttpServletResponse response = mvc.perform(delete("/api/users/"+id)
                .with(csrf().asHeader()))
                .andReturn()
                .getResponse()
        then:
        response.status == HttpStatus.NO_CONTENT.value()
    }

    @WithMockUser(username = "admin", password = "admin", roles = ["ADMIN"])
    "should not delete user when doesn't exist"() {
        when:
        MockHttpServletResponse response = mvc.perform(delete("/api/users/999")
                .with(csrf().asHeader()))
                .andReturn()
                .getResponse()
        then:
        response.status == HttpStatus.NOT_FOUND.value()
    }

    @WithMockUser(username = "user", password = "user", roles = ["USER"])
    "should not delete user when user is not admin"() {
        when:
        MockHttpServletResponse response = mvc.perform(delete("/api/users/"+id)
                .with(csrf().asHeader()))
                .andReturn()
                .getResponse()
        then:
        response.status == HttpStatus.FORBIDDEN.value()
    }
}