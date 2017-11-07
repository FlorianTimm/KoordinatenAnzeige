package de.florian_timm.geo.se2.koordinatenAnzeige;

import java.awt.Point;
import java.awt.geom.Point2D;

/**
 * Punkt Klasse für geodätische Punkte
 * 
 * @author Florian Timm
 * @version 2015.06.14
 */
public class Punkt implements Comparable<Object> {
	private int punktNummer;
	private double rechtsWert, hochWert, hoehe;
	private static double rechtsWertLinksUnten, hochWertLinksUnten,
			rechtsWertRechtsOben, hochWertRechtsOben;

	/**
	 * Konstruktor
	 */
	public Punkt() {
		punktNummer = -1;
		rechtsWert = -9.9999e10;
		hochWert = -9.9999e10;
		hoehe = -9.9999e10;
	}

	/**
	 * Konstruktor
	 * 
	 * @param punktNummer
	 *            Punktnummer
	 * @param rechtsWert
	 *            Rechtswert
	 * @param hochWert
	 *            Hochwert
	 * @param hoehe
	 *            Höhe
	 */
	public Punkt(int punktNummer, double rechtsWert, double hochWert,
			double hoehe) {
		this.punktNummer = punktNummer;
		this.rechtsWert = rechtsWert;
		this.hochWert = hochWert;
		this.hoehe = hoehe;
	}

	/**
	 * Konstruktor
	 * 
	 * @param punktNummer
	 *            Punktnummer
	 * @param rechtsWert
	 *            Rechtswert
	 * @param hochWert
	 *            Hochwert
	 */
	public Punkt(int punktNummer, double rechtsWert, double hochWert) {
		this.punktNummer = punktNummer;
		this.rechtsWert = rechtsWert;
		this.hochWert = hochWert;
		this.hoehe = -9.9999e10;
	}

	/**
	 * Konstruktor
	 * 
	 * @param pkt
	 *            Punkt mit Rechts- und Hochwert
	 */
	public Punkt(Point2D.Double pkt) {
		punktNummer = -1;
		rechtsWert = pkt.getX();
		hochWert = pkt.getY();
		hoehe = -9.9999e10;
	}

	/**
	 * Konstruktor
	 * 
	 * @param pkt
	 *            Punkt mit Rechts- und Hochwert
	 */
	public Punkt(Point pkt) {
		punktNummer = -1;
		rechtsWert = pkt.getX();
		hochWert = pkt.getY();
		hoehe = -9.9999e10;
	}

	/**
	 * Gibt die Daten des Punktes als String zurück
	 * 
	 * @return Zeichenkette
	 */
	public String toString() {
		return String.format("PktNr: %4d R: %15.3f H: %15.3f Hoe: %15.4f",
				punktNummer, rechtsWert, hochWert, hoehe);
	}

	/**
	 * Gibt die Daten des Punktes als String ohne Höhe zurück
	 * 
	 * @return Zeichenkette
	 */
	public String toStringoH() {
		return String.format("PktNr: %4d R: %15.3f H: %15.3f", punktNummer,
				rechtsWert, hochWert);
	}

	/**
	 * Gibt die Daten des Punktes als String ohne Höhe mit Tab zurück
	 * 
	 * @return Zeichenkette
	 */
	public String toStringoHmT() {
		return String.format("%4d\tR%10.3f\tH%10.3f", punktNummer, rechtsWert,
				hochWert);
	}

	/**
	 * Setzt die Punktnummer
	 * 
	 * @param punktNummer
	 *            Neue Punktnummer
	 */
	public void setPunktNummer(int punktNummer) {
		this.punktNummer = punktNummer;
	}

	/**
	 * Setzt den Rechtswert
	 * 
	 * @param rechtsWert
	 *            Neuer Rechtswert
	 */
	public void setRechtsWert(double rechtsWert) {
		this.rechtsWert = rechtsWert;
	}

	/**
	 * Setzt den Hochwert
	 * 
	 * @param hochWert
	 *            Neuer Hochwert
	 */
	public void setHochWert(double hochWert) {
		this.hochWert = hochWert;
	}

	/**
	 * Setzt die Höhe
	 * 
	 * @param hoehe
	 *            Neue Höhe
	 */
	public void setHoehe(double hoehe) {
		this.hoehe = hoehe;
	}

	/**
	 * Gibt die Punktnummer zurück
	 * 
	 * @return Punktnummer
	 */
	public int getPunktNummer() {
		return punktNummer;
	}

	/**
	 * Gibt den Rechtswert zurück
	 * 
	 * @return Rechtswert
	 */
	public double getRechtsWert() {
		return rechtsWert;
	}

	/**
	 * Gibt den Hochwert zurück
	 * 
	 * @return Hochwert
	 */
	public double getHochWert() {
		return hochWert;
	}

