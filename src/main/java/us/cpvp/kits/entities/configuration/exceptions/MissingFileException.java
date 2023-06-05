package us.cpvp.kits.entities.configuration.exceptions;

import java.io.File;

public class MissingFileException extends RuntimeException {
    private final String name;
    private final String path;

    public MissingFileException(String name, String path) {
        this.name = name;
        this.path = path;
    }

    public MissingFileException(File file) {
        this(file.getName(), file.getAbsolutePath());
    }

    @Override
    public String getMessage() {
        if (path == null || path.isBlank())
            return "Missing file named " + name;
        else return "Missing file named " + name + ". Expected at " + path;
    }

    public String name() {
        return name;
    }

    public String path() {
        return path;
    }
}
