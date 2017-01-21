package pl.szleperm.messenger

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.*
import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.*
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*

import java.util.stream.Collectors

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.http.HttpStatus
import org.springframework.mock.web.MockHttpServletResponse
import org.springframework.security.test.context.support.WithMockUser
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.setup.MockMvcBuilders
import org.springframework.transaction.annotation.Propagation
import org.springframework.transaction.annotation.Transactional
import org.springframework.web.context.WebApplicationContext

import groovy.json.JsonSlurper
import pl.szleperm.messenger.domain.User
import spock.lang.Specification

@SpringBootTest
@AutoConfigureMockMvc
@Transactional(propagation=Propagation.REQUIRES_NEW )
class UserResourceIntegrationSpec extends Specification{
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
	def "should return all users"(){
		when:
			MockHttpServletResponse response = mvc.perform(get("/users")).andReturn().getResponse()
			def content = json.parseText(response.contentAsString) as List
		then:
			response.status == HttpStatus.OK.value
			content.size() == 2 
			(content.stream()
					.map{m -> (m as Map)["name"]}
					.collect(Collectors.toList()) as List).contains("user")
	}
	def "should not return user when doesn't exist"(){
		when:
			MockHttpServletResponse response = mvc.perform(get("/users/999")).andReturn().getResponse()
		then:
			response.status == HttpStatus.NOT_FOUND.value
	}
	def "should return user"(){
		when:
			MockHttpServletResponse response = mvc.perform(get("/users/1")).andReturn().getResponse()
			def content = json.parseText(response.contentAsString) as Map
		then:
			response.status == HttpStatus.OK.value
			content["id"] as int == 1
			content["name"] == "user"
			(content["roles"] as List).contains("ROLE_USER")
	}
	@WithMockUser(username="admin", password="admin",roles=["ADMIN"])
	def "should delete user when user is admin"(){
		when:
			MockHttpServletResponse response = mvc.perform(delete("/users/1").with(csrf().asHeader())).andReturn().getResponse()
			def content = json.parseText(response.contentAsString) as Map
		then:
			response.status == HttpStatus.OK.value
			content.containsKey("message")
	}
	@WithMockUser(username="admin", password="admin",roles=["ADMIN"])
	def "should not delete user when doesn't exist"(){
		when:
			MockHttpServletResponse response = mvc.perform(delete("/users/999").with(csrf().asHeader())).andReturn().getResponse()
		then:
			response.status == HttpStatus.NOT_FOUND.value
	}
	@WithMockUser(username="user", password="user",roles=["USER"])
	def "should not delete user when user is not admin"(){
		when:
			MockHttpServletResponse response = mvc.perform(delete("/users/2").with(csrf().asHeader())).andReturn().getResponse()
		then:
			response.status == HttpStatus.FORBIDDEN.value
	}
}

