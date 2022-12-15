package cn.xiejx.examorder.fxml;

import cn.xiejx.examorder.entity.ExamRoomInfo;
import cn.xiejx.examorder.entity.PersonInfo;
import cn.xiejx.examorder.excel.Read;
import cn.xiejx.examorder.utils.SpringContextUtil;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
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

import java.awt.*;
import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URI;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
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
    private String dataPath;
    private String roomPath;

    private File lastChosenFile = new File(System.getProperty("user.dir"));

    public static List<PersonInfo> personInfoList = new ArrayList<>();

    public static Map<String, Map<String, List<ExamRoomInfo>>> placeSubjectRoomInfoMap = new HashMap<>();

    @FXML
    public void initialize() {
        addInfo("程序已启动！");
    }

    public void openBrowser(ActionEvent actionEvent) throws Exception {
        if (CollectionUtils.isEmpty(personInfoList)) {
            addInfo("没有有效的考生数据！请重新选择Excel文件！");
            return;
        }

        if (MapUtils.isEmpty(placeSubjectRoomInfoMap)) {
            addInfo("没有有效的考场场次数据！请重新选择Excel文件！");
            return;
        }

        addInfo("打开浏览器...");
        Environment environment = SpringContextUtil.getBean(Environment.class);
        String port = environment.getProperty("server.port");
        String url = "http://127.0.0.1:%s".formatted(port);
        String osName = System.getProperty("os.name", "");
        try {
            if (osName.startsWith("Mac OS")) {
                Class.forName("com.apple.eio.FileManager").getDeclaredMethod("openURL", String.class).invoke(null, url);
            } else if (osName.startsWith("Windows")) {
                Runtime.getRuntime().exec("rundll32 url.dll,FileProtocolHandler " + url);
            } else {
                if (Desktop.isDesktopSupported() && Desktop.getDesktop().isSupported(Desktop.Action.BROWSE)) {
                    URI uri = new URI(url);
                    Desktop.getDesktop().browse(uri);
                } else {
                    boolean success = false;
                    String[] cmdList = {"xdg-open ", "sensible-browser ", "x-www-browser ", "gnome-open "};
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
                        addInfo(url.replace("http://", ""));
                    }
                }
            }
        } catch (Exception e) {
            addInfo("打开浏览器失败！");
            addInfo("请手动打开浏览器，并且输入以下网址：");
            addInfo(url.replace("http://", ""));
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
            dataPath = null;
            return;
        }
        lastChosenFile = file.getParentFile();
        String fileName = file.getName();
        dataPathLabel.setText(fileName);
        dataPath = file.getAbsolutePath();

        Runnable runnable = null;
        if (fileName.endsWith(".xls") || fileName.endsWith(".xlsx")) {
            runnable = () -> readPersonExcel(dataPath);
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
        roomPath = file.getAbsolutePath();

        Runnable runnable = null;
        if (fileName.endsWith(".xls") || fileName.endsWith(".xlsx")) {
            runnable = () -> readRoomExcel(roomPath);
        }
        new Thread(runnable).start();
    }

    private void readPersonExcel(String filePath) {
        personInfoList = Read.readPersonData(filePath);
        List<String> valid = PersonInfo.valid(personInfoList);
        String boo = valid.get(0);
        valid.remove(0);
        for (String s : valid) {
            addInfo(s, false);
        }
        if (!Boolean.TRUE.toString().equalsIgnoreCase(boo)) {
            addInfo("读取失败！请重新选择！");
            personInfoList.clear();
            return;
        }
        addInfo("[%s]读取完毕，数据记录数：%s".formatted(new File(filePath).getName(), personInfoList.size()));
    }

    private void readRoomExcel(String filePath) {
        List<ExamRoomInfo> examRoomInfoInfoList = Read.readRoomData(filePath);

        if (CollectionUtils.isEmpty(examRoomInfoInfoList)) {
            addInfo("没有试场信息，请检测模板填写是否正确");
            return;
        }
        examRoomInfoInfoList.forEach(ExamRoomInfo::clearInvalidRoomName);

        placeSubjectRoomInfoMap.clear();

        Set<String> placeIdSet = new HashSet<>();
        Set<String> subjectTypeSet = new HashSet<>();
        int totalRoomCount = 0;
        for (ExamRoomInfo examRoomInfo : examRoomInfoInfoList) {
            String examPlaceId = examRoomInfo.getExamPlaceId();
            String subjectType = examRoomInfo.getSubjectType();

            totalRoomCount += examRoomInfo.getRoomCount();
            placeIdSet.add(examPlaceId);
            subjectTypeSet.add(subjectType);

            Map<String, List<ExamRoomInfo>> subjectExamRoomInfoMap = placeSubjectRoomInfoMap.get(examPlaceId);
            if (subjectExamRoomInfoMap == null) {
                subjectExamRoomInfoMap = new HashMap<>();
                List<ExamRoomInfo> examRoomInfos = new ArrayList<>();
                examRoomInfos.add(examRoomInfo);
                subjectExamRoomInfoMap.put(subjectType, examRoomInfos);
                placeSubjectRoomInfoMap.put(examPlaceId, subjectExamRoomInfoMap);
            } else {
                List<ExamRoomInfo> examRoomInfos = subjectExamRoomInfoMap.get(subjectType);
                if (CollectionUtils.isEmpty(examRoomInfos)) {
                    examRoomInfos = new ArrayList<>();
                    examRoomInfos.add(examRoomInfo);
                    subjectExamRoomInfoMap.put(subjectType, examRoomInfos);
                } else {
                    examRoomInfos.add(examRoomInfo);
                }
            }
        }

        addInfo("[%s]读取完毕，数据记录数：%s".formatted(new File(filePath).getName(), examRoomInfoInfoList.size()));
        addInfo("[共计]考点数：%s, 类别数：%s, 试场总数：%s".formatted(placeIdSet.size(), subjectTypeSet.size(), totalRoomCount));
    }

    public void addInfo(String s) {
        addInfo(s, true);
    }

    public void addInfo(String s, boolean toBottom) {
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
                1. 点击“选择考生信息数据文件”按钮，选择带 姓名、考生号、类别代码 等信息的Excel文件。选择完成后会在信息框中看到相关提示。
                2. 点击“选择考生照片文件夹”按钮。里面需要包含考生照片，以 考生号 或者 身份证号 为文件名，后缀为 jpg 或 png。会自动通过考生号或身份证号关联。
                3. 选择完毕后，点击“打开浏览器”按钮，进行一些布局的相关设置。
                4. 打开浏览器后，请不要关闭本程序。本程序将会后台运行，在浏览器页面上输出编排表，以及导出Excel相关功能。
                5. 请勿使用 IE 浏览器，可以使用 Edge、Chrome、Firefox、360、2345 等主流浏览器。若“打开浏览器”按钮无法打开浏览器，请自行打开浏览器，输入 127.0.0.1:13322 回车访问即可。
                """);
        alert.setHeaderText("""
                考试编排系统使用说明
                """);
        alert.showAndWait();
    }

    public void downloadExcelTemplate(ActionEvent actionEvent) {
        String templateName = "考生信息Excel模板.xlsx";
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
