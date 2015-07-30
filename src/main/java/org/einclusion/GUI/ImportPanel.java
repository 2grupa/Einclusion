package org.einclusion.GUI;

import java.awt.Desktop;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Iterator;

import javax.swing.Action;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.border.EmptyBorder;
import javax.swing.filechooser.FileNameExtensionFilter;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFCell;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import javax.swing.ScrollPaneConstants;

/**
 * Panel for importing, exporting and updating database using xlsx and csv files
 */
public class ImportPanel extends JPanel implements ActionListener, KeyListener {
	private static final long serialVersionUID = 1L;

	public static final String FILE_TYPE = "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet";
	JFileChooser fileChooser;
	FileNameExtensionFilter filter; // filter for jfilechooser
	JButton chooseFile, updateDatabase, exampleFile, writeToXlsx,
			openDatabaseFile;
	static JButton openFile;
	JScrollPane scrollPane; // scroll pane for log
	JTextField fieldForInput;
	static JTextArea log; // text area for log (what the application is doing)
	File path;
	// JDBC driver name and database URL
	static final String JDBC_DRIVER = "org.h2.Driver";
	static final String DB_URL = "jdbc:h2:data/Student";
	// Database credentials
	static final String USER = "sa";
	static final String PASS = "";
	static final String DB_TABLE_NAME = "STUDENT";
	static final String UNIQUE_ID = "PHONE";
	static final String[] DOUBLES = { "SWL", "SAL", "ELM", "IWS", "ELE",
			"PUOU", "DS", "M2", "OU", "PU" };

	Statement stmt = null;
	Connection conn = null;

	public ImportPanel() {

		this.setLayout(null);

		fileChooser = new JFileChooser(); // creates a new JfileChooser
		fileChooser.setDialogTitle("Choose a file"); // sets jfilechooser name
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
		chooseFile.setToolTipText("Choose a valid xlsx file"); // adds tooltip
																// to button
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
		log = new JTextArea(); // creates new jtextarea
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

		openFile = new JButton("Open"); // creates a nw button for opening a
										// file
		openFile.setToolTipText("<html>Opens the created .csv file with its default program<br>"
				+ "(to change this program right click on any .csv file<br>and choose"
				+ " the program you want to open it with)</html>"); // sets
																	// tooltip
																	// for
																	// jbutton
		openFile.setFont(new Font("Arial", Font.BOLD, 12)); // sets font for j
															// button
		openFile.setBounds(250, 10, 110, 30); // sets location and size of
												// jbutton
		openFile.addActionListener(this); // adds actionlistener to jbutton
		openFile.setVisible(false); // sets jbutton to not visible
		this.add(openFile); // adds jbutton to jpanel

		updateDatabase = new JButton("Update database"); // creates a jbutton
															// for updating
															// database
		updateDatabase
				.setToolTipText("<html>Choose an xlsx file that will update database<br>"
						+ " (see example first)</html>");// sets tooltip for
															// jbutton
		updateDatabase.setFont(new Font("Arial", Font.BOLD, 12)); // sets font
																	// for
																	// jbutton
		updateDatabase.setBounds(50, 60, 190, 30); // sets location and size of
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
		exampleFile.setBounds(250, 60, 110, 30); // sets location and size of
													// jbutton
		exampleFile.addActionListener(this); // adds actionlistener to jbutton
		this.add(exampleFile); // adds jbutton to janel

		JLabel labelForInput = new JLabel("Enter exported file name:"); // creates
																		// a new
																		// JLabel
																		// for
																		// info
																		// about
																		// export
		labelForInput.setFont(new Font("Arial", Font.BOLD, 12)); // sets font
																	// for
																	// jlabel
		labelForInput.setBounds(50, 101, 190, 30); // sets location and size of
													// jlabel
		this.add(labelForInput); // adds jlabel to jpanel

		fieldForInput = new JTextField("Student"); // creates a jbutton for
													// user input
		fieldForInput.setToolTipText("Enter a file name without the extension");
		fieldForInput.setFont(new Font("Arial", Font.BOLD, 12)); // sets font
																	// for
																	// jTextField
		fieldForInput.setBounds(50, 129, 190, 30); // sets location size of
													// jbutton
		fieldForInput.addKeyListener(this); // add keylistener to jtextfield
		this.add(fieldForInput); // adds jbutton to jpanel

		writeToXlsx = new JButton("Write to xlsx"); // creates a jbutton for
													// seeing example xlsx file
		writeToXlsx.setToolTipText("Writes database contents to xlsx file"); // sets
																				// tooltip
																				// for
																				// jbutton
		writeToXlsx.setFont(new Font("Arial", Font.BOLD, 12)); // sets font for
																// jbutton
		writeToXlsx.setBounds(50, 176, 190, 30); // sets location and size of
													// jbutton
		writeToXlsx.addActionListener(this); // adds actionlistener to jbutton
		this.add(writeToXlsx); // adds jbutton to jpanel

		openDatabaseFile = new JButton("Open"); // creates a jbutton for user
												// input
		openDatabaseFile
				.setToolTipText("<html>Opens the created .xlsx file with its default program<br>"
						+ "(to change this program right click on any .xlsx file<br>and choose"
						+ " the program you want to open it with)</html>"); // sets
																			// tooltip
																			// for
																			// jbutton
		openDatabaseFile.setFont(new Font("Arial", Font.BOLD, 12)); // sets font
																	// for
																	// jTextField
		openDatabaseFile.setBounds(250, 176, 110, 30); // sets location size of
														// jbutton
		openDatabaseFile.setVisible(false);
		openDatabaseFile.addActionListener(this); // add keylistener to
													// jtextfield
		this.add(openDatabaseFile); // adds jbutton to jpanel
	}

