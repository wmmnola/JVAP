/*
 * EditorHeader.java
 *
 * Created on 10. Februar 2006, 13:44
 *
 * JVAPTools - Tools for rapid Java-VST Audio Plugin creation Copyright (C) 2006 Daniel Reinert
 * This library is free software; you can redistribute it and/or modify it under the terms of the
 * GNU Lesser General Public License as published by the Free Software Foundation; either version 2.1
 * of the License, or (at your option) any later version.
 *
 * This library is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY;
 * without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.
 * See the GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License along with this library;
 * if not, write to the Free Software Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA 02111-1307 USA
 */

package org.jvaptools.PatternEditor;

/**
 *
 * @author root
 */
public class EditorHeader  extends javax.swing.JPanel {
    
    PatternEditor editor;
    
    /** Creates a new instance of EditorHeader */
    public EditorHeader(PatternEditor e){
        super();
        editor=e;
        int sx=e.getWidth();
        int sy=30;
        this.setPreferredSize(new java.awt.Dimension(sx,sy));
        this.setSize(sx,sy);
        clip=new java.awt.Rectangle();
        
        javax.swing.event.MouseInputAdapter ma=new javax.swing.event.MouseInputAdapter()
        {
            
            
            public void mouseClicked(java.awt.event.MouseEvent e){
                if(e.getClickCount()==2) {                    
                 //Fix Line
                }
            }
            
            
            public void mousePressed(java.awt.event.MouseEvent e){
                dragedbar=-1; 
                //Start Draggingint bs=editor.getBarspace();
                int bs=editor.getBarspace();                
                float zoom=editor.getZoom();
                int tickstart=(int)(((float)e.getPoint().x)/zoom);
                int bar=java.lang.Math.round((float)tickstart/(float)bs);
                if(java.lang.Math.abs(e.getPoint().x-bar*bs*zoom)<4){
                    dragedbar=bar;
                    dragpos=bar*bs;
                }
                bardragged=false;
            }
            
            public void mouseReleased(java.awt.event.MouseEvent e) {
                //End Dragging
                if(dragedbar!=-1) {
                    dragedbar=-1;
                    bardragged=false;
                    float zoom=editor.getZoom();          
                    int oldx=(int)((float)dragpos*zoom);
                    dragpos=-1;
                    repaint(oldx-1,0,3,getHeight()); 
                    editor.repaint(oldx-1,0,3,editor.getHeight());                    
                }
                bardragged=false;
                dragedbar=-1;
            }
            
            public void mouseMoved(java.awt.event.MouseEvent e) {
                //System.out.println("Mouse moved!");  
            }
            
            public void mouseDragged(java.awt.event.MouseEvent e){
                
                //Drag Line
                if(dragedbar!=-1) {
                    float zoom=editor.getZoom();          
                    int oldx=(int)((float)dragpos*zoom);
                    dragpos=(int)(((float)e.getPoint().x)/zoom);
                    bardragged=true;                      
                    repaint(oldx-1,0,3,getHeight()); 
                    editor.repaint(oldx-1,0,3,editor.getHeight());
                    repaint(e.getPoint().x-1,0,3,getHeight());
                    editor.repaint(e.getPoint().x-1,0,3,editor.getHeight());
                }
            }


        };        
        this.addMouseListener(ma);
        this.addMouseMotionListener(ma);
        
    }
   
    public int dragedbar=-1;
    public boolean bardragged;
    public int dragpos=-1;
    
    public boolean isBarDragged()
    {
        return this.bardragged;
    }

    public int getDraggedPos()
    {
        return this.dragpos;
    }
    
    java.awt.Rectangle clip;
    
    protected void paintComponent(java.awt.Graphics g){
       
       //Draw Background       
       clip.x=0;
       clip.y=0;
       clip.height=this.getHeight();
       clip.width=this.getWidth();
       g.getClipBounds(clip);
       java.awt.Color oldc=g.getColor();
       g.setColor(this.getBackground());
       g.fillRect(clip.x,clip.y, clip.width+1, clip.height+1);
       g.setColor(this.getForeground());
       //Draw Lines
       int tickpos=0;       
       int bs=editor.getBarspace();
       int bars=(int)editor.getTickLen()/bs;
       float zoom=editor.getZoom();
       for(int i=1;i<bars;i++)
       {
           tickpos=tickpos+bs;
           int xpos=java.lang.Math.round(tickpos*zoom);
           if((xpos>=clip.x)&&(xpos<=clip.x+clip.width)) {
               int ystart=(clip.y>20)?clip.y:20;
               g.drawLine(xpos,ystart, xpos, clip.y+clip.height);               
           }
           if((xpos+30>=clip.x)&&(xpos-30<=clip.x+clip.width)&&((i)%4==0)) {
                   g.drawString(""+(((i)/4)+1),xpos-5,17);
           }
       }
       if(this.dragedbar!=-1) {
           int xpos=java.lang.Math.round(dragpos*zoom);
          if((xpos>=clip.x)&&(xpos<=clip.x+clip.width)) {
               int ystart=(clip.y>20)?clip.y:20;
               g.setColor(java.awt.Color.RED);
               g.drawLine(xpos,ystart, xpos, clip.y+clip.height);               
           } 
       }
       g.setColor(oldc);
   }
    
}
