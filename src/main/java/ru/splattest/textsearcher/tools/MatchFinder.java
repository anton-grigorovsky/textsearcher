package ru.splattest.textsearcher.tools;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.nio.file.Path;
import java.util.function.Consumer;

/**
 * Created by Антон on 08.08.2018.
 */
public class MatchFinder implements Runnable {

    private static final Logger LOGGER = LogManager.getLogger(MatchFinder.class);
    private Consumer<Boolean> callBack;
    private Path file;
    private String searchedText;

    private MatchFinder(Consumer<Boolean> callBack, Path file, String searchedText) {
        this.callBack = callBack;
        this.file = file;
        this.searchedText = searchedText;
    }

    @Override
    public void run() {
        Boolean status = false;
        try {

            status = readFile();
        }
        catch (Exception e) {
            LOGGER.error("There are problems with reading file " + file.toString() + ": " + e.getMessage());
        }
        finally {
            callBack.accept(status);
        }
    }

    private boolean readFile() throws IOException
    {
        BufferedReader in = new BufferedReader(new FileReader(file.toFile()));
        String line = null;
        while ((line = in.readLine()) != null)
        {
            if(line.toLowerCase().contains(searchedText.toLowerCase())) {
                return true;
            }
        }

        return false;
    }

    public static class Builder
    {
        private Consumer<Boolean> callBack;
        private Path file;
        private String searchedText;

        public Builder setCallBack(Consumer<Boolean> callBack) {
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

        public MatchFinder build() {
            return new MatchFinder(callBack, file, searchedText);
        }

    }


}
