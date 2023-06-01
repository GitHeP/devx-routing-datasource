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

import com.github.devx.routing.sql.SqlStatementType;
import lombok.Data;
import lombok.experimental.Accessors;

import java.util.Set;

/**
 * @author Peng He
 * @since 1.0
 */

@Data
@Accessors(chain = true)
public class SqlStatement {

    private String sql;

    private SqlStatementType statementType;

    private Object statement;

    private boolean write;

    private boolean read;

    private Set<String> databases;

    private Set<String> tables;

    private Set<String> normalTables;

    private Set<String> joinTables;

    private Set<String> subTables;

}
