let app = new Vue({
    el: '#app',
    data: {
        allExamInfo: {},
        imgWidth: 120,
        imgHeight: null,
        personTableList: [],
        colCount: 6,
        infoFontSize: 16,
        colCountTmp: 0,
        picHost: "",
        subjectInfoMaxCountList: [],
        subjectInfoMaxCountMap: {},
        random: 0,
        tableTitle: "嘉兴市职业技能操作考试试场核对单",
        tableStickersTitle: "嘉兴市职业技能操作考试试场桌贴",
        emptyPng: "data:image/png;base64,R0lGODlhAQABAIAAAAAAAP///yH5BAEAAAAALAAAAAABAAEAAAIBRAA7",
        advance: false,
        printTitle: "准考证",
        ticketColCount: 2,
        tab: "核对单列表",
        examName: "准考证",
        examTime: "",
        ticketImgWidth: 120,
        ticketRow: 0,
        ticketTitle: "浙江省高校招生职业技能操作考试",
        examRoomInfoList: [],
        showSubjectInfo: false,
        showTicketSinglePic: true,
        ticketSingleTd: {
            border: "1px solid black",
            padding: "8px",
            "min-width": "250px",
            "max-width": "600px",
        },
        ticketSingleMsg: "",
        tableStickersPerPageCount: 10,
    },
    created() {
        this.imgHeight = this.imgWidth * 1.4;
        document.documentElement.style.fontSize = this.infoFontSize + "px";
        this.picHost = window.location.origin;

        this.getSubjectInfoMaxCountList();
    },
    methods: {
        change() {
            let url = "updateMaxCount";
            axios.post(url, this.examRoomInfoList).then((res) => {
                console.log(res.data);
            });
        },
        adjustImgSize() {
            this.imgHeight = this.imgWidth * 1.4;
        },
        advanceChange() {
            this.advance = !this.advance;
            this.ticketRow = 0;
        },
        changeInfoFontSize(x) {
            this.infoFontSize += x;
            document.documentElement.style.fontSize = this.infoFontSize + "px";
        },
        changeTab(tab) {
            this.tab = tab;
        },
        processPic(random) {
            this.random = random;
            let url = "processPersonByGroup";
            axios.get(url, {
                params: {
                    random: this.random,
                }
            }).then((res) => {
                this.changeTab("核对单列表(带照片)");
                this.allExamInfo = new AllExamInfo(res.data);
            });
        },
        getSubjectInfoMaxCountList() {
            let url = "getSubjectInfoMaxCountList";
            axios.get(url).then((res) => {
                let list = res.data;
                if (list == null || list.length === 0) {
                    return;
                }
                this.examRoomInfoList = [];
                for (let i = 0; i < list.length; i++) {
                    this.examRoomInfoList.push(new ExamRoomInfo(list[i]));
                }
            });
        },
        addPrefixZero(num, n) {
            if (!num) {
                return "";
            }
            return addPrefixZero(num, n);
        },
        exportExcel() {
            let url = "exportExcel";
            axios.get(url, {
                params: {
                    exportFileName: this.tableTitle,
                }
            }).then((res) => {
            });
        },
        log(data) {
            console.log(data);
        },
        changeSubjectInfo() {
            this.showSubjectInfo = !this.showSubjectInfo;
        },
        changeTicketPicShow() {
            this.showTicketSinglePic = !this.showTicketSinglePic;
            this.ticketSingleTd["min-width"] = this.showTicketSinglePic ? "250px" : "330px";
        },
        displayTicketSingleMsg() {
            return this.ticketSingleMsg.split("\n");
        },
    },

});
