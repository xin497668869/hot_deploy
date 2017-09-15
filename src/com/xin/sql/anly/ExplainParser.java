package com.xin.sql.anly;

import org.apache.commons.lang3.StringUtils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @Description:
 * @Create by: zhengamin@cvte.com
 * @Date: 2017/9/2 10:35
 * @Version: 1.0
 */
public class ExplainParser {

    private static       Map<String, String> systemTypes      = new HashMap<>();
    private static       List<String>        types            = new ArrayList<>();
    private static final String              LOWER_LIMIT_TYPE = "range";

    static {
        Arrays.stream(SystemTypeEnum.values()).forEach(systemTypeEnum -> systemTypes.put(systemTypeEnum.name(), systemTypeEnum.getMessage()));
        Arrays.stream(TypeEnum.values()).forEach(typeEnum -> types.add(typeEnum.getType()));
    }

    public static String parser(ExplainResult result) {
        StringBuilder stringBuilder = new StringBuilder();
        if (!StringUtils.isEmpty(result.getSelectType())) {
            stringBuilder.append(systemTypes.get(result.getSelectType().toUpperCase().replaceAll("\\s*", "")));
        }

        if (result.getPossibleKeys() != null) {
            stringBuilder.append("可能用到的索引：" + result.getPossibleKeys() + "\n");
            stringBuilder.append("实际使用索引：" + result.getKey() + "\n");
        }

        stringBuilder.append("访问类型排行：system > const > eq_ref > ref > index_merge > unique_subquery > index_subquery > range > index > ALL" + "\n");
        stringBuilder.append("访问类型:" + result.getType() + "\n");
        stringBuilder.append("访问类型性能：" + (types.indexOf(result.getType().toLowerCase()) <= types.indexOf(LOWER_LIMIT_TYPE) ? "好" : "差" + "\n"));
        return stringBuilder.toString();
    }
}
