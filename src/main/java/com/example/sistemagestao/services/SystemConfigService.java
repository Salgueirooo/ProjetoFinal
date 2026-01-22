package com.example.sistemagestao.services;

import com.example.sistemagestao.domain.SystemConfig;
import com.example.sistemagestao.dto.SystemConfigResponseDTO;
import com.example.sistemagestao.dto.VarsMakeOrderDTO;
import com.example.sistemagestao.repositories.SystemConfigRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class SystemConfigService {

    @Autowired
    private SystemConfigRepository systemConfigRepository;

    public int getInt(String configKey, int defaultValue) {
        return systemConfigRepository.findByConfigKey(configKey)
                .map(c -> Integer.parseInt(c.getConfigValue()))
                .orElse(defaultValue);
    }

    public String getVar(String configKey) {
        return systemConfigRepository.findByConfigKey(configKey)
                .map(SystemConfig::getConfigValue)
                .orElseThrow(() ->  new EntityNotFoundException("Não foi encontrada nenhuma variável '" + configKey + "'."));
    }

    public void updateConfig(Long id, String configValue) {
        SystemConfig systemConfig = systemConfigRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Configuração não encontrada."));

        if (systemConfig.getConfigKey().contains("TIME")) {
            systemConfig.setConfigValue(configValue.replace("\"", ""));
        } else {
            systemConfig.setConfigValue(configValue);
        }

        systemConfigRepository.save(systemConfig);
    }

    public List<SystemConfigResponseDTO> findAll() {
        return systemConfigRepository.findAllByOrderByConfigKeyAsc()
                .stream()
                .map(SystemConfigResponseDTO::new)
                .toList();
    }

    public SystemConfig get(String configKey) {
        return systemConfigRepository.findByConfigKey(configKey)
                .orElseThrow(() ->  new EntityNotFoundException("Não foi encontrada nenhuma variável '" + configKey + "'."));
    }

    public VarsMakeOrderDTO findMakeOrderVars() {
        return new VarsMakeOrderDTO(
                get("MIN_ORDER_HOURS"),
                get("OPENING_TIME"),
                get("CLOSING_TIME")

        );
    }
}
