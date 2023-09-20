package cn.xiejx.examorder.fxml;

import cn.xiejx.examorder.config.AppProperties;
import cn.xiejx.examorder.constants.Constants;
import cn.xiejx.examorder.entity.ExamRoomInfo;
import cn.xiejx.examorder.entity.FileStreamDto;
import cn.xiejx.examorder.entity.PersonInfo;
import cn.xiejx.examorder.entity.ReadPersonInfo;
import cn.xiejx.examorder.logic.ProcessLogic;
import cn.xiejx.examorder.utils.SpringContextUtil;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.stage.DirectoryChooser;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.collections4.MapUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.core.env.Environment;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.util.FileCopyUtils;

import java.io.File;
import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;

/**
 * There is description
 *
 * @author sleepybear
 * @date 2022/09/05 14:22
 */
@Slf4j
public class IndexFxml {

    public Label picPathLabel;
    public static String picPath = null;

    public Label dataPathLabel;
    public ListView<String> infoListView;
    public Label roomPathLabel;
    public Button download1;

    public Button download2;

    public static String personInfoKey = "";
    public static String placeSubjectRoomInfoKey = "";

    private File lastChosenFile = new File(System.getProperty("user.dir"));

    @FXML
    public void initialize() {
        addInfo("程序已启动！");
    }

    public void openBrowser(ActionEvent actionEvent) {
        ReadPersonInfo readPersonInfo = Constants.READ_PERSON_INFO_CACHER.get(IndexFxml.personInfoKey);
        if (CollectionUtils.isEmpty(readPersonInfo.getPersonInfoList())) {
            addInfo("没有有效的考生数据！请重新选择Excel文件！");
            return;
        }

        if (MapUtils.isEmpty(Constants.EXAM_ROOM_INFO_MAP_CACHER.get(IndexFxml.placeSubjectRoomInfoKey))) {
            addInfo("没有有效的考场场次数据！请重新选择Excel文件！");
            return;
        }

        new Thread(this::openBrowser1).start();
    }

    public void openBrowser1() {
        addInfo("打开浏览器...");
        Environment environment = SpringContextUtil.getBean(Environment.class);
        String port = environment.getProperty("server.port");
        String urlRaw = "http://127.0.0.1:%s".formatted(port);
        String url = urlRaw + "?version=" + SpringContextUtil.getBean(AppProperties.class).getVersion();
        String osName = System.getProperty("os.name", "");
        try {
            if (osName.startsWith("Mac OS")) {
                Class.forName("com.apple.eio.FileManager").getDeclaredMethod("openURL", String.class).invoke(null, url);
            } else if (osName.startsWith("Windows")) {
                Runtime.getRuntime().exec("rundll32 url.dll,FileProtocolHandler " + url);
            } else {
                boolean success = false;
                String[] cmdList = {"xdg-open", "sensible-browser", "x-www-browser", "gnome-open"};
                for (String cmd : cmdList) {
                    try {
                        Process proc = Runtime.getRuntime().exec(cmd + " " + url);
                    } catch (IOException ignored) {
                        continue;
                    }
                    success = true;
                    break;
                }
                if (!success) {
                    addInfo("打开浏览器失败！");
                    addInfo("请手动打开浏览器，并且输入以下网址：");
                    addInfo(urlRaw.replace("http://", ""));
                }
            }
        } catch (Exception e) {
            addInfo("打开浏览器失败！");
            addInfo("请手动打开浏览器，并且输入以下网址：");
            addInfo(urlRaw.replace("http://", ""));
        }
    }

    public void choosePicDir(ActionEvent actionEvent) {
        DirectoryChooser directoryChooser = new DirectoryChooser();
        directoryChooser.setTitle("请选择照片文件夹");

        File file = directoryChooser.showDialog(new Stage());
        if (file == null) {
            picPathLabel.setText("未选择！");
            picPath = null;
            return;
        }
        lastChosenFile = file.getParentFile();
        picPathLabel.setText(file.getName());
        picPath = file.getAbsolutePath();
        File[] files = file.listFiles();
        if (files == null || files.length == 0) {
            addInfo("文件夹为空！");
        } else {
            addInfo("文件夹内文件数量：%s".formatted(files.length));
        }
    }

