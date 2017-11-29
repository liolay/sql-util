package cn.ocoop.framework.sql.tenant;

import cn.ocoop.framework.sql.TC;
import com.alibaba.druid.sql.ast.SQLExpr;
import com.alibaba.druid.sql.ast.expr.SQLIdentifierExpr;
import com.alibaba.druid.sql.ast.expr.SQLIntegerExpr;
import com.alibaba.druid.sql.ast.expr.SQLPropertyExpr;
import com.alibaba.druid.sql.ast.statement.*;
import com.alibaba.druid.sql.dialect.mysql.ast.expr.MySqlCharExpr;
import com.alibaba.druid.sql.dialect.mysql.ast.statement.MySqlInsertStatement;
import com.alibaba.druid.sql.dialect.mysql.ast.statement.MySqlSelectQueryBlock;
import org.apache.commons.lang3.StringUtils;

import java.util.List;

/**
 * Created by liolay on 2017/11/8.
 */
public class MySqlInsertTenantOptimizer extends AbstractMysqlTenantASTVisitorAdapter {
    private int insertColumnNum = 0;

    public MySqlInsertTenantOptimizer(String tenantColumn, String tenantColumnType) {
        super(tenantColumn, tenantColumnType);
    }

    @Override
    public boolean visit(MySqlInsertStatement x) {
        super.visit(x);

        SQLIdentifierExpr merchantIdColumn = new SQLIdentifierExpr(tenantColumn);
        if (!x.getColumns().contains(merchantIdColumn)) {
            x.addColumn(merchantIdColumn);
            for (SQLInsertStatement.ValuesClause valuesClause : x.getValuesList()) {
                SQLExpr merchantIdValue;
                if ("String".equals(tenantColumnType)) {
                    merchantIdValue = new MySqlCharExpr((String) TC.get());
                } else {
                    merchantIdValue = new SQLIntegerExpr((Number) TC.get());
                }
                if (!valuesClause.getValues().contains(merchantIdValue)) {
                    valuesClause.addValue(merchantIdValue);
                }
            }
        }

        insertColumnNum = x.getColumns().size();
        return true;
    }

    public boolean visit(MySqlSelectQueryBlock x) {
        super.visit(x);

        SQLExprTableSource sqlTableSource = recursiveGetOwner(x.getFrom());
        List<SQLSelectItem> selectList = x.getSelectList();

        if (selectList.size() == insertColumnNum - 1) {

            SQLExpr leftExpr;
            if (StringUtils.isNotBlank(sqlTableSource.getAlias())) {
                leftExpr = new SQLPropertyExpr(sqlTableSource.getAlias(), tenantColumn);
            } else {
                leftExpr = new SQLIdentifierExpr(tenantColumn);
            }

            SQLSelectItem sqlSelectItem = new SQLSelectItem(leftExpr);
            if (!selectList.contains(sqlSelectItem)) {
                x.addSelectItem(sqlSelectItem);
            }
        }

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
