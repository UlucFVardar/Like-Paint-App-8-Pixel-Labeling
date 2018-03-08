package edu.tr.mef.comp106.fxquickstart;

import java.awt.Canvas;
import java.awt.image.RenderedImage;
import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.embed.swing.SwingFXUtils;
import javafx.event.ActionEvent;
import javafx.event.Event;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Cursor;
import javafx.scene.ImageCursor;
import javafx.scene.Scene;
import javafx.scene.control.Hyperlink;
import javafx.scene.control.Label;
import javafx.scene.control.MenuItem;
import javafx.scene.control.Slider;
import javafx.scene.control.TitledPane;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.image.PixelReader;
import javafx.scene.image.PixelWriter;
import javafx.scene.image.WritableImage;
import javafx.scene.input.MouseDragEvent;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;

import javafx.scene.paint.Color;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import javax.imageio.ImageIO;

/**
 *
 * @author vardaru@mef.edu.tr
 */
public class MainPaneController extends DisjointSet implements Initializable {

    @FXML
    TitledPane selectionMenu;

    @FXML
    MenuItem CleanPage;

    @FXML
    ImageView ivMain;

    @FXML
    MenuItem information;

    @FXML
    ImageView drawregt;

    @FXML
    ImageView sprey;

    @FXML
    ImageView eraser;

    @FXML
    ImageView brush;

    @FXML
    Slider setThicknees;

    @FXML
    ImageView ivcursor;

    @FXML
    ImageView ivPalette;

    @FXML
    MenuItem miAddImage;

    @FXML
    MenuItem miSave;

    @FXML
    MenuItem deleteAll;

    @FXML
    MenuItem newPaper;

    @FXML
    VBox MainPane;

    Image image;
    WritableImage wImage;

    DisjointSet ds;
    static boolean spreyclicked;
    static boolean eraserclicked;
    static boolean drawreg;

