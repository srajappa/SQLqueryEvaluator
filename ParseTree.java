package edu.buffalo.cse562;

import java.io.File;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map.Entry;

import net.sf.jsqlparser.schema.Table;
import net.sf.jsqlparser.statement.create.table.ColumnDefinition;
import net.sf.jsqlparser.statement.select.SelectItem;
import net.sf.jsqlparser.expression.DoubleValue;
import net.sf.jsqlparser.expression.Expression;
import net.sf.jsqlparser.expression.LeafValue;
import net.sf.jsqlparser.expression.LeafValue.InvalidLeaf;
import net.sf.jsqlparser.expression.StringValue;
import net.sf.jsqlparser.statement.select.AllColumns;
import net.sf.jsqlparser.statement.select.Distinct;
import net.sf.jsqlparser.statement.select.FromItem;
import net.sf.jsqlparser.statement.select.Join;
import net.sf.jsqlparser.statement.select.Limit;
import net.sf.jsqlparser.statement.select.PlainSelect;
import net.sf.jsqlparser.statement.select.SelectBody;
import net.sf.jsqlparser.statement.select.SelectExpressionItem;
import net.sf.jsqlparser.statement.select.SubSelect;
import net.sf.jsqlparser.statement.select.Union;

public class ParseTree {
	public class Node{
		Operator operator;
		Node[] children;
		public Node(Operator operator, Node[] children) {
			super();
			this.operator = operator;
			this.children = children;
		}
	}

	File dataDir;
	HashMap<String, List<ColumnDefinition>> schema;
	public ParseTree( File dataDir, HashMap<String, List<ColumnDefinition>> schema) {
		super();
		this.dataDir = dataDir;
		this.schema = schema;
	}	 
	public Operator createParseTree(SelectBody selectBody){
		PlainSelect select = null;

		if(selectBody instanceof PlainSelect ){
			select = (PlainSelect) selectBody;
			}
		
		Operator scanOp = null;
		FromItem fromTable = select.getFromItem();
		////////////////////////////System.out.println(fromTable.getClass());
		if(fromTable instanceof Table){
			//////////////////////System.out.println(fromTable.getAlias());
			 scanOp = new ScanOperator(fromTable, dataDir, schema);
		}else if(fromTable instanceof SubSelect){
			scanOp = createParseTree((PlainSelect)((SubSelect) fromTable).getSelectBody());
			
		}
		//////////////////////////////System.out.println("FromTable : " + fromTable);
		

		
		Expression whereCondition = select.getWhere(); //SelectOperator
//		////////////////////////////////////////////////System.out.println(whereCondition);
		Operator joinScanOp = null;
		JoinOperator joinOp = null;
		Operator selectOp = null;
		@SuppressWarnings("unchecked")
		List<Join> joinTables = select.getJoins();
		Operator leftJoinOp = scanOp;
		if(joinTables != null){
			//////////////////////////////////////////////System.out.println("Join Detected : " +joinTables);
			for (Join joinTable : joinTables){
				//////////////////////////////////////////////System.out.println(joinTable.getRightItem());
				joinScanOp = new ScanOperator(joinTable.getRightItem(), dataDir, schema);
				leftJoinOp = new JoinOperator(leftJoinOp, joinScanOp ,fromTable, joinTable);
				selectOp = new SelectionOperator(leftJoinOp, whereCondition);

			}
		}else{
			selectOp = new SelectionOperator(scanOp, whereCondition);
			////////////////////////////////////////////////////System.out.println("whereCondition : " + whereCondition);
		}
		Limit limitItem = select.getLimit();
		if(limitItem != null){
			System.out.print("LimitItem");
			int a = 10/0;
		}
		
		@SuppressWarnings("unchecked")
		List<SelectItem> projectColumns = select.getSelectItems(); //project
		for(SelectItem projectColumn : projectColumns){
			if(projectColumn instanceof SelectExpressionItem){
				((SelectExpressionItem) projectColumn).getAlias();
			}
		}
		
		@SuppressWarnings("unchecked")
		List<Expression> groupBy = select.getGroupByColumnReferences(); //groupBy
		GroupByOperator groupByOp = new GroupByOperator(selectOp, groupBy, projectColumns,  true);
		
		Operator projectOp = new ProjectionOperator(groupByOp, projectColumns);
		
		
		ArrayList orderByItems = (ArrayList) select.getOrderByElements();
		OrderByOperator orderByOp = new OrderByOperator(projectOp, orderByItems);
		
		Distinct distinct = select.getDistinct();	//Distinct
		GroupByOperator distinctGroupByOp = new GroupByOperator(orderByOp, groupBy, projectColumns, false);
		List<Expression> distinctItems = new ArrayList<Expression>();
		if(distinct != null){
			for(SelectItem projectColumn : projectColumns){
				if(projectColumn instanceof SelectExpressionItem)
				distinctItems.add(((SelectExpressionItem) projectColumn).getExpression());
			}
			distinctGroupByOp = new GroupByOperator(orderByOp, distinctItems, projectColumns, true);
		}
		
		Union unionItem = null;
		UnionOperator unionOp =  new UnionOperator(distinctGroupByOp, null);;
		 if(selectBody instanceof Union){
		 unionItem = (Union) selectBody;
		 List<SelectBody> selects = unionItem.getPlainSelects();
			Operator leftUnionOp = createParseTree(selects.get(0));
			Operator rightUnionOp = createParseTree(selects.get(1));
			
			 unionOp = new UnionOperator(leftUnionOp, rightUnionOp);

		}
		
		
		Operator rootOperator = distinctGroupByOp;
		return rootOperator;
	}
	public void executeQuery(SelectBody select){
		
		Operator rootOperator = createParseTree(select);
		rootOperator.open();
		Tuple tuple = null;
		do{
			tuple = rootOperator.getNextTuple();
			if(tuple == null) break;

			printOutput(tuple);

		}while(tuple!=null);
		
		
		rootOperator.close();
	}
	private void printOutput(Tuple tuple) {
		if (tuple == null) return;
		////////////////////////////////////////////System.out.println(tuple);
		try {

			ArrayList<String> printBuffer = new ArrayList<String>();
			for (Entry<String, LeafValue> keyValue : tuple.getTupleMap().entrySet()) {
				LeafValue value = keyValue.getValue();
				if (value instanceof StringValue) {
					printBuffer.add(value.toString().replaceAll("^'", "").replaceAll("'$", ""));

				} else if (value instanceof DoubleValue) {
					DecimalFormat format = new DecimalFormat("0.##");
					printBuffer.add(format.format(value.toDouble()));

				} else {
					printBuffer.add(value.toString());
				}
				printBuffer.add("|");
			}
			if (printBuffer.size() > 1)
				printBuffer.remove(printBuffer.size() - 1); // to remove the
															// last pipes
			for (String cell : printBuffer) {
				System.out.print(cell);
			}
			System.out.print("\n");
		} catch (InvalidLeaf e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}
}
