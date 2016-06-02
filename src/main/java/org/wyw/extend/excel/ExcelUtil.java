package org.wyw.extend.excel;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;


import jxl.Cell;
import jxl.CellType;
import jxl.Sheet;
import jxl.Workbook;
import jxl.format.Border;
import jxl.format.BorderLineStyle;
import jxl.format.CellFormat;
import jxl.format.Colour;
import jxl.read.biff.BiffException;
import jxl.write.Formula;
import jxl.write.Label;
import jxl.write.Number;
import jxl.write.NumberFormat;
import jxl.write.WritableCell;
import jxl.write.WritableCellFormat;
import jxl.write.WritableFont;
import jxl.write.WritableHyperlink;
import jxl.write.WritableImage;
import jxl.write.WritableSheet;
import jxl.write.WritableWorkbook;
import jxl.write.WriteException;
import jxl.write.biff.RowsExceededException;

public class ExcelUtil {

	/**
	 * 读取Excel文件的内容
	 * @param file 待读取的文件
	 * @return
	 */
	public static String readExcel(File file) {
		StringBuffer sb = new StringBuffer();

		Workbook wb = null;
		try {
			// 构造Workbook（工作薄）对象
			wb = Workbook.getWorkbook(file);
		} catch (BiffException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}

		if (wb == null)
			return null;

		// 获得了Workbook对象之后，就可以通过它得到Sheet（工作表）对象了
		Sheet[] sheet = wb.getSheets();

		if (sheet != null && sheet.length > 0) {
			// 对每个工作表进行循环
			for (int i = 0; i < sheet.length; i++) {
				// 得到当前工作表的行数
				int rowNum = sheet[i].getRows();
				for (int j = 0; j < rowNum; j++) {
					// 得到当前行的所有单元格
					Cell[] cells = sheet[i].getRow(j);
					if (cells != null && cells.length > 0) {
						// 对每个单元格进行循环
						for (int k = 0; k < cells.length; k++) {
							// 读取当前单元格的值
							String cellValue = cells[k].getContents();
							sb.append(cellValue + "\t");
						}
					}
					sb.append("\r\n");
				}
				sb.append("\r\n");
			}
		}
		// 最后关闭资源，释放内存
		wb.close();
		return sb.toString();
	}

	/**
	 * 生成一个Excel文件
	 * @param fileName 要生成的Excel文件名
	 */
	public static void writeExcel(String fileName) {
		WritableWorkbook wwb = null;
		try {
			// 首先要使用Workbook类的工厂方法创建一个可写入的工作薄(Workbook)对象
			wwb = Workbook.createWorkbook(new File(fileName));
		} catch (IOException e) {
			e.printStackTrace();
		}
		if (wwb != null) {
			// 创建一个可写入的工作表
			// Workbook的createSheet方法有两个参数，第一个是工作表的名称，第二个是工作表在工作薄中的位置
			WritableSheet ws = wwb.createSheet("sheet1", 0);
			// 下面开始添加单元格
			for (int i = 0; i < 10; i++) {
				for (int j = 0; j < 5; j++) {
					// 这里需要注意的是，在Excel中，第一个参数表示列，第二个表示行
					Label labelC = new Label(j, i, "这是第" + (i + 1) + "行，第" + (j + 1) + "列");
					try {
						// 将生成的单元格添加到工作表中
						ws.addCell(labelC);
					} catch (RowsExceededException e) {
						e.printStackTrace();
					} catch (WriteException e) {
						e.printStackTrace();
					}

				}
			}
			try {
				// 从内存中写入文件中
				wwb.write();
				// 关闭资源，释放内存
				wwb.close();
			} catch (IOException e) {
				e.printStackTrace();
			} catch (WriteException e) {
				e.printStackTrace();
			}
		}
	}

	/**
	 * 搜索某一个文件中是否包含某个关键字
	 * @param file 待搜索的文件
	 * @param keyWord  要搜索的关键字
	 * @return
	 */
	public static boolean searchKeyWord(File file, String keyWord) {
		boolean res = false;
		Workbook wb = null;
		try {
			// 构造Workbook（工作薄）对象
			wb = Workbook.getWorkbook(file);
		} catch (BiffException e) {
			return res;
		} catch (IOException e) {
			return res;
		}
		if (wb == null)
			return res;

		// 获得了Workbook对象之后，就可以通过它得到Sheet（工作表）对象了
		Sheet[] sheet = wb.getSheets();
		boolean breakSheet = false;
		if (sheet != null && sheet.length > 0) {
			// 对每个工作表进行循环
			for (int i = 0; i < sheet.length; i++) {
				if (breakSheet)
					break;

				// 得到当前工作表的行数
				int rowNum = sheet[i].getRows();

				boolean breakRow = false;

				for (int j = 0; j < rowNum; j++) {
					if (breakRow)
						break;
					// 得到当前行的所有单元格
					Cell[] cells = sheet[i].getRow(j);
					if (cells != null && cells.length > 0) {
						boolean breakCell = false;
						// 对每个单元格进行循环
						for (int k = 0; k < cells.length; k++) {
							if (breakCell)
								break;
							// 读取当前单元格的值
							String cellValue = cells[k].getContents();
							if (cellValue == null)
								continue;
							if (cellValue.contains(keyWord)) {
								res = true;
								breakCell = true;
								breakRow = true;
								breakSheet = true;
							}
						}
					}
				}
			}
		}
		// 最后关闭资源，释放内存
		wb.close();

		return res;
	}

