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

import org.apache.log4j.Logger;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFCell;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.einclusion.model.InstanceManager;
import org.einclusion.model.M2;
import org.einclusion.model.ModelManager;
import org.einclusion.model.PrepareData;

import javax.swing.ScrollPaneConstants;

/**
 * Panel for importing, exporting and updating database using xlsx and csv files
 */
public class ImportPanel extends JPanel implements ActionListener {
	private static final long serialVersionUID = 1L;
	
	static final String PERSISTENCE_SET = "test";
	private static final Logger LOG = Logger.getLogger(InstanceManager.class);
	
	public  static final String FILE_TYPE = "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet";
	JFileChooser fileChooser;
	FileNameExtensionFilter filter; // filter for jfilechooser
	JButton chooseFile, createTemplateXlsx, openTemplateXlsx, updateDatabase, exampleFile;
	static JButton openFile;
	JScrollPane scrollPane;			// scrollpane for log
	static JTextArea log;			// textarea for log (what the application is doing)
	File path;
	// JDBC driver name and database URL
	static final String JDBC_DRIVER = "org.h2.Driver";
	static final String DB_URL = "jdbc:h2:data/Student";
	// Database credentials
	static final String USER = "sa";
	static final String PASS = "";
	static final String DB_TABLE_NAME = "STUDENT";
	// Databse column names
	static final String UNIQUE_ID = "PHONE";				
	static final String[] DOUBLES = {"SWL","SAL","ELM","IWS","ELE","PUOU","DS","M2","OU","PU"};

	Statement stmt = null;
	Connection conn = null;

