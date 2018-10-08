package de.apricotroom.tools;

import java.io.ByteArrayInputStream;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellType;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;

import de.apricotroom.bo.Auswertung;
import de.apricotroom.bo.Produkt;
import de.apricotroom.pers.JPAServiceProdukt;

public class ProduktLister {
	private JPAServiceProdukt serviceProdukt = new JPAServiceProdukt();

	public static void main(String[] args) {
		ProduktLister pg = new ProduktLister();

	}

	public Map<Produkt, Integer> readFile(Auswertung a) {
		Map<Produkt, Integer> result = new HashMap<>();
		ByteArrayInputStream bais = new ByteArrayInputStream(a.getData());
		this.readFile(bais, result);
		printResult(result);
		return result;
	}

	public Map<Produkt, Integer> readFiles(List<Auswertung> list) {
		Map<Produkt, Integer> result = new HashMap<>();
		Iterator<Auswertung> it = list.iterator();
		while (it.hasNext()) {
			ByteArrayInputStream bais = new ByteArrayInputStream(it.next().getData());
			this.readFile(bais, result);
		}
		printResult(result);
		return result;
	}

	public Map<Produkt, Integer> readFile(InputStream fis, Map<Produkt, Integer> map) {
		Map<Produkt, Integer> result = new HashMap<>();
		try {
			Workbook workbook = new HSSFWorkbook(fis);
			workbook.close();
			result = this.readList(workbook, map);
		} catch (IOException e) {
			e.printStackTrace();
		}
		printResult(result);
		return result;
	}

	public Map<Produkt, Integer> readFile(String filename) {
		Map<Produkt, Integer> result = new HashMap<>();
		try {
			FileInputStream excelFile = new FileInputStream(filename);
			Workbook workbook = new HSSFWorkbook(excelFile);
			workbook.close();
			result = this.readList(workbook, result);
		} catch (IOException e) {
			e.printStackTrace();
		}
		printResult(result);
		return result;
	}

	public void printResult(Map<Produkt, Integer> map) {
		int total = 0;
		int pCount = 0;
		Iterator<Produkt> it = map.keySet().iterator();
		while (it.hasNext()) {
			Produkt p = it.next();
			pCount = map.get(p);
			total = total + pCount;
			System.out.println("Produkt " + p.getSerialnumber() + " " + p.getDescription() + " wurde " + pCount
					+ " mal verkauft!");
		}
		System.out.println("Es wurden insgesamt " + total + " Produkte verkauft");

	}

	private Map<Produkt, Integer> readList(Workbook workbook, Map<Produkt, Integer> map) {
		Produkt p = null;
		String serial = "";
		Integer count = 1;
		Sheet sheetInventar = workbook.getSheetAt(0);
		for (int i = 0; i < sheetInventar.getLastRowNum() + 1; i++) {
			count = 1;
			Row row = sheetInventar.getRow(i);
			for (int j = 0; j < row.getLastCellNum(); j++) {
				Cell cell = row.getCell(j);
				if (cell != null) {
					// serialPart1
					if (j == 0) {
						cell.setCellType(CellType.STRING);
						String cell1Value = (String) cell.getStringCellValue();
						serial = cell1Value;
					}
					// serialPart2
					if (j == 1) {
						count = (int) cell.getNumericCellValue();
					}
					if (row.getLastCellNum() == 1 && j == 0 || row.getLastCellNum() == 2 && j == 1) {
						p = serviceProdukt.getProduktBySerial(serial);
						if (map.containsKey(p)) {
							Integer integer = map.get(p).intValue() + count;
							map.put(p, integer);

						} else {
							map.put(p, count);
						}
					}
				}
			}
		}
		return map;
	}
}
