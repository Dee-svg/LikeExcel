package com.kevin.likeexcel.bean;

import java.util.List;

public class FloorData {

    private String floorname;
    private List<UnitData> unitList;

    public String getFloorname() {
        return floorname;
    }

    public void setFloorname(String floorname) {
        this.floorname = floorname;
    }

    public List<UnitData> getUnitList() {
        return unitList;
    }

    public void setUnitList(List<UnitData> unitList) {
        this.unitList = unitList;
    }
}
