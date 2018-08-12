package ru.splattest.textsearcher.tools;

import org.apache.commons.lang3.StringUtils;

import java.io.IOException;
import java.nio.file.*;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Антон on 08.08.2018.
 */
public class FileTreeWalker {

    private String sourcePath;
    private String format;
    private List<Path> filePaths;

    public FileTreeWalker() {
        this.filePaths = new ArrayList<>();
    }

    public List<Path> startWalkTree()
    {
        if(StringUtils.isEmpty(sourcePath) || StringUtils.isEmpty(format))
        {
            throw new RuntimeException("Set source path and file format");
        }
        Path path = Paths.get(sourcePath);
        try {
            Files.walkFileTree(path, new FileVisitor());
        } catch (IOException e) {
            e.printStackTrace();
        }

        return filePaths;
    }

    private class FileVisitor extends SimpleFileVisitor<Path> {

        @Override
        public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) throws IOException {
            PathMatcher matcher = FileSystems.getDefault().getPathMatcher("glob:**" + format);
            if (matcher.matches(file)) {
                filePaths.add(file);
            }
            return FileVisitResult.CONTINUE;
        }
    }

    public void setSourcePath(String sourcePath) {
        this.sourcePath = sourcePath;
    }

    public void setFormat(String format) {
        this.format = format;
    }
}
