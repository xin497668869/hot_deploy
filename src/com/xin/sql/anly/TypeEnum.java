package com.xin.sql.anly;

/**
 * @Description:
 * @Create by: zhengamin
 * @Date: 2017/9/4 10:46
 * @Version: 1.0
 */
public enum TypeEnum {

    SYSTEM("system"),
    CONST("const"),
    EQ_REF("eq_ref"),
    REF("ref"),
    FULLTEXT("fulltext"),
    REF_OR_NULL("ref_or_null"),
    INDEX_MERGE("index_merge"),
    UNIQUE_SUBQUERY("unique_subquery"),
    INDEX_SUBQUERY("index_subquery"),
    RANGE("range"),
    INDEX("index"),
    ALL("all");

    private String type;

    TypeEnum(String type) {
        this.type = type;
    }

    public String getType() {
        return type;
    }
}
