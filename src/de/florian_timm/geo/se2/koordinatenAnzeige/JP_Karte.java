package de.florian_timm.geo.se2.koordinatenAnzeige;

import java.awt.BorderLayout;
import java.awt.Cursor;
import java.awt.Point;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.awt.event.MouseWheelEvent;
import java.awt.event.MouseWheelListener;
import java.awt.geom.Point2D;

import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;

/**
 * JPanel mit der Karte
 *
 * @version 2015.06.14
 * @author Florian Timm
 */

public class JP_Karte extends JPanel implements ActionListener, MouseListener,
		MouseMotionListener, MouseWheelListener {
	private static final long serialVersionUID = -2352802676032105617L;
	JLabel jlab;
	protected Zeichenflaeche zeichenFlaeche;
	DatenVerwaltung pv;
	KoordinatenAnzeige koordAnz;
	// für Event-Handling benötigt
	Point2D.Double mousePressedCenter;
	Point mousePressedZF;

	/**
	 * @param koordAnzeige koordAnzeige
	 * @param punkteVerwaltung punkteVerwaltung
	 */
	public JP_Karte(KoordinatenAnzeige koordAnzeige,
			DatenVerwaltung punkteVerwaltung) {
		this.koordAnz = koordAnzeige;
		this.pv = punkteVerwaltung;

		// Einrichten des 1. Tabs: Karte
		this.setLayout(new BorderLayout());
		JPanel jp1_C = new JPanel();
		JPanel jp1_N = new JPanel();
		this.add(jp1_N, BorderLayout.NORTH);
		this.add(jp1_C, BorderLayout.CENTER);

		// // 1. Tab Norden (größer, kleiner, fit, Koordinaten)
		jp1_N.setLayout(new BorderLayout());
		jlab = new JLabel("---");
		jp1_N.add(jlab, BorderLayout.EAST);
		JPanel jp1_N_W = new JPanel();
		jp1_N.add(jp1_N_W, BorderLayout.WEST);

		JButton jb5 = new JButton("+");
		JButton jb6 = new JButton("-");
		JButton jb7 = new JButton("fit");
		jb5.setActionCommand("gross");
		jb6.setActionCommand("klein");
		jb7.setActionCommand("fit");
		jb5.addActionListener(this);
		jb6.addActionListener(this);
		jb7.addActionListener(this);
		jp1_N_W.add(jb6);
		jp1_N_W.add(jb7);
		jp1_N_W.add(jb5);

		// // 1. Tab Mitte (Karte mit Verschiebe-Buttons
		jp1_C.setLayout(new BorderLayout());
		zeichenFlaeche = new Zeichenflaeche(pv);
		zeichenFlaeche.addMouseListener(this);
		zeichenFlaeche.addMouseMotionListener(this);
		zeichenFlaeche.addMouseWheelListener(this);
		zeichenFlaeche.setCursor(new Cursor(Cursor.CROSSHAIR_CURSOR));
		jp1_C.add(zeichenFlaeche, BorderLayout.CENTER);

		JButton jb1 = new JButton("^");
		jb1.setActionCommand("nord");
		jb1.addActionListener(this);
		jp1_C.add(jb1, BorderLayout.NORTH);

		JButton jb2 = new JButton(">");
		jb2.setActionCommand("ost");
		jb2.addActionListener(this);
		jp1_C.add(jb2, BorderLayout.EAST);

		JButton jb3 = new JButton("<");
		jb3.setActionCommand("west");
		jb3.addActionListener(this);
		jp1_C.add(jb3, BorderLayout.WEST);

		JButton jb4 = new JButton("v");
		jb4.setActionCommand("sued");
		jb4.addActionListener(this);
		jp1_C.add(jb4, BorderLayout.SOUTH);

	}

	public void actionPerformed(ActionEvent ae) {
		switch (ae.getActionCommand()) {
		case "klein":
			zeichenFlaeche.kleiner();
			break;
		case "gleich":
			zeichenFlaeche.gleich();
			break;
		case "gross":
			zeichenFlaeche.groesser();
			break;
		case "nord":
			zeichenFlaeche.nord();
			break;
		case "west":
			zeichenFlaeche.west();
			break;
		case "ost":
			zeichenFlaeche.ost();
			break;
		case "sued":
			zeichenFlaeche.sued();
			break;
		case "fit":
			zeichenFlaeche.fit();
			break;
		}

		// zeichenFlaeche.repaint();
	}

	/**
	 * Zeigt Koordinate in Dialogfenster
	 * 
	 * @param x
	 *            x-Wert im Panel
	 * @param y
	 *            y-Wert im Panel
	 */
	public void zeigeKoordinateByKlick(int x, int y) {
		Point2D.Double gkP = zeichenFlaeche.koordZF2GK(x, y);
		JOptionPane.showConfirmDialog(null, "" + GMath.geoRund(gkP.getX(), 3)
				+ " " + GMath.geoRund(gkP.getY(), 3), "Koordinatenabgriff",
				JOptionPane.DEFAULT_OPTION);
	}

	/**
	 * Öffnet ein Dialogfenster zum Setzen eines neuen Punktes
	 * 
	 * @param x
	 *            x-Wert im Panel
	 * @param y
	 *            y-Wert im Panel
	 */
	public void neuerPunktByKlick(int x, int y) {

		NeuerPunktByKlick npbk = new NeuerPunktByKlick(x,y, zeichenFlaeche, pv);
		int result = JOptionPane.showConfirmDialog(null, npbk,
				"Neuer Punkt", JOptionPane.OK_CANCEL_OPTION);
		if (result == JOptionPane.OK_OPTION) {
			try {
				koordAnz.schreibeProtokoll("-------\nNeuer Punkt\n");
				koordAnz.schreibeProtokoll(pv.addPunkt(npbk.getPunkt()));
				// koordAnz.aktualisiereTabelle();
				zeichenFlaeche.repaint();
			} catch (Exception e) {

			}
		}

	}

	/*
	 * ###################################################################
	 * ######## behandelte Mouse(Wheel)Events #############
	 * ###################################################################
	 */

	/**
	 * Ereignisverarbeitung des Mouseevents Clicked
	 * 
	 * @param me
	 *            MouseEvent
	 */
	public void mouseClicked(MouseEvent me) {
		if (me.getButton() == MouseEvent.BUTTON1) {
			zeigeKoordinateByKlick(me.getX(), me.getY());
		} else if (me.getButton() == MouseEvent.BUTTON2) {
			zeichenFlaeche.fit();
		} else if (me.getButton() == MouseEvent.BUTTON3) {
			neuerPunktByKlick(me.getX(), me.getY());
		}
	}

	/**
	 * Ereignisverarbeitung des Mouseevents Pressed Speichert Punkt, um ihn in
	 * MouseDragged (Karte verschieben) zu verarbeiten
	 * 
	 * @param me
	 *            MouseEvent
	 */
	public void mousePressed(MouseEvent me) {
		mousePressedZF = new Point(me.getX(), me.getY());
		mousePressedCenter = zeichenFlaeche.getCenter();
		zeichenFlaeche.setCursor(new Cursor(Cursor.MOVE_CURSOR));
	}

	/**
	 * Ereignisverarbeitung des Mouseevents Moved Koordinatenanzeige wird
	 * aktualisiert
	 * 
	 * @param me
	 *            MouseEvent
	 */
	public void mouseMoved(MouseEvent me) {
		Point2D.Double p = zeichenFlaeche.koordZF2GK(me.getX(), me.getY());
		jlab.setText(GMath.geoRund(p.getX(), 3) + " "
				+ GMath.geoRund(p.getY(), 3));
	}

	/**
	 * Ereignisverarbeitung des Mouseevents Dragged Karte wird verschoben
	 * 
	 * @param me
	 *            MouseEvent
	 */
	public void mouseDragged(MouseEvent me) {
		zeichenFlaeche.schiebeKarte(mousePressedCenter, mousePressedZF,
				new Point(me.getX(), me.getY()));
	}

	/**
	 * Ereignisverarbeitung des Mouseevents M
	 * 
	 * @param mwe
	 *            MouseWheelEvent
	 */
	public void mouseWheelMoved(MouseWheelEvent mwe) {

		if (mwe.getPreciseWheelRotation() < 0) {
			zeichenFlaeche.groesser(0.95, new Point(mwe.getX(), mwe.getY()));
		} else {
			zeichenFlaeche.kleiner(1.05, new Point(mwe.getX(), mwe.getY()));
		}
	}

	/**
	 * Ereignisverarbeitung des Mouseevents Released
	 * 
	 * @param me
	 *            MouseEvent
	 */
	public void mouseReleased(MouseEvent me) {
		zeichenFlaeche.setCursor(new Cursor(Cursor.CROSSHAIR_CURSOR));
	}

	/*
	 * ###################################################################
	 * ######## nicht behandelte Mouse(Wheel)Events #############
	 * ###################################################################
	 */

	/**
	 * Ereignisverarbeitung des Mouseevents Exited
	 * 
	 * @param me
	 *            MouseEvent
	 */
	public void mouseExited(MouseEvent me) {

	}

	/**
	 * Ereignisverarbeitung des Mouseevents Entered
	 * 
	 * @param me
	 *            MouseEvent
	 */
	public void mouseEntered(MouseEvent me) {
	}
}