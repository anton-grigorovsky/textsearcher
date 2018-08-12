package ru.splattest.textsearcher.service;

import javafx.application.Platform;
import ru.splattest.textsearcher.tools.FileTreeWalker;
import ru.splattest.textsearcher.tools.ResultStatus;
import ru.splattest.textsearcher.tools.TextFinder;

import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.BrokenBarrierException;
import java.util.concurrent.CyclicBarrier;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.function.Consumer;

/**
 * Created by Антон on 09.08.2018.
 */
public class TextSearcherService {

    private static final ExecutorService EXECUTOR_SERVICE = Executors.newCachedThreadPool();

    public void findFilesWithText(String path, String format, String text, Consumer<List<Path>> callBack)
    {
        Platform.runLater(() -> {
            FileTreeWalker treeWalker = new FileTreeWalker();
            treeWalker.setSourcePath(path);
            treeWalker.setFormat(format);
            List<Path> files = treeWalker.startWalkTree();
            List<Path> filesWithText = new ArrayList<>();
            CyclicBarrier cyclicBarrier = new CyclicBarrier(files.size(), () ->
                    Platform.runLater(() -> callBack.accept(filesWithText)));

            files.forEach(f -> {
                EXECUTOR_SERVICE.execute(() -> {
                    TextFinder textFinder = new TextFinder.Builder().
                            setFile(f).
                            setSearchedText(text).
                            setCallBack(resultStatus -> {
                                if(resultStatus == ResultStatus.FOUND)
                                {
                                    filesWithText.add(f);
                                }
                                try {
                                    cyclicBarrier.await();
                                }
                                catch (Exception e)
                                {
                                }
                            }).build();
                    new Thread(textFinder).run();
                });
            });
        });
    }
}
