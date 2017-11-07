package de.florian_timm.geo.se2.koordinatenAnzeige;

import java.awt.geom.Point2D;

/**
 * Koordinatensysteme umrechnen
 * 
 * @author Florian Timm
 * @version 2015.06.15
 *
 */
public class KoordTransformer {

	/**
	 * Koordinatensysteme
	 */
	public static final String[] SYSTEME = { "Lokal (o.Transf.)","DHDN GK", "DHDN UTM32",
			"DHDN LonLat", "WGS84 GK", "WGS84 UTM32", "WGS84 LonLat" };
	public static final int LOKAL = 0;
	public static final int DHDN_GK = 1;
	public static final int DHDN_UTM32 = 2;
	public static final int DHDN_LonLat = 3;
	public static final int WGS84_GK = 4;
	public static final int WGS84_UTM32 = 5;
	public static final int WGS84_LonLat = 6;

	private static final int DHDN = 101;
	private static final int WGS84 = 102;

	private static final int GK = 201;
	private static final int UTM32 = 202;
	private static final int LonLat = 203;

	public static Point2D.Double wandleKoordinaten(int von, int nach,
			Point2D.Double punkt) {
		int vEllip = -1, vKoord = -1, nEllip = -1, nKoord = -1;
		
		if (nach != LOKAL && von != LOKAL) {
			switch (von) {
			case DHDN_GK:
				vEllip = DHDN;
				vKoord = GK;
				break;
			case DHDN_UTM32:
				vEllip = DHDN;
				vKoord = UTM32;
				break;
			case DHDN_LonLat:
				vEllip = DHDN;
				vKoord = LonLat;
				break;
			case WGS84_GK:
				vEllip = WGS84;
				vKoord = GK;
				break;
			case WGS84_UTM32:
				vEllip = WGS84;
				vKoord = UTM32;
				break;
			case WGS84_LonLat:
				vEllip = WGS84;
				vKoord = LonLat;
				break;
			}

			switch (nach) {
			case DHDN_GK:
				nEllip = DHDN;
				nKoord = GK;
				break;
			case DHDN_UTM32:
				nEllip = DHDN;
				nKoord = UTM32;
				break;
			case DHDN_LonLat:
				nEllip = DHDN;
				nKoord = LonLat;
				break;
			case WGS84_GK:
				nEllip = WGS84;
				nKoord = GK;
				break;
			case WGS84_UTM32:
				nEllip = WGS84;
				nKoord = UTM32;
				break;
			case WGS84_LonLat:
				nEllip = WGS84;
				nKoord = LonLat;
				break;
			}
			if (nEllip != vEllip) {
				if (vKoord == UTM32) {
					punkt = utm2geo("32U", punkt);
					//System.out.println("utm2geo");
					vKoord = LonLat;

				} else if (vKoord == GK) {
					punkt = gk2geo(punkt);
					//System.out.println("gk2geo");
					vKoord = LonLat;
				}
				if (vEllip == DHDN && nEllip == WGS84) {
					punkt = pot2wgs(punkt);
					//System.out.println("pot2wgs");
					vEllip = WGS84;
				} else if (vEllip == WGS84 && nEllip == DHDN) {
					punkt = wgs2pot(punkt);
					//System.out.println("wgs2pot");
					vEllip = DHDN;
				}
			}

			if (nKoord != vKoord) {
				if (nKoord == GK) {
					if (vKoord == UTM32) {
						punkt = utm2geo("32N", punkt);
						//System.out.println("utm2geo");
						punkt = lonlat2gk(punkt);
						//System.out.println("lonlat2gk");
					} else if (vKoord == LonLat) {
						punkt = lonlat2gk(punkt);
						//System.out.println("lonlat2gk");
					}
				} else if (nKoord == UTM32) {
					if (vKoord == GK) {
						punkt = gk2geo(punkt);
						//System.out.println("gk2geo");
						punkt = lonlat2utm(punkt);
						//System.out.println("lonlat2utm");
					} else if (vKoord == LonLat) {
						punkt = lonlat2utm(punkt);
						//System.out.println("lonlat2utm");
					}
				} else if (nKoord == LonLat) {
					if (vKoord == GK) {
						punkt = gk2geo(punkt);
						//System.out.println("gk2geo");
					} else if (vKoord == UTM32) {
						punkt = utm2geo("32N", punkt);
						//System.out.println("utm2geo");
					}
				}
			}
		}
		return punkt;
	}