    int[][] pigments;
    int[][] labeled;
    int maxX, maxY;
    Color choosenColor;
    int ChoosenLabel;
    int startX, startY;
    boolean drawing = false;//program is open as setted up painting 
    public MainPaneController() {
        this.clickdelete = new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                drawing = false;
                spreyclicked = false;
                selectionMenu.setVisible(false);
                setThicknees.setVisible(false);
                File file = new File("src/edu/tr/mef/comp106/fxquickstart/images/logo_tr_color.png");
                if (file == null) {
                    return;
                }
                Image image = new Image(file.toURI().toString());
                ivMain.setImage(image);
                ivMain.setFitHeight(0);
                ivMain.setFitWidth(0);
            }
        };
        this.OpenNewPaper = new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {

                setThicknees.setVisible(true);
                selectionMenu.setVisible(true);
                drawing = true;
                MainApp.scene.setCursor(Cursor.DEFAULT);
                File file = new File("src/edu/tr/mef/comp106/fxquickstart/images/newemtypaper.png");
                if (file == null) {
                    return;
                }
                image = new Image(file.toURI().toString());
                PixelReader pixelReader = image.getPixelReader();
                wImage = new WritableImage((int) image.getWidth(), (int) image.getHeight());
                PixelWriter pixelWriter = wImage.getPixelWriter();
                maxX = (int) image.getHeight();
                maxY = (int) image.getWidth();
                for (int Y = 0; Y < maxX; Y++) {
                    for (int X = 0; X < maxY; X++) {
                        pixelWriter.setColor(X, Y, Color.WHITE);
                        if (Y == 0 || X == 0 || Y == maxX - 1 || X == maxY - 1) {
                            pixelWriter.setColor(X, Y, Color.BLACK);
                        }
                    }
                }
                ivMain.setImage(wImage);
                ivMain.setFitHeight(0);
                ivMain.setFitWidth(0);
            }
        };
        this.addNewImage = new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                drawing = false;
                drawreg = false;
                eraserclicked = false;
                spreyclicked = false;
                selectionMenu.setVisible(false);
                setThicknees.setVisible(false);

                FileChooser chooser = new FileChooser();
                chooser.setInitialDirectory(new File("src/edu/tr/mef/comp106/fxquickstart/images"));
                chooser.setTitle("Open File");
                Stage stage = new Stage();
                File file = chooser.showOpenDialog(stage);
                if (file == null) {
                    return;
                }
                image = new Image(file.toURI().toString());
                PixelReader pixelReader = image.getPixelReader();
                // Create WritableImage
                wImage = new WritableImage((int) image.getWidth(), (int) image.getHeight());
                PixelWriter pixelWriter = wImage.getPixelWriter();
                maxX = (int) image.getHeight();
                maxY = (int) image.getWidth();
                pigments = new int[maxX][maxY];
                labeled = new int[maxX][maxY];

                for (int Y = 0; Y < maxX; Y++) {
                    for (int X = 0; X < maxY; X++) {
                        Color color = pixelReader.getColor(X, Y);
                        // clean parazit  
                        double averageofpixelColor = (color.getBlue() + color.getGreen() + color.getRed()) * 255;
                        if ((averageofpixelColor / 3) > 150) {
                            pixelWriter.setColor(X, Y, Color.WHITE);
                            pigments[Y][X] = 1;
                            if (Y == 0 || X == 0 || Y == maxX - 1 || X == maxY - 1) {
                                pixelWriter.setColor(X, Y, Color.BLACK);
                                pigments[Y][X] = 0;
                            }
                        } else {
                            pixelWriter.setColor(X, Y, Color.BLACK);
                            pigments[Y][X] = 0;
                            if (Y == 0 || X == 0 || Y == maxX - 1 || X == maxY - 1) {
                                pixelWriter.setColor(X, Y, Color.BLACK);
                                pigments[Y][X] = 0;
                            }
                        }
                    }
                }

               
                //creation of binary bigment matrix 0's are black , 1's are White
                //creation of a frame outside of the image
                //labeling oparation
                makeİtLabeled();
                correctLabel();
                // 

                ivMain.setImage(wImage);
                ivMain.setFitHeight(0);
                ivMain.setFitWidth(0);

            }

        };
        this.cleanPage = new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                PixelWriter pixelWriter = wImage.getPixelWriter();
                for (int Y = 0; Y < maxX; Y++) {
                    for (int X = 0; X < maxY; X++) {

                        if (drawing) {
                            pixelWriter.setColor(X, Y, Color.WHITE);
                            continue;
                        }
                        if (pigments[Y][X] == 1 && !drawing) {
                            pixelWriter.setColor(X, Y, Color.WHITE);
                            continue;
                        }
                    }
                }
            }
        };
        this.Information = new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                System.out.println(".handle()");

                Stage secondStage = new Stage();

                secondStage.setTitle("Mail to the Author");
                Scene scene = new Scene(new Browser(), 750, 600, Color.web("#666970"));
                secondStage.setScene(scene);
                scene.getStylesheets().add("webviewsample/BrowserToolbar.css");
                secondStage.show();
            }
        };
        this.SaveImage = new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                File file = new File("haveBeenPainted/Painting.png");
                RenderedImage renderedImage = SwingFXUtils.fromFXImage(wImage, null);
                try {
                    ImageIO.write(renderedImage, "png", file);
                } catch (IOException ex) {
                    Logger.getLogger(MainPaneController.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
        };
    }
    
    
    @Override
    public void initialize(URL location, ResourceBundle resources) {
        miAddImage.setOnAction(addNewImage);
        deleteAll.setOnAction(clickdelete);
        miAddImage.setOnAction(addNewImage);
        CleanPage.setOnAction(cleanPage);
        newPaper.setOnAction(OpenNewPaper);
        miSave.setOnAction(SaveImage);
        ivPalette.setOnMouseClicked(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent event) {
                File file = new File("src/edu/tr/mef/comp106/fxquickstart/palet12.png");
                if (file == null) {
                    return;
                }
                Image palette = new Image(file.toURI().toString());
                PixelReader pixelReader2 = palette.getPixelReader();
                choosenColor = pixelReader2.getColor((int) event.getX(), (int) event.getY());

                System.out.println((int) event.getX() + " " + (int) event.getY() + "blue " + choosenColor.getBlue());
                File corsor = new File("src/edu/tr/mef/comp106/fxquickstart/cursor.png");
                if (corsor == null) {
                    return;
                }
                Image ivcorsorr = new Image(corsor.toURI().toString());
                PixelReader pixelReader3 = ivcorsorr.getPixelReader();
                //
                WritableImage wImage = new WritableImage((int) ivcorsorr.getWidth(), (int) ivcorsorr.getHeight());
                PixelWriter pixelWriter = wImage.getPixelWriter();
                for (int Y = 0; Y < ivcorsorr.getHeight(); Y++) {
                    for (int X = 0; X < ivcorsorr.getWidth(); X++) {
                        Color color = pixelReader3.getColor(X, Y);
                        // clean parazit  
                        double averageofpixelColor = (color.getBlue() + color.getGreen() + color.getRed()) * 255;

                        if (averageofpixelColor / 3 > 200) {
                            pixelWriter.setColor(X, Y, choosenColor);

                        }

                    }
                }
                //
                if (!spreyclicked) {
                    MainApp.scene.setCursor(new ImageCursor(wImage));
                }

                if (spreyclicked) {
                    File corsor2 = new File("src/edu/tr/mef/comp106/fxquickstart/621366734.png");
                    if (corsor == null) {
                        return;
                    }
                    Image ivcorsorr2 = new Image(corsor2.toURI().toString());
                    PixelReader pixelReader6 = ivcorsorr2.getPixelReader();
                    //
                    WritableImage wImage2 = new WritableImage((int) ivcorsorr2.getWidth(), (int) ivcorsorr2.getHeight());
                    PixelWriter pixelWriter5 = wImage2.getPixelWriter();
                    for (int Y = 0; Y < ivcorsorr.getHeight(); Y++) {
                        for (int X = 0; X < ivcorsorr2.getWidth(); X++) {
                            Color color = pixelReader6.getColor(X, Y);
                            // clean parazit  
                            double averageofpixelColor = (color.getBlue() + color.getGreen() + color.getRed()) * 255;
                            pixelWriter5.setColor(X, Y, pixelReader6.getColor(X, Y));
                            if (averageofpixelColor / 3 > 200) {
                                pixelWriter5.setColor(X, Y, choosenColor);

                            }

                        }
                    }
                    //
                    MainApp.scene.setCursor(new ImageCursor(wImage2));
                }
            }
        });
        ivMain.setOnMousePressed(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent event) {
                if (drawing && drawreg) {
                    startX = (int) event.getX();
                    startY = (int) event.getY();
                }
            }
        });
        ivMain.setOnMouseReleased(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent event) {
                if (drawing && drawreg) {
                    MainApp.scene.setCursor(Cursor.DEFAULT);
                    spreyclicked = false;
                    eraserclicked = false;
                    System.out.println(".sadadad()");

                    PixelReader pixelReader = image.getPixelReader();

                    PixelWriter pixelWriter = wImage.getPixelWriter();
                    int endX = (int) event.getX();
                    int endY = (int) event.getY();
                    int th = (int) setThicknees.getValue();
                    for (int t = 1; t <= (int) (th / 20) + 1; t++) {
                        if (startX - endX < 0) {
                            for (int i = startX; i <= endX; i++) {
                                pixelWriter.setColor(i, startY, Color.BLACK);

                                pixelWriter.setColor(i, endY, Color.BLACK);
                                pixelWriter.setColor(i, endY + (1 * t), Color.BLACK);
                                pixelWriter.setColor(i, startY + (1 * t), Color.BLACK);
                            }
                        }
                        if (startX - endX >= 0) {
                            for (int i = endX; i <= startX; i++) {
                                pixelWriter.setColor(i, startY + (1 * t), Color.BLACK);
                                pixelWriter.setColor(i, endY + (1 * t), Color.BLACK);
                                pixelWriter.setColor(i, startY, Color.BLACK);
                                pixelWriter.setColor(i, endY, Color.BLACK);
                            }
                        }

                        if (startY - endY < 0) {
                            for (int i = startY; i <= endY; i++) {
                                pixelWriter.setColor(startX + (1 * t), i, Color.BLACK);
                                pixelWriter.setColor(endX + (1 * t), i, Color.BLACK);
                                pixelWriter.setColor(startX, i, Color.BLACK);
                                pixelWriter.setColor(endX, i, Color.BLACK);
                            }
                        }
                        if (startY - endY >= 0) {
                            for (int i = endY; i <= startY; i++) {

                                pixelWriter.setColor(startX + (1 * t), i, Color.BLACK);
                                pixelWriter.setColor(endX + (1 * t), i, Color.BLACK);
                                pixelWriter.setColor(startX, i, Color.BLACK);
                                pixelWriter.setColor(endX, i, Color.BLACK);
                            }
                        }
                    }
                }
            }
        });
        ivMain.setOnMouseClicked(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent event) {
                if (!drawing && pigments[(int) event.getY()][(int) event.getX()] != 0) {// add an botun fo fill reg
                    System.out.print((int) event.getX() + "  " + (int) event.getY());
                    System.out.println("label " + labeled[(int) event.getY()][(int) event.getX()]);

                    PixelWriter pixelWriter = wImage.getPixelWriter();

                    ChoosenLabel = (int) labeled[(int) event.getY()][(int) event.getX()];
                    System.out.println(ChoosenLabel);

                    for (int i = 0; i < maxX; i++) {
                        for (int j = 0; j < maxY; j++) {
                            if (labeled[i][j] == ChoosenLabel) {
                                pixelWriter.setColor(j, i, choosenColor);
                            }

                        }
                    }
                }
            }
        });
        ivMain.setOnMouseDragged(new EventHandler<MouseEvent>() {

            public void handle(MouseEvent me1) {
                if (drawing && spreyclicked) {

                    PixelWriter pixelWriter = wImage.getPixelWriter();
                    pixelWriter.setColor((int) me1.getX(), (int) me1.getY(), choosenColor);
                    int thicknees = (int) setThicknees.getValue();
                    if (thicknees == 0) {
                        thicknees = 1;
                    }
                    for (int t = 1; t < (int) thicknees / 5; t++) {// in 4direction go at most 5 pixels
                        pixelWriter.setColor((int) me1.getX() - (1 * t), (int) me1.getY(), choosenColor);
                        pixelWriter.setColor((int) me1.getX() + (1 * t), (int) me1.getY(), choosenColor);
                        pixelWriter.setColor((int) me1.getX(), (int) me1.getY() - (1 * t), choosenColor);
                        pixelWriter.setColor((int) me1.getX(), (int) me1.getY() + (1 * t), choosenColor);
                        pixelWriter.setColor((int) me1.getX() - (1 * t), (int) me1.getY() - (1 * t), choosenColor);
                        pixelWriter.setColor((int) me1.getX() + (1 * t), (int) me1.getY() + (1 * t), choosenColor);
                        pixelWriter.setColor((int) me1.getX() - (1 * t), (int) me1.getY() - (1 * t), choosenColor);
                        pixelWriter.setColor((int) me1.getX() + (1 * t), (int) me1.getY() + (1 * t), choosenColor);
                        pixelWriter.setColor((int) me1.getX() - 2 - (1 * t), (int) me1.getY(), choosenColor);
                        pixelWriter.setColor((int) me1.getX() + 2 - (1 * t), (int) me1.getY(), choosenColor);
                        pixelWriter.setColor((int) me1.getX(), (int) me1.getY() - 2 - (1 * t), choosenColor);
                        pixelWriter.setColor((int) me1.getX(), (int) me1.getY() + 2 - (1 * t), choosenColor);
                        pixelWriter.setColor((int) me1.getX(), (int) me1.getY() - 2 - (1 * t), choosenColor);
                        pixelWriter.setColor((int) me1.getX(), (int) me1.getY() + 2 - (1 * t), choosenColor);
                        pixelWriter.setColor((int) me1.getX() - 2 - (1 * t), (int) me1.getY(), choosenColor);
                        pixelWriter.setColor((int) me1.getX() + 2 - (1 * t), (int) me1.getY(), choosenColor);

                    }
                }
                if (eraserclicked && drawing) {
                    PixelWriter pixelWriter = wImage.getPixelWriter();
                    pixelWriter.setColor((int) me1.getX(), (int) me1.getY(), Color.WHITE);
                    int thicknees = (int) setThicknees.getValue();
                    if (thicknees == 0) {
                        thicknees = 1;
                    }

                    pixelWriter.setColor((int) me1.getX() - (1), (int) me1.getY(), Color.WHITE);
                    pixelWriter.setColor((int) me1.getX() + (1), (int) me1.getY(), Color.WHITE);
                    pixelWriter.setColor((int) me1.getX(), (int) me1.getY() - (1), Color.WHITE);
                    pixelWriter.setColor((int) me1.getX(), (int) me1.getY() + (1), Color.WHITE);
                    pixelWriter.setColor((int) me1.getX() - (1), (int) me1.getY() - (1), Color.WHITE);
                    pixelWriter.setColor((int) me1.getX() + (1), (int) me1.getY() + (1), Color.WHITE);
                    pixelWriter.setColor((int) me1.getX() - (1), (int) me1.getY() - (1), Color.WHITE);
                    pixelWriter.setColor((int) me1.getX() + (1), (int) me1.getY() + (1), Color.WHITE);
                    pixelWriter.setColor((int) me1.getX() - 1 - (1), (int) me1.getY(), Color.WHITE);
                    pixelWriter.setColor((int) me1.getX() + 1 - (1), (int) me1.getY(), Color.WHITE);
                    pixelWriter.setColor((int) me1.getX(), (int) me1.getY() - 1 - (1), Color.WHITE);
                    pixelWriter.setColor((int) me1.getX(), (int) me1.getY() + 1 - (1), Color.WHITE);
                    pixelWriter.setColor((int) me1.getX() - 2 - (1), (int) me1.getY(), Color.WHITE);
                    pixelWriter.setColor((int) me1.getX() + 2 - (1), (int) me1.getY(), Color.WHITE);
                    pixelWriter.setColor((int) me1.getX(), (int) me1.getY() - 2 - (1), Color.WHITE);
                    pixelWriter.setColor((int) me1.getX(), (int) me1.getY() + 2 - (1), Color.WHITE);

                }

            }

        });
        sprey.setOnMouseClicked(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent event) {
                spreyclicked = true;
                drawreg = false;
                eraserclicked = false;
                drawing = true;
                File file = new File("src/edu/tr/mef/comp106/fxquickstart/palet12.png");
                if (file == null) {
                    return;
                }
                Image palette = new Image(file.toURI().toString());
                PixelReader pixelReader2 = palette.getPixelReader();
                choosenColor = pixelReader2.getColor((int) event.getX(), (int) event.getY());
                File corsor = new File("src/edu/tr/mef/comp106/fxquickstart/621366734.png");
                if (corsor == null) {
                    return;
                }
                Image ivcorsorr = new Image(corsor.toURI().toString());
                PixelReader pixelReader3 = ivcorsorr.getPixelReader();
                WritableImage wImage = new WritableImage((int) ivcorsorr.getWidth(), (int) ivcorsorr.getHeight());
                PixelWriter pixelWriter = wImage.getPixelWriter();
                for (int Y = 0; Y < ivcorsorr.getHeight(); Y++) {
                    for (int X = 0; X < ivcorsorr.getWidth(); X++) {
                        Color color = pixelReader3.getColor(X, Y);
                        // clean parazit  
                        double averageofpixelColor = (color.getBlue() + color.getGreen() + color.getRed()) * 255;
                        pixelWriter.setColor(X, Y, pixelReader3.getColor(X, Y));
                        if (averageofpixelColor / 3 > 200) {
                            pixelWriter.setColor(X, Y, choosenColor);
                        }
                    }
                }
                //
                MainApp.scene.setCursor(new ImageCursor(wImage));

            }
        });
        drawregt.setOnMouseClicked(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent event) {
                drawreg = true;
                spreyclicked = false;
                eraserclicked = false;
                drawing = true;
            }
        });
        eraser.setOnMouseClicked(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent event) {
                eraserclicked = true;
                spreyclicked = false;
                drawreg = false;
                drawing = true;
                File corsor = new File("src/edu/tr/mef/comp106/fxquickstart/eraser.png");
                if (corsor == null) {
                    return;
                }
                Image ivcorsorr = new Image(corsor.toURI().toString());
                PixelReader pixelReader3 = ivcorsorr.getPixelReader();
                //
                WritableImage wImage = new WritableImage((int) ivcorsorr.getWidth(), (int) ivcorsorr.getHeight());
                PixelWriter pixelWriter = wImage.getPixelWriter();
                for (int Y = 0; Y < ivcorsorr.getHeight(); Y++) {
                    for (int X = 0; X < ivcorsorr.getWidth(); X++) {

                        pixelWriter.setColor(X, Y, pixelReader3.getColor(X, Y));

                    }
                }
                //
                MainApp.scene.setCursor(new ImageCursor(wImage));
            }
        });
        information.setOnAction(Information);
    }
    
    void makeİtLabeled() {

        ds = new DisjointSet();
        int k = 0;
        for (int i = 1; i < maxX - 1; i++) {
            for (int j = 1; j < maxY - 1; j++) {
                if (pigments[i][j] == 1) {
                    if (pigments[i][j - 1] == 0 && pigments[i - 1][j - 1] == 0 && pigments[i - 1][j] == 0 && pigments[i - 1][j + 1] == 0) { // arrounded by blacks
                        k++;
                        ds.makeSet(k);
                        labeled[i][j] = k;
                    } //aroundedn by only one white
                    else if (pigments[i][j - 1] == 1 && pigments[i - 1][j - 1] == 0 && pigments[i - 1][j] == 0 && pigments[i - 1][j + 1] == 0) {
                        labeled[i][j] = labeled[i][j - 1];
                        ds.union(labeled[i][j], labeled[i][j - 1]);
                    } else if (pigments[i][j - 1] == 0 && pigments[i - 1][j - 1] == 1 && pigments[i - 1][j] == 0 && pigments[i - 1][j + 1] == 0) {
                        labeled[i][j] = labeled[i - 1][j - 1];
                        ds.union(labeled[i][j], labeled[i - 1][j - 1]);
                    } else if (pigments[i][j - 1] == 0 && pigments[i - 1][j - 1] == 0 && pigments[i - 1][j] == 1 && pigments[i - 1][j + 1] == 0) {
                        labeled[i][j] = labeled[i - 1][j];
                        ds.union(labeled[i][j], labeled[i - 1][j]);
                    } else if (pigments[i][j - 1] == 0 && pigments[i - 1][j - 1] == 0 && pigments[i - 1][j] == 0 && pigments[i - 1][j + 1] == 1) {
                        labeled[i][j] = labeled[i - 1][j + 1];
                        ds.union(labeled[i][j], labeled[i - 1][j + 1]);
                    } //aroundedn by 2 white
                    else if (pigments[i][j - 1] == 1 && pigments[i - 1][j - 1] == 1 && pigments[i - 1][j] == 0 && pigments[i - 1][j + 1] == 0) {
                        ds.union(labeled[i][j - 1], labeled[i - 1][j - 1]);

                        labeled[i][j] = labeled[i][j - 1];

                    } else if (pigments[i][j - 1] == 1 && pigments[i - 1][j - 1] == 0 && pigments[i - 1][j] == 1 && pigments[i - 1][j + 1] == 0) {
                        ds.union(labeled[i][j - 1], labeled[i - 1][j]);

                        labeled[i][j] = labeled[i][j - 1];

                    } else if (pigments[i][j - 1] == 1 && pigments[i - 1][j - 1] == 0 && pigments[i - 1][j] == 0 && pigments[i - 1][j + 1] == 1) {
                        ds.union(labeled[i][j - 1], labeled[i - 1][j + 1]);
                        labeled[i][j] = labeled[i][j - 1];

                    } else if (pigments[i][j - 1] == 0 && pigments[i - 1][j - 1] == 1 && pigments[i - 1][j] == 0 && pigments[i - 1][j + 1] == 1) {

                        ds.union(labeled[i - 1][j - 1], labeled[i - 1][j + 1]);

                        labeled[i][j] = labeled[i - 1][j - 1];

                    } else if (pigments[i][j - 1] == 0 && pigments[i - 1][j - 1] == 0 && pigments[i - 1][j] == 1 && pigments[i - 1][j + 1] == 1) {
                        ds.union(labeled[i - 1][j], labeled[i - 1][j + 1]);
                        labeled[i][j] = labeled[i - 1][j];

                    } else if (pigments[i][j - 1] == 0 && pigments[i - 1][j - 1] == 1 && pigments[i - 1][j] == 1 && pigments[i - 1][j + 1] == 0) {
                        ds.union(labeled[i - 1][j], labeled[i - 1][j - 1]);
                        labeled[i][j] = labeled[i - 1][j];

                    } // arounden by 3 white 
                    else if (pigments[i][j - 1] == 1 && pigments[i - 1][j - 1] == 1 && pigments[i - 1][j] == 1 && pigments[i - 1][j + 1] == 0) {
                        ds.union(labeled[i][j - 1], labeled[i - 1][j - 1]);
                        ds.union(labeled[i - 1][j], labeled[i - 1][j - 1]);
                        labeled[i][j] = labeled[i - 1][j - 1];

                    } else if (pigments[i][j - 1] == 1 && pigments[i - 1][j - 1] == 1 && pigments[i - 1][j] == 0 && pigments[i - 1][j + 1] == 1) {
                        ds.union(labeled[i][j - 1], labeled[i - 1][j - 1]);
                        ds.union(labeled[i - 1][j + 1], labeled[i - 1][j - 1]);
                        labeled[i][j] = labeled[i - 1][j - 1];
                    } else if (pigments[i][j - 1] == 1 && pigments[i - 1][j - 1] == 0 && pigments[i - 1][j] == 1 && pigments[i - 1][j + 1] == 1) {
                        ds.union(labeled[i][j - 1], labeled[i - 1][j]);
                        ds.union(labeled[i - 1][j + 1], labeled[i - 1][j]);
                        labeled[i][j] = labeled[i - 1][j];
                    } else if (pigments[i][j - 1] == 0 && pigments[i - 1][j - 1] == 1 && pigments[i - 1][j] == 1 && pigments[i - 1][j + 1] == 1) {
                        ds.union(labeled[i - 1][j - 1], labeled[i - 1][j]);
                        ds.union(labeled[i - 1][j + 1], labeled[i - 1][j]);
                        labeled[i][j] = labeled[i - 1][j];
                    } //arounden by 4 white
                    else if (pigments[i][j - 1] == 1 && pigments[i - 1][j - 1] == 1 && pigments[i - 1][j] == 1 && pigments[i - 1][j + 1] == 1) {
                        ds.union(labeled[i][j - 1], labeled[i - 1][j - 1]);
                        ds.union(labeled[i - 1][j], labeled[i - 1][j - 1]);
                        ds.union(labeled[i - 1][j], labeled[i - 1][j + 1]);
                        labeled[i][j] = labeled[i - 1][j];
                    }

                } else {
                    continue;
                }
            }
        }

    }

    void correctLabel() {

        for (int i = 1; i < maxX - 1; i++) {
            for (int j = 1; j < maxY - 1; j++) {
                if (pigments[i][j] == 1 && labeled[i][j] != 0) {

                    labeled[i][j] = (int) ds.findSet((long) labeled[i][j]);

                }
            }
        }
        for (int i = 0; i < maxX; i++) {
            for (int j = 0; j < maxY; j++) {
                if (labeled[i][j] != 0) {
                    if (labeled[i][j] < 10) {
                        System.out.print(labeled[i][j] );
                    } else if (labeled[i][j] < 100 && labeled[i][j] >= 10) {
                        System.out.print(labeled[i][j] );
                    } else if (labeled[i][j] >= 100) {
                        System.out.print(labeled[i][j]);
                    }
                } else {
                    System.out.print("*");
                }

            }
            System.out.println("");
        }
    }
    
    public EventHandler<ActionEvent> addNewImage;
    public EventHandler<ActionEvent> clickdelete;
    public EventHandler<ActionEvent> OpenNewPaper;
    public EventHandler<ActionEvent> cleanPage;
    public EventHandler<ActionEvent> SaveImage;
    public EventHandler<ActionEvent> Information;
}