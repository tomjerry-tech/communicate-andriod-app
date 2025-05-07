package com.ruoyi.tom.controller;

import com.ruoyi.tom.entity.Message;
import com.ruoyi.tom.model.ApiResponse;
import com.ruoyi.tom.service.MessageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/messages")
public class MessageController {

    @Autowired
    private MessageService messageService;

    @GetMapping
    public ApiResponse<List<Message>> getAllMessages() {
        List<Message> messages = messageService.getAllMessages();
        return ApiResponse.success(messages);
    }
}