	/**
	 * Wandelt Längen- und Breitengrad in GK um
	 * 
	 * @param punkt
	 *            Point mit X = Längengrad, Y = Breitengrad im PotsdamDatum
	 * @return Punkt mit X = Rechts, Y = Hoch
	 */
	public static Point2D.Double lonlat2gk(Point2D.Double punkt) {
		/*
		 * Copyright (c) 2006, HELMUT H. HEIMEIER Permission is hereby granted,
		 * free of charge, to any person obtaining a copy of this software and
		 * associated documentation files (the "Software"), to deal in the
		 * Software without restriction, including without limitation the rights
		 * to use, copy, modify, merge, publish, distribute, sublicense, and/or
		 * sell copies of the Software, and to permit persons to whom the
		 * Software is furnished to do so, subject to the following conditions:
		 * The above copyright notice and this permission notice shall be
		 * included in all copies or substantial portions of the Software.
		 */

		/*
		 * Die Funktion wandelt geographische Koordinaten in GK Koordinaten um.
		 * Geographische Länge lp und Breite bp müssen im Potsdam Datum gegeben
		 * sein. Berechnet werden Rechtswert rw und Hochwert hw.
		 */

		// Geographische Länge lp und Breite bp im Potsdam Datum
		double lp = punkt.getX();
		double bp = punkt.getY();

		// Grenzen des Gauss-Krüger-Systems für Deutschland 46° N < bp < 55° N,
		// 5° E < lp < 16° E
		if (bp < 46 || bp > 56 || lp < 5 || lp > 16) {
			// Werte außerhalb des für Deutschland definierten
			// Gauss-Krüger-Systems 5° E < LP < 16° E, 46° N < BP < 55° N
			return null;
		}

		// Potsdam Datum
		// Große Halbachse a und Abplattung f
		double a = 6377397.155;
		double f = 3.34277321e-3;
		double pi = Math.PI;

		// Polkrümmungshalbmesser c
		double c = a / (1 - f);

		// Quadrat der zweiten numerischen Exzentrizität
		double ex2 = (2 * f - f * f) / ((1 - f) * (1 - f));
		double ex4 = ex2 * ex2;
		double ex6 = ex4 * ex2;
		double ex8 = ex4 * ex4;

		// Koeffizienten zur Berechnung der Meridianbogenlänge
		double e0 = c
				* (pi / 180)
				* (1 - 3 * ex2 / 4 + 45 * ex4 / 64 - 175 * ex6 / 256 + 11025 * ex8 / 16384);
		double e2 = c
				* (-3 * ex2 / 8 + 15 * ex4 / 32 - 525 * ex6 / 1024 + 2205 * ex8 / 4096);
		double e4 = c
				* (15 * ex4 / 256 - 105 * ex6 / 1024 + 2205 * ex8 / 16384);
		double e6 = c * (-35 * ex6 / 3072 + 315 * ex8 / 12288);

		// Breite in Radianten
		double br = bp * pi / 180;

		double tan1 = Math.tan(br);
		double tan2 = tan1 * tan1;
		double tan4 = tan2 * tan2;

		double cos1 = Math.cos(br);
		double cos2 = cos1 * cos1;
		double cos4 = cos2 * cos2;
		double cos3 = cos2 * cos1;
		double cos5 = cos4 * cos1;

		double etasq = ex2 * cos2;

		// Querkrümmungshalbmesser nd
		double nd = c / Math.sqrt(1 + etasq);

		// Meridianbogenlänge g aus gegebener geographischer Breite bp
		double g = e0 * bp + e2 * Math.sin(2 * br) + e4 * Math.sin(4 * br) + e6
				* Math.sin(6 * br);

		// Längendifferenz dl zum Bezugsmeridian lh
		int kz = (int) ((lp + 1.5) / 3);
		int lh = kz * 3;
		double dl = (lp - lh) * pi / 180;
		double dl2 = dl * dl;
		double dl4 = dl2 * dl2;
		double dl3 = dl2 * dl;
		double dl5 = dl4 * dl;

		// Hochwert hw und Rechtswert rw als Funktion von geographischer Breite
		// und Länge
		double hw = (g + nd * cos2 * tan1 * dl2 / 2 + nd * cos4 * tan1
				* (5 - tan2 + 9 * etasq) * dl4 / 24);
		double rw = (nd * cos1 * dl + nd * cos3 * (1 - tan2 + etasq) * dl3 / 6
				+ nd * cos5 * (5 - 18 * tan2 + tan4) * dl5 / 120 + kz * 1e6 + 500000);

		double nk = hw - (int) (hw);
		if (nk < 0.5)
			hw = (int) (hw);
		else
			hw = (int) (hw) + 1;

		nk = rw - (int) (rw);
		if (nk < 0.5)
			rw = (int) (rw);
		else
			rw = (int) (rw + 1);

		return new Point2D.Double(rw, hw);
	}
	
//	/**
//	 * Wandelt Längen- und Breitengrad in GK um
//	 * 
//	 * @param punkt
//	 *            Point mit X = Längengrad, Y = Breitengrad im PotsdamDatum
//	 * @return Punkt mit X = Rechts, Y = Hoch
//	 */
//	public static Point2D.Double lonlat2gk(Point2D.Double punkt) {
//		Double L = punkt.getX();
//		Double B = punkt.getY();
//		int Lo = 9; //Mittelmeridian
//		
//		////////// Bessel ////////////
//		Double e0 = 111120.619607;	//
//		Double e2 = -15988.6383;	//
//		Double e4 = 16.7300;		//
//		Double e6 = -0.0218;		//
//									//
//		Double a = 6377397.155;		//
//		Double b = 6356078.963;		//
//		//////////////////////////////
//		
//		Double phi = 180/Math.PI;
//		Double t = Math.tan(B);
//		Double l = (L-Lo)/phi*Math.cos(B);
//		
//		Double es2 = (a*a-b*b)/(b*b);
//		Double c = a*a/b;
//		Double v = Math.sqrt(1 + es2 * Math.pow(Math.cos(B),2));
//		Double y = l*c/v*(1+l*l*(v*v-t*t+l*(0.3-t*t))/6);
//		Double rechts = (Lo/3+0.5)*1000000+y;
//		
//		Double xb = e0*B+e2*Math.sin(2*B)+e4*Math.sin(4*B)+e6*Math.sin(6*B);
//		Double hoch = xb + l*l*c*t/v*(0.5+l*l*(5.03-t*t)/24);
//		
//		return new Point2D.Double(rechts,hoch);
//	}
	
	

