/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package quake3mapfixer;

import java.util.ArrayList;

/**
 *
 * @author chrollo
 */
public class KMPMatch {
    
    public int indexOf(byte[] data, byte[] pattern) {
        int j = 0;
        if (data.length == 0) return -1;
        for (int i = 0; i < data.length; i++) {
            if (pattern[j] == data[i]) { 
                if (j == (pattern.length - 1)) {
                    return i - pattern.length + 1;
                }
                j++; 
            }
            else { 
                j = 0; 
            }
        }
        return -1;
    }

    public ArrayList<Integer> indexOfAll(byte[] data, byte[] pattern) {
        ArrayList<Integer> list = new ArrayList<>();
        int j = 0;
        for (int i = 0; i < data.length; i++) {
            if (pattern[j] == data[i]) {
                if (j == (pattern.length - 1)) {
                    list.add(i - pattern.length + 1);
                    j = 0;
                }
                else {
                    j++;
                }
            }
            else { 
                j = 0; 
            }
            
        }
        return list;
    }
}
