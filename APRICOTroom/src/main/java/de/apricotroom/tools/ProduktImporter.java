package de.apricotroom.tools;

import java.io.FileInputStream;
import java.io.IOException;
import java.security.cert.PKIXRevocationChecker.Option;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.stream.Stream;

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
import de.apricotroom.bo.Materialien;
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

	public void readFile(String filename) {
		try {
			FileInputStream excelFile = new FileInputStream(filename);
			Workbook workbook = new HSSFWorkbook(excelFile);
			List<Produkt> list = serviceProdukt.getProdukte();
			this.readInventar(workbook);
			workbook.close();
		} catch (IOException e) {
			e.printStackTrace();
		}

	}

	private void readInventar(Workbook workbook) {

		Produkt p = null;
		String serial1 = "";
		String serial2 = "";
		List<Produkt> produkte = new ArrayList<>();
		Sheet sheetInventar = workbook.getSheetAt(0);
		for (int i = 1; i <= sheetInventar.getLastRowNum(); i++) {
			Row row = sheetInventar.getRow(i);
			p = new Produkt();
			p.setGenerated(true);
			produkte.add(p);
			for (int j = 0; j < row.getLastCellNum(); j++) {
				Cell cell = row.getCell(j);
				if (cell != null) {
					// serialPart1
					if (j == 0) {
						String cell0Value = cell.getStringCellValue();
						serial1 = this.readKategorie(cell0Value);
					}
					if (j == 1) {
						String cell1Value = cell.getStringCellValue();
						serial2 = this.readMaterial(cell1Value);
					}
				}
			}
		}
		serviceProdukt.persistProdukte(produkte);

	}

	private String readKategorie(String cell1Value) {
		String result = null;
		if (cell1Value != null && !cell1Value.isEmpty()) {
			Optional<Kategorien> o = Arrays.asList(Kategorien.values()).stream()
					.filter(e -> e.equals(Kategorien.of(cell1Value))).findFirst();
			Kategorien k = o.get();
			result = k.getIndex();

		}
		return result;

	}

	private String readMaterial(String cell2Value) {
		String result = null;
		if (cell2Value != null && !cell2Value.isEmpty()) {
			Optional<Materialien> o = Arrays.asList(Materialien.values()).stream()
					.filter(e -> e.equals(Materialien.of(cell2Value))).findFirst();
			Materialien k = o.get();
			result = k.getIndex();
		}
		return result;
	}

}
