package cn.ocoop.framework.sql;

import com.alibaba.druid.sql.ast.statement.SQLSelectGroupByClause;
import com.alibaba.druid.sql.dialect.mysql.visitor.MySqlASTVisitorAdapter;
import org.apache.commons.collections4.CollectionUtils;

/**
 * Created by liolay on 2017/12/2.
 */
public class MySqlGroupByMarkOptimizer extends MySqlASTVisitorAdapter implements SqlOptimizer {
    protected boolean hasGroupByClause = false;

    public boolean visit(SQLSelectGroupByClause x) {
        super.visit(x);

        if (hasGroupByClause) return true;
        if (CollectionUtils.isNotEmpty(x.getItems())) {
            hasGroupByClause = true;
        }
        return true;
    }
}
