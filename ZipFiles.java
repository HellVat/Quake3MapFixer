/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package quake3mapfixer;

import java.io.File;
import net.lingala.zip4j.core.ZipFile;
import net.lingala.zip4j.model.ZipParameters;
import net.lingala.zip4j.util.Zip4jConstants;

/**
 *
 * @author chrollo
 */
public class ZipFiles {
    
    public void archiveDir(String zipPath, String zipName) {
        try {
            ZipFile zipFile = new ZipFile(zipPath + File.separatorChar + zipName + ".pk3");
            File fileDel = new File(zipPath + File.separatorChar + zipName + ".pk3");
            fileDel.delete();
            ZipParameters parameters = new ZipParameters();
            parameters.setCompressionMethod(Zip4jConstants.COMP_DEFLATE);
            parameters.setCompressionLevel(Zip4jConstants.DEFLATE_LEVEL_ULTRA);
            BSP_Fixer bf = new BSP_Fixer();
            File[] files;
            files = bf.filesFinder(zipPath + File.separatorChar + zipName);
            for (File file : files) {
                if(file.isDirectory()) {
                    zipFile.addFolder(file.getPath(), parameters);
                }
            }
        } catch (Exception e) {
         e.printStackTrace();
        }
    }
}
