package com.ruben.yourdownload.model;


import java.io.File;
import java.util.Date;

/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */


/**
 *
 * @author LionKeriot
 */
public class DownloadInfo {
    
    private final String fileName;
    private final String absolutePath;
    private final Date downloadDate;
    private final Long size;
    private final String mimeType;
    
    public DownloadInfo (String absolutePath, Date downloadDate, Long size, String mimeType){
        this.fileName = new File(absolutePath).getName();
        this.absolutePath = absolutePath;
        this.downloadDate = downloadDate;
        this.size = size;
        this.mimeType = mimeType;
    }

    public String getFileName() {
        return fileName;
    }

    public String getAbsolutePath() {
        return absolutePath;
    }

    public Date getDownloadDate() {
        return downloadDate;
    }

    public Long getSize() {
        return size;
    }

    public String getMimeType() {
        return mimeType;
    }
    
    public String getFormatedSize() {
        if (size <= 0) return "0 Bytes";
    
        final String[] units = new String[] { "Bytes", "KB", "MB", "GB", "TB" };
        int digitGroups = (int) (Math.log10(size) / Math.log10(1024));
         return new java.text.DecimalFormat("#,##0.#")
                .format(size / Math.pow(1024, digitGroups)) + " " + units[digitGroups];
    }
    @Override
    public String toString() {
    // Usamos el nombre del archivo y el tamaño formateado para que sea informativo.
    return fileName + " (" + getFormatedSize() + ")";
    }  
}
