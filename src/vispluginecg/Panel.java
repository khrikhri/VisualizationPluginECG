
package vispluginecg;
;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollBar;
import javax.swing.JScrollPane;

class Panel extends JPanel {
    private int escalaX = 20;
    private int escalaY = 10;
    private static int x0 = 15;
    private int y0;
    private int xp,xb,sbValue;
    private int w,h;
    private long fin;
    public long start, end;
    public boolean playing = true;
    JScrollPane scroller;
    Dimension area = new Dimension(0,0);
    JPanel drawingPane;  
    JScrollBar sb;
    ArrayList<Long> Tiempo = new ArrayList();
    ArrayList<Long> Datos = new ArrayList();
   
    JButton AmplitudU = new JButton("+");
    JButton AmplitudD = new JButton("-");
    JButton LongitudU = new JButton("+");
    JButton LongitudD = new JButton("-");
    JLabel Titulo = new JLabel("ElectroCardiogram");
    
    public Panel(File file){
        xp=x0-5;
        drawingPane = new DrawingPane();
        drawingPane.setBackground(Color.WHITE);
        FileReader fr;
        try {
            fr = new FileReader (file);            
            BufferedReader br = new BufferedReader(fr);
            
            String line;
            //Lee ch3, sera asignado a EMG
            while((line = br.readLine())!=null){
                String ch1 = line.substring(line.indexOf(",")+1);
                String ch2 = ch1.substring(ch1.indexOf(",")+1);
                
                //if(Long.parseLong(line.substring(line.indexOf(",")+1))!=0){                    
                    Tiempo.add(Long.parseLong(line.substring(0,line.indexOf(","))));
                    Datos.add(Long.parseLong(ch2.substring(0,ch2.indexOf(","))));
                //}
            }
            start = Tiempo.get(0);
            end = Tiempo.get(Tiempo.size()-1);
            scroller = new JScrollPane(drawingPane);
            scroller.setPreferredSize(new Dimension(600,150));
            add(scroller,BorderLayout.CENTER);
            AmplitudU.addActionListener(new ActionListener(){
                @Override
                public void actionPerformed(ActionEvent e) {
                        setUEscalaY();
                        repaint();
                }
                
            });  
            AmplitudD.addActionListener(new ActionListener(){
                @Override
                public void actionPerformed(ActionEvent e) {
                        setDEscalaY();
                        repaint();
                }
                
            });  
            LongitudU.addActionListener(new ActionListener(){
                @Override
                public void actionPerformed(ActionEvent e) {
                        setUEscalaX();
                        repaint();
                }
                
            }); 
            LongitudD.addActionListener(new ActionListener(){
                @Override
                public void actionPerformed(ActionEvent e) {
                        setDEscalaX();
                        repaint();
                }
                
            }); 
        } catch (FileNotFoundException ex) {
            Logger.getLogger(Panel.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException ex) {
            Logger.getLogger(Panel.class.getName()).log(Level.SEVERE, null, ex);
        }      
        
        
    }   
    
    public void graficarEjes(Graphics g) {
        g.setColor(Color.BLACK);
        linea(0,0,w,0,g);
        linea(0,y0,0,-y0,g);
    }
    
    public void graficarProgreso(Graphics g){
        Color barColor = new Color(255,117,20,127);
        g.setColor(barColor);
        g.fillRect(xp, (int) (y0-(drawingPane.getHeight()/1.2)),10,drawingPane.getHeight());
    }
    
    public void play(int millis){
        int tem = millis/escalaX;
        xp= x0+tem-5;
        sb = scroller.getHorizontalScrollBar();
        if(xp<=fin){
            repaint();
            sb.setValue((int)tem-(x0+10));
            if(sb.getValue()!=sbValue){
                xb=xp;
            }
            sbValue=sb.getValue();
        }else{
            xp=x0-5;
            xb=xp;
            repaint();         
            sb.setValue(0);
        }                   
    }
    
    public void stop(){
        xp=x0;
        repaint();
        sb.setValue(0);
    }
    
    class DrawingPane extends JPanel {
        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            w  = getSize().width;
            h  = getSize().height;
            y0 = (int) (h/1.2);       
            AjustarScroll();
            graficarEscpectrograma(g);
            graficarEjes(g);
            graficarProgreso(g);
            
            add(Titulo);
            add(AmplitudU);
            add(AmplitudD);
            add(LongitudU);
            add(LongitudD);
            AmplitudU.setLocation(xb+37, y0-87);
            AmplitudD.setLocation(xb+37, y0-33);
            LongitudU.setLocation(xb+60, y0-60);
            LongitudD.setLocation(xb+20, y0-60);
            
        }
    }
    public void setDEscalaX(){
        if(escalaX<30){            
            escalaX++;
        }
    }
    public void setUEscalaX(){
        if(escalaX>5){
            escalaX--;
        }
    }
    
    public void setDEscalaY(){
        if(escalaY<20){            
            escalaY++;
        }
    }
    public void setUEscalaY(){
        if(escalaY>2){
            escalaY--;
        }
    }
    
    public void graficarEscpectrograma(Graphics g){
       g.setColor(Color.BLUE);
        int size = Tiempo.size();
        long i, xi=0,xf=0,yi=0,yf=0;
        for(i=0;i<size;i++){
            xi = Tiempo.get((int)i)-Tiempo.get(0);
            xi=xi/escalaX;
            yi = Datos.get((int)i);
            yi=yi/escalaY;
            if((i+1)!=size){                
                xf = Tiempo.get((int)i+1)-Tiempo.get(0);
                xf=xf/escalaX;
                yf = Datos.get((int)i+1);
                yf=yf/escalaY;
                linea(xi, yi, xf, yf, g);
            }
        }
        fin=xf;
        if(xf>w) {
            area.width = (int) (xf+100);
            drawingPane.setPreferredSize(area);
            drawingPane.revalidate();
        } 
    }
     public void AjustarScroll(){
        scroller.setPreferredSize(new Dimension(getWidth(),getHeight()));
    }
     public void linea(double x1, double y1, double x2, double y2, Graphics g) {
        g.drawLine((int)Math.round(x1+x0),
                (int)Math.round(y0-y1),
                (int)Math.round(x2+x0),
                (int)Math.round(y0-y2));
    }
     
         
}
