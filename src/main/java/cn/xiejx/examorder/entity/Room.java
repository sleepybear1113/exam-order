package cn.xiejx.examorder.entity;

import lombok.Data;

import java.io.Serial;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

/**
 * There is description
 * @author sleepybear
 * @date 2023/08/04 00:10
 */
@Data
public class Room implements Serializable {
    @Serial
    private static final long serialVersionUID = -4036883568186191746L;

    private Integer id;
    private String examPlaceId;
    private String examPlaceName;
    private String subjectType;
    private String subjectName;
    private String maxCountStr;
    private Integer maxCount;
    private String roomCountStr;
    private Integer roomCount;
    private String roomName;
    private String time;

    private List<PersonInfo> persons = new ArrayList<>();

    public static List<Room> build(List<ExamPlaceInfo> list) {
        List<Room> rooms = new ArrayList<>();
        for (ExamPlaceInfo examPlaceInfo : list) {
            for (ExamRoomInfo examRoomInfo : examPlaceInfo.getList()) {
                for (ExamRoomInfo roomInfo : examRoomInfo.getRoomList()) {
                    Room room = new Room();
                    room.setId(roomInfo.getId());
                    room.setExamPlaceId(examPlaceInfo.getExamPlaceId());
                    room.setExamPlaceName(examPlaceInfo.getExamPlaceName());
                    room.setSubjectType(examRoomInfo.getSubjectType());
                    room.setSubjectName(examRoomInfo.getSubjectName());
                    room.setMaxCountStr(examRoomInfo.getMaxCountStr());
                    room.setMaxCount(examRoomInfo.getMaxCount());
                    room.setRoomCountStr(examRoomInfo.getRoomCountStr());
                    room.setRoomCount(examRoomInfo.getRoomCount());
                    room.setRoomName(roomInfo.getRoomName());
                    room.setTime(roomInfo.getTime());
                    List<PersonInfo> persons = roomInfo.getPersons();
                    room.setPersons(persons);
                    rooms.add(room);

                    for (PersonInfo person : persons) {
                        person.setSubjectTypeName(examRoomInfo.getSubjectName());
                        person.setExamPlaceName(examPlaceInfo.getExamPlaceName());
                    }
                }
            }
        }

        rooms.sort(Comparator.comparingInt(Room::getId));
        return rooms;
    }

    public static List<PersonInfo> getSortedPersons(List<Room> rooms) {
        List<PersonInfo> persons = new ArrayList<>();
        for (Room room : rooms) {
            persons.addAll(room.getPersons());
        }
        // 按照 examNumber 排序
        persons.sort(Comparator.comparing(PersonInfo::getExamNumber));
        return persons;
    }
}
