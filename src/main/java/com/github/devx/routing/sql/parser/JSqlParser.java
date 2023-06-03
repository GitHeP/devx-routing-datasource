/*
 *    Copyright 2023 the original author or authors.
 *
 *    Licensed under the Apache License, Version 2.0 (the "License");
 *    you may not use this file except in compliance with the License.
 *    You may obtain a copy of the License at
 *
 *       https://www.apache.org/licenses/LICENSE-2.0
 *
 *    Unless required by applicable law or agreed to in writing, software
 *    distributed under the License is distributed on an "AS IS" BASIS,
 *    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *    See the License for the specific language governing permissions and
 *    limitations under the License.
 */

package com.github.devx.routing.sql.parser;

import com.github.devx.routing.sql.SqlStatement;
import com.github.devx.routing.sql.SqlStatementBuilder;
import com.github.devx.routing.sql.SqlType;
import lombok.extern.slf4j.Slf4j;
import net.sf.jsqlparser.JSQLParserException;
import net.sf.jsqlparser.Model;
import net.sf.jsqlparser.expression.Expression;
import net.sf.jsqlparser.parser.CCJSqlParserUtil;
import net.sf.jsqlparser.schema.Table;
import net.sf.jsqlparser.statement.ExplainStatement;
import net.sf.jsqlparser.statement.Statement;
import net.sf.jsqlparser.statement.delete.Delete;
import net.sf.jsqlparser.statement.insert.Insert;
import net.sf.jsqlparser.statement.select.FromItem;
import net.sf.jsqlparser.statement.select.Join;
import net.sf.jsqlparser.statement.select.PlainSelect;
import net.sf.jsqlparser.statement.select.Select;
import net.sf.jsqlparser.statement.select.SelectBody;
import net.sf.jsqlparser.statement.select.SubJoin;
import net.sf.jsqlparser.statement.select.SubSelect;
import net.sf.jsqlparser.statement.update.Update;
import net.sf.jsqlparser.statement.update.UpdateSet;
import net.sf.jsqlparser.util.TablesNamesFinder;

import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;

/**
 * Parse SQL statements using JSqlParser.
 * <p><a href="https://github.com/JSQLParser/JSqlParser">JSqlParser</a><p/>
 *
 * @author Peng He
 * @since 1.0
 */

@Slf4j
public class JSqlParser implements SqlParser {

    private static final Set<Class<? extends Statement>> READ_STATEMENTS = Collections.synchronizedSet(new HashSet<>());

    static {
        // register read statement
        registerReadStatement(Select.class);
        registerReadStatement(ExplainStatement.class);

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

        private final SqlStatement statement = new SqlStatement();

        private final Set<String> databases = new HashSet<>();

        private final Set<String> tables = new HashSet<>();

        private final Set<String> subTables = new HashSet<>();

        private final Set<String> joinTables = new HashSet<>();

        private final Set<String> normalTables = new HashSet<>();

        private SqlType statementType = SqlType.OTHER;

        private Model prevVisited;

        @Override
        public SqlStatement build(Statement obj) {
            getTableList(obj);
            if (normalTables.size() > subTables.size() && normalTables.containsAll(subTables)) {
                normalTables.removeAll(subTables);
            }
            if (normalTables.size() > joinTables.size() && normalTables.containsAll(joinTables)) {
                normalTables.removeAll(joinTables);
            }
            databases.removeIf(Objects::isNull);
            tables.removeIf(Objects::isNull);
            normalTables.removeIf(Objects::isNull);
            joinTables.removeIf(Objects::isNull);
            subTables.removeIf(Objects::isNull);
            prevVisited = null;
            return statement.setStatement(obj).setDatabases(databases).setNormalTables(normalTables)
                    .setTables(tables).setJoinTables(joinTables).setSubTables(subTables)
                    .setWrite(isWrite(obj)).setRead(isRead(obj)).setStatementType(statementType);
        }

        @Override
        public void visit(Insert insert) {
            prevVisited = insert;
            super.visit(insert);
            Table table = insert.getTable();
            if (!normalTables.isEmpty()) {
                normalTables.clear();
            }
            normalTables.add(table.getName());
            databases.add(table.getSchemaName());
            statementType = SqlType.INSERT;
        }

        @Override
        public void visit(Update update) {
            prevVisited = update;
            super.visit(update);
            Table table = update.getTable();
            normalTables.add(table.getName());
            databases.add(table.getSchemaName());

            // parse set fragment sub select
            List<UpdateSet> sets = update.getUpdateSets();
            if (Objects.nonNull(sets) && !sets.isEmpty()) {
                for (UpdateSet set : sets) {
                    for (Expression expression : set.getExpressions()) {
                        expression.accept(this);
                    }
                }
            }
            statementType = SqlType.UPDATE;
        }

        @Override
        public void visit(Delete delete) {
            super.visit(delete);
            Table table = delete.getTable();
            normalTables.add(table.getName());
            databases.add(table.getSchemaName());
            statementType = SqlType.DELETE;
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
        public void visit(Table table) {
            super.visit(table);
            String database = table.getSchemaName();
            if (Objects.nonNull(database)) {
                databases.add(database);
            }
            tables.add(table.getName());
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
            if (Objects.equals(prevVisited , plainSelect)) {
                return;
            }
            prevVisited = plainSelect;
            super.visit(plainSelect);

            FromItem fromItem = plainSelect.getFromItem();
            if (fromItem instanceof Table) {
                String name = ((Table) fromItem).getName();
                normalTables.add(name);
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
            statementType = SqlType.SELECT;
        }

        private boolean isRead(Statement statement) {
            return READ_STATEMENTS.contains(statement.getClass());
        }

        private boolean isWrite(Statement statement) {
            return !isRead(statement);
        }
    }
}
