let app = new Vue({
    el: '#app',
    data: {
        imgWidth: 120,
        imgHeight: null,
        personTableList: [],
        colCount: 6,
        infoFontSize: 16,
        examPlaceInfoList: [],
        colCountTmp: 0,
        picHost: "",
        subjectInfoMaxCountList: [],
        subjectInfoMaxCountMap: {},
        random: 0,
        tableTitle: "嘉兴市职业技能操作考试试场核对单",
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
    },
    created() {
        this.imgHeight = this.imgWidth * 1.4;
        document.documentElement.style.fontSize = this.infoFontSize + "px";
        this.picHost = window.location.origin + "/";

        this.getSubjectInfoMaxCountList();
    },
    methods: {
        change: function () {
            let url = "updateMaxCount";
            axios.post(url, this.subjectInfoMaxCountList).then((res) => {
                console.log(res.data);
            });
        },
        adjustImgSize: function () {
            this.imgHeight = this.imgWidth * 1.4;
        },
        advanceChange: function () {
            this.advance = !this.advance;
            this.ticketRow = 0;
        },
        changeInfoFontSize: function (x) {
            this.infoFontSize += x;
            document.documentElement.style.fontSize = this.infoFontSize + "px";
        },
        changeTab: function (tab) {
            this.tab = tab;
        },
        processPic: function (random) {
            this.random = random;
            let url = "processPersonByGroup";
            axios.get(url, {
                params: {
                    random: this.random,
                }
            }).then((res) => {
                this.changeTab("核对单列表");
                let allExamInfo = new AllExamInfo(res.data);
                this.examPlaceInfoList = [];
                for (let key in allExamInfo.list) {
                    let examPlaceInfo = new ExamPlaceInfo(allExamInfo.list[key]);
                    this.examPlaceInfoList.push(examPlaceInfo);
                }
            });
        },
        getSubjectInfoMaxCountList: function () {
            let url = "getSubjectInfoMaxCountList";
            axios.get(url).then((res) => {
                let allExamInfo = new AllExamInfo(res.data);
                let subjectInfoList = res.data;
                this.subjectInfoMaxCountList = [];
                this.subjectInfoMaxCountMap = {};
                for (let key in subjectInfoList) {
                    let subjectInfo = new SubjectInfo(subjectInfoList[key]);
                    this.subjectInfoMaxCountList.push(subjectInfo);

                    this.subjectInfoMaxCountMap[subjectInfo.subjectType] = subjectInfo;
                }
            });
        },
        addPrefixZero: function (num, n) {
            return addPrefixZero(num, n);
        },
        exportExcel: function () {
            let url = "exportExcel";
            axios.get(url, {
                params: {
                    exportFileName: this.tableTitle,
                }
            }).then((res) => {
            });
        },
    },

});
