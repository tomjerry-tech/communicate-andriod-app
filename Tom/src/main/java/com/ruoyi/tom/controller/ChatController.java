package com.ruoyi.tom.controller;

import com.ruoyi.tom.entity.Message;
import com.ruoyi.tom.entity.User;
import com.ruoyi.tom.model.ChatMessage;
import com.ruoyi.tom.repository.MessageRepository;
import com.ruoyi.tom.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.stereotype.Controller;

import java.text.SimpleDateFormat;
import java.util.Date;

@Controller
public class ChatController {

    @Autowired
    private MessageRepository messageRepository;
    
    @Autowired
    private UserRepository userRepository;

    @MessageMapping("/chat.sendMessage")
    @SendTo("/topic/public")
    public ChatMessage sendMessage(@Payload ChatMessage chatMessage) {
        // 设置消息发送时间
        chatMessage.setTimestamp(new SimpleDateFormat("HH:mm:ss").format(new Date()));
        
        // 保存消息到数据库
        try {
            User sender = userRepository.findByUsername(chatMessage.getSender());
            if (sender != null) {
                Message message = new Message();
                message.setContent(chatMessage.getContent());
                message.setSender(sender);
                message.setCreateTime(new Date());
                messageRepository.save(message);
            }
        } catch (Exception e) {
            System.err.println("保存消息失败: " + e.getMessage());
        }
        
        return chatMessage;
    }

    @MessageMapping("/chat.addUser")
    @SendTo("/topic/public")
    public ChatMessage addUser(@Payload ChatMessage chatMessage) {
        // 设置消息类型为JOIN
        chatMessage.setType(ChatMessage.MessageType.JOIN);
        chatMessage.setTimestamp(new SimpleDateFormat("HH:mm:ss").format(new Date()));
        chatMessage.setContent(chatMessage.getSender() + " 加入了聊天室!");
        return chatMessage;
    }
}