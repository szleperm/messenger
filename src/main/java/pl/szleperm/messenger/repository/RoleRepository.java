package pl.szleperm.messenger.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import pl.szleperm.messenger.domain.Role;

import java.util.Optional;

public interface RoleRepository extends JpaRepository<Role, Long>{
	Optional<Role> findByName(String name);
}
