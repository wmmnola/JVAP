/*
 * editsettings.java
 *
 * Created on 3. April 2006, 23:34
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

package org.jvaptools.scripteffect;

/**
 *
 * @author  Daniel
 */
public class editsettings extends javax.swing.JFrame {
    
    vstgui gui;
   
    /** Creates new form editsettings */    
    public editsettings(vstgui gui) {
        this.gui=gui;        
        initComponents();
        this.updateSettings();
    }
    
    public void updateSettings() {
        this.TextSizeBox.setText(gui.getFontSize()); 
    }
    
    /** This method is called from within the constructor to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the Form Editor.
     */
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        TextSizeLabel = new javax.swing.JLabel();
        TextSizeBox = new javax.swing.JTextField();
        Okbutton = new javax.swing.JButton();
        jButton1 = new javax.swing.JButton();

        setTitle("Settings");
        setAlwaysOnTop(true);
        setResizable(false);
        getContentPane().setLayout(new java.awt.GridBagLayout());

        TextSizeLabel.setText("Text size:");
        getContentPane().add(TextSizeLabel, new java.awt.GridBagConstraints());

        TextSizeBox.setText("11");
        getContentPane().add(TextSizeBox, new java.awt.GridBagConstraints());

        Okbutton.setText("Ok");
        Okbutton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                OkbuttonActionPerformed(evt);
            }
        });
        getContentPane().add(Okbutton, new java.awt.GridBagConstraints());

        jButton1.setText("Show path");
        jButton1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton1ActionPerformed(evt);
            }
        });
        getContentPane().add(jButton1, new java.awt.GridBagConstraints());

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void jButton1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton1ActionPerformed
// TODO add your handling code here:
        javax.swing.JOptionPane.showMessageDialog(null,gui.pPlugin.directory);
    }//GEN-LAST:event_jButton1ActionPerformed

    private void OkbuttonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_OkbuttonActionPerformed
        this.setVisible(false);
        gui.setFontSize(this.TextSizeBox.getText()); 
        gui.saveSettingsToVST();
    }//GEN-LAST:event_OkbuttonActionPerformed
    
    
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton Okbutton;
    private javax.swing.JTextField TextSizeBox;
    private javax.swing.JLabel TextSizeLabel;
    private javax.swing.JButton jButton1;
    // End of variables declaration//GEN-END:variables
    
}