	/**
	 * Wandelt Längen- und Breitengrad in UTM um
	 * 
	 * @param punkt
	 *            Point mit X = Längengrad, Y = Breitengrad
	 * @return Punkt mit X = East, Y = North
	 */
	public static Point2D.Double lonlat2utm(Point2D.Double punkt) {
		double lw = punkt.getX();
		double bw = punkt.getY();
		/*
		 * Copyright (c) 2006, HELMUT H. HEIMEIER Permission is hereby granted,
		 * free of charge, to any person obtaining a copy of this software and
		 * associated documentation files (the "Software"), to deal in the
		 * Software without restriction, including without limitation the rights
		 * to use, copy, modify, merge, publish, distribute, sublicense, and/or
		 * sell copies of the Software, and to permit persons to whom the
		 * Software is furnished to do so, subject to the following conditions:
		 * The above copyright notice and this permission notice shall be
		 * included in all copies or substantial portions of the Software.
		 */

		/*
		 * Die Funktion wandelt geographische Koordinaten in UTM Koordinaten um.
		 * Geographische Länge lw und Breite bw müssen im WGS84 Datum gegeben
		 * sein. Berechnet werden UTM Zone, Ostwert ew und Nordwert nw.
		 */

		// Geographische Länge lw und Breite bw im WGS84 Datum

		if (lw <= -180 || lw > 180 || bw <= -80 || bw >= 84) {
			// Werte nicht im Bereich des UTM Systems
			// -180° <= LW < +180°, -80° < BW < 84° N
			return null;
		}

		// WGS84 Datum
		// Große Halbachse a und Abplattung f
		double a = 6378137.000;
		double f = 3.35281068e-3;
		double pi = Math.PI;
		// String b_sel = "CDEFGHJKLMNPQRSTUVWXX";

		// Polkrümmungshalbmesser c
		double c = a / (1 - f);

		// Quadrat der zweiten numerischen Exzentrizität
		double ex2 = (2 * f - f * f) / ((1 - f) * (1 - f));
		double ex4 = ex2 * ex2;
		double ex6 = ex4 * ex2;
		double ex8 = ex4 * ex4;

		// Koeffizienten zur Berechnung der Meridianbogenlänge
		double e0 = c
				* (pi / 180)
				* (1 - 3 * ex2 / 4 + 45 * ex4 / 64 - 175 * ex6 / 256 + 11025 * ex8 / 16384);
		double e2 = c
				* (-3 * ex2 / 8 + 15 * ex4 / 32 - 525 * ex6 / 1024 + 2205 * ex8 / 4096);
		double e4 = c
				* (15 * ex4 / 256 - 105 * ex6 / 1024 + 2205 * ex8 / 16384);
		double e6 = c * (-35 * ex6 / 3072 + 315 * ex8 / 12288);

		// Längenzone lz und Breitenzone (Band) bz
		int lzn = (int) ((lw + 180) / 6) + 1;
		// String lz = String.valueOf(lzn);
		// if (lzn < 10)
		// lz = "0" + lzn;
		// int bd = (int) ((bw + 80) / 8);
		// char bz = b_sel.charAt(bd);

		// Geographische Breite in Radianten br
		double br = bw * pi / 180;

		double tan1 = Math.tan(br);
		double tan2 = tan1 * tan1;
		double tan4 = tan2 * tan2;

		double cos1 = Math.cos(br);
		double cos2 = cos1 * cos1;
		double cos4 = cos2 * cos2;
		double cos3 = cos2 * cos1;
		double cos5 = cos4 * cos1;

		double etasq = ex2 * cos2;

		// Querkrümmungshalbmesser nd
		double nd = c / Math.sqrt(1 + etasq);

		// Meridianbogenlänge g aus gegebener geographischer Breite bw
		double g = (e0 * bw) + (e2 * Math.sin(2 * br))
				+ (e4 * Math.sin(4 * br)) + (e6 * Math.sin(6 * br));

		// Längendifferenz dl zum Bezugsmeridian lh
		double lh = (lzn - 30) * 6 - 3;
		double dl = (lw - lh) * pi / 180;
		double dl2 = dl * dl;
		double dl4 = dl2 * dl2;
		double dl3 = dl2 * dl;
		double dl5 = dl4 * dl;

		// Maßstabsfaktor auf dem Bezugsmeridian bei UTM Koordinaten m = 0.9996
		// Nordwert nw und Ostwert ew als Funktion von geographischer Breite und
		// Länge
		double nw;
		if (bw < 0) {
			nw = 10e6 + 0.9996 * (g + nd * cos2 * tan1 * dl2 / 2 + nd * cos4
					* tan1 * (5 - tan2 + 9 * etasq) * dl4 / 24);
		} else {
			nw = 0.9996 * (g + nd * cos2 * tan1 * dl2 / 2 + nd * cos4 * tan1
					* (5 - tan2 + 9 * etasq) * dl4 / 24);
		}
		double ew = 0.9996 * (nd * cos1 * dl + nd * cos3 * (1 - tan2 + etasq)
				* dl3 / 6 + nd * cos5 * (5 - 18 * tan2 + tan4) * dl5 / 120) + 500000;

		// String zone = lz + bz;

		double nk = nw - (int) (nw);
		if (nk < 0.5)
			nw = (int) (nw);
		else
			nw = (int) (nw) + 1;

		/*
		 * while (nw.length < 7) { nw = "0" + nw; }
		 */

		nk = ew - (int) (ew);
		if (nk < 0.5)
			ew = (int) (ew);
		else
			ew = (int) (ew + 1);

		return new Point2D.Double(ew, nw);
	}

