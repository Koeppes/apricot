package de.apricotroom.tools;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellType;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;

import de.apricotroom.bo.Edelsteine;
import de.apricotroom.bo.Farben;
import de.apricotroom.bo.Kategorien;
import de.apricotroom.bo.Lieferant;
import de.apricotroom.bo.Produkt;
import de.apricotroom.pers.JPAServiceLieferant;
import de.apricotroom.pers.JPAServiceProdukt;

public class ProduktGenerator {
	private JPAServiceProdukt serviceProdukt = new JPAServiceProdukt();
	private JPAServiceLieferant serviceLieferant = new JPAServiceLieferant();
	private Produkt previousProduct;

	public static void main(String[] args) {
		ProduktGenerator pg = new ProduktGenerator();
		pg.readFile("/Users/jurgenhochkoppler/git/APRICOTroom/src/main/java/de/apricotroom/tools/masterlisteCopy.xls");
	}

	public void readFile(String filename) {
		try {
			FileInputStream excelFile = new FileInputStream(filename);
			Workbook workbook = new HSSFWorkbook(excelFile);
			List<Produkt> products = serviceProdukt.getProdukte();
			serviceProdukt.removeAll(products);
			this.readRinge(workbook);
			this.readInventar(workbook);
			workbook.close();
		} catch (IOException e) {
			e.printStackTrace();
		}

	}

	private void readInventar(Workbook workbook) {

		Produkt p = null;
		String serial = "";
		String serial1 = "";
		String serial2 = "";
		String serial12 = "";
		List<Produkt> produkte = new ArrayList<>();
		Sheet sheetInventar = workbook.getSheetAt(0);
		for (int i = 1; i < sheetInventar.getLastRowNum(); i++) {
			Row row = sheetInventar.getRow(i);
			previousProduct = p;
			p = new Produkt();
			p.setGenerated(true);
			produkte.add(p);
			for (int j = 0; j < row.getLastCellNum(); j++) {
				Cell cell = row.getCell(j);
				if (cell != null) {
					// serialPart1
					if (j == 1) {
						int cell1Value = (int) cell.getNumericCellValue();
						serial1 = "0" + cell1Value;
					}
					// serialPart2
					if (j == 2) {
						cell.setCellType(CellType.STRING);
						serial2 = cell.getStringCellValue();
						if (serial2.length() == 1) {
							serial2 = "0" + serial2;
						}
						serial12 = serial1 + serial2.substring(0, 2);
						serial = serial1 + serial2;
					}

					// description
					if (j == 3) {
						if (p.getSize() != 0) {
							serial = serial + "-" + p.getSize();
							p.setSerialnumber(serial);
						} else {
							p.setSerialnumber(serial);
						}
						if (serial.startsWith("01")) {
							p.setKategorie(Kategorien.OHRRING.getValue());
						}
						if (serial.startsWith("02")) {
							p.setKategorie(Kategorien.HALSKETTE.getValue());
						}
						if (serial.startsWith("03")) {
							p.setKategorie(Kategorien.ARMBAND.getValue());
						}
						if (serial.contains("-g")) {
							p.setFarbe(Farben.GOLD.getValue());
						}
						if (serial.contains("-s")) {
							p.setFarbe(Farben.SILBER.getValue());
						}
						if (serial.contains("-r")) {
							p.setFarbe(Farben.ROSE.getValue());
						}
						if (serial.contains("-RQ")) {
							p.setGemstone(true);
							p.setEdelstein(Edelsteine.ROSENQUARZ.getValue());
						}
						if (serial.contains("-LB")) {
							p.setGemstone(true);
							p.setEdelstein(Edelsteine.LABRADORIT.getValue());
						}
						if (serial.contains("-AC")) {
							p.setGemstone(true);
							p.setEdelstein(Edelsteine.AQUA_CHALCEDON.getValue());
						}
						if (serial.contains("-PR")) {
							p.setGemstone(true);
							p.setEdelstein(Edelsteine.PREHNIT.getValue());
						}
						if (serial.contains("-MG")) {
							p.setGemstone(true);
							p.setEdelstein(Edelsteine.MONDSTEIN_GRAU.getValue());
						}
						if (serial.contains("-MW")) {
							p.setGemstone(true);
							p.setEdelstein(Edelsteine.MONDSTEIN_WEISS.getValue());
						}
						if (serial.endsWith("-C")) {
							p.setGemstone(true);
							p.setEdelstein(Edelsteine.CHALCEDON.getValue());
						}
						String desc = cell.getStringCellValue();
						if (desc == null || "".equals(desc) && previousProduct != null
								&& previousProduct.getDescription() != null
								&& !previousProduct.getDescription().isEmpty()
								&& previousProduct.getSerialnumber().substring(0, 6).equals(serial12)) {
							p.setDescription(previousProduct.getDescription());
						} else {
							p.setDescription(desc);
						}
					}
					// price
					if (j == 8) {
						p.setSellingPrice(cell.getNumericCellValue());
					}
					// lieferant
					if (j == 10) {
						try {
						int nr = (int) cell.getNumericCellValue();
						Lieferant l = serviceLieferant.getLieferantByNr(nr);
						p.setLieferant(l);
						} catch (IllegalStateException e) {
							//do nothing
						}
					}
				}
			}
		}
		serviceProdukt.persistProdukte(produkte);

	}

