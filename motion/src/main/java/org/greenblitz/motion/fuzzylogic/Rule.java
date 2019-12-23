package org.greenblitz.motion.fuzzylogic;

import java.util.List;

public class Rule {

    private FuzzyValue[] conditions;
    private FuzzyValue result;

    public Rule(FuzzyValue[] conditions, FuzzyValue result){
        this.conditions = conditions;
        this.result = result;
    }

    public FuzzyValue[] getConditions() {
        return conditions;
    }

    public FuzzyValue getResult() {
        return result;
    }
}
