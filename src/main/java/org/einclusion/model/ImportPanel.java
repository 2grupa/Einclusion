package org.einclusion.model;


import java.awt.Desktop;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Iterator;

import javax.swing.Action;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.border.EmptyBorder;
import javax.swing.filechooser.FileNameExtensionFilter;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import javax.swing.ScrollPaneConstants;

public class ImportPanel extends JPanel implements ActionListener {
	private static final long serialVersionUID = 1L;

	public static final String FILE_TYPE = "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet";
	JFileChooser fileChooser;
	FileNameExtensionFilter filter; // filter for jfilechooser
	JButton chooseFile, openFile, writeToDatabase, updateDatabase, exampleFile;
	JScrollPane scrollPane; // scrollpane for log
	JTextArea log; // textarea for log (what the application is doing)
	// JDBC driver name and database URL
	static final String JDBC_DRIVER = "org.h2.Driver";
	static final String DB_URL = "jdbc:h2:data/Studenti";
	// Database credentials
	static final String USER = "sa";
	static final String PASS = "";
	static final String DB_TABLE_NAME = "STUDENTI";
	static final String UNIQUE_ID = "NUMURS";
	static final String[] Doubles = { "SWL", "SAL", "ELM", "IWS", "ELE",
			"PUOU", "DS", "M2", "OU", "PU" };

	Statement stmt = null;
	Connection conn = null;

