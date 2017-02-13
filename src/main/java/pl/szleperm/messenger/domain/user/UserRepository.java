package pl.szleperm.messenger.domain.user;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface UserRepository extends JpaRepository<User, String> {

    Optional<User> findByUsername(String username);

    Optional<User> findByEmail(String email);

    Page<UserProjection> findPagedProjectedBy(Pageable pageable);

    Optional<UserProjection> findProjectedByUsername(String name);

    Page<UserProjection> findByUsernameContaining(String name, Pageable pageable);
}

