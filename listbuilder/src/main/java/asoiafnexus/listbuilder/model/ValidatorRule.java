package asoiafnexus.listbuilder.model;

import java.util.List;
import java.util.Map;

public class ValidatorRule {
    private String id;
    private String validator;
    private String name;
    private String season;
    private List<Map<String, String>> general;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getValidator() {
        return validator;
    }

    public void setValidator(String validator) {
        this.validator = validator;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getSeason() {
        return season;
    }

    public void setSeason(String season) {
        this.season = season;
    }

    public List<Map<String, String>> getGeneral() {
        return general;
    }

    public void setGeneral(List<Map<String, String>> general) {
        this.general = general;
    }

    @Override
    public String toString() {
        return "ValidatorRule{" +
                "id='" + id + '\'' +
                ", validator='" + validator + '\'' +
                ", name='" + name + '\'' +
                ", season='" + season + '\'' +
                ", general=" + general +
                '}';
    }

}
