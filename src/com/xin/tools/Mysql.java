package com.xin.tools;

import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.Messages;
import com.xin.stack.ResponseData;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;

/**
 * @author linxixin@cvte.com
 * @version 1.0
 * @description
 */
public class Mysql {
    static {
        try {
            //调用Class.forName()方法加载驱动程序
            Class.forName("com.mysql.jdbc.Driver");
            System.out.println("成功加载MySQL驱动！");
        } catch (ClassNotFoundException e1) {
            System.out.println("找不到MySQL驱动!");
            e1.printStackTrace();
        }
    }

    @FunctionalInterface
    public interface RunSql {
        void run(Statement statement);
    }

    public static void runSql(Project project, ResponseData.DbDetailInfoVo dbDetailInfoVo, RunSql runSql) {
        if (dbDetailInfoVo == null || dbDetailInfoVo.getUrl() == null || dbDetailInfoVo.getUsername() == null || dbDetailInfoVo.getPassword() == null) {
            Messages.showErrorDialog(project, "数据库信息获取失败, 无法执行 " + dbDetailInfoVo, "提示");
            return;
        }
        try (Connection conn = DriverManager.getConnection(dbDetailInfoVo.getUrl(), dbDetailInfoVo.getUsername(), dbDetailInfoVo.getPassword())) {
            //创建一个Statement对象
            try (Statement stmt = conn.createStatement()) { //创建Statement对象 {
                runSql.run(stmt);
            }
            System.out.print("成功连接到数据库！");
        } catch (SQLException e) {
            Messages.showErrorDialog(project, "数据库连接失败 " + e.getMessage() + "\n" + dbDetailInfoVo, "提示");
            e.printStackTrace();
            return;
        }
    }
}
