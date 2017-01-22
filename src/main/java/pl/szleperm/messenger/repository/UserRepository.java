package pl.szleperm.messenger.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import pl.szleperm.messenger.domain.User;

import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long>{
	Optional<User> findByUsername(String username);
	Optional<User> findByEmail(String email);
	Optional<User> findById(Long id);
}
