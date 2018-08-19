package ru.splattest.textsearcher.utils;

import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.geometry.Point2D;
import javafx.scene.Scene;
import javafx.scene.control.TextField;
import javafx.scene.control.Tooltip;
import javafx.util.Duration;
import org.apache.commons.lang3.StringUtils;

import java.io.File;
import java.lang.reflect.Field;

/**
 * Created by Антон on 19.08.2018.
 */
public class ValidationUtils
{
    private static final String FILE_LOCATION_PATTERN = "([A-Z|a-z]:\\\\?[^*|\"<>?\\n]*)|(\\\\\\\\[^*|\"<>?\\n]*?(\\\\.)*)";
    private static final String FORMAT_PATTERN = "[A-Za-z0-9]+";

    public static boolean isPathValid(TextField textField)
    {
        String text = textField.getText();
        if (!text.matches(FILE_LOCATION_PATTERN))
        {
            showToolTip("Invalid source path.", textField);
            return false;
        }
        File file = new File(text);
        if (!file.exists() || !file.isDirectory())
        {
            showToolTip("There is no directory by this path." , textField);
            return false;
        }
        return true;
    }

    public static boolean isTextValid(TextField textField)
    {
        if(StringUtils.isEmpty(textField.getText()))
        {
            showToolTip("Please, enter searched text", textField);
            return false;
        }
        return true;
    }

    public static boolean isFormatValid(TextField textField)
    {
        if(!textField.getText().matches(FORMAT_PATTERN))
        {
            showToolTip("Please, enter correct file format", textField);
            return false;
        }
        return true;
    }

    private static void showToolTip(String text, TextField textField)
    {
        Tooltip tooltip = new Tooltip(text);
        textField.setTooltip(tooltip);
        Point2D p = textField.localToScene(0.0, 0.0);
        textField.getTooltip().show(textField,
                p.getX() + textField.getScene().getWindow().getX(),
                p.getY() + textField.getScene().getWindow().getY());
        tooltip.show(textField.getScene().getWindow());
        Timeline timeline = new Timeline(new KeyFrame(new Duration(3000), e ->
        {
            tooltip.hide();
            textField.setTooltip(null);
        }));
        timeline.setCycleCount(1);
        timeline.play();
    }
}
