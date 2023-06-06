package com.lp.sql.routing.sql.parser;

import com.lp.sql.routing.RoutingTargetType;
import com.lp.sql.routing.sql.AnnotationSqlAttribute;
import com.lp.sql.routing.sql.SqlAttribute;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * @author Peng He
 * @see AnnotationSqlParser
 * @since 1.0
 */
class AnnotationSqlParserTest {

    static SqlParser sqlParser = new AnnotationSqlParser(new JSqlParser(), new DefaultAnnotationSqlHintParser());

    static SqlHintConverter<RoutingTargetType> sqlHintConverter = new RoutingTypeSqlHintConverter();


    @Test
    void parse_1() {

        String sql = "/*!routingType=write;xxx=ok;timeout=30s;*/ select * from employee where id = ?";
        SqlAttribute attribute = sqlParser.parse(sql);
        assertThat(attribute).isNotNull().isInstanceOf(AnnotationSqlAttribute.class);
        AnnotationSqlAttribute annotationSqlAttribute = (AnnotationSqlAttribute) attribute;
        assertThat(annotationSqlAttribute.getSqlHint()).isNotNull()
                .extracting(hint -> sqlHintConverter.convert(hint)).extracting(RoutingTargetType::isWrite).isEqualTo(true);

    }

    @Test
    void parse_2() {

        String sql = "/*!routingType=write;*/ select * from employee where id = ?";
        SqlAttribute attribute = sqlParser.parse(sql);
        assertThat(attribute).isNotNull().isInstanceOf(AnnotationSqlAttribute.class);
        AnnotationSqlAttribute annotationSqlAttribute = (AnnotationSqlAttribute) attribute;
        assertThat(annotationSqlAttribute.getSqlHint()).isNotNull()
                .extracting(hint -> sqlHintConverter.convert(hint)).extracting(RoutingTargetType::isWrite).isEqualTo(true);

    }

    @Test
    void parse_3() {

        String sql = "/*!routingType=write */ select * from employee where id = ?";
        SqlAttribute attribute = sqlParser.parse(sql);
        assertThat(attribute).isNotNull().isInstanceOf(AnnotationSqlAttribute.class);
        AnnotationSqlAttribute annotationSqlAttribute = (AnnotationSqlAttribute) attribute;
        assertThat(annotationSqlAttribute.getSqlHint()).isNotNull()
                .extracting(hint -> sqlHintConverter.convert(hint)).extracting(RoutingTargetType::isWrite).isEqualTo(true);

    }
}