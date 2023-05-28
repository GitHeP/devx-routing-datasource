package com.github.devx.routing.datasource.sql.parser;

import lombok.extern.slf4j.Slf4j;
import net.sf.jsqlparser.JSQLParserException;
import net.sf.jsqlparser.parser.CCJSqlParserUtil;
import net.sf.jsqlparser.schema.Table;
import net.sf.jsqlparser.statement.Commit;
import net.sf.jsqlparser.statement.CreateFunctionalStatement;
import net.sf.jsqlparser.statement.ExplainStatement;
import net.sf.jsqlparser.statement.RollbackStatement;
import net.sf.jsqlparser.statement.ShowColumnsStatement;
import net.sf.jsqlparser.statement.Statement;
import net.sf.jsqlparser.statement.alter.Alter;
import net.sf.jsqlparser.statement.alter.AlterSession;
import net.sf.jsqlparser.statement.alter.AlterSystemStatement;
import net.sf.jsqlparser.statement.alter.RenameTableStatement;
import net.sf.jsqlparser.statement.alter.sequence.AlterSequence;
import net.sf.jsqlparser.statement.create.function.CreateFunction;
import net.sf.jsqlparser.statement.create.index.CreateIndex;
import net.sf.jsqlparser.statement.create.schema.CreateSchema;
import net.sf.jsqlparser.statement.create.table.CreateTable;
import net.sf.jsqlparser.statement.create.view.AlterView;
import net.sf.jsqlparser.statement.delete.Delete;
import net.sf.jsqlparser.statement.insert.Insert;
import net.sf.jsqlparser.statement.select.FromItem;
import net.sf.jsqlparser.statement.select.Join;
import net.sf.jsqlparser.statement.select.PlainSelect;
import net.sf.jsqlparser.statement.select.Select;
import net.sf.jsqlparser.statement.select.SelectBody;
import net.sf.jsqlparser.statement.select.SubJoin;
import net.sf.jsqlparser.statement.select.SubSelect;
import net.sf.jsqlparser.statement.show.ShowTablesStatement;
import net.sf.jsqlparser.statement.truncate.Truncate;
import net.sf.jsqlparser.statement.update.Update;
import net.sf.jsqlparser.util.TablesNamesFinder;

import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;

/**
 * Parse SQL statements using JSqlParser.
 * <p>https://github.com/JSQLParser/JSqlParser<p/>
 *
 * @author he peng
 * @since 1.0
 */

@Slf4j
public class JSqlParser implements SqlParser {


    private static final Set<Class<? extends Statement>> WRITE_STATEMENTS = Collections.synchronizedSet(new HashSet<>());

    private static final Set<Class<? extends Statement>> READ_STATEMENTS = Collections.synchronizedSet(new HashSet<>());

    static {

        // register write statement
        registerWriteStatement(Delete.class);
        registerWriteStatement(Insert.class);
        registerWriteStatement(Update.class);
        registerWriteStatement(Truncate.class);
        registerWriteStatement(CreateSchema.class);
        registerWriteStatement(CreateTable.class);
        registerWriteStatement(CreateIndex.class);
        registerWriteStatement(CreateFunction.class);
        registerWriteStatement(CreateFunctionalStatement.class);
        registerWriteStatement(RenameTableStatement.class);
        registerWriteStatement(Commit.class);
        registerWriteStatement(RollbackStatement.class);
        registerWriteStatement(Alter.class);
        registerWriteStatement(AlterSession.class);
        registerWriteStatement(AlterSystemStatement.class);
        registerWriteStatement(AlterView.class);
        registerWriteStatement(AlterSequence.class);

        // register read statement
        registerReadStatement(Select.class);
        registerReadStatement(ExplainStatement.class);
        registerReadStatement(ShowColumnsStatement.class);
        registerReadStatement(ShowTablesStatement.class);

    }

    private static void registerWriteStatement(Class<? extends Statement> type) {
        WRITE_STATEMENTS.add(type);
    }

    private static void registerReadStatement(Class<? extends Statement> type) {
        READ_STATEMENTS.add(type);
    }

    @Override
    public SqlStatement parse(String sql) {
        SqlStatement sqlStatement;
        try {
            Statement statement = CCJSqlParserUtil.parse(sql);
            SqlStatementVisitor visitor = new SqlStatementVisitor();
            sqlStatement = visitor.build(statement).setSql(sql);
        } catch (JSQLParserException e) {
            log.warn("Cannot be parsed SQL statement [{}]", sql);
            sqlStatement = null;
        }
        return sqlStatement;
    }

    private static class SqlStatementVisitor extends TablesNamesFinder implements SqlStatementBuilder<Statement> {

        private final Set<String> databases = new HashSet<>();

        private final Set<String> subTables = new HashSet<>();

        private final Set<String> joinTables = new HashSet<>();

        private String fromTable;

        private final SqlStatement statement = new SqlStatement();


        @Override
        public SqlStatement build(Statement obj) {
            List<String> tableList = getTableList(obj);
            return statement.setStatement(obj).setDatabases(databases).setFromTable(fromTable)
                    .setTables(new HashSet<>(tableList)).setJoinTables(joinTables).setSubTables(subTables)
                    .setWrite(isWrite(obj)).setRead(isRead(obj));
        }

        @Override
        public void visit(SubJoin subjoin) {
            super.visit(subjoin);

            List<Join> joinList = subjoin.getJoinList();
            if (Objects.nonNull(joinList)) {
                for (Join join : joinList) {
                    FromItem rightItem = join.getRightItem();
                    if (rightItem instanceof Table) {
                        Table joinTable = (Table) rightItem;
                        joinTables.add(joinTable.getName());
                    }
                }
            }
        }

        @Override
        public void visit(Table tableName) {
            super.visit(tableName);
            String database = tableName.getSchemaName();
            if (Objects.nonNull(database)) {
                databases.add(database);
            }
        }

        @Override
        public void visit(SubSelect subSelect) {
            super.visit(subSelect);
            SelectBody selectBody = subSelect.getSelectBody();
            if (selectBody instanceof PlainSelect) {
                PlainSelect select = (PlainSelect) selectBody;
                FromItem fromItem = select.getFromItem();
                if (fromItem instanceof Table) {
                    subTables.add(((Table) fromItem).getName());
                }
            }
        }

        @Override
        public void visit(PlainSelect plainSelect) {
            super.visit(plainSelect);

            FromItem fromItem = plainSelect.getFromItem();
            if (fromItem instanceof Table) {
                this.fromTable = ((Table) fromItem).getName();
            }

            List<Join> joins = plainSelect.getJoins();
            if (Objects.nonNull(joins)) {
                for (Join join : joins) {
                    FromItem rightItem = join.getRightItem();
                    if (rightItem instanceof Table) {
                        Table joinTable = (Table) rightItem;
                        joinTables.add(joinTable.getName());
                    }
                }
            }
        }

        private boolean isRead(Statement statement) {
            return READ_STATEMENTS.contains(statement.getClass());
        }

        private boolean isWrite(Statement statement) {
            return WRITE_STATEMENTS.contains(statement.getClass());
        }
    }
}
