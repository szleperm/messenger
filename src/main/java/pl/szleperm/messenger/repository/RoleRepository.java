package pl.szleperm.messenger.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import pl.szleperm.messenger.domain.Role;

public interface RoleRepository extends JpaRepository<Role, Long>{
	Optional<Role> findByName(String name);
}
