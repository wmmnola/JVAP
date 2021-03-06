/*
 * TestFrame.java
 *
 * Created on 9. Februar 2006, 10:35
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
 * @author  root
 */
public class TestFrame extends javax.swing.JFrame {
    
    javax.sound.midi.ShortMessage noteOn;
    javax.sound.midi.ShortMessage noteOff;
    javax.sound.midi.MetaMessage trackEnd;
    javax.sound.midi.Track track;
    int resolution=480;
    javax.sound.midi.Sequencer s;
    javax.sound.midi.Sequence abba;
    PatternEditor e;
    
    /** Creates new form TestFrame */
    public TestFrame() {
        initComponents();
        abba=null;
        try{
            abba=javax.sound.midi.MidiSystem.getSequence(
                 new java.io.File("/export/home/daniel/Downloads/Gimme_Gimme_Gimme.mid"));
            //abba=new javax.sound.midi.Sequence(javax.sound.midi.Sequence.PPQ,resolution, 1);
            //trackEnd=new javax.sound.midi.MetaMessage();
            noteOn=new javax.sound.midi.ShortMessage();
            noteOn.setMessage(javax.sound.midi.ShortMessage.NOTE_ON,0,60, 100);
            noteOff=new javax.sound.midi.ShortMessage();
            noteOff.setMessage(javax.sound.midi.ShortMessage.NOTE_OFF,0,60, 100);
            track=abba.getTracks()[0];
            /*track.add(new javax.sound.midi.MidiEvent(noteOn,resolution*0));
            track.add(new javax.sound.midi.MidiEvent(noteOff,(int)(((float)resolution)*0.5)));
            track.add(new javax.sound.midi.MidiEvent(noteOn,480*1));
            track.add(new javax.sound.midi.MidiEvent(noteOff,(int)(((float)resolution)*1.5)));
            track.add(new javax.sound.midi.MidiEvent(noteOn,480*2));
            track.add(new javax.sound.midi.MidiEvent(noteOff,(int)(((float)resolution)*2.5)));
            track.add(new javax.sound.midi.MidiEvent(noteOn,480*3));
            track.add(new javax.sound.midi.MidiEvent(noteOff,(int)(((float)resolution)*3.5)));*/
            //track.add(new javax.sound.midi.MidiEvent(trackEnd, (int)(((float)resolution)*4.0)));
            
        s=javax.sound.midi.MidiSystem.getSequencer();
        System.out.println(s.getClass().toString());
        s.setSequence(abba);
        s.setLoopStartPoint(4*480);
        s.setLoopEndPoint(12*480);
        s.setLoopCount(s.LOOP_CONTINUOUSLY);
        s.open();
        s.start();
        System.out.println("Division: "+abba.getDivisionType());
        System.out.println("Resolution: "+abba.getResolution());
        }catch(Exception e) { e.printStackTrace(); }
        e=new PatternEditor(abba);
        EditorKeyboard k=new EditorKeyboard(e);
        EditorHeader h=new EditorHeader(e);
        e.setHeader(h);
        //e.setBackground(java.awt.Color.BLUE);
        this.jScrollPane1.setViewportView(e);
        this.jScrollPane1.setColumnHeaderView(h);
        this.jScrollPane1.setRowHeaderView(k);  
        
        EditorHeader h2=new EditorHeader(e);
        
        j=new javax.swing.JViewport();
        j.setView(h2);
        this.jScrollPane1.getViewport().addChangeListener(new javax.swing.event.ChangeListener(){
            public void stateChanged(javax.swing.event.ChangeEvent cevt){
                java.awt.Point p=jScrollPane1.getViewport().getViewPosition();
                p.y=0;
                j.setViewPosition(p);
            }
        }
        );
        j.setSize(this.jScrollPane1.getViewport().getSize().width, this.jPanel1.getHeight());
        this.jPanel1.add(j);
        this.setSize(400,400);
    }
    
    javax.swing.JViewport j;
    
