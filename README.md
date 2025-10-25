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