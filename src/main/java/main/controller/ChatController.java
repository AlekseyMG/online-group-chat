package main.controller;

import main.dto.DtoMessage;
import main.dto.MessageMapper;
import main.model.Message;
import main.model.User;
import main.repository.MessageRepository;
import main.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.context.request.RequestContextHolder;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

@RestController
public class ChatController {
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private MessageRepository messageRepository;
    private MessageMapper messageMapper;
    @GetMapping("/init")
    public ConcurrentHashMap<String, Boolean> init() {
        ConcurrentHashMap<String, Boolean> response = new ConcurrentHashMap<>();
        String sessionId = RequestContextHolder.getRequestAttributes().getSessionId();
        Optional<User> optionalUser = userRepository.findBySessionId(sessionId);
        response.put("result", optionalUser.isPresent());
        return response;
    }
    @PostMapping("/auth")
    public ConcurrentHashMap<String, Boolean> auth(@RequestParam String name) {
        ConcurrentHashMap<String, Boolean> response = new ConcurrentHashMap<>();
        if(name.isEmpty()) {
            response.put("result", false);
            return response;
        }
        String sessionId = RequestContextHolder.getRequestAttributes().getSessionId();
        User user = new User();
        user.setName(name);
        user.setSessionId(sessionId);
        userRepository.save(user);
        response.put("result", true);
        return response;
    }
    @PostMapping("/message")
    public ConcurrentHashMap<String, Boolean> sendMessage(@RequestParam String message) {
        ConcurrentHashMap<String, Boolean> response = new ConcurrentHashMap<>();
        if(message.isEmpty()) {
            response.put("result", false);
            return response;
        }
        String sessionId = RequestContextHolder.getRequestAttributes().getSessionId();
        User user = userRepository.findBySessionId(sessionId).get();
        Message userMessage = new Message();
        userMessage.setMessage(message);
        userMessage.setUser(user);
        userMessage.setDateTime(LocalDateTime.now());
        messageRepository.save(userMessage);
        response.put("result", true);
        return response;
    }
    @GetMapping("/message")
    public List<DtoMessage> getMessagesList() {
        return messageRepository
                .findAll(Sort.by(Sort.Direction.ASC, "dateTime"))
                .stream()
                .map(message -> MessageMapper.map(message))
                .collect(Collectors.toList());
    }

    @GetMapping("/user")
    public List<String> getUserList() {
        return userRepository.findAll(Sort.by(Sort.Direction.ASC, "name"))
                .stream().map(User::getName)
                .toList();
    }
}
