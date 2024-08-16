package pl.apap.qrgenerator.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import pl.apap.qrgenerator.Data.User;

public interface UserRepository extends JpaRepository<User, Long> {
    User findByUsername(String username);
}
