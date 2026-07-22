package ProyectoEstudiaYa.webapp.controller;

import ProyectoEstudiaYa.webapp.dto.PlanEstudioRequestDTO;
import ProyectoEstudiaYa.webapp.dto.PlanEstudioResponseDTO;
import ProyectoEstudiaYa.webapp.services.PlanEstudioService;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/plan-estudio")
public class PlanEstudioController {

    private final PlanEstudioService planEstudioService;

    public PlanEstudioController(PlanEstudioService planEstudioService) {
        this.planEstudioService = planEstudioService;
    }

    @PostMapping("/generar")
    public PlanEstudioResponseDTO generarPlan(@RequestBody PlanEstudioRequestDTO request) {
        return planEstudioService.generarPlan(request);
    }
}
