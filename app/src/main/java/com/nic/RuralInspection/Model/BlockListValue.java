package com.nic.RuralInspection.Model;

import android.graphics.Bitmap;

import java.sql.Blob;

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
    private String workGroupID;
    private String workTypeID;
    private String workID;
    private String workName;
    private String asAmount;
    private String tsAmount;
    private String isHighValue;
    private String workStageCode;
    private String workStageOrder;
    private String workStageName;
    private String projectID;
    private String observationName;
    private String description;
    private String latitude;
    private String longitude;
    private Bitmap image;


//    public BlockListValue(String bcode, String scheme_id, String work_id, String as_value, String is_high_value) {
//        this.blockCode = bcode;
//        this.schemeID = scheme_id;
//        this.workID = work_id;
//        this.asAmount = as_value;
//        this.isHighValue = is_high_value;
//    }

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

    public void setProjectName(String projectName) {
        this.projectName = projectName;
    }

    public String getProjectName() {
        return projectName;
    }

    public void setProjectID(String projectID) {
        this.projectID = projectID;
    }

    public String getProjectID() {
        return projectID;
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

    public void setWorkGroupID(String workGroupID) {
        this.workGroupID = workGroupID;
    }

    public String getWorkGroupID() {
        return workGroupID;
    }

    public void setWorkTypeID(String workTypeID) {
        this.workTypeID = workTypeID;
    }

    public String getWorkTypeID() {
        return workTypeID;
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

    public void setWorkStageCode(String workStageCode) {
        this.workStageCode = workStageCode;
    }

    public String getWorkStageCode() {
        return workStageCode;
    }

    public void setWorkStageOrder(String workStageOrder) {
        this.workStageOrder = workStageOrder;
    }

    public String getWorkStageOrder() {
        return workStageOrder;
    }

    public void setWorkStageName(String workStageName) {
        this.workStageName = workStageName;
    }

    public String getWorkStageName() {
        return workStageName;
    }

    public void setObservationName(String observationName) {
        this.observationName = observationName;
    }

    public String getObservationName() {
        return observationName;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getDescription() {
        return description;
    }

    public void setLatitude(String latitude) {
        this.latitude = latitude;
    }

    public String getLatitude() {
        return latitude;
    }

    public void setLongitude(String longitude) {
        this.longitude = longitude;
    }

    public String getLongitude() {
        return longitude;
    }

    public void setImage(Bitmap image) {
        this.image = image;
    }

    public Bitmap getImage() {
        return image;
    }
}