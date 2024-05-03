package com.uni.pe.storyhub.controller;

import com.uni.pe.storyhub.model.Blog;
import com.uni.pe.storyhub.model.BlogDto;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;

@Controller
public class WebSocketController {

    @Autowired
    private SimpMessagingTemplate messagingTemplate;

    @MessageMapping("/update-blogs")
    @SendTo("/topic/blogs")
    public String sendUpdate(String message) {
        return message; // Puedes enviar información adicional aquí si es necesario
    }
}
