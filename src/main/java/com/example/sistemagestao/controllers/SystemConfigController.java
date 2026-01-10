package com.example.sistemagestao.controllers;

import com.example.sistemagestao.dto.SystemConfigResponseDTO;
import com.example.sistemagestao.dto.VarsMakeOrderDTO;
import com.example.sistemagestao.services.SystemConfigService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("api/system-config")
public class SystemConfigController {

    @Autowired
    private SystemConfigService systemConfigService;

    @PutMapping("/update/{id}")
    public void updateSystemConfig(@PathVariable Long id, @RequestBody String newValue){
        systemConfigService.updateConfig(id, newValue);
    }

    @GetMapping("/all")
    public List<SystemConfigResponseDTO> getAllSystemConfig(){
        return systemConfigService.findAll();
    }

    @GetMapping("/get")
    public String getSystemConfig(@RequestParam String key){
        return systemConfigService.getVar(key);
    }

    @GetMapping("/get-make-order")
    public VarsMakeOrderDTO getMakeOrder(){
        return systemConfigService.findMakeOrderVars();
    }
}
