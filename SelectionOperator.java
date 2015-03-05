package edu.buffalo.cse562;

import java.sql.SQLException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import net.sf.jsqlparser.expression.BinaryExpression;
import net.sf.jsqlparser.expression.BooleanValue;
import net.sf.jsqlparser.expression.Expression;
import net.sf.jsqlparser.expression.LeafValue;
import net.sf.jsqlparser.schema.Column;

public class SelectionOperator extends Eval implements Operator {
	Operator inputOperator;
	Expression whereCondition;
	Tuple tuple = null;

	
	public SelectionOperator(Operator inputOperator, Expression condition) {
		super();
		this.inputOperator = inputOperator;
		this.whereCondition = condition;
	}




	@Override
	public void open() {
		//////////////System.out.println("---------SELECT-------\n");

		inputOperator.open();

	}


	@Override
	public Tuple getNextTuple()  {
		try {
			do {

				tuple = inputOperator.getNextTuple();
				if(whereCondition == null) return tuple;

				if(tuple == null) break;
				////////////////System.out.println(tuple);
				
					// ////////////////////////////////////////////////////System.out.println(whereCondition);
					LeafValue status = super.eval(whereCondition);
					//////////System.out.println(status.toString());
					if (status instanceof BooleanValue) {
						if (status == BooleanValue.FALSE) {
							tuple = null;
						}
					}
				
			} while (tuple == null);
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return tuple;
	}

	@Override
	public void close() {

		inputOperator.close();
	}

	@Override
	public LeafValue eval(Column col) throws SQLException {
			return tuple.get(col.getWholeColumnName());
			
	}

}
