package org.einclusion.GUI;

import javax.swing.*;

import java.awt.*;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.sql.*;
import java.util.ArrayList;
import java.util.TreeSet;
import java.util.Vector;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;

import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableModel;

import org.apache.poi.xssf.usermodel.XSSFCell;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.einclusion.frontend.RegressionModel;

/**
 * Panel for a table view of Database table with calculated student e-inclusion degrees. 
 */
public class ViewStudentsPanel extends JPanel implements ActionListener, KeyListener {
	private static final long serialVersionUID = 1L;
	// JDBC driver name and database URL
    static final String JDBC_DRIVER = "org.h2.Driver";
    static final String DB_PATH = "data/Student";
    static final String DB_URL = "jdbc:h2:" + DB_PATH;
    //  Database credentials
    static final String USER = "sa";
    static final String PASS = "";
    static final String DB_TABLE_NAME = "STUDENT";
    static final String DB_REGRESSION_TABLE = "MODELMANAGER";
	
    public static ArrayList<ArrayList<String>> list = new ArrayList<ArrayList<String>>();
	static final String[] COLUMNS = {"PHONE","NAME","TOPIC","SWL","DS","ELM","ELE","IWS","SAL","SUBMITDATE","M2"};
	JButton writeToXlsx;
	JTextField fieldForInput;
    
	JTable table;
	DefaultTableModel tableModel;
	JComboBox<String> comboBox_1;
	JComboBox<String> comboBox_2;
	JButton btnApply;
	Connection conn = null;
    PreparedStatement pStmt = null;
    ArrayList<RegressionModel> regressionModels = new ArrayList<>();
    
	@SuppressWarnings("serial")
	public ViewStudentsPanel() {
		setVisible(true);
		setBackground(Color.gray);
		setLayout(new BorderLayout(0, 0));
		
		JScrollPane scrollPane = new JScrollPane();
		add(scrollPane, BorderLayout.CENTER);
		
		JPanel panel = new JPanel();
		GridBagLayout gbl_panel = new GridBagLayout();
		gbl_panel.columnWidths = new int[] { 100, 100, 100, 150, 140, 100 };
		gbl_panel.rowHeights = new int[] { 30, 0 };
		gbl_panel.columnWeights = new double[] { 0.5, 0.0, 1.0, 1.0, 0.0, Double.MIN_VALUE };
		gbl_panel.rowWeights = new double[] { 0.0, Double.MIN_VALUE };
		panel.setLayout(gbl_panel);
		
		fieldForInput = new JTextField("Students");					// creates a jbutton for user input
		fieldForInput.setToolTipText("Enter a file name without the extension");
		fieldForInput.setFont(new Font("Arial", Font.BOLD, 12));	// sets fonr for jTextField
		GridBagConstraints gbc_input =	new GridBagConstraints();
		gbc_input.fill = GridBagConstraints.HORIZONTAL;
		gbc_input.insets = new Insets(0, 10, 0, 10);
		gbc_input.gridx = 0;
		gbc_input.gridy = 0;
		gbc_input.ipady = 6;
		fieldForInput.addKeyListener(this); 						// add keylistener to jtextfield
		panel.add(fieldForInput, gbc_input);
		
		writeToXlsx = new JButton("Export to xlsx");
		GridBagConstraints gbc_export =	new GridBagConstraints();
		writeToXlsx.setToolTipText("Exports .xlsx file to Desktop");	// sets tooltip for jbutton
		writeToXlsx.setFont(new Font("Arial", Font.BOLD, 12));					// sets font for jbutton
		gbc_export.anchor = GridBagConstraints.WEST;
		gbc_export.insets = new Insets(0, 0, 0, 5);
		gbc_export.gridx = 1;
		gbc_export.gridy = 0;
		writeToXlsx.addActionListener(this);						// adds actionlistener to jbutton
		panel.add(writeToXlsx, gbc_export);
		
		JLabel lblNewLabel = new JLabel("Filter:");
		GridBagConstraints gbc_lblNewLabel = new GridBagConstraints();
		gbc_lblNewLabel.anchor = GridBagConstraints.EAST;
		gbc_lblNewLabel.insets = new Insets(0, 0, 0, 5);
		gbc_lblNewLabel.gridx = 2;
		gbc_lblNewLabel.gridy = 0;
		panel.add(lblNewLabel, gbc_lblNewLabel);

		comboBox_1 = new JComboBox<String>();
		GridBagConstraints gbc_comboBox_1 = new GridBagConstraints();
		gbc_comboBox_1.fill = GridBagConstraints.HORIZONTAL;
		gbc_comboBox_1.insets = new Insets(0, 0, 0, 5);
		gbc_comboBox_1.gridx = 3;
		gbc_comboBox_1.gridy = 0;
		panel.add(comboBox_1, gbc_comboBox_1);

		comboBox_2 = new JComboBox<>();
		GridBagConstraints gbc_comboBox_2 = new GridBagConstraints();
		gbc_comboBox_2.insets = new Insets(0, 0, 0, 5);
		gbc_comboBox_2.fill = GridBagConstraints.HORIZONTAL;
		gbc_comboBox_2.gridx = 4;
		gbc_comboBox_2.gridy = 0;
		panel.add(comboBox_2, gbc_comboBox_2);

		add(panel, BorderLayout.NORTH);

		btnApply = new JButton("Apply");
		btnApply.addActionListener(this);
		GridBagConstraints gbc_btnApply = new GridBagConstraints();
		gbc_btnApply.insets = new Insets(0, 0, 0, 10);
		gbc_btnApply.fill = GridBagConstraints.HORIZONTAL;
		gbc_btnApply.gridx = 5;
		gbc_btnApply.gridy = 0;
		panel.add(btnApply, gbc_btnApply);
		
		tableModel = new DefaultTableModel() {
			public boolean isCellEditable(int row, int column) {
		      return false; //This causes all cells to be not editable.
		    }
			@Override
		    public Class<?> getColumnClass(int columnIndex) {
				if (tableModel.getRowCount()==0)
					return Object.class;
				else if (columnIndex == 10)
					return Double.class;
				else
					return getValueAt(0, columnIndex).getClass();
		    }
		};
		
		table = new JTable();
		prepareTable();
		table.setAutoCreateRowSorter(true);
		table.getTableHeader().setReorderingAllowed(false);
		table.setDefaultRenderer(Double.class, new MyRenderer());
		scrollPane.setViewportView(table);		
	}

