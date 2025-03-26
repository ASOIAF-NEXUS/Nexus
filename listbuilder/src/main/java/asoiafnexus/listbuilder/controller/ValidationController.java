package asoiafnexus.listbuilder.controller;

import asoiafnexus.listbuilder.ResourceLoader;
import asoiafnexus.listbuilder.model.Attachment;
import asoiafnexus.listbuilder.model.NCU;
import asoiafnexus.listbuilder.model.Unit;
import asoiafnexus.listbuilder.model.ValidatorDefinition;
import asoiafnexus.listbuilder.model.ValidatorRule;
import java.util.List;
import java.util.stream.Collectors;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/validation")
public class ValidationController {

    @Autowired
    private ResourceLoader resourceLoader;

    @PostMapping("/validate")
    public ValidationResponse validate(@RequestBody ValidationRequest request) {
        // Load full objects based on IDs
        System.out.println("Got request: " + request);
        List<Unit> units = request.getUnits().stream()
                .map(unitId -> resourceLoader.getAllUnits().stream()
                        .filter(unit -> unit.id().equals(unitId.id()))
                        .findFirst()
                        .orElse(null)) // Handle not found case appropriately
                .filter(java.util.Objects::nonNull)
                .collect(Collectors.toList());

        List<NCU> ncus = request.getNcus().stream()
                .map(ncuId -> resourceLoader.getAllNCUs().stream()
                        .filter(ncu -> ncu.id().equals(ncuId.id()))
                        .findFirst()
                        .orElse(null)) // Handle not found case appropriately
                .filter(java.util.Objects::nonNull)
                .collect(Collectors.toList());

        List<Attachment> attachments = request.getAttachments().stream()
                .map(attachmentId -> resourceLoader.getAllAttachments().stream()
                        .filter(attachment -> attachment.id().equals(attachmentId.id()))
                        .findFirst()
                        .orElse(null)) // Handle not found case appropriately
                .filter(java.util.Objects::nonNull)
                .collect(Collectors.toList());

        ValidatorRule rule = resourceLoader.getValidatorRules().stream()
                .filter(validatorRule -> validatorRule.getId().equals(request.getRules()))
                .findFirst()
                .orElse(null);

        ValidatorDefinition validatorDefinition = null;
        if (rule != null) {
            validatorDefinition = resourceLoader.getValidators().stream()
                    .filter(validatorDefinition1 -> validatorDefinition1.id().equals(rule.getValidator()))
                    .findFirst()
                    .orElse(null);
        }

        
        // TODO: Implement validation logic

        

        // Create ValidationResponse
        ValidationResponse response = new ValidationResponse();
        response.setSeason(request.getSeason());
        response.setRules(request.getRules());
        response.setUnits(request.getUnits().stream().map(UnitId::id).collect(Collectors.toList()));
        response.setNcus(request.getNcus().stream().map(NCUId::id).collect(Collectors.toList()));
        response.setAttachments(request.getAttachments().stream().map(AttachmentId::id).collect(Collectors.toList()));
        response.setValid("true");

        System.out.println("Received validation request: " + request);
        System.out.println("Units: " + units);
        System.out.println("NCUs: " + ncus);
        System.out.println("Attachments: " + attachments);
        System.out.println("Rule: " + rule);
        System.out.println("ValidatorDefinition: " + validatorDefinition);
        System.out.println("Response: " + response);

        return response;
    }

    @GetMapping("/rules")
    public List<ValidatorRule> listValidatorRules() {
        return resourceLoader.getValidatorRules();
    }

    @GetMapping("/definitions")
    public List<ValidatorDefinition> listValidatorDefinitions() {
        return resourceLoader.getValidators();
    }

    public static class ValidationRequest {
        private List<UnitId> units;
        private List<NCUId> ncus;
        private List<AttachmentId> attachments;
        private String season;
        private String rules;

        public List<UnitId> getUnits() {
            return units;
        }

        public void setUnits(List<UnitId> units) {
            this.units = units;
        }

        public List<NCUId> getNcus() {
            return ncus;
        }

        public void setNcus(List<NCUId> ncus) {
            this.ncus = ncus;
        }

        public List<AttachmentId> getAttachments() {
            return attachments;
        }

        public void setAttachments(List<AttachmentId> attachments) {
            this.attachments = attachments;
        }

        public String getSeason() {
            return season;
        }

        public void setSeason(String season) {
            this.season = season;
        }

        public String getRules() {
            return rules;
        }

        public void setRules(String rules) {
            this.rules = rules;
        }

        @Override
        public String toString() {
            return "ValidationRequest{" +
                    "units=" + units +
                    ", ncus=" + ncus +
                    ", attachments=" + attachments +
                    ", season='" + season + '\'' +
                    ", rules='" + rules + '\'' +
                    '}';
        }
    }

    public record UnitId(String id) {}
    public record NCUId(String id) {}
    public record AttachmentId(String id) {}

    public static class ValidationResponse {
        private String season;
        private String rules;
        private List<String> units;
        private List<String> ncus;
        private List<String> attachments;
        private String valid;

        public String getSeason() {
            return season;
        }

        public void setSeason(String season) {
            this.season = season;
        }

        public String getRules() {
            return rules;
        }

        public void setRules(String rules) {
            this.rules = rules;
        }

        public List<String> getUnits() {
            return units;
        }

        public void setUnits(List<String> units) {
            this.units = units;
        }

        public List<String> getNcus() {
            return ncus;
        }

        public void setNcus(List<String> ncus) {
            this.ncus = ncus;
        }

        public List<String> getAttachments() {
            return attachments;
        }

        public void setAttachments(List<String> attachments) {
            this.attachments = attachments;
        }

        public String getValid() {
            return valid;
        }

        public void setValid(String valid) {
            this.valid = valid;
        }

        @Override
        public String toString() {
            return "ValidationResponse{" +
                    "season='" + season + '\'' +
                    ", rules='" + rules + '\'' +
                    ", units=" + units +
                    ", ncus=" + ncus +
                    ", attachments=" + attachments +
                    ", valid='" + valid + '\'' +
                    '}';
        }
    }
}
