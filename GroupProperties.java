package edu.buffalo.cse562;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map.Entry;

import net.sf.jsqlparser.expression.DoubleValue;
import net.sf.jsqlparser.expression.Expression;
import net.sf.jsqlparser.expression.Function;
import net.sf.jsqlparser.expression.LeafValue;
import net.sf.jsqlparser.expression.LeafValue.InvalidLeaf;
import net.sf.jsqlparser.expression.operators.relational.ExpressionList;
import net.sf.jsqlparser.schema.Column;
class AverageValue implements LeafValue{
	public Double sum;
	public Double count;
	public AverageValue(Double sum, Double count) {
	super();
	this.sum = sum;
	this.count = count;
}

	@Override
	public double toDouble() throws InvalidLeaf {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public long toLong() throws InvalidLeaf {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public String toString() {
		return "AverageValue [sum=" + sum + ", count=" + count + "]";
	}
	
	
}
public class GroupProperties extends Eval{


	Tuple tuple = null;
	HashMap<Function, LeafValue> results = new HashMap<Function, LeafValue>();
	public GroupProperties(ArrayList<Function> functions) {
		for(Function function: functions){
			if(function.getName().equalsIgnoreCase("MIN")){
				results.put(function, new DoubleValue(Double.MAX_VALUE));

			}else if(function.getName().equalsIgnoreCase("AVG")){
				results.put(function, new AverageValue(0.0, 0.0));
			}else{
				results.put(function, new DoubleValue(0));
			}
		}
	}

//	public void init(Function function){
//		if(function.getName().equalsIgnoreCase("MIN")){
//			results.put(function, new DoubleValue(Double.MAX_VALUE));
//
//		}else{
//			results.put(function, null);
//		}
//			
//	}
	
	public void consume(Tuple inputTuple){
		Double result;
		tuple = inputTuple;
		for (Entry<Function, LeafValue> entry : results.entrySet()){
			//////////////////////////////////System.out.println(entry.getKey().getName());
			if(entry.getKey().getName().equalsIgnoreCase("SUM")){
				result= calculateSum(entry.getKey(), tuple);
				////////////////////////////////////////////System.out.println("result : "+new DoubleValue(result.toString()));

				results.put(entry.getKey(), new DoubleValue(result.toString()));
			}else if(entry.getKey().getName().equalsIgnoreCase("AVG")){
				LeafValue avg = calculateAvg(entry.getKey(), tuple);
				results.put(entry.getKey(), avg);
			}else if(entry.getKey().getName().equalsIgnoreCase("MIN")){
				result= calculateMin(entry.getKey(), tuple);
				results.put(entry.getKey(), new DoubleValue(result.toString()));
			}else if(entry.getKey().getName().equalsIgnoreCase("MAX")){
				result= calculateMax(entry.getKey(), tuple);
				results.put(entry.getKey(), new DoubleValue(result.toString()));
			}else if(entry.getKey().getName().equalsIgnoreCase("COUNT")){
				result= calculateCount(entry.getKey(), tuple);
				results.put(entry.getKey(), new DoubleValue(result.toString()));
			}
		}
	}
	private Double calculateCount(Function function, Tuple tuple2) {
		
		ExpressionList params = function.getParameters();
		LeafValue value = null;
		Double count = new Double(0);
		
		try {
			if(function.isAllColumns()){
				if(results.containsKey(function)){
					count = results.get(function).toDouble();
					count++;
				}
				return count;
			}
			for(Object expression : params.getExpressions()){
			
				 value = super.eval((Expression)expression);
			}
			if(results.containsKey(function)){
				count = results.get(function).toDouble();
				count++;
			}
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (InvalidLeaf e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		return count;
	}

	private Double calculateMax(Function function, Tuple tuple2) {
		ExpressionList params = function.getParameters();
		LeafValue value = null;
		Double max = new Double(0);
//		////////////////////////////////System.out.println("Max");

		try {
			for(Object expression : params.getExpressions()){
			
				 value = super.eval((Expression)expression);
			}
			if(results.containsKey(function)){
				max = results.get(function).toDouble();

				if(value.toDouble() > max){
					max = value.toDouble();
				}
			}
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (InvalidLeaf e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		return max;
	}

	private Double calculateMin(Function function, Tuple tuple2) {
		ExpressionList params = function.getParameters();
		LeafValue value = null;
		Double min = Double.MAX_VALUE;

		try {
			for(Object expression : params.getExpressions()){
			
				 value = super.eval((Expression)expression);
			}
			if(results.containsKey(function)){
				min = results.get(function).toDouble();

				if(value.toDouble() < min){
					min = value.toDouble();
				}
			}
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (InvalidLeaf e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		return min;
	}

	private AverageValue calculateAvg(Function function, Tuple tuple2) {
		ExpressionList params = function.getParameters();
		LeafValue value = null;
		Double sum = new Double(0);
		Double count = new Double(0);
		AverageValue avg = new AverageValue(0.0, 0.0);
		try {
			for(Object expression : params.getExpressions()){
			
				 value = super.eval((Expression)expression);
			}
			if(results.containsKey(function)){
				avg =(AverageValue) results.get(function);
				avg.count++;
				avg.sum += value.toDouble();
				
			}
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (InvalidLeaf e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		return avg;
	}

	private Double calculateSum(Function function, Tuple tuple) {
		////////////System.out.println(function);
		ExpressionList params = function.getParameters();
		LeafValue value = null;
		Double sum = new Double(0);

		try {
			for(Object expression : params.getExpressions()){
			
				 value = super.eval((Expression)expression);
				 ////////////System.out.println(value);
			}
			if(results.containsKey(function)){
				sum = results.get(function).toDouble();
				sum = sum+Double.parseDouble(value.toString());
			}
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (InvalidLeaf e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		return sum;
	}

	public LeafValue roundUp(Function function){
		if(results.containsKey(function)){
			////////////////////////////////////////////System.out.println("contains Key");
			if(function.getName().equalsIgnoreCase("AVG"))
			{
				LeafValue avg = results.get(function);
				if(avg instanceof AverageValue){
					avg = new DoubleValue((((AverageValue) avg).sum)/((AverageValue) avg).count);
					return avg;
				}
			}else return results.get(function);
		}
		return null;
	}

	@Override
	public LeafValue eval(Column col) throws SQLException {
		if(col!=null){
			LeafValue value = tuple.get(col.getWholeColumnName());
			////////////System.out.println(col + value.toString());
			return value;
			
		}
		return  null;
	}
	
	

}
