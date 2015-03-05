package edu.buffalo.cse562;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map.Entry;

import net.sf.jsqlparser.expression.DateValue;
import net.sf.jsqlparser.expression.DoubleValue;
import net.sf.jsqlparser.expression.LeafValue;
import net.sf.jsqlparser.expression.LeafValue.InvalidLeaf;
import net.sf.jsqlparser.expression.StringValue;
import net.sf.jsqlparser.schema.Column;
import net.sf.jsqlparser.schema.Table;
import net.sf.jsqlparser.statement.create.table.ColDataType;
import net.sf.jsqlparser.statement.create.table.ColumnDefinition;
import net.sf.jsqlparser.statement.select.FromItem;

public class ScanOperator implements Operator {
	FromItem fromTable;
	File dataDir;
	BufferedReader reader;
	HashMap<String, List<ColumnDefinition>> schema;
	List<ColumnDefinition> columns;
	static Integer BATCHCOUNT = 1;
	ArrayList<Tuple> tupleBuffer = new ArrayList<Tuple>();
	Iterator<Tuple> bufferIterator ;
	Tuple tuple;

	public ScanOperator(FromItem fromTable, File dataDir, HashMap<String, List<ColumnDefinition>> schema) {
		super();
		this.fromTable = fromTable;
		this.dataDir = dataDir;
		this.schema = schema;
		
	}
	@Override
	public void open() {
		// TODO Auto-generated method stub
		//////////////System.out.println("---------SCAN-------\n");

		try {
			
			if (dataDir != null && dataDir.exists() && dataDir.isDirectory()) {
				File[] dataFiles = dataDir.listFiles();
				////////////////////////////System.out.println(fromTable.toString() + ".dat"); 
				for (File dataFile : dataFiles) {
					////////////////////////////System.out.println(dataFile.getName());
					if (dataFile.getName().equalsIgnoreCase(((Table)fromTable).getName().toString().toLowerCase() + ".dat") || dataFile.getName().equalsIgnoreCase(((Table)fromTable).getName().toString().toUpperCase() + ".dat") || dataFile.getName().equalsIgnoreCase(((Table)fromTable).getName().toString() + ".dat" ))
					{
	
						reader = new BufferedReader(new FileReader(dataFile));
						reader.toString();
					}
				}
				
				
				//de-serializing from file
				for(Entry<String, List<ColumnDefinition>> entry : schema.entrySet()){
					////////////////////////////////////////////////System.out.println("table"+entry.getKey());
					////////////////////////////////////////////////System.out.println("columns"+entry.getValue());

				}
				////////////////////////////////////////////////System.out.println(fromTable.toString());
				columns = schema.get(((Table)fromTable).getName().toString().toUpperCase());
				////////////////////////////////////////////////System.out.println("col"+columns);
				//////////////////////////////////////////////////////System.out.println(schema);
				//////////////////////////////////////////////////////System.out.println("Deserialized the file :" +fromTable.toString().toLowerCase()+".schema");
			}
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}


	@Override
	public Tuple getNextTuple() {
		//////System.out.println("scan");
		String line = null;
		try {
			if (tupleBuffer.isEmpty()) {
//				for (int j = 0; j < BATCHCOUNT; j++) {
				do{
					line = reader.readLine();
					
					if(line == null) {
						//////System.out.println(line);

						break;
					}
					if (line  != null) {
						String[] stringValues = line.split("\\|");
						// ////////////////////////////////////////////////////System.out.println("Data values in the tuple : ");
						// for (String string: stringValues){
						// ////////////////////////////////////////////////////System.out.println(string);
						// }
						// int a = 10/0;
						LinkedHashMap<String, LeafValue> tupleMap = new LinkedHashMap<String, LeafValue>();

						for (int i = 0; i < columns.size(); i++) {
							
							if (fromTable.getAlias() != null) {
								Table aliasTable = new Table();
								((Table) aliasTable).setName(fromTable.getAlias());
//								column.add(new Column((Table) aliasTable, columns.get(i).getColumnName()));
//								TODO aliasing table name
							}
							String colDataType = columns.get(i).getColDataType().getDataType();
							if (colDataType.equalsIgnoreCase("INT") || colDataType.equalsIgnoreCase("DECIMAL")) {
								tupleMap.put(fromTable.toString()+"."+columns.get(i).getColumnName(),new DoubleValue(stringValues[i]));
							} else if (colDataType.equalsIgnoreCase("STRING") || colDataType.equalsIgnoreCase("CHAR") || colDataType.equalsIgnoreCase("varchar")) {
								tupleMap.put(fromTable.toString()+"."+columns.get(i).getColumnName(),new StringValue("'" + stringValues[i] + "'"));
							} else if (colDataType.equalsIgnoreCase("date") ) {
								tupleMap.put(fromTable.toString()+"."+columns.get(i).getColumnName(),new DateValue("'" + stringValues[i] + "'"));
							}
							
						}
						tupleBuffer.add(new Tuple(tupleMap));
					}
				
				}while(line != null);
				bufferIterator = tupleBuffer.iterator();

			}
//				////////////////////////////////////////////////////System.out.println("SCAN["+fromTable+"("+columnNames+")]");
//				////////////////////////////////////////////////////System.out.println(leafValues);
//				//////////////////////System.out.println(column);
				//////System.out.println("out of for loop");

//			}
			if(bufferIterator.hasNext()){
				tuple = bufferIterator.next();			
			}else{
				bufferIterator = tupleBuffer.iterator();
				tuple = null;
			}

		} catch (IOException e) {
			e.printStackTrace();
		}
		return tuple;
		
	}
	@Override
	public void close() {
		try {
			reader.close();
			
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	

}
