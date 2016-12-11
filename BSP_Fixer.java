/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package quake3mapfixer;

import java.awt.Color;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.io.File;
import java.io.FilenameFilter;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.UIManager;

/**
 *
 * @author chrollo
 */
public class BSP_Fixer {
    public static final byte SPACE = (byte) ' ';
    public static final byte[] START_TIMER = "\"classname\" \"target_startTimer\"".getBytes(StandardCharsets.UTF_8);
    public static final byte[] STOP_TIMER = "\"classname\" \"target_stopTimer\"".getBytes(StandardCharsets.UTF_8);
    public static final byte[] TRIGGER_MULTPLE = "\"classname\" \"trigger_multiple\"".getBytes(StandardCharsets.UTF_8);
    public static final byte[] TARGET_PRINT = "\"classname\" \"target_print\"".getBytes(StandardCharsets.UTF_8);
    public static final byte[] TARGET_SMALLPRINT = "\"classname\" \"target_smallprint\"".getBytes(StandardCharsets.UTF_8);
    public static final byte[] TARGET_NAME = "\"targetname\"".getBytes(StandardCharsets.UTF_8);
    public static final byte[] TARGET = "\"target\" \"".getBytes(StandardCharsets.UTF_8);
    public static final byte[] WAIT = "\"wait\"".getBytes(StandardCharsets.UTF_8);
    public static final byte OPEN_BRACE = (byte) '{';
    public static final byte CLOSE_BRACE = (byte) '}';
    private boolean isDeletingMessages;

    public void setIsDeletingMessages(boolean isDeletingMessages) {
        this.isDeletingMessages = isDeletingMessages;
    }
    
    public File[] bspFinder(String dirName){
    	File dir = new File(dirName);

    	return dir.listFiles(new FilenameFilter() { 
                     @Override
    	         public boolean accept(File dir, String filename)
    	              { return filename.endsWith(".bsp"); }
    	} );

    }
    
    public File[] filesFinder(String dirName){
    	File dir = new File(dirName);
    	return dir.listFiles();
    }
    
    public void bspFilesFixing(String currentDirectory, String absolutePath) throws IOException {
        String path = currentDirectory + File.separator + absolutePath + File.separator + "maps";
        File[] listOfFiles;
        listOfFiles = bspFinder(path);
        for (File listOfFile : listOfFiles) {
            if (listOfFile.isFile()) {
                bspFixer(listOfFile.getPath());
            } 
        }   
    }
    
    public String getAbsoluthPath(String bspPath) {
        int beginIndex = 0;
        for(int i = bspPath.length() - 1; i >= 0; i--) {
            if(bspPath.charAt(i) == File.separatorChar) {
                beginIndex = i + 1;
                break;
            }
        }
        return bspPath.substring(beginIndex);
    }
    
