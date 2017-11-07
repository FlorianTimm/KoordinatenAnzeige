package de.florian_timm.geo.se2.koordinatenAnzeige;

import java.awt.BorderLayout;
import java.awt.Container;
import java.awt.Dialog;
import java.awt.Dimension;
import java.awt.Frame;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.Collections;

import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;

/**
 * Dialog zur Überprüfung von eingelesenen Koordinaten,setzen des
 * Bezugssystemes, auswählen der Transformation
 *
 * @version 2015.06.14
 * @author Florian Timm
 */

public class KoordKontrolle extends JDialog implements ActionListener {
	private static final long serialVersionUID = -1056715157416507164L;
	public final static int OK = 1;
	public final static int ABBRUCH = 0;
	int auswahl = 0;
	ArrayList<Punkt> alle, uebernehmen;
	ArrayList<PolyLinie> pL_alle, pL_uebernehmen;
	JTable jtab, jtab2;
	JComboBox<String> koordSystem;

	/**
	 * @param frame
	 *            Frame
	 * @param punkte
	 *            Punkte
	 * @param polyLinie
	 *            PolyLinien
	 */
	public KoordKontrolle(Frame frame, ArrayList<Punkt> punkte,
			ArrayList<PolyLinie> polyLinie) {
		this(frame, punkte, polyLinie, KoordTransformer.LOKAL);
	}

	/**
	 * @param frame
	 *            Frame
	 * @param punkte
	 *            Punkte
	 * @param polyLinie
	 *            PolyLinien
	 * @param koordSys
	 *            KoordinatenSystem
	 */
	public KoordKontrolle(Frame frame, ArrayList<Punkt> punkte,
			ArrayList<PolyLinie> polyLinie, int koordSys) {
		super(frame, "KoordKontrolle (Tracklängen stimmen nicht)", Dialog.ModalityType.DOCUMENT_MODAL);
		alle = punkte;
		pL_alle = polyLinie;
		Container cp = this.getContentPane();
		cp.setLayout(new BorderLayout());
		PunktTabModell ptm = new PunktTabModell(frame, punkte);
		jtab = new JTable(ptm);
		jtab.setFillsViewportHeight(true);
		jtab.setAutoCreateRowSorter(true);

		TrackTabModell ttm = new TrackTabModell(frame, polyLinie);
		jtab2 = new JTable(ttm);
		JScrollPane jsp = new JScrollPane(jtab);

		jtab2.setFillsViewportHeight(true);
		jtab2.setAutoCreateRowSorter(true);

		JScrollPane jsp2 = new JScrollPane(jtab2);
		JPanel jp4 = new JPanel();
		jp4.setLayout(new GridLayout(2, 1));
		jp4.add(jsp, BorderLayout.NORTH);
		jp4.add(jsp2, BorderLayout.CENTER);
		cp.add(jp4, BorderLayout.CENTER);

		koordSystem = new JComboBox<String>(KoordTransformer.SYSTEME);
		koordSystem.setSelectedIndex(koordSys);

		JPanel jp1 = new JPanel();
		JButton jb1 = new JButton("Alle");
		jb1.addActionListener(this);
		jb1.setActionCommand("alleuebernehmen");

		JButton jb3 = new JButton("Markierte");
		jb3.addActionListener(this);
		jb3.setActionCommand("uebernehmen");

		JButton jb2 = new JButton("Abbrechen");
		jb2.addActionListener(this);
		jb2.setActionCommand("abbrechen");
		jp1.add(koordSystem);
		jp1.add(jb1);
		jp1.add(jb3);
		jp1.add(jb2);
		cp.add(jp1, BorderLayout.SOUTH);

		this.setPreferredSize(new Dimension(400, 400));
		jtab.selectAll();
		jtab2.selectAll();
		this.pack();
		this.setVisible(true);

	}

	public void actionPerformed(ActionEvent ae) {
		switch (ae.getActionCommand()) {
		case "abbrechen":
			auswahl = 0;
			break;

		case "uebernehmen":
			auswahl = 1;
			uebernehmen = new ArrayList<Punkt>();
			int[] select = jtab.getSelectedRows();
			for (int i = 0; i < select.length; i++) {
				uebernehmen
						.add(alle.get(jtab.convertRowIndexToModel(select[i])));
			} // end of for
			int[] select2 = jtab2.getSelectedRows();
			for (int i = 0; i < select2.length; i++) {
				pL_uebernehmen.add(pL_alle.get(jtab2
						.convertRowIndexToModel(select2[i])));
			} // end of for
				// pL_uebernehmen = pL_alle;
			Collections.sort(uebernehmen);
			break;
		case "alleuebernehmen":
			auswahl = 1;
			uebernehmen = alle;
			pL_uebernehmen = pL_alle;
			Collections.sort(uebernehmen);
			break;
		}

		this.setVisible(false);

	}

	public int getAuswahl() {
		return auswahl;
	}

	public ArrayList<Punkt> getPunktAuswahl() {
		return uebernehmen;
	}

	public ArrayList<PolyLinie> getPolyLinieAuswahl() {
		return pL_uebernehmen;
	}
	public int getKoordSys() {
		return koordSystem.getSelectedIndex();
	}
}