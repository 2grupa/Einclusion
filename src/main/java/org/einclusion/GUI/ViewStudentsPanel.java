package org.einclusion.GUI;

import javax.swing.*;
import java.awt.*;
import java.sql.*;

import java.util.ArrayList;
import java.util.TreeSet;
import java.util.Vector;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableModel;

import org.einclusion.frontend.RegressionModel;

/**
 * Panel for a table view of Database table with calculated student e-inclusion degrees. 
 */
public class ViewStudentsPanel extends JPanel implements ActionListener {
	private static final long serialVersionUID = 1L;
	// JDBC driver name and database URL
    static final String JDBC_DRIVER = "org.h2.Driver";
    static final String DB_PATH = "data/Studenti";
    static final String DB_URL = "jdbc:h2:" + DB_PATH;
    //  Database credentials
    static final String USER = "sa";
    static final String PASS = "";
    static final String DB_TABLE_NAME = "STUDENTI";
    static final String DB_REGRESSION_TABLE = "MODELMANAGER";
	
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
		gbl_panel.columnWidths = new int[]{202, 0, 42, 0, 0};
		gbl_panel.rowHeights = new int[]{15, 0};
		gbl_panel.columnWeights = new double[]{0.0, 1.0, 1.0, 0.0, Double.MIN_VALUE};
		gbl_panel.rowWeights = new double[]{0.0, Double.MIN_VALUE};
		panel.setLayout(gbl_panel);
		
		JLabel lblNewLabel = new JLabel("Filter:");
		GridBagConstraints gbc_lblNewLabel = new GridBagConstraints();
		gbc_lblNewLabel.anchor = GridBagConstraints.EAST;
		gbc_lblNewLabel.insets = new Insets(0, 0, 0, 5);
		gbc_lblNewLabel.gridx = 0;
		gbc_lblNewLabel.gridy = 0;
		panel.add(lblNewLabel, gbc_lblNewLabel);
		
		comboBox_1 = new JComboBox<String>();
		GridBagConstraints gbc_comboBox_1 = new GridBagConstraints();
		gbc_comboBox_1.fill = GridBagConstraints.HORIZONTAL;
		gbc_comboBox_1.insets = new Insets(0, 0, 0, 5);
		gbc_comboBox_1.gridx = 1;
		gbc_comboBox_1.gridy = 0;
		panel.add(comboBox_1, gbc_comboBox_1);
		
		comboBox_2 = new JComboBox<>();
		GridBagConstraints gbc_comboBox_2 = new GridBagConstraints();
		gbc_comboBox_2.insets = new Insets(0, 0, 0, 5);
		gbc_comboBox_2.fill = GridBagConstraints.HORIZONTAL;
		gbc_comboBox_2.gridx = 2;
		gbc_comboBox_2.gridy = 0;
		panel.add(comboBox_2, gbc_comboBox_2);
		
		add(panel, BorderLayout.NORTH);
		
		btnApply = new JButton("Apply");
		btnApply.addActionListener(this);
		GridBagConstraints gbc_btnApply = new GridBagConstraints();
		gbc_btnApply.gridx = 3;
		gbc_btnApply.gridy = 0;
		panel.add(btnApply, gbc_btnApply);
		
		tableModel = new DefaultTableModel() {
			public boolean isCellEditable(int row, int column) {
		      return false; //This causes all cells to be not editable.
		    }
		};
		
		table = new JTable();
		prepareTable();
		table.setAutoCreateRowSorter(true);
		table.getTableHeader().setReorderingAllowed(false);
		table.setDefaultRenderer(Object.class, new MyRenderer());
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
			String M2risk) {
		
		Vector<String> v = new Vector<>();
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
		v.add(String.format("%.2f",Double.parseDouble(M2risk)));
		
		if (v.size() == tableModel.getColumnCount())
			tableModel.addRow(v);
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
	            
	            if (colVal.equals("NAME")||colVal.equals("TEMA")) {
	            	while (rs.next()) {
	            		Clob clob = rs.getClob(colVal);
	            		ts.add(clob.getSubString(1, (int)clob.length()));
	            	}
	            } else if (colVal.equals("IZPILDITS")) {
	            	 while (rs.next()) {
						Date dateStamp = new Date(rs.getTimestamp(colVal).getTime());
						ts.add(dateStamp.toString());
	            	 }
	            } else if (colVal.equals("NUMURS")) {
	            	while (rs.next())
	            		ts.add(rs.getLong(colVal)+"");
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
	            		sql += "3";
	            		break;
	            	case "Orange":
	            		sql += "1.25" + " AND " + colName + " <= " + "3";
	            		break;
	            	case "Red":
	            		sql += "-0.1" + " AND " + colName + " <= " + "1.25";
	            		break;
            	}
            	sql += " ORDER BY NAME";
            	pStmt = conn.prepareStatement(sql);
            } else {
            	sql = "SELECT * FROM " + DB_TABLE_NAME + " WHERE NAME IS NOT 'test' AND "+colName+" = ? ORDER BY NAME";
            	pStmt = conn.prepareStatement(sql);
            	if (colName.equals("NAME") || colName.equals("TEMA") || colName.equals("IZPILDITS"))
            		pStmt.setString(1, value);
            	else
            		pStmt.setDouble(1, Double.parseDouble(value));
            }
            ResultSet rs = pStmt.executeQuery();
            conn.commit();
            
            tableModel.setRowCount(0); // clears table contents
            while (rs.next()) {
            	String ID = rs.getBigDecimal("NUMURS") + "";
            	Clob clob = rs.getClob("NAME");
            	String name = clob.getSubString(1, (int)clob.length());
            	clob = rs.getClob("TEMA");
            	String course = clob.getSubString(1, (int)clob.length());
            	Date dateStamp = new Date(rs.getTimestamp("IZPILDITS").getTime());
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
            			// TODO: M2 risk calculation - next line - remove.
            			if (Double.parseDouble(m2risk)==0)
            				m2risk = RegressionModel.getM2regressionDegree(rm, Double.parseDouble(mot), 
            						Double.parseDouble(learnCpcty), Double.parseDouble(eRsrc), 
            						Double.parseDouble(instr), Double.parseDouble(eEnvrn))+"";
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
				shortCol = "NUMURS";
				break;
	    	case "Name":
	    		shortCol = "NAME";
	    		break;
	    	case "Topic":
	    		shortCol = "TEMA";
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
	    		shortCol = "IZPILDITS";
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
	
	@Override
	public void actionPerformed(ActionEvent e) {
		if (e.getSource().equals(comboBox_1)) {
			// Generates combobox_2 values; checks unique values in table where: column = combobox_1.selectedItem
			comboBox2Generate();
		} else if (e.getSource().equals(btnApply)) {
			// makes the table show only filter appropriate items.
			readDBfiltered(comboBox_1.getSelectedItem().toString(), comboBox_2.getSelectedItem().toString());
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
		        double columnValue = Double.parseDouble((String)model.getValueAt(modelRow, column))/5d;
				
		        if (columnValue > 0.6)
					c.setBackground(new Color(103, 235, 103));//Color.green);
				else if (columnValue > 0.25)
					c.setBackground(Color.orange);
				else
					c.setBackground(new Color(235, 69, 69));//Color.red);
			} else
				c.setBackground(new JButton().getBackground());
			this.setHorizontalAlignment( JLabel.CENTER );
			
			return c;
		}

	}	
}
