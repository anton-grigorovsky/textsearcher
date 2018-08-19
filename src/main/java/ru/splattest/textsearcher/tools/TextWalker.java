package ru.splattest.textsearcher.tools;

import javafx.application.Platform;
import javafx.scene.control.TextArea;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Created by Антон on 12.08.2018.
 */
public class TextWalker {
    private final ExecutorService EXECUTOR_SERVICE = Executors.newSingleThreadExecutor( r -> {
        Thread t = Executors.defaultThreadFactory().newThread(r);
        t.setDaemon(true);
        return t;
    });
    private TextArea textArea;
    private String text;
    private int caret;

    public TextWalker(TextArea textArea) {
        this.textArea = textArea;

    }

    public void findNext(String text) {
        EXECUTOR_SERVICE.execute(() ->
        {
            checkText(text);
            int index = textArea.getText().indexOf(text, caret);
            if(index == caret && caret!= 0)
            {
                caret++;
                findNext(text);
                return;
            }
            if(index == -1)
            {
                caret = 0;
                findNext(text);
                return;
            }
            caret = index;
            Platform.runLater(() -> textArea.selectRange(index, index + text.length()));


        });
    }

    private void checkText(String str) {

        {
            if(text == null)
            {
                text = str;
            }
            else if(text.equals(str))
            {
                return;
            }
            else
            {
                caret = 0;
                text = str;
            }
        }
    }

    public void findPrevious(String text) {
        EXECUTOR_SERVICE.execute(() ->
        {
            int index = textArea.getText().lastIndexOf(text, caret);
            if(index == caret && caret!= 0)
            {
                caret--;
                findPrevious(text);
                return;
            }
            if(index == -1)
            {
                caret = textArea.getText().length();
                findPrevious(text);
                return;
            }
            caret = index;
            Platform.runLater(() -> textArea.selectRange(index, index + text.length()));
        });
    }
}
