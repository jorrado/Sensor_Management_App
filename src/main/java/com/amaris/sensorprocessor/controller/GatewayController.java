package com.amaris.sensorprocessor.controller;

import com.amaris.sensorprocessor.entity.Gateway;
import com.amaris.sensorprocessor.exception.CustomException;
import com.amaris.sensorprocessor.service.GatewayService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;

@Controller
public class GatewayController {

    private final GatewayService gatewayService;

    @Autowired
    public GatewayController(GatewayService gatewayService) {
        this.gatewayService = gatewayService;
    }

    @GetMapping("/manage-gateways")
    public String manageGateways(Model model) {
        List<Gateway> gateways = gatewayService.getAllGateways();
        model.addAttribute("gateways", gateways);
        return "manageGateways";
    }

    @PostMapping("/manage-gateways/add")
    public String addGateway(@ModelAttribute Gateway gateway, RedirectAttributes redirectAttributes) {
        try {
            gatewayService.save(gateway);
        } catch (CustomException e) {
            redirectAttributes.addFlashAttribute("error", "Gateway already exists!");
            return "redirect:/manage-gateways";
        }
        redirectAttributes.addFlashAttribute("error", null);
        return "redirect:/manage-gateways";
    }

    @PostMapping("/manage-gateways/delete/{idGateway}")
    public String deleteGateway(@PathVariable String idGateway) {
        gatewayService.deleteGateway(idGateway);
        return "redirect:/manage-gateways";
    }

    @GetMapping("/manage-gateways/edit/{idGateway}")
    public String editGateway(@PathVariable String idGateway, Model model) {
        List<Gateway> gateways = gatewayService.getAllGateways();
        model.addAttribute("gateways", gateways);
        Gateway gateway = gatewayService.searchGatewayById(idGateway);
        model.addAttribute("gateway", gateway);
        return "manageGateways";
    }

    @PostMapping("/manage-gateways/edit")
    public String updateGateway(@ModelAttribute Gateway gateway, RedirectAttributes redirectAttributes) {
        gatewayService.update(gateway);
        return "redirect:/manage-gateways";
    }

    @GetMapping("/manage-gateways/monitoring/{id}")
    public String monitoringReading(@PathVariable String id, Model model) {
        // MonitoringData monitoringData = gatewayService.getMonitoringData(id);
        Object monitoringData = gatewayService.getMonitoringData(id);
        model.addAttribute("monitoringData", monitoringData);
        return "monitoringGateway";
    }

}
