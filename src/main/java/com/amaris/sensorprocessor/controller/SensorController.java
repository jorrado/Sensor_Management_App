package com.amaris.sensorprocessor.controller;

import com.amaris.sensorprocessor.constant.Constants;
import com.amaris.sensorprocessor.entity.Sensor;
import com.amaris.sensorprocessor.service.SensorService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;

import java.util.List;

@Controller
public class SensorController {

    private static final String SENSOR_ADD = "sensorAdd";

    private final SensorService sensorService;

    @Autowired
    public SensorController(SensorService sensorService) {
        this.sensorService = sensorService;
    }

    /** Liste des capteurs */
    @GetMapping("/manage-sensors")
    public String manageSensors(Model model) {
        prepareModel(model);
        return Constants.PAGE_MANAGE_SENSORS; // ex: "manageSensors"
    }

    /** Fallback POST -> redirection (évite 405 en cas de refresh/back) */
    @PostMapping("/manage-sensors")
    public String handleManageSensorsPost(Model model) {
        return redirectWithTimestamp();
    }

    /* ====================== PRIVÉS ====================== */

    private void prepareModel(Model model) {
        List<Sensor> sensors = sensorService.getAllSensors();
        model.addAttribute("sensors", sensors);

        if (!model.containsAttribute(SENSOR_ADD)) {
            model.addAttribute(SENSOR_ADD, new Sensor());
        }
    }

    private String redirectWithTimestamp() {
        return "redirect:/manage-sensors?_=" + System.currentTimeMillis();
    }
}
