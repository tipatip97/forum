package ru.pon.demo.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.pon.demo.entity.Theme;

import java.util.List;

public interface ThemeRepository extends JpaRepository<Theme, Long> {
    List<Theme> findByRemoved(Boolean removed);
    Theme findByIdAndRemoved(Long id, Boolean removed);
}
