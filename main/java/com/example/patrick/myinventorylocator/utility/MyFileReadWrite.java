package com.example.patrick.myinventorylocator.utility;

import android.content.Context;

import com.example.patrick.myinventorylocator.model.Vehicle;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.StreamCorruptedException;
import java.util.HashMap;

/**
 * Created by Patrick on 2/24/2018.
 */

public class MyFileReadWrite {

    private Context mContext;

    public MyFileReadWrite(Context theContext) {
        mContext = theContext;
    }

    /**
     * This method stores the current Inventory hashmap in the devices internal storage.
     */
    public void writeFile(HashMap<String, Vehicle> theHashMap, String theSubFolder, String theFileName) {
        File cacheDir = null;
        File appDirectory = null;

        if (android.os.Environment.getExternalStorageState().
                equals(android.os.Environment.MEDIA_MOUNTED)) {
            cacheDir = mContext.getExternalCacheDir();
            appDirectory = new File(cacheDir + theSubFolder);

        } else {
            cacheDir = mContext.getCacheDir();
            String BaseFolder = cacheDir.getAbsolutePath();
            appDirectory = new File(BaseFolder + theSubFolder);

        }

        if (appDirectory != null && !appDirectory.exists()) {
            appDirectory.mkdirs();
        }

        File fileName = new File(appDirectory, theFileName);

        FileOutputStream fos = null;
        ObjectOutputStream out = null;
        try {
            fos = new FileOutputStream(fileName);
            out = new ObjectOutputStream(fos);
            out.writeObject(theHashMap);
        } catch (IOException ex) {
            ex.printStackTrace();
        }  catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                if (fos != null)
                    fos.flush();
                fos.close();
                if (out != null)
                    out.flush();
                out.close();
            } catch (Exception e) {

            }
        }
    }

    /**
     * This method retrieves the Inventory hashmap from the devices internal storage.
     */
    public HashMap<String, Vehicle> readFile(String theSubFolder, String theFileName) {
        File cacheDir = null;
        File appDirectory = null;
        if (android.os.Environment.getExternalStorageState().
                equals(android.os.Environment.MEDIA_MOUNTED)) {
            cacheDir = mContext.getExternalCacheDir();
            appDirectory = new File(cacheDir + theSubFolder);
        } else {
            cacheDir = mContext.getCacheDir();
            String BaseFolder = cacheDir.getAbsolutePath();
            appDirectory = new File(BaseFolder + theSubFolder);
        }

        if (appDirectory != null && !appDirectory.exists()) {

        }

        File fileName = new File(appDirectory, theFileName);

        FileInputStream fis = null;
        ObjectInputStream in = null;
        HashMap<String, Vehicle> myHashMap = null;
        try {
            fis = new FileInputStream(fileName);
            in = new ObjectInputStream(fis);
            myHashMap = (HashMap<String, Vehicle> ) in.readObject();

        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (StreamCorruptedException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }finally {

            try {
                if(fis != null) {
                    fis.close();
                }
                if(in != null) {
                    in.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return myHashMap;
    }
}

