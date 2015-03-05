package edu.buffalo.cse562;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

import net.sf.jsqlparser.expression.Expression;
import net.sf.jsqlparser.expression.Function;
import net.sf.jsqlparser.expression.LeafValue;
import net.sf.jsqlparser.schema.Column;
import net.sf.jsqlparser.schema.Table;
import net.sf.jsqlparser.statement.select.AllColumns;
import net.sf.jsqlparser.statement.select.SelectExpressionItem;
import net.sf.jsqlparser.statement.select.SelectItem;

public class ProjectionOperator extends Eval implements Operator{
	Operator inputOperator;
	List<SelectItem> projectExpression;
	Tuple tuple;
	public ProjectionOperator(Operator inputOperator, List<SelectItem> projectExpression) {
		super();
		this.inputOperator = inputOperator;
		this.projectExpression = projectExpression;
	}

	@Override
	public void open() {
		//////////////System.out.println("---------PROJECT-------\n");
		inputOperator.open();
		
	}
	@Override
	public Tuple getNextTuple() {

		ArrayList<LeafValue> tupleData = new ArrayList<LeafValue>();
		ArrayList<Column> columns = new ArrayList<>();

		Tuple outputTuple = null;
		try {
			do {
				tuple = inputOperator.getNextTuple();
				////////////////////////////////////System.out.println("Columns in Project Op : "+tuple.columns);
				if (tuple == null)
					break;
				 outputTuple = new Tuple();

				for (SelectItem projectItem : projectExpression) {
//					////////////////////////////////////////////System.out.println(projectItem.getClass());
					if (projectItem instanceof SelectExpressionItem) {
						////////////////////////////////////////////////System.out.println(((SelectExpressionItem) projectItem).getExpression());

						Expression expression = ((SelectExpressionItem) projectItem).getExpression();
						
						LeafValue value = super.eval(expression);
						tupleData.add(value);
//						Column tmp  = new Column();
						if(((SelectExpressionItem) projectItem).getAlias() != null){
							
							outputTuple.addValue(((SelectExpressionItem) projectItem).getAlias().toString(), value);

						}else{
							outputTuple.addValue(((SelectExpressionItem) projectItem).toString(), value);
						}
//						tmp.setTable(new Table("", ""));
//						columns.add(tmp );
//						TODO column aliasing
						
						//////////////////System.out.println(outputTuple.columns);

						
					}else if(projectItem instanceof AllColumns){
						outputTuple = tuple;
					}
				}

			} while (tuple == null);
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		return outputTuple;
			
	}
	@Override
	public void close() {
		inputOperator.close();
		
	}
	
	@Override
	public LeafValue eval(Column col) throws SQLException {
			 
			return tuple.get(col.getWholeColumnName());

	}
	
	public LeafValue eval(Function func){
		////////////////////////////////////System.out.println(func.toString());
		return tuple.get(func.toString());
	}

}
