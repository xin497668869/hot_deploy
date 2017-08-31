package com.xin.stack;

import lombok.Data;

import java.util.List;

/**
 * @author linxixin@cvte.com
 * @version 1.0
 * @description
 */
@Data
public class ResponseData {
    private DbDetailInfoVo dbDetailInfoVo;
    private List<String>   sql;

    @Data
    public class DbDetailInfoVo {

        private String  url;
        private String  host;
        private Integer port;
        private String  username;
        private String  password;
        private String  dbName;


    }

}
