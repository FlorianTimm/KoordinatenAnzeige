package de.florian_timm.geo.se2.koordinatenAnzeige;

import java.awt.BorderLayout;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.Frame;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;

import javax.swing.JDialog;
import javax.swing.JEditorPane;
import javax.swing.JScrollPane;
import javax.swing.SwingUtilities;

public class HilfeFenster extends JDialog implements ActionListener {
  private static final long serialVersionUID = 4729936016625318080L;

  public HilfeFenster(Frame frame) {
    super(frame);
    this.setTitle("KoordAnzeige Hilfe");
    this.setPreferredSize(new Dimension(500, 500));
    JEditorPane jep = new JEditorPane();
    jep.setContentType("text/html");
    final JScrollPane jsp = new JScrollPane(jep);
    java.net.URL helpURL = HilfeFenster.class
        .getResource("hilfe/index.htm");
    if (helpURL != null) {
      try {
        jep.setPage(helpURL);
      } catch (IOException e) {
        System.out.println("Hilfedatei nicht gefunden");
      }
    }
    SwingUtilities.invokeLater(new Runnable() {
      public void run() {
        jsp.getViewport().setViewPosition(new java.awt.Point(0, 0));
      }
    });
    jep.setEditable(false);
    Container cp = this.getContentPane();
    cp.setLayout(new BorderLayout());
    cp.add(jsp, BorderLayout.CENTER);
    this.pack();

    this.setVisible(true);
  }

  @Override
  public void actionPerformed(ActionEvent e) {
    // TODO Automatisch generierter Methodenstub

  }

}
