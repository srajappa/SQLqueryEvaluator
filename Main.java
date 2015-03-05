package edu.buffalo.cse562;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import net.sf.jsqlparser.expression.DateValue;
import net.sf.jsqlparser.expression.DoubleValue;
import net.sf.jsqlparser.expression.LeafValue;
import net.sf.jsqlparser.expression.LeafValue.InvalidLeaf;
import net.sf.jsqlparser.expression.StringValue;
import net.sf.jsqlparser.parser.CCJSqlParser;
import net.sf.jsqlparser.parser.ParseException;
import net.sf.jsqlparser.schema.Table;
import net.sf.jsqlparser.statement.Statement;
import net.sf.jsqlparser.statement.create.table.ColumnDefinition;
import net.sf.jsqlparser.statement.create.table.CreateTable;
import net.sf.jsqlparser.statement.select.PlainSelect;
import net.sf.jsqlparser.statement.select.Select;
import net.sf.jsqlparser.statement.select.SelectBody;

public class Main{
	
	public static void main(String[] args){
		long startTime = System.currentTimeMillis();
		
		HashMap<String, List<ColumnDefinition>> schema = new HashMap<String, List<ColumnDefinition>>();
		try {
			File dataDir = null;
			ArrayList<File> fileList = new ArrayList<File>();
			for (int i=0; i<args.length; i++){
				if (args[i].equalsIgnoreCase("--data")){
					dataDir = new File(args[i+1]);	//Stores the directory value
					i++;		//continue with the loop
				}else{
					fileList.add(new File(args[i])); // Stores the file path
				}
			}
		

			for (File inputFile : fileList){
				BufferedReader reader;
				reader = new BufferedReader(new FileReader(inputFile));
				CCJSqlParser parser = new CCJSqlParser(reader);
				Statement statement;


			
				while((statement = parser.Statement()) != null){
					// Select Statement
					if(statement instanceof Select){
						//////////////////////////////////////////////////////System.out.println("Select Statement");
						Select selectStatement = (Select) statement;
						
						SelectBody selectBody = selectStatement.getSelectBody();
						
						
						ParseTree parseTree = new ParseTree( dataDir, schema);
						
						parseTree.executeQuery(selectBody);
						

						//////////////////////////////////////////////////////System.out.println(selectBody.toString());
	
					// Create Statement	
					}else if(statement instanceof CreateTable){
						//////////////////////////////////////////////////////System.out.println("Create Statement");
						CreateTable createStatement = (CreateTable) statement;
						
						//Table Name
						Table table = createStatement.getTable();
						//////////////////////////////////////////////////System.out.println("Table Name : "+table);
						
						@SuppressWarnings("unchecked")
						List<ColumnDefinition> columns = createStatement.getColumnDefinitions();
						
						for (ColumnDefinition column : columns){
							////////////////////////System.out.println(column.getColDataType());
						}
						
						 schema.put(table.getName(), columns);
					
					}
				}
				////////////////////////////////////////////////////System.out.println(new DoubleValue(3).hashCode());
				////////////////////////////////////////////////////System.out.println(new DoubleValue(3).hashCode());
			}
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} 
		long endTime   = System.currentTimeMillis();
		long totalTime = endTime - startTime;
//		System.out.println(totalTime);
	}
}