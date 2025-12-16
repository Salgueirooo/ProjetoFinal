package com.example.sistemagestao.infra.websocket;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationListener;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.messaging.SessionConnectEvent;
import org.springframework.web.socket.messaging.SessionDisconnectEvent;

import java.security.Principal;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;

@Component
public class WebSocketSessionTracker {
    private final Map<String, UserSessionInfo> sessions = new ConcurrentHashMap<>();

    public void registerSession(SessionConnectEvent event) {
        StompHeaderAccessor accessor = StompHeaderAccessor.wrap(event.getMessage());

        Principal user = accessor.getUser();
        if (user == null) {
            return;
        }

        String username = user.getName();

        List<String> roles = (user instanceof Authentication auth)
                ? auth.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .toList()
                : List.of();

        sessions.put(accessor.getSessionId(), new UserSessionInfo(username, roles));
    }

    public void removeSession(SessionDisconnectEvent event) {
        sessions.remove(event.getSessionId());
    }

    public List<String> getConnectedUsersByRole(String role) {
        return sessions.values().stream()
                .filter(s -> s.roles().contains(role))
                .map(UserSessionInfo::username)
                .toList();
    }
}

record UserSessionInfo(String username, List<String> roles) {}
