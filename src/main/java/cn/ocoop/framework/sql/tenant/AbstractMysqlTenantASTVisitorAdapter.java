package cn.ocoop.framework.sql.tenant;

import cn.ocoop.framework.sql.SqlOptimizer;
import com.alibaba.druid.sql.dialect.mysql.visitor.MySqlASTVisitorAdapter;

public abstract class AbstractMysqlTenantASTVisitorAdapter extends MySqlASTVisitorAdapter implements SqlOptimizer {
    protected String tenantColumn;
    protected String tenantColumnType;

    public AbstractMysqlTenantASTVisitorAdapter(String tenantColumn, String tenantColumnType) {
        this.tenantColumn = tenantColumn;
        this.tenantColumnType = tenantColumnType;
    }
}
