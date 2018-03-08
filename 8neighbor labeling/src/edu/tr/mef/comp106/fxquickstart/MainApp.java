package edu.tr.mef.comp106.fxquickstart;

/**
 *
 * @author vardaru@mef.edu.tr
 */
import com.sun.javaws.Main;
import java.io.File;
import javafx.application.Application;
import static javafx.application.Application.launch;
import javafx.fxml.FXMLLoader;
import javafx.scene.Cursor;
import javafx.scene.ImageCursor;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.scene.paint.Color;
import javafx.stage.Stage;

public class MainApp extends Application{

    public static Scene scene;

    @Override
    public void start(Stage stage) throws Exception {
        FXMLLoader fXMLLoader = new FXMLLoader();
        Parent root = fXMLLoader.load(getClass().getResource("/edu/tr/mef/comp106/fxquickstart/fxml/Scene.fxml"));
        MainPaneController mainPaneController = (MainPaneController) fXMLLoader.getController();
        this.scene = new Scene(root);
        File cursor = new File("src/edu/tr/mef/comp106/fxquickstart/cursor.png");
        File cursor2 = new File("src/edu/tr/mef/comp106/fxquickstart/621366734.png");
        File cursor3 = new File("src/edu/tr/mef/comp106/fxquickstart/eraser.png");
        if (cursor == null) {
            return;
        }
        Image cursorr = new Image(cursor.toURI().toString());
        Image cursorr2 = new Image(cursor2.toURI().toString());
        Image cursorr3 = new Image(cursor3.toURI().toString());
        
        scene.setCursor(new ImageCursor(cursorr));

        if (MainPaneController.spreyclicked) {
            scene.setCursor(new ImageCursor(cursorr2));
        }
        
        if (MainPaneController.eraserclicked) {
            scene.setCursor(new ImageCursor(cursorr3));
        }

        scene.getStylesheets().add("/edu/tr/mef/comp106/fxquickstart/styles/Styles.css");
        setUserAgentStylesheet(STYLESHEET_CASPIAN);
        stage.setTitle("Painting");
        stage.setScene(scene);
        stage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }

}
