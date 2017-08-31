package com.xin.stack;

import lombok.Data;

import java.util.ArrayList;
import java.util.List;

/**
 * @author linxixin@cvte.com
 * @version 1.0
 * @description
 */
@Data
public class TreeVo {
    private String       className;
    private String       methodName;
    private List<String> paramClassNames;
    private Long         timeConsuming;
    private List<TreeVo> treeVo = new ArrayList<>();

    public TreeVo() {
    }

    public TreeVo(String className, String methodName, List<String> paramClassNames, Long timeConsuming) {
        this.className = className;
        this.methodName = methodName;
        this.paramClassNames = paramClassNames;
        this.timeConsuming = timeConsuming;
    }
}
