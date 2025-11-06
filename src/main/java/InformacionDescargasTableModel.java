
import java.util.List;

/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */


/**
 *
 * @author LionKeriot
 */
public class InformacionDescargasTableModel extends javax.swing.table.AbstractTableModel {
    private final List<InformacionDescargas> recursos;
    private final String[] nombresColumnas = {"Nombre", "Tamaño", "Formato", "Fecha"};
    
    public InformacionDescargasTableModel(List<InformacionDescargas> recursos){
        this.recursos = recursos;
    }
    
    
    @Override
    public int getRowCount(){
        return recursos.size();
    }
    
    public int getColumnCount() {
        return nombresColumnas.length;
    }
    
    public String getColumnName(int columnIndex){
        return nombresColumnas[columnIndex];
    }
    
    public Object getValueAt(int rowIndex, int columnIndex){
        InformacionDescargas recurso = recursos.get(rowIndex);
        
        switch (columnIndex) {
            case 0: return recurso.getNombreArchivo();
            case 1: return recurso.getTamanoFormateado();
            case 2: return recurso.getMimeType();
            case 3: return recurso.getFechaDescarga();
            default: return null;
        }
    }
}