	/**
	 * 往Excel中插入图片
	 * @param dataSheet 待插入的工作表
	 * @param col 图片从该列开始
	 * @param row 图片从该行开始
	 * @param width 图片所占的列数
	 * @param height 图片所占的行数
	 * @param imgFile 要插入的图片文件
	 */
	public static void insertImg(WritableSheet dataSheet, int col, int row, int width, int height, File imgFile) {
		WritableImage img = new WritableImage(col, row, width, height, imgFile);
		dataSheet.addImage(img);
	}

	/**
	 * 往Excel中插入图片
	 * @param dataSheet 待插入的工作表
	 * @param col 图片从该列开始
	 * @param row 图片从该行开始
	 * @param width 图片所占的列数
	 * @param height 图片所占的行数
	 * @param b 字节流
	 */
	public static void insertImg2(WritableSheet dataSheet, int col, int row, double width, double height, byte[] b) {
		WritableImage img = new WritableImage(col, row, width, height, b);
		dataSheet.addImage(img);
	}

	/**
	 * 复制旧excel到新工作区
	 * @param inFile 输入文件目录
	 * @param outFile 输出文件目录
	 * @throws BiffException
	 * @throws IOException
	 */
	public static WritableWorkbook createWorkbookByOld(String inFile, String outFile)
			throws BiffException, IOException {
		File file1 = new File(inFile);
		File file2 = new File(outFile);
		Workbook wb = Workbook.getWorkbook(file1);
		WritableWorkbook wwb = Workbook.createWorkbook(file2, wb);
		if (wwb == null)
			return null;
		return wwb;
	}

	public static WritableSheet getWritableSheetByIndex(WritableWorkbook wwb, int index) {
		if (wwb == null)
			return null;
		return wwb.getSheet(index);
	}

	/**
	 * 根据名称获取 sheet
	 * @param wwb
	 * @param columnName sheet的名称
	 * @return
	 */
	public static WritableSheet getWritableSheetByIndex2(WritableWorkbook wwb, String columnName) {
		if (wwb == null)
			return null;
		return wwb.getSheet(columnName);
	}

	/**
	 * 根据名称获取 sheet
	 * 
	 * @param wwb
	 * @param SheetName
	 * @return
	 */
	public static WritableSheet getWritableSheetBySheetName(WritableWorkbook wwb, String SheetName) {
		if (wwb == null)
			return null;
		return wwb.getSheet(SheetName);
	}

	public static void setCell(WritableSheet dataSheet, WritableCell cell, String Value, CellFormat format)
			throws RowsExceededException, WriteException {
		// 判断单元格的类型, 做出相应的转化
		if (cell.getType() == CellType.LABEL) {
			Label lbl = (Label) cell;
			lbl.setString(Value);
			if (format != null)
				lbl.setCellFormat(format);
		} else if (cell.getType() == CellType.NUMBER) {
			// 数字单元格修改
			Value = "".equals(Value.trim()) ? "0" : Value;
			Number n1 = (Number) cell;
			n1.setValue(Double.parseDouble(Value));
			if (format != null)
				n1.setCellFormat(format);
		} else if (cell.getType() == CellType.EMPTY) {
			try {
				Number n1 = new Number(cell.getColumn(), cell.getRow(), Double.parseDouble(Value));
				if (format != null)
					n1.setCellFormat(format);
				dataSheet.addCell(n1);
			} catch (Exception e) {
				Label label = new Label(cell.getColumn(), cell.getRow(), Value);
				if (format != null)
					label.setCellFormat(format);
				dataSheet.addCell(label);
			}
		}
	}

