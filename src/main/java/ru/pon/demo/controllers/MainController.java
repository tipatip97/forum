package ru.pon.demo.controllers;

import javassist.NotFoundException;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import ru.pon.demo.entity.Message;
import ru.pon.demo.entity.Role;
import ru.pon.demo.entity.Theme;
import ru.pon.demo.entity.User;
import ru.pon.demo.model.NewMessage;
import ru.pon.demo.model.NewTheme;
import ru.pon.demo.repository.MessageRepository;
import ru.pon.demo.repository.ThemeRepository;
import ru.pon.demo.repository.UserRepository;

import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

@Controller
public class MainController {
    private final ThemeRepository themeRepository;
    private final MessageRepository messageRepository;
    private final UserRepository userRepository;

    public MainController(ThemeRepository themeRepository, MessageRepository messageRepository, UserRepository userRepository) {
        this.themeRepository = themeRepository;
        this.messageRepository = messageRepository;
        this.userRepository = userRepository;
    }


    /**
     * Генерирование пользователя (заглушка, пока нет spring security)
     * @return Редирект на главную страницу
     */
    @Deprecated
    @GetMapping(path = "/generateUser")
    public String generate() {
        User user = new User();
        user.setUsername("Pabel");
        user.setPassword("qwerty");
        user.setRole(Role.USER);
        userRepository.saveAndFlush(user);


        return "redirect:/";
    }

    @GetMapping(path = "/")
    public String themes(ModelMap model) {
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

        model.addAttribute("themes", themes);
        return "themes";
    }

    @GetMapping(path = "/theme/{theme_id}")
    public String theme(ModelMap model, @PathVariable("theme_id") Long themeId) {
        Theme theme = themeRepository.findByIdAndRemoved(themeId, false);
            String themeName = theme.getTitle();
        String author = theme.getAuthor().getUsername();

        List<Message> messages = theme.getMessages().stream()
                .filter(message -> !message.getRemoved())
                .sorted(Comparator.comparing(Message::getDate))
                .collect(Collectors.toList());

        model.addAttribute("themeName", themeName);
        model.addAttribute("author", author);
        model.addAttribute("themeId", themeId);
        model.addAttribute("messages", messages);
        return "theme";
    }

    @GetMapping(path = "/delete-message/{themeId}/{messageId}")
    public String deleteMessage(ModelMap model, @PathVariable("themeId") Long themeId, @PathVariable("messageId") Long messageId) {
        Message message = messageRepository.findByIdAndRemoved(messageId, false);
        if (message != null) {
            message.setRemoved(true);
            messageRepository.saveAndFlush(message);
        }

        return "redirect:/theme/" + themeId;
    }

    @PostMapping(path = "/new-message/{themeId}")
    public String newMessage(ModelMap model, @PathVariable Long themeId, @ModelAttribute NewMessage newMessage) throws NotFoundException {
        if (newMessage == null || newMessage.getText() == null || newMessage.getText().isEmpty()) {
            return "redirect:/theme/" + themeId;
        }
        Message message = new Message();
        message.setAuthor(userRepository.findAll().get(0));
        message.setText(newMessage.getText());
        message.setDate(new Date());
        message.setRemoved(false);
        messageRepository.saveAndFlush(message);

        Theme theme = themeRepository.findById(themeId).orElseThrow(() -> new NotFoundException("theme not found"));

        theme.getMessages().add(message);
        themeRepository.saveAndFlush(theme);

        return "redirect:/theme/" + themeId;
    }


    @PostMapping(path = "/new-theme")
    public String newTheme(ModelMap model, @ModelAttribute NewTheme newTheme)   {
        if (newTheme == null || newTheme.getTitle() == null || newTheme.getTitle().isEmpty()) {
            return "redirect:/";
        }

        Theme theme = new Theme();
        theme.setTitle(newTheme.getTitle());
        theme.setAuthor(userRepository.findAll().get(0));
        theme.setDate(new Date());
        theme.setRemoved(false);

        theme = themeRepository.saveAndFlush(theme);

        return "redirect:/theme/" + theme.getId();
    }

}
