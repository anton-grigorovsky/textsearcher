package ru.splattest.textsearcher.tools;

import javafx.application.Platform;
import javafx.scene.control.Label;
import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;


/**
 * Created by Антон on 18.08.2018.
 */
public class LoadIndicator extends Thread implements CounterObserver {
    private static final Logger LOGGER = LogManager.getLogger(LoadIndicator.class);
    private final String SEARCHING_FILES_TEXT = "Searching files";
    private final String PROCESS_FILES_TEXT = "Remain process files ";
    private boolean isFilesFound;
    private boolean isStopped;
    private Label label;
    private int foundFiles;
    private int handledFiles;


    public LoadIndicator(Label label) {
        this.label = label;
    }

    @Override
    public void run() {
        int point = 0;
        while (!isFilesFound && !isStopped)
        {
            if(point > 3)
            {
                point = 0;
            }
            final String msg = SEARCHING_FILES_TEXT + StringUtils.repeat(".", point++);
            Platform.runLater(() -> label.setText(msg));
            try
            {
                Thread.sleep(500);
            }
            catch (InterruptedException e)
            {
                LOGGER.error(e.getMessage());
            }
        }
        while (!isStopped)
        {
            final String msg = PROCESS_FILES_TEXT + handledFiles + "/" + foundFiles;
            Platform.runLater(() -> label.setText(msg));
            try {
                Thread.sleep(100);
            }
            catch (InterruptedException e)
            {
                LOGGER.error(e.getMessage());
            }
        }
    }

    @Override
    public void onStateChanged(int count) {
        if(!isFilesFound)
        {
            isFilesFound = true;
            foundFiles = count;
        }
        handledFiles = count;

    }

    public void show()
    {
        setDaemon(true);
        start();
    }


    public void hide()
    {
        isStopped = true;
        Platform.runLater(() ->
            label.setText(""));

    }

}
