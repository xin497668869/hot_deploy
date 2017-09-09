package com.xin.sql.anly;

/**
 * @Description:
 * @Create by: zhengamin@cvte.com
 * @Date: 2017/9/2 11:15
 * @Version: 1.0
 */
public enum SystemTypeEnum {

    SIMPLE("简单查询，查询子句不包含UNION或子查询"),
    PRIMARY("最外层的SELECT子句"),
    UNION("UNION中的第二个或后面的SELECT语句"),
    DEPENDENTUNION("UNION中的第二个或后面的SELECT语句，取决于外面的查询"),
    UNIONRESULT("UNION的结果"),
    SUBQUERY("子查询中第一个SELECT"),
    DEPENDENTSUBQUERY("第一个查询是子查询，依赖于外部查询"),
    DERIVED("在from 查询语句中的（派生，嵌套很多）子查询"),
    MATERIALIZED("雾化子查询，（子查询是个视图？）"),
    UNCACHEABLESUBQUERY("子查询结果不能被缓存， 而且必须重写（分析）外部查询的每一行"),
    UNCACHEABLEUNION("第二个或在最后一个union查询之后的select，属于不可缓存的查询");

    private final String message;

    SystemTypeEnum(String message) {
        this.message = message;
    }

    public String getMessage() {
        return message;
    }
}