	/**
	 * Adds a line to the GUI table which represents a single survey
	 * @param ID — identifier (phone number) of the Student
	 * @param name — name of the Student
	 * @param course - name of the Topic the survey was performed on
	 * @param motivation - coefficient value of SWL (students willingness to learn)
	 * @param digSkills - coefficient value of DS (digital skills)
	 * @param eRsrce - coefficient value of ELM (e-learning materials)
	 * @param eEnvrnmnt - coefficient value of ELE (e-learning environment)
	 * @param instructor - coefficient value of IWS (instructors willingness to share knowledge)
	 * @param learningCpcty - coefficient value of SAL (students ability to learn)
	 * @param date - date of when the survey was performed
	 * @param M2risk - value of students e-inclusion risk degree calculated using regression method #2
	 */
	void addTableLine(String ID, String name, String course, String motivation, String digSkills, 
			String eRsrce, String eEnvrnmnt, String instructor, String learningCpcty, String date, 
			String m2risk) {
		if (!motivation.equals(""))
			motivation = String.format("%.2f",Double.parseDouble(motivation));
		if (!digSkills.equals(""))
			digSkills = String.format("%.2f",Double.parseDouble(digSkills));
		if (!eRsrce.equals(""))
			eRsrce = String.format("%.2f",Double.parseDouble(eRsrce));
		if (!eEnvrnmnt.equals(""))
			eEnvrnmnt = String.format("%.2f",Double.parseDouble(eEnvrnmnt));
		if (!instructor.equals(""))
			instructor = String.format("%.2f",Double.parseDouble(instructor));
		if (!learningCpcty.equals(""))
			learningCpcty = String.format("%.2f",Double.parseDouble(learningCpcty));
		Vector<Object> v = new Vector<>();
		v.add(ID);
		v.add(name);
		v.add(course);
		v.add(motivation);
		v.add(digSkills);
		v.add(eRsrce);
		v.add(eEnvrnmnt);
		v.add(instructor);
		v.add(learningCpcty);
		v.add(date);
		v.add(round(Double.parseDouble(m2risk),2));
		
		if (v.size() == tableModel.getColumnCount()){
			tableModel.addRow(v);
			
			ArrayList<String> row = new ArrayList<String>();
			row.add(ID); 		
			row.add(name);		
			row.add(course);	
			row.add(motivation);
			row.add(digSkills); 
			row.add(eRsrce);	
			row.add(eEnvrnmnt);	
			row.add(instructor);
			row.add(learningCpcty);
			row.add(date);		
			row.add(round(Double.parseDouble(m2risk),2)+"");	
			if( row.size() > 0)
				list.add(row);
			
		}
	}
	/**
	 * Sets up table and initial filter field values. Prepares table model and sets column header. 
	 */
	void prepareTable() {
		tableModel.addColumn("ID");
		tableModel.addColumn("Name");
		tableModel.addColumn("Topic");
		tableModel.addColumn("Motivation");
		tableModel.addColumn("Digital skills");
		tableModel.addColumn("E-resources");
		tableModel.addColumn("E-environment");
		tableModel.addColumn("Instructor");
		tableModel.addColumn("Learning ability");
		tableModel.addColumn("Submit date");
		tableModel.addColumn("M2");
		
		table.setModel(tableModel);
		
		comboBox_1.removeAllItems();
		comboBox_1.addItem("All");
		comboBox_1.setSelectedItem("All");
		for (int i=0; i<tableModel.getColumnCount(); i++)
			comboBox_1.addItem(tableModel.getColumnName(i));
		comboBox_1.addActionListener(this);
		
		comboBox2Generate();
	}
	
