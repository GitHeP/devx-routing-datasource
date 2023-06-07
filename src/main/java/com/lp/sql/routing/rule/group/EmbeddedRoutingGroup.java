package com.lp.sql.routing.rule.group;

import com.lp.sql.routing.RoutingContext;
import com.lp.sql.routing.RoutingTargetAttribute;
import com.lp.sql.routing.config.RoutingConfiguration;
import com.lp.sql.routing.config.SqlTypeConfiguration;
import com.lp.sql.routing.loadbalance.LoadBalance;
import com.lp.sql.routing.rule.ForceReadRoutingRule;
import com.lp.sql.routing.rule.ForceTargetRoutingRule;
import com.lp.sql.routing.rule.ForceWriteRoutingRule;
import com.lp.sql.routing.rule.NullSqlAttributeRoutingRule;
import com.lp.sql.routing.rule.PriorityRoutingRule;
import com.lp.sql.routing.rule.ReadWriteSplittingRoutingRule;
import com.lp.sql.routing.rule.RoutingKey;
import com.lp.sql.routing.rule.RoutingTypeAnnotationRoutingRule;
import com.lp.sql.routing.rule.SqlAttributeRoutingRule;
import com.lp.sql.routing.rule.TableRoutingRule;
import com.lp.sql.routing.rule.TxRoutingRule;
import com.lp.sql.routing.sql.SqlAttribute;
import com.lp.sql.routing.sql.parser.AnnotationSqlParser;
import com.lp.sql.routing.sql.parser.DefaultAnnotationSqlHintParser;
import com.lp.sql.routing.sql.parser.SqlParser;

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

        List<RoutingTargetAttribute> routingTargetAttributes = routingConf.getRoutingTargetAttributes(routingConf.getDataSources());
        LoadBalance<RoutingTargetAttribute> readLoadBalance = routingConf.makeReadLoadBalance(routingTargetAttributes);
        LoadBalance<RoutingTargetAttribute> writeLoadBalance = routingConf.makeWriteLoadBalance(routingTargetAttributes);

        NullSqlAttributeRoutingRule nullSqlAttributeRoutingRule = new NullSqlAttributeRoutingRule(sqlParser, readLoadBalance, writeLoadBalance);
        TxRoutingRule txRoutingRule = new TxRoutingRule(sqlParser, readLoadBalance, writeLoadBalance);
        ReadWriteSplittingRoutingRule readWriteSplittingRoutingRule = new ReadWriteSplittingRoutingRule(sqlParser, readLoadBalance, writeLoadBalance);
        ForceWriteRoutingRule forceWriteRoutingRule = new ForceWriteRoutingRule(sqlParser, readLoadBalance, writeLoadBalance);
        ForceReadRoutingRule forceReadRoutingRule = new ForceReadRoutingRule(sqlParser, readLoadBalance, writeLoadBalance);
        ForceTargetRoutingRule forceTargetRoutingRule = new ForceTargetRoutingRule();
        RoutingTypeAnnotationRoutingRule hintRoutingRule = new RoutingTypeAnnotationRoutingRule(sqlParser, readLoadBalance, writeLoadBalance);

        if (Objects.nonNull(routingConf.getRules()) && Objects.nonNull(routingConf.getRules().getTables())) {
            Map<String, Map<String, SqlTypeConfiguration>> tables = routingConf.getRules().getTables();
            install(new TableRoutingRule(tables));
        }

        install(nullSqlAttributeRoutingRule);
        install(txRoutingRule);
        install(readWriteSplittingRoutingRule);
        install(forceWriteRoutingRule);
        install(forceReadRoutingRule);
        install(forceTargetRoutingRule);
        install(hintRoutingRule);
    }

    @Override
    public String routing(RoutingKey key) {

        String targetName = null;
        SqlAttribute sqlAttribute = null;
        if (Objects.nonNull(key) && Objects.nonNull(key.getSql())) {
            sqlAttribute = sqlParser.parse(key.getSql());
            RoutingContext.setSqlAttribute(sqlAttribute);
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
