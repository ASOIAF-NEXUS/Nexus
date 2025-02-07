package asoiafnexus.listbuilder.controller;

import asoiafnexus.listbuilder.Units;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/v1")
public class UnitsController {

    @GetMapping("/units")
    List<Units.Unit> getAllUnits() {
        return Units.allUnits();
    }
}
