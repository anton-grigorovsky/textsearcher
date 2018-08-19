package ru.splattest.textsearcher.service;

import javafx.application.Platform;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import ru.splattest.textsearcher.tools.FileTreeWalker;
import ru.splattest.textsearcher.tools.LoadIndicator;
import ru.splattest.textsearcher.tools.MatchFinder;
import ru.splattest.textsearcher.tools.MatchFinderSynchronizer;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.function.Consumer;

/**
 * Created by Антон on 09.08.2018.
 */
public class TextSearcherService {

    private static final Logger LOGGER = LogManager.getLogger(TextSearcherService.class);
    private final ExecutorService EXECUTOR_SERVICE = Executors.newFixedThreadPool(10, r -> {
            Thread t = Executors.defaultThreadFactory().newThread(r);
            t.setDaemon(true);
            return t;
        });
    private LoadIndicator indicator;
    private Consumer<List<Path>> callBack;
    private String text;
    private String path;
    private String format;
    private FileTreeWalker fileTreeWalker;

    public void findFilesWithText()
    {
        fileTreeWalker = new FileTreeWalker();
        fileTreeWalker.setSourcePath(path);
        fileTreeWalker.setFormat(format);
        indicator.show();
        EXECUTOR_SERVICE.execute(() -> fileTreeWalker.startWalkTree(this::filterFileList));
    }

    private void filterFileList(List<Path> files) {
        if(files.size() == 0)
        {
            indicator.hide();
            Platform.runLater(() -> {
                callBack.accept(Collections.emptyList());
            });
            return;
        }
        List<Path> filesWithText = new ArrayList<>();
        MatchFinderSynchronizer synchronizer = new MatchFinderSynchronizer(() ->
        {
            if(filesWithText.size() == 0)
            {
                indicator.hide();
            }
            Platform.runLater(() -> callBack.accept(filesWithText));
        }, files.size());
        synchronizer.registerObserver(indicator);
        files.forEach(f -> {
            EXECUTOR_SERVICE.execute(new MatchFinder.Builder().
                        setFile(f).
                        setSearchedText(text).
                        setCallBack(success -> {
                            if(success)
                            {
                                filesWithText.add(f);
                            }
                            synchronizer.countDown();
                        }).build());
            });
    }

    public void getText(Path path, Consumer<String> callBack)
    {
        EXECUTOR_SERVICE.execute(() ->
        {
            try {
                final String fileText = new String(Files.readAllBytes(path));
                Platform.runLater(() -> {
                    callBack.accept(fileText);
                });
            }
            catch (IOException e)
            {
                LOGGER.error("There are problems with reading file " + path.toString() + ": " + e.getMessage());
            }
        });
    }

    public void setCallBack(Consumer<List<Path>> callBack) {
        this.callBack = callBack;
    }

    public void setText(String text) {
        this.text = text;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public void setFormat(String format) {
        this.format = format;
    }

    public void setIndicator(LoadIndicator indicator) {
        this.indicator = indicator;
    }

    public void stop() {
        fileTreeWalker.stop();
        ((ThreadPoolExecutor) EXECUTOR_SERVICE).getQueue().clear();
        indicator.hide();
    }
}