	public static Point2D.Double wgs2pot(Point2D.Double punkt) {
		/*
		 * Copyright (c) 2006, HELMUT H. HEIMEIER Permission is hereby granted,
		 * free of charge, to any person obtaining a copy of this software and
		 * associated documentation files (the "Software"), to deal in the
		 * Software without restriction, including without limitation the rights
		 * to use, copy, modify, merge, publish, distribute, sublicense, and/or
		 * sell copies of the Software, and to permit persons to whom the
		 * Software is furnished to do so, subject to the following conditions:
		 * The above copyright notice and this permission notice shall be
		 * included in all copies or substantial portions of the Software.
		 */

		/*
		 * Die Funktion verschiebt das Kartenbezugssystem (map datum) vom WGS84
		 * Datum (World Geodetic System 84) zum in Deutschland gebräuchlichen
		 * Potsdam-Datum. Geographische Länge lw und Breite bw gemessen in grad
		 * auf dem WGS84 Ellipsoid müssen gegeben sein. Ausgegeben werden
		 * geographische Länge lp und Breite bp (in grad) auf dem
		 * Bessel-Ellipsoid. Bei der Transformation werden die Ellipsoidachsen
		 * parallel verschoben um dx = -587 m, dy = -16 m und dz = -393 m.
		 */
		// 

		// Geographische Länge lw und Breite bw im WGS84 Datum
		double lw = punkt.getX();
		double bw = punkt.getY();

		// Quellsystem WGS84 Datum
		// Große Halbachse a und Abplattung fq
		double a = 6378137.000;
		double fq = 3.35281066e-3;

		// Zielsystem Potsdam Datum
		// Abplattung f
		double f = fq - 1.003748e-5;

		// Parameter für datum shift
		int dx = -587;
		int dy = -16;
		int dz = -393;
		/*int dx = -631;
		int dy = -23;
		int dz = -451;*/

		// Quadrat der ersten numerischen Exzentrizität in Quell- und Zielsystem
		double e2q = (2 * fq - fq * fq);
		double e2 = (2 * f - f * f);

		// Breite und Länge in Radianten
		double pi = Math.PI;
		double b1 = bw * (pi / 180);
		double l1 = lw * (pi / 180);

		// Querkrümmungshalbmesser nd
		double nd = a / Math.sqrt(1 - e2q * Math.sin(b1) * Math.sin(b1));

		// Kartesische Koordinaten des Quellsystems WGS84
		double xw = nd * Math.cos(b1) * Math.cos(l1);
		double yw = nd * Math.cos(b1) * Math.sin(l1);
		double zw = (1 - e2q) * nd * Math.sin(b1);

		// Kartesische Koordinaten des Zielsystems (datum shift) Potsdam
		double x = xw + dx;
		double y = yw + dy;
		double z = zw + dz;

		// Berechnung von Breite und Länge im Zielsystem
		double rb = Math.sqrt(x * x + y * y);
		double b2 = (180 / pi) * Math.atan((z / rb) / (1 - e2));
		double l2 = 0;
		if (x > 0)
			l2 = (180 / pi) * Math.atan(y / x);
		if (x < 0 && y > 0)
			l2 = (180 / pi) * Math.atan(y / x) + 180;
		if (x < 0 && y < 0)
			l2 = (180 / pi) * Math.atan(y / x) - 180;

		double lp = l2;
		double bp = b2;

		if (lp < 5 || lp > 16 || bp < 46 || bp > 56) {
			return null;
		}
		return new Point2D.Double(lp, bp);
	}

