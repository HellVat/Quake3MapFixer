/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package quake3mapfixer;
import java.io.File;
import net.lingala.zip4j.exception.ZipException;
import net.lingala.zip4j.core.ZipFile;
import java.io.IOException;

/**
 *
 * @author chrollo
 */
public class UnZipFile {
    
    public void unzip(String source, String destination, String dirName) throws ZipException, IOException{

        ZipFile zipFile = new ZipFile(source);
        String newDirName = destination + File.separatorChar + getFileName(dirName);
        //newDirName = newDirName.substring(0, newDirName.length() - 4);
        File file = new File(newDirName);
        file.delete();
        file.mkdirs();
        zipFile.extractAll(newDirName); 
    }
    
    public String getFileName(String dirName) {
        return dirName.substring(0, dirName.length() - 4) + "_f";
    }

}
    
 

