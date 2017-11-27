package cn.ocoop.framework.sql;

import com.alibaba.druid.sql.ast.SQLOrderBy;
import com.alibaba.druid.sql.dialect.mysql.visitor.MySqlASTVisitorAdapter;

/**
 * Created by liolay on 2017/11/24.
 */
public class MySqlRemoveOrderByOptimizer extends MySqlASTVisitorAdapter implements SqlOptimizer {

    @Override
    public boolean visit(SQLOrderBy x) {
        super.visit(x);
        x.getItems().clear();
        return true;
    }

}
