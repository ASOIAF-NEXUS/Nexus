package asoiafnexus.listbuilder.controller;

import asoiafnexus.listbuilder.Units;
import asoiafnexus.listbuilder.validation.StarkFactionValidation;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/v1")
public class ListBuilderController {

    @GetMapping("/units")
    List<Units.Unit> getAllUnits() {
        return Units.allUnits();
    }

    @GetMapping("/factions/starks/validators")
    StarkFactionValidation getStarkValidators() { return new StarkFactionValidation(); }
}
