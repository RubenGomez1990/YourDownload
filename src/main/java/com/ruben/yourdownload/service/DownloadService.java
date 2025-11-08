package com.ruben.yourdownload.service;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import com.ruben.yourdownload.model.DownloadInfo; // Asumiendo este nombre para el Modelo
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

public class DownloadService {
    
    private static final Logger logger = Logger.getLogger(DownloadService.class.getName());
    private static final String PREFS_NODE = "PreferencesPanel"; 
    private static final String HISTORY_FILE_NAME = "downloads_history.json";

    // --- LÓGICA DE PERSISTENCIA JSON ---
    
    public static void saveHistory(List<DownloadInfo> historyList) {
        Gson gson = new GsonBuilder().setPrettyPrinting().create();
        File historyFile = new File(System.getProperty("user.home"), HISTORY_FILE_NAME);

        try (FileWriter writer = new FileWriter(historyFile)) {
            gson.toJson(historyList, writer);
        } catch (IOException e) {
            logger.log(Level.SEVERE, "Error al guardar historial JSON.", e);
        }
    }

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

    // --- LÓGICA DE RUTAS PORTABLES (Refactorización) ---
    
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