package edu.buffalo.cse562;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map.Entry;
import java.util.Set;

import net.sf.jsqlparser.expression.DoubleValue;
import net.sf.jsqlparser.expression.Expression;
import net.sf.jsqlparser.expression.Function;
import net.sf.jsqlparser.expression.LeafValue;
import net.sf.jsqlparser.expression.operators.relational.ExpressionList;
import net.sf.jsqlparser.schema.Column;
import net.sf.jsqlparser.schema.Table;
import net.sf.jsqlparser.statement.select.Distinct;
import net.sf.jsqlparser.statement.select.SelectExpressionItem;
import net.sf.jsqlparser.statement.select.SelectItem;

public class GroupByOperator extends Eval implements Operator{
	Operator inputOperator;
	List<Expression> groupByItems;
	Set<Tuple> keySet = new HashSet<Tuple>();
	List<SelectItem> projectColumns;
	Tuple tuple = null;
	Tuple groupByTuple = new Tuple();;
	Tuple finalTuple = null;
	ArrayList<Function> functions = new ArrayList<Function>();
	Iterator<Tuple> iterator;
	HashMap<Tuple, GroupProperties> groups = new HashMap<Tuple, GroupProperties>();
	Boolean isDistinct ;
	public GroupByOperator(Operator inputOperator, List<Expression> groupByItems, List<SelectItem> projectColumns, Boolean isDistinct) {
		super();
		this.inputOperator = inputOperator;
		this.groupByItems = groupByItems;
		this.projectColumns = projectColumns;
		this.isDistinct = isDistinct;
	}

	@Override
	public void open() {
		//////////////System.out.println("---------GROUPBY-------\n");

		inputOperator.open();

	}

	@Override
	public Tuple getNextTuple() {
			
		GroupProperties properties;
		try {
			if(keySet.isEmpty()){

			for (SelectItem projectItem : projectColumns) {
				if (projectItem instanceof SelectExpressionItem) {
					Expression expression = ((SelectExpressionItem) projectItem).getExpression();
					if (expression instanceof Function) {
						Function function = ((Function) expression);
						functions.add(function);
						String functionName = function.getName();
						ExpressionList parameters = function.getParameters();
						// ////////////////////////////////////////////System.out.println("function Name & parameters : "
						// + functionName +"&"+ parameters);
						//GroupProperties.init(function);
					}
				}
			}
			do {
				tuple = inputOperator.getNextTuple();
			//	////////////////////////////////////System.out.println("tuple input to groupBy"+ tuple.columns);
				if(isDistinct == false){    // when group By op is not used for distinct
					return tuple;
				}
				
				if (groupByItems == null){
					if(!functions.isEmpty()){   // when group by op is used for aggregation
						groupByItems = new ArrayList<Expression>();
		
					}else{// if no group by items and no aggregation
						return tuple;
					}
				}
				if (tuple == null)
					break;

				////////////////////////////////////System.out.println("groupByItems : " + groupByItems);
				LinkedHashMap<String, LeafValue> tupleMap = new LinkedHashMap<String, LeafValue>();

				for (Expression groupByItem : groupByItems) {
					////////////////////////////////////System.out.println("GroupByItem : "+groupByItem);
					//////////////////////////System.out.println(groupByItem);
					LeafValue groupByValue = super.eval(groupByItem);
					groupByTuple.addValue(groupByItem.toString(), Cloner.clone(groupByValue));
				}
				if (groups.containsKey(groupByTuple)) {
					properties = groups.get(groupByTuple);
					properties.consume(tuple);
				} else {
					properties = new GroupProperties(functions);
					properties.consume(tuple);
				}
				groups.put(groupByTuple, properties);
				groupByTuple = new Tuple();
//				//////////////////////////////////////////System.out.println("group");
//				for(Entry tmp : groups.entrySet()){
//					//////////////////////////////////////////System.out.println(tmp.getKey().toString()+tmp.getValue());
//				}
			} while (tuple != null);

			keySet = groups.keySet();
			////////////////////////////////////System.out.println("groups"+groups);
			 iterator = keySet.iterator();
			}
			if (iterator.hasNext()){
				Tuple currentTuple = iterator.next();
				finalTuple = new Tuple((LinkedHashMap<String, LeafValue>) currentTuple.getTupleMap().clone());
				////////////////////////////////////System.out.println("FinalTuple" + finalTuple);
				//////////////////////////////////////////System.out.println(groups);
				for(Function function : functions){
					LeafValue value = groups.get(currentTuple).roundUp(function);

					finalTuple.addValue(function.toString() ,value);
					}
			}else return null;
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return finalTuple;
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
