package cn.ocoop.framework.sql.tenant;

import cn.ocoop.framework.sql.TC;
import com.alibaba.druid.sql.ast.SQLExpr;
import com.alibaba.druid.sql.ast.SQLObject;
import com.alibaba.druid.sql.ast.expr.*;
import com.alibaba.druid.sql.ast.statement.SQLExprTableSource;
import com.alibaba.druid.sql.ast.statement.SQLJoinTableSource;
import com.alibaba.druid.sql.ast.statement.SQLTableSource;
import com.alibaba.druid.sql.dialect.mysql.ast.expr.MySqlCharExpr;
import com.alibaba.druid.sql.dialect.mysql.ast.statement.MySqlDeleteStatement;
import com.alibaba.druid.sql.dialect.mysql.ast.statement.MySqlSelectQueryBlock;
import org.apache.commons.lang3.StringUtils;

import java.util.List;

/**
 * Created by liolay on 2017/11/8.
 */
public class MySqlDeleteTenantOptimizer extends AbstractMysqlTenantASTVisitorAdapter {
    public MySqlDeleteTenantOptimizer(String tenantColumn, String tenantColumnType) {
        super(tenantColumn, tenantColumnType);
    }

    @Override
    public boolean visit(MySqlDeleteStatement x) {
        super.visit(x);

        SQLExprTableSource sqlTableSource = recursiveGetOwner(x.getTableSource());
        if (sqlTableSource == null) return true;

        SQLExpr where = x.getWhere();

        SQLExpr leftExpr;
        if (StringUtils.isNotBlank(sqlTableSource.getAlias())) {
            leftExpr = new SQLPropertyExpr(sqlTableSource.getAlias(), tenantColumn);
        } else {
            leftExpr = new SQLIdentifierExpr(tenantColumn);
        }

        SQLExpr rightExpr;
        if ("String".equals(tenantColumnType)) {
            rightExpr = new MySqlCharExpr((String) TC.get());
        } else {
            rightExpr = new SQLIntegerExpr((Number) TC.get());
        }

        SQLBinaryOpExpr sqlBinaryOpExpr = new SQLBinaryOpExpr(
                leftExpr,
                SQLBinaryOperator.Equality,
                rightExpr
        );


        boolean conditionExist = false;
        if (where != null) {
            List<SQLObject> wheres = where.getChildren();
            if (wheres.contains(sqlBinaryOpExpr)) conditionExist = true;
        }
        if (!conditionExist) {
            x.addCondition(sqlBinaryOpExpr);
        }
        return true;
    }


    @Override
    public boolean visit(MySqlSelectQueryBlock x) {
        return new MySqlSelectTenantOptimizer(tenantColumn, tenantColumnType).visit(x);
    }


    public SQLExprTableSource recursiveGetOwner(SQLTableSource sqlTableSource) {
        if (sqlTableSource instanceof SQLExprTableSource) {
            return (SQLExprTableSource) sqlTableSource;
        }

        if (sqlTableSource instanceof SQLJoinTableSource) {
            SQLJoinTableSource tableSource = (SQLJoinTableSource) sqlTableSource;
            return recursiveGetOwner(tableSource.getLeft());
        }

        return null;
    }
}
