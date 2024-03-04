package msharp;

// classes
import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;

import javafx.stage.FileChooser;
import javafx.stage.Stage;
import javafx.util.Duration;
import javafx.application.Platform;
import javafx.beans.InvalidationListener;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.paint.Color;

import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.Slider;
import javafx.scene.input.ClipboardContent;
import javafx.scene.input.Dragboard;
import javafx.scene.input.TransferMode;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundFill;
import javafx.scene.layout.VBox;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.text.Text;
//

public class MusicManager
{
    private MediaPlayer mediaPlayerPlayed;
    
    private String mediaPlayerPath;

    private List<String> mediaPlayerPathList;

    private String audioNameSingle = "";

    private List<String> audioNameInList = new ArrayList<>();

    public static int listMediaPlayerIndex = 0; // default

    private Slider slider;

    private boolean isPlayingAll = false;
    
    private boolean isFirstPlayWithList = false;

    /**
     * Kiểm tra có thay đổi index mỗi khi chuyển từ chạy single media sang list media
     */
    private boolean isNotChangeIndex = false;

    private boolean isEnded = false;
    /**
     * Phải đặt giá trị cho trường này trước các code khác
     */
    private boolean isOpeningFileChooserDialog = false;

    private Button addMediaBtn;
    
    private Button playOrPauseBtn;
    
    private Button loopBtn;

    private Button playAllBtn;

    private Label currentTimeMediaLabel;
    private Label maxTimeMediaLabel;

    private Label audioPlayingShowLabel;
    private Label audioNextPlayingLabel;
    private double widthCurrentLabel;

    private VBox vBoxContainItems;
    private VBox vBoxParent;
    private ScrollPane scrollPane;
    private float heightVBoxContainItems = PropertiesDefault.HEIGHT_CELL_DEFAULT;

    private ObservableList<String> itemsObsList = FXCollections.observableArrayList();
    private ListView<String> listView = new ListView<>(itemsObsList);

    private TimeMedia timeMedia = new TimeMedia();

    public void initFX(Stage mainStage)
    {
        // Tạo một Scene JavaFX với một Slider
        Scene scene = CreateScene(mainStage, PropertiesDefault.WIDTH_SCENE_DEFAULT, PropertiesDefault.HEIGHT_SCENE_DEFAULT);

        mainStage.setScene(scene);

        // Tắt thu phóng
        //mainStage.setResizable(false);

        mainStage.show();

        Platform.runLater(() -> 
        {
            maxTimeMediaLabel.setLayoutX(scene.getWidth() - maxTimeMediaLabel.getWidth() - PropertiesDefault.DISTANCE);
        
            playAllBtn.setVisible(false);
        });

        mainStage.setOnCloseRequest(event -> 
        {
            if (mediaPlayerPlayed != null)
            {
                mediaPlayerPlayed.dispose();
            }

            Platform.exit(); // clean
        });
    }

