package org.greenblitz.motion.fuzzylogic;

public class FuzzyValue {

    private String name;
    private IMemFunc inMemFunc;
    private IMemFunc outMemFunc;

    public FuzzyValue(String name, IMemFunc inMemFunc, IMemFunc outMemFunc){
        this.name = name;
        this.inMemFunc = inMemFunc;
        this.outMemFunc = outMemFunc;
    }

    public FuzzyValue(String name, IMemFunc MemFunc){
        this(name,MemFunc,null);
    }

    public String getName() {
        return name;
    }

    public IMemFunc getInMemFunc() {
        return inMemFunc;
    }

    public IMemFunc getOutMemFunc() {
        return outMemFunc;
    }

}
