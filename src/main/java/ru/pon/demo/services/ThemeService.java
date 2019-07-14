package ru.pon.demo.services;

import org.springframework.stereotype.Service;
import ru.pon.demo.entity.Theme;
import ru.pon.demo.repository.ThemeRepository;

import java.util.Date;
import java.util.List;

@Service
public class ThemeService {
    final ThemeRepository themeRepository;

    public ThemeService(ThemeRepository themeRepository) {
        this.themeRepository = themeRepository;
    }

    public List<Theme> getNotRemovedSortedThemes() {
        List<Theme> themes = themeRepository.findByRemoved(false);
        themes.sort((o1, o2) -> {
            Date o1LastVisibleChange = o1.getLastNotRemovedMessageOrNull() != null
                    ? o1.getLastNotRemovedMessageOrNull().getDate()
                    : o1.getDate();
            Date o2LastVisibleChange = o2.getLastNotRemovedMessageOrNull() != null
                    ? o2.getLastNotRemovedMessageOrNull().getDate()
                    : o2.getDate();

            return o2LastVisibleChange.compareTo(o1LastVisibleChange);
        });

        return themes;
    }

    public Theme getByIdAndRemoved(Long id, Boolean isRemoved) {
        return themeRepository.findByIdAndRemoved(id, false);
    }

    public void delete(Long id) {
        Theme theme = themeRepository.getOne(id);
        theme.setRemoved(true);
        themeRepository.saveAndFlush(theme);
    }

    public Long save(Theme theme) {
        theme = themeRepository.saveAndFlush(theme);
        return theme.getId();
    }

}
