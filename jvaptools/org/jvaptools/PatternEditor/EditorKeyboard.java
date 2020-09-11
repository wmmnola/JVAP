/*
 * EditorKeyboard.java
 *
 * Created on 10. Februar 2006, 14:22
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
public class EditorKeyboard extends javax.swing.JPanel {
    
    PatternEditor editor;
    
    /** Creates a new instance of EditorKeyboard */
    public EditorKeyboard(PatternEditor e){
        super();
        editor=e;
        int sx=30;
        int sy=e.getHeight();
        this.setPreferredSize(new java.awt.Dimension(sx,sy));
        this.setSize(sx,sy);
        clip=new java.awt.Rectangle();
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
       //g.setColor(this.getBackground());
       //g.fillRect(clip.x,clip.y, clip.width+1, clip.height+1);
       java.awt.Color fg=this.getForeground();
       //Draw Lines
       int ypos=-1;
       int noteheight=editor.getNoteheight();
       for(int i=0;i<128;i++)
       {   ypos=ypos+noteheight;
           g.setColor(fg);
           if((ypos>=clip.y)&&(ypos<=(clip.y+clip.height)))
               g.drawLine(clip.x,ypos,clip.x+clip.width,ypos);
           if((ypos>=clip.y)&&(ypos-noteheight<=(clip.y+clip.height)))
           {
               int n=(127-i)%12;
               if(n==1||n==3||n==6||n==8||n==10) g.setColor(java.awt.Color.BLACK);
               else g.setColor(java.awt.Color.WHITE);
               g.fillRect(clip.x, ypos-noteheight+1, clip.width, noteheight-1);
               if(i==127-60) {
                   g.setColor(fg);
                   g.drawString("C4",2, ypos);
               }
                   
           }           
       }       
       g.setColor(oldc);
   }  
}
