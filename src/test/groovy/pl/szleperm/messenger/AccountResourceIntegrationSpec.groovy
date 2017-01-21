package pl.szleperm.messenger

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.*
import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.*
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.json.BasicJsonParser
import org.springframework.boot.test.autoconfigure.json.AutoConfigureJson
import org.springframework.boot.test.autoconfigure.json.AutoConfigureJsonTesters
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

import groovy.json.JsonOutput
import groovy.json.JsonSlurper
import spock.lang.Specification

@SpringBootTest
@AutoConfigureMockMvc
@Transactional(propagation=Propagation.REQUIRES_NEW )
class AccountResourceIntegrationSpec extends Specification{
	
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
	def "should redirect when user is anonymous"(){
		expect:
			mvc.perform(get("/me")).andExpect(status().is3xxRedirection())
	}
	@WithMockUser(username="user", password="user")
	def "should get account data"(){
		when:
			MockHttpServletResponse response = mvc.perform(get("/me")).andReturn().getResponse()
			def content = json.parseText(response.contentAsString) as Map
		then:
			response.status == HttpStatus.OK.value	
			content["id"] as int == 1						
			content["name"] as String == "user"
			content["email"] as String == "user@user"
			(content["roles"] as List).size() == 1
			(content["roles"] as List).contains("ROLE_USER")
	}
	@WithAnonymousUser
	def "should return remote validation response"(){
		given: "prepare json request with username: 'user' and email 'user@user'"
			def requestBody = JsonOutput.toJson([username: "user", email: "user@user"])
		when: 
			def response = mvc.perform(post("/register/available")
								.content(requestBody)
								.contentType(MediaType.APPLICATION_JSON_VALUE)
								.with(csrf().asHeader()))
							.andReturn().response
			def content = json.parseText(response.contentAsString) as Map
		then:
			response.status == HttpStatus.OK.value
			content.size() == 2
			content["username"] as boolean == false
			content["email"] as boolean == false
	}
	@WithMockUser(username="user", password="user")
	def "should change password"(){
		given:
			def requestBody = JsonOutput.toJson([username: "user",
											oldPassword: "user", 
											newPassword: "1234", 
											confirmNewPassword: "1234"])
									
		when:
			def response = mvc.perform(post("/change_password")
								.content(requestBody)
								.contentType(MediaType.APPLICATION_JSON_VALUE)
								.with(csrf().asHeader()))
							.andReturn().response
		then:
			response.status == HttpStatus.OK.value
	}
	@WithAnonymousUser
	def "should not change password when user is anonymous"(){
		given:
			def requestBody = JsonOutput.toJson([username: "user",
											oldPassword: "user",
											newPassword: "1234",
											confirmNewPassword: "1234"])
		when:
			def response = mvc.perform(post("/change_password")
								.content(requestBody)
								.contentType(MediaType.APPLICATION_JSON_VALUE)
								.with(csrf().asHeader()))
							.andReturn().response
		then:
			response.status == HttpStatus.FOUND.value
	}
	@WithMockUser(username="admin", password="admin")
	def "should not change password when other user is authorized"(){
		given:
			def requestBody = JsonOutput.toJson([username: "user",
											oldPassword: "user",
											newPassword: "1234",
											confirmNewPassword: "1234"])
		when:
			def response = mvc.perform(post("/change_password")
								.content(requestBody)
								.contentType(MediaType.APPLICATION_JSON_VALUE)
								.with(csrf().asHeader()))
							.andReturn().response
		then:
			response.status == HttpStatus.FORBIDDEN.value
	}
	@WithAnonymousUser
	def "should register user"(){
		given:
			def requestBody = JsonOutput.toJson([username: "new_user",
											email: "email@email",
											password: "1234",
											confirmPassword: "1234"])
		when:
			def response = mvc.perform(post("/register")
								.content(requestBody)
								.contentType(MediaType.APPLICATION_JSON_VALUE)
								.with(csrf().asHeader()))
							.andReturn().response
		then:
			response.status == HttpStatus.OK.value
	}
	@WithAnonymousUser
	def "should not register user when username already in use"(){
		given:
			def requestBody = JsonOutput.toJson([username: "user",
											email: "email@email",
											password: "1234",
											confirmPassword: "1234"])
		when:
			def response = mvc.perform(post("/register")
								.content(requestBody)
								.contentType(MediaType.APPLICATION_JSON_VALUE)
								.with(csrf().asHeader()))
							.andReturn().response
			def content = json.parseText(response.contentAsString) as Map
		then:
			response.status == HttpStatus.BAD_REQUEST.value
			(content["field"] as List).contains("username")
			(content["message"] as List).contains("Username user already in use")
	}
}