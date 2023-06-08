package com.github.devx.routing.rule.group;

import com.github.devx.routing.RoutingTargetAttribute;
import com.github.devx.routing.config.RoutingConfiguration;
import com.github.devx.routing.loadbalance.LoadBalance;
import com.github.devx.routing.rule.DatabaseRoutingRule;
import com.github.devx.routing.rule.ForceTargetRoutingRule;
import com.github.devx.routing.rule.NullSqlAttributeRoutingRule;
import com.github.devx.routing.rule.PriorityRoutingRule;
import com.github.devx.routing.rule.ReadWriteSplittingRoutingRule;
import com.github.devx.routing.rule.RoutingKey;
import com.github.devx.routing.rule.RoutingNameSqlHintRoutingRule;
import com.github.devx.routing.rule.RoutingRule;
import com.github.devx.routing.rule.RoutingTypeSqlHintRoutingRule;
import com.github.devx.routing.rule.SqlAttributeRoutingRule;
import com.github.devx.routing.rule.TableRoutingRule;
import com.github.devx.routing.rule.TxRoutingRule;
import com.github.devx.routing.sql.SqlAttribute;
import com.github.devx.routing.sql.parser.AnnotationSqlParser;
import com.github.devx.routing.sql.parser.DefaultAnnotationSqlHintParser;
import com.github.devx.routing.sql.parser.SqlParser;
import lombok.extern.slf4j.Slf4j;

import java.util.Comparator;
import java.util.List;
import java.util.Objects;

/**
 * @author Peng He
 * @since 1.0
 */

@Slf4j
public class EmbeddedRoutingGroup extends AbstractComparableRoutingGroup<SqlAttributeRoutingRule> {

    private final SqlParser sqlParser;

    public EmbeddedRoutingGroup(RoutingConfiguration routingConf, SqlParser sqlParser) {

        super(Comparator.comparingInt(PriorityRoutingRule::priority));
        if (!(sqlParser instanceof AnnotationSqlParser)) {
            this.sqlParser = new AnnotationSqlParser(sqlParser, new DefaultAnnotationSqlHintParser());
        } else {
            this.sqlParser = sqlParser;
        }
        AnnotationSqlParser annotationSqlParser = (AnnotationSqlParser) this.sqlParser;

        List<RoutingTargetAttribute> routingTargetAttributes = routingConf.getRoutingTargetAttributes(routingConf.getDataSources());
        LoadBalance<RoutingTargetAttribute> readLoadBalance = routingConf.makeReadLoadBalance(routingTargetAttributes);
        LoadBalance<RoutingTargetAttribute> writeLoadBalance = routingConf.makeWriteLoadBalance(routingTargetAttributes);

        install(new TxRoutingRule(sqlParser, readLoadBalance, writeLoadBalance));
        install(new NullSqlAttributeRoutingRule(sqlParser, readLoadBalance, writeLoadBalance));
        install(new ReadWriteSplittingRoutingRule(sqlParser, readLoadBalance, writeLoadBalance));
        install(new ForceTargetRoutingRule(routingConf, sqlParser, readLoadBalance, writeLoadBalance));
        install(new RoutingTypeSqlHintRoutingRule(annotationSqlParser, readLoadBalance, writeLoadBalance));
        install(new RoutingNameSqlHintRoutingRule(annotationSqlParser));
        install(new TableRoutingRule(sqlParser , routingConf));
        install(new DatabaseRoutingRule(sqlParser , routingConf));
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
                if (log.isDebugEnabled()) {
                    String msg = new StringBuilder("Hit Routing Rule")
                            .append(System.lineSeparator())
                            .append("Rule: ").append(routingRule.getClass().getSimpleName())
                            .append(System.lineSeparator())
                            .append("TargetName: ").append(targetName)
                            .append(System.lineSeparator())
                            .append("SQL: ").append(key.getSql())
                            .toString();
                    log.debug(msg);
                }
                break;
            }
        }
        return targetName;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder(this.getClass().getSimpleName());
        sb.append(" Routing Rules : ");
        if (routingRules == null) {
            sb.append("no match");
        } else if (routingRules.isEmpty()) {
            sb.append("[] empty (bypassed by RoutingRule='none') ");
        } else {
            sb.append("[\n");
            for (RoutingRule r : routingRules) {
                sb.append("  ").append(r.getClass().getSimpleName()).append("\n");
            }
            sb.append("]");
        }

        return sb.toString();
    }

}
