package com.ruoyi.tom.service;

import com.ruoyi.tom.entity.Message;
import java.util.List;

public interface MessageService {
    Message saveMessage(Message message);
    List<Message> getAllMessages();
}