	/**
	 * Gibt die Höhe zurück
	 * 
	 * @return Höhe
	 */
	public double getHoehe() {
		return hoehe;
	}

	/**
	 * Setzt die Kartenecke Links-Unten
	 * 
	 * @param rechtsWertLinksUnten
	 *            Rechtswert der Ecke Links-Unten
	 * @param hochWertLinksUnten
	 *            Hochwert der Ecke Links-Unten
	 */
	public static void setReHoLinksUnten(double rechtsWertLinksUnten,
			double hochWertLinksUnten) {
		Punkt.rechtsWertLinksUnten = rechtsWertLinksUnten;
		Punkt.hochWertLinksUnten = hochWertLinksUnten;
	}

	/**
	 * Setzt die Kartenecke Rechts-Oben
	 * 
	 * @param rechtsWertRechtsOben
	 *            Rechtswert der Ecke Rechts-Oben
	 * @param hochWertRechtsOben
	 *            Hochwert der Ecke Rechts-Oben
	 */
	public static void setReHoRechtsOben(double rechtsWertRechtsOben,
			double hochWertRechtsOben) {
		Punkt.rechtsWertRechtsOben = rechtsWertRechtsOben;
		Punkt.hochWertRechtsOben = hochWertRechtsOben;
	}

	/**
	 * Gibt den Rechtswert der Ecke Links-Unten zurück
	 * 
	 * @return Rechtswert der Ecke Links-Unten
	 */
	public static double getRechtsWertLinksUnten() {
		return rechtsWertLinksUnten;
	}

	/**
	 * Gibt den Hochwert der Ecke Links-Unten zurück
	 * 
	 * @return Hochwert der Ecke Links-Unten
	 */
	public static double getHochWertLinksUnten() {
		return hochWertLinksUnten;
	}

	/**
	 * Gibt den Rechtswert der Ecke Rechts-Oben zurück
	 * 
	 * @return Rechtswert der Ecke Rechts-Oben
	 */
	public static double getRechtsWertRechtsOben() {
		return rechtsWertRechtsOben;
	}

	/**
	 * Gibt den Hochwert der Ecke Rechts-Oben zurück
	 * 
	 * @return Hochwert der Ecke Rechts-Oben
	 */
	public static double getHochWertRechtsOben() {
		return hochWertRechtsOben;
	}

	/**
	 * Gibt die Höhendifferenz zwischen diesem und einem anderen Punkt zurück
	 * 
	 * @param punkt
	 *            Punkt
	 * @return Höhendifferenz
	 */
	public double deltaH(Punkt punkt) {
		return (this.hoehe - punkt.hoehe);
	}

	/**
	 * Gibt die Höhendifferenz zwischen zwei Punkten zurück
	 * 
	 * @param punkt1
	 *            Punkt
	 * @param punkt2
	 *            Punkt
	 * @return Höhendifferenz
	 */
	public static double deltaH(Punkt punkt1, Punkt punkt2) {
		return (punkt1.hoehe - punkt2.hoehe);
	}

	/**
	 * Berechnet die Strecke zwischen zwei Punkten
	 * 
	 * @param punkt
	 *            Endpunktes
	 * @return Strecke
	 */
	public double strecke(Punkt punkt) {
		return GMath.strecke(this, punkt);
	}

	/**
	 * Berechnet die Strecke zwischen zwei Punkten
	 * 
	 * @param punkt
	 *            Endpunktes
	 * @return Richtungswinkel in Gon
	 */
	public double richtungsWinkel(Punkt punkt) {
		return GMath.richtungsWinkel(this, punkt);
	}

	/**
	 * Ermittelt, ob Punkt in der Karte liegt
	 * 
	 * @param rechtsOben
	 *            Punkt der Ecke Rechts-Oben
	 * @param linksUnten
	 *            Punkt der Ecke Links-Unten
	 * @return true, wenn in Karte
	 */
	public boolean isInside(Punkt rechtsOben, Punkt linksUnten) {
		boolean inRechtswert = this.rechtsWert >= rechtsOben.rechtsWert
				&& this.rechtsWert <= linksUnten.rechtsWert;
		boolean inHochwert = this.hochWert >= linksUnten.hochWert
				&& this.hochWert <= rechtsOben.hochWert;
		return inRechtswert && inHochwert;
	}

	public int compareTo(Object o) {
		Punkt punkt = (Punkt) o;
		if (this.getPunktNummer() < punkt.getPunktNummer()) {
			return -1;
		} else if (this.getPunktNummer() > punkt.getPunktNummer()) {
			return 1;
		}
		return 0;
	}

	public Point2D.Double getPoint() {
		return new Point2D.Double(getRechtsWert(), getHochWert());
	}
}