	/**
	 * 修改单元格的值
	 * @param dataSheet 工作表
	 * @param col cellName : 单元格名称
	 * @param str String : 单元格内容
	 * @param format CellFormat : 单元格的样式
	 * @throws RowsExceededException
	 * @throws WriteException
	 */
	public static void modiStrCell(WritableSheet dataSheet, String cellName, String str, CellFormat format)
			throws RowsExceededException, WriteException {
		// 获得单元格对象
		WritableCell cell = dataSheet.getWritableCell(cellName);
		setCell(dataSheet, cell, str, format);
	}

	/**
	 * 修改单元格的值
	 * @param dataSheet 工作表
	 * @param col int : 列
	 * @param row int : 行
	 * @param str String : 单元格内容
	 * @param format CellFormat : 单元格的样式
	 * @throws RowsExceededException
	 * @throws WriteException
	 */
	public static void modiStrCell2(WritableSheet dataSheet, int col, int row, String str, CellFormat format)
			throws RowsExceededException, WriteException {
		// 获得单元格对象
		WritableCell cell = dataSheet.getWritableCell(col, row);
		setCell(dataSheet, cell, str, format);
	}

	// 将单元格拷贝到新单元格中
	public static void copyToCell(WritableSheet sheet, String srcCell, int destCol, int destRow) {
		WritableCell cell = sheet.getWritableCell(srcCell);
		WritableCell backCellD = cell.copyTo(destCol, destRow);
		try {
			sheet.addCell(backCellD);
		} catch (RowsExceededException e) {
			e.printStackTrace();
		} catch (WriteException e) {
			e.printStackTrace();
		}
	}

	/**
	 * 修改单元格
	 * @param dataSheet
	 * @param cellName
	 * @param sr
	 * @param format
	 * @throws RowsExceededException
	 * @throws WriteException
	 */
	public static void modiStrCell3(WritableSheet dataSheet, String cellName, double sr, CellFormat format)
			throws RowsExceededException, WriteException {
		String str = Double.toString(sr);
		str = str.substring(0, str.length() - 2);
		// 获得单元格对象
		WritableCell cell = dataSheet.getWritableCell(cellName);
		setCell(dataSheet, cell, str, format);
	}

	/**
	 * @param c 单元格的背景色(返回的格式，都是已经加了边框的)
	 * @throws WriteException
	 */
	public static WritableCellFormat getCellFormat(Colour c) throws WriteException {
		WritableCellFormat wcf = new WritableCellFormat();
		wcf.setBackground(c);
		wcf.setBorder(Border.ALL, BorderLineStyle.MEDIUM);
		return wcf;
	}

	/**
	 * (返回的格式，字体是12号宋体，并且有2位小数)
	 * @param c  单元格的背景色
	 * @param b  是否以百分号的形式返回
	 * @throws WriteException
	 */
	public static WritableCellFormat getCellFormatWithFont(Colour c, String b) throws WriteException {
		NumberFormat nf = null;
		if ("%".equals(b))
			nf = new NumberFormat("0.00%");
		else
			nf = new NumberFormat("0.00");
		WritableCellFormat wcf = new WritableCellFormat(nf);
		wcf.setBackground(c);
		wcf.setBorder(Border.ALL, BorderLineStyle.THIN);
		WritableFont wf = new WritableFont(WritableFont.createFont("宋体"), 10, WritableFont.NO_BOLD, false);
		wcf.setFont(wf);
		return wcf;
	}

	/**
	 * 读取一列信息
	 * @param dataSheet WritableSheet : 工作表
	 * @param cellName : 列名
	 * @return
	 */
	public static String readData(WritableSheet dataSheet, String cellName) {
		// 通过WritableSheet方法的getCell方法选择单元格位置（两个参数都从0开始）
		Cell c = dataSheet.getCell(cellName);
		// 通过Cell的getContents方法把单元格中的信息以字符的形式读取出来
		String str = c.getContents();
		return str;
	}

	public static String readData2(WritableSheet dataSheet, int col, int row) {
		// 通过WritableSheet方法的getCell方法选择单元格位置（两个参数都从0开始）
		Cell c = dataSheet.getCell(col, row);
		// 通过Cell的getContents方法把单元格中的信息以字符的形式读取出来
		String str = c.getContents();
		return str;
	}

	/**
	 * 给单元格添加边框
	 * 
	 * @param dataSheet WritableSheet : 工作表
	 * @param start int : 要添加多少列边框
	 * @param start_draw int : 从哪个单元格开始添加
	 * @param end int : 要添加多少行边框
	 * @param end_draw int : 到哪个单元结束添加
	 * @return
	 */
	public static void addBorder(int start, int start_draw, int end, int end_draw, WritableSheet dataSheet) {
		WritableCellFormat wcf = new WritableCellFormat();
		try {
			wcf.setBorder(jxl.format.Border.ALL, jxl.format.BorderLineStyle.THIN);
			for (int i = start_draw; i <= start; i++) {
				for (int j = end_draw; j <= end; j++) {
					// 设置对齐方式
					// wcf.setAlignment(Alignment.CENTRE);
					// wcf.setVerticalAlignment(VerticalAlignment.CENTRE);
					// 设置边框样式
					Label labelCFC = new Label(i, j, null, wcf);
					dataSheet.addCell(labelCFC);
				}
			}
		} catch (WriteException e) {
			e.printStackTrace();
		}
	}

