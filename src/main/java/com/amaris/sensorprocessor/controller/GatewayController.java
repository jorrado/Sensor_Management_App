package com.amaris.sensorprocessor.controller;

import com.amaris.sensorprocessor.constant.FrequencyPlan;
import com.amaris.sensorprocessor.entity.Gateway;
import com.amaris.sensorprocessor.service.GatewayLorawanService;
import com.amaris.sensorprocessor.service.GatewayService;
import com.amaris.sensorprocessor.service.InputValidationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.web.csrf.CsrfToken;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BeanPropertyBindingResult;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

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
    private static final String ERROR_EDIT = "errorEdit";
    private static final String GATEWAY_EDIT = "gatewayEdit";
    private static final String ERROR_DELETE = "errorDelete";

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

    /**
     * Gère les requêtes POST sur /manage-gateways pour éviter l’erreur 405
     * en cas de navigation arrière/avant ou de rafraîchissement.
     * La méthode redirige systématiquement vers la liste des gateways
     * avec un horodatage pour forcer le rafraîchissement et éviter le cache.
     *
     * @param model modèle MVC pour la vue (non utilisé ici mais nécessaire pour la signature)
     * @return redirection vers la liste des gateways avec horodatage
     */
    @PostMapping("/manage-gateways")
    public String handleManageGatewaysPost(Model model) {
        return "redirect:/manage-gateways?_=" + System.currentTimeMillis();
    }

    @PostMapping("/manage-gateways/add")
    public String addGateway(@ModelAttribute(GATEWAY_ADD) Gateway gateway,BindingResult bindingResult, Model model) {
        model.addAttribute(GATEWAY_ADD, gateway);
        inputValidationService.validateGatewayForCreateForm(gateway, bindingResult);
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
        return "redirect:/manage-gateways?_=" + System.currentTimeMillis();
    }

    /**
     * Gère les requêtes GET sur /manage-gateways/add pour éviter l’erreur 405
     * en cas de navigation arrière/avant ou de rafraîchissement.
     * La route attend normalement des requêtes POST pour la soumission du formulaire.
     * Cette méthode redirige systématiquement vers la liste des gateways,
     * empêchant ainsi les erreurs liées aux requêtes GET non gérées.
     *
     * @return redirection vers la liste des gateways avec horodatage pour éviter le cache
     */
    @GetMapping("/manage-gateways/add")
    public String handleAddGet() {
        return "redirect:/manage-gateways?_=" + System.currentTimeMillis();
    }

    @PostMapping("/manage-gateways/delete/{gatewayId}")
    public String deleteGateway(@PathVariable String gatewayId, Model model) {
        BindingResult bindingResult = new BeanPropertyBindingResult(new Gateway(), "deleteGateway");
        gatewayLorawanService.deleteGatewayInLorawan(gatewayId, bindingResult);
        if (bindingResult.getGlobalError() != null) {
            prepareModel(model);
            if ("gatewayId".equals(bindingResult.getGlobalError().getCode())) {
                model.addAttribute(ERROR_DELETE, "Gateway ID not found");
            } else if ("permissionDenied".equals(bindingResult.getGlobalError().getCode())) {
                model.addAttribute(ERROR_DELETE, "Permission user denied");
            } else if ("gatewayProblem".equals(bindingResult.getGlobalError().getCode())) {
                model.addAttribute(ERROR_DELETE, "Problem with this gateway");
            } else {
                model.addAttribute(ERROR_DELETE, "Lorawan server problem");
            }
            return "manageGateways";
        }
        gatewayService.deleteGatewayInDatabase(gatewayId, bindingResult);
        if (bindingResult.getGlobalError() != null) {
            prepareModel(model);
            if ("gatewayId".equals(bindingResult.getGlobalError().getCode())) {
                model.addAttribute(ERROR_DELETE, "Gateway ID not found");
            } else {
                model.addAttribute(ERROR_DELETE, "Database problem");
            }
            return "manageGateways";
        }
        model.addAttribute(ERROR_DELETE, null);
        return "redirect:/manage-gateways?_=" + System.currentTimeMillis();
    }

    @GetMapping("/manage-gateways/edit/{gatewayId}")
    public String editGateway(@PathVariable String gatewayId, Model model) {
        prepareModel(model);
        Gateway gateway;
        try {
            gateway = gatewayService.searchGatewayById(gatewayId);
            model.addAttribute(GATEWAY_EDIT, gateway);
            if (gateway == null) {
                model.addAttribute(ERROR_EDIT, "Gateway don't exists");
            }
        } catch (Exception e) {
            model.addAttribute(ERROR_EDIT, "Database problem");
            model.addAttribute(GATEWAY_EDIT, new Gateway());
        }
        return "manageGateways";
    }

    @PostMapping("/manage-gateways/edit")
    public String updateGateway(@ModelAttribute(GATEWAY_EDIT) Gateway gateway,BindingResult bindingResult, Model model) {
        model.addAttribute(GATEWAY_EDIT, gateway);
        inputValidationService.validateGatewayForUpdateForm(gateway, bindingResult);
        if (bindingResult.hasErrors()) {
            prepareModel(model);
            model.addAttribute("org.springframework.validation.BindingResult.gatewayEdit", bindingResult);
            model.addAttribute(ERROR_EDIT, "Input error in the form");
            return "manageGateways";
        }
        gatewayLorawanService.updateGatewayInLorawan(gateway, bindingResult);
        if (bindingResult.hasErrors()) {
            prepareModel(model);
            if (bindingResult.hasFieldErrors("gatewayId")) {
                model.addAttribute(ERROR_EDIT, "Gateway ID not found");
            } else if (bindingResult.getGlobalError() != null && "permissionDenied".equals(bindingResult.getGlobalError().getCode())) {
                model.addAttribute(ERROR_EDIT, "Permission user denied");
            } else {
                model.addAttribute(ERROR_EDIT, "Lorawan server problem");
            }
            model.addAttribute("org.springframework.validation.BindingResult.gatewayEdit", bindingResult);
            return "manageGateways";
        }
        gatewayService.updateGatewayInDatabase(gateway, bindingResult);
        if (bindingResult.hasErrors()) {
            prepareModel(model);
            if (bindingResult.hasFieldErrors("gatewayId")) {
                model.addAttribute(ERROR_EDIT, "Gateway ID not found");
            } else {
                model.addAttribute(ERROR_EDIT, "Database problem");
            }
            return "manageGateways";
        }
        model.addAttribute(ERROR_EDIT, null);
        return "redirect:/manage-gateways?_=" + System.currentTimeMillis();
    }

    /**
     * Gère les requêtes GET sur /manage-gateways/edit pour éviter l’erreur 405
     * en cas de navigation arrière/avant ou de rafraîchissement.
     * La route attend normalement des requêtes POST pour la soumission du formulaire.
     * Si un GET sans gatewayId est reçu (ex : rafraîchissement ou navigation),
     * la méthode redirige vers la liste des gateways pour éviter l’erreur.
     * Si gatewayId est présent, elle affiche le formulaire d’édition via editGateway().
     *
     * @param gatewayId identifiant optionnel du gateway à éditer
     * @param model modèle MVC pour la vue
     * @return vue à afficher ou redirection vers la liste des gateways avec horodatage pour éviter le cache
     */
    @GetMapping("/manage-gateways/edit")
    public String handleEditGet(@RequestParam(required = false) String gatewayId, Model model) {
        if (gatewayId == null) {
            return "redirect:/manage-gateways?_=" + System.currentTimeMillis();
        }
        return editGateway(gatewayId, model);
    }

    /**
     * Affiche la page de monitoring en injectant l'ID de la gateway et son IP.
     *
     * @param id ID de la gateway à monitorer
     * @param ip Adresse IP de la gateway
     * @param model Modèle pour passer les données à la vue Thymeleaf
     * @return Nom de la vue Thymeleaf "monitoringGateway"
     */
    @GetMapping("/manage-gateways/monitoring/{id}/view")
    public String monitoringView(@PathVariable("id") String id, @RequestParam String ip, Model model) {
        model.addAttribute("gatewayId", id);
        model.addAttribute("ipAddress", ip);
        return "monitoringGateway";
    }

    /**
     * Stream en temps réel les données de monitoring d'une gateway via SSE.
     *
     * @param id ID de la gateway
     * @param ip Adresse IP de la gateway
     * @return SseEmitter pour transmettre les données en continu au client
     */
    @GetMapping("/manage-gateways/monitoring/{id}/stream")
    public SseEmitter streamMonitoringData(@PathVariable("id") String id, @RequestParam("ip") String ip) {
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