    public void bspFixer(String bspPath) throws IOException {
        boolean isFixedStartTimer = false;
        boolean isFixedStopTimer = false;
        int countDeletedMessages = 0;
        byte[] data;
        data = Files.readAllBytes(Paths.get(bspPath));
        KMPMatch kmp = new KMPMatch();
        int indexOfPattern = kmp.indexOf(data, START_TIMER);
        
        /*------------------------Fixing startTimer---------------------------*/
        
        if(indexOfPattern != -1) {
            int indexOfOpenBrace = indexOfPattern, indexOfCloseBrace = indexOfPattern;
            for(int i = indexOfPattern; i >= 0; i--) {
                if(data[i] == OPEN_BRACE) {
                    indexOfOpenBrace = i;
                    break;
                }
            }
            for(int i = indexOfPattern; i < data.length; i++) {
                if(data[i] == CLOSE_BRACE) {
                    indexOfCloseBrace = i;
                    break;
                }
            }
            for(int i = indexOfOpenBrace + 1; i < indexOfCloseBrace; i++) {
                if(data[i] == WAIT[0] && data[i + 1] == WAIT[1] && data[i + 2] == WAIT[2] &&
                        data[i + 3] == WAIT[3] && data[i + 4] == WAIT[4] && data[i + 5] == WAIT[5]) {
                    int j = i;
                    while(data[j] != (byte)'\n') {
                        data[j++] = SPACE;
                    }
                    isFixedStartTimer = true;
                    break;
                }
            }
            List<Byte> targetNameList;
            targetNameList = new ArrayList<>();
            boolean isTargetName = false;
            for(int i = indexOfOpenBrace; i < indexOfCloseBrace; i++) {
                if(data[i] == TARGET_NAME[0] && data[i + 1] == TARGET_NAME[1] && data[i + 2] == TARGET_NAME[2] &&
                        data[i + 3] == TARGET_NAME[3] && data[i + 4] == TARGET_NAME[4] && data[i + 5] == TARGET_NAME[5] &&
                        data[i + 6] == TARGET_NAME[6] && data[i + 7] == TARGET_NAME[7] && data[i + 8] == TARGET_NAME[8] &&
                        data[i + 9] == TARGET_NAME[9] && data[i + 10] == TARGET_NAME[10] && data[i + 11] == TARGET_NAME[11]) {
                    isTargetName = true;
                    int j = i + 11;
                    while(data[j] != SPACE) {
                        j++;
                    }
                    while(data[j] != (byte)'\"') {
                        j++;
                    }
                    j++;
                    while(data[j] != (byte)'\"') {
                        targetNameList.add(data[j++]);
                    }
                    break;
                }
            }
            if(isTargetName) {
                List<Byte> targetList;
                targetList = new ArrayList<>();
                for(int i = 0; i < TARGET.length; i++) {
                    targetList.add(TARGET[i]);
                }
                for(int i = 0; i < targetNameList.size(); i++){
                    targetList.add(targetNameList.get(i));
                }
                targetList.add((byte) '\"');
                byte[] target = new byte[targetList.size()];
                int k = 0;
                for (Byte b : targetList) {
                    target[k++] = b;
                }
                int indexOfTarget = kmp.indexOf(data, target);
                for(int i = indexOfTarget; i >= 0; i--) {
                    if(data[i] == OPEN_BRACE) {
                        indexOfOpenBrace = i;
                        break;
                    }
                }
                for(int i = indexOfTarget; i < data.length; i++) {
                    if(data[i] == CLOSE_BRACE) {
                        indexOfCloseBrace = i;
                        break;
                    }
                }
                for(int i = indexOfOpenBrace + 1; i < indexOfCloseBrace; i++) {
                    if(data[i] == TRIGGER_MULTPLE[0] && data[i + 1] == TRIGGER_MULTPLE[1] && data[i + 2] == TRIGGER_MULTPLE[2] &&
                            data[i + 3] == TRIGGER_MULTPLE[3] && data[i + 4] == TRIGGER_MULTPLE[4] && data[i + 5] == TRIGGER_MULTPLE[5] &&
                            data[i + 6] == TRIGGER_MULTPLE[6] && data[i + 7] == TRIGGER_MULTPLE[7] && data[i + 8] == TRIGGER_MULTPLE[8] &&
                            data[i + 9] == TRIGGER_MULTPLE[9] && data[i + 10] == TRIGGER_MULTPLE[10] && data[i + 11] == TRIGGER_MULTPLE[11] &&
                            data[i + 12] == TRIGGER_MULTPLE[12] && data[i + 13] == TRIGGER_MULTPLE[13] && data[i + 14] == TRIGGER_MULTPLE[14] &&
                            data[i + 15] == TRIGGER_MULTPLE[15] && data[i + 16] == TRIGGER_MULTPLE[16] && data[i + 17] == TRIGGER_MULTPLE[17] &&
                            data[i + 18] == TRIGGER_MULTPLE[18] && data[i + 19] == TRIGGER_MULTPLE[19] && data[i + 20] == TRIGGER_MULTPLE[20] &&
                            data[i + 21] == TRIGGER_MULTPLE[21] && data[i + 22] == TRIGGER_MULTPLE[22] && data[i + 23] == TRIGGER_MULTPLE[23] &&
                            data[i + 24] == TRIGGER_MULTPLE[24] && data[i + 25] == TRIGGER_MULTPLE[25] && data[i + 26] == TRIGGER_MULTPLE[26] &&
                            data[i + 27] == TRIGGER_MULTPLE[27] && data[i + 28] == TRIGGER_MULTPLE[28] && data[i + 29] == TRIGGER_MULTPLE[29]) {
                        for(int j = indexOfOpenBrace + 1; j < indexOfCloseBrace; j++) {
                            if(data[j] == WAIT[0] && data[j + 1] == WAIT[1] && data[j + 2] == WAIT[2] &&
                                data[j + 3] == WAIT[3] && data[j + 4] == WAIT[4] && data[j + 5] == WAIT[5]) {
                                int l = j;
                                while(data[l] != (byte)'\n') {
                                    data[l++] = SPACE;
                                }
                                isFixedStartTimer = true;
                                break;
                            }
                        }
                    }
                }
                  
            }
            targetNameList.clear();
            /*----------------------------------------------------------------*/
            
            
            /*-----------------------Fixing stopTimer-------------------------*/
            indexOfPattern = kmp.indexOf(data, STOP_TIMER);
            indexOfOpenBrace = indexOfPattern;
            indexOfCloseBrace = indexOfPattern;
            for(int i = indexOfPattern; i >= 0; i--) {
                if(data[i] == OPEN_BRACE) {
                    indexOfOpenBrace = i;
                    break;
                }
            }
            for(int i = indexOfPattern; i < data.length; i++) {
                if(data[i] == CLOSE_BRACE) {
                    indexOfCloseBrace = i;
                    break;
                }
            }
            for(int i = indexOfOpenBrace + 1; i < indexOfCloseBrace; i++) {
                if(data[i] == WAIT[0] && data[i + 1] == WAIT[1] && data[i + 2] == WAIT[2] &&
                        data[i + 3] == WAIT[3] && data[i + 4] == WAIT[4] && data[i + 5] == WAIT[5]) {
                    int j = i;
                    while(data[j] != (byte)'\n') {
                        data[j++] = SPACE;
                    }
                    isFixedStopTimer = true;
                    break;
                }
            }
            isTargetName = false;
            for(int i = indexOfOpenBrace; i < indexOfCloseBrace; i++) {
                if(data[i] == TARGET_NAME[0] && data[i + 1] == TARGET_NAME[1] && data[i + 2] == TARGET_NAME[2] &&
                        data[i + 3] == TARGET_NAME[3] && data[i + 4] == TARGET_NAME[4] && data[i + 5] == TARGET_NAME[5] &&
                        data[i + 6] == TARGET_NAME[6] && data[i + 7] == TARGET_NAME[7] && data[i + 8] == TARGET_NAME[8] &&
                        data[i + 9] == TARGET_NAME[9] && data[i + 10] == TARGET_NAME[10] && data[i + 11] == TARGET_NAME[11]) {
                    isTargetName = true;
                    int j = i + 11;
                    while(data[j] != SPACE) {
                        j++;
                    }
                    while(data[j] != (byte)'\"') {
                        j++;
                    }
                    j++;
                    while(data[j] != (byte)'\"') {
                        targetNameList.add(data[j++]);
                    }
                    break;
                }
            }
            if(isTargetName) {
                List<Byte> targetList;
                targetList = new ArrayList<>();
                for(int i = 0; i < TARGET.length; i++) {
                    targetList.add(TARGET[i]);
                }
                for(int i = 0; i < targetNameList.size(); i++){
                    targetList.add(targetNameList.get(i));
                }
                targetList.add((byte) '\"');
                byte[] target = new byte[targetList.size()];
                int k = 0;
                for (Byte b : targetList) {
                    target[k++] = b;
                }
                int indexOfTarget = kmp.indexOf(data, target);
                for(int i = indexOfTarget; i >= 0; i--) {
                    if(data[i] == OPEN_BRACE) {
                        indexOfOpenBrace = i;
                        break;
                    }
                }
                for(int i = indexOfTarget; i < data.length; i++) {
                    if(data[i] == CLOSE_BRACE) {
                        indexOfCloseBrace = i;
                        break;
                    }
                }
                for(int i = indexOfOpenBrace + 1; i < indexOfCloseBrace; i++) {
                    if(data[i] == TRIGGER_MULTPLE[0] && data[i + 1] == TRIGGER_MULTPLE[1] && data[i + 2] == TRIGGER_MULTPLE[2] &&
                            data[i + 3] == TRIGGER_MULTPLE[3] && data[i + 4] == TRIGGER_MULTPLE[4] && data[i + 5] == TRIGGER_MULTPLE[5] &&
                            data[i + 6] == TRIGGER_MULTPLE[6] && data[i + 7] == TRIGGER_MULTPLE[7] && data[i + 8] == TRIGGER_MULTPLE[8] &&
                            data[i + 9] == TRIGGER_MULTPLE[9] && data[i + 10] == TRIGGER_MULTPLE[10] && data[i + 11] == TRIGGER_MULTPLE[11] &&
                            data[i + 12] == TRIGGER_MULTPLE[12] && data[i + 13] == TRIGGER_MULTPLE[13] && data[i + 14] == TRIGGER_MULTPLE[14] &&
                            data[i + 15] == TRIGGER_MULTPLE[15] && data[i + 16] == TRIGGER_MULTPLE[16] && data[i + 17] == TRIGGER_MULTPLE[17] &&
                            data[i + 18] == TRIGGER_MULTPLE[18] && data[i + 19] == TRIGGER_MULTPLE[19] && data[i + 20] == TRIGGER_MULTPLE[20] &&
                            data[i + 21] == TRIGGER_MULTPLE[21] && data[i + 22] == TRIGGER_MULTPLE[22] && data[i + 23] == TRIGGER_MULTPLE[23] &&
                            data[i + 24] == TRIGGER_MULTPLE[24] && data[i + 25] == TRIGGER_MULTPLE[25] && data[i + 26] == TRIGGER_MULTPLE[26] &&
                            data[i + 27] == TRIGGER_MULTPLE[27] && data[i + 28] == TRIGGER_MULTPLE[28] && data[i + 29] == TRIGGER_MULTPLE[29]) {
                        for(int j = indexOfOpenBrace + 1; j < indexOfCloseBrace; j++) {
                            if(data[j] == WAIT[0] && data[j + 1] == WAIT[1] && data[j + 2] == WAIT[2] &&
                                data[j + 3] == WAIT[3] && data[j + 4] == WAIT[4] && data[j + 5] == WAIT[5]) {
                                int l = j;
                                while(data[l] != (byte)'\n') {
                                    data[l++] = SPACE;
                                }
                                isFixedStopTimer = true;
                                break;
                            }
                        }
                    }
                }
            }
            /*----------------------------------------------------------------*/
        }   
        
        if(isDeletingMessages) {
            ArrayList<Integer> listTargetPrint = kmp.indexOfAll(data, TARGET_PRINT);
            for (Integer listTarget : listTargetPrint) {
                for(int i = listTarget; i < listTarget + TARGET_PRINT.length; i++) {
                    data[i] = SPACE;
                }
            }
            ArrayList<Integer> listTargetSmallPrint = kmp.indexOfAll(data, TARGET_SMALLPRINT);
            for (Integer listSmallTarget : listTargetSmallPrint) {
                for(int i = listSmallTarget; i < listSmallTarget + TARGET_SMALLPRINT.length; i++) {
                    data[i] = SPACE;
                }
            }
            countDeletedMessages = listTargetPrint.size() + listTargetSmallPrint.size();
        }

        JOptionPane.showMessageDialog(null,getPanel(isFixedStartTimer, isFixedStopTimer, countDeletedMessages),
                getAbsoluthPath(bspPath), JOptionPane.INFORMATION_MESSAGE);
        Files.write(Paths.get(bspPath), data);
    }
    
