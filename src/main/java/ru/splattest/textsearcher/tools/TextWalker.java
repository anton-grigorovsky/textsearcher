package ru.splattest.textsearcher.tools;

import javafx.application.Platform;
import javafx.scene.control.TextArea;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
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
    private final List<String> BUFFER;
    private TextArea textArea;
    private String text;
    private int caret = -1;
    private Path file;



    public TextWalker(Path file, String text, TextArea textArea) {
        this.file = file;
        this.textArea = textArea;
        this.text = text;
        BUFFER = new ArrayList<>();
        init();
    }

    private void init()
    {
        EXECUTOR_SERVICE.execute(() ->
        {
            try {
                BufferedReader bufferedReader = new BufferedReader(new FileReader(file.toFile()));
                String line = null;
                int lineNum = 0;
                while ((line = bufferedReader.readLine()) != null)
                {
                    lineNum++;
                    if(line.toLowerCase().contains(text.toLowerCase()))
                    {
                        BUFFER.add("line " + lineNum + ": " + line);
                    }
                }
            }
            catch (IOException e)
            {}
            findNext();
        });
    }


    public void findNext() {
        Platform.runLater(() ->
        {
            caret++;
            if(caret >= BUFFER.size() - 1)
            {
                caret = 0;
            }
            String line = BUFFER.get(caret);
            textArea.setText(line);
            int index = line.indexOf(text);
            textArea.selectRange(index, index + text.length());
        });
    }


    public void findPrevious() {
        Platform.runLater(() ->
        {
            caret--;
            if(caret < 0)
            {
                caret = BUFFER.size() - 1;
            }
            String line = BUFFER.get(caret);
            textArea.setText(line);
            int index = line.indexOf(text);
            textArea.selectRange(index, index + text.length());
        });
    }
}
