package de.florian_timm.geo.se2.koordinatenAnzeige;

import javax.imageio.ImageIO;
import javax.swing.JPanel;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.geom.Point2D;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.Proxy;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import javax.swing.JOptionPane;

/**
 * JPanel mit Koordinatendarstellung und Hintergrundkarten
 *
 * @version 2016.09-06-LGV
 * @author Florian Timm
 */

public class Zeichenflaeche extends JPanel {
	private static final long serialVersionUID = 7443676113106531846L;

	double faktor = 14; // m/pixel
	double minfaktor = 0.01;
	double height, width;
	BufferedImage bild = null;
	double rechts = 566626., hoch = 5932845.;
	double rechtsL = -9.999E20, hochL = -9.999E20;
	double heightL = -9.999E20, widthL = -9.999E20, faktorL = -9.999E20;
	boolean useWMS = false;
	DatenVerwaltung datenVerwaltung;

	/**
	 * Konstruktor
	 * 
	 * @param datenVerwaltung
	 *            Zugriff auf die DatenVerwaltung
	 */
	public Zeichenflaeche(DatenVerwaltung datenVerwaltung) {
		super();
		this.datenVerwaltung = datenVerwaltung;
		this.setBackground(new Color(0xddffdd));
	}

	/**
	 * Zeichnet das JPanel inkl. Koordinaten und Karte
	 * 
	 * @param g
	 *            Graphics-Objekt
	 */
	protected void paintComponent(Graphics g) {
		super.paintComponent(g);

		Graphics2D g2d = (Graphics2D) g;
		if (useWMS) {
			if (rechtsL != rechts || hochL != hoch || heightL != height || widthL != width || faktorL != faktor) {
				this.karte(rechts, hoch);
				rechtsL = rechts;
				hochL = hoch;
				heightL = height;
				widthL = width;
				faktorL = faktor;
			}
			g2d.drawImage(bild, 0, 0, this);
		}
		height = this.getSize().getHeight();
		width = this.getSize().getWidth();
		g2d.setColor(new Color(0x000000));
		for (int i = 0; i < datenVerwaltung.getPktAnzahl(); i++) {

			int h = (int) (height
					- ((datenVerwaltung.getPkt(i).getHochWert() - (hoch - 0.5 * height * faktor)) / faktor));
			int r = (int) ((datenVerwaltung.getPkt(i).getRechtsWert() - (rechts - 0.5 * width * faktor)) / faktor);
			g2d.fillRect(r - 2, h - 2, 4, 4);
			g2d.drawString(new Integer(datenVerwaltung.getPkt(i).getPunktNummer()).toString(), (r + 7), (h - 3));
		}

		for (int i = 0; i < datenVerwaltung.getPolyLinienAnzahl(); i++) {

			ArrayList<Point2D.Double> pLpkt = datenVerwaltung.getPolyLinie(i).getPunkte();
			int s = pLpkt.size();
			int[] h = new int[s];
			int[] r = new int[s];
			for (int j = 0; j < pLpkt.size(); j++) {
				r[j] = (int) ((pLpkt.get(j).getX() - (rechts - 0.5 * width * faktor)) / faktor);
				h[j] = (int) (height - ((pLpkt.get(j).getY() - (hoch - 0.5 * height * faktor)) / faktor));
			}

			g2d.drawPolyline(r, h, s);
			// g2d.drawString(new
			// Integer(pv.getPkt(i).getPunktNummer()).toString(),(r+7),(h-3));
		}

		int pixelPerInch = java.awt.Toolkit.getDefaultToolkit().getScreenResolution();

		int[] mbalken = { 25, 10, 25, 50, 100, 250, 500, 1000, 2500, 5000, 10000, 25000, 50000 };
		String[] mbalkenT = { "5m", "10m", "25m", "50m", "100m", "250m", "500m", "1km", "2,5km", "5km", "10km", "25km",
				"50km" };

		int i = 0;
		while (i < mbalken.length - 1 && mbalken[i] / faktor < 100) {
			i++;
		} // end of while

		g2d.drawString(mbalkenT[i], (int) (width - 10 - mbalken[i] / faktor), (int) (height - 15));
		g2d.fillRect((int) (width - 10 - mbalken[i] / faktor), (int) height - 10, (int) (mbalken[i] / faktor), 3);

		// 100m/px px/2,54m
		g2d.drawString("1:" + Math.round(faktor * pixelPerInch / 0.0254), (int) (10), (int) (height - 7));
	}

