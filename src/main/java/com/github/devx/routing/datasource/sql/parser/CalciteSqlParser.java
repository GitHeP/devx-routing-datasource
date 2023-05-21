package com.github.devx.routing.datasource.sql.parser;

/**
 * @author he peng
 * @since 1.0
 */
public class CalciteSqlParser implements SqlParser {

    @Override
    public SqlStatement parse(String sql) {
        //org.apache.calcite.sql.parser.SqlParser.Config config = org.apache.calcite.sql.parser.SqlParser.config()
        //        .withQuoting(Quoting.SINGLE_QUOTE);
        //org.apache.calcite.sql.parser.SqlParser parser = org.apache.calcite.sql.parser.SqlParser.create(sql, config);
        //try {
        //    SqlNode sqlNode = parser.parseStmt();
        //    SqlNode sqlNode1 = parser.parseQuery();
        //    SqlNode sqlNode2 = parser.parseExpression();
        //    System.out.println();
        //} catch (SqlParseException e) {
        //    throw new RuntimeException(e);
        //}


        return null;
    }
}