	/**
	 * Generates items for comboBox_2 by adding unique values selected column (selected item in comboBox_1)
	 */
	void comboBox2Generate() {
		TreeSet<String> ts = new TreeSet<>();
		String colVal = null;
		colVal = comboBox_1.getSelectedItem().toString();
		colVal = getShortForColumn(colVal);
		
		if (!colVal.equals("*")) {
			try {
	            Class.forName(JDBC_DRIVER);
	            System.out.println("Connecting to a selected database...");
	            conn = DriverManager.getConnection(DB_URL, USER, PASS);
	            conn.setAutoCommit(false);
	            System.out.println("Connected to database successfully...");
	            String sql = "SELECT * FROM "+ DB_TABLE_NAME + " WHERE NAME IS NOT 'test'";
	            pStmt = conn.prepareStatement(sql);
	            ResultSet rs = pStmt.executeQuery();
	            conn.commit();
	            
	            if (colVal.equals("NAME")||colVal.equals("TOPIC")) {
	            	while (rs.next()) {
	            		ts.add(rs.getString(colVal));
	            	}
	            } else if (colVal.equals("SUBMITDATE")) {
	            	 while (rs.next()) {
						Date dateStamp = new Date(rs.getTimestamp(colVal).getTime());
						ts.add(dateStamp.toString());
	            	 }
	            } else if (colVal.equals("PHONE")) {
	            	while (rs.next())
	            		ts.add(rs.getString(colVal));
	            } else {
		            while (rs.next())
		            	ts.add(rs.getDouble(colVal)+"");
	            }
	            if (colVal.equals("M2")) {
	            	ts.add("Green");
	            	ts.add("Orange");
	            	ts.add("Red");
	            }
	        } catch (SQLException se) { //Handle errors for JDBC
	            se.printStackTrace();
	        } catch (Exception e) { 	//Handle errors for Class.forName
	            e.printStackTrace();
	        } finally {
	            try {
	                if (pStmt!=null)
	                    conn.close();
	            } catch (SQLException se) {}
	            try {
	                if (conn!=null)
	                    conn.close();
	            } catch (SQLException se) {
	                se.printStackTrace();
	            }
	        }
		}
		comboBox_2.removeAllItems();
		comboBox_2.addItem("All");
		
		if (ts.size()>0) {
			for (String s: ts)
				comboBox_2.addItem(s);
		}
	}
	/**
	 * Reads specific filtered data from database.
	 * @param colName - column name
	 * @param value - row value in selected column
	 */
	void readDBfiltered(String colName, String value) {
		
		try { 
            Class.forName(JDBC_DRIVER);
            System.out.println("Connecting to a selected database...");
            conn = DriverManager.getConnection(DB_URL, USER, PASS);
            conn.setAutoCommit(false);
            System.out.println("Connected database successfully...");
            
            prepareRegressionModels(); // connects to DB, reads MODELMANAGER table.
            
            colName = getShortForColumn(colName);
            String sql;
            if (colName.equals("*") || value.equals("All")) {
            	sql = "SELECT * FROM " + DB_TABLE_NAME + " WHERE NAME IS NOT 'test'" + " ORDER BY NAME";
            	pStmt = conn.prepareStatement(sql);
            } else if (colName.equals("M2") && (value.equals("Green") || value.equals("Orange") || value.equals("Red") )) {
            	sql = "SELECT * FROM " + DB_TABLE_NAME + " WHERE NAME IS NOT 'test' AND "+colName+" > ";
            	switch (value) {
	            	case "Green":
	            		sql += "60";
	            		break;
	            	case "Orange":
	            		sql += "25" + " AND " + colName + " <= " + "60";
	            		break;
	            	case "Red":
	            		sql += "-0.1" + " AND " + colName + " <= " + "25";
	            		break;
            	}
            	sql += " ORDER BY NAME";
            	pStmt = conn.prepareStatement(sql);
            } else {
            	sql = "SELECT * FROM " + DB_TABLE_NAME + " WHERE NAME IS NOT 'test' AND "+colName+" = ? ORDER BY NAME";
            	pStmt = conn.prepareStatement(sql);
            	if (colName.equals("NAME") || colName.equals("TOPIC") || colName.equals("SUBMITDATE"))
            		pStmt.setString(1, value);
            	else
            		pStmt.setDouble(1, Double.parseDouble(value));
            }
            ResultSet rs = pStmt.executeQuery();
            conn.commit();
            
            list.clear(); 				// clears the arraylist
            
            tableModel.setRowCount(0); // clears table contents
            while (rs.next()) {
            	String ID = rs.getBigDecimal("PHONE") + "";
            	String name = rs.getString("NAME");
            	String course = rs.getString("TOPIC");
            	Date dateStamp = new Date(rs.getTimestamp("SUBMITDATE").getTime());
            	String date = dateStamp.toString();
            	String mot = rs.getDouble("SWL") + "";
            	String digSkills = rs.getDouble("DS") + "";
            	String eRsrc = rs.getDouble("ELM") + "";
            	String eEnvrn = rs.getDouble("ELE") + "";;
            	String instr = rs.getDouble("IWS") + "";
            	String learnCpcty = rs.getDouble("SAL") + "";
            	String m2risk = rs.getDouble("M2") + "";        	
            	
            	String regressionModel = getRegressionModel(course);
            	for (RegressionModel rm: regressionModels) {
            		if (rm.key.equals(regressionModel)) {
            			mot = rm.hasCoefficientValue("SWL") ? mot : "";
            			digSkills = rm.hasCoefficientValue("DS") ? digSkills : "";
            			eRsrc = rm.hasCoefficientValue("ELM") ? eRsrc : "";
            			eEnvrn = rm.hasCoefficientValue("ELE") ? eEnvrn : "";
            			instr = rm.hasCoefficientValue("IWS") ? instr : "";
            			learnCpcty = rm.hasCoefficientValue("SAL") ? learnCpcty : "";
            		}
            	}
            	
            	addTableLine(ID, name, course, mot, digSkills, eRsrc, eEnvrn, instr, learnCpcty, date, m2risk);
            }
        } catch (SQLException se) { //Handle errors for JDBC
            se.printStackTrace();
        } catch (Exception e) { 	//Handle errors for Class.forName
            e.printStackTrace();
        } finally {
            try {
                if (pStmt!=null)
                    conn.close();
            } catch (SQLException se) {}
            try {
                if (conn!=null)
                    conn.close();
            } catch (SQLException se) {
                se.printStackTrace();
            }
        }
	}
	
