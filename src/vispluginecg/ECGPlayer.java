package vispluginecg;

import java.io.File;
import java.util.logging.Logger;
import javax.swing.SwingUtilities;
import mo.core.ui.dockables.DockableElement;
import mo.core.ui.dockables.DockablesRegistry;
import mo.visualization.Playable;

public class ECGPlayer implements Playable{

    private long start;
    private long end;
    private Panel ap;
    private String path;
    

    private static final Logger logger = Logger.getLogger(ECGPlayer.class.getName());

    public ECGPlayer(File file) {
        ap = new Panel(file);
        start=ap.start;
        end=ap.end;
        SwingUtilities.invokeLater(new Runnable() {
                @Override
                public void run() {
                    DockableElement e = new DockableElement();
                    e.add(ap);
                    DockablesRegistry.getInstance().addAppWideDockable(e);
                }
            }); 
    }
        
    
    @Override
    public void pause() {
       
    }

    @Override
    public void seek(long desiredMillis) {
        ap.play((int)(desiredMillis-start));
    }
    
    @Override
    public long getStart() {        
        return start;
    }

    @Override
    public long getEnd() {
        return end;
    }   

    @Override
    public void play(long millis) {
        ap.play((int)((millis-start)));
    }

    @Override
    public void stop() {
        ap.stop();
    }

}
