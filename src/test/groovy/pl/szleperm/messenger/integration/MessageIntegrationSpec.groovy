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
import spock.lang.Unroll

import static groovy.json.JsonOutput.toJson
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf
import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put

@SpringBootTest
@AutoConfigureMockMvc
@Transactional(propagation = Propagation.REQUIRES_NEW)
class MessageIntegrationSpec extends Specification {
    public static final String USERS = "/api/users"
    public static final String ACCOUNT = "/api/account"
    public static final String MESSAGES = "/api/messages"
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

    @WithMockUser(username = "user")
    def "should return all messages for user: 'user'"() {
        when:
        MockHttpServletResponse response = mvc.perform(get(MESSAGES))
                .andReturn()
                .getResponse()
        def content = json.parseText(response.contentAsString) as Map
        def messages = (content["_embedded"] as Map)["messages"] as List
        def links = (content["_links"] as Map)
        then:
        response.status == HttpStatus.OK.value()
        messages.size() == 2
        (links["self"]["href"] as String).endsWith(MESSAGES)
        (links["users"]["href"] as String).endsWith(USERS)
        (links["account"]["href"] as String).endsWith(ACCOUNT)
    }

    @Unroll
    @WithMockUser(username = "user")
    def "should return messages with page links for user: 'user'"() {
        when:
        MockHttpServletResponse response = mvc.perform(get(MESSAGES + '?' + suffix))
                .andReturn()
                .getResponse()
        def content = json.parseText(response.contentAsString) as Map
        def messages = (content["_embedded"] as Map)["messages"] as List
        def links = (content["_links"] as Map)
        then:
        response.status == HttpStatus.OK.value()
        messages.size() == size
        (links["self"]["href"] as String).endsWith(MESSAGES + '?' + suffix)
        (links["users"]["href"] as String).endsWith(USERS)
        (links["account"]["href"] as String).endsWith(ACCOUNT)
        links.containsKey("next") == next
        links.containsKey("prev") == prev
        links.containsKey("first") == first
        links.containsKey("last") == last
        where:
        suffix          | size | next  | prev  | first | last
        "size=1"        | 1    | true  | false | true  | true
        "size=1&page=1" | 1    | false | true  | true  | true
        "size=10"       | 2    | false | false | false | false
    }

    @Unroll
    @WithMockUser(username = "user")
    def "should return #size message(s) for user: 'user' with path params '#suffix'"() {
        when:
        MockHttpServletResponse response = mvc.perform(get(MESSAGES + '?' + suffix))
                .andReturn()
                .getResponse()
        def content = json.parseText(response.contentAsString) as Map
        def embedded = (content["_embedded"] as Map)
        def links = (content["_links"] as Map)
        then:
        response.status == HttpStatus.OK.value()
        (embedded == null) == empty
        embedded == null ? true : (embedded["messages"] as Map).size() == size
        (links["self"]["href"] as String).endsWith(MESSAGES + '?' + suffix)
        (links["users"]["href"] as String).endsWith(USERS)
        (links["account"]["href"] as String).endsWith(ACCOUNT)
        where:
        suffix                 | size | empty
        "search=hello"         | 1    | false
        "search=wxffdfg"       | 0    | true
        "with=admin"           | 2    | false
        "from=admin"           | 1    | false
        "read=false"           | 1    | false
        "to=admin"             | 1    | false
        "sent=true"            | 1    | false
        "read=true&from=admin" | 0    | true
        "sent=false&to=admin"  | 1    | false
    }

    @WithMockUser(username = "admin")
    def "should return empty page for user: 'admin'"() {
        when:
        MockHttpServletResponse response = mvc.perform(get(MESSAGES))
                .andReturn()
                .getResponse()
        def content = json.parseText(response.contentAsString) as Map
        def messages = content["_embedded"] as Map
        def links = (content["_links"] as Map)
        then:
        response.status == HttpStatus.OK.value()
        messages == null
        (links["self"]["href"] as String).endsWith(MESSAGES)
        (links["users"]["href"] as String).endsWith(USERS)
        (links["account"]["href"] as String).endsWith(ACCOUNT)
    }

