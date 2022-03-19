package com.imagefinder;

import java.io.File;
import java.io.Serializable;

public class Model implements Serializable {
    public int width = 800;
    public int height = 600;
    public int x = 50;
    public int y = 50;
    public String currentDirectory;
    public File[] openFiles;
}
