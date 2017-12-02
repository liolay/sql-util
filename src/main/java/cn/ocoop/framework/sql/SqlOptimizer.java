package cn.ocoop.framework.sql;

import com.alibaba.druid.sql.SQLUtils;
import com.alibaba.druid.sql.ast.SQLStatement;
import com.alibaba.druid.sql.visitor.SQLASTVisitor;
import com.alibaba.druid.util.JdbcUtils;
import org.apache.commons.collections4.CollectionUtils;

import java.util.List;

/**
 * Created by liolay on 2017/11/24.
 */
public interface SqlOptimizer extends SQLASTVisitor {
    default String optimize(String sql) {
        List<SQLStatement> sqlStatements = SQLUtils.parseStatements(sql, JdbcUtils.MYSQL);
        if (CollectionUtils.isNotEmpty(sqlStatements)) {
            SQLStatement sqlStatement = sqlStatements.get(0);
            sqlStatement.accept(this);
            return sqlStatement.toString();
        }
        return sql;
    }
}
