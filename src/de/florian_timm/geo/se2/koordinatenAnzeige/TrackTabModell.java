package de.florian_timm.geo.se2.koordinatenAnzeige;

import java.awt.Frame;
import java.util.ArrayList;

import javax.swing.JOptionPane;
import javax.swing.table.AbstractTableModel;

/**
 * Tabelle mit Punkten
 * 
 * @author Florian Timm
 * @version 2015.06.14
 */
public class TrackTabModell extends AbstractTableModel {
	String[] spalten = { "Länge", "PktAnzahl" };
	ArrayList<PolyLinie> polyLinien;
	Frame frame;

	private static final long serialVersionUID = 7299168976492896204L;

	public TrackTabModell(Frame frame, ArrayList<PolyLinie> polyLinien) {
		this.frame = frame;
		this.polyLinien = polyLinien;
	}

	@Override
	public int getColumnCount() {
		return spalten.length;
	}

	@Override
	public int getRowCount() {
		return polyLinien.size();
	}

	@Override
	public Object getValueAt(int row, int column) {

		PolyLinie p = polyLinien.get(row);

		switch (column) {
		case 0:
			return GMath.geoRund(p.getLaenge(),3);
		case 1:
			return String.valueOf(p.getPunkte().size());
		}
		return null;
	}

	public String getColumnName(int column) {
		return spalten[column];
	}

	public boolean isCellEditable(int row, int column) {
		return false;
	}

	public void setValueAt(Object value, int row, int column) {
		if (!(getValueAt(row, column).equals(value))) {
			int result = JOptionPane.showConfirmDialog(null,
					"Möchten Sie die Daten wirklich ändern?", "Datenänderung",
					JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE);
			if (result == JOptionPane.OK_OPTION) {
				/*PolyLinie p = polyLinien.get(row);
				String wert = ((String) value).trim().replaceAll(",", ".");
				try {
					switch (column) {
					case 0:
						p.setPunktNummer(Integer.valueOf(wert));
						break;
					case 1:
						p.setRechtsWert(Double.valueOf(wert));
						break;
					case 2:
						p.setHochWert(Double.valueOf(wert));
						break;
					case 3:
						p.setHoehe(Double.valueOf(wert));
					}
					// TODO Repaint ZF
				} catch (NumberFormatException nfe) {

				}*/
			}
		}
	}

}
