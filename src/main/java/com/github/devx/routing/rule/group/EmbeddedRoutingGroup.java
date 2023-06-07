package com.github.devx.routing.rule.group;

import com.github.devx.routing.RoutingTargetAttribute;
import com.github.devx.routing.config.RoutingConfiguration;
import com.github.devx.routing.config.SqlTypeConfiguration;
import com.github.devx.routing.loadbalance.LoadBalance;
import com.github.devx.routing.rule.ForceTargetRoutingRule;
import com.github.devx.routing.rule.NullSqlAttributeRoutingRule;
import com.github.devx.routing.rule.PriorityRoutingRule;
import com.github.devx.routing.rule.ReadWriteSplittingRoutingRule;
import com.github.devx.routing.rule.RoutingKey;
import com.github.devx.routing.rule.RoutingNameSqlHintRoutingRule;
import com.github.devx.routing.rule.RoutingTypeSqlHintRoutingRule;
import com.github.devx.routing.rule.SqlAttributeRoutingRule;
import com.github.devx.routing.rule.TableRoutingRule;
import com.github.devx.routing.rule.TxRoutingRule;
import com.github.devx.routing.sql.SqlAttribute;
import com.github.devx.routing.sql.parser.AnnotationSqlParser;
import com.github.devx.routing.sql.parser.DefaultAnnotationSqlHintParser;
import com.github.devx.routing.sql.parser.SqlParser;

import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Objects;

/**
 * @author Peng He
 * @since 1.0
 */
public class EmbeddedRoutingGroup extends AbstractComparableRoutingGroup<SqlAttributeRoutingRule> {

    private final SqlParser sqlParser;

    public EmbeddedRoutingGroup(RoutingConfiguration routingConf, SqlParser sqlParser) {

        super(Comparator.comparingInt(PriorityRoutingRule::priority));
        if (!(sqlParser instanceof AnnotationSqlParser)) {
            this.sqlParser = new AnnotationSqlParser(sqlParser , new DefaultAnnotationSqlHintParser());
        } else {
            this.sqlParser = sqlParser;
        }
        AnnotationSqlParser annotationSqlParser = (AnnotationSqlParser) this.sqlParser;

        List<RoutingTargetAttribute> routingTargetAttributes = routingConf.getRoutingTargetAttributes(routingConf.getDataSources());
        LoadBalance<RoutingTargetAttribute> readLoadBalance = routingConf.makeReadLoadBalance(routingTargetAttributes);
        LoadBalance<RoutingTargetAttribute> writeLoadBalance = routingConf.makeWriteLoadBalance(routingTargetAttributes);

        if (Objects.nonNull(routingConf.getRules()) && Objects.nonNull(routingConf.getRules().getTables())) {
            Map<String, Map<String, SqlTypeConfiguration>> tables = routingConf.getRules().getTables();
            install(new TableRoutingRule(tables));
        }

        install(new TxRoutingRule(sqlParser, readLoadBalance, writeLoadBalance));
        install(new NullSqlAttributeRoutingRule(sqlParser, readLoadBalance, writeLoadBalance));
        install(new ReadWriteSplittingRoutingRule(sqlParser, readLoadBalance, writeLoadBalance));
        install(new ForceTargetRoutingRule(routingConf ,sqlParser, readLoadBalance, writeLoadBalance));
        install(new RoutingTypeSqlHintRoutingRule(annotationSqlParser, readLoadBalance, writeLoadBalance));
        install(new RoutingNameSqlHintRoutingRule(annotationSqlParser));
    }

    @Override
    public String routing(RoutingKey key) {

        String targetName = null;
        SqlAttribute sqlAttribute = null;
        if (Objects.nonNull(key) && Objects.nonNull(key.getSql())) {
            sqlAttribute = sqlParser.parse(key.getSql());
        }

        for (SqlAttributeRoutingRule routingRule : routingRules) {
            targetName = routingRule.routing(sqlAttribute);
            if (Objects.nonNull(targetName) && targetName.length() != 0) {
                break;
            }
        }
        return targetName;
    }
}
