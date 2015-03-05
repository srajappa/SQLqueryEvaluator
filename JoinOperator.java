package edu.buffalo.cse562;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.LinkedHashMap;

import net.sf.jsqlparser.expression.BooleanValue;
import net.sf.jsqlparser.expression.DoubleValue;
import net.sf.jsqlparser.expression.Expression;
import net.sf.jsqlparser.expression.LeafValue;
import net.sf.jsqlparser.expression.StringValue;
import net.sf.jsqlparser.expression.operators.relational.EqualsTo;
import net.sf.jsqlparser.schema.Column;
import net.sf.jsqlparser.schema.Table;
import net.sf.jsqlparser.statement.select.FromItem;
import net.sf.jsqlparser.statement.select.Join;

public class JoinOperator extends Eval implements Operator {
	public Operator inputOperator;
	public Operator joinInputOperator;
	public FromItem fromTable;
	public Join join;
	// public Expression whereCondition;
	Tuple fromTuple = null;
	Tuple joinTuple = null;

	public JoinOperator(Operator inputOperator, Operator joinInputOperator, FromItem fromTable, Join join) {
		super();
		this.joinInputOperator = joinInputOperator;
		this.inputOperator = inputOperator;
		this.fromTable = fromTable;
		this.join = join;
		// this.whereCondition = whereCondition;
	}

	@Override
	public void open() {
		//////////////System.out.println("---------JOIN-------\n");

		// TODO Auto-generated method stub
		inputOperator.open();
		joinInputOperator.open();
		joinTuple = joinInputOperator.getNextTuple();


	}

	@Override
	public Tuple getNextTuple() {
		Tuple joinedTuple = null;
		if (true) {
			do {
				
				fromTuple = inputOperator.getNextTuple();
				if (fromTuple == null) {
					joinTuple = joinInputOperator.getNextTuple();
					inputOperator.close();
					inputOperator.open();
				}

			} while (fromTuple == null);
			do {

					if (joinTuple == null) break;
					LinkedHashMap<String, LeafValue> joinedTupleMap = new LinkedHashMap<String, LeafValue>();
					joinedTupleMap.putAll(fromTuple.getTupleMap());
					joinedTupleMap.putAll(joinTuple.getTupleMap());


					joinedTuple = new Tuple(joinedTupleMap);

					//fromTuple.appendTuple();
			} while (joinTuple == null);

		}

		return joinedTuple;
	}


	@Override
	public void close() {
		// TODO Auto-generated method stub
		inputOperator.close();
		joinInputOperator.close();
	}

	@Override
	public LeafValue eval(Column col) throws SQLException {

			if (col.getTable().getName().equalsIgnoreCase(((Table)fromTable).getName())) {

				return fromTuple.get(col.getWholeColumnName());
			} else if (col.getTable().getName().equalsIgnoreCase(((Table)join.getRightItem()).getName())) {



				return joinTuple.get(col.getWholeColumnName());

			}
			
			return null;
	}
}
