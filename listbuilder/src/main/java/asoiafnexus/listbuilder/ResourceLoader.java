package asoiafnexus.listbuilder;

import asoiafnexus.listbuilder.model.Attachment;
import asoiafnexus.listbuilder.model.NCU;
import asoiafnexus.listbuilder.model.Unit;
import asoiafnexus.listbuilder.model.ValidatorDefinition;
import asoiafnexus.listbuilder.model.ValidatorRule;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.ClassPathScanningCandidateComponentProvider;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

@Component
public class ResourceLoader {

    @Autowired
    ObjectMapper objMapper;

    final List<String> factions = List.of(
            "baratheon",
            "bolton",
            "brotherhood",
            "freefolk",
            "greyjoy",
            "lannister",
            "martell",
            "neutral",
            "nightswatch",
            "stark",
            "targaryen");

    final List<String> validators = List.of(
            "cmon"
    );

    public ResourceLoader() {

    }

    public record FactionFile(
            List<Unit> units,
            List<NCU> ncus,
            List<Attachment> attachments
    ) {
    }

    public record ValidatorFile(
            List<ValidatorDefinition> definitions
    ) {
    }

    public record ValidatorRuleFile(
            List<ValidatorRule> rules
    ) {
    }

    public List<FactionFile> loadFiles() { return loadFiles(factions); }
    public List<FactionFile> loadFiles(List<String> factions) {
        return factions.stream()
                .map(f -> ResourceLoader.class.getResourceAsStream("/data/" + f + ".json"))
                .map(iostream -> {
                    try {
                        return objMapper.readValue(iostream, FactionFile.class);
                    } catch (IOException e) {
                        throw new RuntimeException(e);
                    }
                })
                .toList();
    }

    public List<Unit> getAllUnits() {
        return loadFiles().stream()
                .flatMap(x -> x.units().stream())
                .toList();
    }

    public List<NCU> getAllNCUs() {
        return loadFiles().stream()
                .flatMap(x -> x.ncus().stream())
                .toList();
    }

    public List<Attachment> getAllAttachments() {
        return loadFiles().stream()
                .flatMap(x -> x.attachments().stream())
                .toList();
    }

    public byte[] getImage(String id) {
        try(var io = ResourceLoader.class.getResourceAsStream("/img/" + id + ".jpg")) {
            if(Objects.isNull(io)) return null;
            return io.readAllBytes();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public List<ValidatorFile> loadValidatorFiles() {
        return loadValidatorFiles(validators);
    }

    public List<ValidatorFile> loadValidatorFiles(List<String> validators) {
        return validators.stream()
                .map(f -> {
                    String filePath = "/validator/definitions/" + f + ".json";
                    try (InputStream iostream = ResourceLoader.class.getResourceAsStream(filePath)) {
                        System.out.println("Loading validator file: " + filePath);
                        return objMapper.readValue(iostream, ValidatorFile.class);
                    } catch (IOException e) {
                        System.err.println("Error loading validator file: " + filePath);
                        throw new RuntimeException(e);
                    }
                })
                .toList();
    }

    public List<ValidatorDefinition> getValidators() {
        return loadValidatorFiles().stream()
                .flatMap(x -> x.definitions().stream())
                .toList();
    }

    public List<ValidatorRuleFile> loadValidatorRuleFiles() {
        return loadValidatorRuleFiles(validators);
    }

    public List<ValidatorRuleFile> loadValidatorRuleFiles(List<String> validators) {
        return validators.stream()
                .map(f -> {
                    String filePath = "/validator/rules/" + f + ".json";
                    try (InputStream iostream = ResourceLoader.class.getResourceAsStream(filePath)) {
                        System.out.println("Loading validator rule file: " + filePath);
                        TypeReference<Map<String, List<ValidatorRule>>> typeRef = new TypeReference<>() {};
                        Map<String, List<ValidatorRule>> ruleMap = objMapper.readValue(iostream, typeRef);
                        List<ValidatorRule> rules = ruleMap.get("rules");
                        return new ValidatorRuleFile(rules);
                    } catch (IOException e) {
                        System.err.println("Error loading validator rule file: " + filePath);
                        throw new RuntimeException(e);
                    }
                })
                .toList();
    }

    public List<ValidatorRule> getValidatorRules() {
        return loadValidatorRuleFiles().stream()
                .flatMap(x -> x.rules().stream())
                .collect(Collectors.toList());
    }
}