package edu.buffalo.cse562;

import net.sf.jsqlparser.expression.DateValue;
import net.sf.jsqlparser.expression.DoubleValue;
import net.sf.jsqlparser.expression.LeafValue;
import net.sf.jsqlparser.expression.LeafValue.InvalidLeaf;
import net.sf.jsqlparser.expression.LongValue;
import net.sf.jsqlparser.expression.StringValue;

public class Cloner {

	public static LeafValue clone(LeafValue value) {
		try {
		if(value instanceof StringValue){
			return new StringValue(value.toString());
		}else if(value instanceof DoubleValue){
			return new DoubleValue(value.toDouble());
		}else if(value instanceof LongValue){
			
				return new LongValue(value.toLong());
			
		}else if(value instanceof DateValue){
			
			return new DateValue("'"+value.toString()+"'");

		}
		} catch (InvalidLeaf e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
	}

}
