class PersonInfo {
    constructor(props) {
        if (props == null) {
            return;
        }

        this.picSrc = (props.picSrc == null || props.picSrc === "") ? "" : props.picSrc;
        this.id = props.id;
        this.name = props.name;
        this.sex = props.sex;
        this.roomNo = props.roomNo;
        this.examPlaceId = props.examPlaceId;
        this.seatNo = props.seatNo;
        this.examNumber = props.examNumber;
        this.examPlaceName = props.examPlaceName;
        this.idCard = props.idCard;
        this.subjectType = props.subjectType;
        this.subjectTypeName = props.subjectTypeName;
        this.time = props.time;
    }
}

class AllExamInfo {
    constructor(props) {
        if (props == null) {
            return;
        }

        let tmpList = props.list;
        this.list = [];
        if (tmpList != null && tmpList.length >= 0) {
            for (let i = 0; i < tmpList.length; i++) {
                this.list.push(new ExamPlaceInfo(tmpList[i]));
            }
        }
    }
}

class ExamPlaceInfo {
    constructor(props) {
        if (props == null) {
            return;
        }

        this.examPlaceName = props.examPlaceName;
        this.examPlaceId = props.examPlaceId;
        let tmpList = props.list;
        this.list = [new ExamRoomInfo()];
        this.list = [];
        if (tmpList != null && tmpList.length >= 0) {
            for (let i = 0; i < tmpList.length; i++) {
                this.list.push(new ExamRoomInfo(tmpList[i]));
            }
        }
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