    private Scene CreateScene(Stage mainStage, float w, float h)
    {
        mainStage.setMinHeight(h);
        mainStage.setMinWidth(w);

        Group root = new Group();
        Scene scene = new Scene(root, w, h);
        scene.setFill(Color.rgb(40, 40, 40));

        mainStage.setOnShown(event ->
        {
            slider = GetSlider(0, 10, 1);

            slider.setPrefWidth(scene.getWidth());

            currentTimeMediaLabel = GetLabel(timeMedia.getCurrentTime(), "Palatino Linotype", PropertiesDefault.FONT_SIZE, Color.WHITE, null);
            maxTimeMediaLabel = GetLabel(timeMedia.getMaxTime(), "Palatino Linotype", PropertiesDefault.FONT_SIZE, Color.WHITE, null);

            audioPlayingShowLabel = GetLabel("", "Imprint MT Shadow", PropertiesDefault.FONT_SIZE * 2, GetInitWidthLabelTittle(), Color.WHITE, null);
            
            audioNextPlayingLabel = GetLabel("Next: ", "Imprint MT Shadow", PropertiesDefault.FONT_SIZE, GetInitWidthLabelTittle(), Color.WHITE, null);

            Button openBtn = GetButton("Open", PropertiesDefault.WIDTH_BTN_DEFAULT, PropertiesDefault.HEIGHT_BTN_DEFAULT);

            //#region Button action media
            playOrPauseBtn = GetButton("Pause", PropertiesDefault.WIDTH_BTN_DEFAULT, PropertiesDefault.HEIGHT_BTN_DEFAULT);

            addMediaBtn = GetButton("Add", PropertiesDefault.WIDTH_BTN_DEFAULT, PropertiesDefault.HEIGHT_BTN_DEFAULT);

            playAllBtn = GetButton("All", PropertiesDefault.WIDTH_BTN_DEFAULT, PropertiesDefault.HEIGHT_BTN_DEFAULT);

            loopBtn = GetButton("0", PropertiesDefault.HEIGHT_BTN_DEFAULT, PropertiesDefault.HEIGHT_BTN_DEFAULT);

            //SetImageBtn(GetResourcesPath() + "no_loop.png", loopBtn);

            //#endregion

            openBtn.setFont(GetFont("Rockwell Extra Bold", 17));
            addMediaBtn.setFont(GetFont("Rockwell Extra Bold", 17));
            playOrPauseBtn.setFont(GetFont("Rockwell Extra Bold", 17));
            playAllBtn.setFont(GetFont("Rockwell Extra Bold", 17));
            loopBtn.setFont(GetFont("Rockwell Extra Bold", 17));

            //#region event/action
            SetMouseClickedOpenBtn(openBtn, slider);
            SetMouseClickedAddBtn(addMediaBtn, slider);
            PlayOrPauseMediaBtn();
            ChangeLoopStatusBtn();
            PlayAllBtn();
            //#region

            //#region Chưa có thay đổi, mới chạy chương trình
            float initialSliderY = (float)(scene.getHeight() - (scene.getHeight() / PropertiesDefault.RATIO));

            slider.setPrefWidth(scene.getWidth() - PropertiesDefault.DISTANCE * 2);

            slider.setLayoutX((scene.getWidth() - (scene.getWidth() - PropertiesDefault.DISTANCE * 2)) / 2);
            slider.setLayoutY(initialSliderY);

            slider.setValue(0);

            openBtn.setLayoutX(PropertiesDefault.DISTANCE);
            openBtn.setLayoutY(PropertiesDefault.DISTANCE);

            addMediaBtn.setLayoutX(PropertiesDefault.DISTANCE);
            addMediaBtn.setLayoutY(PropertiesDefault.DISTANCE * 2 + PropertiesDefault.HEIGHT_BTN_DEFAULT);

            playOrPauseBtn.setLayoutX((scene.getWidth() - PropertiesDefault.WIDTH_BTN_DEFAULT) / 2f);
            playOrPauseBtn.setLayoutY(initialSliderY + PropertiesDefault.DISTANCE + 10);

            loopBtn.setLayoutX(((scene.getWidth() - PropertiesDefault.WIDTH_BTN_DEFAULT) / 2f) 
                            + PropertiesDefault.WIDTH_BTN_DEFAULT + PropertiesDefault.DISTANCE);
            
            loopBtn.setLayoutY(initialSliderY + PropertiesDefault.DISTANCE + 10);

            currentTimeMediaLabel.setLayoutX(PropertiesDefault.DISTANCE);
            currentTimeMediaLabel.setLayoutY(initialSliderY + PropertiesDefault.DISTANCE);

            maxTimeMediaLabel.setLayoutY(initialSliderY + PropertiesDefault.DISTANCE);

            audioPlayingShowLabel.setLayoutX(PropertiesDefault.DISTANCE);
            audioPlayingShowLabel.setLayoutY(initialSliderY - PropertiesDefault.DISTANCE * 2);

            audioNextPlayingLabel.setLayoutX(PropertiesDefault.DISTANCE);
            audioNextPlayingLabel.setLayoutY(initialSliderY - PropertiesDefault.DISTANCE * 4);

            //#endregion
            
            vBoxContainItems = new VBox();

            vBoxParent = new VBox();

            vBoxParent.setStyle("-fx-background-color: rgb(50, 50, 50); " + 
                                // Đặt bán kính cho viền cong
                                "-fx-background-radius: 5; " +
                                "-fx-border-radius: 5; " +
                                "-fx-border-color: black; " +
                                "-fx-border-width: 2; " +
                                "-fx-padding: 10;"); // Tăng khoảng cách giữa nội dung và viền

            double initialYVBoxParent = (PropertiesDefault.HEIGHT_SCENE_DEFAULT - (PropertiesDefault.HEIGHT_SCENE_DEFAULT / (PropertiesDefault.RATIO / 2)));

            double wVBoxParent = PropertiesDefault.WIDTH_SCENE_DEFAULT / (PropertiesDefault.RATIO / 2);
            double hVBoxParent = (initialYVBoxParent - PropertiesDefault.DISTANCE - 17);

            vBoxParent.setLayoutX(PropertiesDefault.WIDTH_SCENE_DEFAULT - (wVBoxParent + PropertiesDefault.DISTANCE));
            vBoxParent.setLayoutY(PropertiesDefault.DISTANCE);

            vBoxParent.setPrefWidth(wVBoxParent);
            vBoxParent.setPrefHeight(hVBoxParent);

            vBoxParent.getChildren().add(vBoxContainItems);

            playAllBtn.setLayoutX(scene.getWidth() - wVBoxParent 
                                - PropertiesDefault.WIDTH_BTN_DEFAULT 
                                - PropertiesDefault.DISTANCE * 2);

            playAllBtn.setLayoutY(initialYVBoxParent - 
                                  PropertiesDefault.HEIGHT_BTN_DEFAULT - 
                                  PropertiesDefault.DISTANCE);

            //#region Thêm listener, thực hiện hành động khi scene thay đổi w và h
            scene.heightProperty().addListener((obs, oldHeight, newHeight) ->
            {
                float ySl = newHeight.floatValue() - (newHeight.floatValue() / PropertiesDefault.RATIO);

                slider.setLayoutY(ySl);

                playOrPauseBtn.setLayoutY(ySl + PropertiesDefault.DISTANCE + 10);

                loopBtn.setLayoutY(playOrPauseBtn.getLayoutY());
                                                    
                currentTimeMediaLabel.setLayoutY(ySl + PropertiesDefault.DISTANCE);
                maxTimeMediaLabel.setLayoutY(ySl + PropertiesDefault.DISTANCE);

                audioPlayingShowLabel.setLayoutY(ySl - PropertiesDefault.DISTANCE * 2);

                audioNextPlayingLabel.setLayoutY(ySl - PropertiesDefault.DISTANCE * 4);


                double newYVBoxParent = (newHeight.doubleValue() - (newHeight.doubleValue() / (PropertiesDefault.RATIO / 2)));
                double newHVBoxParent = newYVBoxParent - PropertiesDefault.DISTANCE - 17;

                vBoxParent.setPrefHeight(newHVBoxParent);

                playAllBtn.setLayoutY(newYVBoxParent - 
                                    PropertiesDefault.HEIGHT_BTN_DEFAULT - 
                                    PropertiesDefault.DISTANCE);
            });

            scene.widthProperty().addListener((obs, oldWidth, newWidth) ->
            {
                slider.setPrefWidth(newWidth.doubleValue() - PropertiesDefault.DISTANCE * 2);

                slider.widthProperty().addListener((o, oldW, newW) ->
                {
                    slider.setLayoutX((newWidth.doubleValue() - slider.getWidth()) / 2);
                });

                playOrPauseBtn.setLayoutX((newWidth.doubleValue() - playOrPauseBtn.getWidth()) / 2f);

                loopBtn.setLayoutX(((scene.getWidth() - playOrPauseBtn.getWidth()) / 2f) 
                + playOrPauseBtn.getWidth() + PropertiesDefault.DISTANCE);

                //currentTimeMediaLabel.setLayoutX(PropertiesDefault.DISTANCE);
                maxTimeMediaLabel.setLayoutX(scene.getWidth() - maxTimeMediaLabel.getWidth() - PropertiesDefault.DISTANCE);

                //audioPlayingShowLabel.setLayoutX(PropertiesDefault.DISTANCE);
                
                double newWidthVBoxParent = newWidth.doubleValue() / (PropertiesDefault.RATIO / 2);
                double newXVBoxParent = newWidth.doubleValue() - (newWidthVBoxParent + PropertiesDefault.DISTANCE);
                
                vBoxParent.setLayoutX(newXVBoxParent);
                vBoxParent.setPrefWidth(newWidthVBoxParent);

                playAllBtn.setLayoutX(scene.getWidth() - newWidthVBoxParent 
                                    - PropertiesDefault.WIDTH_BTN_DEFAULT 
                                    - PropertiesDefault.DISTANCE * 2);
                                    
                widthCurrentLabel = GetUpdatedWidthLabelTittle(scene.getWidth());
                
                audioNextPlayingLabel.setPrefWidth(GetUpdatedWidthLabelTittle(scene.getWidth()));
                audioPlayingShowLabel.setPrefWidth(GetUpdatedWidthLabelTittle(scene.getWidth()));
            });
            //#endregion

            root.getChildren().add(slider);
            root.getChildren().add(openBtn);
            root.getChildren().add(playOrPauseBtn);
            root.getChildren().add(addMediaBtn);
            root.getChildren().add(loopBtn);
            root.getChildren().add(playAllBtn);

            root.getChildren().add(currentTimeMediaLabel);
            root.getChildren().add(maxTimeMediaLabel);

            root.getChildren().add(audioPlayingShowLabel);

            root.getChildren().add(audioNextPlayingLabel);

            root.getChildren().add(vBoxParent);
        });

        return scene;
    }

