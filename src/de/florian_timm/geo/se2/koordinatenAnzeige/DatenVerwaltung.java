package de.florian_timm.geo.se2.koordinatenAnzeige;

import java.awt.geom.Point2D;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;

import javax.swing.JFrame;
import javax.swing.JFileChooser;
import javax.swing.filechooser.FileNameExtensionFilter;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

/**
 * Einlesen und Verwalten von Geo-Daten
 *
 * @version 2015.06.14
 * @author Florian Timm
 */

public class DatenVerwaltung {
	ArrayList<Punkt> punkte = new ArrayList<Punkt>();
	ArrayList<PolyLinie> polyLinien = new ArrayList<PolyLinie>();

	JFileChooser fcR, fcW;
	File f;
	String inhalt = "";
	JFrame frame;
	FileNameExtensionFilter gpx, txt;

	/**
	 * Konstruktor
	 * 
	 * @param frameL
	 *            JFrame des aufrufenden Fensters
	 */
	public DatenVerwaltung(JFrame frameL) {
		this.frame = frameL;
		fcR = new JFileChooser();
		fcW = new JFileChooser();
		FileNameExtensionFilter filter = new FileNameExtensionFilter(
				"Alle unterstützten Dateien (*.txt, *.dat, *.gpx)", "txt",
				"dat", "gpx");
		FileNameExtensionFilter gpx2 = new FileNameExtensionFilter(
				"GPX-Dateien (*.gpx)", "gpx");
		FileNameExtensionFilter txtdat = new FileNameExtensionFilter(
				"ASCII-Dateien (*.dat, *.txt)", "txt", "dat");
		gpx = new FileNameExtensionFilter("GPX-Datei (*.gpx)", "gpx");
		txt = new FileNameExtensionFilter("TXT-Datei (*.txt)", "txt");
		fcR.setFileFilter(filter);
		fcR.addChoosableFileFilter(gpx2);
		fcR.addChoosableFileFilter(txtdat);

		fcW.setAcceptAllFileFilterUsed(false);
		fcW.addChoosableFileFilter(txt);
		fcW.addChoosableFileFilter(gpx);
	}

	/**
	 * Öffnet den Dialog zur Auswahl einer Datei
	 * 
	 * @throws NoSelectionException
	 *             wird geworfen, wenn nichts ausgewählt wurde
	 * @return vom Benutzer ausgewählte Datei, kann von leseDatei()
	 *         weiterverarbeitet werden
	 * @see leseDatei
	 */
	public File waehleDatei() throws NoSelectionException {

		int option = fcR.showOpenDialog(frame);

		if (option == JFileChooser.APPROVE_OPTION) {
			return fcR.getSelectedFile();
		} else {
			throw new NoSelectionException();
		}

	}

