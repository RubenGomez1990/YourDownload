
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
public class InformacionDescargas {
    
    private final String nombreArchivo;
    private final String rutaAbsoluta;
    private final Date fechaDescarga;
    private final Long tamanyo;
    private final String mimeType;
    
    public InformacionDescargas (String rutaAbsoluta, Date fechaDescarga, Long tamanyo, String mimeType){
        this.nombreArchivo = new File(rutaAbsoluta).getName();
        this.rutaAbsoluta = rutaAbsoluta;
        this.fechaDescarga = fechaDescarga;
        this.tamanyo = tamanyo;
        this.mimeType = mimeType;
    }

    public String getNombreArchivo() {
        return nombreArchivo;
    }

    public String getRutaAbsoluta() {
        return rutaAbsoluta;
    }

    public Date getFechaDescarga() {
        return fechaDescarga;
    }

    public Long getTamanyo() {
        return tamanyo;
    }

    public String getMimeType() {
        return mimeType;
    }
    
    public String getTamanoFormateado() {
        if (tamanyo <= 0) return "0 Bytes";
    
        final String[] units = new String[] { "Bytes", "KB", "MB", "GB", "TB" };
        int digitGroups = (int) (Math.log10(tamanyo) / Math.log10(1024));
         return new java.text.DecimalFormat("#,##0.#")
                .format(tamanyo / Math.pow(1024, digitGroups)) + " " + units[digitGroups];
    }
    @Override
    public String toString() {
    // Usamos el nombre del archivo y el tamaño formateado para que sea informativo.
    return nombreArchivo + " (" + getTamanoFormateado() + ")";
    }  
}
