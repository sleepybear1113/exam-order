let app = new Vue({
    el: '#app',
    data: {
        allExamInfo: {},
        imgWidth: 100,
        imgHeight: null,
        infoFontSize: 16,
        picHost: "",
        random: 0,
        emptyPng: "data:image/gif;base64,R0lGODlhAQABAIAAAAAAAP///yH5BAEAAAAALAAAAAABAAEAAAIBRAA7",
        advance: false,
        tab: "核对单列表",
        examTime: "",
        ticketImgWidth: 120,
        ticketRow: 0,
        picturesFilesMap: {},
        dataId: {
            personInfoKey: "",
            personInfoId: null,
            personInfoKeyFilename: "",
            personCount: "",

            placeSubjectRoomInfoId: null,
            placeSubjectRoomInfoKey: "",
            placeSubjectRoomInfoKeyFilename: "",
            examSubjectCount: "",
        },
        ticketSingleTd: {
            border: "1px solid black",
            padding: "8px",
            "min-width": "200px",
            "max-width": "500px",
        },
        tableConfig: {
            table1: {
                title1: "考试试场核对单",
                title2: "",
                colCount: 6,
                showExamNumber: true,
                showIdCard: true,
                showName: true,
                showSeatNo: true,
                showSex: true,
            },
            table2: {
                title1: "考试试场核对单",
                title2: "",
                showExamNumber: true,
                showNumber: true,
                showIdCard: true,
                showName: true,
                showSeatNo: true,
                showSex: true,
                showSign: true,
                tableStickersPerPageCount: 30,
            },
            table3: {
                title1: "xxxxxxxxxx考试",
                title2: "准考证",
                showExamNumber: true,
                showIdCard: true,
                showName: true,
                showSeatNo: true,
                tableStickersPerPageCount: 10,
                ticketColCount: 2,
            },
            table4: {
                title1: "xxxxxxxxxx考试",
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
        createInputUpload(inputId, multiple = false, webkitdirectory = false) {
            let parentDiv = document.getElementById("div-" + inputId);
            while (parentDiv.firstChild) {
                parentDiv.removeChild(parentDiv.firstChild);
            }

            let inputElement = document.createElement("input");
            let id = "input-" + inputId + "-" + parseInt(String(Math.random() * 100000));

            inputElement.setAttribute("type", "file");
            inputElement.setAttribute("id", id);
            inputElement.setAttribute("accept", ".xls,.xlsx");
            if (multiple) {
                inputElement.setAttribute("multiple", "multiple");
            }
            if (webkitdirectory) {
                inputElement.setAttribute("webkitdirectory", "webkitdirectory");
            }

            if (inputId === "stu") {
                inputElement.addEventListener("change", this.uploadStu);
                this.dataId.personInfoId = id;
            } else if (inputId === "room") {
                inputElement.addEventListener("change", this.uploadRoom);
                this.dataId.placeSubjectRoomInfoId = id;
            } else if (inputId === "pictures") {
                inputElement.addEventListener("change", this.uploadPictures);
            }

            parentDiv.appendChild(inputElement);
            inputElement.click();
        },
        uploadStu() {
            this.uploadExcel("stu", 1);
        },
        uploadRoom() {
            this.uploadExcel("room", 2);
        },
        uploadExcel(inputId, type) {
            let url = "/upload/file";
            let elementId = "";
            if (type === 1) {
                elementId = this.dataId.personInfoId;
            } else if (type === 2) {
                elementId = this.dataId.placeSubjectRoomInfoId;
            }

            let input = document.getElementById(elementId);
            const file = input.files[0];
            if (!file) {
                return;
            }
            if (type === 1) {
                this.dataId.personInfoKeyFilename = file.name;
            } else if (type === 2) {
                this.dataId.placeSubjectRoomInfoKeyFilename = file.name;
            }
            const formData = new FormData();
            formData.append("file", file);
            formData.append("deleteAfterUpload", true);
            formData.append("expireTimeMinutes", 60);

            axios.post(url, formData, {
                'Content-type': 'multipart/form-data'
            }).then(res => {
                let uploadFileInfoDto = new UploadFileInfoDto(res.data.result);
                if (!uploadFileInfoDto) {
                    alert("上传失败！");
                    return;
                }

                if (type === 1) {
                    this.dataId.personInfoKey = uploadFileInfoDto.fileStreamDtoId;
                    this.dataId.personInfoKeyFilename = uploadFileInfoDto.filename;
                    this.processStu(uploadFileInfoDto.fileStreamDtoId);
                } else if (type === 2) {
                    this.dataId.placeSubjectRoomInfoKey = uploadFileInfoDto.fileStreamDtoId;
                    this.dataId.placeSubjectRoomInfoKeyFilename = uploadFileInfoDto.filename;
                    this.processRoom(uploadFileInfoDto.fileStreamDtoId);
                }
            }).catch(err => {
                // 出现错误时的处理
                alert("上传失败，请选择其他文件");
            });
        },
        uploadPictures(event) {
            const files = event.target.files;
            if (!files) {
                return;
            }

            this.picturesFilesMap = {};
            for (let file of files) {
                // 将图片类型的 file 放入 picturesFilesMap 中，并以无后缀的文件名为 key
                if (file.type.startsWith("image")) {
                    let key = file.name.split(".")[0];
                    file.srcUrl = URL.createObjectURL(file);
                    this.picturesFilesMap[key.toUpperCase()] = file;
                }
            }
        },
        clearPictureFiles() {
            this.picturesFilesMap = {};
        },
        buildPicSrc(idCard, examNumber) {
            if (!idCard) {
                idCard = "";
            }
            if (!examNumber) {
                examNumber = "";
            }
            idCard = idCard.toUpperCase();
            examNumber = examNumber.toUpperCase();

            if (Object.keys(this.picturesFilesMap).length === 0) {
                return "/exam-order/empty-photo.png";
            }

            let pictureFile = this.picturesFilesMap[idCard] || this.picturesFilesMap[examNumber];
            if (!pictureFile) {
                return "/exam-order/empty-photo.png";
            }
            return pictureFile.srcUrl;
        },
        processStu(fileStreamDtoId) {
            let url = "/process/process";
            axios.get(url, {
                params: {
                    fileStreamDtoId: fileStreamDtoId,
                    type: 1,
                }
            }).then((res) => {
                let key = res.data.result;

                axios.get("/data/getReadPersonInfo", {
                    params: {
                        personInfoKey: key,
                    }
                }).then((res) => {
                    let readPersonInfo = new ReadPersonInfo(res.data.result);
                    console.log(readPersonInfo);
                    if (readPersonInfo.validList) {
                        if (readPersonInfo.validList[0] === "true") {
                            this.dataId.personInfoKey = readPersonInfo.key;
                            this.dataId.personCount = readPersonInfo.personCount;
                        } else {
                            this.dataId.personInfoKeyFilename = "";
                            alert("上传的文件有信息错误！" + readPersonInfo.validList.join("\n"));
                        }
                    }
                });
            });
        },
        processRoom(fileStreamDtoId) {
            let url = "/process/process";
            axios.get(url, {
                params: {
                    fileStreamDtoId: fileStreamDtoId,
                    type: 2,
                }
            }).then((res) => {
                let key = res.data.result;
                axios.get("/data/getReadRoomInfo", {
                    params: {
                        placeSubjectRoomInfoKey: key,
                    }
                }).then((res) => {
                    let readRoomInfo = new ReadRoomInfo(res.data.result);
                    if (!readRoomInfo) {
                        alert("???");
                        return;
                    }
                    console.log(readRoomInfo);
                    this.dataId.placeSubjectRoomInfoKey = readRoomInfo.key;
                    this.dataId.examSubjectCount = readRoomInfo.examSubjectCount;
                });
            });
        },
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
            if (!this.dataId.personInfoId) {
                alert("请先上传学生信息文件");
                return;
            }
            if (!this.dataId.placeSubjectRoomInfoId) {
                alert("请先上传考场信息文件");
                return;
            }
            this.random = random;
            let url = "/processPersonByGroup";
            axios.get(url, {
                params: {
                    random: this.random,
                    personInfoKey: this.dataId.personInfoKey,
                    placeSubjectRoomInfoKey: this.dataId.placeSubjectRoomInfoKey,
                }
            }).then((res) => {
                this.changeTab("核对单列表(带照片)");
                this.allExamInfo = new AllExamInfo(res.data.result);
            });
        },
        addPrefixZero(num, n) {
            if (!num) {
                return "";
            }
            return addPrefixZero(num, n);
        },
        exportExcel() {
            let url = "/exportExcel";
            axios.get(url, {
                params: {
                    key: this.allExamInfo.id,
                }
            }).then((res) => {
                let key = res.data.result;
                if (!key) {
                    return;
                }
                this.downloadUrl(axios.defaults.baseURL + "/download/downloadFile?key=" + key);
            });
        },
        log(data) {
            console.log(data);
        },
        changeTicketPicShow() {
            this.tableConfig.table4.showTicketSinglePic = !this.tableConfig.table4.showTicketSinglePic;
            this.ticketSingleTd["min-width"] = this.tableConfig.table4.showTicketSinglePic ? "200px" : "310px";
            this.ticketSingleTd["max-width"] = this.tableConfig.table4.showTicketSinglePic ? "200px" : "310px";
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
                version = res.data.result;
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
        },
        downloadUrl(url) {
            const link = document.createElement('a');
            link.href = url;
            link.style.display = 'none';
            document.body.appendChild(link);
            link.click();
            document.body.removeChild(link);
        },
    },
});