	public static Point2D.Double pot2wgs(Point2D.Double punkt) {
		/*
		 * Copyright (c) 2006, HELMUT H. HEIMEIER Permission is hereby granted,
		 * free of charge, to any person obtaining a copy of this software and
		 * associated documentation files (the "Software"), to deal in the
		 * Software without restriction, including without limitation the rights
		 * to use, copy, modify, merge, publish, distribute, sublicense, and/or
		 * sell copies of the Software, and to permit persons to whom the
		 * Software is furnished to do so, subject to the following conditions:
		 * The above copyright notice and this permission notice shall be
		 * included in all copies or substantial portions of the Software.
		 */

		/*
		 * Die Funktion verschiebt das Kartenbezugssystem (map datum) vom in
		 * Deutschland gebräuchlichen Potsdam-Datum zum WGS84 (World Geodetic
		 * System 84) Datum. Geographische Länge lp und Breite bp gemessen in
		 * grad auf dem Bessel-Ellipsoid müssen gegeben sein. Ausgegeben werden
		 * geographische Länge lw und Breite bw (in grad) auf dem
		 * WGS84-Ellipsoid. Bei der Transformation werden die Ellipsoidachsen
		 * parallel verschoben um dx = 587 m, dy = 16 m und dz = 393 m.
		 */

		// Geographische Länge lp und Breite bp im Potsdam Datum
		double lp = punkt.getX();
		double bp = punkt.getY();

		// Quellsystem Potsdam Datum
		// Große Halbachse a und Abplattung fq
		double a = 6378137.000 - 739.845;
		double fq = 3.35281066e-3 - 1.003748e-05;

		// Zielsystem WGS84 Datum
		// Abplattung f
		double f = 3.35281066e-3;

		// Parameter für datum shift
		int dx = 587;
		int dy = 16;
		int dz = 393;
		/*int dx = 631;
		int dy = 23;
		int dz = 451;*/

		// Quadrat der ersten numerischen Exzentrizität in Quell- und Zielsystem
		double e2q = (2 * fq - fq * fq);
		double e2 = (2 * f - f * f);

		// Breite und Länge in Radianten
		double pi = Math.PI;
		double b1 = bp * (pi / 180);
		double l1 = lp * (pi / 180);

		// Querkrümmungshalbmesser nd
		double nd = a / Math.sqrt(1 - e2q * Math.sin(b1) * Math.sin(b1));

		// Kartesische Koordinaten des Quellsystems Potsdam
		double xp = nd * Math.cos(b1) * Math.cos(l1);
		double yp = nd * Math.cos(b1) * Math.sin(l1);
		double zp = (1 - e2q) * nd * Math.sin(b1);

		// Kartesische Koordinaten des Zielsystems (datum shift) WGS84
		double x = xp + dx;
		double y = yp + dy;
		double z = zp + dz;

		// Berechnung von Breite und Länge im Zielsystem
		double rb = Math.sqrt(x * x + y * y);
		double b2 = (180 / pi) * Math.atan((z / rb) / (1 - e2));

		double l2 = 0;
		if (x > 0)
			l2 = (180 / pi) * Math.atan(y / x);
		if (x < 0 && y > 0)
			l2 = (180 / pi) * Math.atan(y / x) + 180;
		if (x < 0 && y < 0)
			l2 = (180 / pi) * Math.atan(y / x) - 180;

		double lw = l2;
		double bw = b2;
		return new Point2D.Double(lw, bw);
	}

