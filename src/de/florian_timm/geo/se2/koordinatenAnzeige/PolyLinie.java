package de.florian_timm.geo.se2.koordinatenAnzeige;

import java.awt.geom.Point2D;
import java.util.ArrayList;

/**
 * Klasse zur Speicherung einer PolyLinie
 *
 * @version 2015.06.14
 * @author Florian Timm
 */
public class PolyLinie {
	ArrayList<Point2D.Double> punkte = new ArrayList<Point2D.Double>();
	String name = "";

	/**
	 * @param punkte
	 *            Punkte der Polylinie als Array
	 */
	public PolyLinie(Point2D.Double[] punkte) {
		for (Point2D.Double punkt : punkte) {
			this.punkte.add(punkt);
		}
	}

	/**
	 * @param punkte
	 *            Punkte der Polylinie als ArrayList
	 */
	public PolyLinie(ArrayList<Point2D.Double> punkte) {
		this.punkte.addAll(punkte);
	}

	/**
	 * @param name
	 *            Name der PolyLinie
	 * @param punkte
	 *            Punkte der Polylinie als Array
	 */
	public PolyLinie(String name, Point2D.Double[] punkte) {
		this(punkte);
		this.name = name;
	}

	/**
	 * @param name
	 *            Name der PolyLinie
	 * @param punkte
	 *            Punkte der Polylinie als ArrayList
	 */
	public PolyLinie(String name, ArrayList<Point2D.Double> punkte) {
		this(punkte);
		this.name = name;
	}

	/**
	 * Gibt alle Punkte der Polylinie wieder
	 * 
	 * @return ArrayList aller Punkte
	 */
	public ArrayList<Point2D.Double> getPunkte() {
		return punkte;
	}

	/**
	 * Gibt die Länge in Koordinateneinheiten zurück
	 * 
	 * @return Länge der PolyLinie in KoordinatenEinheiten
	 */
	public double getLaenge() {
		double strecke = 0;
		if (punkte.size() > 0) {
			for (int i = 1; i < punkte.size(); i++) {
				strecke += punkte.get(i - 1).distance(punkte.get(i));
			}
		} // end of if
		return strecke;
	}
	
	public String getName() {
		return name;
	}
}