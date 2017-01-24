package pl.szleperm.messenger.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import pl.szleperm.messenger.domain.User;
import pl.szleperm.messenger.domain.projection.UserSimplifiedProjection;

import java.util.List;
import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long>{
	Optional<User> findByUsername(String username);
	Optional<User> findByEmail(String email);
	Optional<User> findById(Long id);
	List<UserSimplifiedProjection> findAllProjectedBy();
}
