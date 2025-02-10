package asoiafnexus.listbuilder;

import asoiafnexus.listbuilder.model.Unit;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * Functions and capabilities for interacting with unit data.
 */
@Service
public class Units {

    private static final Logger log = LoggerFactory.getLogger(Units.class);
    private Map<String, Unit> unitLookup;

    public Units(ObjectMapper mapper) {
        try (var io = Units.class.getClassLoader().getResourceAsStream("unitdata.json")) {
            var type = mapper
                    .getTypeFactory()
                    .constructCollectionType(List.class, Unit.class);
            List<Unit> loadedUnits = mapper.readValue(io, type);
            unitLookup = loadedUnits.stream().collect(Collectors.toMap(Unit::id, Function.identity()));
        } catch (Exception ex) {
            log.error("Loading unit data", ex);
            unitLookup = Map.of();
        }
    }

    public Map<String, Unit> unitLookup() { return unitLookup; }

    public List<Unit> allUnits() {
        return unitLookup.values().stream().toList();
    }
}
