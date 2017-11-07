package de.florian_timm.geo.se2.koordinatenAnzeige;


/**
 * Klasse für geodätische Berechnungen
 * 
 * @author Florian Timm
 * @version 2015.06.14
 */

public class GMath {
	/**
	 * Enthält den Wert von Rho = {@value #RHO}
	 */
	public static final double RHO = 200. / Math.PI;

	/**
	 * Wandelt einen Winkel von Radiant in Gon um
	 * 
	 * @param winkel
	 *            Winkel in Radiant
	 * @return Winkel in Gon
	 */
	public static double toGon(double winkel) {
		return winkel / Math.PI * 200;
	}

	/**
	 * Wandelt einen Winkel von Gon in Radiant um
	 * 
	 * @param winkel
	 *            Winkel in Gon
	 * @return Winkel in Radiant
	 */
	public static double toRadiant(double winkel) {
		return Math.toRadians(winkel / 0.9);
	}

	/**
	 * Berechnet die Strecke zwischen zwei Punkten
	 * 
	 * @param rechtswertA
	 *            Rechtswert des Ausgangspunktes
	 * @param hochwertA
	 *            Hochwert des Ausgangspunktes
	 * @param rechtswertB
	 *            Rechtswert des Endpunktes
	 * @param hochwertB
	 *            Hochwert des Endpunktes
	 * @return Strecke zwischen den Punkten
	 */
	public static double strecke(double rechtswertA, double hochwertA,
			double rechtswertB, double hochwertB) {
		return Math.sqrt(Math.pow(rechtswertA - rechtswertB, 2)
				+ Math.pow(hochwertA - hochwertB, 2));
	}

	/**
	 * Berechnet die Strecke zwischen zwei Punkten
	 * 
	 * @param punktA
	 *            Ausgangspunktes
	 * @param punktB
	 *            Endpunktes
	 * @return Strecke zwischen den Punkten
	 */
	public static double strecke(Punkt punktA, Punkt punktB) {
		return Math.sqrt(Math.pow(
				punktA.getRechtsWert() - punktB.getRechtsWert(), 2)
				+ Math.pow(punktA.getHochWert() - punktB.getHochWert(), 2));
	}

	/**
	 * Berechnet den Richtungswinkel zwischen zwei Punkten in Gon
	 * 
	 * @param rechtswertA
	 *            Rechtswert des Ausgangspunktes
	 * @param hochwertA
	 *            Hochwert des Ausgangspunktes
	 * @param rechtswertB
	 *            Rechtswert des Endpunktes
	 * @param hochwertB
	 *            Hochwert des Endpunktes
	 * @return Richtungswinkel
	 */
	public static double richtungsWinkel(double rechtswertA, double hochwertA,
			double rechtswertB, double hochwertB) {
		double rW = toGon(Math.atan2(rechtswertB - rechtswertA, hochwertB
				- hochwertA));
		if (rW < 0) {
			rW += 400;
		}
		return rW;
	}

	/**
	 * Berechnet den Richtungswinkel zwischen zwei Punkten in Gon
	 * 
	 * @param punktA
	 *            Ausgangspunktes
	 * @param punktB
	 *            Endpunktes
	 * @return Richtungswinkel
	 */
	public static double richtungsWinkel(Punkt punktA, Punkt punktB) {
		double rW = toGon(Math.atan2(
				punktB.getRechtsWert() - punktA.getRechtsWert(),
				punktB.getHochWert() - punktA.getHochWert()));
		if (rW < 0) {
			rW += 400;
		}
		return rW;
	}

	/**
	 * Gibt den minimalen Wert zurück
	 * 
	 * @param zahl1
	 *            Zahl
	 * @param zahl2
	 *            Zahl
	 * @return kleinere Zahl (-1, wenn beide gleich groß)
	 */
	public static double min(double zahl1, double zahl2) {
		if (zahl1 < zahl2) {
			return zahl1;
		} else if (zahl2 > zahl1) {
			return zahl2;
		} else {
			return -1.;
		}

	}

	/**
	 * Gibt den maximalen Wert zurück
	 * 
	 * @param zahl1
	 *            Zahl
	 * @param zahl2
	 *            Zahl
	 * @return größere Zahl (-1, wenn beide gleich groß)
	 */
	public static double max(double zahl1, double zahl2) {
		if (zahl1 > zahl2) {
			return zahl1;
		} else if (zahl2 < zahl1) {
			return zahl2;
		} else {
			return -1.;
		}
	}

	/**
	 * Rundet eine Zahl "normal" auf eine bestimmte Stellenanzahl
	 * 
	 * @param zahl
	 *            zurundende Zahl
	 * @param stellen
	 *            gewünschte Stellenanzahl
	 * @return Zahl als String
	 */
	public static String normRund(double zahl, int stellen) {
		double exp = Math.pow(10, stellen);
		long zahlN = Math.round(zahl * exp);
		String zahlS = String.valueOf(zahlN);
		for (; zahlS.length() < stellen + 1;) {
			zahlS = "0" + zahlS;
		}
		String zahlR = zahlS.substring(0, zahlS.length() - stellen) + ","
				+ zahlS.substring(zahlS.length() - stellen, zahlS.length());
		return zahlR;
	}

	/**
	 * Rundet eine Zahl geodätisch auf eine bestimmte Stellenanzahl
	 * 
	 * @param zahl
	 *            zurundende Zahl
	 * @param stellen
	 *            gewünschte Stellenanzahl
	 * @return Zahl als String
	 */
	public static String geoRund(double zahl, int stellen) {
		long rundZ = (long) (zahl * Math.pow(10, stellen));
		double diff = zahl * Math.pow(10, stellen) - rundZ;
		if (diff < 0.499) {
			rundZ += 0;
		} else if (diff > 0.501) {
			rundZ += 1;
		} else {
			if ((rundZ % 2) != 0) {
				rundZ += 1;
			}
		}
		String rZ = new Long(rundZ).toString();
		for (int i = rZ.length(); i <= stellen; i++) {
			rZ = "0" + rZ;
		}
		return rZ.substring(0, rZ.length() - stellen) + ","
				+ rZ.substring(rZ.length() - stellen);
	}
}