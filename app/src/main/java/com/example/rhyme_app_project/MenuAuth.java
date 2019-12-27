package com.example.rhyme_app_project;

public class MenuAuth {
    private String funcName;
    private String authorValue;

    public String getFuncName() {
        return funcName;
    }

    public void setFuncName(String funcName) {
        this.funcName = funcName;
    }

    public String getValue() { return authorValue; }

    public void setValue(String authorValue) {
        this.authorValue = authorValue;
    }

//    @Override
//    public String toString() {
//        return "User [userID=" + userID + ", PW=" + PW + ", CompID="
//                + CompID + "]";
//    }
}
