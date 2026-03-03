package com.gomez.yourdownload.model;

import java.io.File;
import java.util.Date;

/**
 * Model class representing a media resource within the application. Stores
 * information about local files (path, download date) and network resources
 * (network ID, sync status).
 *
 * * @author Rubén Gómez Hernández
 * @version 1.0
 */
public class DownloadInfo {

    /**
     * * The name of the media file.
     */
    private final String fileName;
    /**
     * * The local absolute path on the disk. Null if resource is network-only.
     */
    private final String absolutePath;
    /**
     * * The date when the file was downloaded. Null if resource is
     * network-only.
     */
    private final Date downloadDate;
    /**
     * * The size of the file in bytes.
     */
    private final Long size;
    /**
     * * The media MIME type or file format extension.
     */
    private String mimeType;

    /**
     * * Unique identifier assigned by the server API.
     */
    private Integer networkId;
    /**
     * * Flag indicating if the file exists on the server.
     */
    private boolean isInNetwork;
    /**
     * * Flag indicating if the local file has been successfully uploaded.
     */
    private boolean isUploaded;
    /**
     * * Flag indicating if the resource has not been downloaded yet.
     */
    private boolean isNetworkOnly;

    /**
     * Constructs a DownloadInfo instance for a local file.
     *
     * @param absolutePath The full system path to the file.
     * @param downloadDate The timestamp of the download.
     * @param size The file size in bytes.
     * @param mimeType The format or extension of the file.
     */
    public DownloadInfo(String absolutePath, Date downloadDate, Long size, String mimeType) {
        this.fileName = new File(absolutePath).getName();
        this.absolutePath = absolutePath;
        this.downloadDate = downloadDate;
        this.size = size;
        this.mimeType = mimeType;
        this.networkId = null;
        this.isInNetwork = false;
        this.isUploaded = false;
        this.isNetworkOnly = false;
    }

    /**
     * Constructs a DownloadInfo instance for a network-only resource.
     *
     * @param networkId The unique ID provided by the API.
     * @param fileName The name of the remote file.
     * @param size The remote file size in bytes.
     * @param mimeType The format provided by the server.
     */
    public DownloadInfo(Integer networkId, String fileName, Long size, String mimeType) {
        this.networkId = networkId;
        this.fileName = fileName;
        this.size = size;
        this.mimeType = mimeType;

        // Es de red:
        this.isInNetwork = true;
        this.isNetworkOnly = true;

        // No es local
        this.absolutePath = null;
        this.downloadDate = null;
        this.isUploaded = false;
    }

    /**
     * @return The file name.
     */
    public String getFileName() {
        return fileName;
    }

    /**
     * @return The system absolute path.
     */
    public String getAbsolutePath() {
        return absolutePath;
    }

    /**
     * @return The download date.
     */
    public Date getDownloadDate() {
        return downloadDate;
    }

    /**
     * @return The size in bytes.
     */
    public Long getSize() {
        return size;
    }

    /**
     * @return The MIME type or extension.
     */
    public String getMimeType() {
        return mimeType;
    }

    /**
     * @param mimeType The new MIME type to set.
     */
    public void setMimeType(String mimeType) {
        this.mimeType = mimeType;
    }

    /**
     * @return The server-side network ID.
     */
    public Integer getNetworkId() {
        return networkId;
    }

    /**
     * @return True if the file only exists on the server.
     */
    public boolean isNetworkOnly() {
        return isNetworkOnly;
    }

    /**
     * @return True if the file is available in the network.
     */
    public boolean isInNetwork() {
        return isInNetwork;
    }

    /**
     * @return True if the file has been uploaded.
     */
    public boolean isUploaded() {
        return isUploaded;
    }

    /**
     * @param networkId Sets the network ID.
     */
    public void setNetworkId(Integer networkId) {
        this.networkId = networkId;
    }

    /**
     * @param isInNetwork Updates the network availability status.
     */
    public void setIsInNetwork(boolean isInNetwork) {
        this.isInNetwork = isInNetwork;
    }

    /**
     * @param isNetworkOnly Updates the network-only status.
     */
    public void setIsNetworkOnly(boolean isNetworkOnly) {
        this.isNetworkOnly = isNetworkOnly;
    }

    /**
     * @param isUploaded Updates the upload status.
     */
    public void setIsUploaded(boolean isUploaded) {
        this.isUploaded = isUploaded;
    }
    
    /**
     * Converts the raw byte size into a human-readable format (KB, MB, GB).
     * @return A formatted string with the appropriate size unit.
     */
    public String getFormatedSize() {
        if (size <= 0) {
            return "0 Bytes";
        }

        final String[] units = new String[]{"Bytes", "KB", "MB", "GB", "TB"};
        int digitGroups = (int) (Math.log10(size) / Math.log10(1024));
        return new java.text.DecimalFormat("#,##0.#")
                .format(size / Math.pow(1024, digitGroups)) + " " + units[digitGroups];
    }
    
    /**
     * Provides a summary of the media resource.
     * @return String containing filename and formatted size.
     */
    @Override
    public String toString() {
        // Usamos el nombre del archivo y el tamaño formateado para que sea informativo.
        return fileName + " (" + getFormatedSize() + ")";
    }
}
