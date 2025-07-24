package com.sun.enums;

public enum AIContent {
    ARTICLE_REVIEW("请判断如下文章是否有意义，如果有意义，" +
            "请用简要中文文字概括该文章，如果没意义请直接回复“没意义”：");

    public final String value;

    private AIContent(String value) {
        this.value = value;
    }
}
