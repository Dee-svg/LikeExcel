package com.kevin.likeexcel.bean;

import java.util.List;

public class UnitData {
    private String unitname;
    private List<RoomData> roomList;

    public String getUnitname() {
        return unitname;
    }

    public void setUnitname(String unitname) {
        this.unitname = unitname;
    }

    public List<RoomData> getRoomList() {
        return roomList;
    }

    public void setRoomList(List<RoomData> roomList) {
        this.roomList = roomList;
    }
}
