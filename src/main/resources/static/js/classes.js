class PersonInfo {
    constructor(props) {
        if (props == null) {
            return;
        }

        this.picSrc = (props.picSrc == null || props.picSrc === "") ? "/empty-photo.png" : (props.picSrc.startsWith("/") ? props.picSrc : "/" + props.picSrc);
        this.id = props.id;
        this.name = props.name;
        this.sex = props.sex;
        this.roomNo = props.roomNo;
        this.examPlaceId = props.examPlaceId;
        this.seatNo = props.seatNo;
        this.examNumber = props.examNumber;
        this.examPlaceName = props.examPlaceName;
        this.originExamPlaceName = props.originExamPlaceName;
        this.idCard = props.idCard;
        this.subjectType = props.subjectType;
        this.subjectTypeName = props.subjectTypeName;
        this.time = props.time;
    }
}

class UploadFileInfoDto {
    constructor(props = {}) {
        this.id = props.id;
        this.filename = props.filename;
        this.fileStreamDtoId = props.fileStreamDtoId;
    }
}

class AllExamInfo {
    constructor(props) {
        if (props == null) {
            return;
        }

        this.id = props.id;
        this.rooms = props.rooms.map(item => new Room(item));
        this.persons = props.persons.map(item => new PersonInfo(item));
    }
}

class ExamRoomInfo {
    constructor(props) {
        if (props == null) {
            return;
        }

        this.id = props.id;
        this.examPlaceId = props.examPlaceId;
        this.examPlaceName = props.examPlaceName;
        this.subjectType = props.subjectType;
        this.subjectName = props.subjectName;
        this.roomName = props.roomName;
        this.roomCount = props.roomCount;
        this.time = props.time;
        this.maxCount = props.maxCount == null ? 30 : props.maxCount;

        this.list = [[new PersonInfo()]];
        this.list = [];

        let tmpPersonList = props.persons;
        this.persons = [new PersonInfo()];
        this.persons = [];
        if (tmpPersonList != null && tmpPersonList.length >= 0) {
            for (let i = 0; i < tmpPersonList.length; i++) {
                this.persons.push(new PersonInfo(tmpPersonList[i]));
            }
        }

        let roomTmpList = props.roomList;
        this.roomList = [new ExamRoomInfo()];
        this.roomList = [];
        if (roomTmpList != null && roomTmpList.length >= 0) {
            for (let i = 0; i < roomTmpList.length; i++) {
                this.roomList.push(new ExamRoomInfo(roomTmpList[i]));
            }
        }
    }
}

class Room {
    constructor(props) {
        if (props == null) {
            return;
        }

        this.id = props.id;
        this.examPlaceId = props.examPlaceId;
        this.examPlaceName = props.examPlaceName;
        this.subjectType = props.subjectType;
        this.subjectName = props.subjectName;
        this.maxCountStr = props.maxCountStr;
        this.maxCount = props.maxCount;
        this.roomCountStr = props.roomCountStr;
        this.roomCount = props.roomCount;
        this.roomName = props.roomName;
        this.time = props.time;
        this.persons = props.persons.map(item => new PersonInfo(item));
    }
}