	/**
	 * Liest eine Koordinaten- oder Trackdatei ein
	 * 
	 * @param file
	 *            Datei
	 * @return Protokoll des Einlesens
	 */
	public String leseDatei(File file) {

		try {
			String fehler = "";
			ArrayList<Punkt> pkt_tmp = new ArrayList<Punkt>();
			ArrayList<PolyLinie> polyLinie_tmp = new ArrayList<PolyLinie>();

			// XML-Datei im GPX-Format einlesen
			KoordKontrolle kK = null;
			if (file.getName().toLowerCase().endsWith(".gpx")) {
				DocumentBuilder db = DocumentBuilderFactory.newInstance()
						.newDocumentBuilder();

				Element doc = db.parse(file).getDocumentElement();
				// String ns = doc.getNamespaceURI();
				NodeList wpts = doc.getElementsByTagName("wpt");
				// System.out.println(wpts.getLength());
				for (int i = 0; i < wpts.getLength(); i++) {
					// wpt.getElementsByTagName("ele").item(0).getTextContent();
					Element wpt = (Element) wpts.item(i);
					String lon = wpt.getAttribute("lon");
					String lat = wpt.getAttribute("lat");
					String ele = null;
					NodeList eleL = wpt.getElementsByTagName("ele");
					if (eleL.getLength() != 0) {
						ele = eleL.item(0).getTextContent();
					}

					// System.out.println(lon+lat+ele);
					try {
						Point2D.Double pkt = new Point2D.Double(
								Double.parseDouble(lon),
								Double.parseDouble(lat));
						if (ele == null) {
							pkt_tmp.add(new Punkt(getMaxPktNr() + 1 + i, pkt
									.getX(), pkt.getY()));
						} else {
							pkt_tmp.add(new Punkt(getMaxPktNr() + 1 + i, pkt
									.getX(), pkt.getY(), Double
									.parseDouble(ele)));
						}

					} catch (Exception e) {
						System.out.print("Parser-Fehler: " + e.toString());
						fehler += "Fehler in XML: " + e.toString() + "\n";
					}

				}

				// Tracks
				NodeList trks = doc.getElementsByTagName("trk");

				for (int i = 0; i < trks.getLength(); i++) {
					NodeList trksegs = ((Element) trks.item(i))
							.getElementsByTagName("trkseg");
					for (int j = 0; j < trksegs.getLength(); j++) {
						Element trk = (Element) trksegs.item(j);
						ArrayList<Point2D.Double> trkPkt = new ArrayList<Point2D.Double>();

						NodeList trkpts = trk.getElementsByTagName("trkpt");
						for (int k = 0; k < trkpts.getLength(); k++) {
							Element trkpt = (Element) trkpts.item(k);
							String lon = trkpt.getAttribute("lon");
							String lat = trkpt.getAttribute("lat");

							try {
								trkPkt.add((new Point2D.Double(Double
										.parseDouble(lon), Double
										.parseDouble(lat))));
							} catch (Exception e) {
								fehler += "Fehler in XML: " + e.toString()
										+ "\n";
							}
						}
						polyLinie_tmp.add(new PolyLinie(trkPkt));
					}
				}
				// Punkte sortieren
				Collections.sort(pkt_tmp);

				// Kontrollfenster anzeigen
				kK = new KoordKontrolle(frame, pkt_tmp, polyLinie_tmp,
						KoordTransformer.WGS84_LonLat);
			} else if (file.getName().toLowerCase().endsWith(".txt")
					|| file.getName().toLowerCase().endsWith(".dat")) {
				// dat oder txt-Dateien

				BufferedReader br = new BufferedReader(new FileReader(file));
				String zeile = "";
				int zeileNr = 0;

				while ((zeile = br.readLine()) != null) {
					zeileNr++;
					String[] sp = zeile.trim().split("\\s+");
					if (sp.length == 2) {
						try {
							pkt_tmp.add(new Punkt(getMaxPktNr() + 1, Double
									.parseDouble(sp[0]), Double
									.parseDouble(sp[1])));
						} catch (Exception e) {
							fehler += "Z" + zeileNr + ": " + zeile + "\n";
						}
					} else if (sp.length == 3) {
						try {
							pkt_tmp.add(new Punkt(Integer.parseInt(sp[0]),
									Double.parseDouble(sp[1]), Double
											.parseDouble(sp[2])));
						} catch (Exception e) {
							try {
								pkt_tmp.add(new Punkt(getMaxPktNr() + 1, Double
										.parseDouble(sp[0]), Double
										.parseDouble(sp[1]), Double
										.parseDouble(sp[2])));
							} catch (Exception ex) {
								fehler += "Z" + zeileNr + ": " + zeile + "\n";
							}

						}

					} else if (sp.length == 4) {
						try {
							pkt_tmp.add(new Punkt(Integer.parseInt(sp[0]),
									Double.parseDouble(sp[1]), Double
											.parseDouble(sp[2]), Double
											.parseDouble(sp[3])));
						} catch (Exception e) {
							fehler += "Z" + zeileNr + ": " + zeile + "\n";
						}
					} else {
						fehler += "Z" + zeileNr + ": " + zeile + "\n";
					}
				}
				br.close();
				// Punkte sortieren
				Collections.sort(pkt_tmp);

				// Kontrollfenster anzeigen
				kK = new KoordKontrolle(frame, pkt_tmp, polyLinie_tmp);
			} // Ende vom Datei einlesen

			// Übernommene Koordinaten und Tracks verarbeiten
			if (kK.getAuswahl() == KoordKontrolle.OK) {
				ArrayList<Punkt> pkt_tmp2 = kK.getPunktAuswahl();
				ArrayList<PolyLinie> poly_tmp2 = kK.getPolyLinieAuswahl();

				int kSys = kK.getKoordSys();
				// System.out.println("Koordsys " + kSys +
				// KoordTransformer.SYSTEME[kSys]);

				for (int i = 0; i < pkt_tmp2.size(); i++) {
					Punkt pkt = pkt_tmp2.get(i);
					Point2D.Double poi = KoordTransformer.wandleKoordinaten(
							kSys, KoordTransformer.WGS84_UTM32, pkt.getPoint());
					punkte.add(new Punkt(pkt.getPunktNummer(), poi.getX(), poi
							.getY(), pkt.getHoehe()));
				}

				for (int i = 0; i < poly_tmp2.size(); i++) {
					PolyLinie poly = poly_tmp2.get(i);
					ArrayList<Point2D.Double> pkts = poly.getPunkte();
					ArrayList<Point2D.Double> pkts_tmp = new ArrayList<Point2D.Double>();
					for (int j = 0; j < pkts.size(); j++) {
						pkts.get(j);
						Point2D.Double poi = KoordTransformer
								.wandleKoordinaten(kSys,
										KoordTransformer.WGS84_UTM32,
										pkts.get(j));
						pkts_tmp.add(poi);
					}
					polyLinien.add(new PolyLinie(pkts_tmp));
				}

				// punkte.addAll(pkt_tmp2);
				// polyLinien.addAll(poly_tmp2);
				Collections.sort(punkte);
				String returning = getText(kK.getPunktAuswahl()) + "\n";
				if (fehler != "") {
					if (returning != "")
						returning += "\n";
					returning += "Folgende Zeilen sind fehlerhaft und konnten nicht eingelesen werden:\n"
							+ fehler;
				}
				return returning;

			} // end of if-else
		} catch (FileNotFoundException fnfe) {
			System.out.print("Datei nicht gefunden");
			return "Datei nicht gefunden\n";
		} catch (IOException e) {
			System.out.print("IO-Fehler: " + e.toString());
			return "IO-Fehler: " + e.toString() + "\n";
		} /*
		 * catch (Exception ex) { return "Fehler: " + ex.toString(); }
		 */catch (SAXException e) {
			System.out.print("SAX-Fehler: " + e.toString());
			return "Fehler: " + e.toString() + "\n";
		} catch (ParserConfigurationException e) {
			System.out.print("Parser-Fehler: " + e.toString());
			return "Fehler: " + e.toString() + "\n";
		}
		return "Abbruch\n";
	}

