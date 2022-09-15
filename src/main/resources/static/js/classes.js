class PersonInfo {
    constructor(props) {
        if (props == null) {
            return;
        }

        this.picSrc = window.location.origin + props.picSrc;
        this.name = props.name;
        this.seatNo = props.seatNo;
        this.examNumber = props.examNumber;
        this.examPlaceName = props.examPlaceName;
    }
}

class AllExamInfo {
    constructor(props) {
        if (props == null) {
            return;
        }

        this.list = props.list;
    }
}

class ExamPlaceInfo {
    constructor(props) {
        if (props == null) {
            return;
        }

        this.examPlaceName = props.examPlaceName;
        this.list = props.list;
    }
}

class SubjectInfo {
    constructor(props) {
        if (props == null) {
            return;
        }

        this.subjectType = props.subjectType;
        this.subjectTypeName = props.subjectTypeName;
        this.personInfoList = props.personInfoList;
        this.examRoomInfoList = props.examRoomInfoList;
        this.maxCount = props.maxCount == null ? 30 : props.maxCount;
    }
}


class ExamRoomInfo {
    constructor(props) {
        if (props == null) {
            return;
        }

        this.subjectName = props.subjectName;
        this.roomName = props.roomName;
        this.list = props.list;
        this.maxCount = props.maxCount == null ? 30 : props.maxCount;
    }
}