	/**
	 * Returns short form (abbreviation) of a column header to match with its database header name.
	 * @param colName - column name
	 * @return String - abbreviation of colName
	 */
	String getShortForColumn(String colName) {
		String shortCol = null;
		
		switch (colName) {
			case "ID":
				shortCol = "PHONE";
				break;
	    	case "Name":
	    		shortCol = "NAME";
	    		break;
	    	case "Topic":
	    		shortCol = "TOPIC";
	    		break;
	    	case "Motivation":
	    		shortCol = "SWL";
	    		break;
	    	case "Digital skills":
	    		shortCol = "DS";
	    		break;
	    	case "E-resources":
	    		shortCol = "ELM";
	    		break;
	    	case "E-environment":
	    		shortCol = "ELE";
	    		break;
	    	case "Instructor":
	    		shortCol = "IWS";
	    		break;
	    	case "Learning ability":
	    		shortCol = "SAL";
	    		break;
	    	case "Submit date":
	    		shortCol = "SUBMITDATE";
	    		break;
	    	case "PO Usage":
	    		shortCol = "PUOU";
	    		break;
	    	case "M2":
	    		shortCol = "M2";
	    		break;
			default : // All
				shortCol = "*";
				break;
		}
		return shortCol;
	}
	
	/**
	 * Returns the correct regression model name of the given course.
	 * @param course (String) - name of course
	 * @return (String) if model for given course found, NULL if course not recognized. 
	 */
	String getRegressionModel(String course) {
		String regressionModel = null;
		switch (course) {
			case "Robotika":
				regressionModel = "M2-robotika";
				break;
			case "Video":
				regressionModel = "M2-video";
				break;
			case "Mobilās tehnoloģijas":
				regressionModel = "M2-mobilas";
				break;
			default:
				break;
		}
		return regressionModel;
	}
	