	/**
	 * Gibt Punkte aus einem Array als String wieder
	 * 
	 * @param punkte
	 *            Darzustellende Punkte
	 * @return Punkte als String
	 */
	public String getText(ArrayList<Punkt> punkte) {
		String gesamt = "";
		for (int i = 0; i < punkte.size(); i++) {
			if (i != 0) {
				gesamt += "\n";
			} // end of if
			gesamt += punkte.get(i).toStringoHmT();
		}
		return gesamt;
	}

	/**
	 * Speichert die geöffneten Punkte in einer Textdatei
	 * 
	 * @return true, wenn Datei erfolgreich geschrieben wurde
	 */
	public boolean speicherPunkte() {

		int option = fcW.showSaveDialog(frame);
		try {
			if (option == JFileChooser.APPROVE_OPTION) {
				if (fcW.getFileFilter().getDescription()
						.equals(txt.getDescription())) {
					File file = fcW.getSelectedFile();
					if (!(file.getName().toLowerCase().endsWith(".txt"))) {
						file = new File(file.getAbsolutePath().concat(".txt"));
					}
					BufferedWriter bw = new BufferedWriter(new FileWriter(file));

					// bw.write();

					for (int i = 0; i < punkte.size(); i++) {
						if (i != 0) {
							bw.newLine();
						}
						bw.write(punkte.get(i).getPunktNummer() + " "
								+ punkte.get(i).getRechtsWert() + " "
								+ punkte.get(i).getHochWert() + " "
								+ punkte.get(i).getHoehe());
					}
					bw.close();
				} else if (fcW.getFileFilter().getDescription()
						.equals(gpx.getDescription())) {
					File file = fcW.getSelectedFile();
					if (!(file.getName().toLowerCase().endsWith(".gpx"))) {
						file = new File(file.getAbsolutePath().concat(".gpx"));
					}

					// / GPX_Datei erzeugen

					DocumentBuilder db = DocumentBuilderFactory.newInstance()
							.newDocumentBuilder();

					Document doc = db.newDocument();
					Element root = doc.createElementNS(
							"http://www.topografix.com/GPX/1/1", "gpx");
					root.setAttribute("creator", "KoordinatenAnzeige");
					root.setAttribute("version", "1.1");
					root.setAttribute("xmlns:xsi",
							"http://www.w3.org/2001/XMLSchema-instance");
					root.setAttribute("xsi:schemaLocation",
							"http://www.topografix.com/GPX/1/1 http://www.topografix.com/GPX/1/1/gpx.xsd");
					doc.appendChild(root);
					Element metadata = doc.createElement("metadata");
					root.appendChild(metadata);
					Element name = doc.createElement("name");
					name.setTextContent(file.getName().replaceAll("[^A-Za-z0-9+-]", ""));
					Element desc = doc.createElement("desc");
					desc.setTextContent("Koordinaten-Export aus KoordinatenAnzeige");
					Element author = doc.createElement("author");
					Element autName = doc.createElement("name");
					autName.setTextContent("KoordinatenAnzeige");
					author.appendChild(autName);
					metadata.appendChild(name);
					metadata.appendChild(desc);
					metadata.appendChild(author);

					for (int i = 0; i < punkte.size(); i++) {
						Element wpt = doc.createElement("wpt");
						Point2D.Double pkt = KoordTransformer
								.wandleKoordinaten(
										KoordTransformer.WGS84_UTM32,
										KoordTransformer.WGS84_LonLat, punkte
												.get(i).getPoint());
						wpt.setAttribute("lat", GMath.geoRund(pkt.getY(), 7).replace(',', '.'));
						wpt.setAttribute("lon", GMath.geoRund(pkt.getX(), 7).replace(',', '.'));
						Element ele = doc.createElement("ele");
						ele.setTextContent(GMath.geoRund(punkte.get(i)
								.getHoehe(), 4).replace(',', '.'));
						Element nameWpt = doc.createElement("name");
						nameWpt.setTextContent(String.valueOf(punkte.get(i)
								.getPunktNummer()));
						wpt.appendChild(ele);
						wpt.appendChild(nameWpt);
						root.appendChild(wpt);
					}
					
					for (int i = 0; i < polyLinien.size(); i++) {
						Element trk = doc.createElement("trk");
						root.appendChild(trk);
						Element trkName = doc.createElement("name");
						trkName.setTextContent(polyLinien.get(i).getName());
						trk.appendChild(trkName);
						Element trkDesc = doc.createElement("desc");
						trkDesc.setTextContent(polyLinien.get(i).getName());
						trk.appendChild(trkDesc);
						Element trkseg = doc.createElement("trkseg");
						trk.appendChild(trkseg);
						
					}

					// Use a Transformer for output
					TransformerFactory tFactory = TransformerFactory
							.newInstance();
					Transformer transformer = tFactory.newTransformer();

					DOMSource source = new DOMSource(doc);
					StreamResult result = new StreamResult(new FileWriter(file));
					transformer.transform(source, result);
				}

				return true;
			}
			return false;
		} catch (Exception e) {

			return false;
		}
	}

