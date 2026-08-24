package com.demo.myfirstagent.guard;

public record ToolDecision (
        boolean allowed,
        String reason,
        String requiredTool
){
    public static ToolDecision allow(){
        return new ToolDecision(true, null, null);
    }

    public static ToolDecision deny(String reason, String requiredtool){
        return new ToolDecision(false, reason, requiredtool);
    }
}
