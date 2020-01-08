package com.kevin.likeexcel;

import com.kevin.likeexcel.bean.FloorData;
import com.kevin.likeexcel.bean.RoomData;
import com.kevin.likeexcel.bean.UnitData;

import java.util.ArrayList;
import java.util.List;

public class DataTools {
    public static List<FloorData> getFloorList() {
        List<FloorData> floorList = new ArrayList<>();
        for (int i = 1; i < 20; i++) {
            FloorData floorData = new FloorData();
            floorData.setFloorname(i + "");
            floorData.setUnitList(getUnitData(i));
            floorList.add(floorData);
        }
        return floorList;
    }

    private static List<UnitData> getUnitData(int position) {
        List<UnitData> unitList = new ArrayList<>();
        for (int i = 1; i < (10); i++) {
            UnitData unitData = new UnitData();
            unitData.setUnitname(i + "");
            unitData.setRoomList(getRoomList(position, i));
            unitList.add(unitData);
        }
        return unitList;
    }

    private static List<RoomData> getRoomList(int floorposition, int unitposition) {
        List<RoomData> roomList = new ArrayList<>();
        for (int i = 1; i < 4; i++) {
            String roomname = unitposition + "-" + floorposition + "0" + i;
            if ((unitposition * floorposition) % i == 0) {
                RoomData roomData = new RoomData();
                roomData.setRoomname(roomname);
                roomList.add(roomData);
            }
        }
        return roomList;
    }
}