    public JPanel getPanel(boolean isFixedStartTimer, boolean isFixedStopTimer, int countDeletedMessages) {
        
        JPanel panel = new JPanel();
        GridBagLayout gbl = new GridBagLayout();
        panel.setLayout(gbl);
        GridBagConstraints gbc = new GridBagConstraints();
        
        JLabel labelStartTimer = createLabel("Fixed startTimer : ");
        JLabel labelYes1 = createLabel("YES", Color.getHSBColor(0.5f, 0.8f, 0.5f));
        JLabel labelNo1 = createLabel("NO", Color.RED);
        JLabel labelStopTimer = createLabel("Fixed stopTimer : ");
        JLabel labelYes2 = createLabel("YES", Color.getHSBColor(0.5f, 0.8f, 0.5f));
        JLabel labelNo2 = createLabel("NO", Color.RED);
        JLabel labelDeletedMessages = createLabel("Deleted messages : ");
        JLabel labelNumberOfDeletedMessages1 = createLabel(String.valueOf(countDeletedMessages), Color.getHSBColor(0.5f, 0.8f, 0.5f));
        JLabel labelNumberOfDeletedMessages2 = createLabel("0", Color.RED);
        
        labelStartTimer.setFont(new Font("Serif", Font.PLAIN, 14));
        labelYes1.setFont(new Font("Serif", Font.BOLD, 14));
        labelNo1.setFont(new Font("Serif", Font.BOLD, 14));
        labelStopTimer.setFont(new Font("Serif", Font.PLAIN, 14));
        labelYes2.setFont(new Font("Serif", Font.BOLD, 14));
        labelNo2.setFont(new Font("Serif", Font.BOLD, 14));
        labelDeletedMessages.setFont(new Font("Serif", Font.PLAIN, 14));
        labelNumberOfDeletedMessages1.setFont(new Font("Serif", Font.BOLD, 14));
        labelNumberOfDeletedMessages2.setFont(new Font("Serif", Font.BOLD, 14));
        
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbl.setConstraints(labelStartTimer, gbc);
        panel.add(labelStartTimer);
        
        gbc.gridx = 1;
        gbc.gridy = 0;
        if(isFixedStartTimer) {
            gbl.setConstraints(labelYes1, gbc);
            panel.add(labelYes1);
        }
        else {
            gbl.setConstraints(labelNo1, gbc);
            panel.add(labelNo1);
        }
        
        gbc.gridx = 0;
        gbc.gridy = 1;
        gbl.setConstraints(labelStopTimer, gbc);
        panel.add(labelStopTimer);
        
        gbc.gridx = 1;
        gbc.gridy = 1;
        if(isFixedStopTimer) {
            gbl.setConstraints(labelYes2, gbc);
            panel.add(labelYes2);
        }
        else {
            gbl.setConstraints(labelNo2, gbc);
            panel.add(labelNo2);
        }

        gbc.gridx = 0;
        gbc.gridy = 2;
        gbl.setConstraints(labelDeletedMessages, gbc);
        panel.add(labelDeletedMessages);
        
        gbc.gridx = 1;
        gbc.gridy = 2;
        if(countDeletedMessages > 0) {
            gbl.setConstraints(labelNumberOfDeletedMessages1, gbc);
            panel.add(labelNumberOfDeletedMessages1);
        }
        else {
            gbl.setConstraints(labelNumberOfDeletedMessages2, gbc);
            panel.add(labelNumberOfDeletedMessages2);
        }
        
        return panel;
    }
    
    public static JLabel createLabel(String text) {

        return createLabel(text, UIManager.getColor("Label.foreground"));

    }
    
    public static JLabel createLabel(String text, Color color) {
        JLabel label = new JLabel(text);
        label.setForeground(color);
        return label;
    }
    
}
