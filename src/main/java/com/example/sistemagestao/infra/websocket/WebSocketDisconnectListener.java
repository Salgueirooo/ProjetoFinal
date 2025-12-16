package com.example.sistemagestao.infra.websocket;

import lombok.NonNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationListener;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.messaging.SessionDisconnectEvent;

@Component
public class WebSocketDisconnectListener implements ApplicationListener<SessionDisconnectEvent> {

    @Autowired
    private WebSocketSessionTracker tracker;

    @Override
    public void onApplicationEvent(@NonNull SessionDisconnectEvent event) {
        tracker.removeSession(event);
    }
}
