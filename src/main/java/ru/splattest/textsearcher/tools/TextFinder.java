package ru.splattest.textsearcher.tools;

import org.apache.commons.lang3.StringUtils;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.function.Consumer;

/**
 * Created by Антон on 08.08.2018.
 */
public class TextFinder implements Runnable {

    private Consumer<ResultStatus> callBack;
    private Path file;
    private String searchedText;
    private String fileText;

    private TextFinder(Consumer<ResultStatus> callBack, Path file, String searchedText) {
        this.callBack = callBack;
        this.file = file;
        this.searchedText = searchedText;
    }

    @Override
    public void run() {
        fileText = readFile(file);
        String reworked = fileText.toLowerCase();
        ResultStatus status = ResultStatus.NOT_FOUND;
        if(StringUtils.isNotEmpty(fileText))
        {
            status = reworked.contains(searchedText.toLowerCase()) ? ResultStatus.FOUND : ResultStatus.NOT_FOUND;
        }

        callBack.accept(status);
    }

    private String readFile(Path path)
    {
        String fileText = null;
        try {
            fileText = new String(Files.readAllBytes(path));
        }
        catch (IOException e)
        {

        }
        return fileText;
    }

    public static class Builder
    {
        private Consumer<ResultStatus> callBack;
        private Path file;
        private String searchedText;

        public Builder setCallBack(Consumer<ResultStatus> callBack) {
            this.callBack = callBack;
            return this;
        }

        public Builder setFile(Path file) {
            this.file = file;
            return this;
        }

        public Builder setSearchedText(String searchedText) {
            this.searchedText = searchedText;
            return this;
        }

        public TextFinder build() {
            return new TextFinder(callBack, file, searchedText);
        }

    }


}
