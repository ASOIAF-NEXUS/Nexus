package asoiafnexus.listbuilder.controller;

import asoiafnexus.listbuilder.Units;
import asoiafnexus.listbuilder.model.Unit;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/listbuilder")
public class ListBuilderController {

    private static final Logger log = LoggerFactory.getLogger(ListBuilderController.class);

    @Autowired
    private Units units;

    @GetMapping("/units")
    List<Unit> getAllUnits() {
        return units.allUnits();
    }

    @PostMapping("/validate")
    ResponseEntity<?> validateArmyList(@RequestBody ArmyListPayload payload) {
        var unitLookup = units.unitLookup();
        var invalidIds = payload.invalidIds(unitLookup);
        if(!invalidIds.isEmpty()) {
            return ResponseEntity
                    .status(HttpStatus.BAD_REQUEST)
                    .body(String.format("Unrecognized unit ids %s", invalidIds));
        }
        // algorithm sketch
        // take payload and map it based on unit balance data
        var armyList = payload.toArmyList(unitLookup);
        log.info(armyList.toString());
        // run validators against the enriched army list

        // return any validation errors collected
        return ResponseEntity.ok().build();
    }
}
