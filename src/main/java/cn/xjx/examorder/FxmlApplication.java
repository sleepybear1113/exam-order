package cn.xjx.examorder;

import cn.xjx.examorder.utils.SpringContextUtil;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;

/**
 * @author xjx
 */
public class FxmlApplication extends Application {
    @Override
    public void start(Stage stage) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/fxml/index.fxml"));
        Scene scene = new Scene(fxmlLoader.load(), 400, 300);
        stage.setTitle("考点考号编排");
        stage.setScene(scene);
        stage.show();
    }

    @Override
    public void stop() {
        SpringContextUtil.getApplicationContext().close();
    }

    public static void main(String[] args) {
        launch();
    }
}