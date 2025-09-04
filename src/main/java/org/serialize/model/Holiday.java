package org.serialize.model;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

public class Holiday {
    private String date;
    private String localName;
    private String name;
    private String countryCode;
    private boolean fixed;
    private boolean global;
    private List<String> counties;
    private Integer launchYear;
    private List<String> types;

    @JsonProperty("date")
    public String getDate() { return date; }
    public void setDate(String date) { this.date = date; }

    @JsonProperty("localName")
    public String getLocalName() { return localName; }
    public void setLocalName(String localName) { this.localName = localName; }

    @JsonProperty("name")
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    @JsonProperty("countryCode")
    public String getCountryCode() { return countryCode; }
    public void setCountryCode(String countryCode) { this.countryCode = countryCode; }

    @JsonProperty("fixed")
    public boolean isFixed() { return fixed; }
    public void setFixed(boolean fixed) { this.fixed = fixed; }

    @JsonProperty("global")
    public boolean isGlobal() { return global; }
    public void setGlobal(boolean global) { this.global = global; }

    @JsonProperty("counties")
    public List<String> getCounties() { return counties; }
    public void setCounties(List<String> counties) { this.counties = counties; }

    @JsonProperty("launchYear")
    public Integer getLaunchYear() { return launchYear; }
    public void setLaunchYear(Integer launchYear) { this.launchYear = launchYear; }

    @JsonProperty("types")
    public List<String> getTypes() { return types; }
    public void setTypes(List<String> types) { this.types = types; }

    @Override
    public String toString() {
        return date + " - " + localName + " (" + name + ")";
    }
}