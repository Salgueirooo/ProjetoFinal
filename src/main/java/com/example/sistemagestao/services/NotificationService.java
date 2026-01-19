package com.example.sistemagestao.services;

import com.example.sistemagestao.domain.Bakery;
import com.example.sistemagestao.dto.WSMessageDTO;
import com.example.sistemagestao.infra.websocket.WebSocketSessionTracker;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class NotificationService {

    @Autowired
    private SimpMessagingTemplate messagingTemplate;

    @Autowired
    private WebSocketSessionTracker webSocketSessionTracker;

    public void sendToUser(String email, String message, String hyperlink, Bakery bakery, List<String> path) {
        messagingTemplate.convertAndSendToUser(
                email,
                "/queue/notifications",
                new WSMessageDTO(message, bakery, path,  hyperlink)
        );
    }

    public void sendToRole(String role, String message, String hyperlink, Bakery bakery, List<String> path) {
        var users = webSocketSessionTracker.getConnectedUsersByRole(role);

        for (String username : users) {
            messagingTemplate.convertAndSendToUser(
                    username,
                    "/queue/notifications",
                    new WSMessageDTO(message, bakery, path, hyperlink)
            );
        }
    }

    public void sendAll (String message) {
        messagingTemplate.convertAndSend("/topic/news", new WSMessageDTO(message, null, null, null));
    }

    @Getter
    @RequiredArgsConstructor
    public enum FrontendPath {

        Products("products"),
        SearchProducts("search-products"),
        InCart("in-cart"),
        Accompany("accompany"),
        SearchMyOrders("search-my-orders"),
        ReadyOrders("ready-orders"),
        ConfirmedOrders("confirmed-orders"),
        PendentOrders("pendent-orders"),
        SearchAllOrders("search-all-orders"),
        SearchRecipes("search-recipes"),
        StartedRecipes("started-recipes"),
        TaskListRecipes("recipes-to-do"),
        HistoryRecipes("history-recipes"),
        ManageProductStock("manage-products-stock"),
        ManageIngredientStock("manage-ingredients-stock"),
        VerifyStock("verify-stock"),
        SalesStats("sales-stats"),
        RevenueStats("revenue-stats"),
        UserStats("user-stats");


        private final String path;
    }
}



