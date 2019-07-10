package ru.pon.demo.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.pon.demo.entity.Message;

public interface MessageRepository extends JpaRepository<Message, Long> {
    Message findByIdAndRemoved(Long id, Boolean removed);

}
