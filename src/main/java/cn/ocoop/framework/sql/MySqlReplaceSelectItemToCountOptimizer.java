package cn.ocoop.framework.sql;

import com.alibaba.druid.sql.ast.expr.SQLAggregateExpr;
import com.alibaba.druid.sql.ast.expr.SQLIntegerExpr;
import com.alibaba.druid.sql.ast.expr.SQLVariantRefExpr;
import com.alibaba.druid.sql.ast.statement.SQLSelectItem;
import com.alibaba.druid.sql.dialect.mysql.ast.statement.MySqlSelectQueryBlock;
import com.alibaba.druid.sql.dialect.mysql.visitor.MySqlASTVisitorAdapter;

/**
 * Created by liolay on 2017/11/24.
 */
public class MySqlReplaceSelectItemToCountOptimizer extends MySqlASTVisitorAdapter implements SqlOptimizer {
    private boolean hasSQLVariantRefExpr = false;

    @Override
    public boolean visit(MySqlSelectQueryBlock x) {
        super.visit(x);
        for (SQLSelectItem sqlSelectItem : x.getSelectList()) {
            if (sqlSelectItem.getExpr() instanceof SQLVariantRefExpr) {
                hasSQLVariantRefExpr = true;
                return true;
            }
        }
        x.getSelectList().clear();
        SQLAggregateExpr count = new SQLAggregateExpr("COUNT");
        count.addArgument(new SQLIntegerExpr(1));
        x.getSelectList().add(new SQLSelectItem(count));
        return true;
    }

    @Override
    public String optimize(String sql) {
        String optimizedSql = SqlOptimizer.super.optimize(sql);
        if (hasSQLVariantRefExpr) return "SELECT COUNT(1) FROM (" + optimizedSql + ") A";
        return optimizedSql;
    }
}
