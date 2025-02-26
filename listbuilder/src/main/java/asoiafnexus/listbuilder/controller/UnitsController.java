package asoiafnexus.listbuilder.controller;

import asoiafnexus.listbuilder.ResourceLoader;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.CacheControl;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.Instant;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.TimeUnit;

@RestController
@RequestMapping("/api/v1/listbuilder")
public class UnitsController {

    @Autowired
    ResourceLoader resourceLoader;
    
    @GetMapping("/data")
    List<?> getFactionData(@RequestParam(required = false) List<String> factions) {
        if(Objects.isNull(factions)) {
            return resourceLoader.loadFiles();
        } else {
            return resourceLoader.loadFiles(factions);
        }
    }

    @GetMapping("/units")
    List<?> getAllUnits() {
        return resourceLoader.getAllUnits();
    }

    @GetMapping("/ncus")
    List<?> getAllNCUS() { return resourceLoader.getAllNCUs(); }

    @GetMapping("/attachments")
    List<?> getAllAttachments() { return resourceLoader.getAllAttachments(); }

    @GetMapping("cards/{id}")
    ResponseEntity<?> getCard(@PathVariable String id) {
        var bytes = resourceLoader.getImage(id);
        if(Objects.isNull(bytes)) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok()
                .contentType(MediaType.IMAGE_JPEG)
                .cacheControl(CacheControl.maxAge(1, TimeUnit.DAYS))
                .body(bytes);
    }
}
