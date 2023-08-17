let app = new Vue({
    el: '#app',
    data: {
        allExamInfo: {},
        imgWidth: 100,
        imgHeight: null,
        infoFontSize: 16,
        picHost: "",
        random: 0,
        emptyPng: "data:image/png;base64,R0lGODlhAQABAIAAAAAAAP///yH5BAEAAAAALAAAAAABAAEAAAIBRAA7",
        advance: false,
        tab: "核对单列表",
        examTime: "",
        ticketImgWidth: 120,
        ticketRow: 0,
        ticketSingleTd: {
            border: "1px solid black",
            padding: "8px",
            "min-width": "250px",
            "max-width": "600px",
        },
        tableConfig: {
            table1: {
                title1: "嘉兴市职业技能操作考试试场核对单",
                title2: "",
                colCount: 6,
                showExamNumber: true,
                showIdCard: true,
                showName: true,
                showSeatNo: true,
                showSex: true,
            },
            table2: {
                title1: "嘉兴市职业技能操作考试试场核对单",
                title2: "",
                showExamNumber: true,
                showNumber: true,
                showIdCard: true,
                showName: true,
                showSeatNo: true,
                showSex: true,
                showSign: true,
            },
            table3: {
                title1: "嘉兴市职业技能操作考试",
                title2: "准考证",
                showExamNumber: true,
                showIdCard: true,
                showName: true,
                showSeatNo: true,
                tableStickersPerPageCount: 10,
                ticketColCount: 2,
            },
            table4: {
                title1: "嘉兴市职业技能操作考试",
                title2: "准考证",
                showExamNumber: true,
                showIdCard: true,
                showName: true,
                showSeatNo: true,
                ticketSingleMsg: "",
                showTicketSinglePic: true,
                specificExamPlaceName: "",
            },
        },
    },
    created() {
        this.imgHeight = this.imgWidth * 1.3;
        document.documentElement.style.fontSize = this.infoFontSize + "px";
        this.picHost = window.location.origin;
    },
    methods: {
        adjustImgSize() {
            this.imgHeight = this.imgWidth * 1.3;
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
                    exportFileName: this.tableConfig.table1.title1,
                }
            }).then((res) => {
            });
        },
        log(data) {
            console.log(data);
        },
        changeTicketPicShow() {
            this.tableConfig.table4.showTicketSinglePic = !this.tableConfig.table4.showTicketSinglePic;
            this.ticketSingleTd["min-width"] = this.tableConfig.table4.showTicketSinglePic ? "250px" : "330px";
        },
        displayTicketSingleMsg() {
            return this.tableConfig.table4.ticketSingleMsg.split("\n");
        },
        toShowHideStr(bool) {
            return !bool ? "隐藏" : "显示";
        },
        getVersion() {
            let url = "getVersion";
            axios.get(url).then((res) => {
                version = res.data;
            });
        },
        getPersonOriginSchool() {
            let persons = this.allExamInfo.persons;
            if (!persons || persons.length === 0) {
                return [];
            }
            let result = [];
            for (let i = 0; i < persons.length; i++) {
                let person = persons[i];
                if (person.originExamPlaceName) {
                    result.push(person.originExamPlaceName);
                }
            }
            return this.toUniqueArr(result);
        },
        toUniqueArr(arr) {
            let uniqueArr = [];
            let seen = new Set();
            for (let i = 0; i < arr.length; i++) {
                if (!seen.has(arr[i])) {
                    seen.add(arr[i]);
                    uniqueArr.push(arr[i]);
                }
            }

            return uniqueArr;
        }
    },
});
