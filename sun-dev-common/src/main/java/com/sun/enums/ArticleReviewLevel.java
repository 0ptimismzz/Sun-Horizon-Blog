package com.sun.enums;

/**
 * @Desc: 文章自动审核结果 枚举
 */
public enum ArticleReviewLevel {
    PASS(1, "自动审核通过"),
    BLOCK(2, "自动审核不通过"),
    REVIEW(3, "疑似"),
    Error(4,"审核失败");

    public final Integer type;
    public final String value;

    ArticleReviewLevel(Integer type, String value) {
        this.type = type;
        this.value = value;
    }
}
