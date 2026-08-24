package com.demo.myfirstagent.model;

public record ToolResponse<T> (
        boolean success,
        String requiredTool,
        T data,
        ToolError error
){
    public static <T> ToolResponse<T> success(T data){
        return new ToolResponse<>(true,  null, data, null);
    }

    public static <T> ToolResponse<T> blocked(String error, String requiredTool){
        return new ToolResponse<>(false,  requiredTool, null, new ToolError("validation", false, error, null));
    }

    public static <T> ToolResponse<T> error(ToolError error){
        return new ToolResponse<>(false, null, null, error);
    }
}
