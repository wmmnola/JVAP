/*
 * VstParameterControl.java
 *
 * Created on 1. Februar 2006, 17:30
 */

package org.jvaptools.vstparam2midi;

/**
 *
 * @author  Daniel
 */
public class VstParameterControl extends javax.swing.JPanel {
    
    String name;
    float val;
    
    /** Creates new form VstParameterControl */
    public VstParameterControl(String n,float f) {
        name=n;
        val=f;
        initComponents();
        this.setSize(40, this.getHeight());
        this.Titel.setText(name);
        this.jSlider1.setValue((int)f*100);
        this.Val.setText(""+f);
    }
    
    /** This method is called from within the constructor to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the Form Editor.
     */
    // <editor-fold defaultstate="collapsed" desc=" Generated Code ">//GEN-BEGIN:initComponents
    private void initComponents() {
        Titel = new javax.swing.JLabel();
        jSlider1 = new javax.swing.JSlider();
        Val = new javax.swing.JLabel();

        setLayout(new java.awt.BorderLayout());

        Titel.setText("Name");
        Titel.setPreferredSize(new java.awt.Dimension(10, 14));
        add(Titel, java.awt.BorderLayout.NORTH);

        jSlider1.setOrientation(javax.swing.JSlider.VERTICAL);
        jSlider1.setPreferredSize(new java.awt.Dimension(10, 100));
        add(jSlider1, java.awt.BorderLayout.CENTER);

        Val.setText("Val");
        Val.setPreferredSize(new java.awt.Dimension(10, 14));
        add(Val, java.awt.BorderLayout.SOUTH);

    }
    // </editor-fold>//GEN-END:initComponents
    
    
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JLabel Titel;
    private javax.swing.JLabel Val;
    private javax.swing.JSlider jSlider1;
    // End of variables declaration//GEN-END:variables
    
}
