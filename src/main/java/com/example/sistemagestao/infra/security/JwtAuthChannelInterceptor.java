package com.example.sistemagestao.infra.security;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.simp.stomp.StompCommand;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.messaging.support.ChannelInterceptor;
import org.springframework.messaging.support.MessageHeaderAccessor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;
import org.springframework.security.core.authority.SimpleGrantedAuthority;

import java.util.List;

@Component
public class JwtAuthChannelInterceptor implements ChannelInterceptor {

    @Autowired
    private TokenService tokenService;

    @Override
    public Message<?> preSend(Message<?> message, MessageChannel channel) {

        StompHeaderAccessor accessor =
                MessageHeaderAccessor.getAccessor(message, StompHeaderAccessor.class);

        if (StompCommand.CONNECT.equals(accessor.getCommand())) {
            String token = accessor.getFirstNativeHeader("Authorization");

            if (token != null && token.startsWith("Bearer ")) {
                token = token.substring(7);
                String email = tokenService.validateToken(token);

                if (email != null && !email.isBlank()) {

                    List<String> roles = tokenService.extractRoles(token);

                    Authentication auth =
                            new UsernamePasswordAuthenticationToken(
                                    email,
                                    null,
                                    roles.stream()
                                            .map(SimpleGrantedAuthority::new)
                                            .toList()
                            );

                    accessor.setUser(auth);
                }
            }
        }

        return message;
    }
}