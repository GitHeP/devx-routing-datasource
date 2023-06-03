package com.github.devx.routing.sql.parser;

import com.github.devx.routing.RoutingTargetType;
import com.github.devx.routing.sql.AnnotationSqlAttribute;
import com.github.devx.routing.sql.DefaultSqlAttribute;
import com.github.devx.routing.sql.SqlAttribute;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * @author Peng He
 * @see AnnotationSqlParser
 * @since 1.0
 */
class AnnotationSqlParserTest {

    static SqlParser sqlParser;

    @BeforeAll
    static void init() {
        List<SqlHintVisitor> visitors = new ArrayList<>();
        visitors.add(new RoutingTargetTypeVisitor());
        sqlParser = new AnnotationSqlParser(new JSqlParser(), new DefaultAnnotationSqlHintParser(visitors));
    }

    @Test
    void parse_1() {

        String sql = "/*!routingType=write;xxx=ok;timeout=30s;*/ select * from employee where id = ?";
        SqlAttribute attribute = sqlParser.parse(sql);
        assertThat(attribute).isNotNull().isInstanceOf(AnnotationSqlAttribute.class);
        AnnotationSqlAttribute annotationSqlAttribute = (AnnotationSqlAttribute) attribute;
        assertThat(annotationSqlAttribute.getSqlHint()).isNotNull()
                .extracting(SqlHint::getRoutingTargetType).extracting(RoutingTargetType::isWrite).isEqualTo(true);

    }

    @Test
    void parse_2() {

        String sql = "/*!routingType=write;*/ select * from employee where id = ?";
        SqlAttribute attribute = sqlParser.parse(sql);
        assertThat(attribute).isNotNull().isInstanceOf(AnnotationSqlAttribute.class);
        AnnotationSqlAttribute annotationSqlAttribute = (AnnotationSqlAttribute) attribute;
        assertThat(annotationSqlAttribute.getSqlHint()).isNotNull()
                .extracting(SqlHint::getRoutingTargetType).extracting(RoutingTargetType::isWrite).isEqualTo(true);

    }

    @Test
    void parse_3() {

        String sql = "/*!routingType=write */ select * from employee where id = ?";
        SqlAttribute attribute = sqlParser.parse(sql);
        assertThat(attribute).isNotNull().isInstanceOf(AnnotationSqlAttribute.class);
        AnnotationSqlAttribute annotationSqlAttribute = (AnnotationSqlAttribute) attribute;
        assertThat(annotationSqlAttribute.getSqlHint()).isNotNull()
                .extracting(SqlHint::getRoutingTargetType).extracting(RoutingTargetType::isWrite).isEqualTo(true);

    }
}