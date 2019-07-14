package ru.pon.demo.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.pon.demo.entity.Message;
import ru.pon.demo.repository.MessageRepository;

@Service
public class MessageService {
    private final MessageRepository messageRepository;

    public MessageService(MessageRepository messageRepository) {
        this.messageRepository = messageRepository;
    }

    public Message getByIdAndRemoved(Long id, Boolean isRemoved) {
        return messageRepository.findByIdAndRemoved(id, isRemoved);
    }

    public void deleteMessage(Long id) {
        Message message = messageRepository.getOne(id);
        message.setRemoved(true);
        messageRepository.saveAndFlush(message);
    }

    public Long save(Message message) {
        message = messageRepository.saveAndFlush(message);
        return message.getId();
    }
}
