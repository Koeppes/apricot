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

public class LieferantenGenerator {
	private JPAServiceLieferant serviceLiefernt = new JPAServiceLieferant();

	public static void main(String[] args) {
		LieferantenGenerator pg = new LieferantenGenerator();
		pg.readFile(
				"/Users/jurgenhochkoppler/Downloads/BootsFaces-Examples-master/Combobox/src/main/java/de/apricotroom/tools/masterlisteCopy.xls");
	}

	public void readFile(String filename) {
		try {
			FileInputStream excelFile = new FileInputStream(filename);
			Workbook workbook = new HSSFWorkbook(excelFile);
			List<Lieferant> products = serviceLiefernt.getLieferanten();
			serviceLiefernt.removeAll(products);
			this.readLieferanten(workbook);
			workbook.close();
		} catch (IOException e) {
			e.printStackTrace();
		}

	}

	
	private void readLieferanten(Workbook workbook) {
		Lieferant l = null;
		List<Lieferant> lieferanten = new ArrayList<>();
		Sheet sheetLieferanten = workbook.getSheetAt(7);
		for (int i = 1; i < sheetLieferanten.getLastRowNum(); i++) {
			Row row = sheetLieferanten.getRow(i);
			l = new Lieferant();
			lieferanten.add(l);
			for (int j = 0; j < row.getLastCellNum(); j++) {
				Cell cell = row.getCell(j);
				if (cell != null) {
					if (j == 0) {
						int nr = (int) cell.getNumericCellValue();
						l.setNr(nr);
					}
					if (j == 1) {
						cell.setCellType(CellType.STRING);
						String name = cell.getStringCellValue();
						l.setName(name);
					}
				}
			}
		}
		serviceLiefernt.persistLieferanten(lieferanten);
	}

}