	public static Point2D.Double gk2geo(Point2D.Double punkt) {
		/*
		 * Copyright (c) 2006, HELMUT H. HEIMEIER Permission is hereby granted,
		 * free of charge, to any person obtaining a copy of this software and
		 * associated documentation files (the "Software"), to deal in the
		 * Software without restriction, including without limitation the rights
		 * to use, copy, modify, merge, publish, distribute, sublicense, and/or
		 * sell copies of the Software, and to permit persons to whom the
		 * Software is furnished to do so, subject to the following conditions:
		 * The above copyright notice and this permission notice shall be
		 * included in all copies or substantial portions of the Software.
		 */

		/*
		 * Die Funktion wandelt GK Koordinaten in geographische Koordinaten um.
		 * Rechtswert rw und Hochwert hw müssen gegeben sein. Berechnet werden
		 * geographische Länge lp und Breite bp im Potsdam Datum.
		 */

		// Rechtswert rw und Hochwert hw im Potsdam Datum
		double rw = punkt.getX();
		double hw = punkt.getY();

		// Potsdam Datum
		// Große Halbachse a und Abplattung f
		double a = 6377397.155;
		double f = 3.34277321e-3;
		double pi = Math.PI;

		// Polkrümmungshalbmesser c
		double c = a / (1 - f);

		// Quadrat der zweiten numerischen Exzentrizität
		double ex2 = (2 * f - f * f) / ((1 - f) * (1 - f));
		double ex4 = ex2 * ex2;
		double ex6 = ex4 * ex2;
		double ex8 = ex4 * ex4;

		// Koeffizienten zur Berechnung der geographischen Breite aus gegebener
		// Meridianbogenlänge
		double e0 = c
				* (pi / 180)
				* (1 - 3 * ex2 / 4 + 45 * ex4 / 64 - 175 * ex6 / 256 + 11025 * ex8 / 16384);
		double f2 = (180 / pi)
				* (3 * ex2 / 8 - 3 * ex4 / 16 + 213 * ex6 / 2048 - 255 * ex8 / 4096);
		double f4 = (180 / pi)
				* (21 * ex4 / 256 - 21 * ex6 / 256 + 533 * ex8 / 8192);
		double f6 = (180 / pi) * (151 * ex6 / 6144 - 453 * ex8 / 12288);

		// Geographische Breite bf zur Meridianbogenlänge gf = hw
		double sigma = hw / e0;
		double sigmr = sigma * pi / 180;
		double bf = sigma + f2 * Math.sin(2 * sigmr) + f4 * Math.sin(4 * sigmr)
				+ f6 * Math.sin(6 * sigmr);

		// Breite bf in Radianten
		double br = bf * pi / 180;
		double tan1 = Math.tan(br);
		double tan2 = tan1 * tan1;
		double tan4 = tan2 * tan2;

		double cos1 = Math.cos(br);
		double cos2 = cos1 * cos1;

		double etasq = ex2 * cos2;

		// Querkrümmungshalbmesser nd
		double nd = c / Math.sqrt(1 + etasq);
		double nd2 = nd * nd;
		double nd4 = nd2 * nd2;
		double nd6 = nd4 * nd2;
		double nd3 = nd2 * nd;
		double nd5 = nd4 * nd;

		// Längendifferenz dl zum Bezugsmeridian lh
		int kz = (int) (rw / 1e6);
		int lh = kz * 3;
		double dy = rw - (kz * 1e6 + 500000);
		double dy2 = dy * dy;
		double dy4 = dy2 * dy2;
		double dy3 = dy2 * dy;
		double dy5 = dy4 * dy;
		double dy6 = dy3 * dy3;

		double b2 = -tan1 * (1 + etasq) / (2 * nd2);
		double b4 = tan1 * (5 + 3 * tan2 + 6 * etasq * (1 - tan2)) / (24 * nd4);
		double b6 = -tan1 * (61 + 90 * tan2 + 45 * tan4) / (720 * nd6);

		double l1 = 1 / (nd * cos1);
		double l3 = -(1 + 2 * tan2 + etasq) / (6 * nd3 * cos1);
		double l5 = (5 + 28 * tan2 + 24 * tan4) / (120 * nd5 * cos1);

		// Geographischer Breite bp und Länge lp als Funktion von Rechts- und
		// Hochwert
		double bp = bf + (180 / pi) * (b2 * dy2 + b4 * dy4 + b6 * dy6);
		double lp = lh + (180 / pi) * (l1 * dy + l3 * dy3 + l5 * dy5);

		if (lp < 5 || lp > 16 || bp < 46 || bp > 56) {
			// RW und/oder HW ungültig für das deutsche Gauss-Krüger-System
			return null;
		}
		return new Point2D.Double(lp, bp);
	}

