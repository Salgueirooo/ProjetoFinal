package com.example.sistemagestao.services;

import com.example.sistemagestao.domain.SystemConfig;
import com.example.sistemagestao.repositories.SystemConfigRepository;
import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class SystemConfigInitializer {
    @Autowired
    private SystemConfigRepository systemConfigRepository;

    @PostConstruct
    public void init() {
        createIfMissing("MAX_REVIEW_DAYS", "Prazo máximo para submeter uma classificação a um produto", "7");
        createIfMissing("MAX_ORDER_CANCEL_HOURS", "Prazo máximo (em horas) para cancelar uma encomenda","48");
        createIfMissing("MIN_ORDER_HOURS", "Mínima antecipação (em horas) para fazer uma encomenda", "24");
        createIfMissing("CHECK_STOCK_DAYS", "Intervalo de verificação de stock (em dias)", "2");
        createIfMissing("OPENING_TIME", "Horário de abertura", "08:00");
        createIfMissing("CLOSING_TIME", "Horário de fecho", "20:00");
    }

    private void createIfMissing(String key, String description, String value) {
        systemConfigRepository.findByConfigKey(key)
                .orElseGet(() -> systemConfigRepository.save(
                        new SystemConfig(key, description, value)
                ));
    }
}
