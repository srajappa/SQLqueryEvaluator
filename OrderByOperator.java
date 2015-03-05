package edu.buffalo.cse562;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.TreeSet;

import net.sf.jsqlparser.schema.Column;
import net.sf.jsqlparser.schema.Table;
import net.sf.jsqlparser.statement.select.OrderByElement;

public class OrderByOperator implements Operator{
	Operator inputOperator;
	ArrayList orderByItems;
	Tuple tuple;
	ArrayList<Tuple> orderedTuple =  new ArrayList<Tuple>();
	Iterator<Tuple> iterator;
	public OrderByOperator(Operator inputOperator, ArrayList orderByItems) {
		super();
		this.inputOperator = inputOperator;
		this.orderByItems = orderByItems;
	}

	@Override
	public void open() {
		//////////System.out.println("---------ORDERBY--------\n");
		inputOperator.open();		
	}

	@Override
	public Tuple getNextTuple() {
		
		tuple = null;
		
		if (orderedTuple.isEmpty()) {

			do {
				tuple = inputOperator.getNextTuple();
				if(orderByItems ==  null) return tuple;
				if (tuple == null)
					break;
				
				////////////////////////////System.out.println(tuple);
				////////////////////////////System.out.println(tuple.columns);
				orderedTuple.add(tuple);

			} while (tuple != null);
			ArrayList<String> orderedFields = new ArrayList<String>();
			for(Object orderByItem : orderByItems){
				orderedFields.add(((OrderByElement)orderByItem).getExpression().toString());

			}
			
			TupleComparator comparator = new TupleComparator(orderedFields);
			orderedTuple.sort(comparator);
			iterator = orderedTuple.iterator();
		}
		if(iterator.hasNext()){
			return iterator.next();
		}
		return null;
	}

	@Override
	public void close() {
		inputOperator.close();		
	}

}
