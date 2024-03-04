package msharp;

import javafx.application.Application;
import javafx.stage.Stage;

public class Main extends Application
{
    public static void main(String[] args) 
    {
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) throws Exception 
    {
        MusicManager mm = new MusicManager();
        mm.initFX(primaryStage);
    }
}