	public ImportPanel() {

		this.setLayout(null);

		fileChooser = new JFileChooser(); // creates a new JfileChooser
		fileChooser.setDialogTitle("Choose a file"); // sets jfilehooser name
		fileChooser.setPreferredSize(new Dimension(600, 500)); // sets
																// jfilechooser
																// size
		filter = new FileNameExtensionFilter("xlsx files", "xlsx"); // creates a
																	// new
																	// filter
		fileChooser.addChoosableFileFilter(filter); // adds filter to dropdown
		fileChooser.setFileFilter(filter); // sets filter as default
		try {
			File defaultDirectory = new File(
					new File(System.getProperty("user.home")
							+ System.getProperty("file.separator"))
							.getCanonicalPath()); // creates new directory path
			fileChooser.setCurrentDirectory(defaultDirectory); // sets
																// jfilechooser
																// starting
																// directory
		} catch (IOException e) {
			System.out
					.println("Exeption while setting directory for JfileChooser\n"
							+ e.getMessage());
		}
		fileChooser.setVisible(false); // fileChooser is not visible
		this.add(fileChooser); // adds filechooser to jpanel

		chooseFile = new JButton("Choose a file"); // creates a new button for
													// choosing file
		chooseFile.setFont(new Font("Arial", Font.BOLD, 12)); // sets button
																// font
		chooseFile.addActionListener(this); // adds actionlistener to button
		chooseFile.setToolTipText("Find a valid xlsx file"); // adds tooltip to
																// button
		chooseFile.setBounds(50, 10, 190, 30); // set location and size of
												// button
		this.add(chooseFile); // add button to jpanel

		JPanel panel = new JPanel(); // creates a new jpanel that will contain
										// log
		panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS)); // sets
																	// top-down
																	// box
																	// layout to
																	// jpanel
		log = new JTextArea("No file selected"); // creates new jtextarea
		log.setFont(new Font("Arial", Font.PLAIN, 12)); // sets font for
														// jtextarea
		log.setEditable(false); // jtextarea is not editable
		log.setOpaque(false); // background of jtextarea will be opaque
		log.setAlignmentX(JTextArea.LEFT_ALIGNMENT); // text will be aligned to
														// the left
		panel.setBorder(new EmptyBorder(10, 10, 10, 10)); // creates an empty
															// border for
															// spacing
		panel.add(log); // adds jtextara to jpanel

		scrollPane = new JScrollPane(panel,
				JScrollPane.VERTICAL_SCROLLBAR_ALWAYS,
				ScrollPaneConstants.HORIZONTAL_SCROLLBAR_ALWAYS); // create a
																	// scrollpane
																	// with
																	// visible
																	// scrollbars
		scrollPane.setAlignmentX(JScrollPane.LEFT_ALIGNMENT); // sets left
																// aligment to
																// components
		scrollPane.setBounds(370, 11, 360, 255); // sets location and size of
													// jscrollpane
		this.add(scrollPane); // adds jscrollpane to jpanel

		openFile = new JButton("Open created file"); // creates a nw button for
														// opening a file
		openFile.setToolTipText("Opens the created .csv file with the default program for this file type"); // sets
																											// tooltip
																											// for
																											// jbutton
		openFile.setFont(new Font("Arial", Font.BOLD, 12)); // sets font for j
															// button
		openFile.setBounds(50, 60, 190, 30); // sets location and size of
												// jbutton
		openFile.addActionListener(this); // adds actionlistener to jbutton
		openFile.setVisible(false); // sets jbutton to not visible
		this.add(openFile); // adds jbutton to jpanel

		writeToDatabase = new JButton("Write to database");
		writeToDatabase.setFont(new Font("Arial", Font.BOLD, 12));
		writeToDatabase.setBounds(50, 110, 190, 30);
		writeToDatabase.addActionListener(this);
		writeToDatabase.setVisible(false);
		this.add(writeToDatabase);

		updateDatabase = new JButton("Update database"); // creates a jbutton
															// for updating
															// database
		updateDatabase
				.setToolTipText("Choose a xlsx file from which to update database (see example first)"); // sets
																											// tooltip
																											// for
																											// jbutton
		updateDatabase.setFont(new Font("Arial", Font.BOLD, 12)); // sets font
																	// for
																	// jbutton
		updateDatabase.setBounds(50, 160, 190, 30); // sets location and size of
													// jbutton
		updateDatabase.addActionListener(this); // adds actionlistener to
												// jbutton
		this.add(updateDatabase); // adds jbutton to jpanel

		exampleFile = new JButton("Example"); // creates a jbutton for seeing
												// example xlsx file
		exampleFile.setToolTipText("Opens an example xlsx file"); // sets
																	// tooltip
																	// for
																	// jbutton
		exampleFile.setFont(new Font("Arial", Font.BOLD, 12)); // sets font for
																// jbutton
		exampleFile.setBounds(253, 160, 107, 30); // sets location and size of
													// jbutton
		exampleFile.addActionListener(this); // adds actionlistener to jbutton
		this.add(exampleFile); // adds jbutton to janel
	}

	@SuppressWarnings("resource")
	static void convert(File inputFile, File outputFile) {
		// For storing data into CSV files
		StringBuffer data = new StringBuffer();

		try {
			FileOutputStream fos = new FileOutputStream(outputFile);
			// Get the workbook object for XLSX file
			XSSFWorkbook wBook = new XSSFWorkbook(
					new FileInputStream(inputFile));
			// Get first sheet from the workbook
			XSSFSheet sheet = wBook.getSheetAt(1); // CHOOSE WHICH EXCEL SHEET

			// "Iterate" through each rows from first sheet

			int skipComments = -1;
			int skipKursaID = 18;
			int skipKurss = 19;
			int date = skipKursaID - 1;

			String dateString;

			for (Row row : sheet) {
				if (row.getRowNum() != 1 && row.getRowNum() != 2) {
					for (int cn = 0; cn < row.getLastCellNum(); cn++) {
						if (cn != skipKursaID && cn != skipKurss
								&& cn != skipComments) {
							Cell cell = row.getCell(cn,
									Row.CREATE_NULL_AS_BLANK);

							switch (cell.getCellType()) {

							case Cell.CELL_TYPE_BOOLEAN:
								data.append(cell.getBooleanCellValue() + "|");
								break;
							case Cell.CELL_TYPE_NUMERIC:
								data.append(cell.getNumericCellValue() + "|");
								break;
							case Cell.CELL_TYPE_STRING:
								if (cell.getStringCellValue().equals(
										"J�su koment�ri")) {
									skipComments = cn;
								} else if (cn == date) {
									dateString = cell.getStringCellValue();
									String dateFinal = new String();
									int dotCount = 0;
									String day = new String();
									for (int i = 0; i < dateString.length(); i++) {
										char c = dateString.charAt(i);
										if (dotCount < 2) {
											if (c >= 48 && c <= 57
													&& dotCount != 1) {
												dateFinal += c;
											} else if (c >= 48 && c <= 57) {
												day += c;
											} else if (c == 46 && dotCount != 3) {
												dotCount++;
											}
										} else if (dateString
												.contains("January")) {
											dateFinal += "-1-";
											dateFinal += day;
											dotCount = 0;
											break;
										} else if (dateString
												.contains("February")) {
											dateFinal += "-2-";
											dateFinal += day;
											dotCount = 0;
											break;
										} else if (dateString.contains("March")) {
											dateFinal += "-3-";
											dateFinal += day;
											dotCount = 0;
											break;
										} else if (dateString.contains("April")) {
											dateFinal += "-4-";
											dateFinal += day;
											dotCount = 0;
											break;
										} else if (dateString.contains("May")) {
											dateFinal += "-5-";
											dateFinal += day;
											dotCount = 0;
											break;
										} else if (dateString.contains("June")) {
											dateFinal += "-6-";
											dateFinal += day;
											dotCount = 0;
											break;
										} else if (dateString.contains("July")) {
											dateFinal += "-7-";
											dateFinal += day;
											dotCount = 0;
											break;
										} else if (dateString
												.contains("August")) {
											dateFinal += "-8-";
											dateFinal += day;
											dotCount = 0;
											break;
										} else if (dateString
												.contains("September")) {
											dateFinal += "-9-";
											dateFinal += day;
											dotCount = 0;
											break;
										} else if (dateString
												.contains("October")) {
											dateFinal += "-10-";
											dateFinal += day;
											dotCount = 0;
											break;
										} else if (dateString
												.contains("November")) {
											dateFinal += "-11-";
											dateFinal += day;
											dotCount = 0;
											break;
										} else if (dateString
												.contains("December")) {
											dateFinal += "-12-";
											dateFinal += day;
											dotCount = 0;
											break;
										}
									}
									if (row.getRowNum() != 0)
										data.append(dateFinal + "|");
								} else if (row.getRowNum() != 0)
									data.append(cell.getStringCellValue() + "|");
								break;
							case Cell.CELL_TYPE_BLANK:
								data.append("");
								break;
							default:
								data.append(cell + "|");
							}
						}
					}
					if (row.getRowNum() != 0)
						data.append("\r\n"); // EXCEL RINDAS BEIGAS
				}
			}

			fos.write(data.toString().getBytes());
			fos.close();
		} catch (Exception e) {
			System.out.println("Exception while converting xlsx to csv\n"
					+ e.getMessage());
		}
	}

	static void readExcelFile(File readFrom,
			ArrayList<ArrayList<String>> excelData) {
		try {
			FileInputStream fis = new FileInputStream(readFrom);// create the
																// input stream
																// from the xlsx
																// file
			@SuppressWarnings("resource")
			Workbook workbook = new XSSFWorkbook(fis); // create Workbook
														// instance for xlsx
														// file input stream
			Sheet sheet = workbook.getSheetAt(0); // get the 1st sheet from the
													// workbook
			Iterator<Row> rowIterator = sheet.iterator(); // every sheet has
															// rows, iterate
															// over them
			boolean firstIteration = true; // for reading columns names
			while (rowIterator.hasNext()) { // while there are rows to read from
				ArrayList<String> rows = new ArrayList<String>(); // arraylist
																	// for
																	// saving
																	// one row
				String uniqueID = "";
				String stringToChange = "";
				String columnName = "";

				// Get the row object
				Row row = rowIterator.next();

				// Every row has columns, get the column iterator and iterate
				// over them
				Iterator<Cell> cellIterator = row.cellIterator();
				while (cellIterator.hasNext()) {
					// Get the Cell object
					Cell cell = cellIterator.next();

					// check the cell type and process accordingly
					switch (cell.getCellType()) {
					case Cell.CELL_TYPE_STRING: {
						if (firstIteration == true) {
							columnName = cell.getStringCellValue().trim();
							rows.add(columnName);
						} else {
							uniqueID = cell.getStringCellValue().trim();
							rows.add(uniqueID);
						}
						break;
					}
					case Cell.CELL_TYPE_NUMERIC: {
						long number = (long) cell.getNumericCellValue();
						stringToChange = String.valueOf(number);
						rows.add(stringToChange);
						break;
					}
					}
				} // end of cell iterator
				firstIteration = false;
				if (rows.size() > 0) {
					excelData.add(rows);
				}
			} // end of rows iterator

		} catch (IOException e) {
			System.out.println("Exception while reading from xlsx file\n"
					+ e.getMessage());
		}
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		Action details = fileChooser.getActionMap().get("viewTypeDetails");
		details.actionPerformed(e);

		if (e.getSource().equals(chooseFile)) {
			fileChooser.setVisible(true);
			int status = fileChooser.showDialog(null, "Choose File"); // gets
																		// state
																		// of
																		// jfilechooser

			if (status == JFileChooser.APPROVE_OPTION) { // if open is pressed

				new Thread() {
					public void run() {
						try {
							final File file = fileChooser.getSelectedFile(); // gets
																				// selected
																				// files
																				// path
							String fileType = Files.probeContentType(file
									.toPath());
							if (FILE_TYPE.equals(fileType)) {
								log.setText("Selected file: " + file.getName()
										+ "\n");
								String fileName = file.getName();
								fileName = fileName.substring(0,
										fileName.indexOf("."))
										+ ".csv";
								File currentDirectory = new File(fileName);

								log.append("Converting ...\n");
								long start = System.nanoTime();
								convert(file, currentDirectory);
								long end = System.nanoTime();
								long elapsedTime = end - start;
								double seconds = (double) elapsedTime / 1000000000.0;
								log.append("Execution time: " + seconds + "\n");
								log.append("Created file: " + fileName);
								openFile.setVisible(true);
								writeToDatabase.setVisible(true);
							} else {
								JOptionPane.showMessageDialog(null,
										"Invalid file type", "Warning !",
										JOptionPane.INFORMATION_MESSAGE);
							}
						} catch (IOException ioe) {
							System.out
									.println("Exception while determening file type\n"
											+ ioe.getMessage());
						}
					}
				}.start();
			} else if (status == JFileChooser.ERROR_OPTION) {
				JOptionPane.showMessageDialog(null, "File chooser error",
						"Warning !", JOptionPane.INFORMATION_MESSAGE);
			}
		} else if (e.getSource().equals(openFile)) {
			try {
				File file = fileChooser.getSelectedFile();
				String fileName = file.getName();
				fileName = fileName.substring(0, fileName.indexOf("."))
						+ ".csv";
				file = new File(fileName);

				if (Desktop.isDesktopSupported()) {
					Desktop desktop = Desktop.getDesktop();
					if (file.exists())
						desktop.open(file);
				}
			} catch (IOException ioe) {
				System.out.println("Exception while opening file\n"
						+ ioe.getMessage());
			}
		} else if (e.getSource().equals(updateDatabase)) {
			fileChooser.setVisible(true);
			int status = fileChooser.showDialog(null, "Choose File"); // what
																		// state
																		// is
																		// jfilechooser
																		// in
			if (status == JFileChooser.APPROVE_OPTION) { // if open is pressed
				final File file = fileChooser.getSelectedFile(); // gets
																	// selected
																	// files
																	// path

				new Thread() {
					public void run() {
						try {
							String fileType = Files.probeContentType(file
									.toPath());
							if (FILE_TYPE.equals(fileType)) {
								log.setText("Selected file: " + file.getName()
										+ "\n");
								try {
									ArrayList<ArrayList<String>> excelData = new ArrayList<ArrayList<String>>();
									log.append("Reading file... \n");
									readExcelFile(file, excelData);
									log.append("File read successfully \n");
									for (ArrayList<String> s : excelData) {
										System.out.println(s);
									}

									long start = System.nanoTime();

									Class.forName(JDBC_DRIVER);
									log.append("Connecting to database... \n");
									System.out
											.println("Connecting to a selected database...");
									conn = DriverManager.getConnection(DB_URL,
											USER, PASS);
									conn.setAutoCommit(false);
									log.append("Connected to database successfully \n");
									System.out
											.println("Connected database successfully...");

									StringBuilder sb = new StringBuilder(); // gets
																			// columnNames
																			// from
																			// excelData
									for (int i = 0; i < excelData.get(0).size(); i++) {
										if (i == excelData.get(0).size() - 1) {
											sb.append(excelData.get(0).get(i)
													+ " "); // add column names
															// to string builder
										} else {
											sb.append(excelData.get(0).get(i)
													+ ", ");
										}
									}
									String columnNames = new String(sb); // make
																			// string
																			// with
																			// stringbuilder
																			// contents
									System.out.println(columnNames);
									String sql = "SELECT " + columnNames
											+ "FROM " + DB_TABLE_NAME; // make
																		// sql
																		// statement
									System.out.println(sql);

									stmt = conn.createStatement();
									ResultSet rs = stmt.executeQuery(sql);
									conn.commit();
									ResultSetMetaData rsmd = rs.getMetaData();
									int columnCount = rsmd.getColumnCount();
									for (int i = 0; i < columnCount; i++) {
										String name = rsmd.getColumnName(i + 1);
										System.out.println(name);
									}

									System.out.println("exceldata size: "
											+ excelData.size()); // amount of
																	// columns
									while (rs.next()) {
										String number = rs.getString(excelData
												.get(0).get(0)); // columnname =
																	// excelData.get(0).get(i)
										if (number != null) {
											for (int i = 0; i < excelData
													.size(); i++) {
												if (number.equals(excelData
														.get(i).get(0))) { // number
																			// =
																			// excelData.get(j).get(0)
													for (int j = 1; j < excelData
															.get(0).size(); j++) {
														boolean isDouble = false;
														for (String value : Doubles) {
															if (excelData
																	.get(0)
																	.get(j)
																	.equals(value)) {
																isDouble = true;
																break;
															}// closes if
																// statement
														}// closes for statement
														if (isDouble == true) {
															if (Double
																	.parseDouble(excelData
																			.get(i)
																			.get(j)) >= 1
																	&& Double
																			.parseDouble(excelData
																					.get(i)
																					.get(j)) <= 5) {
																System.out
																		.println(excelData
																				.get(i)
																				.get(j));
																String statement = "UPDATE "
																		+ DB_TABLE_NAME
																		+ " SET "
																		+ excelData
																				.get(0)
																				.get(j)
																		+ "='"
																		+ excelData
																				.get(i)
																				.get(j)
																		+ "' WHERE "
																		+ UNIQUE_ID
																		+ "='"
																		+ number
																		+ "'";
																stmt = conn
																		.createStatement();
																stmt.executeUpdate(statement);
																System.out
																		.println(statement);
																conn.commit();
															} // closesif
																// statement
															else {
																log.append("At: ["
																		+ number
																		+ "] value: ["
																		+ excelData
																				.get(i)
																				.get(j)
																		+ "] Not in interval [1-5]\n");
															} // closes else
																// statement
														}// closes if statmement
														else {
															String statement = "UPDATE "
																	+ DB_TABLE_NAME
																	+ " SET "
																	+ excelData
																			.get(0)
																			.get(j)
																	+ "='"
																	+ excelData
																			.get(i)
																			.get(j)
																	+ "' WHERE "
																	+ UNIQUE_ID
																	+ "='"
																	+ number
																	+ "'";
															stmt = conn
																	.createStatement();
															System.out
																	.println(statement);
															stmt.executeUpdate(statement);
															conn.commit();
														}
													}// closes for statement
												}// closes if statment
											}// closes for statement
										}// closes while statement
									}
									long end = System.nanoTime();
									long elapsedTime = end - start;
									double seconds = (double) elapsedTime / 1000000000.0;
									log.append("Execution time: " + seconds
											+ "\n");
								} catch (SQLException sqle) { // Handle errors
																// for JDBC
									System.out.println("SQL exception\n"
											+ sqle.getMessage());
								} catch (Exception e) { // Handle errors for
														// Class.forName
									System.out.println("Unexpected exception\n"
											+ e.getMessage());
								} finally {
									try {
										if (stmt != null) {
											stmt.close();
										}
										if (conn != null)
											conn.close();
									} catch (SQLException sqle) {
										System.out.println("SQL exception\n"
												+ sqle.getMessage());
									}
								}
							} else {
								JOptionPane.showMessageDialog(null,
										"Invalid file type", "Warning !",
										JOptionPane.INFORMATION_MESSAGE);
							}
						} catch (IOException ioe) {
							System.out
									.println("Exception while determening file type\n"
											+ ioe.getMessage());
						}
					}
				}.start();

			} else if (status == JFileChooser.ERROR_OPTION) {
				JOptionPane.showMessageDialog(null, "File chooser error",
						"Warning !", JOptionPane.INFORMATION_MESSAGE);
			}
		} else if (e.getSource().equals(exampleFile)) {
			try {
				File file = new File("resources/example.xlsx");
				if (Desktop.isDesktopSupported()) {
					Desktop desktop = Desktop.getDesktop();
					if (file.exists())
						desktop.open(file);
				}
			} catch (IOException ioe) {
				System.out.println("Exception while opening file\n"
						+ ioe.getMessage());
			}
		}
	}
}
