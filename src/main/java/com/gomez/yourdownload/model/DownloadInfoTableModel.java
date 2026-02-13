package com.gomez.yourdownload.model;

import java.text.SimpleDateFormat;
import java.util.List;
import javax.swing.table.AbstractTableModel;

public class DownloadInfoTableModel extends AbstractTableModel {

    private final List<DownloadInfo> downloads;
    private final String[] columnNames = {"ID", "Name", "Size", "Format", "Download Date", "Status"};
    private final SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

    public DownloadInfoTableModel(List<DownloadInfo> downloads) {
        this.downloads = downloads;
    }

    @Override
    public int getRowCount() { return downloads.size(); }

    @Override
    public int getColumnCount() { return columnNames.length; }

    @Override
    public String getColumnName(int column) { return columnNames[column]; }

    // CRÍTICO: Esto arregla el orden (3 después de 28) indicando que la col 0 es numérica
    @Override
    public Class<?> getColumnClass(int columnIndex) {
        if (columnIndex == 0) return Integer.class; 
        return Object.class;
    }

    @Override
    public Object getValueAt(int rowIndex, int columnIndex) {
        if (rowIndex < 0 || rowIndex >= downloads.size()) return null;
        DownloadInfo resource = downloads.get(rowIndex);

        switch (columnIndex) {
            case 0:
                // Retornamos el Integer puro para que el ordenador funcione bien
                return resource.getNetworkId(); 
            case 1:
                return resource.getFileName();
            case 2:
                return resource.getFormatedSize();
            case 3:
                return (resource.getMimeType() != null) ? resource.getMimeType().split("/")[0] : "Unknown";
            case 4:
                return (resource.getDownloadDate() != null) ? dateFormat.format(resource.getDownloadDate()) : "N/A (No Local)";
            case 5:
                // --- Etiquetas de Estado Personalizadas ---
                
                // 1. Está en ambos (Local + Net)
                if (resource.getAbsolutePath() != null && resource.isInNetwork()) {
                    return "Local + Net";
                }
                // 2. Solo en la API
                if (resource.isNetworkOnly() || (resource.getAbsolutePath() == null && resource.isInNetwork())) {
                    return "Network Only";
                }
                // 3. Solo en el ordenador
                if (resource.getAbsolutePath() != null) {
                    return "Local Only";
                }
                return "Unknown";
            default:
                return null;
        }
    }
}
