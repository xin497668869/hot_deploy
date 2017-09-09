package com.xin.sql.anly;

import lombok.Getter;
import lombok.Setter;

/**
 * @Description:
 * @Create by: zhengamin@cvte.com
 * @Date: 2017/9/2 10:36
 * @Version: 1.0
 */
@Setter
@Getter
public class ExplainResult {

    private Integer id;
    private String  selectType;
    private String  table;
    private String  partitions;
    private String  type;
    private String  possibleKeys;
    private String  key;
    private Integer keyLen;
    private String  ref;
    private Integer rows;
    private String  filtered;
    private String  extra;

}
