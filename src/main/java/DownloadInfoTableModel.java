
import java.util.List;

/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */


/**
 *
 * @author LionKeriot
 */
public class DownloadInfoTableModel extends javax.swing.table.AbstractTableModel {
    private final List<DownloadInfo> resources;
    private final String[] columnName = {"Name", "Size", "Format", "Download Date"};
    
    public DownloadInfoTableModel(List<DownloadInfo> resources){
        this.resources = resources;
    }
    
    
    @Override
    public int getRowCount(){
        return resources.size();
    }
    
    public int getColumnCount() {
        return columnName.length;
    }
    
    public String getColumnName(int columnIndex){
        return columnName[columnIndex];
    }
    
    public Object getValueAt(int rowIndex, int columnIndex){
        DownloadInfo resource = resources.get(rowIndex);
        
        switch (columnIndex) {
            case 0: return resource.getFileName();
            case 1: return resource.getFormatedSize();
            case 2: return resource.getMimeType();
            case 3: return resource.getDownloadDate();
            default: return null;
        }
    }
}