	private void readRinge(Workbook workbook) {
		Produkt p = null;
		String serial;
		String serial1;
		String serial2;
		String serial12;
		List<Produkt> produkte = new ArrayList<>();
		Sheet sheetInventarRinge = workbook.getSheetAt(5);
		for (int i = 1; i < sheetInventarRinge.getLastRowNum(); i++) {
			Row row = sheetInventarRinge.getRow(i);
			previousProduct = p;
			serial = "";
			serial1 = "";
			serial2 = "";
			serial12 = "";
			p = new Produkt();
			p.setGenerated(true);
			p.setKategorie(Kategorien.RING.getValue());
			produkte.add(p);
			for (int j = 0; j < row.getLastCellNum(); j++) {
				Cell cell = row.getCell(j);
				if (cell != null) {
					// serialPart1
					if (j == 2) {
						int cell1Value = (int) cell.getNumericCellValue();
						serial1 = "0" + cell1Value;
					}
					// serialPart2
					if (j == 3) {
						cell.setCellType(CellType.STRING);
						serial2 = cell.getStringCellValue();
						if (serial2.length() == 1) {
							serial2 = "0" + serial2;
						}
						if (serial2.length() > 2) {
							serial12 = serial1 + serial2.substring(0, 2);
						} else {
							serial12 = serial1 + serial2;
						}
						serial = serial1 + serial2;
					}
					// size
					if (j == 4) {
						Integer size = (int) cell.getNumericCellValue();
						if (size != null) {
							p.basicSetSize(size.intValue());
						}
						if (serial.contains("-g")) {
							p.setFarbe(Farben.GOLD.getValue());
						}
						if (serial.contains("-s")) {
							p.setFarbe(Farben.SILBER.getValue());
						}
						if (serial.contains("-r")) {
							p.setFarbe(Farben.ROSE.getValue());
						}
						if (serial.contains("-RQ")) {
							p.setGemstone(true);
							p.setEdelstein(Edelsteine.ROSENQUARZ.getIndex());
						}
						if (serial.contains("-LB")) {
							p.setGemstone(true);
							p.setEdelstein(Edelsteine.LABRADORIT.getIndex());
						}
						if (serial.contains("-AC")) {
							p.setGemstone(true);
							p.setEdelstein(Edelsteine.AQUA_CHALCEDON.getIndex());
						}
						if (serial.contains("-PR")) {
							p.setGemstone(true);
							p.setEdelstein(Edelsteine.PREHNIT.getIndex());
						}
						if (serial.contains("-MG")) {
							p.setGemstone(true);
							p.setEdelstein(Edelsteine.MONDSTEIN_GRAU.getIndex());
						}
						if (serial.contains("-MW")) {
							p.setGemstone(true);
							p.setEdelstein(Edelsteine.MONDSTEIN_WEISS.getIndex());
						}
						if (serial.contains("-MS")) {
							p.setGemstone(true);
							p.setEdelstein(Edelsteine.MONDSTEIN_SCHWARZ.getIndex());
						}
						if (serial.endsWith("-C")) {
							p.setGemstone(true);
							p.setEdelstein(Edelsteine.CHALCEDON.getIndex());
						}
						if (p.isGemstone()) {
							serial12 = serial12 + p.getEdelstein();
						}
					}
					// description
					if (j == 5) {
						if (p.getSize() != 0) {
							serial = serial + "-" + p.getSize();
							p.setSerialnumber(serial);
						} else {
							p.setSerialnumber(serial);
						}
						String desc = cell.getStringCellValue();
						if (desc == null || "".equals(desc) && previousProduct != null
								&& previousProduct.getDescription() != null
								&& !previousProduct.getDescription().isEmpty()) {
							if (p.isGemstone() && previousProduct.isGemstone()) {
								if (previousProduct.getSerialnumber().substring(0, 9).equals(serial12)) {
									p.setDescription(previousProduct.getDescription());
								}
							} else {
								if (previousProduct.getSerialnumber().substring(0, 6).equals(serial12)) {
									p.setDescription(previousProduct.getDescription());
								}
							}
						} else {
							p.setDescription(desc);
						}
					}
					// price
					if (j == 12) {
						p.setSellingPrice(cell.getNumericCellValue());
					}
				}
			}
		}
		serviceProdukt.persistProdukte(produkte);
	}

}
