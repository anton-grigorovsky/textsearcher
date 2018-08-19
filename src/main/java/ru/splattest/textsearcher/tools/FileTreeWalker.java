package ru.splattest.textsearcher.tools;

import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import ru.splattest.textsearcher.service.TextSearcherService;

import java.io.IOException;
import java.nio.file.*;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

/**
 * Created by Антон on 08.08.2018.
 */
public class FileTreeWalker {

    private static final Logger LOGGER = LogManager.getLogger(FileTreeWalker.class);
    private String sourcePath;
    private String format;
    private List<Path> filePaths;
    private boolean isStopped;

    public FileTreeWalker() {
        this.filePaths = new ArrayList<>();
    }

    public void startWalkTree(Consumer<List<Path>> callBack)
    {
        Path path = Paths.get(sourcePath);
        try
        {
            Files.walkFileTree(path, new FileVisitor());
        } catch (IOException e)
        {
            LOGGER.error("There are problems with walking file tree: " + e.getMessage());
        }
        if(!isStopped)
        {
            callBack.accept(filePaths);
        }
    }

    private class FileVisitor extends SimpleFileVisitor<Path> {

        @Override
        public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) throws IOException {
            PathMatcher matcher = FileSystems.getDefault().getPathMatcher("glob:**" + format);
            if (matcher.matches(file)) {
                filePaths.add(file);
            }
            if(!isStopped)
            {
                return FileVisitResult.CONTINUE;
            }
            else
            {
                return FileVisitResult.TERMINATE;
            }

        }

        @Override
        public FileVisitResult visitFileFailed(Path file, IOException exc) throws IOException {
            return FileVisitResult.SKIP_SUBTREE;
        }
    }

    public void setSourcePath(String sourcePath) {
        this.sourcePath = sourcePath;
    }

    public void setFormat(String format) {
        this.format = format;
    }

    public void stop()
    {
        isStopped = true;
    }

}
