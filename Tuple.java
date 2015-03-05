package edu.buffalo.cse562;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Map.Entry;

import net.sf.jsqlparser.expression.LeafValue;
import net.sf.jsqlparser.schema.Column;



public class Tuple implements Cloneable, Comparable<Tuple>{
	@Override
	protected Object clone() throws CloneNotSupportedException {
		return super.clone();
	}


	@Override
	public String toString() {
		return "Tuple [tupleMap=" + tupleMap + "]";
	}



	String compareElement;
	LinkedHashMap<String, LeafValue> tupleMap;
	public Tuple(){
		tupleMap = new LinkedHashMap<String, LeafValue>();
	}
	
	public Tuple(LinkedHashMap<String, LeafValue> tupleMap){
		this.tupleMap = tupleMap;
	}
	
	
	public void addValue(String column, LeafValue value){
		tupleMap.put(column, value);
	}

	
	public LeafValue get(String name)  {
		// TODO Auto-generated method stub		
		
		
		
//		int index = -1;
//		int current = 0;
//
//
//		for(Column column:columns){
//			String columnName = column.getColumnName();
//
//			if(name.getTable().getName() != null){
//				//////System.out.println("Has table name : if part");
//				if(column.toString().equals(name.toString())  ){
//					index = current;
//					break;
//				}
//			}else{
//				//////System.out.println("Has no table name : else part");
//				 if(columnName.equals(name.toString()) || columnName.equals(name.getColumnName())){
//					index = current;
//					break;
//				}
//			}
//			current++;
//		}
		if(tupleMap.containsKey(name)){
			return tupleMap.get(name);
		}else{
			for(Entry<String, LeafValue> entry : tupleMap.entrySet()){
				if(entry.getKey().contains(name)){
					return entry.getValue();
				}
				
			}
			System.out.println("tupleMap : " + tupleMap);
			System.out.println("Column Name : " + name);
			return tupleMap.get(name);

		}

		
	}

	public void appendTuple(LinkedHashMap<String, LeafValue> joinTuple){
		tupleMap.putAll(joinTuple);
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((tupleMap == null) ? 0 : tupleMap.toString().hashCode());
		return result;
	}



	@Override
	public boolean equals(Object obj) {
		
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Tuple other = (Tuple) obj;
		if (tupleMap == null) {
			if (other.tupleMap != null)
				return false;
		} else if (!tupleMap.toString().equalsIgnoreCase(other.tupleMap.toString()))
			return false;
		return true;
	}


	@Override
	public int compareTo(Tuple other) {
		return other.get(compareElement).toString().compareTo(this.get(compareElement).toString());
	}


	public LinkedHashMap<String, LeafValue> getTupleMap() {
		return tupleMap;
	}


	public void setTupleMap(LinkedHashMap<String, LeafValue> tupleMap) {
		this.tupleMap = tupleMap;
	}
		
	
}

