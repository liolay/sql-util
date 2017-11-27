package cn.ocoop.framework.sql;

import com.alibaba.druid.sql.SQLUtils;
import com.alibaba.druid.sql.ast.SQLStatement;
import com.alibaba.druid.sql.ast.expr.SQLAggregateExpr;
import com.alibaba.druid.sql.ast.expr.SQLIntegerExpr;
import com.alibaba.druid.sql.ast.statement.SQLSelectItem;
import com.alibaba.druid.sql.dialect.mysql.ast.statement.MySqlSelectQueryBlock;
import com.alibaba.druid.sql.dialect.mysql.visitor.MySqlASTVisitorAdapter;
import com.alibaba.druid.util.JdbcUtils;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by liolay on 2017/11/24.
 */
public class MySqlReplaceSelectItemToCountOptimizer extends MySqlASTVisitorAdapter implements SqlOptimizer {

    @Override
    public boolean visit(MySqlSelectQueryBlock x) {
        super.visit(x);
        x.getSelectList().clear();
        SQLAggregateExpr count = new SQLAggregateExpr("COUNT");
        count.addArgument(new SQLIntegerExpr(1));
        x.getSelectList().add(new SQLSelectItem(count));
        return true;
    }

    @Override
    public List<String> optimize(String sql) {
        List<String> optimizedSql = new ArrayList<>();

        List<SQLStatement> sqlStatements = SQLUtils.parseStatements(sql, JdbcUtils.MYSQL);
        for (SQLStatement sqlStatement : sqlStatements) {
            sqlStatement.accept(this);
            optimizedSql.add(sqlStatement.toString());
        }

        return optimizedSql;
    }
}
