package com.nic.RuralInspection.Model;

/**
 * Created by AchanthiSundar on 01-11-2017.
 */

public class BlockListValue {

    private String distictCode;
    private String blockCode;
    private String blockName;
    private String schemeName;
    private String projectName;
    private String schemeSequentialID;
    private String selectedBlockCode;
    private String schemeID;
    private String workID;
    private String workName;
    private String asAmount;
    private String tsAmount;
    private String isHighValue;


    public String getFinancialYear() {
        return financialYear;
    }

    public void setFinancialYear(String financialYear) {
        this.financialYear = financialYear;
    }

    private String financialYear;

    public String getSchemeSequentialID() {
        return schemeSequentialID;
    }

    public void setSchemeSequentialID(String schemeSequentialID) {
        this.schemeSequentialID = schemeSequentialID;
    }

    public String getSchemeName() {
        return schemeName;
    }

    public void setSchemeName(String schemeName) {
        this.schemeName = schemeName;
    }

    public String getName() {
        return Name;
    }

    public void setName(String name) {
        Name = name;
    }

    private String Name;

    public String getProjectName() {
        return projectName;
    }

    public void setProjectName(String projectName) {
        this.projectName = projectName;
    }


    public String getDistictCode() {
        return distictCode;
    }

    public void setDistictCode(String distictCode) {
        this.distictCode = distictCode;
    }

    public String getBlockCode() {
        return blockCode;
    }

    public void setBlockCode(String blockCode) {
        this.blockCode = blockCode;
    }

    public String getBlockName() {
        return blockName;
    }

    public void setBlockName(String blockName) {
        this.blockName = blockName;
    }

    public String getSelectedBlockCode() {
        return selectedBlockCode;
    }

    public void setSelectedBlockCode(String selectedBlockCode) {
        this.selectedBlockCode = selectedBlockCode;
    }

    public String getSchemeID() {
        return schemeID;
    }

    public void setSchemeID(String schemeID) {
        this.schemeID = schemeID;
    }

    public String getWorkID() {
        return workID;
    }

    public void setWorkID(String workID) {
        this.workID = workID;
    }

    public String getWorkName() {
        return workName;
    }

    public void setWorkName(String workName) {
        this.workName = workName;
    }

    public String getAsAmount() {
        return asAmount;
    }

    public void setAsAmount(String asAmount) {
        this.asAmount = asAmount;
    }

    public String getTsAmount() {
        return tsAmount;
    }

    public void setTsAmount(String tsAmount) {
        this.tsAmount = tsAmount;
    }

    public String getIsHighValue() {
        return isHighValue;
    }

    public void setIsHighValue(String isHighValue) {
        this.isHighValue = isHighValue;
    }
}