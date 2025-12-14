package com.gomez.yourdownload.model;

import java.text.SimpleDateFormat;
import java.util.List;
import javax.swing.table.AbstractTableModel;

/**
 * Table Model para manejar la lista híbrida de archivos (Local, Red, Ambos).
 */
public class DownloadInfoTableModel extends AbstractTableModel {
    
    private final List<DownloadInfo> resources;
    // 🛑 CAMBIO 1: Añadimos la columna "Status"
    private final String[] columnName = {"Name", "Size", "Format", "Download Date", "Status"};
    
    // Formateador para la fecha
    private final SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm");
    
    public DownloadInfoTableModel(List<DownloadInfo> resources){
        this.resources = resources;
    }
    
    
    @Override
    public int getRowCount(){
        return resources.size();
    }
    
    @Override
    public int getColumnCount() {
        // 🛑 CAMBIO 2: Aseguramos que se devuelve la longitud del array de nombres (5 columnas)
        return columnName.length;
    }
    
    @Override
    public String getColumnName(int columnIndex){
        return columnName[columnIndex];
    }
    
    @Override
    public Object getValueAt(int rowIndex, int columnIndex){
        DownloadInfo resource = resources.get(rowIndex);
        
        switch (columnIndex) {
            case 0: return resource.getFileName();
            case 1: return resource.getFormatedSize();
            case 2: 
                // Usamos solo el tipo principal (ej: video/mp4 -> video)
                if (resource.getMimeType() != null) {
                    return resource.getMimeType().split("/")[0];
                }
                return "N/A";
                
            case 3: 
                // 🛑 CORRECCIÓN 3: Manejo de Null para archivos Solo Red
                if (resource.getDownloadDate() != null) {
                    return dateFormat.format(resource.getDownloadDate());
                }
                return "N/A (No Local)"; // Indica que no tiene fecha de descarga local
                
            case 4: 
                // 🛑 CORRECCIÓN 4: Lógica para la Columna de Estado
                if (resource.isNetworkOnly()) {
                    return "Network Only";
                } 
                if (resource.isInNetwork()) {
                    return "Local & Network";
                }
                // Si no está en Red y tiene ruta local
                if (resource.getAbsolutePath() != null) {
                    return "Local Only";
                }
                return "Unknown/Broken"; // Estado para archivos huérfanos sin ruta
                
            default: return null;
        }
    }
}


