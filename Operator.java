package edu.buffalo.cse562;


public interface Operator{
	public void open();
	public Tuple getNextTuple() ;
	public void close();
}
