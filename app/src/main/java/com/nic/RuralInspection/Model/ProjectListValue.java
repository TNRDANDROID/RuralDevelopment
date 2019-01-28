package com.nic.RuralInspection.Model;

/**
 * Created by AchanthiSundar on 01-11-2017.
 */

public class ProjectListValue {

    /*Chiller Cooler*/

    private String projectName, amount, stage, district;
    private int projectCount;

    public ProjectListValue(String projectName, String amount, String stage, String district
            , int projectCount) {
        this.projectName = projectName;
        this.amount = amount;
        this.stage = stage;
        this.district = district;
        this.projectCount = projectCount;

    }

    public String getProjectName() {
        return projectName;
    }

    public void setProjectName(String projectName) {
        this.projectName = projectName;
    }

    public String getAmount() {
        return amount;
    }

    public void setAmount(String amount) {
        this.amount = amount;
    }

    public String getStage() {
        return stage;
    }

    public void setStage(String stage) {
        this.stage = stage;
    }

    public String getDistrict() {
        return district;
    }

    public void setDistrict(String district) {
        this.district = district;
    }

    public int getProjectCount() {
        return projectCount;
    }

    public void setProjectCount(int projectCount) {
        this.projectCount = projectCount;
    }


}