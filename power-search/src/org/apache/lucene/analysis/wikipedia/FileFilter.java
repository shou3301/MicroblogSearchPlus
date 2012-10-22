package org.apache.lucene.analysis.wikipedia;

import java.io.File;

public class FileFilter implements java.io.FileFilter {
	
	public boolean accept(File pathname) {
        if(pathname.isFile())    {
            return true;
        }
        return false;
    
	}
}