	/**
	 * Reads MODELMANAGER table from database and saves, formats its elements in static variable RegressionModel ArrayList.
	 * Database connection must be open beforehand.
	 */
	void prepareRegressionModels() {
		try {
            String sql = "SELECT * FROM "+ DB_REGRESSION_TABLE;
            pStmt = conn.prepareStatement(sql);
            ResultSet rs = pStmt.executeQuery();
            conn.commit();
            
            regressionModels.clear();
			while (rs.next()) {
				regressionModels.add(new RegressionModel(rs.getString("key"), rs.getString("value")));
			}
        } catch (SQLException se) { //Handle errors for JDBC
            se.printStackTrace();
        } catch (Exception e) { 	//Handle errors for Class.forName
            e.printStackTrace();
        }
	}
	
	public static double round(double value, int places) {
		if (places < 0)
			throw new IllegalArgumentException();
		long factor = (long) Math.pow(10, places);
		value = value * factor;
		long tmp = Math.round(value);
		return (double) tmp/factor;
	}
	
	@Override
	public void actionPerformed(ActionEvent e) {
		if (e.getSource().equals(comboBox_1)) {
			// Generates combobox_2 values; checks unique values in table where: column = combobox_1.selectedItem
			comboBox2Generate();
		} else if (e.getSource().equals(btnApply)) {
			// makes the table show only filter appropriate items.
			readDBfiltered(comboBox_1.getSelectedItem().toString(), comboBox_2.getSelectedItem().toString());
		} else if (e.getSource().equals(writeToXlsx)) {
			
			if( list.size() > 0 && fieldForInput.getText().length() > 0 ){
				@SuppressWarnings("resource")
				XSSFWorkbook wb = new XSSFWorkbook();			// create Workbook instance for xlsx file
				XSSFSheet sheet = wb.createSheet("detailed");	// create Sheet for xlsx file
				
				XSSFRow row = sheet.createRow(1); 				// creates a new row in 2nd row of the file
				XSSFCell cell;									// cell object
				
				for(int i = 0; i < COLUMNS.length; i++){
					cell = row.createCell(i);				// creates a new cell in i column
					cell.setCellValue(COLUMNS[i]);		// sets cell value to column name from database
				}
				int counter = 0;
				for(int i = 2; i < list.size()+2; i++){	// iterates rows times
					row = sheet.createRow(i);			// creates a new row 
					
					for(int j = 0; j < list.get(0).size(); j++){		// iterates columns times
						cell = row.createCell(j);					// creates a new cell
						cell.setCellValue(list.get(counter).get(j));	// sets cell value
					}
					counter++;
				}
				
				for(int i = 0; i < list.get(0).size(); i++){
					sheet.autoSizeColumn(i);
				}
				
				try
				{
					File file = new File(System.getProperty("user.home") + "/Desktop/" + fieldForInput.getText() + ".xlsx");
					FileOutputStream fileOut = new FileOutputStream(file);
					wb.write(fileOut);	// write to file
					fileOut.flush();	// clears bytes from output stream
					fileOut.close();	// closes outputstream
				}
				catch( IOException ioe ){
					System.out.println("Input output exception\n"+ ioe.getMessage());
				}
			}
		}
	}
	
