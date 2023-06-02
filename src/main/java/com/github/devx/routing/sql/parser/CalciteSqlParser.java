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

import org.apache.calcite.sql.SqlNode;

/**
 * @author Peng He
 * @since 1.0
 */
public class CalciteSqlParser implements SqlParser {

    @Override
    public SqlStatement parse(String sql) {


//        org.apache.calcite.sql.parser.SqlParser parser = org.apache.calcite.sql.parser.SqlParser.create(sql);
//        SqlNode sqlNode = parser.parseStmt();
//        sqlNode.accept();
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