	/**
	 * Function that opens a file with its default program
	 * 
	 * @param file
	 *            - path to file
	 */
	static void openFile(File file) {
		try {
			if (Desktop.isDesktopSupported()) { // if desktop class is supported
												// on this platform
				Desktop desktop = Desktop.getDesktop(); // gets desktop instance
														// of current browser
														// context
				if (file.exists())
					desktop.open(file); // if file exists open it with its
										// default program
			}
		} catch (IOException ioe) {
			System.out.println("Exception while opening file\n"
					+ ioe.getMessage());
		}
	}

	/**
	 * Function for creating a csv file from a xlsx file
	 * 
	 * @param inputFile
	 *            - file path to a xlsx file
	 * @param outputFile
	 *            - file path to a csv file
	 */
	static void xlsxToCsv(File inputFile, File outputFile) throws Exception {
		// For storing data into CSV files
		StringBuffer data = new StringBuffer();
		try {
			@SuppressWarnings("resource")
			XSSFWorkbook wBook = new XSSFWorkbook(
					new FileInputStream(inputFile));
			// Get first sheet from the workbook
			XSSFSheet sheet = null;
			// "Iterate" through each rows from first sheet
			// for each sheet in the workbook
			boolean found = false;
			for (int i = 0; i < wBook.getNumberOfSheets(); i++) {
				if (wBook.getSheetName(i).equals("detailed")) {
					sheet = wBook.getSheetAt(i);
					found = true;
					break;
				}
			}
			if (found == false) {
				log.append("\"detailed\" sheet not found\n");
				throw new Exception("No sheet found exception");
			} else {
				FileOutputStream fos = new FileOutputStream(outputFile);
				// Values to skip and not write to the CSV file which will then
				// be sent to database
				int skipLietotajvards = 1; // Ignores Lietotajvards column
				int skipComments = 16; // Jusu komentars column number set
										// manually so it can be skipped
				int skipKursaID = 18; // Kursa ID column number set manually so
										// it can be skipped
				int skipKurss = 19; // Kurss column number set manually so it
									// can be skipped
				int date = skipKursaID - 1; // Assumes date column is the column
											// just before Kursa ID column

				String dateString; // will hold the date cell as written in the
									// excel file

				for (Row row : sheet) { // while there are rows
					if (row.getRowNum() != 1) { // don't need to look at the
												// second row
						for (int cn = 0; cn < row.getLastCellNum(); cn++) { // while
																			// there
																			// are
																			// cells
																			// in
																			// the
																			// row
							if (cn != skipKursaID && cn != skipKurss
									&& cn != skipComments
									&& cn != skipLietotajvards) { // if the
																	// current
																	// cell is
																	// one we
																	// need
								Cell cell = row.getCell(cn,
										Row.CREATE_NULL_AS_BLANK); // takes the
																	// cell even
																	// if it's
																	// empty

								switch (cell.getCellType()) { // determines the
																// type of the
																// cell
								case Cell.CELL_TYPE_BOOLEAN:
									data.append(cell.getBooleanCellValue()
											+ ",");
									break;
								case Cell.CELL_TYPE_NUMERIC:
									data.append(cell.getNumericCellValue()
											+ ",");
									break;
								case Cell.CELL_TYPE_STRING:
									if (cn == date) {
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
												} else if (c == 46) {
													dotCount++;
												}
											} else if (dateString
													.contains("January")) {
												dateFinal += "-1-";
												dateFinal += day;
												break;
											} else if (dateString
													.contains("February")) {
												dateFinal += "-2-";
												dateFinal += day;
												break;
											} else if (dateString
													.contains("March")) {
												dateFinal += "-3-";
												dateFinal += day;
												break;
											} else if (dateString
													.contains("April")) {
												dateFinal += "-4-";
												dateFinal += day;
												break;
											} else if (dateString
													.contains("May")) {
												dateFinal += "-5-";
												dateFinal += day;
												break;
											} else if (dateString
													.contains("June")) {
												dateFinal += "-6-";
												dateFinal += day;
												break;
											} else if (dateString
													.contains("July")) {
												dateFinal += "-7-";
												dateFinal += day;
												break;
											} else if (dateString
													.contains("August")) {
												dateFinal += "-8-";
												dateFinal += day;
												break;
											} else if (dateString
													.contains("September")) {
												dateFinal += "-9-";
												dateFinal += day;
												break;
											} else if (dateString
													.contains("October")) {
												dateFinal += "-10-";
												dateFinal += day;
												break;
											} else if (dateString
													.contains("November")) {
												dateFinal += "-11-";
												dateFinal += day;
												break;
											} else if (dateString
													.contains("December")) {
												dateFinal += "-12-";
												dateFinal += day;
												break;
											} else {
												dateFinal += "-00-";
												dateFinal += day;
												break;
											}
										}
										dotCount = 0;
										if (row.getRowNum() != 0)
											data.append(dateFinal + ",");
									} else if (row.getRowNum() != 0)
										data.append(cell.getStringCellValue()
												+ ",");
									break;
								case Cell.CELL_TYPE_BLANK:
									if (row.getRowNum() > 1) {
										data.append("No data" + ",");
										break;
									} else
										break;
								default:
									data.append(cell + ",");
								}
							}
						}
						if (row.getRowNum() != 0)
							data.append("\r\n"); // EXCEL RINDAS BEIGAS
					}
				}
				fos.write(data.toString().getBytes());
				fos.close();
				openFile.setVisible(true);
				log.append("Created file: " + outputFile.getName() + "\n");
			}
		} catch (Exception ioe) {
			ioe.printStackTrace();
		}

	}

	/**
	 * Function for reading an excel file and saving its contents
	 * 
	 * @param readFrom
	 *            - path to a xlsx file
	 * @param excelData
	 *            - ArrayList(ArrayList(String)) an ArrayList that contains
	 *            ArrayLists of String values
	 */
	static void readExcelFile(File readFrom,
			ArrayList<ArrayList<String>> excelData) {
		try {
			FileInputStream fis = new FileInputStream(readFrom); // create the
																	// input
																	// stream
																	// from the
																	// xlsx file
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
				String columnName = null;
				String cellValue = null;

				Row row = rowIterator.next(); // get the row object
				Iterator<Cell> cellIterator = row.cellIterator(); // every row
																	// has
																	// columns,
																	// get the
																	// column
																	// iterator
																	// and
																	// iterate
																	// over them
				while (cellIterator.hasNext()) {
					Cell cell = cellIterator.next(); // get the Cell object
					switch (cell.getCellType()) { // check the cell type
					case Cell.CELL_TYPE_STRING: // if celltype is string
					{
						if (firstIteration == true) { // if first iteration
							columnName = cell.getStringCellValue().trim(); // get
																			// column
																			// name
							rows.add(columnName); // add to arraylist
						} else {
							cellValue = cell.getStringCellValue().trim(); // get
																			// cellValue
							rows.add(cellValue); // add to arraylist
						}
						break;
					}
					case Cell.CELL_TYPE_NUMERIC: // if celltype is numeric
					{
						long number = (long) cell.getNumericCellValue(); // cast
																			// value
																			// to
																			// long
						cellValue = String.valueOf(number); // create string
															// from long
						rows.add(cellValue); // add to arraylist
						break;
					}
					}
				} // end of cell iterator
				firstIteration = false; // after firstiteration is false
				if (rows.size() > 0) { // if arraylist of one row is not empty
					excelData.add(rows); // add it to arraylist that contains
											// all rows
				}
			} // end of rows iterator

		} catch (IOException e) {
			System.out.println("Exception while reading from xlsx file\n"
					+ e.getMessage());
		}
	}

	/**
	 * Function for writing exporting database to xlsx file
	 * 
	 * @param file
	 *            - path to file
	 */
	static void writeExcelFile(File file) {

		XSSFWorkbook wb = new XSSFWorkbook(); // create Workbook instance for
												// xlsx file
		XSSFSheet sheet = wb.createSheet("detailed"); // create Sheet for xlsx
														// file
		Statement stmt = null;
		Connection conn = null;
		try {
			Class.forName(JDBC_DRIVER); // jdbc driver name
			log.append("Connecting to database... \n");
			conn = DriverManager.getConnection(DB_URL, USER, PASS); // establsih
																	// connection
																	// to
																	// database
			log.append("Connected to database successfully \n");
			conn.setAutoCommit(false); // sets autocommit to false
			// , NAME, TEMA, SWL, SAL, ELM, IWS, ELE, PUOU, IZPILDITS, DS, M2,
			// OU, PU
			String sql = "SELECT PHONE, NAME, TOPIC, SWL, SAL, ELM, IWS, ELE, PUOU, SUBMITDATE, DS, M2, OU, PU FROM "
					+ DB_TABLE_NAME;
			stmt = conn.createStatement(); // creates a new statement object
			ResultSet rs = stmt.executeQuery(sql); // a table of data that is
													// obtained by executing a
													// sql statement
			ResultSetMetaData rsmd = rs.getMetaData(); // creates a table of
														// data that contains
														// information about
														// table
			XSSFRow row = sheet.createRow(1); // creates a new row in 2nd row of
												// the file
			XSSFCell cell; // cell object
			for (int i = 0; i < rsmd.getColumnCount(); i++) { // iterates
																// columnCount
																// times
				cell = row.createCell(i); // creates a new cell in i column
				cell.setCellValue(rsmd.getColumnName(i + 1)); // sets cell value
																// to column
																// name from
																// database
			}
			conn.commit(); // makes changes to datbase permanent

			int rowCounter = 2; // counts rows
			while (rs.next()) { // while table has contents
				String numurs = rs.getString("PHONE"); // gets uniqueid PHONE
														// from database
				if (numurs != null) { // if NUMURS is initiliazed
					row = sheet.createRow(rowCounter); // creates a new row
					int cellCounter = 0; // counts cells
					cell = row.createCell(cellCounter); // creates a new cell
					cell.setCellValue(numurs); // sets cell value
					cellCounter++; // increases cellCounter by 1

					String name = rs.getString("NAME"); // gets String from
														// database
					cell = row.createCell(cellCounter); // creates new cell
					cell.setCellValue(name); // sets cell value
					cellCounter++; // increases cell counter

					String tema = rs.getString("TOPIC");
					cell = row.createCell(cellCounter);
					cell.setCellValue(tema);
					cellCounter++;

					Double swl = rs.getDouble("SWL");
					cell = row.createCell(cellCounter);
					cell.setCellValue(swl);
					cellCounter++;

					Double sal = rs.getDouble("SAL");
					cell = row.createCell(cellCounter);
					cell.setCellValue(sal);
					cellCounter++;

					Double elm = rs.getDouble("ELM");
					cell = row.createCell(cellCounter);
					cell.setCellValue(elm);
					cellCounter++;

					Double iws = rs.getDouble("IWS");
					cell = row.createCell(cellCounter);
					cell.setCellValue(iws);
					cellCounter++;

					Double ele = rs.getDouble("ELE");
					cell = row.createCell(cellCounter);
					cell.setCellValue(ele);
					cellCounter++;

					Double puou = rs.getDouble("PUOU");
					cell = row.createCell(cellCounter);
					cell.setCellValue(puou);
					cellCounter++;

					Timestamp timeStamp = rs.getTimestamp("SUBMITDATE");
					cell = row.createCell(cellCounter);
					cell.setCellValue(timeStamp.toString());
					cellCounter++;

					Double ds = rs.getDouble("DS");
					cell = row.createCell(cellCounter);
					cell.setCellValue(ds);
					cellCounter++;

					Double m2 = rs.getDouble("M2");
					cell = row.createCell(cellCounter);
					cell.setCellValue(m2);
					cellCounter++;

					Double ou = rs.getDouble("OU");
					cell = row.createCell(cellCounter);
					cell.setCellValue(ou);
					cellCounter++;

					Double pu = rs.getDouble("PU");
					cell = row.createCell(cellCounter);
					cell.setCellValue(pu);
					cellCounter++;

					rowCounter++; // increases row counter
				}
			}

		} catch (ClassNotFoundException clnfe) {
			System.out.println(clnfe.getMessage());
		} catch (SQLException sqle) {
			System.out.println(sqle.getMessage());
		} finally {
			try {
				if (stmt != null) {
					stmt.close();
				}
				if (conn != null) {
					conn.close();
				}
			} catch (SQLException sqle) {
				System.out.println(sqle.getMessage());
			}
		}

		try {
			FileOutputStream fileOut = new FileOutputStream(file);

			// write this workbook to an Outputstream.
			wb.write(fileOut);
			fileOut.flush();
			fileOut.close();
		} catch (FileNotFoundException fnfe) {
			System.out.println("File Not Found\n" + fnfe.getMessage());
		} catch (IOException ioe) {
			System.out.println("Input output exception\n" + ioe.getMessage());
		}

	}

	@Override
	public void actionPerformed(ActionEvent e) {
		Action details = fileChooser.getActionMap().get("viewTypeDetails"); // uses
																			// Action
																			// to
																			// set
																			// jfilechooser
																			// view
																			// to
																			// details
		details.actionPerformed(e); // when an actionevent occurs
		if (e.getSource().equals(chooseFile)) { // choosefile button is pressed
			fileChooser.setVisible(true); // sets jfilechooser to visible
			int status = fileChooser.showDialog(null, "Choose File"); // gets
																		// state
																		// of
																		// jfilechooser
			if (status == JFileChooser.APPROVE_OPTION) { // if open is pressed
															// in jfilechooser
				new Thread() { // creates a new thread so processes execute
								// consecutively
					public void run() { // creates run method for thread
						try {
							final File file = fileChooser.getSelectedFile(); // gets
																				// selected
																				// files
																				// path
							String fileType = Files.probeContentType(file
									.toPath());// gets file type
							if (FILE_TYPE.equals(fileType)) { // if file type is
																// xlsx
								log.setText("Selected file: " + file.getName()
										+ "\n");
								String fileName = file.getName(); // gets files
																	// name
								fileName = fileName.substring(0,
										fileName.indexOf("."))
										+ ".csv"; // changes extension to .csv
								File currentDirectory = new File(fileName); // sets
																			// files
																			// location
																			// to
																			// current
																			// directory
																			// (directory
																			// project
																			// is
																			// in)

								log.append("Converting ...\n");
								long start = System.nanoTime(); // gets time
																// before
																// executing
																// function
								try {
									xlsxToCsv(file, currentDirectory);
								} catch (Exception nse) {
									nse.printStackTrace();
								}
								long end = System.nanoTime(); // gets time after
																// executing
																// function
								long elapsedTime = end - start; // gets
																// execution
																// time of
																// function
								double seconds = (double) elapsedTime / 1000000000.0;// converts
																						// nanoseconds
																						// to
																						// seconds
								log.append("Execution time: " + seconds);
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
				}.start(); // starts thread
			} else if (status == JFileChooser.ERROR_OPTION) {
				JOptionPane.showMessageDialog(null, "File chooser error",
						"Warning !", JOptionPane.INFORMATION_MESSAGE);
			}
		} else if (e.getSource().equals(openFile)) { // if openfile button is
														// pressed
			File file = fileChooser.getSelectedFile(); // gets selected files
														// path
			String fileName = file.getName(); // gets selected files name
			fileName = fileName.substring(0, fileName.indexOf(".")) + ".csv"; // changes
																				// files
																				// extension
																				// to
																				// .csv
			file = new File(fileName); // creates new path in working directory
			openFile(file); // function that opens file with its default program
		} else if (e.getSource().equals(updateDatabase)) { // if updatedatabase
															// button is pressed
			fileChooser.setVisible(true); // set jfilechooser to visible
			int status = fileChooser.showDialog(null, "Choose File"); // gets
																		// state
																		// of
																		// jfilechooser
			if (status == JFileChooser.APPROVE_OPTION) { // if open is pressed
															// in jfilechooser
				final File file = fileChooser.getSelectedFile(); // gets
																	// selected
																	// files
																	// path
				new Thread() { // creates a new thread so processes execute
								// consecutively
					public void run() { // creates run method for thread
						try {
							String fileType = Files.probeContentType(file
									.toPath()); // gets chosen file type
							if (FILE_TYPE.equals(fileType)) { // if fileType is
																// xlsx
								log.setText("Selected file: " + file.getName()
										+ "\n");
								try {
									// creates an arraylist of all rows that
									// contains arraylists of one row
									ArrayList<ArrayList<String>> excelData = new ArrayList<ArrayList<String>>();
									log.append("Reading file... \n");
									readExcelFile(file, excelData); // reads
																	// excelfile
																	// and saves
																	// it to
																	// arraylist
									log.append("File read successfully \n");

									long start = System.nanoTime(); // get
																	// system
																	// time
																	// before
																	// opening
																	// database

									Class.forName(JDBC_DRIVER); // jdbc driver
																// name
									log.append("Connecting to database... \n");
									conn = DriverManager.getConnection(DB_URL,
											USER, PASS); // establsih connection
															// to database
									conn.setAutoCommit(false); // sets
																// autocommit to
																// false
									log.append("Connected to database successfully \n");

									StringBuilder sb = new StringBuilder(); // creates
																			// a
																			// stringbuilder
																			// for
																			// column
																			// names
									for (int i = 0; i < excelData.get(0).size(); i++) { // repeats
																						// first
																						// row
																						// size
																						// times
										if (i == excelData.get(0).size() - 1) { // if
																				// last
																				// iteration
											sb.append(excelData.get(0).get(i)
													+ " "); // add column names
															// to string builder
										} else { // if not last iteration
											sb.append(excelData.get(0).get(i)
													+ ", "); // add column names
																// to string
																// builder
										}
									}
									String columnNames = new String(sb); // make
																			// string
																			// with
																			// stringbuilder
																			// contents
									String sql = "SELECT " + columnNames
											+ "FROM " + DB_TABLE_NAME; // make
																		// sql
																		// statement

									stmt = conn.createStatement(); // creates a
																	// new
																	// statement
																	// object
									ResultSet rs = stmt.executeQuery(sql); // a
																			// table
																			// of
																			// data
																			// that
																			// is
																			// obtained
																			// by
																			// executing
																			// a
																			// sql
																			// statement
									conn.commit(); // makes changes to datbase
													// permanent

									while (rs.next()) { // while table has
														// contents
										String number = rs.getString(excelData
												.get(0).get(0)); // columnname (
																	// excelData.get(0).get(i)
																	// )
										if (number != null) { // if number is
																// initialized
											for (int i = 0; i < excelData
													.size(); i++) { // amount of
																	// rows
																	// times
												if (number.equals(excelData
														.get(i).get(0))) { // number
																			// (
																			// excelData.get(j).get(0)
																			// )
													for (int j = 1; j < excelData
															.get(0).size(); j++) { // (amount
																					// of
																					// columns
																					// -
																					// 1)
																					// times
														boolean isDouble = false; // check
																					// if
																					// value
																					// is
																					// double
														for (String columnName : DOUBLES) { // iterator
																							// for
																							// DOUBLES
																							// array
															if (excelData
																	.get(0)
																	.get(j)
																	.equals(columnName)) { // if
																							// column
																							// name
																							// matches
																							// one
																							// of
																							// DOUBLES
																							// array
																							// values
																isDouble = true; // column
																					// contains
																					// double
																					// values
																break; // stop
																		// for
																		// cycle
															}
														}
														if (isDouble == true) { // if
																				// column
																				// contains
																				// double
																				// values
																				// if
																				// number
																				// is
																				// in
																				// interval
																				// [1-5]
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
																// creates sql
																// statement fot
																// inserting
																// value into
																// database
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
																		.createStatement(); // creates
																							// a
																							// new
																							// statement
																							// object
																stmt.executeUpdate(statement); // executes
																								// statement
																conn.commit(); // commits
																				// changes
															} else {
																log.append("At: ["
																		+ number
																		+ "] value: ["
																		+ excelData
																				.get(i)
																				.get(j)
																		+ "] Not in interval [1-5]\n");
															}
														} else {
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
															conn.commit();
														}
													}// closes for statement (
														// amount of columns - 1
														// ) times
												}// closes if statment ( number
													// equals )
											}// closes for statement ( amount of
												// row ) times
										}// closes if statement ( number is not
											// null )
									}// closes while statement ( table has
										// contents )
									long end = System.nanoTime(); // get system
																	// time
																	// after
																	// actions
																	// with
																	// database
																	// are
																	// finished
									long elapsedTime = end - start; // gets
																	// elapsed
																	// time in
																	// nanoseconds
									double seconds = (double) elapsedTime / 1000000000.0; // converts
																							// nanoseconds
																							// to
																							// seconds
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
				}.start(); // starts thread
			} else if (status == JFileChooser.ERROR_OPTION) {
				JOptionPane.showMessageDialog(null, "File chooser error",
						"Warning !", JOptionPane.INFORMATION_MESSAGE);
			}
		} else if (e.getSource().equals(exampleFile)) {
			File file = new File("resources/example.xlsx"); // creates file path
															// to example.xlsx
															// file
			openFile(file); // function that opens file with its default program
		} else if (e.getSource().equals(writeToXlsx)) {
			new Thread() {
				public void run() {
					String extension;
					if (fieldForInput.getText().contains(".")) {
						String fileName = fieldForInput.getText();
						extension = fileName.substring(fileName.indexOf("."),
								fileName.length());
						if (extension.equals(".xlsx")) {
							File file = new File(fileName);
							path = file;
							log.setText("Writing to " + fileName + " file...\n");
							long start = System.nanoTime(); // gets time before
															// executing
															// function
							writeExcelFile(file);
							long end = System.nanoTime(); // gets time after
															// executing
															// function
							long elapsedTime = end - start; // gets execution
															// time of function
							double seconds = (double) elapsedTime / 1000000000.0;// converts
																					// nanoseconds
																					// to
																					// seconds
							log.append(fileName + " file created\n");
							log.append("Execution time: " + seconds + "\n");
							openDatabaseFile.setVisible(true);
						}
					} else if (fieldForInput.getText().length() > 0) {
						String fileName = fieldForInput.getText();
						File file = new File(fileName + ".xlsx");
						path = file;
						log.setText("Writing to " + fileName + " file...\n");
						long start = System.nanoTime(); // gets time before
														// executing function
						writeExcelFile(file);
						long end = System.nanoTime(); // gets time after
														// executing function
						long elapsedTime = end - start; // gets execution time
														// of function
						double seconds = (double) elapsedTime / 1000000000.0;// converts
																				// nanoseconds
																				// to
																				// seconds
						log.append(fileName + " file created\n");
						log.append("Execution time: " + seconds + "\n");
						openDatabaseFile.setVisible(true);
					}
				}
			}.start();
		} else if (e.getSource().equals(openDatabaseFile)) {
			openFile(path);
		}
	}

	@Override
	public void keyPressed(KeyEvent e) {
	}

	@Override
	public void keyReleased(KeyEvent e) {
		if (e.getSource().equals(fieldForInput)) {
			String line;
			if (e.getKeyCode() != KeyEvent.VK_DELETE
					&& e.getKeyCode() != KeyEvent.VK_BACK_SPACE
					&& e.getKeyCode() != KeyEvent.VK_CONTROL
					&& e.getKeyCode() != KeyEvent.VK_SHIFT
					&& e.getKeyCode() != KeyEvent.VK_CAPS_LOCK
					&& e.getKeyCode() != KeyEvent.VK_LEFT
					&& e.getKeyCode() != KeyEvent.VK_UP
					&& e.getKeyCode() != KeyEvent.VK_RIGHT
					&& e.getKeyCode() != KeyEvent.VK_DOWN
					&& e.getKeyCode() != KeyEvent.VK_KP_LEFT
					&& e.getKeyCode() != KeyEvent.VK_KP_UP
					&& e.getKeyCode() != KeyEvent.VK_KP_RIGHT
					&& e.getKeyCode() != KeyEvent.VK_KP_DOWN) {
				// ignores backspace, delete, shift, caps lock, control and
				// arrows
				if (!((e.getKeyChar() <= 122 && e.getKeyChar() >= 97)
						|| (e.getKeyChar() <= 90 && e.getKeyChar() >= 65)
						|| (e.getKeyChar() <= 57 && e.getKeyChar() >= 48) || e
							.getKeyChar() == 95)) { // if char is not a-z or A-Z
													// or 0-9 then delete it
													// from JTextField
					if (fieldForInput.getText().contains("" + e.getKeyChar())) { // if
																					// textfield
																					// contains
																					// illegal
																					// char
						line = fieldForInput.getText().replaceAll(
								"[\\" + e.getKeyChar() + "]+", "");// removes
																	// all
																	// illegal
																	// chars
																	// from
																	// string
						fieldForInput.setText(line); // sets substring without
														// illegal characters to
														// jtextfield
						log.append("invalid character: " + e.getKeyChar()
								+ "\n");
					}
				}
			}
		}
	}

	@Override
	public void keyTyped(KeyEvent e) {
	}
}