package asoiafnexus.listbuilder;

import asoiafnexus.listbuilder.model.Attachment;
import asoiafnexus.listbuilder.model.NCU;
import asoiafnexus.listbuilder.model.Unit;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.ClassPathScanningCandidateComponentProvider;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;
import java.util.List;
import java.util.Objects;

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

    public ResourceLoader() {

    }

    public record FactionFile(
            List<Unit> units,
            List<NCU> ncus,
            List<Attachment> attachments
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
}
