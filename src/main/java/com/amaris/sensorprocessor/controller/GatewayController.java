package com.amaris.sensorprocessor.controller;

import com.amaris.sensorprocessor.entity.Gateway;
import com.amaris.sensorprocessor.exception.CustomException;
import com.amaris.sensorprocessor.service.GatewayService;
import com.amaris.sensorprocessor.service.InputValidationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.io.IOException;
import java.util.List;

@Controller
public class GatewayController {

    private final GatewayService gatewayService;
    private final InputValidationService inputValidationService;

    @Autowired
    public GatewayController(GatewayService gatewayService, InputValidationService inputValidationService) {
        this.gatewayService = gatewayService;
        this.inputValidationService = inputValidationService;
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
            inputValidationService.validateGateway(gateway);
        } catch (CustomException e) {
            redirectAttributes.addFlashAttribute("error", e.getMessage());
            return "redirect:/manage-gateways";
        }
        try {
            gatewayService.save(gateway);
        } catch (CustomException e) {
            redirectAttributes.addFlashAttribute("error", e.getMessage());
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
        try {
            inputValidationService.validateGateway(gateway);
        } catch (CustomException e) {
            redirectAttributes.addFlashAttribute("error", e.getMessage());
            return "redirect:/manage-gateways";
        }
        gatewayService.update(gateway);
        return "redirect:/manage-gateways";
    }

    @GetMapping("/manage-gateways/monitoring/{id}/view")
    public String monitoringView(@PathVariable String id, Model model) {
        model.addAttribute("gatewayId", id);
        return "monitoringGateway";
    }

    /**
     * Établit un flux SSE (Server-Sent Events) pour envoyer les données de monitoring
     * en temps réel d’un gateway identifié par son ID et son adresse IP.
     * Cette méthode est appelée automatiquement depuis le JavaScript de la page HTML,
     * via une requête SSE vers l’endpoint.
     *
     * @param id l’identifiant du gateway
     * @param ip l’adresse IP du gateway
     * @return un {@link SseEmitter} qui envoie les données de monitoring en continu
     */
    @GetMapping("/manage-gateways/monitoring/{id}/stream")
    public SseEmitter streamMonitoringData(@PathVariable String id, @RequestParam String ip) {
        SseEmitter emitter = new SseEmitter(0L);
        gatewayService.getMonitoringData(id, ip)
                .subscribe(data -> {
                    try {
                        emitter.send(data);
                    } catch (IOException e) {
                        emitter.completeWithError(e);
                    }
                }, emitter::completeWithError, emitter::complete);
        return emitter;
    }

}
