package com.github.devx.routing.sql.parser;

import com.github.devx.routing.sql.DefaultSqlAttribute;
import com.github.devx.routing.sql.SqlAttribute;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Peng He
 * @since 1.0
 *
 * @see AnnotationSqlParser
 */
class AnnotationSqlParserTest {

    static SqlParser sqlParser;

    @BeforeAll
    static void init() {
        List<SqlHintVisitor> visitors = new ArrayList<>();
        visitors.add(new RoutingTargetTypeVisitor());
        sqlParser = new AnnotationSqlParser(new JSqlParser() , new DefaultAnnotationSqlHintParser(visitors));
    }

    @Test
    void parse() {

        String sql = "/*!routingType=write;xxx=ok;timeout=30s;*/ select * from employee where id = ?";
        SqlAttribute attribute = sqlParser.parse(sql);
        System.out.println();
    }
}