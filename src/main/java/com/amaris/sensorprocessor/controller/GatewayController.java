package com.amaris.sensorprocessor.controller;

import com.amaris.sensorprocessor.constant.FrequencyPlan;
import com.amaris.sensorprocessor.entity.Gateway;
import com.amaris.sensorprocessor.exception.CustomException;
import com.amaris.sensorprocessor.service.GatewayLorawanService;
import com.amaris.sensorprocessor.service.GatewayService;
import com.amaris.sensorprocessor.service.InputValidationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.web.csrf.CsrfToken;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.io.IOException;
import java.util.List;
import java.util.Map;

@Controller
public class GatewayController {

    private final GatewayService gatewayService;
    private final InputValidationService inputValidationService;
    private final GatewayLorawanService gatewayLorawanService;

    private static final String ERROR_ADD = "errorAdd";
    private static final String GATEWAY_ADD = "gatewayAdd";

    @Autowired
    public GatewayController(GatewayService gatewayService,
                             InputValidationService inputValidationService,
                             GatewayLorawanService gatewayLorawanService) {
        this.gatewayService = gatewayService;
        this.inputValidationService = inputValidationService;
        this.gatewayLorawanService = gatewayLorawanService;
    }

    @GetMapping("/manage-gateways")
    public String manageGateways(Model model) {
        prepareModel(model);
        return "manageGateways";
    }

    @PostMapping("/manage-gateways/add")
    public String addGateway(@ModelAttribute(GATEWAY_ADD) Gateway gateway,BindingResult bindingResult, Model model) {
        model.addAttribute(GATEWAY_ADD, gateway);
        inputValidationService.validateGateway(gateway, bindingResult);
        if (bindingResult.hasErrors()) {
            prepareModel(model);
            model.addAttribute("org.springframework.validation.BindingResult.gatewayAdd", bindingResult);
            model.addAttribute(ERROR_ADD, "Input error in the form");
            return "manageGateways";
        }
        gatewayLorawanService.saveGatewayInLorawan(gateway, bindingResult);
        if (bindingResult.hasErrors()) {
            prepareModel(model);
            if (bindingResult.hasFieldErrors("gatewayId")) {
                model.addAttribute(ERROR_ADD, "Gateway ID already exists");
            } else if (bindingResult.hasFieldErrors("gatewayEui")) {
                model.addAttribute(ERROR_ADD, "Gateway EUI already exists");
            } else {
                model.addAttribute(ERROR_ADD, "Lorawan server problem");
            }
            model.addAttribute("org.springframework.validation.BindingResult.gatewayAdd", bindingResult);
            return "manageGateways";
        }
        gatewayService.saveGatewayInDatabase(gateway, bindingResult);
        if (bindingResult.hasErrors()) {
            prepareModel(model);
            if (bindingResult.hasFieldErrors("gatewayId")) {
                model.addAttribute(ERROR_ADD, "Gateway ID already exists");
            } else {
                model.addAttribute(ERROR_ADD, "Database problem");
            }
            return "manageGateways";
        }
        model.addAttribute(ERROR_ADD, null);
        return "redirect:/manage-gateways";
    }

    @PostMapping("/manage-gateways/delete/{gatewayId}")
    public String deleteGateway(@PathVariable String gatewayId, RedirectAttributes redirectAttributes) {
        try {
            gatewayLorawanService.deleteGatewayInLorawan(gatewayId);
            gatewayService.deleteGatewayInDatabase(gatewayId);
        } catch (CustomException e) {
            redirectAttributes.addFlashAttribute("error", e.getMessage());
            return "redirect:/manage-gateways";
        }
        redirectAttributes.addFlashAttribute("error",null);
        return "redirect:/manage-gateways";
    }

    @GetMapping("/manage-gateways/edit/{gatewayId}")
    public String editGateway(@PathVariable String gatewayId, Model model) {
        prepareModel(model);
        Gateway gateway = gatewayService.searchGatewayById(gatewayId);
        model.addAttribute("gateway", gateway);
        return "manageGateways";
    }

    @PostMapping("/manage-gateways/edit")
    public String updateGateway(@ModelAttribute Gateway gateway, RedirectAttributes redirectAttributes) {
        try {
//            inputValidationService.validateGateway(gateway);
        } catch (CustomException e) {
            redirectAttributes.addFlashAttribute("error", e.getMessage());
            return "redirect:/manage-gateways";
        }
        gatewayLorawanService.updateGatewayInLorawan(gateway);
        gatewayService.updateGatewayInDatabase(gateway);
        return "redirect:/manage-gateways";
    }

    @GetMapping("/manage-gateways/monitoring/{id}/view")
    public String monitoringView(@PathVariable String gatewayId, Model model) {
        model.addAttribute("gatewayId", gatewayId);
        return "monitoringGateway";
    }

    /**
     * Établit un flux SSE (Server-Sent Events) pour envoyer les données de monitoring
     * en temps réel d’un gateway identifié par son ID et son adresse IP.
     * Cette méthode est appelée automatiquement depuis le JavaScript de la page HTML,
     * via une requête SSE vers l’endpoint.
     *
     * @param gatewayId l’identifiant du gateway
     * @param ipAddress l’adresse IP du gateway
     * @return un {@link SseEmitter} qui envoie les données de monitoring en continu
     */
    @GetMapping("/manage-gateways/monitoring/{id}/stream")
    public SseEmitter streamMonitoringData(@PathVariable String gatewayId, @RequestParam String ipAddress) {
        SseEmitter emitter = new SseEmitter(0L);
        gatewayService.getMonitoringData(gatewayId, ipAddress)
            .subscribe(data -> {
                try {
                    emitter.send(data);
                } catch (IOException e) {
                    emitter.completeWithError(e);
                }
            }, emitter::completeWithError, emitter::complete);
        return emitter;
    }

    @GetMapping("/csrf-token")
    @ResponseBody
    public Map<String, String> csrfToken(CsrfToken token) {
        return Map.of(
                "parameterName", token.getParameterName(),
                "token", token.getToken()
        );
    }

    private void prepareModel(Model model) {
        model.addAttribute("frequencyPlans", FrequencyPlan.values());
        List<Gateway> gateways = gatewayService.getAllGateways();
        model.addAttribute("gateways", gateways);
        if (!model.containsAttribute(GATEWAY_ADD)) {
            model.addAttribute(GATEWAY_ADD, new Gateway());
        }
    }

}
