package com.bakalris.example.basicandroidsample;

import java.util.Comparator;

/**
 * Created by Mirko on 16.4.2016.
 */
public class CustomHorizontalLineComparator implements Comparator<MergedLine> {

    @Override
    public int compare(MergedLine lhs, MergedLine rhs) {

        double minY1 = Math.min(lhs.getStart().y, lhs.getEnd().y);
        double minY2 = Math.min(rhs.getStart().y, rhs.getEnd().y);

        if(minY1 == minY2)
            return 0;

        return (minY1 > minY2)? 1 : -1;
    }
}
