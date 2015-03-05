package edu.buffalo.cse562;

import java.io.Serializable;
import java.util.List;

import net.sf.jsqlparser.statement.create.table.ColumnDefinition;


public class TableSchema implements Serializable{
	/**
	 * 
	 */
	private static final long serialVersionUID = 93781645689796257L;
	List<ColumnDefinition> columns;
	
	public TableSchema(List<ColumnDefinition> columns) {
		super();
		this.columns = columns;
	}

	@Override
	public String toString() {
		return "TableSchema ["+columns+"]";
	}
}
