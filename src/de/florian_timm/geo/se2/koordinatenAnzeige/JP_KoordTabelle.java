package de.florian_timm.geo.se2.koordinatenAnzeige;

import java.util.Arrays;

import java.awt.BorderLayout;
import java.awt.Frame;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;

/**
 * Tabelle mit den aktuellen Tracks
 * 
 * @author Florian Timm
 * @version 2015.06.14
 */
public class JP_KoordTabelle extends JPanel implements ActionListener {
  private static final long serialVersionUID = 8501397349404899786L;
  public PunktTabModell ptm;
  DatenVerwaltung datenVerwaltung;
  JTable jtab;
  
  public JP_KoordTabelle(Frame frame, DatenVerwaltung datenVerwaltung) {
    this.datenVerwaltung = datenVerwaltung;
    
    this.setLayout(new BorderLayout());
    
    JPanel jp_N = new JPanel();
    JButton jb_1 = new JButton("markierte Punkte l�schen");
    jb_1.addActionListener(this);
    jb_1.setActionCommand("pktloesch");
    jp_N.add(jb_1);
    this.add(jp_N, BorderLayout.NORTH);
    
    ptm = new PunktTabModell(frame, datenVerwaltung.getPunkte());
    jtab = new JTable ( ptm );
    jtab.setFillsViewportHeight(true);
    jtab.setAutoCreateRowSorter(true);
      JScrollPane sp = new JScrollPane ( jtab );
    this.add(sp, BorderLayout.CENTER);
    
    
  }
  
  @Override
  public void actionPerformed(ActionEvent ae) {
    switch (ae.getActionCommand()) {
    case "pktloesch":
      int[] select = jtab.getSelectedRows();
      int[] pkt_auswahl = new int[select.length];
      for (int i = 0; i < select.length; i++) {
        pkt_auswahl[i] = jtab.convertRowIndexToModel(select[i]);
      }
      
      Arrays.sort(pkt_auswahl);
      for (int i = select.length -1; i >= 0; i--) {
        datenVerwaltung.delPkt(pkt_auswahl[i]);
      }
      
      
      aktualisiereTabelle();
      break;
    }

    // zf.repaint();
  }

  /**
   * stellt alle aktuell, eingelesene Daten da
   */
  protected void aktualisiereTabelle() {
    ptm.fireTableDataChanged();
  }

}
