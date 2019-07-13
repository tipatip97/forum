package ru.pon.demo.controllers;

import javassist.NotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
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
import ru.pon.demo.services.UserService;

import java.security.Principal;
import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

@Controller
public class MainController {
    private final ThemeRepository themeRepository;
    private final MessageRepository messageRepository;
    private final UserRepository userRepository;
    private final UserService userService;

    public MainController(ThemeRepository themeRepository, MessageRepository messageRepository, UserRepository userRepository, UserService userService) {
        this.themeRepository = themeRepository;
        this.messageRepository = messageRepository;
        this.userRepository = userRepository;
        this.userService = userService;
    }

    @GetMapping(path = "/")
    public String themes(ModelMap model, Principal principal) {
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
        model.addAttribute("currentUser", principal.getName());
        model.addAttribute("isAdmin", userService.getUser(principal.getName()).getRole() == Role.ADMIN);
        return "themes";
    }

    @GetMapping(path = "/theme/{theme_id}")
    public String theme(ModelMap model, Principal principal, @PathVariable("theme_id") Long themeId) {
        Theme theme = themeRepository.findByIdAndRemoved(themeId, false);
            String themeName = theme.getTitle();
        String author = theme.getAuthor().getFirstName() + " " + theme.getAuthor().getLastName();

        List<Message> messages = theme.getMessages().stream()
                .filter(message -> !message.getRemoved())
                .sorted(Comparator.comparing(Message::getDate))
                .collect(Collectors.toList());

        model.addAttribute("currentUser", principal.getName());
        model.addAttribute("isAdmin", userService.getUser(principal.getName()).getRole() == Role.ADMIN);

        model.addAttribute("themeName", themeName);
        model.addAttribute("author", author);
        model.addAttribute("themeId", themeId);
        model.addAttribute("messages", messages);
        return "theme";
    }

    @GetMapping(path = "/delete-message/{themeId}/{messageId}")
    public String deleteMessage(ModelMap model, Principal principal, @PathVariable("themeId") Long themeId, @PathVariable("messageId") Long messageId) {

        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        User currentUser = userService.getUser(auth.getName());

        Message message = messageRepository.findByIdAndRemoved(messageId, false);
        if (message != null) {

            if (message.getAuthor().equals(currentUser)) {
                message.setRemoved(true);
                messageRepository.saveAndFlush(message);
            } else {
                model.addAttribute("errorMessage", "У вас недостаточно прав для этого действия");
                return theme(model, principal, themeId);
            }
        }
        return ";";
    }

    @GetMapping(path = "/delete-theme/{themeId}")
    public String deleteTheme(ModelMap model, Principal principal, @PathVariable("themeId") Long themeId) {

        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        User currentUser = userService.getUser(auth.getName());

        if (currentUser.getRole() != Role.ADMIN) {
            Theme theme = themeRepository.findByIdAndRemoved(themeId, false);

            if (theme != null) {
                theme.setRemoved(true);
                themeRepository.saveAndFlush(theme);
            }
        } else {
            model.addAttribute("errorMessage", "У вас недостаточно прав для этого действия");
        }

        return "redirect:/";
    }

    @PostMapping(path = "/new-message/{themeId}")
    public String newMessage(ModelMap model, Principal principal, @PathVariable Long themeId, @ModelAttribute NewMessage newMessage) throws NotFoundException {
        if (newMessage == null || newMessage.getText() == null || newMessage.getText().isEmpty()) {
            return "redirect:/theme/" + themeId;
        }

        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        User currentUser = userService.getUser(auth.getName());

        Message message = new Message();
        message.setAuthor(currentUser);
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
    public String newTheme(ModelMap model, Principal principal, @ModelAttribute NewTheme newTheme)   {
        if (newTheme == null || newTheme.getTitle() == null || newTheme.getTitle().isEmpty()) {
            return "redirect:/";
        }

        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        User currentUser = userService.getUser(auth.getName());

        Theme theme = new Theme();
        theme.setTitle(newTheme.getTitle());
        theme.setAuthor(currentUser);
        theme.setDate(new Date());
        theme.setRemoved(false);

        theme = themeRepository.saveAndFlush(theme);

        return "redirect:/theme/" + theme.getId();
    }

}
