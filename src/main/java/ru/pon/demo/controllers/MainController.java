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
import ru.pon.demo.entity.Theme;
import ru.pon.demo.entity.User;
import ru.pon.demo.model.NewMessage;
import ru.pon.demo.model.NewTheme;
import ru.pon.demo.repository.MessageRepository;
import ru.pon.demo.repository.ThemeRepository;
import ru.pon.demo.repository.UserRepository;
import ru.pon.demo.services.MessageService;
import ru.pon.demo.services.ThemeService;
import ru.pon.demo.services.UserService;

import java.security.Principal;
import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

@Controller
public class MainController {
    private final UserService userService;
    private final ThemeService themeService;
    private final MessageService messageService;

    public MainController(UserService userService, ThemeService themeService, MessageService messageService) {
        this.userService = userService;
        this.themeService = themeService;
        this.messageService = messageService;
    }

    @GetMapping(path = "/")
    public String main() {
        return "redirect:/themes";
    }

    @GetMapping(path = "/themes")
    public String themes(ModelMap model, Principal principal) {
        List<Theme> themes = themeService.getNotRemovedSortedThemes();

        model.addAttribute("themes", themes);
        model.addAttribute("currentUser", principal.getName());
        model.addAttribute("isAdmin", userService.getUser(principal.getName()).isAdmin());
        return "themes";
    }

    @GetMapping(path = "/themes/{theme_id}")
    public String theme(ModelMap model, Principal principal, @PathVariable("theme_id") Long themeId) {
        Theme theme = themeService.getByIdAndRemoved(themeId, false);

        String themeName = theme.getTitle();

        String author = theme.getAuthor().getFirstName() + " " + theme.getAuthor().getLastName();

        List<Message> messages = theme.getMessages().stream()
                .filter(message -> !message.getRemoved())
                .sorted(Comparator.comparing(Message::getDate))
                .collect(Collectors.toList());

        model.addAttribute("currentUser", principal.getName());
        model.addAttribute("isAdmin", userService.getUser(principal.getName()).isAdmin());

        model.addAttribute("themeName", themeName);
        model.addAttribute("author", author);
        model.addAttribute("themeId", themeId);
        model.addAttribute("messages", messages);
        return "theme";
    }

    @PostMapping(path = "/themes/{themeId}/{messageId}")
    public String deleteMessage(ModelMap model, Principal principal, @PathVariable("themeId") Long themeId, @PathVariable("messageId") Long messageId) {

        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        User currentUser = userService.getUser(auth.getName());

        User messageAuthor = messageService.getByIdAndRemoved(messageId, false).getAuthor();

        if (messageAuthor.equals(currentUser)) {
            messageService.deleteMessage(messageId);
        }

        return "redirect:/themes/" + themeId;
    }

    @PostMapping(path = "themes/{themeId}")
    public String deleteTheme(ModelMap model, Principal principal, @PathVariable("themeId") Long themeId) {

        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        User currentUser = userService.getUser(auth.getName());

        if (currentUser.isAdmin()) {
            themeService.delete(themeId);
        }

        return "redirect:/themes";
    }

    @PostMapping(path = "/themes/{themeId}/new-message")
    public String newMessage(ModelMap model, Principal principal, @PathVariable Long themeId, @ModelAttribute NewMessage newMessage) throws NotFoundException {
        if (newMessage == null || newMessage.getText() == null || newMessage.getText().isEmpty()) {
            return "redirect:/themes/" + themeId;
        }

        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        User currentUser = userService.getUser(auth.getName());

        Message message = new Message();
        message.setAuthor(currentUser);
        message.setText(newMessage.getText());
        message.setDate(new Date());
        message.setRemoved(false);
        messageService.save(message);

        Theme theme = themeService.getByIdAndRemoved(themeId, false);

        theme.getMessages().add(message);
        themeService.save(theme);

        return "redirect:/themes/" + themeId;
    }


    @PostMapping(path = "/new-theme")
    public String newTheme(ModelMap model, Principal principal, @ModelAttribute NewTheme newTheme)   {
        if (newTheme == null || newTheme.getTitle() == null || newTheme.getTitle().isEmpty()) {
            return "redirect:/themes";
        }

        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        User currentUser = userService.getUser(auth.getName());

        Theme theme = new Theme();
        theme.setTitle(newTheme.getTitle());
        theme.setAuthor(currentUser);
        theme.setDate(new Date());
        theme.setRemoved(false);

        Long themeId = themeService.save(theme);

        return "redirect:/themes/" + themeId;
    }

}
