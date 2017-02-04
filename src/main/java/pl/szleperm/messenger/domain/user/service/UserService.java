package pl.szleperm.messenger.domain.user.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import pl.szleperm.messenger.domain.user.entity.Role;
import pl.szleperm.messenger.domain.user.entity.User;
import pl.szleperm.messenger.domain.user.repository.RoleRepository;
import pl.szleperm.messenger.domain.user.repository.UserRepository;
import pl.szleperm.messenger.domain.user.resource.UserProjection;
import pl.szleperm.messenger.web.forms.AccountFormsVM;
import pl.szleperm.messenger.web.forms.UserFormVM;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class UserService {
    private static final String ROLE_USER = "ROLE_USER";
    private final UserRepository userRepository;
    private final RoleRepository roleRepository;

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
    public void create(AccountFormsVM.RegisterFormVM registerFormVM) {
        User user = new User();
        user.setUsername(registerFormVM.getUsername());
        user.setEmail(registerFormVM.getEmail());
        user.setPassword(new BCryptPasswordEncoder().encode(registerFormVM.getPassword()));
        user.setRoles(
                roleRepository.findAll().stream()
                        .filter(r -> r.getName().equals(ROLE_USER))
                        .collect(Collectors.toSet())
        );
        userRepository.save(user);
    }

    @Transactional
    @PreAuthorize("#changePasswordFormVM.username == authentication.name")
    public void changePassword(AccountFormsVM.ChangePasswordFormVM changePasswordFormVM) {
        userRepository.findByUsername(changePasswordFormVM.getUsername())
                .ifPresent(u -> {
                    u.setPassword(new BCryptPasswordEncoder()
                            .encode(changePasswordFormVM.getNewPassword()));
                    userRepository.save(u);
                });
    }
    @Transactional(readOnly = true)
    public Optional<UserProjection> findProjectedByName(String name){
        return userRepository.findProjectedByUsername(name);
    }
    @Transactional(readOnly = true)
    public Page<UserProjection> findAll(Pageable pageable) {
        return userRepository.findPagedProjectedBy(pageable);
    }

    @Transactional
    @PreAuthorize("hasRole('ADMIN')")
    public Optional<UserProjection> updateUser(UserFormVM form, String name) {
        userRepository.findByUsername(name)
                .map(u -> {
                    u.setRoles(form.getRoles().stream()
                            .map(roleRepository::findByName)
                            .map(Optional::get)
                            .collect(Collectors.toSet()));
                    return u;
                }).ifPresent(userRepository::save);
        return userRepository.findProjectedByUsername(name);
    }

    @Transactional
    @PreAuthorize("hasRole('ADMIN')")
    public void delete(String name) {
        userRepository.delete(name);
    }

    @Transactional(readOnly = true)
    public Optional<Role> findRoleByName(String roleName) {
        return roleRepository.findByName(roleName);
    }

    @Transactional(readOnly = true)
    public List<UserProjection> searchByName(String name) {
        return userRepository.findFirst10ByUsernameContaining(name);
    }
    @Transactional(readOnly = true)
    public boolean checkPasswordForUsername(String password, String username){
        return userRepository.findByUsername(username).map(u ->
                new BCryptPasswordEncoder().matches(password, u.getPassword()))
                .orElse(false);
    }//todo test
}