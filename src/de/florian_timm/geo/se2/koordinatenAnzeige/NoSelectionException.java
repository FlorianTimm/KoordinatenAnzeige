package de.florian_timm.geo.se2.koordinatenAnzeige;

/**
 * Fehler, wenn keine Selektion erfolgte
 *
 * @version 2015.06.14
 * @author Florian Timm
 */


public class NoSelectionException extends Exception {
	private static final long serialVersionUID = 2832922850452942928L;

	/**
	 * Konstruktor
	 */
	public NoSelectionException() {
		super("Nichts ausgewählt");
	}
}