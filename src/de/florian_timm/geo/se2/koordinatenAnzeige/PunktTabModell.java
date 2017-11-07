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
public class PunktTabModell extends AbstractTableModel {
	String[] spalten = { "PktNr", "Rechtswert", "Hochwert", "Höhe" };
	ArrayList<Punkt> punkte;
	Frame frame;

	private static final long serialVersionUID = 7299168976492896204L;

	public PunktTabModell(Frame frame, ArrayList<Punkt> punkte) {
		this.frame = frame;
		this.punkte = punkte;
	}

	@Override
	public int getColumnCount() {
		return spalten.length;
	}

	@Override
	public int getRowCount() {
		return punkte.size();
	}

	@Override
	public Object getValueAt(int row, int column) {

		Punkt p = punkte.get(row);

		switch (column) {
		case 0:
			return String.valueOf(p.getPunktNummer());
		case 1:
			return GMath.geoRund(p.getRechtsWert(), 3);
		case 2:
			return GMath.geoRund(p.getHochWert(), 3);
		case 3:
			return GMath.geoRund(p.getHoehe(), 4);
		}
		return null;
	}

	public String getColumnName(int column) {
		return spalten[column];
	}

	public boolean isCellEditable(int row, int column) {
		return true;
	}

	public void setValueAt(Object value, int row, int column) {
		if (!(getValueAt(row, column).equals(value))) {
			int result = JOptionPane.showConfirmDialog(null,
					"Möchten Sie die Daten wirklich ändern?", "Datenänderung",
					JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE);
			if (result == JOptionPane.OK_OPTION) {
				Punkt p = punkte.get(row);
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

				}
			}
		}
	}

}