	/**
	 * Vergrößert die Darstellung der Karte auf 200%
	 */
	public void groesser() {
		groesser(0.5);
	}

	/**
	 * Vergrößert die Darstellung der Karte um einen variablen Wert
	 * 
	 * @param scale
	 *            Faktor, mit dem der Maßstab multipliziert wird
	 */
	public void groesser(double scale) {
		if (faktor >= (minfaktor * (1 / scale))) {
			faktor *= scale;
			this.repaint();
		}
	}

	/**
	 * Vergrößert die Darstellung der Karte um einen variablen Wert unter
	 * Beibehaltung einer Position am Bildschirm
	 * 
	 * @param scale
	 *            Faktor, mit dem der Maßstab multipliziert wird
	 * @param punkt
	 *            Punkt in der Zeichenfläche, der beibehalten werden soll
	 */
	public void groesser(double scale, Point punkt) {
		if (faktor >= (minfaktor * (1 / scale))) {
			Point2D.Double pgk1 = this.koordZF2GK(punkt);
			faktor *= scale;
			Point2D.Double pgk2 = this.koordZF2GK(punkt);
			rechts += pgk1.getX() - pgk2.getX();
			hoch += pgk1.getY() - pgk2.getY();
			this.repaint();
		}
	}

	/**
	 * Setzt den Maßstab auf 20m/px
	 */
	public void gleich() {
		faktor = 20;
		this.repaint();
	}

	/**
	 * Verkleinert die Darstellung der Karte auf 50%
	 */
	public void kleiner() {
		kleiner(2);
	}

	/**
	 * Verkleinert die Darstellung der Karte um einen variablen Wert
	 * 
	 * @param scale
	 *            Faktor, mit dem der Maßstab multipliziert wird
	 */
	public void kleiner(double scale) {
		faktor *= (scale);
		this.repaint();
	}

	/**
	 * Verkleinert die Darstellung der Karte um einen variablen Wert unter
	 * Beibehaltung einer Position am Bildschirm
	 * 
	 * @param scale
	 *            Faktor, mit dem der Maßstab multipliziert wird
	 * @param punkt
	 *            Punkt in der Zeichenfläche, der beibehalten werden soll
	 */
	public void kleiner(double scale, Point punkt) {
		Point2D.Double pgk1 = this.koordZF2GK(punkt);
		faktor *= scale;
		Point2D.Double pgk2 = this.koordZF2GK(punkt);
		rechts += pgk1.getX() - pgk2.getX();
		hoch += pgk1.getY() - pgk2.getY();
		this.repaint();
	}

	/**
	 * Verschiebt die Karte um 1/3 nach Norden
	 */
	public void nord() {
		hoch += (int) this.getSize().getHeight() / 3 * faktor;
		this.repaint();
	}

	/**
	 * Verschiebt die Karte um 1/3 nach Osten
	 */
	public void ost() {
		rechts += (int) this.getSize().getWidth() / 3 * faktor;
		this.repaint();
	}

	/**
	 * Verschiebt die Karte um 1/3 nach Westen
	 */
	public void west() {
		rechts -= (int) this.getSize().getWidth() / 3 * faktor;
		this.repaint();
	}

	/**
	 * Verschiebt die Karte um 1/3 nach Süden
	 */
	public void sued() {
		hoch -= (int) this.getSize().getHeight() / 3 * faktor;
		this.repaint();
	}