    /** This method is called from within the constructor to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the Form Editor.
     */
    // <editor-fold defaultstate="collapsed" desc=" Generated Code ">//GEN-BEGIN:initComponents
    private void initComponents() {
        jPanel2 = new javax.swing.JPanel();
        jSplitPane1 = new javax.swing.JSplitPane();
        jPanel1 = new javax.swing.JPanel();
        jPanel3 = new javax.swing.JPanel();
        jPanel5 = new javax.swing.JPanel();
        jScrollPane1 = new javax.swing.JScrollPane();
        jPanel4 = new javax.swing.JPanel();
        jButton1 = new javax.swing.JButton();
        jButton2 = new javax.swing.JButton();
        jButton3 = new javax.swing.JButton();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
        jSplitPane1.setDividerSize(3);
        jSplitPane1.setOrientation(javax.swing.JSplitPane.VERTICAL_SPLIT);
        jSplitPane1.setResizeWeight(0.7);
        jPanel1.setLayout(new java.awt.BorderLayout());

        jPanel3.setBackground(new java.awt.Color(102, 102, 102));
        jPanel3.setPreferredSize(new java.awt.Dimension(30, 10));
        jPanel1.add(jPanel3, java.awt.BorderLayout.WEST);

        jPanel1.add(jPanel5, java.awt.BorderLayout.CENTER);

        jSplitPane1.setRightComponent(jPanel1);

        jScrollPane1.setHorizontalScrollBarPolicy(javax.swing.ScrollPaneConstants.HORIZONTAL_SCROLLBAR_ALWAYS);
        jScrollPane1.setVerticalScrollBarPolicy(javax.swing.ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS);
        jSplitPane1.setLeftComponent(jScrollPane1);

        getContentPane().add(jSplitPane1, java.awt.BorderLayout.CENTER);

        jButton1.setText("Start");
        jButton1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton1ActionPerformed(evt);
            }
        });

        jPanel4.add(jButton1);

        jButton2.setText("Stop");
        jButton2.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton2ActionPerformed(evt);
            }
        });

        jPanel4.add(jButton2);

        jButton3.setText("Show Events");
        jButton3.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton3ActionPerformed(evt);
            }
        });

        jPanel4.add(jButton3);

        getContentPane().add(jPanel4, java.awt.BorderLayout.NORTH);

        pack();
    }
    // </editor-fold>//GEN-END:initComponents

    private void jButton3ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton3ActionPerformed
// TODO add your handling code here:
        e.notes.clear();
        e.ExtractNotes();
        for(int i=0;i<e.seq.getTracks().length;i++) {
            for(int j=0;j<e.seq.getTracks()[i].size();j++)
               System.out.println(e.seq.getTracks()[i].get(j).toString());
        }
        try{
        s=javax.sound.midi.MidiSystem.getSequencer();
        System.out.println(s.getClass().toString());
        s.setSequence(abba);
        s.setLoopStartPoint(0);
        s.setLoopEndPoint(s.getTickLength());
        s.setLoopCount(s.LOOP_CONTINUOUSLY);
        s.open();
        s.start();
        } catch(Exception e)
        {
            e.printStackTrace();
        }
    }//GEN-LAST:event_jButton3ActionPerformed

    private void jButton2ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton2ActionPerformed
// TODO add your handling code here:
        s.stop();
    }//GEN-LAST:event_jButton2ActionPerformed

    private void jButton1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton1ActionPerformed
// TODO add your handling code here:
        s.start();
    }//GEN-LAST:event_jButton1ActionPerformed
    
    /**
     * @param args the command line arguments
     */
    public static void main(String args[]) {
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                new TestFrame().setVisible(true);
            }
        });
    }
    
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton jButton1;
    private javax.swing.JButton jButton2;
    private javax.swing.JButton jButton3;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JPanel jPanel3;
    private javax.swing.JPanel jPanel4;
    private javax.swing.JPanel jPanel5;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JSplitPane jSplitPane1;
    // End of variables declaration//GEN-END:variables
    
}
