package ru.pon.demo.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.pon.demo.entity.User;

public interface UserRepository extends JpaRepository<User, Long> {
    User findByUsername(String username);
}