    private void SetMouseClickedOpenBtn(Button btn, Slider slider)
    {
        btn.setOnAction(event ->
        {
            if (!isOpeningFileChooserDialog)
            {
                isOpeningFileChooserDialog = true;
                ChooseFileAudio();
            }
        });
    }

    private void PlayOrPauseMediaBtn()
    {
        playOrPauseBtn.setOnAction(event ->
        {
            if (!isOpeningFileChooserDialog)
            {
                SetChangeStatusMedia();
            }
        });
    }

    private void ChangeLoopStatusBtn()
    {
        loopBtn.setOnAction(event ->
        {
            if (!isOpeningFileChooserDialog)
                LoopOptions();
        });        
    }

    private void PlayAllBtn()
    {
        playAllBtn.setOnAction(e ->
        {
            if (!isOpeningFileChooserDialog)
            {
                if (!isPlayingAll)
                {
                    isPlayingAll = true;
                    
                    isNotChangeIndex = true;

                    playAllBtn.setBackground(GetBackground(PropertiesDefault.COLOR_1));

                    if (!loopBtn.getText().equals("0") ||
                        !loopBtn.getText().equals("1"))
                    {
                        loopBtn.setText("0");
                        loopBtn.setBackground(GetBackground(Color.WHITE));
                    }

                    SetLabel(audioNextPlayingLabel, 
                    "Next: " + audioNameInList.get(listMediaPlayerIndex), 
                    "Imprint MT Shadow", PropertiesDefault.FONT_SIZE, widthCurrentLabel, Color.WHITE);
                }
                else
                {
                    isPlayingAll = false;

                    isNotChangeIndex = false;

                    playAllBtn.setBackground(GetBackground(Color.WHITE));

                    if (loopBtn.getText().equals("N") ||
                        loopBtn.getText().equals("R"))
                    {
                        loopBtn.setText("0");
                        loopBtn.setBackground(GetBackground(Color.WHITE));
                    }

                    SetLabel(audioNextPlayingLabel, 
                    "Next: " + audioNameSingle, 
                    "Imprint MT Shadow", PropertiesDefault.FONT_SIZE, widthCurrentLabel, Color.WHITE);
                }
            }
        });
    }