	/**
	 * Löscht die Punkte
	 */
	public void leerePunktliste() {
		punkte.clear();

	}

	/**
	 * Gibt einen Punkt nach ID zurück
	 * 
	 * @param i
	 *            ID des Punktes
	 * @return Punkt
	 */
	public Punkt getPkt(int i) {
		return punkte.get(i);
	}

	/**
	 * Löscht einen Punkt nach ID
	 * 
	 * @param i
	 *            ID des Punktes
	 */
	public void delPkt(int i) {
		punkte.remove(i);
	}

	/**
	 * Gibt die Anzahl aller Punkte zurück
	 * 
	 * @return Anzahl der Punkte
	 */
	public int getPktAnzahl() {
		return punkte.size();
	}

	/**
	 * Fügt einen Punkt hinzu
	 * 
	 * @param pkt
	 *            Punkt, der hinzugefügt werden soll
	 * @return Formatierter String mit Daten des Punktes
	 */
	public String addPunkt(Punkt pkt) {
		punkte.add(pkt);
		Collections.sort(punkte);
		return pkt.toStringoHmT();
	}

	/**
	 * Gibt die höchste Punktnummer zurück
	 * 
	 * @return Höchste Punktnummer
	 */
	public int getMaxPktNr() {
		if (punkte.size() != 0) {
			return punkte.get(punkte.size() - 1).getPunktNummer();
		}
		return 0;

	}

