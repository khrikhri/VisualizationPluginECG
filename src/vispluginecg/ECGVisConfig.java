package vispluginecg;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import mo.organization.Configuration;
import mo.visualization.Playable;
import mo.visualization.VisualizableConfiguration;

public class ECGVisConfig implements VisualizableConfiguration {

        
    private String id;
    ECGPlayer player;
    List<File> files;
    private final String[] creators = {"capturepluginbitalino.BitalinoRecorder"};
    

    public ECGVisConfig() {
        files = new ArrayList<>();
    }
    
   @Override
    public String getId() {
        return id;
    }

    @Override
    public File toFile(File parent) {
        File f = new File(parent, "ecg-visualization_"+id+".xml");
        try {
            f.createNewFile();
        } catch (IOException ex) {
            Logger.getLogger(ECGVisConfig.class.getName()).log(Level.SEVERE, null, ex);
        }
        return f;
    }

    @Override
    public Configuration fromFile(File file) {
        String fileName = file.getName();

        if (fileName.contains("_") && fileName.contains(".")) {
            String name = fileName.substring(fileName.indexOf("_")+1, fileName.lastIndexOf("."));
            ECGVisConfig config = new ECGVisConfig();
            config.id = name;
            return config;
        }
        return null;
    }

    @Override
    public List<String> getCompatibleCreators() {
       return Arrays.asList(creators);
    }
    
    public void setId(String id) {
        this.id = id;
    }
    
    private void ensurePlayerCreated() {
        if (player == null && !files.isEmpty()) {
            player = new ECGPlayer(files.get(0));
        }
    }

    @Override
    public void addFile(File file) {
        if ( !files.contains(file) ) {
            this.files.add(file);
        }
    }

    public Playable getPlayer() {
        ensurePlayerCreated();
        return player;
    }
    
    @Override
    public void removeFile(File file) {
        File toRemove = null;
        
        for (File f : files) {
            if (f.equals(file)) {
                toRemove = f;
            }
        }
        
        if (toRemove != null) {
            files.remove(toRemove);
        }
    }

    
}
