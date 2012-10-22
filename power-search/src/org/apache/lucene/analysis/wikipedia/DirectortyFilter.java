package org.apache.lucene.analysis.wikipedia;

import java.io.File;

public class DirectortyFilter implements java.io.FileFilter {

    public boolean accept(File pathname) {
        if(pathname.isDirectory())    {
            return true;
        }
        return false;
    }

}
