package cn.xjx.examorder.fxml;

import cn.xjx.examorder.dbf.DbfAllRecord;
import cn.xjx.examorder.entity.PersonInfo;
import cn.xjx.examorder.entity.SubjectMaxCount;
import cn.xjx.examorder.excel.Read;
import cn.xjx.examorder.utils.SpringContextUtil;
import com.linuxense.javadbf.DBFRow;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.stage.DirectoryChooser;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.core.env.Environment;

import java.io.File;
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
    public TextArea infoTextArea;
    public static String picPath = null;

    public Label dataPathLabel;
    private String dataPath;

    private File lastChosenFile = new File(System.getProperty("user.dir"));

    public static List<PersonInfo> personInfoList = new ArrayList<>();
    public static List<SubjectMaxCount> subjectInfoMaxCountList = new ArrayList<>();

    @FXML
    public void initialize() {
        List<SubjectMaxCount> list = SubjectMaxCount.read();
        if (CollectionUtils.isNotEmpty(list)) {
            subjectInfoMaxCountList.clear();
            subjectInfoMaxCountList.addAll(list);
            log.info("已加载数量：{}", list.size());
        }
    }

    public void openBrowser(ActionEvent actionEvent) throws Exception {
        Environment environment = SpringContextUtil.getBean(Environment.class);
        String port = environment.getProperty("server.port");
        System.out.println(port);
        String url = "http://127.0.0.1:%s".formatted(port);
        Runtime.getRuntime().exec("rundll32 url.dll,FileProtocolHandler " + url);
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
            infoTextArea.setText("文件夹为空！");
        } else {
            infoTextArea.setText("文件夹内文件数量：%s".formatted(files.length));
        }
    }

    public void chooseDataPath(ActionEvent actionEvent) {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setInitialDirectory(lastChosenFile);
        fileChooser.getExtensionFilters().addAll(
                new FileChooser.ExtensionFilter("Excel、dbf文件", "*.xls", "*.xlsx", "*.dbf"),
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
        if (fileName.endsWith(".dbf")) {
            runnable = () -> readDbf(dataPath);
        } else if (fileName.endsWith(".xls") || fileName.endsWith(".xlsx")) {
            runnable = () -> readExcel(dataPath);
        }
        new Thread(runnable).start();
    }

    private void readDbf(String dbfFilePath) {
        DbfAllRecord dbfAllRecord = DbfAllRecord.read(dbfFilePath);
        if (dbfAllRecord == null) {
            Platform.runLater(() -> infoTextArea.setText("%s 文件读取失败！".formatted(dbfFilePath)));
            return;
        }

        String[] fieldNames = dbfAllRecord.getFieldNames();
        List<DBFRow> records = dbfAllRecord.getRecords();

        Platform.runLater(() -> Platform.runLater(() -> infoTextArea.setText("DBF文件读取完毕，数据记录数：%s".formatted(records.size()))));
    }

    private void readExcel(String dbfFilePath) {
        personInfoList = Read.readData(dbfFilePath);
        Platform.runLater(() -> Platform.runLater(() -> infoTextArea.setText("Excel文件读取完毕，数据记录数：%s".formatted(personInfoList.size()))));

        List<SubjectMaxCount> list = SubjectMaxCount.read();

        Map<String, SubjectMaxCount> map = new HashMap<>();
        if (CollectionUtils.isNotEmpty(list)) {
            for (SubjectMaxCount subjectMaxCount : list) {
                map.put(subjectMaxCount.getSubjectType(), subjectMaxCount);
            }
            subjectInfoMaxCountList.clear();
            subjectInfoMaxCountList.addAll(list);
        }

        int addCount = 0;
        for (PersonInfo personInfo : personInfoList) {
            String subjectType = personInfo.getSubjectType();
            String subjectTypeName = personInfo.getSubjectTypeName();
            SubjectMaxCount subjectMaxCount = map.get(subjectType);

            if (subjectMaxCount == null) {
                subjectMaxCount = new SubjectMaxCount(subjectType, subjectTypeName);
                map.put(subjectType, subjectMaxCount);
                addCount++;
            }
        }

        if (addCount > 0) {
            list = new ArrayList<>(map.values());
            SubjectMaxCount.write(list);
            subjectInfoMaxCountList.clear();
            subjectInfoMaxCountList.addAll(list);
            final int count = addCount;
            Platform.runLater(() -> Platform.runLater(() -> infoTextArea.setText(infoTextArea.getText() + "\n新增考点配置数：%s".formatted(count))));
        }
    }
}