	public ImportPanel(){

		this.setLayout(null);

		fileChooser = new JFileChooser();								// creates a new JfileChooser
		fileChooser.setDialogTitle("Choose a file");					// sets jfilehooser name
		fileChooser.setPreferredSize(new Dimension(600, 500));			// sets jfilechooser size
		filter = new FileNameExtensionFilter("xlsx files", "xlsx");		// creates a new filter
		fileChooser.addChoosableFileFilter(filter);						// adds filter to dropdown
		fileChooser.setFileFilter(filter);								// sets filter as default
		try{
			File defaultDirectory = new File(new File(System.getProperty("user.home") + 
					System.getProperty("file.separator")).getCanonicalPath() );			// creates new directory path
			fileChooser.setCurrentDirectory(defaultDirectory);			// sets jfilechooser starting directory
		}catch(IOException e){
			System.out.println("Exeption while setting directory for JfileChooser\n"+e.getMessage());
		}
		fileChooser.setVisible(false);				// fileChooser is not visible
		this.add(fileChooser);						// adds filechooser to jpanel

		chooseFile = new JButton("Choose a file");				// creates a new button for choosing file
		chooseFile.setFont(new Font("Arial", Font.BOLD, 12));	// sets button font
		chooseFile.addActionListener(this);						// adds actionlistener to button
		chooseFile.setToolTipText("Choose a valid xlsx file");	// adds tooltip to button
		chooseFile.setBounds(50, 10, 190, 30);					// set location and size of button
		this.add(chooseFile);									// add button to jpanel

		JPanel panel = new JPanel();								// creates a new jpanel that will contain log
		panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));	// sets top-down box layout to jpanel
		log = new JTextArea();										// creates new jtextarea
		log.setFont(new Font("Arial", Font.PLAIN, 12));				// sets font for jtextarea
		log.setEditable(false);										// jtextarea is not editable
		log.setOpaque(false);										// background of jtextarea will be opaque
		log.setAlignmentX(JTextArea.LEFT_ALIGNMENT);				// text will be aligned to the left
		panel.setBorder(new EmptyBorder(10,10,10,10));				// creates an empty border for spacing
		panel.add(log);												// adds jtextara to jpanel

		scrollPane = new JScrollPane(panel,JScrollPane.VERTICAL_SCROLLBAR_ALWAYS,
				ScrollPaneConstants.HORIZONTAL_SCROLLBAR_ALWAYS);	// create a scrollpane with visible scrollbars
		scrollPane.setAlignmentX(JScrollPane.LEFT_ALIGNMENT);		// sets left aligment to components
		scrollPane.setBounds(370,11,360,255);						// sets location and size of jscrollpane
		this.add(scrollPane);										// adds jscrollpane to jpanel

		openFile = new JButton("Open");		// creates a  nw button for opening a file
		openFile.setToolTipText("<html>Opens the created .csv file with its default program<br>"+
				"(to change this program right click on any .csv file<br>and choose"+
				" the program you want to open it with)</html>"); // sets tooltip for jbutton
		openFile.setFont(new Font("Arial", Font.BOLD, 12));	// sets font for j button
		openFile.setBounds(250,10,110,30);					// sets location and size of jbutton
		openFile.addActionListener(this);					// adds actionlistener to jbutton
		openFile.setVisible(false);							// sets jbutton to not visible
		this.add(openFile);									// adds jbutton to jpanel

		createTemplateXlsx = new JButton("Create template");		// creates a  nw button for opening a file
		createTemplateXlsx.setToolTipText("Creates a new .xlsx file on your Desktop"); // sets tooltip for jbutton
		createTemplateXlsx.setFont(new Font("Arial", Font.BOLD, 12));	// sets font for j button
		createTemplateXlsx.setBounds(50,60,190,30);					// sets location and size of jbutton
		createTemplateXlsx.addActionListener(this);					// adds actionlistener to jbutton
		this.add(createTemplateXlsx);									// adds jbutton to jpanel

		openTemplateXlsx = new JButton("Open");		// creates a  nw button for opening a file
		openTemplateXlsx.setToolTipText("Opens the created template file"); // sets tooltip for jbutton
		openTemplateXlsx.setFont(new Font("Arial", Font.BOLD, 12));	// sets font for j button
		openTemplateXlsx.setBounds(250,60,110,30);					// sets location and size of jbutton
		openTemplateXlsx.setVisible(false);
		openTemplateXlsx.addActionListener(this);					// adds actionlistener to jbutton
		this.add(openTemplateXlsx);									// adds jbutton to jpanel

		updateDatabase = new JButton("Update database");			// creates a jbutton for updating database
		updateDatabase.setToolTipText("<html>Choose an xlsx file that will update database<br>"+
				" (see example first)</html>");// sets tooltip for jbutton
		updateDatabase.setFont(new Font("Arial", Font.BOLD, 12));   // sets font for jbutton
		updateDatabase.setBounds(50,110,190,30);						// sets location and size of jbutton
		updateDatabase.addActionListener(this);						// adds actionlistener to jbutton
		this.add(updateDatabase);									// adds jbutton to jpanel

		exampleFile = new JButton("Example");						// creates a jbutton for seeing example xlsx file
		exampleFile.setToolTipText("Opens an example xlsx file");	// sets tooltip for jbutton
		exampleFile.setFont(new Font("Arial", Font.BOLD, 12));		// sets font for jbutton
		exampleFile.setBounds(250,110,110,30);						// sets location and size of jbutton
		exampleFile.addActionListener(this);						// adds actionlistener to jbutton
		this.add(exampleFile);										// adds jbutton to janel

	}

	/**
	 * Function that opens a file with its default program
	 * @param file - path to file
	 */
	public static void openFile(File file){
		try
		{
			if(Desktop.isDesktopSupported()){			// if desktop class is suported on this platform
				Desktop desktop = Desktop.getDesktop(); // gets desktop instance of current broswer context
				if(file.exists()) desktop.open(file);	// if file exists open it with its default program
			}
		}catch( IOException ioe){
			System.out.println("Exception while opening file\n"+ioe.getMessage());
		}
	}
	/**
	 * Function for creating a csv file from a xlsx file
	 * @param inputFile - file path to an xlsx file
	 * @param outputFile - file path to a csv file
	 */
	public static void xlsxToCsv(File inputFile, File outputFile) throws Exception {
		// For storing data into CSV files
		StringBuffer data = new StringBuffer();
		try {			
			@SuppressWarnings("resource")
			XSSFWorkbook wBook = new XSSFWorkbook(new FileInputStream(inputFile));
			// Get first sheet from the workbook
			XSSFSheet sheet = null;
			// "Iterate" through each rows from first sheet
			// for each sheet in the workbook
			boolean found = false;
			for (int i = 0; i < wBook.getNumberOfSheets(); i++) {
				if(wBook.getSheetName(i).equals("detailed")){
					sheet = wBook.getSheetAt(i);
					found = true;
					break;
				}
			}
			if( found == false ){
				log.append("\"detailed\" sheet not found\n");
				throw new Exception("No sheet found exception");
			}
			else{
				FileOutputStream fos = new FileOutputStream(outputFile);
				//Values to skip and not write to the CSV file which will then be sent to database
				int skipLietotajvards = 1;	//Ignores Lietotajvards column
				int skipComments = 16;	//Jusu komentars column number set manually so it can be skipped
				int skipKursaID = 18;	//Kursa ID column number set manually so it can be skipped
				int skipKurss = 19;		//Kurss column number set manually so it can be skipped
				int date = skipKursaID -1;	//Assumes date column is the column just before Kursa ID column

				String dateString; //will hold the date cell as written in the excel file

				for(Row row : sheet){	 //while there are rows
					if(row.getRowNum()!=1){ 	//don't need to look at the second row
						for(int cn = 0; cn < row.getLastCellNum(); cn++) {		//while there are cells in the row
							if(cn != skipKursaID && cn != skipKurss && cn != skipComments && cn != skipLietotajvards){		//if the current cell is one we need
								Cell cell = row.getCell(cn, Row.CREATE_NULL_AS_BLANK); 		//takes the cell even if it's empty

								switch (cell.getCellType()) { //determines the type of the cell
								case Cell.CELL_TYPE_BOOLEAN:
									data.append(cell.getBooleanCellValue() + ",");
									break;
								case Cell.CELL_TYPE_NUMERIC:
									data.append(cell.getNumericCellValue() + ",");
									break;
								case Cell.CELL_TYPE_STRING:
									if(cn == date){
										dateString = cell.getStringCellValue();
										String dateFinal = new String();
										int dotCount = 0;
										String day = new String();
										for (int i = 0; i < dateString.length(); i++){
											char c = dateString.charAt(i);  
											if(dotCount < 2){
												if(c >= 48 && c <=57 && dotCount != 1){
													dateFinal += c;
												}
												else if(c >= 48 && c <=57){
													day += c;
												}
												else if(c == 46){
													dotCount++;
												}
											}
											else if(dateString.contains("January")){
												dateFinal += "-1-";
												dateFinal += day;
												break;
											}
											else if(dateString.contains("February")){
												dateFinal += "-2-";
												dateFinal += day;
												break;
											}
											else if(dateString.contains("March")){
												dateFinal += "-3-";
												dateFinal += day;
												break;
											}
											else if(dateString.contains("April")){
												dateFinal += "-4-";
												dateFinal += day;
												break;
											}
											else if(dateString.contains("May")){
												dateFinal += "-5-";
												dateFinal += day;
												break;
											}
											else if(dateString.contains("June")){
												dateFinal += "-6-";
												dateFinal += day;
												break;
											}
											else if(dateString.contains("July")){
												dateFinal += "-7-";
												dateFinal += day;
												break;
											}
											else if(dateString.contains("August")){
												dateFinal += "-8-";
												dateFinal += day;
												break;
											}
											else if(dateString.contains("September")){
												dateFinal += "-9-";
												dateFinal += day;
												break;
											}
											else if(dateString.contains("October")){
												dateFinal += "-10-";
												dateFinal += day;
												break;
											}
											else if(dateString.contains("November")){
												dateFinal += "-11-";
												dateFinal += day;
												break;
											}
											else if(dateString.contains("December")){
												dateFinal += "-12-";
												dateFinal += day;
												break;
											}
											else{
												dateFinal += "-00-";
												dateFinal += day;
												break;
											}
										}
										dotCount = 0;
										if(row.getRowNum() != 0)
											data.append(dateFinal + ",");
									}
									else if (row.getRowNum() != 0)
										data.append(cell.getStringCellValue() + ",");
									break;
								case Cell.CELL_TYPE_BLANK:
									if(row.getRowNum() > 1){
										data.append("No data" + ",");
										break;
									}
									else
										break;
								default:
									data.append(cell + ",");
								}
							}
						}
						if(row.getRowNum() != 0)
							data.append("\r\n"); //EXCEL RINDAS BEIGAS
					}
				}
				fos.write(data.toString().getBytes());
				fos.close();
				openFile.setVisible(true);
				log.append("Created file: "+outputFile.getName()+"\n");
			}
		} catch (Exception ioe) {
			ioe.printStackTrace();
		}

	}
	
	public static void writeToDatabase(File file){
		
	}
	/**
	 * Function for reading an excel file and saving its contents
	 * @param readFrom - path to a xlsx file
	 * @param excelData - ArrayList(ArrayList(String)) an ArrayList that contains ArrayLists of String values
	 */
	public static void readExcelFile( File readFrom, ArrayList<ArrayList<String>> excelData ){
		try {
			FileInputStream fis = new FileInputStream(readFrom);	// create the input stream from the xlsx file
			@SuppressWarnings("resource")
			Workbook workbook = new XSSFWorkbook(fis);				// create Workbook instance for xlsx file input stream
			Sheet sheet = workbook.getSheetAt(0);					// get the 1st sheet from the workbook
			Iterator<Row> rowIterator = sheet.iterator();			// every sheet has rows, iterate over them
			boolean firstIteration = true;							// for reading columns names
			while (rowIterator.hasNext()) {							// while there are rows to read from
				ArrayList<String> rows = new ArrayList<String>();	// arraylist for saving one row
				String columnName = null;
				String cellValue = null;

				Row row = rowIterator.next();						// get the row object
				Iterator<Cell> cellIterator = row.cellIterator();	//every row has columns, get the column iterator and iterate over them
				while (cellIterator.hasNext()) {
					Cell cell = cellIterator.next();			// get the Cell object
					switch(cell.getCellType()){					// check the cell type
					case Cell.CELL_TYPE_STRING:					// if celltype is string
					{
						if( firstIteration == true){						// if firstiteration
							columnName = cell.getStringCellValue().trim();	// get columnname
							rows.add(columnName);							// add to arraylist
						}
						else{
							cellValue = cell.getStringCellValue().trim();	// get cellValue
							rows.add(cellValue);							// add to arraylist
						}
						break;
					}
					case Cell.CELL_TYPE_NUMERIC:				// if celltype is numeric
					{
						long number = (long) cell.getNumericCellValue();	// cast value to long
						cellValue = String.valueOf(number);					// create string from long
						rows.add(cellValue);								// add to arraylist
						break;
					}
					}
				} //end of cell iterator
				firstIteration = false;							// after firstiteration is false
				if(rows.size() > 0){							// if arraylist of one row is not empty
					excelData.add(rows);						// add it to arraylist that contains all rows
				}
			} //end of rows iterator

		}
		catch (IOException e) {
			System.out.println("Exception while reading from xlsx file\n"+e.getMessage());
		}
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		if( e.getSource().equals(chooseFile) ){							// choosefile button is pressed
			fileChooser.setVisible(true);								// sets jfilechooser to visible
			int status = fileChooser.showDialog(null, "Choose File");	// gets state of jfilechooser
			if( status == JFileChooser.APPROVE_OPTION ){				// if open is pressed in jfilechooser
				new Thread() { 											// creates a new thread so processes execute consecutively
					public void run() {									// creates run method for thread
						try{
							final File file = fileChooser.getSelectedFile();		// gets selected files path
							String fileType = Files.probeContentType(file.toPath());// gets file type
							if( FILE_TYPE.equals(fileType) ){						// if file type is xlsx
								log.setText("Selected file: "+file.getName()+"\n");
								String fileName = file.getName();					// gets files name
								fileName = fileName.substring(0, fileName.indexOf(".")) + ".csv";	// changes extension to .csv
								File currentDirectory = new File(fileName);			// sets files location to current directory (directory project is in)

								log.append("Converting ...\n");
								long start = System.nanoTime();						// gets time before executing function
								try{
									xlsxToCsv(file, currentDirectory);
								}
								catch( Exception nse ){
									nse.printStackTrace();
								}

								try {
									log.append("Writing to database...\n");
									ModelManager.initModelManager(PERSISTENCE_SET);
									PrepareData.csv2db(currentDirectory);
									M2.getRegression("Video", "M2-video");
									M2.getRegression("Robotika", "M2-robotika");
									M2.getRegression("Mobilās tehnoloģijas", "M2-mobilas");
								} catch (Throwable t) {
									LOG.error(t.getMessage() + " " + t.getCause());
								}
								finally {
									// Close DB session
									ModelManager.closeModelManager();
									log.append("Writing complete\n");
								}
								long end = System.nanoTime();						// gets time after executing function
								long elapsedTime = end - start;						// gets execution time of function
								double seconds = (double)elapsedTime / 1000000000.0;// converts nanoseconds to seconds
								log.append("Execution time: "+seconds);
							}
							else{
								JOptionPane.showMessageDialog( null, "Invalid file type", "Warning !", JOptionPane.INFORMATION_MESSAGE );
							}
						} catch (IOException ioe) {
							System.out.println("Exception while determening file type\n" + ioe.getMessage());
						} 
					}
				}.start();	// starts thread
			}
			else if( status == JFileChooser.ERROR_OPTION ){
				JOptionPane.showMessageDialog( null, "File chooser error", "Warning !", JOptionPane.INFORMATION_MESSAGE );
			}
		}
		else if( e.getSource().equals(openFile) ){			// if openfile button is pressed
			File file = fileChooser.getSelectedFile();	// gets selected files path
			String fileName = file.getName();			// gets selected files name
			fileName = fileName.substring(0, fileName.indexOf(".")) + ".csv";	// changes files extension to .csv
			file = new File(fileName);					// creates new path in working directory
			PrepareData.csv2db(file);
			openFile(file);								// function that opens file with its default program
		}
		else if( e.getSource().equals(createTemplateXlsx) ){
			new Thread(){
				public void run(){
					try
					{
						long start = System.nanoTime();	// get system time
						log.setText("Creating template file...\n");
						@SuppressWarnings("resource")
						XSSFWorkbook workBook = new XSSFWorkbook();			// create Workbook instance for xlsx file
						XSSFSheet sheet = workBook.createSheet("Sheet1");	// create Sheet for xlsx file
						XSSFRow row = sheet.createRow(0);					// creates a new row
						Cell cell = row.createCell(0);						// creates a new cell
						cell.setCellValue(UNIQUE_ID);						// sets cell value
						cell = row.createCell(1);							// creates a new cell
						cell.setCellValue("OU");							// sets cell value

						row = sheet.createRow(1);
						cell = row.createCell(0);
						cell.setCellValue("Phone number");
						cell = row.createCell(1);
						cell.setCellValue("Value");

						sheet.autoSizeColumn(0); //adjust width of the first column
						sheet.autoSizeColumn(1); //adjust width of the second column

						File file = new File(System.getProperty("user.home") + "/Desktop/Update_Database.xlsx");
						FileOutputStream fileOut = new FileOutputStream(file);
						workBook.write(fileOut);
						fileOut.flush();
						fileOut.close();
						log.append("Template file created:\n");
						log.append(file.getPath()+"\n");
						long end = System.nanoTime();	// get system time
						long elapsedTime = end - start;
						double seconds = (double)elapsedTime / 1000000000.0; // converts nanoseconds to seconds
						log.append("Execution time: "+seconds+"\n");
						openTemplateXlsx.setVisible(true);
					}
					catch( IOException ioe){
						System.out.println("Input output exception\n"+ ioe.getMessage());
					}
				}
			}.start();
		}
		else if( e.getSource().equals(openTemplateXlsx) ){
			File file = new File(System.getProperty("user.home") + "/Desktop/Update_Database.xlsx");
			openFile(file);
		}
		else if( e.getSource().equals(updateDatabase) ){	// if updatedatabase button is pressed
			fileChooser.setVisible(true);					// set jfilechooser to visible
			int status = fileChooser.showDialog(null, "Choose File");	// gets state of jfilechooser
			if( status == JFileChooser.APPROVE_OPTION ){				// if open is pressed in jfilechooser
				final File file = fileChooser.getSelectedFile();		// gets selected files path
				new Thread() { 		// creates a new thread so processes execute consecutively
					public void run() {		// creates run method for thread
						try {
							String fileType = Files.probeContentType(file.toPath());	// gets chosen file type
							if( FILE_TYPE.equals(fileType) ){							// if fileType is xlsx
								log.setText("Selected file: "+file.getName()+"\n");
								try {
									// creates an arraylist of all rows that contains arraylists of one row
									ArrayList<ArrayList<String>> excelData = new ArrayList<ArrayList<String>>();
									log.append("Reading file... \n");
									readExcelFile(file, excelData);	// reads excelfile and saves it to arraylist
									log.append("File read successfully \n");

									long start = System.nanoTime();	// get system time before opening database

									Class.forName(JDBC_DRIVER);		// jdbc driver name							
									log.append("Connecting to database... \n");
									conn = DriverManager.getConnection(DB_URL, USER, PASS);	// establsih connection to database
									conn.setAutoCommit(false);		// sets autocommit to false
									log.append("Connected to database successfully \n");

									StringBuilder sb = new StringBuilder();				// creates a stringbuilder for column names
									for(int i = 0; i < excelData.get(0).size(); i++){	// repeats first row size times
										if( i == excelData.get(0).size()-1){			// if last iteration
											sb.append(excelData.get(0).get(i)+" ");		// add column names to string builder
										}
										else{											// if not last iteration
											sb.append(excelData.get(0).get(i)+", ");	// add column names to string builder
										}
									}
									String columnNames = new String(sb);				// make string with stringbuilder contents
									String sql = "SELECT "+ columnNames +"FROM "+DB_TABLE_NAME;	// make sql statement

									stmt = conn.createStatement();				// creates a new statement object
									ResultSet rs = stmt.executeQuery(sql);		// a table of data that is obtained by executing a sql statement
									conn.commit();								// makes changes to datbase permanent

									while( rs.next() ){											// while table has contents
										String number = rs.getString(excelData.get(0).get(0));  // columnname ( excelData.get(0).get(i) )
										if(number != null){										// if number is initialized
											for(int i= 0; i < excelData.size(); i++){			// amount of rows times
												if( number.equals(excelData.get(i).get(0))){	// number ( excelData.get(j).get(0)	)					            			
													for(int j = 1; j < excelData.get(0).size(); j++ ){	// (amount of columns - 1) times
														boolean isDouble = false;						// check if value is double
														for(String columnName : DOUBLES){					// iterator for DOUBLES array
															if(excelData.get(0).get(j).equals(columnName)){ // if column name matches one of DOUBLES array values
																isDouble = true;							// column contains double values
																break;										// stop for cycle
															}
														}
														if( isDouble == true ){								// if column contains double values
															// if number is in interval [1-5]
															if( Double.parseDouble(excelData.get(i).get(j)) >= 1 && Double.parseDouble(excelData.get(i).get(j)) <= 5 ){
																// creates sql statement fot inserting value into database
																String statement = "UPDATE "+DB_TABLE_NAME+" SET "+excelData.get(0).get(j)+
																		"='"+excelData.get(i).get(j)+"' WHERE "+UNIQUE_ID+
																		"='"+number+"'";
																stmt = conn.createStatement();				// creates a new statement object
																stmt.executeUpdate(statement);				// executes statement
																conn.commit();								// commits changes
															} 
															else{
																log.append("At: ["+number+"] value: ["+excelData.get(i).get(j)+"] Not in interval [1-5]\n");
															} 
														}
														else{
															String statement = "UPDATE "+DB_TABLE_NAME+" SET "+excelData.get(0).get(j)+
																	"='"+excelData.get(i).get(j)+"' WHERE "+UNIQUE_ID+
																	"='"+number+"'";
															stmt = conn.createStatement();
															stmt.executeUpdate(statement);
															conn.commit();
														}
													}// closes for statement ( amount of columns - 1 ) times
												}// closes if statment ( number equals )
											}// closes for statement ( amount of row ) times
										}// closes if statement ( number is not null )
									}// closes while statement ( table has contents )
									long end = System.nanoTime(); // get system time after actions with database are finished
									long elapsedTime = end - start; // gets elapsed time in nanoseconds
									double seconds = (double)elapsedTime / 1000000000.0; // converts nanoseconds to seconds
									log.append("Execution time: "+seconds+"\n");
								}catch (SQLException sqle) { //Handle errors for JDBC
									System.out.println("SQL exception\n"+sqle.getMessage());
								} catch (Exception e) { 	//Handle errors for Class.forName
									System.out.println("Unexpected exception\n"+e.getMessage());
								} finally {
									try {
										if(stmt!=null){
											stmt.close();
										}
										if (conn!=null)
											conn.close();
									} catch (SQLException sqle) {
										System.out.println("SQL exception\n"+sqle.getMessage());
									}
								}
							}
							else{
								JOptionPane.showMessageDialog( null, "Invalid file type", "Warning !", JOptionPane.INFORMATION_MESSAGE );
							}
						} catch (IOException ioe) {
							System.out.println("Exception while determening file type\n" + ioe.getMessage());
						}
					}
				}.start(); // starts thread
			}
			else if( status == JFileChooser.ERROR_OPTION ){
				JOptionPane.showMessageDialog( null, "File chooser error", "Warning !", JOptionPane.INFORMATION_MESSAGE );
			}
		}
		else if(e.getSource().equals(exampleFile)){
			File file = new File("resources/example.xlsx");	// creates file path to example.xlsx file
			openFile(file);									// function that opens file with its default program
		}

	}
}