	public static Point2D.Double mgr2utm(String raster, Point2D.Double punkt) {
		/*
		 * Copyright (c) 2006, HELMUT H. HEIMEIER Permission is hereby granted,
		 * free of charge, to any person obtaining a copy of this software and
		 * associated documentation files (the "Software"), to deal in the
		 * Software without restriction, including without limitation the rights
		 * to use, copy, modify, merge, publish, distribute, sublicense, and/or
		 * sell copies of the Software, and to permit persons to whom the
		 * Software is furnished to do so, subject to the following conditions:
		 * The above copyright notice and this permission notice shall be
		 * included in all copies or substantial portions of the Software.
		 */

		/*
		 * Die Funktion wandelt militärische UTM Koordinaten (MGR oder UTMREF)
		 * in zivile UTM Koordinaten um. UTM Zone zone, raster und utmref müssen
		 * gegeben sein. In zone muss die aus 2 Ziffern bestehende Längenzone
		 * enthaltens ein gefolgt von der aus einem Buchstaben bestehenden
		 * Bandangabe. In raster muss die aus 2 Buchstaben bestehende Kennung
		 * für das 100 km x 100 km Rasterfeld enthalten sein. In UTMREF muss der
		 * 5 stellige Ostwert stehen gefolgt von einem blank und dem 5 stelligen
		 * Nordwert. Berechnet wird daraus der 7 stellige Ost- und Nordwert im
		 * zivilen UTM System.
		 */

		// Längenzone zone, Ostwert ew und Nordwert nw im WGS84 Datum
		double ew2 = punkt.getX();
		double nw2 = punkt.getY();

		String m_east_0 = "STUVWXYZ";
		String m_east_1 = "ABCDEFGH";
		String m_east_2 = "JKLMNPQR";
		String m_north_0 = "FGHJKLMNPQRSTUVABCDE";
		String m_north_1 = "ABCDEFGHJKLMNPQRSTUV";

		@SuppressWarnings("unused")
		String zone = raster.substring(0, 3);
		String r_east = raster.substring(3, 4);
		String r_north = raster.substring(4, 5);

		int i = (Integer.parseInt(raster.substring(0, 2))) % 3;
		int m_ce = 0;
		if (i == 0)
			m_ce = m_east_0.indexOf(r_east) + 1;
		if (i == 1)
			m_ce = m_east_1.indexOf(r_east) + 1;
		if (i == 2)
			m_ce = m_east_2.indexOf(r_east) + 1;
		String ew = "0" + m_ce + ew2;

		i = (Integer.parseInt(raster.substring(0, 2))) % 2;
		int m_cn = 0;
		if (i == 0)
			m_cn = m_north_0.indexOf(r_north);
		else
			m_cn = m_north_1.indexOf(r_north);

		char band = raster.charAt(2);
		if (band >= 'N') {
			if (band == 'Q' && m_cn < 10)
				m_cn = m_cn + 20;
			if (band >= 'R')
				m_cn = m_cn + 20;
			if (band == 'S' && m_cn < 30)
				m_cn = m_cn + 20;
			if (band >= 'T')
				m_cn = m_cn + 20;
			if (band == 'U' && m_cn < 50)
				m_cn = m_cn + 20;
		} else {
			if (band == 'C' && m_cn < 10)
				m_cn = m_cn + 20;
			if (band >= 'D')
				m_cn = m_cn + 20;
			if (band == 'F' && m_cn < 30)
				m_cn = m_cn + 20;
			if (band >= 'G')
				m_cn = m_cn + 20;
			if (band == 'H' && m_cn < 50)
				m_cn = m_cn + 20;
			if (band >= 'J')
				m_cn = m_cn + 20;
			if (band == 'K' && m_cn < 70)
				m_cn = m_cn + 20;
			if (band >= 'L')
				m_cn = m_cn + 20;
		}

		String nw;
		if (String.valueOf(m_cn).length() == 1)
			nw = "0" + m_cn + String.valueOf(nw2);
		else
			nw = "" + m_cn + nw2;
		return new Point2D.Double(Integer.parseInt(ew), Integer.parseInt(nw));
	}

	public static String utm2mgr(String zone, Point2D.Double punkt) {
		/*
		 * Copyright (c) 2006, HELMUT H. HEIMEIER Permission is hereby granted,
		 * free of charge, to any person obtaining a copy of this software and
		 * associated documentation files (the "Software"), to deal in the
		 * Software without restriction, including without limitation the rights
		 * to use, copy, modify, merge, publish, distribute, sublicense, and/or
		 * sell copies of the Software, and to permit persons to whom the
		 * Software is furnished to do so, subject to the following conditions:
		 * The above copyright notice and this permission notice shall be
		 * included in all copies or substantial portions of the Software.
		 */

		/*
		 * Die Funktion wandelt zivile UTM Koordinaten in militärische
		 * Koordinaten um. UTM Zone zone, Ostwert ew und Nordwert nw müssen
		 * gegeben sein. Zurückgegeben wird das Rasterfeld raster sowie die aus
		 * den letzten 5 Stellen von Ost- und Nordwert gebildete
		 * Koordinatenangabe UTMREF.
		 */

		// Längenzone zone, Ostwert ew und Nordwert nw im WGS84 Datum
		String ew = String.valueOf(punkt.getX());
		String nw = String.valueOf(punkt.getY());

		int z1 = Integer.parseInt(zone.substring(0, 2));
		char z2 = zone.charAt(2);
		int ew1 = Integer.parseInt(ew.substring(0, 2));
		int nw1 = Integer.parseInt(nw.substring(0, 2));
		String ew2 = ew.substring(2, 7);
		String nw2 = nw.substring(2, 7);

		String m_east = "ABCDEFGHJKLMNPQRSTUVWXYZ";
		String m_north = "ABCDEFGHJKLMNPQRSTUV";

		if (z1 < 1 || z1 > 60 || z2 < 'C' || z2 > 'X')
			return null;
		// ist keine gültige UTM Zonenangabe

		int i = z1 % 3;
		int m_ce = 0;
		if (i == 1)
			m_ce = ew1 - 1;
		if (i == 2)
			m_ce = ew1 + 7;
		if (i == 0)
			m_ce = ew1 + 15;

		i = z1 % 2;
		int m_cn = 0;
		if (i == 1)
			m_cn = 0;
		else
			m_cn = 5;

		i = nw1;
		while (i - 20 >= 0)
			i = i - 20;
		m_cn = m_cn + i;
		if (m_cn > 19)
			m_cn = m_cn - 20;

		String raster = zone + m_east.charAt(m_ce) + m_north.charAt(m_cn);
		return raster + ew2 + nw2;
	}