    private void SetChangeStatusMedia()
    {
        if (playOrPauseBtn.getText().equals("Play"))
        {
            playOrPauseBtn.setText("Pause");
            PauseMedia();
        }
        else if (playOrPauseBtn.getText().equals("Pause"))
        {
            playOrPauseBtn.setText("Play");
            PlayMedia();
        }
    }
    
    private void SetStartStatusMedia()
    {
        if (mediaPlayerPlayed != null)
        {
            mediaPlayerPlayed.statusProperty().addListener((obs, os, ns) -> 
            {
                if (os == MediaPlayer.Status.READY)
                {
                    double totalSeconds = mediaPlayerPlayed.getTotalDuration().toSeconds();
                    
                    slider.setMax(totalSeconds);
                    
                    timeMedia.SetStartTime(mediaPlayerPlayed);
                    
                    SetLabel(currentTimeMediaLabel, timeMedia.getCurrentTime(), "Palatino Linotype", PropertiesDefault.FONT_SIZE, Color.WHITE);
                    
                    SetLabel(maxTimeMediaLabel, timeMedia.getMaxTime(), "Palatino Linotype", PropertiesDefault.FONT_SIZE, Color.WHITE);                    
                }
            }); 
        }
    }

    private void TurnOffPlayAll()
    {
        /*if (!isPlayingAll)
        {
            if (!loopBtn.getText().equals("0") ||
                !loopBtn.getText().equals("1"))
            {
                loopBtn.setText("0");
                loopBtn.setBackground(GetBackground(Color.WHITE));
            }
        }
        else {}*/
        if (isPlayingAll)
        {
            isPlayingAll = false;

            playAllBtn.setBackground(GetBackground(Color.WHITE));

            if (loopBtn.getText().equals("N") ||
                loopBtn.getText().equals("R"))
            {
                loopBtn.setText("0");
                loopBtn.setBackground(GetBackground(Color.WHITE));
            }
        }
    }

    private void LoopOptions()
    {
        if (!isPlayingAll)
        {
            switch (loopBtn.getText()) 
            {
                case "0":        
                {
                    loopBtn.setText("1");
                    loopBtn.setBackground(GetBackground(PropertiesDefault.COLOR_2));
                    break;
                }
                case "1":        
                {
                    loopBtn.setText("0");
                    loopBtn.setBackground(GetBackground(Color.WHITE));
                    break;
                }                
            }
        }
        else
        {
            switch (loopBtn.getText()) 
            {
                case "0":  
                {
                    loopBtn.setText("N");
                    loopBtn.setBackground(GetBackground(PropertiesDefault.COLOR_1));  

                    break;              
                }
                case "N":  
                {
                    SetLabel(audioNextPlayingLabel, "Next: Random", 
                    "Imprint MT Shadow", PropertiesDefault.FONT_SIZE, widthCurrentLabel, Color.WHITE);

                    loopBtn.setText("R");
                    loopBtn.setBackground(GetBackground(PropertiesDefault.COLOR_2));  
                    break;              
                }
                case "R":  
                {
                    SetTextForNextAudioWithLoopOn();

                    loopBtn.setText("0");
                    loopBtn.setBackground(GetBackground(Color.WHITE));  
                    break;              
                }
            }
        }
    }

