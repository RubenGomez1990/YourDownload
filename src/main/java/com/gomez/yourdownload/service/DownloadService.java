package com.gomez.yourdownload.service;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import com.gomez.yourdownload.model.DownloadInfo; // Asumiendo este nombre para el Modelo
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;
import java.util.prefs.Preferences;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Service class responsible for data persistence and system configuration.
 * Manages the serialization of download history to JSON format and handles
 * the resolution of portable paths for external binaries.
 * @author Rubén Gómez Hernández
 * @version 1.0
 */
public class DownloadService {
    
    /** 
     * Logger for tracking service-level events and errors. 
     */
    private static final Logger logger = Logger.getLogger(DownloadService.class.getName());
    /** 
     * Preferences node name used for storing application settings. 
     */
    private static final String PREFS_NODE = "PreferencesPanel"; 
    /** 
     * Filename for the JSON history storage. 
     */
    private static final String HISTORY_FILE_NAME = "downloads_history.json";
    
    /**
     * Private constructor to prevent instantiation of this utility class.
     * This class only provides static service methods.
     * @throws IllegalStateException If called, as this class should not be instantiated.
     */
    private DownloadService() {
        throw new IllegalStateException("Utility class");
    }

    /**
     * Serializes the list of media resources to a local JSON file.
     * Implements a de-duplication filter using a Map to ensure each resource
     * is unique by its ID or filename before saving.
     * @param historyList The list of DownloadInfo objects to persist.
     */
    public static void saveHistory(List<DownloadInfo> historyList) {
    Gson gson = new GsonBuilder().setPrettyPrinting().create();
    File historyFile = new File(System.getProperty("user.home"), HISTORY_FILE_NAME);

    // FILTRO ANTI-DUPLICADOS: Usamos un Mapa para quedarnos solo con un registro por cada ID o Nombre
    java.util.Map<String, DownloadInfo> uniqueMap = new java.util.LinkedHashMap<>();
    
    for (DownloadInfo info : historyList) {
        // Usamos el ID como clave si existe, si no, el nombre del archivo
        String key = (info.getNetworkId() != null) ? info.getNetworkId().toString() : info.getFileName();
        uniqueMap.put(key, info);
    }

    try (FileWriter writer = new FileWriter(historyFile)) {
        // Guardamos solo los valores únicos del mapa
        gson.toJson(new java.util.ArrayList<>(uniqueMap.values()), writer);
    } catch (IOException e) {
        logger.log(Level.SEVERE, "Error saving the JSON historial.", e);
    }
}
    /**
     * Deserializes the download history from the local JSON file.
     * @return A list of DownloadInfo objects, or an empty list if the file is missing or corrupted.
     */
    public static List<DownloadInfo> loadHistory() {
        File historyFile = new File(System.getProperty("user.home"), HISTORY_FILE_NAME);
        
        if (!historyFile.exists()) {
            return new ArrayList<>();
        }

        Gson gson = new Gson();
        Type listType = new TypeToken<ArrayList<DownloadInfo>>() {}.getType();

        try (FileReader reader = new FileReader(historyFile)) {
            List<DownloadInfo> historyList = gson.fromJson(reader, listType);
            return historyList != null ? historyList : new ArrayList<>();
        } catch (IOException e) {
            logger.log(Level.SEVERE, "ERROR al cargar historial JSON. Devolviendo lista vacía.", e);
            return new ArrayList<>(); 
        }
    }

    /**
     * Resolves the path to the yt-dlp executable.
     * Checks user preferences first, then searches common system directories 
     * like LOCALAPPDATA or the user's home folder.
     * @return The absolute path to the binary as a String, or an empty string if not found.
     * @throws SecurityException If system environment variables are inaccessible.
     */
    public static String getBinariesPath() {
        Preferences prefs = Preferences.userRoot().node(PREFS_NODE);
        String binariesPath = prefs.get("binariesPath", ""); 

        if (binariesPath.isEmpty()) { 
            String appDataPath = System.getenv("LOCALAPPDATA") + File.separator + "yt-dlp.exe";
            File appDataFile = new File(appDataPath);
            
            if (appDataFile.exists()) {
                binariesPath = appDataFile.getAbsolutePath();
                prefs.put("binariesPath", binariesPath); 
                return binariesPath;
            }
        }
        
        if (binariesPath.isEmpty()) { 
            String homePath = System.getProperty("user.home") + File.separator + "yt-dlp.exe";
            File homeFile = new File(homePath);

            if (homeFile.exists()) {
                binariesPath = homeFile.getAbsolutePath();
                prefs.put("binariesPath", binariesPath);
                return binariesPath;
            }
        }
        
        return binariesPath;
    }
}