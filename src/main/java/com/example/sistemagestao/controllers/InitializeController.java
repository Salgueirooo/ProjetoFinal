package com.example.sistemagestao.controllers;

import com.example.sistemagestao.domain.MeasurentUnits;
import com.example.sistemagestao.dto.UnitDTO;
import com.example.sistemagestao.dto.WSMessageDTO;
import com.example.sistemagestao.services.NotificationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.Arrays;
import java.util.List;

@RestController
@RequestMapping("api/initialize")
public class InitializeController {

    @Autowired
    private SimpMessagingTemplate messagingTemplate;
    @Autowired
    private NotificationService notificationService;

    @GetMapping
    public String initiallize(){
        return "Conectado ao Servidor.";
    }

    @GetMapping("/get-measurent-units")
    public List<UnitDTO> getMeasureUnits(){
        return Arrays.stream(MeasurentUnits.values())
                .map(u -> new UnitDTO(u.getSymbol(), u.getDescription()))
                .toList();
    }

    @GetMapping("/notify")
    public String sendNotification(@RequestParam String message) {
        messagingTemplate.convertAndSendToUser(
                "ro@gmail.com",
                "/queue/notifications",
                new WSMessageDTO(message, null, null, null)
        );
        return "Mensagem enviada!";
    }

    @GetMapping("/notify-role")
    public String sendNotificationToRole(@RequestParam String message) {
        notificationService.sendToRole("ROLE_ADMIN", message, null, null, null);
        return "Mensagem enviada!";
    }

    @GetMapping("/notify-all")
    public String sendNotificationToAll(@RequestParam String message) {
        notificationService.sendAll(message);
        return "Mensagem enviada!";
    }
}
