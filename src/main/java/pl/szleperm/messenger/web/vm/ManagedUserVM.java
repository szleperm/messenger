package pl.szleperm.messenger.web.vm;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import pl.szleperm.messenger.domain.Role;
import pl.szleperm.messenger.domain.User;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@JsonInclude(Include.NON_NULL)
public class ManagedUserVM {
	private Long id;
	private String name;
	private String email;
	private List<String> roles = new ArrayList<>();
	private List<Map<Long, String>> messages;
	public ManagedUserVM() {
	}
	public ManagedUserVM(User user) {
		this.id = user.getId();
		this.name = user.getUsername();
		this.email = user.getEmail();
		this.roles = user.getRoles().stream()
						.map(Role::getName)
						.collect(Collectors.toList());
		this.messages = user.getMessages().stream()
						.map(m -> Collections.singletonMap(m.getId(),m.getTitle()))
						.collect(Collectors.toList());
	}

	public Long getId() {
		return id;
	}
	public void setId(Long id) {
		this.id = id;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getEmail() {
		return email;
	}
	public void setEmail(String email) {
		this.email = email;
	}
	public List<String> getRoles() {
		return roles;
	}
	public void setRoles(List<String> roles) {
		this.roles = roles;
	}
	public List<Map<Long, String>> getMessages() {
		return messages;
	}
	public void setMessages(List<Map<Long, String>> messages) {
		this.messages = messages;
	}
}
