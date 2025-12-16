package com.example.sistemagestao.infra.websocket;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationListener;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.messaging.SessionConnectEvent;

@Component
public class WebSocketConnectListener implements ApplicationListener<SessionConnectEvent> {

    @Autowired
    private WebSocketSessionTracker tracker;

    @Override
    public void onApplicationEvent(SessionConnectEvent event) {
        tracker.registerSession(event);
    }
}
