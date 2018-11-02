package de.apricotroom.tools;

import java.io.ByteArrayInputStream;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Cell;
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

public class ProduktImporter {
	private JPAServiceProdukt serviceProdukt = new JPAServiceProdukt();
	private JPAServiceLieferant serviceLieferant = new JPAServiceLieferant();

	public static void main(String[] args) {
		ProduktImporter pg = new ProduktImporter();
		pg.readFile("/Users/jurgenhochkoppler/git/APRICOTroom/src/main/java/de/apricotroom/tools/produktImport.xls");
	}

	public List<Produkt> readFile(byte[] b) {
		List<Produkt> newProdukte = new ArrayList<>();
		try {
			ByteArrayInputStream excelFile = new ByteArrayInputStream(b);
			Workbook workbook = new HSSFWorkbook(excelFile);
			return this.readFile(workbook, newProdukte);
		} catch (IOException e) {
			e.printStackTrace();
		}
		return newProdukte;
	}

	public List<Produkt> readFile(String filename) {
		FileInputStream excelFile = null;
		List<Produkt> newProdukte = new ArrayList<>();
		try {
			excelFile = new FileInputStream(filename);
			Workbook workbook = new HSSFWorkbook(excelFile);
			return this.readFile(workbook, newProdukte);
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			if(excelFile != null) {
				try {
					excelFile.close();
				} catch (IOException ex) {
					ex.printStackTrace();
				}
			}
		}
		return newProdukte;
	}

	public java.util.Date readDate(byte[] b) {
		java.util.Date d = null;
		try {
			ByteArrayInputStream excelFile = new ByteArrayInputStream(b);
			Workbook workbook = new HSSFWorkbook(excelFile);
			Sheet sheetDate = workbook.getSheetAt(0);
			Row row = sheetDate.getRow(0);
			Cell c = row.getCell(3);
			if (c != null) {
				d = c.getDateCellValue();
			}
			workbook.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return d;
	}

	private List<Produkt> readFile(Workbook workbook, List<Produkt> newProdukte) throws IOException {
		List<Produkt> list = serviceProdukt.getProdukte();
		int initialCount = list.size();
		int count = initialCount;
		List<Lieferant> lieferanten = serviceLieferant.getLieferanten();
		this.readOhrringe(workbook, count, lieferanten, newProdukte);
		count = initialCount + newProdukte.size();
		this.readKetten(workbook, count, lieferanten, newProdukte);
		count = initialCount + newProdukte.size();
		this.readArmbaender(workbook, count, lieferanten, newProdukte);
		count = initialCount + newProdukte.size();
		this.readRinge(workbook, count, lieferanten, newProdukte);
		count = initialCount + newProdukte.size();
		workbook.close();
		return newProdukte;

	}

	private void readOhrringe(Workbook workbook, int count, List<Lieferant> lieferanten, List<Produkt> newProdukte) {
		this.readSheet(workbook, count, lieferanten, 0, newProdukte, Kategorien.OHRRING.getValue());
	}

	private void readKetten(Workbook workbook, int count, List<Lieferant> lieferanten, List<Produkt> newProdukte) {
		this.readSheet(workbook, count, lieferanten, 1, newProdukte, Kategorien.HALSKETTE.getValue());
	}

	private void readArmbaender(Workbook workbook, int count, List<Lieferant> lieferanten, List<Produkt> newProdukte) {
		this.readSheet(workbook, count, lieferanten, 2, newProdukte, Kategorien.ARMBAND.getValue());
	}

	private void readRinge(Workbook workbook, int count, List<Lieferant> lieferanten, List<Produkt> newProdukte) {
		this.readSheet(workbook, count, lieferanten, 3, newProdukte, Kategorien.RING.getValue());
	}

	private void readSheet(Workbook workbook, int count, List<Lieferant> lieferanten, int sheet,
			List<Produkt> newProdukte, String kategorie) {

		Produkt p = null;
		String stringCellValue = null;
		Sheet sheetOhrringe = workbook.getSheetAt(sheet);
		for (int i = 1; i <= sheetOhrringe.getLastRowNum(); i++) {
			Row row = sheetOhrringe.getRow(i);
			p = new Produkt();
			p.setImported(true);
			p.setKategorie(kategorie);
			for (int j = 0; j < row.getLastCellNum(); j++) {
				Cell cell = row.getCell(j);
				if (cell != null) {
					// serialPart1
					if (j == 0) {
						stringCellValue = cell.getStringCellValue();
						p.setMaterial(stringCellValue);
					}
					if (j == 1) {
						stringCellValue = cell.getStringCellValue();
						p.setDescription(stringCellValue);
					}
					if (j == 2 && sheet == 3) {
						int cell3Value = (int) cell.getNumericCellValue();
						p.setSize(cell3Value);
					}
					if (j == 3) {
						stringCellValue = cell.getStringCellValue();
						p.setFarbe(this.readFarbe(stringCellValue));
					}
					if (j == 4) {
						stringCellValue = cell.getStringCellValue();
						p.setGemstone(true);
						p.setEdelstein(this.readEdelstein(stringCellValue));
					}
					if (j == 5) {
						double cellValue = (double) cell.getNumericCellValue();
						p.setSellingPrice(cellValue);
					}
					if (j == 6) {
						stringCellValue = cell.getStringCellValue();
						Lieferant l = this.readLieferant(lieferanten, stringCellValue);
						p.setLieferant(l);
					}
					p.buildSerialnumber(count);
				}
			}
			newProdukte.add(p);
			count++;
		}
	}

	private Lieferant readLieferant(List<Lieferant> lieferanten, String cellValue) {
		Lieferant lief = lieferanten.stream().filter(l -> l.getName().equalsIgnoreCase(cellValue)).findFirst().get();
		return lief;
	}

	private String readFarbe(String cell2Value) {
		String result = null;
		if (cell2Value != null && !cell2Value.isEmpty()) {
			Optional<Farben> o = Arrays.asList(Farben.values()).stream().filter(e -> e.equals(Farben.of(cell2Value)))
					.findFirst();
			Farben k = o.get();
			result = k.getIndex();
		}
		return result;
	}

	private String readEdelstein(String cell2Value) {
		String result = null;
		if (cell2Value != null && !cell2Value.isEmpty()) {
			Optional<Edelsteine> o = Arrays.asList(Edelsteine.values()).stream()
					.filter(e -> e.equals(Edelsteine.ofValue(cell2Value))).findFirst();
			Edelsteine e = o.get();
			result = e.getIndex();
		}
		return result;
	}
}