    public void chooseDataPath(ActionEvent actionEvent) {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("请选择考生Excel文件");
        fileChooser.setInitialDirectory(lastChosenFile);
        fileChooser.getExtensionFilters().addAll(
                new FileChooser.ExtensionFilter("Excel文件", "*.xls", "*.xlsx"),
                new FileChooser.ExtensionFilter("全部", "*.*")
        );
        File file = fileChooser.showOpenDialog(new Stage());
        if (file == null) {
            dataPathLabel.setText("未选择！");
            return;
        }
        lastChosenFile = file.getParentFile();
        String fileName = file.getName();
        dataPathLabel.setText(fileName);

        FileStreamDto fileStreamDto = new FileStreamDto();
        fileStreamDto.setByteArrayInputStream(file);

        Runnable runnable = null;
        if (fileName.endsWith(".xls") || fileName.endsWith(".xlsx")) {
            runnable = () -> {
                Constants.READ_PERSON_INFO_CACHER.remove(personInfoKey);
                personInfoKey = ProcessLogic.readPersonExcel(fileStreamDto);

                ReadPersonInfo readPersonInfo = Constants.READ_PERSON_INFO_CACHER.get(personInfoKey);
                List<String> validList = readPersonInfo.getValidList();
                List<PersonInfo> personInfoList = readPersonInfo.getPersonInfoList();
                String boo = validList.get(0);
                validList.remove(0);
                for (String s : validList) {
                    addInfo(s, false);
                }
                if (!Boolean.TRUE.toString().equalsIgnoreCase(boo)) {
                    addInfo("读取失败！请重新选择！");
                    personInfoList.clear();
                    Constants.READ_PERSON_INFO_CACHER.remove(personInfoKey);
                    personInfoKey = "";
                } else {
                    addInfo("[%s]读取完毕，数据记录数：%s".formatted(new File(fileStreamDto.getOriginalFilename()).getName(), personInfoList.size()));
                }
            };
        }
        new Thread(runnable).start();
    }

    public void chooseRoomPath(ActionEvent actionEvent) {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("请选择试场场次Excel文件");
        fileChooser.setInitialDirectory(lastChosenFile);
        fileChooser.getExtensionFilters().addAll(
                new FileChooser.ExtensionFilter("Excel文件", "*.xls", "*.xlsx"),
                new FileChooser.ExtensionFilter("全部", "*.*")
        );
        File file = fileChooser.showOpenDialog(new Stage());
        if (file == null) {
            roomPathLabel.setText("未选择！");
            roomPathLabel = null;
            return;
        }
        lastChosenFile = file.getParentFile();
        String fileName = file.getName();
        roomPathLabel.setText(fileName);

        FileStreamDto fileStreamDto = new FileStreamDto();
        fileStreamDto.setByteArrayInputStream(file);

        Runnable runnable = null;
        if (fileName.endsWith(".xls") || fileName.endsWith(".xlsx")) {
            runnable = () -> {
                Constants.EXAM_ROOM_INFO_MAP_CACHER.remove(placeSubjectRoomInfoKey);
                placeSubjectRoomInfoKey = ProcessLogic.readRoomExcel(fileStreamDto);
                if (StringUtils.isEmpty(placeSubjectRoomInfoKey)) {
                    addInfo("没有试场信息，请检测模板填写是否正确");
                    return;
                }

                Map<String, Map<String, List<ExamRoomInfo>>> mapMap = Constants.EXAM_ROOM_INFO_MAP_CACHER.get(placeSubjectRoomInfoKey);
                if (MapUtils.isEmpty(mapMap)) {
                    addInfo("没有试场信息，请检测模板填写是否正确");
                    return;
                }

                int totalRoomCount = 0;
                Set<String> placeIdSet = new HashSet<>();
                Set<String> subjectTypeSet = new HashSet<>();
                for (Map<String, List<ExamRoomInfo>> map : mapMap.values()) {
                    for (List<ExamRoomInfo> examRoomInfos : map.values()) {
                        for (ExamRoomInfo examRoomInfo : examRoomInfos) {
                            totalRoomCount += examRoomInfo.getRoomCount();
                            placeIdSet.add(examRoomInfo.getExamPlaceId());
                            subjectTypeSet.add(examRoomInfo.getSubjectType());
                        }
                    }
                }

                addInfo("[%s]读取完毕，数据记录数：%s".formatted(fileStreamDto.getOriginalFilename(), mapMap.size()));
                addInfo("[共计]考点数：%s, 类别数：%s, 试场总数：%s".formatted(placeIdSet.size(), subjectTypeSet.size(), totalRoomCount));
            };
        }
        new Thread(runnable).start();
    }

