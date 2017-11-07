package de.florian_timm.geo.se2.koordinatenAnzeige;

import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.geom.Point2D;

import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;

public class NeuerPunktByKlick extends JPanel implements ActionListener {
	private static final long serialVersionUID = -4558533513856537493L;
	JComboBox<String> koordSystem;
	JTextField nrField;
	JTextField xField;
	JTextField yField;
	JTextField hField;
	Point2D.Double gkP;

	public NeuerPunktByKlick(int x, int y, Zeichenflaeche zeichenFlaeche,
			DatenVerwaltung pv) {
		super();

		gkP = zeichenFlaeche.koordZF2GK(x, y);
		nrField = new JTextField(10);
		nrField.setText(new Integer(pv.getMaxPktNr() + 1).toString());
		xField = new JTextField(10);
		xField.setText(GMath.geoRund(gkP.getX(), 3));
		yField = new JTextField(10);
		yField.setText(GMath.geoRund(gkP.getY(), 3));
		hField = new JTextField(10);
		hField.setText(new Double(-9.9999e10).toString());

		this.setLayout(new GridLayout(5, 2));

		this.add(new JLabel("Datum"));
		koordSystem = new JComboBox<String>(KoordTransformer.SYSTEME);
		koordSystem.setSelectedIndex(KoordTransformer.WGS84_UTM32);
		koordSystem.addActionListener(this);
		this.add(koordSystem);

		this.add(new JLabel("Punktnummer"));
		this.add(nrField);

		this.add(new JLabel("Rechtswert"));
		this.add(xField);

		this.add(new JLabel("Hochwert"));
		this.add(yField);

		this.add(new JLabel("Höhe"));
		this.add(hField);

	}

	public void actionPerformed(ActionEvent ae) {
		Point2D.Double P = KoordTransformer.wandleKoordinaten(
				KoordTransformer.WGS84_UTM32, koordSystem.getSelectedIndex(),
				gkP);
		xField.setText(GMath.geoRund(P.getX(), 3));
		yField.setText(GMath.geoRund(P.getY(), 3));
	}

	public Punkt getPunkt() {
		Point2D.Double pkt = new Point2D.Double(Double.parseDouble(xField
				.getText().replace(',', '.')), Double.parseDouble(yField
				.getText().replace(',', '.')));
		if (koordSystem.getSelectedIndex() != KoordTransformer.WGS84_UTM32)
			pkt = KoordTransformer.wandleKoordinaten(
					koordSystem.getSelectedIndex(),
					KoordTransformer.WGS84_UTM32, pkt);
		return new Punkt(Integer.parseInt(nrField.getText()), pkt.getX(),
				pkt.getY(), Double.parseDouble(hField.getText().replace(',',
						'.')));
	}
}
