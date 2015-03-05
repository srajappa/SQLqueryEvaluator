package edu.buffalo.cse562;

public class UnionOperator implements Operator {
	
	
	Operator leftUnionOp;
	Operator rightUnionOp;
	Tuple ltuple,rtuple;
	
	public UnionOperator(Operator leftUnionOp, Operator rightUnionOp) {
		// TODO Auto-generated constructor stub
		this.leftUnionOp = leftUnionOp;
		this.rightUnionOp = rightUnionOp;
	}

	@Override
	public void open() {
		// TODO Auto-generated method stub
		System.out.println("Inside OPERATOR: open()");
		leftUnionOp.open();
		rightUnionOp.open();
		
	}

	@Override
	public Tuple getNextTuple() {
		// TODO Auto-generated method stub
		System.out.println("Inside OPERATOR: getNextTuple()");
		do{
			ltuple = leftUnionOp.getNextTuple();
			if(ltuple==null){
				break;
			}
			return ltuple;
		}while(ltuple!=null);
		
		//After the Left Relation goes to the end of the file.
		do{
			rtuple = rightUnionOp.getNextTuple();
			if(rtuple==null){
				break;
			}
			return rtuple;
		}while(rtuple!=null);
		return null;
	}

	@Override
	public void close() {
		// TODO Auto-generated method stub
		System.out.println("Inside OPERATOR: close()");
		leftUnionOp.close();
		rightUnionOp.close();
	} 
}