    public void addInfo(String s) {
        addInfo(s, true);
    }

    public void addInfo(String s, boolean toBottom) {
        if (!Constants.isGui) {
            return;
        }
        DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("[HH:mm:ss.SSS]");
        LocalDateTime now = LocalDateTime.now();
        Platform.runLater(() -> {
            infoListView.getItems().add("[%s] %s".formatted(now.format(dateTimeFormatter), s));
            if (toBottom) {
                infoListView.scrollTo(infoListView.getItems().size());
            }
        });
    }

    public void intro(ActionEvent actionEvent) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("使用说明");
        alert.setContentText("""
                使用步骤：
                1. 点击“选择考生信息Excel”按钮，选择带 姓名、考生号、类别代码 等信息的Excel文件。选择完成后会在信息框中看到相关提示。
                2. 点击“试场场次规格Excel”按钮，选择带 学校、类别代码 等信息的Excel文件。选择完成后会在信息框中看到相关提示。
                3. 点击“选择考生照片文件夹”按钮。里面需要包含考生照片，以 考生号 或者 身份证号 为文件名，后缀为 jpg 或 png。会自动通过考生号或身份证号关联。
                4. 选择完毕后，点击“打开浏览器”按钮，进行一些布局的相关设置。
                5. 打开浏览器后，请不要关闭本程序。本程序将会后台运行，在浏览器页面上输出编排表，以及导出Excel相关功能。
                6. 请勿使用 IE 浏览器，可以使用 Edge、Chrome、Firefox、360、2345 等主流浏览器。若“打开浏览器”按钮无法打开浏览器，请自行打开浏览器，输入 127.0.0.1:13322 回车访问即可。
                """);
        alert.setHeaderText("""
                考试编排系统使用说明
                """);
        alert.showAndWait();
    }

    public void downloadExcelTemplate1(ActionEvent actionEvent) {
        String templateName = "考生信息Excel模板.xlsx";
        downloadTemplate(templateName);
    }

    public void downloadExcelTemplate2(ActionEvent actionEvent) {
        String templateName = "试场场次Excel模板.xlsx";
        downloadTemplate(templateName);
    }

    private void downloadTemplate(String templateName) {
        try {
            //获取inu模板文件
            Resource resource = new ClassPathResource(templateName);
            FileChooser fileChooser = new FileChooser();
            fileChooser.setInitialFileName(templateName);
            fileChooser.getExtensionFilters().addAll(
                    new FileChooser.ExtensionFilter("xlsx", "*.xlsx")
            );
            File file = fileChooser.showSaveDialog(new Stage());
            if (file == null) {
                return;
            }
            FileCopyUtils.copy(resource.getInputStream().readAllBytes(), file);
        } catch (IOException e) {
            log.error(e.getMessage(), e);
        }
    }
}
