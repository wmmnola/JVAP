/*
 * vstgui.java
 *
 * Created on 29. Januar 2006, 22:47
 */

package org.jvaptools.vstparam2midi;
import jvst.wrapper.*;
import jvst.wrapper.gui.VSTPluginGUIRunner;

/**
 *
 * @author  Daniel
 */
public class vstgui extends VSTPluginGUIAdapter {
    
    /** Creates new form vstgui */
    public vstgui(VSTPluginGUIRunner r, VSTPluginAdapter plug) {
        super(r,plug);
        initComponents();        
        this.setSize(500,500);
        this.setPreferredSize(new java.awt.Dimension(500, 500));
    }
    
     private VSTPluginAdapter pPlugin;
      
  public void undecorate()
  {               
      java.awt.Dimension d=this.getSize();
      super.undecorate();
      this.setSize(d);
  }
      
     public void init(VSTPluginAdapter e) {
       this.pPlugin = e;
       this.NameLabel.setText(e.getEffectName());
       for(int i=0;i<e.getNumParams();i++) {
           this.jPanel3.add(new VstParameterControl(e.getParameterName(i), e.getParameter(i)));
       }
       this.jPanel3.setSize(e.getNumParams()*30, 200);       
    }
    
    /** This method is called from within the constructor to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the Form Editor.
     */
    // <editor-fold defaultstate="collapsed" desc=" Generated Code ">//GEN-BEGIN:initComponents
    private void initComponents() {
        jPanel1 = new javax.swing.JPanel();
        jPanel2 = new javax.swing.JPanel();
        NameLabel = new javax.swing.JLabel();
        Transmit = new javax.swing.JButton();
        jPanel3 = new javax.swing.JPanel();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
        jPanel1.setLayout(new java.awt.BorderLayout());

        NameLabel.setText("jLabel1");
        jPanel2.add(NameLabel);

        Transmit.setText("Transmit all parameter");
        Transmit.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                TransmitActionPerformed(evt);
            }
        });

        jPanel2.add(Transmit);

        jPanel1.add(jPanel2, java.awt.BorderLayout.NORTH);

        jPanel3.setLayout(null);

        jPanel1.add(jPanel3, java.awt.BorderLayout.CENTER);

        getContentPane().add(jPanel1, java.awt.BorderLayout.CENTER);

        pack();
    }
    // </editor-fold>//GEN-END:initComponents

    private void TransmitActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_TransmitActionPerformed
// TODO add your handling code here:
        ((vstmain)(this.pPlugin)).transmitAllParameterToMidi(false);
    }//GEN-LAST:event_TransmitActionPerformed
        
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JLabel NameLabel;
    private javax.swing.JButton Transmit;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JPanel jPanel3;
    // End of variables declaration//GEN-END:variables
    
}
