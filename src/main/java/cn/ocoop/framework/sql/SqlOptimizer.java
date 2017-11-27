package cn.ocoop.framework.sql;

import com.alibaba.druid.sql.SQLUtils;
import com.alibaba.druid.sql.ast.SQLStatement;
import com.alibaba.druid.sql.visitor.SQLASTVisitor;
import com.alibaba.druid.util.JdbcUtils;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by liolay on 2017/11/24.
 */
public interface SqlOptimizer extends SQLASTVisitor {
    default List<String> optimize(String sql) {
        List<String> optimizedSql = new ArrayList<>();

        List<SQLStatement> sqlStatements = SQLUtils.parseStatements(sql, JdbcUtils.MYSQL);
        for (SQLStatement sqlStatement : sqlStatements) {
            sqlStatement.accept(this);
            optimizedSql.add(sqlStatement.toString());
        }

        return optimizedSql;
    }
}
