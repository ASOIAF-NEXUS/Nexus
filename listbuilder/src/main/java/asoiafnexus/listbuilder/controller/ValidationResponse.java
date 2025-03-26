package asoiafnexus.listbuilder.controller;

import java.util.List;

public class ValidationResponse {
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