    @WithMockUser(username = "user")
    def "should return message for user: 'user'"() {
        when:
        def response = mvc.perform(get(MESSAGES + "/1"))
                .andReturn()
                .getResponse()
        def content = json.parseText(response.contentAsString) as Map
        def links = (content["_links"] as Map)
        then:
        response.status == HttpStatus.OK.value()
        content["read"] == true
        (links["self"]["href"] as String).endsWith(MESSAGES + "/1")
    }

    @WithMockUser(username = "admin")
    def "should not return message when user isn't owner"() {
        when:
        def response = mvc.perform(get(MESSAGES + "/1"))
                .andReturn()
                .getResponse()
        then:
        response.status == HttpStatus.NOT_FOUND.value()
    }

    @WithMockUser(username = "user")
    def "should not return message when doesn't exist"() {
        when:
        def response = mvc.perform(get(MESSAGES + "/99"))
                .andReturn()
                .getResponse()
        then:
        response.status == HttpStatus.NOT_FOUND.value()
    }

    @WithMockUser(username = "admin", password = "admin")
    def "should add draft"() {
        given:
        def requestBody = toJson(subject: "subject", body: "body", to: "user")
        when:
        def response = mvc.perform(post(MESSAGES)
                .content(requestBody)
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .with(csrf().asHeader()))
                .andReturn().response
        then:
        response.status == HttpStatus.CREATED.value()
    }

    @WithMockUser(username = "admin")
    def "should not add draft when recipient is invalid"() {
        given:
        def requestBody = toJson(subject: "subject", body: "body", to: "invalid")
        when:
        def response = mvc.perform(post(MESSAGES)
                .content(requestBody)
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .with(csrf().asHeader()))
                .andReturn().response
        then:
        response.status == HttpStatus.BAD_REQUEST.value()
    }

    @WithMockUser(username = "user")
    def "should send draft"() {
        when:
        def response = mvc.perform(post(MESSAGES + "/1")
                .with(csrf().asHeader()))
                .andReturn().response
        def content = json.parseText(response.contentAsString) as Map
        then:
        response.status == HttpStatus.OK.value()
        content["sent"] == true
    }
    @WithMockUser(username = "admin")
    def "should not send draft when user isn't message owner"() {
        when:
        def response = mvc.perform(post(MESSAGES + "/1")
                .with(csrf().asHeader()))
                .andReturn().response
        then:
        response.status == HttpStatus.NOT_FOUND.value()
    }
    @WithMockUser(username = "user")
    def "should update draft"() {
        given:
        def requestBody = toJson(subject: "new subject", body: "new body", to: "admin")
        when:
        def response = mvc.perform(put(MESSAGES + "/1")
                .content(requestBody)
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .with(csrf().asHeader()))
                .andReturn().response
        def content = json.parseText(response.contentAsString) as Map
        then:
        response.status == HttpStatus.OK.value()
        content["subject"] == "new subject"
        content["body"] == "new body"
        content["to"] == "admin"
        content["sent"] == false
    }
    @WithMockUser(username = "admin")
    def "should add and send message"() {
        given:
        def requestBody = toJson(subject: "new subject", body: "new body", to: "admin")
        when:
        def response = mvc.perform(post(MESSAGES + "/send")
                .content(requestBody)
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .with(csrf().asHeader()))
                .andReturn().response
        then:
        response.status == HttpStatus.CREATED.value()
    }
    @WithMockUser(username = "user")
    def "should delete message"() {
        when:
        def response = mvc.perform(delete(MESSAGES + "/2")
                .with(csrf().asHeader()))
                .andReturn().response
        then:
        response.status == HttpStatus.NO_CONTENT.value()
    }
}