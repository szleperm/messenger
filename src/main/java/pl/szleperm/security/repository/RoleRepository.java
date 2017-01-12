package pl.szleperm.security.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import pl.szleperm.security.model.Role;

public interface RoleRepository extends JpaRepository<Role, Long>{
	Optional<Role> findByName(String name);
}
