package de.florian_timm.geo.se2.koordinatenAnzeige;

import java.awt.BorderLayout;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.util.ArrayList;

import javax.swing.JCheckBoxMenuItem;
import javax.swing.JFrame;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.JTabbedPane;
import javax.swing.JTextArea;

/**
 * KoordinatenAnzeige
 * 
 * Programm zur Koordinatenverwaltung und -anzeige
 *
 * @version 2015.07.06
 * @author Florian Timm
 */

public class KoordinatenAnzeige extends JFrame implements ActionListener {
  private static final long serialVersionUID = 7185727039889797388L;
  // GUI-Elemente
  JCheckBoxMenuItem jmiWMS;
  JTextArea jta;
  JP_KoordTabelle jpKoordTabelle;
  JP_Karte jpKarte;
  JP_TrackTabelle jpTrackTabelle;

  // für Punkteverwaltung
  DatenVerwaltung pv;
  ArrayList<Punkt> punkte = new ArrayList<Punkt>();

  /**
   * Programmeinstiegspunkt
   * 
   * @param args
   *            Kommandozeilenargumente
   */
  public static void main(String[] args) {
    new KoordinatenAnzeige(args);
  }

  /**
   * Konstruktor des JFrame's / Erzeugung des GUI
   * 
   * @param args
   *            Kommandozeilenargumente
   */
  protected KoordinatenAnzeige(String[] args) {
    // Initalisierung der Punktverwaltung
    pv = new DatenVerwaltung(this);

    // Einrichten des Hauptfensters
    this.setTitle("KoordinatenAnzeige");
    this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    this.setPreferredSize(new Dimension(700, 700));
    Container cp = this.getContentPane();

    // Einrichten der Menübar
    JMenuBar jmb = new JMenuBar();
    cp.add(jmb, BorderLayout.NORTH);

    // // Datei-Menü
    JMenu jmDatei = new JMenu("Datei");
    JMenuItem jmOeffnen = new JMenuItem("Öffnen...");
    jmOeffnen.setActionCommand("oeffnen");
    jmOeffnen.addActionListener(this);
    JMenuItem jmSpeichern = new JMenuItem("Punkte speichern...");
    jmSpeichern.setActionCommand("speichern");
    jmSpeichern.addActionListener(this);
    JMenuItem jmLeeren = new JMenuItem("Koordinaten löschen");
    jmLeeren.setActionCommand("leeren");
    jmLeeren.addActionListener(this);
    JMenuItem jmEnde = new JMenuItem("Beenden");
    jmEnde.setActionCommand("ende");
    jmEnde.addActionListener(this);

    jmDatei.add(jmOeffnen);
    jmDatei.addSeparator();
    jmDatei.add(jmSpeichern);
    jmDatei.add(jmLeeren);
    jmDatei.addSeparator();
    jmDatei.add(jmEnde);

    // // Datenbank-Menü
    JMenu jmDB = new JMenu("Datenbank");
    JMenuItem jmiMySql = new JMenuItem("mySQL...");
    jmiMySql.setActionCommand("mysql");
    jmiMySql.addActionListener(this);
    jmDB.add(jmiMySql);

    // // Hintergrund-Menü
    JMenu jmBG = new JMenu("Hintergrund");
    jmiWMS = new JCheckBoxMenuItem("WMS: GK BKG");
    jmiWMS.setActionCommand("usewms");
    jmiWMS.addActionListener(this);
    jmBG.add(jmiWMS);

    // // Info-Menü
    JMenu jmInfo = new JMenu("Info");
    JMenuItem jmiHilfe = new JMenuItem("Hilfe");
    jmiHilfe.addActionListener(this);
    jmiHilfe.setActionCommand("hilfe");
    JMenuItem jmiInfo = new JMenuItem("Info...");
    jmiInfo.addActionListener(this);
    jmiInfo.setActionCommand("info");

    jmInfo.add(jmiHilfe);
    jmInfo.add(jmiInfo);

    jmb.add(jmDatei);
    jmb.add(jmDB);
    jmb.add(jmBG);
    jmb.add(jmInfo);

    // Einrichten des TabbedPane
    JTabbedPane jtp = new JTabbedPane();
    jpKarte = new JP_Karte(this, pv);
    JPanel jp2 = new JPanel();
    jpKoordTabelle = new JP_KoordTabelle(this, pv);
    jpTrackTabelle = new JP_TrackTabelle(this, pv);
    jtp.addTab("Karte", jpKarte);
    jtp.addTab("Transformation", jp2);
    jtp.addTab("Koordinatenübersicht", jpKoordTabelle);
    jtp.addTab("Tracks", jpTrackTabelle);

    // TextArea mit Bearbeitungsverlauf
    jta = new JTextArea();
    JScrollPane jsp = new JScrollPane(jta);

    // Weitere Einrichtung des Hauptfenstes
    // // Flexibles Teilen zwischen JTabbedPane und dem TextArea
    JSplitPane jsplitt = new JSplitPane(JSplitPane.VERTICAL_SPLIT);
    jsplitt.add(jtp);
    jsplitt.add(jsp);
    jsplitt.setDividerLocation(500);
    jsplitt.setResizeWeight(0.9);
    cp.add(jsplitt, BorderLayout.CENTER);

    // // Fenster anzeigen
    this.pack();
    this.setVisible(true);

    // Falls Datei mit beim Start übergeben wurde, diese öffnen
    if (args.length > 1) {
      pv.leseDatei(new File(args[args.length]));
    }

  }

  /**
   * Ereignisverarbeitung der Buttons
   * 
   * @param ae
   *            ActionEvent
   */
  public void actionPerformed(ActionEvent ae) {
    switch (ae.getActionCommand()) {
    case "oeffnen":
      this.oeffnen();
      break;
    case "ende":
      System.exit(0);
      break;
    case "usewms":
      jpKarte.zeichenFlaeche.useWMS(jmiWMS.getState());
      jpKarte.zeichenFlaeche.repaint();
      break;
    case "leeren":
      pv.leerePunktliste();
      pv.leerePolyLinienListe();
      jpKoordTabelle.aktualisiereTabelle();
      jta.setText("");
      jpKarte.zeichenFlaeche.repaint();
      break;
    case "speichern":
      pv.speicherPunkte();
      break;
    case "info":
      JOptionPane.showMessageDialog(null, "(cc-by-sa 3.0) Florian Timm\nEmail: florian.timm@hcu-hamburg.de\nVersion: 2015-07-06", "KoordinatenAnzeige", JOptionPane.INFORMATION_MESSAGE);
      break;
    case "mysql":
      JOptionPane.showMessageDialog(null, "Noch nicht implementiert!", "Hinweis", JOptionPane.INFORMATION_MESSAGE);
      break;
    case "hilfe":
      new HilfeFenster(this);
      break;
    }

    // zeichenFlaeche.repaint();
  }

  /**
   * bereitet den öffenen Dialog vor und stellt eingelesene Daten da
   */
  private void oeffnen() {
    jta.append("-------\nKoordinaten-Import\n");
    try {
      File f = pv.waehleDatei();
      jta.append(pv.leseDatei(f));
    } catch (Exception e) {

    }
    jpKoordTabelle.aktualisiereTabelle();
    jpTrackTabelle.aktualisiereTabelle();
    jpKarte.zeichenFlaeche.fit();
  }

  protected void schreibeProtokoll(String text) {
    jta.append(text);
  }
}