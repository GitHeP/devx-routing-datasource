package com.github.devx.routing.rule;

import com.github.devx.routing.sql.parser.AnnotationSqlParser;
import com.github.devx.routing.sql.parser.DefaultAnnotationSqlHintParser;
import com.github.devx.routing.sql.parser.JSqlParser;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * @author Peng He
 * @since 1.0
 */
class RoutingNameSqlHintRoutingRuleTest {

    SqlAttributeRoutingRule routingRule = new RoutingNameSqlHintRoutingRule(new AnnotationSqlParser(new JSqlParser() , new DefaultAnnotationSqlHintParser()));

    @Test
    void routing() {
        String sql = "/*!routingTargetName=write_0;*/ select * from employee where id = ?" ;
        String result = routingRule.routing(new RoutingKey().setSql(sql));
        assertThat(result).isEqualTo("write_0");
    }
}