	/**
	 * Erzeugt oder aktualisiert die Hintergrundkarte
	 * 
	 * @param rechts
	 *            Rechtswert, des Mittelpunktes
	 * @param hoch
	 *            Hochwert, des Mittelpunktes
	 */
	public void karte(double hoch, double rechts) {
		height = this.getSize().getHeight();
		width = this.getSize().getWidth(); // EPSG 31467 (GK)
		/*
		 * String url =
		 * "http://odaten.metropolregion.hamburg.de/wms_webatlasde?LAYERS=webatlasde&FORMAT=image%2Fjpeg&RESTRICTEDEXTENT=3327812%2C5767980%2C3818526%2C6094052&SERVICE=WMS&VERSION=1.1.1&REQUEST=GetMap&STYLES=&SRS=EPSG%3A25832&BBOX="
		 * + ((int) (hoch - 0.5 * width * faktor)) + "," + ((int) (rechts - 0.5
		 * * height * faktor)) + "," + ((int) (hoch + 0.5 * width * faktor)) +
		 * "," + ((int) (rechts + 0.5 * height * faktor)) + "&WIDTH=" + ((int)
		 * width) + "&HEIGHT=" + ((int) height) + "";
		 */
		String url = "http://sg.geodatenzentrum.de/wms_webatlasde__218ae197-6f9f-d092-27f0-b64c2cfa13d6?SERVICE=WMS&VERSION=1.3.0&REQUEST=GetMap&CRS=EPSG:25832&LAYERS=webatlasde&STYLES=&FORMAT=image/jpeg&BBOX="
				+ ((int) (hoch - 0.5 * width * faktor)) + "," + ((int) (rechts - 0.5 * height * faktor)) + ","
				+ ((int) (hoch + 0.5 * width * faktor)) + "," + ((int) (rechts + 0.5 * height * faktor)) + "&WIDTH="
				+ ((int) width) + "&HEIGHT=" + ((int) height) + "";
		//System.out.println(url);
		// String url =
		// "http://onmaps.de/wms?version=1.1.0&request=getmap&format=image/png&transparent=true&service=wms&kid=058d4f9fe48b8920d409f2ebb88e0247&WIDTH="+width+"&HEIGHT="+height+"&BBOX="+((int)(hoch-0.5*width*faktor))+","+((int)(rechts-0.5*height*faktor))+","+((int)(hoch+0.5*width*faktor))+","+((int)(rechts+0.5*height*faktor))+"&LAYERS=onmaps_kraeftig,onmaps_strassentexte";
		try {
			//System.setProperty("java.net.useSystemProxies", "true");
			URL urlU = new URL(url);
			
			Proxy proxy = new Proxy(Proxy.Type.HTTP, new InetSocketAddress("wall.lit.hamburg.de", 80));
			URLConnection con = urlU.openConnection(proxy);
			bild = ImageIO.read(con.getInputStream());
			//bild = ImageIO.read(urlU);
		} catch (IOException ioe) {
			JOptionPane.showMessageDialog(null, "WMS wurde nicht gefunden, die Anzeige wurde deaktiviert",
					"WMS-Anzeige", JOptionPane.ERROR_MESSAGE);
		}
		// this.repaint();
	}

	/**
	 * Gibt Koordinaten der Bounding-Box als Text zurück
	 * 
	 * @return Bounding-Box als Text
	 */
	public String getBBOX() {
		return new String(((int) (hoch - 0.5 * width * faktor)) + "," + ((int) (rechts - 0.5 * height * faktor)) + ","
				+ ((int) (hoch + 0.5 * width * faktor)) + "," + ((int) (rechts + 0.5 * height * faktor)));
	}

	/**
	 * Passt die Darstellung an die eingelesenen Punkte an
	 */
	public void fit() {
		double maxH = 0, maxR = 0, minH = 0, minR = 0;

		int allP = 0;

		for (int i = 0; i < datenVerwaltung.getPktAnzahl(); i++) {
			allP++;
			if (i == 0) {
				maxH = datenVerwaltung.getPkt(i).getHochWert();
				minH = datenVerwaltung.getPkt(i).getHochWert();
				maxR = datenVerwaltung.getPkt(i).getRechtsWert();
				minR = datenVerwaltung.getPkt(i).getRechtsWert();
			} else {
				if (maxH < datenVerwaltung.getPkt(i).getHochWert()) {
					maxH = datenVerwaltung.getPkt(i).getHochWert();
				} else if (minH > datenVerwaltung.getPkt(i).getHochWert()) {
					minH = datenVerwaltung.getPkt(i).getHochWert();
				}
				if (maxR < datenVerwaltung.getPkt(i).getRechtsWert()) {
					maxR = datenVerwaltung.getPkt(i).getRechtsWert();
				} else if (minR > datenVerwaltung.getPkt(i).getRechtsWert()) {
					minR = datenVerwaltung.getPkt(i).getRechtsWert();
				}
			}
		}
		// System.out.println( " "+pv.polyLinie.size());
		for (int i = 0; i < datenVerwaltung.getPolyLinienAnzahl(); i++) {
			ArrayList<Point2D.Double> pkt = datenVerwaltung.getPolyLinie(i).getPunkte();
			for (int j = 0; j < pkt.size(); j++) {
				allP++;

				if (maxH < pkt.get(j).getY() || maxH == 0) {
					maxH = pkt.get(j).getY();
				} else if (minH > pkt.get(j).getY() || minH == 0) {
					minH = pkt.get(j).getY();
				}
				if (maxR < pkt.get(j).getX() || maxR == 0) {
					maxR = pkt.get(j).getX();
				} else if (minR > pkt.get(j).getX() || minR == 0) {
					minR = pkt.get(j).getX();
				}
			}
		}

		if (allP > 0) {
			height = this.getSize().getHeight();
			width = this.getSize().getWidth();

			rechts = (int) (minR + (maxR - minR) / 2);
			hoch = (int) (minH + (maxH - minH) / 2);

			double faktorR = (maxR - minR) / (width * 0.8);
			double faktorH = (maxH - minH) / (height * 0.8);

			if (faktorR > faktorH) {
				faktor = faktorR;
			} else {
				faktor = faktorH;
			}

			this.repaint();
		}
	}