	public static Point2D.Double utm2geo(String zone, Point2D.Double punkt) {
		/*
		 * Copyright (c) 2006, HELMUT H. HEIMEIER Permission is hereby granted,
		 * free of charge, to any person obtaining a copy of this software and
		 * associated documentation files (the "Software"), to deal in the
		 * Software without restriction, including without limitation the rights
		 * to use, copy, modify, merge, publish, distribute, sublicense, and/or
		 * sell copies of the Software, and to permit persons to whom the
		 * Software is furnished to do so, subject to the following conditions:
		 * The above copyright notice and this permission notice shall be
		 * included in all copies or substantial portions of the Software.
		 */

		/*
		 * Die Funktion wandelt UTM Koordinaten in geographische Koordinaten um.
		 * UTM Zone, Ostwert ew und Nordwert nw müssen gegeben sein. Berechnet
		 * werden geographische Länge lw und Breite bw im WGS84 Datum.
		 */

		// Längenzone zone, Ostwert ew und Nordwert nw im WGS84 Datum
		if (zone == "")
			zone = "32N";
		double ew = punkt.getX();
		double nw = punkt.getY();

		char band = zone.charAt(2);
		int zoneN = Integer.parseInt(zone.substring(0, 2));

		// WGS84 Datum
		// Große Halbachse a und Abplattung f
		double a = 6378137.000;
		double f = 3.35281068e-3;
		double pi = Math.PI;

		// Polkrümmungshalbmesser c
		double c = a / (1 - f);

		// Quadrat der zweiten numerischen Exzentrizität
		double ex2 = (2 * f - f * f) / ((1 - f) * (1 - f));
		double ex4 = ex2 * ex2;
		double ex6 = ex4 * ex2;
		double ex8 = ex4 * ex4;

		// Koeffizienten zur Berechnung der geographischen Breite aus gegebener
		// Meridianbogenlänge
		double e0 = c
				* (pi / 180)
				* (1 - 3 * ex2 / 4 + 45 * ex4 / 64 - 175 * ex6 / 256 + 11025 * ex8 / 16384);
		double f2 = (180 / pi)
				* (3 * ex2 / 8 - 3 * ex4 / 16 + 213 * ex6 / 2048 - 255 * ex8 / 4096);
		double f4 = (180 / pi)
				* (21 * ex4 / 256 - 21 * ex6 / 256 + 533 * ex8 / 8192);
		double f6 = (180 / pi) * (151 * ex6 / 6144 - 453 * ex8 / 12288);

		// Entscheidung Nord-/Süd Halbkugel
		double m_nw;
		if (band >= 'N')
			m_nw = nw;
		else
			m_nw = nw - 10e6;

		// Geographische Breite bf zur Meridianbogenlänge gf = m_nw
		double sigma = (m_nw / 0.9996) / e0;
		double sigmr = sigma * pi / 180;
		double bf = sigma + f2 * Math.sin(2 * sigmr) + f4 * Math.sin(4 * sigmr)
				+ f6 * Math.sin(6 * sigmr);

		// Breite bf in Radianten
		double br = bf * pi / 180;
		double tan1 = Math.tan(br);
		double tan2 = tan1 * tan1;
		double tan4 = tan2 * tan2;

		double cos1 = Math.cos(br);
		double cos2 = cos1 * cos1;

		double etasq = ex2 * cos2;

		// Querkrümmungshalbmesser nd
		double nd = c / Math.sqrt(1 + etasq);
		double nd2 = nd * nd;
		double nd4 = nd2 * nd2;
		double nd6 = nd4 * nd2;
		double nd3 = nd2 * nd;
		double nd5 = nd4 * nd;

		// Längendifferenz dl zum Bezugsmeridian lh
		int lh = (zoneN - 30) * 6 - 3;
		double dy = (ew - 500000) / 0.9996;
		double dy2 = dy * dy;
		double dy4 = dy2 * dy2;
		double dy3 = dy2 * dy;
		double dy5 = dy3 * dy2;
		double dy6 = dy3 * dy3;

		double b2 = -tan1 * (1 + etasq) / (2 * nd2);
		double b4 = tan1 * (5 + 3 * tan2 + 6 * etasq * (1 - tan2)) / (24 * nd4);
		double b6 = -tan1 * (61 + 90 * tan2 + 45 * tan4) / (720 * nd6);

		double l1 = 1 / (nd * cos1);
		double l3 = -(1 + 2 * tan2 + etasq) / (6 * nd3 * cos1);
		double l5 = (5 + 28 * tan2 + 24 * tan4) / (120 * nd5 * cos1);

		// Geographische Breite bw und Länge lw als Funktion von Ostwert ew
		// und Nordwert nw
		double bw = bf + (180 / pi) * (b2 * dy2 + b4 * dy4 + b6 * dy6);
		double lw = lh + (180 / pi) * (l1 * dy + l3 * dy3 + l5 * dy5);
		return new Point2D.Double(lw, bw);
	}

}