	/**
	 * Gibt Punkte in der Nähe zurück
	 * 
	 * @param punkt
	 *            Koordinate im übergeordneten System
	 * @param abstand
	 *            Umkreis für die Suche
	 * @return Liste der Punkte in der Näche
	 */
	public ArrayList<Punkt> getPointByCoord(Point2D.Double punkt, double abstand) {
		ArrayList<Punkt> ausgabe = new ArrayList<Punkt>();

		for (int i = 0; i < punkte.size(); i++) {
			if (punkte.get(i).strecke(new Punkt(punkt)) < abstand) {
				ausgabe.add(punkte.get(i));
			}
		} // end of for

		return ausgabe;
	}

	/**
	 * Löscht einen Track
	 * 
	 * @param zeile
	 *            ID des zu löschenden Track
	 */
	public void delTrack(int zeile) {
		polyLinien.remove(zeile);
	}

	/**
	 * Gibt alle Punkte zurück
	 * 
	 * @return ArrayList aller Punkte
	 */
	public ArrayList<Punkt> getPunkte() {
		return punkte;
	}

	/**
	 * Gibt alle PolyLinien zurück
	 * 
	 * @return ArrayList aller PolyLinien
	 */
	public ArrayList<PolyLinie> getPolyLinien() {
		return polyLinien;
	}

	/**
	 * Löscht alle PolyLinien
	 */
	public void leerePolyLinienListe() {
		polyLinien.clear();
	}

	/**
	 * Gibt die Anzahl der Polylinien zurück
	 * 
	 * @return Anzahl der PolyLinien
	 */
	public int getPolyLinienAnzahl() {
		return polyLinien.size();

	}

	/**
	 * Gibt eine PolyLinie zurück
	 * 
	 * @param id
	 *            ID der PolyLinie
	 * @return PolyLinie
	 */
	public PolyLinie getPolyLinie(int id) {
		return polyLinien.get(id);
	}
}
