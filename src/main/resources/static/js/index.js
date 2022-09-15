let app = new Vue({
    el: '#app',
    data: {
        imgWidth: 70,
        imgHeight: null,
        personTableList: [],
        colCount: 6,
        infoFontSize: 14,
        examPlaceInfoList: [],
        colCountTmp: 0,
        picHost: "",
        subjectInfoMaxCountList: [],
        random: 0,
        tableTitle: "核对单",
        emptyPng: "data:image/png;base64,R0lGODlhAQABAIAAAAAAAP///yH5BAEAAAAALAAAAAABAAEAAAIBRAA7",
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
        changeInfoFontSize: function (x) {
            this.infoFontSize += x;
            document.documentElement.style.fontSize = this.infoFontSize + "px";
        },
        processPic: function (random) {
            this.random = random;
            let url = "processPersonByGroup";
            axios.get(url, {
                params: {
                    random: this.random,
                }
            }).then((res) => {
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
                for (let key in subjectInfoList) {
                    let subjectInfo = new SubjectInfo(subjectInfoList[key]);
                    this.subjectInfoMaxCountList.push(subjectInfo);
                }
            });
        },
        addPrefixZero: function (num, n) {
            return addPrefixZero(num, n);
        },
        exportExcel: function () {
            let url = "exportExcel";
            axios.get(url).then((res) => {
            });
        },
    },

});