    private void PauseMedia()
    {
        if (mediaPlayerPlayed != null)
        {
            mediaPlayerPlayed.pause();
        }
    }

    private void SetMouseClickedAddBtn(Button addBtn, Slider slider)
    {
        addBtn.setOnAction(event ->
        {
            if (!isOpeningFileChooserDialog)
            {
                isOpeningFileChooserDialog = true;
                ChooseMultipleFileAudios(slider);
            }
        });
    }

    private void ChooseMultipleFileAudios(Slider slider)
    {
       FileChooser fileChooser = new FileChooser();

        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("Audio Files", "*.mp3", "*.wav"));
        //
        List<File> selectedFiles = fileChooser.showOpenMultipleDialog(null);

        // Kiểm tra xem người dùng đã chọn file hay chưa
        if (selectedFiles != null)
        {
            if (mediaPlayerPathList == null)
                mediaPlayerPathList = new ArrayList<>();

            for (File file : selectedFiles)
            {
                String nameWithoutExt = 
                file.getName().substring(0, file.getName().lastIndexOf('.'));

                if (!itemsObsList.contains(nameWithoutExt))
                {
                    String mediaSource = file.toURI().toString();
                    
                    /*Media media = new Media(mediaSource);
                    
                    MediaPlayer mediaPlayerSelected = new MediaPlayer(media);

                    mediaPlayerList.add(mediaPlayerSelected);*/
                    
                    mediaPlayerPathList.add(mediaSource);

                    itemsObsList.add(nameWithoutExt);

                    audioNameInList.add(nameWithoutExt);
                }
            }
            
            playAllBtn.setVisible(true);

            playAllBtn.setBackground(GetBackground(PropertiesDefault.COLOR_1));

            isPlayingAll = true;

            listView.setItems(itemsObsList);

            // fontCell = 15 -> cell.height = 25
            // 
            vBoxContainItems.setStyle("-fx-background-color: rgb(50, 50, 50); ");
            
            SetEventOnCell(listView);

            // chỉ add 1 lần
            if (!vBoxContainItems.getChildren().contains(listView))
                vBoxContainItems.getChildren().add(listView);

            vBoxContainItems.setPrefHeight(heightVBoxContainItems);

            scrollPane = new ScrollPane();
            scrollPane.setContent(vBoxContainItems);
        }
        // khi hộp thoại Open đóng
        isOpeningFileChooserDialog = false;
    }

    private void ChooseFileAudio()
    {
        FileChooser fileChooser = new FileChooser();
        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("Audio Files", "*.mp3", "*.wav"));

        File selectedFile = fileChooser.showOpenDialog(null);

        if (selectedFile != null)
        {
            if (mediaPlayerPlayed != null)
                mediaPlayerPlayed.dispose();

            isNotChangeIndex = true;

            TurnOffPlayAll();

            String mediaSource = selectedFile.toURI().toString();
            
            mediaPlayerPath = mediaSource; // data

            Media media = new Media(mediaSource);

            MediaPlayer mediaPlayer = new MediaPlayer(media);

            mediaPlayerPlayed = mediaPlayer;

            audioNameSingle = 
            selectedFile.getName().substring(0, selectedFile.getName().lastIndexOf('.'));

            StartMediaPlayerSingle();

            mediaPlayerPlayed.play(); // 1 lần

            playOrPauseBtn.setText("Play");
        }

        isOpeningFileChooserDialog = false;
    }

    private Slider GetSlider(float min, float max, float value)
    {
        Slider slider = new Slider(min, max, value);
        return slider;
    }

    private Label GetLabel(String text, String font, int fontSize, float w, Color textFillColor, Background bg)
    {
        Label label = new Label(text);
        
        label.setTextFill(textFillColor);
        
        label.setFont(GetFont(font, fontSize));
        
        label.setPrefWidth(w);

        widthCurrentLabel = w;

        label.setBackground(bg);
        
        return label;
    }
    
    private float GetInitWidthLabelTittle()
    {
        return PropertiesDefault.WIDTH_SCENE_DEFAULT - PropertiesDefault.DISTANCE;
    }    

    private double GetUpdatedWidthLabelTittle(double wScene)
    {
        return wScene - PropertiesDefault.DISTANCE;
    }  

    private Label GetLabel(String text, String font, int fontSize, Color textFillColor, Background bg)
    {
        Text textNode = new Text(text);
        
        textNode.setFill(textFillColor);

        textNode.setFont(GetFont(font, fontSize));

        double textWidth = textNode.getLayoutBounds().getWidth();

        Label label = new Label();
        
        label.setGraphic(textNode); // Sử dụng Text làm đồ họa cho Label

        label.setTextFill(textFillColor);
        
        label.setPrefWidth(textWidth);

        label.setBackground(bg);
        
        return label;
    }

    private Button GetButton(String text, float w, float h)
    {
        Button button = new Button(text);
        button.setPrefSize(w, h);
        return button;
    }

    private Background GetBackground(Color fill)
    {
        BackgroundFill bgf = new BackgroundFill(fill, PropertiesDefault.CORNNER_RADII_DEFAULT, null);
        Background bg = new Background(bgf);
        
        return bg;
    }

    public static Font GetFont(String fontText, double size)
    {
        Font font = Font.font(fontText, FontWeight.BOLD, size);

        return font;
    }

    private void SetLabel(Label label, String text, String font, double fontSize, double w, Color textFillColor)
    {
        label.setText(text);

        label.setTextFill(textFillColor);
        
        label.setFont(GetFont(font, fontSize));
        
        label.setPrefWidth(w);
    }

    private void SetLabel(Label label, String text, String font, float fontSize, Color textFillColor) 
    {
        Text textNode = new Text(text);

        textNode.setFill(textFillColor);

        textNode.setFont(GetFont(font, fontSize));

        double textWidth = textNode.getLayoutBounds().getWidth();

        label.setGraphic(textNode);

        label.setPrefWidth(textWidth);
    }

    /**
    size = 3 => size - 1 = 2; 
    index = 1 < 2; 
    index = 2 == 2 (last) => get(index = 0); 
    listMediaPlayerIndex đã tăng lên 1 mới xét
     */
    private void SetTextForNextAudioWithLoopOn()
    {
        if (listMediaPlayerIndex < mediaPlayerPathList.size() - 1)
        {
            if (mediaPlayerPathList.size() > 1)
            {
                SetLabel(audioNextPlayingLabel, 
                "Next: " + audioNameInList.get(listMediaPlayerIndex + 1), 
                "Imprint MT Shadow", PropertiesDefault.FONT_SIZE, widthCurrentLabel, Color.WHITE);                
            }
        }
        else
        {
            SetLabel(audioNextPlayingLabel, 
            "Next: " + audioNameInList.get(0), 
            "Imprint MT Shadow", PropertiesDefault.FONT_SIZE, widthCurrentLabel, Color.WHITE);
        }        
    }

    private void SetEventOnCell(ListView<String> listView)
    {
        heightVBoxContainItems = itemsObsList.size() * PropertiesDefault.HEIGHT_CELL_DEFAULT;  

        listView.setCellFactory(param -> 
        {
            ListCellWithDragSupport<String> cell = new ListCellWithDragSupport<String>();

            cell.setOnDragDetected(event -> 
            {
                if (cell.getItem() != null) 
                {   
                    Dragboard dragboard = cell.startDragAndDrop(TransferMode.MOVE);
                
                    ClipboardContent content = new ClipboardContent();
                
                    content.put(PropertiesDefault.SERIALIZED_MIME_TYPE, cell.getIndex());

                    dragboard.setContent(content);
                }
            });

            cell.setOnDragOver(event -> 
            {
                Dragboard dragboard = event.getDragboard();

                if (dragboard.hasContent(PropertiesDefault.SERIALIZED_MIME_TYPE)) 
                {
                    if (cell.getIndex() != (int)dragboard.getContent(PropertiesDefault.SERIALIZED_MIME_TYPE)) 
                    {
                        event.acceptTransferModes(TransferMode.MOVE);
                    }      
                }
            });

            cell.setOnDragDropped(event -> 
            {
                Dragboard dragboard = event.getDragboard();
            
                if (dragboard.hasContent(PropertiesDefault.SERIALIZED_MIME_TYPE)) 
                {
                    int draggedIndex = (Integer) dragboard.getContent(PropertiesDefault.SERIALIZED_MIME_TYPE);
                                        
                    ObservableList<String> itemsList = listView.getItems();
                    
                    int dropIndex;// = cell.isEmpty() ? itemsList.size() : cell.getIndex();
                    
                    if (cell.isEmpty())
                        dropIndex = itemsList.size();
                    else
                        dropIndex = cell.getIndex();
                                            
                    Collections.swap(itemsList, draggedIndex, dropIndex);

                    Collections.swap(mediaPlayerPathList, draggedIndex, dropIndex);

                    Collections.swap(audioNameInList, draggedIndex, dropIndex);

                    int nextMediaIndex;
                    
                    if (audioNameInList.size() > 1)
                    {
                        if (dropIndex == listMediaPlayerIndex)
                            nextMediaIndex = dropIndex;
                        else
                            nextMediaIndex = listMediaPlayerIndex + 1;
                    }
                    else
                        nextMediaIndex = listMediaPlayerIndex;

                    SetLabel(audioNextPlayingLabel, 
                    "Next: " + audioNameInList.get(nextMediaIndex), 
                    "Imprint MT Shadow", PropertiesDefault.FONT_SIZE, widthCurrentLabel, Color.WHITE);
                    
                    event.setDropCompleted(true);
            
                    // Cập nhật chiều cao của vBoxContainItems
                    vBoxContainItems.setPrefHeight(heightVBoxContainItems);
                }
            });
            
            return cell;
        });
    }

    private void PlayMedia()
    {
        if (isPlayingAll)
        {
            if (!isFirstPlayWithList)
            {
                isFirstPlayWithList = true;
                
                StartMediaPlayerInList();

                SetLabel(audioPlayingShowLabel, audioNameInList.get(listMediaPlayerIndex), 
                        "Imprint MT Shadow", PropertiesDefault.FONT_SIZE * 1.5f, widthCurrentLabel, Color.WHITE);

                if (audioNameInList.size() > 1)
                {
                    SetLabel(audioNextPlayingLabel, 
                            "Next: " + audioNameInList.get(listMediaPlayerIndex + 1), 
                            "Imprint MT Shadow", PropertiesDefault.FONT_SIZE, widthCurrentLabel, Color.WHITE);                    
                }
            }
        }

        if (isEnded)
        {
            isEnded = false;

            if (isPlayingAll)
            {
                StartMediaPlayerInList();

                SetLabel(audioPlayingShowLabel, audioNameInList.get(listMediaPlayerIndex), 
                    "Imprint MT Shadow", PropertiesDefault.FONT_SIZE * 1.5f, widthCurrentLabel, Color.WHITE);

                if (audioNameInList.size() > 1)
                {
                    SetLabel(audioNextPlayingLabel, 
                            "Next: " + audioNameInList.get(listMediaPlayerIndex + 1), 
                            "Imprint MT Shadow", PropertiesDefault.FONT_SIZE, widthCurrentLabel, Color.WHITE);
                }
            }
            else
            {
                StartMediaPlayerSingle();
            }
        }

        if (mediaPlayerPlayed != null)
            mediaPlayerPlayed.play();
    }

    private void DisposeMediaPrevious()
    {
        if (mediaPlayerPlayed != null)
        {
            mediaPlayerPlayed.dispose();
        }
    }

    private void StartMediaPlayerSingle()
    {
        InitSingleMedia();
    }

    private void StartMediaPlayerInList()
    {
        InitMediaFromListPath();
    }

    private void InitMediaFromListPath()
    {
        if (mediaPlayerPathList != null)
        {
            Media media = new Media(mediaPlayerPathList.get(listMediaPlayerIndex));
            
            MediaPlayer mediaPlayerSelected = new MediaPlayer(media);

            mediaPlayerPlayed = mediaPlayerSelected;

            SetLabel(audioPlayingShowLabel, audioNameInList.get(listMediaPlayerIndex), 
            "Imprint MT Shadow", PropertiesDefault.FONT_SIZE * 1.5f, widthCurrentLabel, Color.WHITE);

            SetStartStatusMedia();
            
            Listener();
        }
    }

    private void InitSingleMedia()
    {
        if (mediaPlayerPath != null && !mediaPlayerPath.isEmpty())
        {
            Media media = new Media(mediaPlayerPath);
            
            MediaPlayer mediaPlayerSelected = new MediaPlayer(media);

            mediaPlayerPlayed = mediaPlayerSelected;

            SetStartStatusMedia();
            
            String nameWithoutExt = audioNameSingle;

            SetLabel(audioPlayingShowLabel, nameWithoutExt,"Imprint MT Shadow", PropertiesDefault.FONT_SIZE * 1.5f, widthCurrentLabel, Color.WHITE);
            
            Listener();
        }
    }

    private void Listener()
    {
        if (mediaPlayerPlayed != null)
        {
            // Tạo một listener cho currentTimeProperty
            InvalidationListener timeListener =
            (obs) ->
            {
                slider.setValue(mediaPlayerPlayed.getCurrentTime().toSeconds()); // thời gian hiện tại của media
                timeMedia.SetTimeUpdated(mediaPlayerPlayed);
                
                if (!playOrPauseBtn.getText().equals("Pause"))
                    SetLabel(currentTimeMediaLabel, timeMedia.getCurrentTime(), "Palatino Linotype", PropertiesDefault.FONT_SIZE, Color.WHITE);

                mediaPlayerPlayed.setOnEndOfMedia(() ->
                {
                    OptionsReplay();

                    if (slider.getValue() != slider.getMax())
                    {
                        slider.setValue(slider.getMax());
                    }
                });
            };
            
            mediaPlayerPlayed.currentTimeProperty().addListener(timeListener);
            
            slider.setOnMousePressed(event ->
            {
                mediaPlayerPlayed.currentTimeProperty().removeListener(timeListener);
            });
            slider.setOnMouseReleased(event ->
            {
                mediaPlayerPlayed.currentTimeProperty().addListener(timeListener);
                mediaPlayerPlayed.seek(Duration.seconds(slider.getValue()));
            });
        }
    }

    private void OptionsReplay()
    {
        if (isPlayingAll)
        {
            switch (loopBtn.getText()) 
            {
                case "0":
                {
                    if (listMediaPlayerIndex < mediaPlayerPathList.size() - 1) 
                    {
                        DisposeMediaPrevious();

                        if (!isNotChangeIndex)
                        {
                            listMediaPlayerIndex++;
                        }

                        ReplayNewMedia();
                    }
                    else
                    {
                        DisposeMediaPrevious();

                        isEnded = true;
                        listMediaPlayerIndex = 0;
                        playOrPauseBtn.setText("Pause");
                    }
                    
                    // size = 3 => size - 1 = 2
                    // index = 1 < 2;
                    // index = 2 == 2 (last) => get(index = 0)
                    // listMediaPlayerIndex đã tăng lên 1 mới xét
                    if (listMediaPlayerIndex < mediaPlayerPathList.size() - 1 &&
                        listMediaPlayerIndex != 0)
                    {
                        if (audioNameInList.size() > 1)
                        {
                            SetLabel(audioNextPlayingLabel, 
                            "Next: " + audioNameInList.get(listMediaPlayerIndex + 1), 
                            "Imprint MT Shadow", PropertiesDefault.FONT_SIZE, widthCurrentLabel, Color.WHITE);                            
                        }
                    }
                    else
                    {
                        SetLabel(audioNextPlayingLabel, 
                        "Next: " + audioNameInList.get(0), 
                        "Imprint MT Shadow", PropertiesDefault.FONT_SIZE, widthCurrentLabel, Color.WHITE);
                    }

                    break;
                }
                case "N": 
                {
                    DisposeMediaPrevious();

                    if (listMediaPlayerIndex < mediaPlayerPathList.size() - 1)
                    {
                        listMediaPlayerIndex++;
                    }
                    else
                        listMediaPlayerIndex = 0;
                   
                    SetLabel(audioPlayingShowLabel, audioNameInList.get(listMediaPlayerIndex), 
                    "Imprint MT Shadow", PropertiesDefault.FONT_SIZE * 1.5f, widthCurrentLabel, Color.WHITE);

                    SetTextForNextAudioWithLoopOn();

                    ReplayNewMedia();
                    
                    break;
                }
                case "R":
                {
                    DisposeMediaPrevious();

                    Random rand = new Random();
                    listMediaPlayerIndex = rand.nextInt(mediaPlayerPathList.size()); // [0, size)

                    ReplayNewMedia();

                    SetLabel(audioPlayingShowLabel, audioNameInList.get(listMediaPlayerIndex), 
                    "Imprint MT Shadow", PropertiesDefault.FONT_SIZE * 1.5f, widthCurrentLabel, Color.WHITE);
                    break;
                }
            }
        }
        else
        {
            switch (loopBtn.getText()) 
            {
                case "0":
                {
                    DisposeMediaPrevious();
                    isEnded = true;
                    playOrPauseBtn.setText("Pause");
                    break;
                }
                case "1": 
                {
                    DisposeMediaPrevious();
                    ReplayMediaCurrent();
                    break;
                }
            }  
        }
    }

    private void ReplayNewMedia()
    {
        StartMediaPlayerInList();

        mediaPlayerPlayed.seek(mediaPlayerPlayed.getStartTime());
        mediaPlayerPlayed.play();
    }

    private void ReplayMediaCurrent()
    {
        StartMediaPlayerSingle();

        mediaPlayerPlayed.seek(mediaPlayerPlayed.getStartTime());
        mediaPlayerPlayed.play();        
    }
}


class ListCellWithDragSupport<T> extends ListCell<T>            
{
    @Override
    public void updateItem(T item, boolean empty) 
    {
        super.updateItem(item, empty);

        if (item != null) 
        {
            setText(item.toString());
            setPadding(new Insets(10));

            setStyle("-fx-text-fill: white; " +
            "-fx-font-family: Arial; " +
            "-fx-font-size: 15;" +
            "-fx-background: rgb(50, 50, 50);");
        } 
    }
}