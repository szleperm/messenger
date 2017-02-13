package pl.szleperm.messenger.domain.user;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import pl.szleperm.messenger.domain.user.form.PasswordForm;
import pl.szleperm.messenger.domain.user.form.RegisterForm;
import pl.szleperm.messenger.domain.user.form.UserForm;

import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class UserService {
    private static final String ROLE_USER = "ROLE_USER";
    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final UserResourceAssembler assembler = new UserResourceAssembler();

    @Autowired
    public UserService(UserRepository userRepository, RoleRepository roleRepository) {
        super();
        this.userRepository = userRepository;
        this.roleRepository = roleRepository;
    }

    @Transactional(readOnly = true)
    public Optional<User> findByName(String name) {
        return userRepository.findByUsername(name);
    }

    @Transactional(readOnly = true)
    public Optional<User> findByEmail(String email) {
        return userRepository.findByEmail(email);
    }

    @Transactional
    public void create(RegisterForm form) {
        User user = new User();
        user.setUsername(form.getUsername());
        user.setEmail(form.getEmail());
        user.setPassword(new BCryptPasswordEncoder().encode(form.getPassword()));
        user.setRoles(
                roleRepository.findAll().stream()
                        .filter(r -> r.getName().equals(ROLE_USER))
                        .collect(Collectors.toSet())
        );
        userRepository.save(user);
    }

    @Transactional
    @PreAuthorize("#form.username == authentication.name")
    public void changePassword(PasswordForm form) {
        userRepository.findByUsername(form.getUsername())
                .ifPresent(u -> {
                    u.setPassword(new BCryptPasswordEncoder()
                            .encode(form.getNewPassword()));
                    userRepository.save(u);
                });
    }
    @Transactional(readOnly = true)
    public Optional<UserResource> findResourceByName(String name){
        return userRepository.findProjectedByUsername(name)
                .map(assembler::toResource);
    }

    @Transactional
    @PreAuthorize("hasRole('ADMIN')")
    public Optional<UserResource> update(UserForm form, String name) {
        userRepository.findByUsername(name)
                .map(u -> {
                    u.setRoles(form.getRoles().stream()
                            .map(roleRepository::findByName)
                            .map(Optional::get)
                            .collect(Collectors.toSet()));
                    return u;
                }).ifPresent(userRepository::save);
        return userRepository.findProjectedByUsername(name)
                .map(assembler::toResource);
    }

    @Transactional
    @PreAuthorize("hasRole('ADMIN')")
    public Optional<String> delete(String name) {
        Optional<User> user = userRepository.findByUsername(name);
        user.ifPresent(userRepository::delete);
        return user.map(User::getUsername);
    }

    @Transactional(readOnly = true)
    public Optional<Role> findRoleByName(String roleName) {
        return roleRepository.findByName(roleName);
    }

    @Transactional(readOnly = true)
    public Page<UserResource> searchByName(String name, Pageable pageable) {
        return userRepository.findByUsernameContaining(name, pageable)
                .map(assembler::toResource);
    }
    @Transactional(readOnly = true)
    public boolean checkPasswordForUsername(String password, String username){
        return userRepository.findByUsername(username).map(u ->
                new BCryptPasswordEncoder().matches(password, u.getPassword()))
                .orElse(false);
    }
}