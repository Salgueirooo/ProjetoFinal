package com.example.sistemagestao.dto;

import com.example.sistemagestao.domain.SystemConfig;

public record SystemConfigResponseDTO(Long id, String configKey, String description, String configValue) {
    public SystemConfigResponseDTO (SystemConfig systemConfig) {
        this(
                systemConfig.getId(),
                systemConfig.getConfigKey(),
                systemConfig.getDescription(),
                systemConfig.getConfigValue()
        );
    }
}
