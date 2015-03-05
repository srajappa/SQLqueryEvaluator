package edu.buffalo.cse562;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;

import net.sf.jsqlparser.schema.Column;


public class TupleComparator implements Comparator<Tuple> {
    protected List<String> fields;

    public TupleComparator(ArrayList<String> orderedFields) {
        fields = new ArrayList<String>();
        for (String field : orderedFields) {
            fields.add(field);
        }
    }

    @Override
    public int compare(Tuple tupleA, Tuple tupleB) {
        Integer score = 0;
        Boolean continueComparison = true;
        Iterator<String> itFields = fields.iterator();

        while (itFields.hasNext() && continueComparison) {
            String field = itFields.next();
            Integer currentScore = 0;
                currentScore = tupleA.get(field).toString().compareTo(tupleB.get(field).toString());
            if (currentScore != 0) {
                continueComparison = false;
            }
            score = currentScore;
        }

        return score;
    }


}