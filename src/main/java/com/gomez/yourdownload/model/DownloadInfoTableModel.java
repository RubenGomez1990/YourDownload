package com.gomez.yourdownload.model;

import java.text.SimpleDateFormat;
import java.util.List;
import javax.swing.table.AbstractTableModel;

/**
 * Custom table model for displaying DownloadInfo objects in a JTable.
 * Manages column names, data types for proper sorting, and dynamic status labels.
 * @author Rubén Gómez Hernández
 * @version 1.0
 */
public class DownloadInfoTableModel extends AbstractTableModel {

    /** * List of DownloadInfo objects representing the table rows. */
    private final List<DownloadInfo> downloads;
    /** * Headers for the table columns. */
    private final String[] columnNames = {"ID", "Name", "Size", "Format", "Download Date", "Status"};
    /** * Formatter for displaying download timestamps. */
    private final SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
    
    /**
     * Constructs the table model with a specific list of downloads.
     * @param downloads The list of media resources to display.
     */
    public DownloadInfoTableModel(List<DownloadInfo> downloads) {
        this.downloads = downloads;
    }

    @Override
    public int getRowCount() { return downloads.size(); }

    @Override
    public int getColumnCount() { return columnNames.length; }

    @Override
    public String getColumnName(int column) { return columnNames[column]; }

    /**
     * Defines the data type for each column.
     * CRITICAL: Returning Integer.class for column 0 ensures numerical sorting 
     * (e.g., 3 comes before 28) instead of alphabetical sorting.
     * @param columnIndex The index of the column.
     * @return The Class of the data held in the column.
     */
    @Override
    public Class<?> getColumnClass(int columnIndex) {
        if (columnIndex == 0) return Integer.class; 
        return Object.class;
    }
    
    /**
     * Retrieves the value to be displayed at a specific cell.
     * Maps DownloadInfo properties to table columns and calculates status labels.
     * @param rowIndex The row being rendered.
     * @param columnIndex The column being rendered.
     * @return The formatted object to display in the cell.
     */
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
