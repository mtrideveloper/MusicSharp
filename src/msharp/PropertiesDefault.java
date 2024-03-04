package msharp;

import javafx.scene.input.DataFormat;
import javafx.scene.paint.Color;
import javafx.scene.layout.CornerRadii;

public class PropertiesDefault 
{
    public final static float WIDTH_SCENE_DEFAULT = 900;    
    public final static float HEIGHT_SCENE_DEFAULT = 500;    

    public final static float WIDTH_BTN_DEFAULT = 90;    
    public final static float HEIGHT_BTN_DEFAULT = 40;  
    
    public final static Color COLOR_1 = Color.rgb(175,238,238);
    public final static Color COLOR_2 = Color.rgb(255,255,224);

    public final static CornerRadii CORNNER_RADII_DEFAULT = new CornerRadii(5);

    public final static float RATIO = 6;

    public final static float DISTANCE = 20;

    public final static int FONT_SIZE = 16;

    public final static DataFormat SERIALIZED_MIME_TYPE = new DataFormat("application/x-java-serialized-object");

    /**
     * Điều kiện: fontSize = 15 và setPadding(new Insets(10));
     */
    public final static float HEIGHT_CELL_DEFAULT = 37.5f;
}
