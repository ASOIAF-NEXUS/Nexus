package asoiafnexus.listbuilder.controller;

import asoiafnexus.listbuilder.Units;
import asoiafnexus.listbuilder.validation.StarkFactionValidation;
import org.springframework.web.bind.annotation.*;

import java.util.Collections;
import java.util.List;

@RestController
@RequestMapping("/api/v1/listbuilder")
public class ListBuilderController {

    @GetMapping("/units")
    List<Units.Unit> getAllUnits() {
        return Units.allUnits();
    }

    @GetMapping("/factions/starks/validators")
    StarkFactionValidation getStarkValidators() { return new StarkFactionValidation(); }

    @PostMapping("/validate")
    List<String> validateArmyList(@RequestBody ArmyListPayload payload) {
        return Collections.emptyList();
    }
}