	/**
	 * Creates custom CellRenderer class which changes cell background color for column 'M2'.
	 * Colors are: GREEN, ORANGE or RED, respectively 100-61, 60-26, 25-0% value in column M2.
	 * Compatible with column sorting.
	 * @author student
	 */
	@SuppressWarnings("serial")
	private class MyRenderer extends DefaultTableCellRenderer {
				
		public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, 
				boolean hasFocus, int row, int column) {
			
			Component c = super.getTableCellRendererComponent(table, value,
					isSelected, hasFocus, row, column);
			
			if (column == 10) {
	        	TableModel model = table.getModel();
		        int modelRow = table.getRowSorter().convertRowIndexToModel(row);
		        double columnValue = (Double)model.getValueAt(modelRow, column);
				if (columnValue > 60)
					c.setBackground(new Color(103, 235, 103));//Color.green);
				else if (columnValue > 25)
					c.setBackground(Color.orange);
				else
					c.setBackground(new Color(235, 69, 69));//Color.red);
			} else
				c.setBackground(new JButton().getBackground());
			this.setHorizontalAlignment( JLabel.CENTER );
			
			return c;
		}
	}

	@Override
	public void keyPressed(KeyEvent arg0) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void keyReleased(KeyEvent e) {
		if(e.getSource().equals(fieldForInput)){
			String line;
			if( e.getKeyCode()!=KeyEvent.VK_DELETE && e.getKeyCode()!=KeyEvent.VK_BACK_SPACE && e.getKeyCode()!=KeyEvent.VK_CONTROL
					&& e.getKeyCode()!=KeyEvent.VK_SHIFT && e.getKeyCode()!=KeyEvent.VK_CAPS_LOCK && e.getKeyCode()!=KeyEvent.VK_LEFT
					&& e.getKeyCode()!=KeyEvent.VK_UP && e.getKeyCode()!=KeyEvent.VK_RIGHT && e.getKeyCode()!=KeyEvent.VK_DOWN&& e.getKeyCode()!=KeyEvent.VK_KP_LEFT
					&& e.getKeyCode()!=KeyEvent.VK_KP_UP && e.getKeyCode()!=KeyEvent.VK_KP_RIGHT && e.getKeyCode()!=KeyEvent.VK_KP_DOWN){ 
				// ignores backspace, delete, shift, caps lock, control and arrows
				if( !((e.getKeyChar()<=122 && e.getKeyChar()>=97) || (e.getKeyChar()<=90 && e.getKeyChar()>=65) || (e.getKeyChar()<=57 && e.getKeyChar()>=48) 
						|| e.getKeyChar()==95) ){ 	// if char is not a-z or A-Z or 0-9 then delete it from JTextField
					if( fieldForInput.getText().contains(""+e.getKeyChar()) ){			// if textfield contains illegal char
						line = fieldForInput.getText().replaceAll("[\\"+e.getKeyChar()+"]+", "");// removes all illegal chars from string
						fieldForInput.setText(line);									// sets substring without illegal characters to jtextfield
					}
				}
			}
		}
	}

	@Override
	public void keyTyped(KeyEvent arg0) {
		// TODO Auto-generated method stub
		
	}
}