	/**
	 * 读取一行信息
	 * @param dataSheet WritableSheet : 工作表
	 * @param cellName : 列名
	 * @return
	 */
	public static List readRowData(WritableSheet dataSheet, int k) {
		// 通过WritableSheet方法的getCell方法选择单元格位置（两个参数都从0开始）
		Cell[] cells = dataSheet.getRow(k);
		String str = "";
		List list = null;
		// 通过Cell的getContents方法把单元格中的信息以字符的形式读取出来
		if (cells != null && cells.length > 0) {
			list = new ArrayList();
			for (int h = 5; h < cells.length; h++) {
				str = cells[h].getContents();
				// System.out.println("str::"+str);
				list.add(str);
			}
		}
		return list;
	}

	/**
	 * 插入公式
	 * 
	 * @param sheet
	 * @param col
	 * @param row
	 * @param formula
	 * @param format
	 */
	public static void insertFormula(WritableSheet sheet, Integer col, Integer row, String formula,
			WritableCellFormat format) {
		try {
			Formula f = new Formula(col, row, formula);
			if (format != null)
				f.setCellFormat(format);
			sheet.addCell(f);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * 插入超联接
	 * @param sheet
	 * @param col
	 * @param row
	 * @param formula
	 * @param format
	 */
	public static void insertLink(WritableSheet sheet, WritableSheet sheet2, Integer col, Integer row, String colum,
			WritableCellFormat format) {
		try {
			WritableHyperlink link = new WritableHyperlink(col, row, colum, sheet2, 1, 1);
			sheet.addHyperlink(link);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * 插入超联接
	 * @param sheet
	 * @param col
	 * @param row
	 * @param formula
	 * @param format
	 */
	public static void insertLink(WritableSheet sheet, WritableSheet sheet2, Integer col, Integer row, String context,
			WritableCellFormat format, int destcol, int destrow, int lastdestcol, int lastdestrow) {
		try {
			WritableHyperlink link = new WritableHyperlink(col, row, col, row, context, sheet2, destcol, destrow,
					lastdestcol, lastdestrow);
			sheet.addHyperlink(link);

		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * 复制列
	 * @param sheet
	 * @param totalcol
	 * @param totalrow
	 */
	public static void copyColumn(WritableSheet sheet, int totalcol, int totalrow) {
		LinkedHashMap hm1 = new LinkedHashMap();
		for (int q = 2; q <= totalcol; q++) {
			WritableCell cell1 = sheet.getWritableCell("G" + q);
			hm1.put("G" + q, cell1);
		}
		char cp = 'H';
		for (int k = 6; k < totalrow; k++) {
			for (int p = 2; p < hm1.size() + 2; p++) {
				WritableCell cell = sheet.getWritableCell(cp + "" + p);
				WritableCell cell1 = (WritableCell) hm1.get("G" + p);
				WritableCell barkCell = cell1.copyTo(cell.getColumn(), cell.getRow());
				try {
					sheet.addCell(barkCell);
				} catch (RowsExceededException e) {
					e.printStackTrace();
				} catch (WriteException e) {
					e.printStackTrace();
				}
			}
			cp++;
		}
	}

	/**
	 * 复制列
	 * @param sheet -- excel Tab页
	 * @param sourceIndex -- 源列标识
	 * @param targetIndex -- 目标列标识
	 * @param rowCount -- 需要复制的行数
	 * @param startRow -- 起始复制行数
	 */
	public static void copyColumnByParams(WritableSheet sheet, String sourceIndex, String targetIndex, int rowCount,
			int startRow) {
		for (int i = startRow; i <= startRow + rowCount; i++) {
			WritableCell cell = sheet.getWritableCell(sourceIndex + i);
			WritableCell targetCell = sheet.getWritableCell(targetIndex + i);
			WritableCell newCell = cell.copyTo(targetCell.getColumn(), targetCell.getRow());
			try {
				sheet.addCell(newCell);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}

	/**
	 * 此单元格复制存在问题，用copyRangeBo
	 * @param src
	 *            源sheet
	 * @param desc目的Sheet
	 * @param srcStRow
	 * @param srcStCol
	 * @param srcEndRow
	 * @param srcEndCol
	 * @param descStRow
	 * @param descStCol
	 * @param descEndRow
	 * @param descEndCol
	 * @throws Exception
	 */
	public static void copyRange(WritableSheet src, WritableSheet desc, int srcStRow, int srcStCol, int srcEndRow,
			int srcEndCol, int descStRow, int descStCol, int descEndRow, int descEndCol) throws Exception {
		if (srcEndRow - srcStRow != descEndRow - descStRow || srcEndCol - srcStCol != descEndCol - descStCol)
			throw new Exception("复制Excel区域的大小不一致");

		for (int i = srcStRow; i <= srcEndRow; i++) {
			for (int j = srcStCol; j <= srcEndCol; j++) {
				copyCell(src, desc, i, j, descStRow + (i - srcStCol), descStCol + (j - srcStCol));
			}
		}
	}

	// 单元格的范围复制
	public static void copyRangeBo(WritableSheet src, WritableSheet desc, int srcStRow, int srcStCol, int srcEndRow,
			int srcEndCol, int descStRow, int descStCol, int descEndRow, int descEndCol) throws Exception {
		if (srcEndRow - srcStRow != descEndRow - descStRow || srcEndCol - srcStCol != descEndCol - descStCol)
			throw new Exception("复制Excel区域的大小不一致");

		for (int i = srcStRow; i <= srcEndRow; i++) {
			for (int j = srcStCol; j <= srcEndCol; j++) {
				copyCell(src, desc, i, j, descStRow + (i - srcStRow), descStCol + (j - srcStCol));
			}
		}
	}

	/**
	 * 插入一行数据
	 * 
	 * @param sheet 工作表
	 * @param row 行号
	 * @param content 内容
	 * @param format 风格
	 */
	public static void insertRowData(WritableSheet sheet, Integer row, String[] dataArr, WritableCellFormat format) {
		try {
			Label label;
			for (int i = 0; i < dataArr.length; i++) {
				label = new Label(i, row, dataArr[i], format);
				sheet.addCell(label);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * 复制单元格
	 * 
	 * @param sheetIndex 工作薄
	 * @param rowIndex  行下标
	 * @param columnIndex 列下标
	 * @decRowIndex 目的行
	 * @decColIndex 目的列
	 * @return
	 * @throws WriteException
	 * @throws RowsExceededException
	 */
	public static void copyCell(WritableSheet src, WritableSheet desc, int rowIndex, int columnIndex, int decRowIndex,
			int decColIndex) throws RowsExceededException, WriteException {
		WritableCell srcCell = src.getWritableCell(columnIndex, rowIndex);
		WritableCell newCell = srcCell.copyTo(decColIndex, decRowIndex);
		desc.addCell(newCell);
	}


	/*
	 * 去除一列中数据重复的单元格，只保留第一个 去除的单元格必须是Label格式，Number格式会报异常 @param sheet excel
	 * Tab页 @param colName 去除重复数据的列 @param start 从哪一行开始 @param rowCount 总的行数
	 */
	public static void deleteSameData(WritableSheet sheet, String colName, int start, int rowCount) {
		for (int i = rowCount; i > start - 1; i--) {
			WritableCell cur = sheet.getWritableCell(colName + (i + 1));
			WritableCell next = sheet.getWritableCell(colName + i);
			if (cur.getContents().equals(next.getContents())) {
				try {
					Label lb = (Label) cur;
					lb.setString("");
				} catch (Exception e) {

				}
			}
		}
	}

	/*
	 * 合并数据重复的单元格,合并的判断必须由后面合并到前面,也就是Z~A合并,不能是A~Z合并,最多支持四列合并
	 * 去除的单元格必须是Label格式，Number格式会报异常 @param sheet excel Tab页 @param firstColName
	 * 用于判断合并重复数据的第一列 @param secondColName 用于判断合并重复数据的第一列 @param thirdColName
	 * 合并重复数据的列 @param start 从哪一行开始 @param rowCount 总的行数
	 */
	public static void mergeSameData(WritableSheet sheet, String firstColName, String secondColName,
			String thirdColName, int start, int rowCount) {
		int iflag = 1;
		for (int i = rowCount; i > start - 2; i--) {
			if (!"".equals(firstColName.trim()) && !"".equals(secondColName.trim())
					&& !"".equals(thirdColName.trim())) {
				WritableCell firstCur = sheet.getWritableCell(firstColName + (i + 1));
				WritableCell firstNext = sheet.getWritableCell(firstColName + i);
				WritableCell secondCur = sheet.getWritableCell(secondColName + (i + 1));
				WritableCell secondNext = sheet.getWritableCell(secondColName + i);
				WritableCell thirdCur = sheet.getWritableCell(thirdColName + (i + 1));
				WritableCell thirdNext = sheet.getWritableCell(thirdColName + i);

				String firstCurValue = firstCur.getContents() == null ? "" : firstCur.getContents().trim();
				String firstNextValue = firstNext.getContents() == null ? "" : firstNext.getContents().trim();
				String secondCurValue = secondCur.getContents() == null ? "" : secondCur.getContents().trim();
				String SecondNextValue = secondNext.getContents() == null ? "" : secondNext.getContents().trim();
				String thirdCurValue = thirdCur.getContents() == null ? "" : thirdCur.getContents().trim();
				String thirdNextValue = thirdNext.getContents() == null ? "" : thirdNext.getContents().trim();

				if (firstCurValue.equals(firstNextValue) && secondCurValue.equals(SecondNextValue)
						&& thirdCurValue.equals(thirdNextValue)) {
					iflag++;
				} else if (iflag > 1) {
					WritableCell bottom = sheet.getWritableCell(thirdColName + (i + iflag));
					try {
						sheet.mergeCells(thirdNext.getColumn(), thirdNext.getRow() + 1, bottom.getColumn(),
								bottom.getRow());
					} catch (Exception e) {
						System.out.println("合并单元格发生异常！");
						e.printStackTrace();
					}
					iflag = 1;
				}
			} else if ("".equals(firstColName.trim()) && !"".equals(secondColName.trim())
					&& !"".equals(thirdColName.trim())) {
				WritableCell secondCur = sheet.getWritableCell(secondColName + (i + 1));
				WritableCell secondNext = sheet.getWritableCell(secondColName + i);
				WritableCell thirdCur = sheet.getWritableCell(thirdColName + (i + 1));
				WritableCell thirdNext = sheet.getWritableCell(thirdColName + i);

				String secondCurValue = secondCur.getContents() == null ? "" : secondCur.getContents().trim();
				String SecondNextValue = secondNext.getContents() == null ? "" : secondNext.getContents().trim();
				String thirdCurValue = thirdCur.getContents() == null ? "" : thirdCur.getContents().trim();
				String thirdNextValue = thirdNext.getContents() == null ? "" : thirdNext.getContents().trim();

				if (secondCurValue.equals(SecondNextValue) && thirdCurValue.equals(thirdNextValue)) {
					iflag++;
				} else if (iflag > 1) {
					WritableCell bottom = sheet.getWritableCell(thirdColName + (i + iflag));
					try {
						sheet.mergeCells(thirdNext.getColumn(), thirdNext.getRow() + 1, bottom.getColumn(),
								bottom.getRow());
					} catch (Exception e) {
						System.out.println("合并单元格发生异常！");
						e.printStackTrace();
					}
					iflag = 1;
				}
			} else if ("".equals(firstColName.trim()) && "".equals(secondColName.trim())
					&& !"".equals(thirdColName.trim())) {
				WritableCell thirdCur = sheet.getWritableCell(thirdColName + (i + 1));
				WritableCell thirdNext = sheet.getWritableCell(thirdColName + i);

				String thirdCurValue = thirdCur.getContents() == null ? "" : thirdCur.getContents().trim();
				String thirdNextValue = thirdNext.getContents() == null ? "" : thirdNext.getContents().trim();

				if (thirdCurValue.equals(thirdNextValue)) {
					iflag++;
				} else if (iflag > 1) {
					WritableCell bottom = sheet.getWritableCell(thirdColName + (i + iflag));
					try {
						sheet.mergeCells(thirdNext.getColumn(), thirdNext.getRow() + 1, bottom.getColumn(),
								bottom.getRow());
					} catch (Exception e) {
						System.out.println("合并单元格发生异常！");
						e.printStackTrace();
					}
					iflag = 1;
				}
			}
		}
	}

	/*
	 * 合并数据重复的单元格（空值不可合并）,并求合计数 去除的单元格必须是Label格式，Number格式会报异常 @param sheet excel
	 * Tab页 @param colName 合并重复数据的列 @param start 从哪一行开始 @param rowCount 总的行数
	 */
	public static void subMergeSameData(WritableSheet sheet, String colName, int start, int rowCount, String colName2,
			String colName3, String colName4, String colName5, String colName6, String colName7, String colName8) {
		int iflag = 1;
		int FColInt = 0;
		int IColInt = 0;
		int JColInt = 0;
		int KColInt = 0;
		for (int i = rowCount; i > start; i = i - 2) {
			WritableCell col = sheet.getWritableCell(colName2 + i); // 合计
			WritableCell next = sheet.getWritableCell(colName + (i - 1)); // 获取最后一行的指定单元格的值
			WritableCell col2 = sheet.getWritableCell(colName2 + (i - 2)); // 合计
			WritableCell cur = sheet.getWritableCell(colName + (i - 3)); // 获取倒数二行的指定单元格的值
			WritableCell FCol = sheet.getWritableCell(colName3 + (i - 1));
			WritableCell ICol = sheet.getWritableCell(colName6 + (i - 1));
			WritableCell JCol = sheet.getWritableCell(colName7 + (i - 1));
			WritableCell KCol = sheet.getWritableCell(colName8 + (i - 1));

			String colValue = col.getContents() == null ? "" : col.getContents().trim();
			String nextValue = next.getContents() == null ? "" : next.getContents().trim();
			String colValue2 = col2.getContents() == null ? "" : col2.getContents().trim();
			String curValue = cur.getContents() == null ? "" : cur.getContents().trim();
			String FColValue = FCol.getContents() == null ? "" : FCol.getContents().trim();
			String IColValue = ICol.getContents() == null ? "" : ICol.getContents().trim();
			String JColValue = JCol.getContents() == null ? "" : JCol.getContents().trim();
			String KColValue = KCol.getContents() == null ? "" : KCol.getContents().trim();
			int FColCount = 0;
			int IColCount = 0;
			int JColCount = 0;
			int KColCount = 0;
			if (!"".equals(FColValue)) {
				FColCount = Integer.parseInt(FColValue);
			} else {
				FColValue = "0";
			}
			if (!"".equals(IColValue)) {
				IColCount = Integer.parseInt(IColValue);
			} else {
				IColValue = "0";
			}
			if (!"".equals(JColValue)) {
				JColCount = Integer.parseInt(JColValue);
			} else {
				JColValue = "0";
			}
			if (!"".equals(KColValue)) {
				KColCount = Integer.parseInt(KColValue);
			} else {
				KColValue = "0";
			}
			if ("<合计>".equals(colValue) && "<合计>".equals(colValue2) && (curValue.trim()).equals(nextValue.trim())) {
				FColInt += FColCount;
				IColInt += IColCount;
				JColInt += JColCount;
				KColInt += KColCount;
				iflag++;
			} else if (iflag > 1) {// 可以合并
				FColInt += FColCount;
				IColInt += IColCount;
				JColInt += JColCount;
				KColInt += KColCount;
				FColValue = Integer.toString(FColInt);
				IColValue = Integer.toString(IColInt);
				JColValue = Integer.toString(JColInt);
				KColValue = Integer.toString(KColInt);
				WritableCell bottom = sheet.getWritableCell(colName + (i + ((iflag - 1) * 2)));
				try {
					sheet.mergeCells(next.getColumn(), next.getRow(), bottom.getColumn(), bottom.getRow());
					sheet.insertRow(bottom.getRow() + 1);
					ExcelUtil.modiStrCell(sheet, colName + (bottom.getRow() + 2), "<合计>", null);
					ExcelUtil.modiStrCell(sheet, colName3 + (bottom.getRow() + 2), FColValue, null);
					ExcelUtil.modiStrCell(sheet, colName6 + (bottom.getRow() + 2), IColValue, null);
					ExcelUtil.modiStrCell(sheet, colName7 + (bottom.getRow() + 2), JColValue, null);
					ExcelUtil.modiStrCell(sheet, colName8 + (bottom.getRow() + 2), KColValue, null);
					ExcelUtil.insertFormula(sheet, 6, (bottom.getRow() + 1), "IF(I" + (bottom.getRow() + 2) + "=0, 0, I"
							+ (bottom.getRow() + 2) + "/J" + (bottom.getRow() + 2) + ")", null);
					ExcelUtil.insertFormula(sheet, 7, (bottom.getRow() + 1), "IF(K" + (bottom.getRow() + 2) + "=0, 0, K"
							+ (bottom.getRow() + 2) + "/I" + (bottom.getRow() + 2) + ")", null);
				} catch (Exception e) {
					System.out.println("合并单元格发生异常！");
					e.printStackTrace();
				}
				IColInt = 0;
				JColInt = 0;
				KColInt = 0;
				FColInt = 0;
				iflag = 1;
			} else {
				try {
					sheet.mergeCells(next.getColumn(), next.getRow(), next.getColumn(), next.getRow() + 1);
					sheet.insertRow(next.getRow() + 2);
					ExcelUtil.modiStrCell(sheet, colName + (next.getRow() + 3), "<合计>", null);
					ExcelUtil.modiStrCell(sheet, colName3 + (next.getRow() + 3), FColValue, null);
					ExcelUtil.modiStrCell(sheet, colName6 + (next.getRow() + 3), IColValue, null);
					ExcelUtil.modiStrCell(sheet, colName7 + (next.getRow() + 3), JColValue, null);
					ExcelUtil.modiStrCell(sheet, colName8 + (next.getRow() + 3), KColValue, null);
					ExcelUtil.insertFormula(sheet, 6, (next.getRow() + 2), "IF(I" + (next.getRow() + 3) + "=0, 0, I"
							+ (next.getRow() + 3) + "/J" + (next.getRow() + 3) + ")", null);
					ExcelUtil.insertFormula(sheet, 7, (next.getRow() + 2), "IF(K" + (next.getRow() + 3) + "=0, 0, K"
							+ (next.getRow() + 3) + "/I" + (next.getRow() + 3) + ")", null);
				} catch (Exception e) {
					System.out.println("合并单元格发生异常！");
					e.printStackTrace();
				}
			}
		}
	}

	/**
	 * 合并相同值的单元格（支持无限制列数的合并）
	 * @param ws 进行操作的工作表
	 * @param rowStart 要进行合并的单元格的开始行数（Excel中的行数）
	 * @param rowEnd 要进行合并的单元格的结束行数（Excel中的行数,一般是List.size() + rowStart - 1）
	 * @param mergeStart要进行合并的单元格的开始列数（Excel中的列数，第1列就为1）
	 * @param mergeEnd 要进行合并的单元格的结束列数（Excel中的列数）
	 */
	public static void mergeSameDataNewest(WritableSheet ws, int rowStart, int rowEnd, int mergeStart, int mergeEnd) {
		try {
			// 循环要合并的列
			for (int col = mergeStart - 1; col < mergeEnd; col++) {
				int rowFlag = 0;
				boolean match = true;
				for (int row = rowEnd - 1; row >= rowStart - 1; row--) {
					WritableCell cur = ws.getWritableCell(col, row);
					WritableCell pre = ws.getWritableCell(col, row - 1);

					if (!"".equals(cur.getContents()) && !"".equals(pre.getContents())
							&& cur.getContents().equals(pre.getContents()) && match) {
						// 循环要合并的单元格，的对应前几列是否值相同，如果之前有任一列的值不同，那就不可合并
						int mergeNum = 0;
						if (col == 0) // 第1列不用比较之前列是否相同
							rowFlag++;
						else {
							for (int mergeFlag = col - 1; mergeFlag >= 0; mergeFlag--) {// 循环列
								WritableCell curFrontMerge = ws.getWritableCell(mergeFlag, row);
								WritableCell preFrontMerge = ws.getWritableCell(mergeFlag, row - 1);
								if (curFrontMerge.getContents().trim().equals(preFrontMerge.getContents().trim())) {
									mergeNum++;
								} else {
									match = false;
									break;
								}
							}
							if (mergeNum == col)
								rowFlag++;
						}
					} else if (rowFlag > 0) {
						WritableCell bottom = ws.getWritableCell(col, cur.getRow() + rowFlag + (match ? 0 : 1));
						ws.mergeCells(pre.getColumn(), cur.getRow() + (match ? 0 : 1), bottom.getColumn(),
								bottom.getRow());// (纵向合并)
						row = match ? row : (row + 1);
						rowFlag = 0;
						match = true;
					}
					if (rowFlag == 0)
						match = true;
				}
			} // for
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * 获取单元格样式
	 */
	public static WritableCellFormat getCellformat(WritableSheet ws, String Cell) {
		CellFormat f = ws.getCell(Cell).getCellFormat();
		WritableCellFormat format = new WritableCellFormat(f);
		return format;
	}
	
	public static void main(String[] args) throws BiffException, IOException, RowsExceededException, WriteException {
		String s = "B5";
		String s2 = "B5";
		String inFile = "D:\\订单明细.xls";
		String outFile = "D:\\test2.xls";
		WritableWorkbook wwb = createWorkbookByOld(inFile, outFile);
		WritableSheet ws = getWritableSheetByIndex(wwb, 0);
		WritableSheet ws2 = getWritableSheetByIndex(wwb, 1);
		modiStrCell2(ws, 0, 0, "22", null);

		try {
			System.out.println("now");
			ExcelUtil.copyCell(ws, ws2, 0, 0, 5, 5);
		} catch (Exception e) {
			e.printStackTrace();
		}
		Runtime rt = Runtime.getRuntime();
		wwb.write();
		wwb.close();
		rt.exec("C:\\Program Files\\Microsoft Office\\OFFICE11\\EXCEL.EXE  D:\\test2.xls");
	}
}