	/**
	 * Schaltet die Nutzung des WMS an oder aus
	 * 
	 * @param useWMS
	 *            gewünschter Zustand
	 */
	public void useWMS(boolean useWMS) {
		this.useWMS = useWMS;
	}

	/**
	 * Gibt Koordinaten im übergeordneten System einer Position in der
	 * Zeichenfläche zurück
	 * 
	 * @param x
	 *            x-Wert in der Zeichenfläche
	 * @param y
	 *            y-Wert in der Zeichenfläche
	 * @return Koordinaten in übergeordneten System
	 */
	public Point2D.Double koordZF2GK(int x, int y) {
		return koordZF2GK(new Point(x, y));
	}

	/**
	 * Gibt Koordinaten im übergeordneten System einer Position in der
	 * Zeichenfläche zurück
	 * 
	 * @param p
	 *            Punkt mit Position in der Zeichenfläche
	 * @return Koordinaten in übergeordneten System
	 */
	public Point2D.Double koordZF2GK(Point p) {
		double height = this.getSize().getHeight();
		double width = this.getSize().getWidth();

		double h = (height - p.getY() - 0.5 * height) * faktor + hoch;
		double r = p.getX() * faktor + rechts - 0.5 * width * faktor;
		return new Point2D.Double(r, h);
	}

	/**
	 * Gibt Position in der Zeichenfläche von Koordinaten im übergeordneten
	 * System zurück
	 * 
	 * @param x
	 *            Rechtswert im übergeordneten System
	 * @param y
	 *            Hochwert im übergeordneten System
	 * @return Position in der Zeichenfläche
	 */
	public Point koordGK2ZF(double x, double y) {
		return koordGK2ZF(new Point2D.Double(x, y));
	}

	/**
	 * Gibt Position in der Zeichenfläche von Koordinaten im übergeordneten
	 * System zurück
	 * 
	 * @param punkt
	 *            Koordinaten im übergeordneten System
	 * @return Position in der Zeichenfläche
	 */
	public Point koordGK2ZF(Point2D.Double punkt) {
		double height = this.getSize().getHeight();
		double width = this.getSize().getWidth();
		int h = (int) (height - ((punkt.getY() / faktor - (hoch - 0.5 * height))));
		int r = (int) (punkt.getX() / faktor - (rechts - 0.5 * width));
		return new Point(r, h);
	}

	/**
	 * Gibt Koordinaten des Mittelpunktes im übergeordneten System zurück
	 * 
	 * @return Koordinaten des Mittelpunktes im übergeordneten System
	 */
	public Point2D.Double getCenter() {
		return new Point2D.Double(rechts, hoch);

	}

	/**
	 * Verschiebt die Karte
	 * 
	 * @param startCenter
	 *            Mittelpunkt der Karte vor dem Verschieben
	 * @param startKlick
	 *            Position des gedrückthalten der Maustaste in der Zeichenfläche
	 * @param endKlick
	 *            Position der Mauspfeiles in der Zeichenfläche
	 */
	public void schiebeKarte(Point2D.Double startCenter, Point startKlick, Point endKlick) {
		rechts = startCenter.getX() - (endKlick.getX() - startKlick.getX()) * faktor;
		hoch = startCenter.getY() + (endKlick.getY() - startKlick.getY()) * faktor;
		this.repaint();
	}

	/**
	 * Gibt Punkte in der Nähe einer Position in der Zeichenfläche zurück
	 * 
	 * @param pktZF
	 *            Position in der Zeichenfläche
	 * @return ArrayList der Punkte in der Nähe
	 */
	public ArrayList<Punkt> getPunkteByKlick(Point pktZF) {
		Point2D.Double pktGK = this.koordZF2GK(pktZF);
		return datenVerwaltung.getPointByCoord(pktGK, faktor * 5);
	}
}