# YourDownload

Author: Rubén Gómez Hernández


Problems:

1. Downloading video Error 404. 
Looked at google/reddit https://www.reddit.com/r/youtubedl/comments/15oyh34/ytdlp_http_error_403_forbidden/
I had a problem with my version of yt-dlp. Used yt-dlp -U to update it. Now it works. Tried with this video: https://youtu.be/8xQbWu9yzS0?si=0HlMMsDTzjpIrONT

2. Used Copilot for the code behind the logic for the connection with yt-dlp

new Thread(() -> {
            try {
                ProcessBuilder builder = new ProcessBuilder("yt-dlp", "-f", formato, url);
                builder.redirectErrorStream(true);
                Process process = builder.start();
                
                BufferedReader reader = new BufferedReader (new InputStreamReader(process.getInputStream()));
                String line;
                while ((line = reader.readLine()) != null) {
                    final String outputLine = line;
                    SwingUtilities.invokeLater(() -> {
                        jTextAreaConsola.append (outputLine + "\n");
                        jTextAreaConsola.setCaretPosition(jTextAreaConsola.getDocument().getLength());
                        actualizarBarraProgreso(outputLine);
                    });
                }
                    process.waitFor();
            } catch (Exception e){
                SwingUtilities.invokeLater(() -> jTextAreaConsola.append("Error: " + e.getMessage() + "\n"));
            } finally {
                SwingUtilities.invokeLater(() -> jButtonDescarga.setEnabled(true));
            }
        }).start();

3. Having some problems with the download of qualities for some videos. Youtube doesn't allow to have some qualities and i don't know how to resolve it. 
For example, i can download with this video https://youtu.be/2Vv-BfVoq4g?si=Rxf6Oj7cIqdwpiLo both 1080p or 480p but not 720p. Can you explain it to me?


4. Mayority of commands used for yt-dlp are in the manual at https://github.com/yt-dlp/yt-dlp?tab=readme-ov-file#subtitle-options
For example, had a problem with subs so looked for the part of sub at the index. Used --write-auto-subs and created a command. Used auto because is hard to find videos that have manual subs.

5. Button go back from preferences. Really hard to do it with my acknolewdge. Used copilot for the logic behind the constructors. panelOriginal = (JPanel) getContentPane(); or 
     // Dentro del JFrame principal
    setContentPane(new PanelPreferencias(this, panelOriginal));
    revalidate();
    repaint();

6. Allowed to save the preferences for the binaries path. Both Stackoverflow and copilot helped me. 

7. Having some troubles with the media player for the recent download. Used again stack overflow or Copilot for the code. 

8. Couldn't do the part of m3u files or selecting the download speed. Tried for a while but was impossible for me.

Personal notes: I'm having so much trouble with this task. I'm working at a company and its hard for me to follow the big quantity of time you need for studying and understanding it. I mean,
i will do my best effort but probably i will need some help. Can you give me some recomendations? There is a lot of code used here that aren't in the theory of the moodle, i've been using lot of time
for investigation but i think that using other codes won't help me because i'm not getting the full concepts. I will improve, for sure but this task was extremely hard. I've figured some methods or
variables but the logic behind the use of every button was impossible to understand. In any case, every single time i used external code o looked for a solution, it was because i needed how to figure
out the logic of the code and still was impossible for me because we have 0 acknowledge about java swing. 

// Update for task 1_2

1. I have been able to learn something from first task but there is still a long way to go. 

2. Some problems with commit / push. Forgot to do some of them, i'm sorry. 

3. Update many things from task 1_1

4. Had some problems with the JList. Used gemini for that because my program wasn't getting the files and i've found that following the code that gemini gave me the problem was with that the JList
was getting List<String> when i needed an object... so after a hard looking up for the solution found at stack overflow that

Object selectedValue = jListSearchList.getSelectedValue();
        DownloadInfo resource = (DownloadInfo) selectedValue; // Cast explícito


So we told to the program that value was an Object and then we cast it into a DownloadInfo to allow the program to work with it. 

5. Followed the idea for allowing to load the download files of the past. Looked to the video tutorial. Added the GSON dependency. 

6. We i have more time i will update the app with better visuals. 

// Update for task 3_1

1. Forgot to commit.

2. Implemented login and improved many things

3. I personally use prefs.remove("jwt_token") to do tests with the token for deleting it

try { 
            // Intentamos leer el token guardado
            java.util.prefs.Preferences prefs = java.util.prefs.Preferences.userRoot().node("YourDownloadApp");
            prefs.remove("jwt_token");
            String token = prefs.get("jwt_token", null); 

4. Tried to fix the problem with the dynamic JSON but couldn't, if you can give me some tips... Because it saves the path of the downloads always.

5. I think that actually it's portable.

6. The logout button it's in file --> Logout. For me it's logic and a good place to place it. 


// UPDATES TASK 4

1. loadAllMediaInfo: Se ha modificado para que descargue toda la información de la plataforma correctamente. Automaticamente, ordena los archivos por ID, muestra solo 1 versión de cada
archivo (Si esta en net y local, muestra local y net). Actualiza y comprueba si hay archivos nuevos para añadirlos a la librería.

2. Archivo JSON para el historial. Utiliza un hashmap para garantizar que no hayan duplicados.

3. Se ha reducido el tamaño de la pantalla principal para que tenga más similitud a un reproductor y hacerlo más minimalista. 
Se ha añadido un botón "Show Log" para mostrar el log del progreso de la descarga en caso de ser necesario por parte del cliente. 

4. Se ha establecido el Login como parte del mismo JFrame que mainscreen. Se ha ordenado el botón en el JMenu para establecerlo encima de exit. 

5. Se ha establecido un tema para la aplicacion FlatLaf más moderno añadiendo su dependencia y la referencia en el main de MainScreen. Mejora visual moderna.

6. El recuadro de Polling ha desaparecido y en su lugar se ha puesto un icono que cambia de color dinámicamente. 

7. Se ha arreglado el error por el cual desde MediaLibrary -> Edit -> Preferences al darle a go back, volvías a MainScreen. 

8. Implementación de una tabla más grande, con orden automático por ID, mejora de la lista de filtrado por tipo de archivo añadiendo unos formatos basicos, mp3, mp4, .avi, .wav.
También se pueden ordenar las columnas en el orden que quiera el cliente, cambiarlas de tamaño o aplicar filtros sobre cada columna. Se ha intentado establecer un icono para que avise de que se 
puede redimensionar al pasar el ratón por encima pero no se ha conseguido y puede ser una limitación de FlatLaf







