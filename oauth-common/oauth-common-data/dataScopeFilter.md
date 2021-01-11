### 关于拼接dsf sql
````
     public String dataScopeFilter(User user, String officeAlias, String mainTableAlias) {
            StringBuilder sqlString = new StringBuilder();
            ArrayList<String> dataScopeList = Lists.newArrayList();
            if (!user.isAdmin()) {
                Iterator<Role> Roles = user.getRoleList().iterator();
                Office company = user.getCompany(); //下属部门
                Office office = user.getOffice(); //归属部门
                while (Roles.hasNext()) {
                    Role role = Roles.next();
                    String dataScope = role.getDataScope();
                    String[] officeTableAlias = StringUtils.split(officeAlias, ",");
                    for (String officeTable : officeTableAlias) {
                        if (!dataScopeList.contains(dataScope) && StringUtils.isNotBlank(officeTable)) {
                            sqlString = this.getDsfSql(sqlString, dataScope, company, office, role, officeTable);
                            dataScopeList.add(dataScope);
                        }
                    }
                    if ("8".equals(dataScope)) {
                        if (com.huarui.project.utils.StringUtils.isNotEmpty(mainTableAlias)) {
                            for (String mainTable : mainTableAlias.split(",")) {
                                sqlString.append(" OR  " + mainTable + ".create_by = '" + user.getId() + "'");
                            }
                        } else {
                            throw new RuntimeException("dsf 主表不能为空");
                        }
                    }
                }
            }
            return StringUtils.isNotBlank(sqlString.toString()) && !sqlString.toString().equals("null") ? " AND (" + sqlString.substring(4) + ")" : "";
        }

        /**
         * 将目标表指定字段和user表关联
         *
         * @param user                NOTNULL
         * @param officeAlias         NOTNULL
         * @param targetAliasAndField [{"tableAlias1":{"creat_by",true/false}},{tableAlias2":{"creat_by",true/false}}]  任一参数非空     * @return
         */
        public Map<String, String> dataScopeFilter(User user, String officeAlias, Map<String, Map<String, Boolean>> targetAliasAndField) {
            HashMap<String, String> sqlMap = new HashMap<>();
            StringBuilder sqlString = new StringBuilder();
            ArrayList<String> dataScopeList = Lists.newArrayList();
            if (!user.isAdmin()) {
                Iterator<Role> Roles = user.getRoleList().iterator();
                Office company = user.getCompany(); //下属部门
                Office office = user.getOffice(); //归属部门
                while (Roles.hasNext()) {
                    Role role = Roles.next();
                    String dataScope = role.getDataScope();
                    String[] officeTableAlias = StringUtils.split(officeAlias, ",");
                    for (String officeTable : officeTableAlias) {
                        if (!dataScopeList.contains(dataScope) && StringUtils.isNotBlank(officeTable)) {
                            sqlString = this.getDsfSql(sqlString, dataScope, company, office, role, officeTable);
                            dataScopeList.add(dataScope);
                        }
                    }
                    if ("8".equals(dataScope)) {
                        StringBuilder targetAliasSQL = new StringBuilder();
                        StringBuilder whereSQL = new StringBuilder();
                        if (null != targetAliasAndField && targetAliasAndField.size() > 0) {
                            int i = 1;
                            for (String targetsAlias : targetAliasAndField.keySet()) {
                                Map<String, Boolean> stringBooleanMap = targetAliasAndField.get(targetsAlias);
                                for (String targetAlias : targetsAlias.replaceAll(" ", "").split(",")) {
                                    for (String fields : stringBooleanMap.keySet()) {
                                        for (String field : fields.replaceAll(" ", "").split(",")) {
                                            if (stringBooleanMap.get(fields)) {
                                                sqlString.append(" OR  " + targetAlias + "." + field + " LIKE  '%" + user.getId() + "%' ");
                                            } else {
                                                targetAliasSQL.append(" LEFT JOIN sys_user su" + i + " ON  " + targetAlias + "." + field + " = su" + i + ".id ");
                                                whereSQL.append(" AND su" + i + ".del_flag='0' ");
                                                sqlString.append(" OR  su" + i + ".id = '" + user.getId() + "' ");
                                                i++;
                                            }
                                        }
                                    }
                                }
                            }
                            sqlMap.put("dtf", targetAliasSQL.toString());
                            sqlMap.put("dwf", whereSQL.toString());
                        } else {
                            throw new RuntimeException("dsf 表不能为空");
                        }
                    }
                }
            }
            sqlMap.put("dsf", StringUtils.isNotBlank(sqlString.toString()) && !sqlString.toString().equals("null") ? " AND (" + sqlString.substring(4) + ")" : "");
            return sqlMap;
        }

        protected Map<String, Boolean> columnsIsLike(String fields, Boolean isLike) {
            Map<String, Boolean> stringBooleanMap = new HashMap<>();
            stringBooleanMap.put(fields, isLike);
            return stringBooleanMap;
        }

        private StringBuilder getDsfSql(StringBuilder sqlString, String dataScope, Office company, Office office, Role role, String officeTable) {
            if ("1".equals(dataScope)) {
            } else if ("2".equals(dataScope)) {
                sqlString.append(" OR  " + officeTable + ".id = '" + company.getId() + "' OR " + officeTable + ".parent_ids LIKE  '" + company.getParentIds() + ",%' ");
            } else if ("3".equals(dataScope)) {
                sqlString.append(" OR  " + officeTable + ".id = '" + company.getId() + "' OR (" + officeTable + ".parent_id = '" + company.getParentId() + "' AND " + officeTable + ".type = '2') ");
            } else if ("4".equals(dataScope)) {
                sqlString.append(" OR  " + officeTable + ".id = '" + office.getId() + "' OR " + officeTable + ".parent_ids LIKE '" + office.getParentIds() + office.getId() + ",%' ");
            } else if ("5".equals(dataScope)) {
                sqlString.append(" OR  " + officeTable + ".id = '" + office.getId() + "' ");
            } else if ("9".equals(dataScope)) {
                sqlString.append(" OR  EXISTS (SELECT 1 FROM sys_role_office WHERE role_id = '" + role.getId() + "' AND office_id = " + officeTable + ".id)");
            }
            return sqlString;
        }
````
### 相关表
````
DROP TABLE IF EXISTS `sys_office`;
CREATE TABLE `sys_office` (
  `id` varchar(64) COLLATE utf8_bin NOT NULL COMMENT '编号',
  `parent_id` varchar(64) COLLATE utf8_bin NOT NULL COMMENT '父级编号',
  `office_id` varchar(255) COLLATE utf8_bin DEFAULT NULL COMMENT '事务所',
  `parent_ids` varchar(2000) COLLATE utf8_bin NOT NULL COMMENT '所有父级编号',
  `name` varchar(100) COLLATE utf8_bin NOT NULL COMMENT '名称',
  `catalog` char(1) COLLATE utf8_bin DEFAULT '0',
  `sort` decimal(10,0) NOT NULL COMMENT '排序',
  `area_id` varchar(64) COLLATE utf8_bin DEFAULT NULL COMMENT '归属区域',
  `code` varchar(100) COLLATE utf8_bin DEFAULT NULL COMMENT '区域编码',
  `type` char(1) COLLATE utf8_bin DEFAULT NULL COMMENT '机构类型',
  `grade` char(1) COLLATE utf8_bin DEFAULT NULL COMMENT '机构等级',
  `address` varchar(255) COLLATE utf8_bin DEFAULT NULL COMMENT '联系地址',
  `zip_code` varchar(100) COLLATE utf8_bin DEFAULT NULL COMMENT '邮政编码',
  `master` varchar(100) COLLATE utf8_bin DEFAULT NULL COMMENT '负责人',
  `phone` varchar(200) COLLATE utf8_bin DEFAULT NULL COMMENT '电话',
  `fax` varchar(200) COLLATE utf8_bin DEFAULT NULL COMMENT '传真',
  `email` varchar(200) COLLATE utf8_bin DEFAULT NULL COMMENT '邮箱',
  `location` varchar(64) COLLATE utf8_bin DEFAULT NULL,
  `risk_status` char(1) COLLATE utf8_bin DEFAULT NULL,
  `useable` varchar(64) COLLATE utf8_bin DEFAULT NULL COMMENT '是否启用',
  `primary_person` varchar(64) COLLATE utf8_bin DEFAULT NULL COMMENT '主负责人',
  `deputy_person` varchar(64) COLLATE utf8_bin DEFAULT NULL COMMENT '副负责人',
  `create_by` varchar(64) COLLATE utf8_bin NOT NULL COMMENT '创建者',
  `create_date` datetime NOT NULL COMMENT '创建时间',
  `update_by` varchar(64) COLLATE utf8_bin NOT NULL COMMENT '更新者',
  `update_date` datetime NOT NULL COMMENT '更新时间',
  `remarks` varchar(255) COLLATE utf8_bin DEFAULT NULL COMMENT '备注信息',
  `del_flag` char(1) COLLATE utf8_bin NOT NULL DEFAULT '0' COMMENT '删除标记',
  `company_status` char(1) COLLATE utf8_bin DEFAULT NULL,
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE KEY `sys_office_id` (`id`) USING BTREE,
  KEY `sys_office_parent_id` (`parent_id`) USING BTREE,
  KEY `sys_office_del_flag` (`del_flag`) USING BTREE,
  KEY `sys_office_type` (`type`) USING BTREE
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_bin COMMENT='机构表';

-- ----------------------------
-- Records of sys_office
-- ----------------------------
INSERT INTO `sys_office` VALUES ('03e4e13cb8f8470daa5c8824a7a3137d', '34ca338ca88349b2afb984dcdc007755', '', '0,1,99638f22fdc24339be61bbb549afd5ed,34ca338ca88349b2afb984dcdc007755,', '南京事务所', '1', '70', '110000', null, '2', '1', null, null, null, null, null, null, null, null, '1', null, null, '1', '2020-04-24 18:13:23', '1', '2020-04-24 18:13:23', null, '0', null);
IINSERT INTO `sys_office` VALUES ('1', '0', '', '0,', '尚安家居信息化管理平台', '1', '10', '440000', '100000', '1', null, null, null, null, null, null, null, null, null, '1', null, null, '1', '2013-05-27 08:00:00', '1', '2018-12-17 08:24:01', null, '0', null);
INSERT INTO `sys_office` VALUES ('18e9736c13324034bda2a8a0df660e19', '770de58e81dc4a5dadfe14fa9c46fed6', '', '0,1,99638f22fdc24339be61bbb549afd5ed,770de58e81dc4a5dadfe14fa9c46fed6,', '天津事务所', '1', '20', '110000', null, '2', '1', null, null, null, null, null, null, null, null, '1', null, null, '1', '2020-04-24 18:14:39', '1', '2020-04-24 18:14:39', null, '0', null);
INSERT INTO `sys_office` VALUES ('1cac1a48eb8b46d5a1fd90ffa528f653', '2076580bae4941dabfca8d3f438f2d78', '', '0,1,99638f22fdc24339be61bbb549afd5ed,2076580bae4941dabfca8d3f438f2d78,', '武汉事务所', '1', '30', '110000', null, '2', '1', null, null, null, null, null, null, null, null, '1', null, null, '1', '2020-04-24 18:06:08', '1', '2020-04-24 18:06:08', null, '0', null);
INSERT INTO `sys_office` VALUES ('29e3f1b983914f91a0e7c00ba98ef026', '1', '', '0,1,', '运营部', '1', '20', '110000', '', '1', '1', '', '', '', '', '', '', null, null, '1', null, null, '1', '2020-04-22 18:18:59', '1', '2020-04-24 17:55:21', '', '0', null);
INSERT INTO `sys_office` VALUES ('2b6eb4c6ca644b40a4e3f9a90eaa4f15', '319e3fc4acff4b51985f658364310e4f', '', '0,1,99638f22fdc24339be61bbb549afd5ed,319e3fc4acff4b51985f658364310e4f,', '长春事务所', '1', '30', '110000', null, '2', '1', null, null, null, null, null, null, null, null, '1', null, null, '1', '2020-04-24 18:16:35', '1', '2020-04-24 18:16:35', null, '0', null);
INSERT INTO `sys_office` VALUES ('2ea3e2fcc5094c57bd85cfaa956acbb7', '29e3f1b983914f91a0e7c00ba98ef026', '', '0,1,29e3f1b983914f91a0e7c00ba98ef026,', '应收资料部', '1', '40', '110000', null, '1', '1', null, null, null, null, null, null, null, null, '1', null, null, '1', '2020-04-24 17:57:44', '1', '2020-04-24 17:57:44', null, '0', null);
INSERT INTO `sys_office` VALUES ('5dc341781f97493dabca496a8c1325bf', '770de58e81dc4a5dadfe14fa9c46fed6', '', '0,1,99638f22fdc24339be61bbb549afd5ed,770de58e81dc4a5dadfe14fa9c46fed6,', '青岛事务所', '1', '50', '110000', null, '2', '1', null, null, null, null, null, null, null, null, '1', null, null, '1', '2020-04-24 18:15:04', '1', '2020-04-24 18:15:04', null, '0', null);
INSERT INTO `sys_office` VALUES ('678055097bbe4c52b9a2162498403a99', '1', 'beff3e8eb76a4ac2b710357d2e50fbe4', '0,1,', '广东保利', '2', '10', '110000', '', '1', '1', '', '', '', '', '', '', null, null, '1', null, null, '1', '2020-04-26 15:57:49', 'cdf22c53925445de910ef95803e59d58', '2020-06-06 15:20:05', '', '0', null);
INSERT INTO `sys_office` VALUES ('85933f662e39498ea69931e13f6c0cc8', '1', '', '0,1,', '财务部', '1', '30', '110000', null, '1', '1', null, null, null, null, null, null, null, null, '1', null, null, '1', '2020-04-22 18:20:35', '1', '2020-04-22 18:20:35', null, '0', null);
INSERT INTO `sys_office` VALUES ('867e9aede0514fc79fdd386337bf5eb9', '99638f22fdc24339be61bbb549afd5ed', '', '0,1,99638f22fdc24339be61bbb549afd5ed,', '家电事业部', '1', '20', '110000', null, '3', '1', null, null, null, null, null, null, null, null, '1', null, null, '1', '2020-04-22 18:26:01', '1', '2020-04-24 18:07:58', null, '0', null);
INSERT INTO `sys_office` VALUES ('90ab4090b81c4c4ba8ef809a5564a42a', '1', '1', '0,1,', '广东万科', '2', '20', '440000', '', '1', '1', '', '', '', '', '', '', null, null, '1', null, null, '1', '2020-04-27 16:59:10', 'cac699db788c4e03b354674d953c2b3a', '2020-06-05 18:36:56', '', '0', null);
INSERT INTO `sys_office` VALUES ('9253141b28cc45498cc5f7e517856337', '29e3f1b983914f91a0e7c00ba98ef026', '', '0,1,29e3f1b983914f91a0e7c00ba98ef026,', '经营管理部', '1', '30', '110000', null, '1', '1', null, null, null, null, null, null, null, null, '1', null, null, '1', '2020-04-24 17:57:24', '1', '2020-04-24 17:57:24', null, '0', null);
INSERT INTO `sys_office` VALUES ('99638f22fdc24339be61bbb549afd5ed', '1', '', '0,1,', '业务部', '1', '10', '110000', '', '2', '1', '', '', '', '', '', '', null, null, '1', null, null, '1', '2020-04-22 17:57:02', '1', '2020-04-24 17:56:05', '', '0', null);
INSERT INTO `sys_office` VALUES ('9965c128f3e94a0bb422e34e423f7311', '770de58e81dc4a5dadfe14fa9c46fed6', '', '0,1,99638f22fdc24339be61bbb549afd5ed,770de58e81dc4a5dadfe14fa9c46fed6,', '北京事务所', '1', '10', '110000', null, '2', '1', null, null, null, null, null, null, null, null, '1', null, null, '1', '2020-04-24 18:14:28', '1', '2020-04-24 18:14:28', null, '0', null);
INSERT INTO `sys_office` VALUES ('9eba51a48c364b58ba1a9e57738c18e5', '99638f22fdc24339be61bbb549afd5ed', '', '0,1,99638f22fdc24339be61bbb549afd5ed,', '西北战区', '1', '90', '110000', null, '2', '1', null, null, null, null, null, null, null, null, '1', null, null, '1', '2020-04-24 18:11:22', '1', '2020-04-24 18:11:22', null, '0', null);


DROP TABLE IF EXISTS `sys_role_office`;
CREATE TABLE `sys_role_office` (
  `role_id` varchar(64) COLLATE utf8_bin NOT NULL COMMENT '角色编号',
  `office_id` varchar(64) COLLATE utf8_bin NOT NULL COMMENT '机构编号',
  PRIMARY KEY (`role_id`,`office_id`) USING BTREE
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_bin COMMENT='角色-机构';

-- ----------------------------
-- Records of sys_role_office
-- ----------------------------
INSERT INTO `sys_role_office` VALUES ('2acda9aa142946ee8315286c37577758', '0bb36bf33e114ea69dabfbdd0d12438c');
INSERT INTO `sys_role_office` VALUES ('2acda9aa142946ee8315286c37577758', '43b9616f8ba54c8983bd02f1a3c1092a');
INSERT INTO `sys_role_office` VALUES ('2acda9aa142946ee8315286c37577758', '4bf4a2c7c47d4e8bab0a88b78467c59b');
INSERT INTO `sys_role_office` VALUES ('2acda9aa142946ee8315286c37577758', '7cf51e5f9a66493abc83764fef05d8c3');
INSERT INTO `sys_role_office` VALUES ('2acda9aa142946ee8315286c37577758', '867e9aede0514fc79fdd386337bf5eb9');
INSERT INTO `sys_role_office` VALUES ('2acda9aa142946ee8315286c37577758', '8e83ad7c2edc4917b843fb59637815a8');
INSERT INTO `sys_role_office` VALUES ('2acda9aa142946ee8315286c37577758', '99638f22fdc24339be61bbb549afd5ed');
INSERT INTO `sys_role_office` VALUES ('2acda9aa142946ee8315286c37577758', 'b49b74a6a71945c791e75fb47bb915c7');
INSERT INTO `sys_role_office` VALUES ('2acda9aa142946ee8315286c37577758', 'beff3e8eb76a4ac2b710357d2e50fbe4');
INSERT INTO `sys_role_office` VALUES ('2acda9aa142946ee8315286c37577758', 'e5f5cf9b5f91461a818614ee7fa6074b');
INSERT INTO `sys_role_office` VALUES ('2acda9aa142946ee8315286c37577758', 'e7c46c68aca147eb903cc0b3cbcaee72');
INSERT INTO `sys_role_office` VALUES ('9753c529a27841ac81149b71f4609e69', '2076580bae4941dabfca8d3f438f2d78');
INSERT INTO `sys_role_office` VALUES ('9753c529a27841ac81149b71f4609e69', '867e9aede0514fc79fdd386337bf5eb9');
INSERT INTO `sys_role_office` VALUES ('9753c529a27841ac81149b71f4609e69', '901c0a13eb814e92a916f87b52015ee9');


DROP TABLE IF EXISTS `sys_role`;
CREATE TABLE `sys_role` (
  `id` varchar(64) COLLATE utf8_bin NOT NULL COMMENT '编号',
  `office_id` varchar(64) COLLATE utf8_bin DEFAULT NULL COMMENT '归属机构',
  `name` varchar(100) COLLATE utf8_bin NOT NULL COMMENT '角色名称',
  `enname` varchar(255) COLLATE utf8_bin DEFAULT NULL COMMENT '英文名称',
  `role_type` varchar(255) COLLATE utf8_bin DEFAULT NULL COMMENT '角色类型',
  `data_scope` char(1) COLLATE utf8_bin DEFAULT NULL COMMENT '数据范围',
  `is_sys` varchar(64) COLLATE utf8_bin DEFAULT NULL COMMENT '是否系统数据',
  `useable` varchar(64) COLLATE utf8_bin DEFAULT NULL COMMENT '是否可用',
  `create_by` varchar(64) COLLATE utf8_bin NOT NULL COMMENT '创建者',
  `create_date` datetime NOT NULL COMMENT '创建时间',
  `update_by` varchar(64) COLLATE utf8_bin NOT NULL COMMENT '更新者',
  `update_date` datetime NOT NULL COMMENT '更新时间',
  `remarks` varchar(255) COLLATE utf8_bin DEFAULT NULL COMMENT '备注信息',
  `del_flag` char(1) COLLATE utf8_bin NOT NULL DEFAULT '0' COMMENT '删除标记',
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE KEY `office_id` (`office_id`,`name`) USING BTREE,
  UNIQUE KEY `office_id_2` (`office_id`,`enname`) USING BTREE,
  KEY `sys_role_del_flag` (`del_flag`) USING BTREE,
  KEY `sys_role_enname` (`enname`) USING BTREE
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_bin COMMENT='角色表';

-- ----------------------------
-- Records of sys_role
-- ----------------------------
INSERT INTO `sys_role` VALUES ('1', '1', '超级系统管理员', 'dept', 'null', '4', '1', '1', '1', '2013-05-27 08:00:00', '1', '2020-06-13 15:11:24', 'null', '0');
INSERT INTO `sys_role` VALUES ('2acda9aa142946ee8315286c37577758', '29e3f1b983914f91a0e7c00ba98ef026', '系统管理员', 'xtgly', 'null', '9', '0', '1', 'cac699db788c4e03b354674d953c2b3a', '2020-06-04 18:50:38', 'cac699db788c4e03b354674d953c2b3a', '2020-06-04 19:17:39', 'null', '0');
INSERT INTO `sys_role` VALUES ('4a49ac338cf24425a719717a581a0926', '17ef264ec09e46caae7b1b4fe7f07bab', '文物巡查员', 'cultureMan', null, '5', '0', '1', 'c787342bc87c466893e9c08760bc1404', '2019-12-25 10:18:10', 'c787342bc87c466893e9c08760bc1404', '2019-12-25 10:18:10', null, '0');
INSERT INTO `sys_role` VALUES ('6b0711fbedfb49b3848edc4741756136', '095de765897a42ad9cf0b26eb5c24d28', '文物巡查员', 'patroller', 'null', '4', '0', '1', '1', '2019-12-06 20:41:53', '867ee3be279e442e82bc1c69eecdd97e', '2020-01-03 10:44:53', 'null', '0');
INSERT INTO `sys_role` VALUES ('6f8fe874c9534d228ee5d838caaccd84', '901c0a13eb814e92a916f87b52015ee9', '保利管理员', 'blgly', 'null', '9', '0', '1', 'cac699db788c4e03b354674d953c2b3a', '2020-06-04 09:36:05', '1', '2020-06-15 11:26:13', 'null', '0');
INSERT INTO `sys_role` VALUES ('8995ac9fd5ae4075be3456ed37dca5da', '901c0a13eb814e92a916f87b52015ee9', '华南战区管理员', 'hngly', 'null', '4', '0', '1', '1', '2020-06-04 10:58:26', '5b99d05e6c9a4d138e336ea9a1dac73e', '2020-06-13 09:58:04', 'null', '0');
INSERT INTO `sys_role` VALUES ('8a28fe66538f4fb983312a2fc8292902', '095de765897a42ad9cf0b26eb5c24d28', '白云文保管理员', 'administrator', 'null', '4', '0', '1', '1', '2019-12-06 11:45:59', '2fd402aa382246369d9d5a3b1977618a', '2020-01-04 18:01:59', 'null', '0');
INSERT INTO `sys_role` VALUES ('9753c529a27841ac81149b71f4609e69', '867e9aede0514fc79fdd386337bf5eb9', '家电事业部管理员', 'jdsybgly', null, '9', '0', '1', 'cac699db788c4e03b354674d953c2b3a', '2020-06-04 18:37:56', 'cac699db788c4e03b354674d953c2b3a', '2020-06-04 18:37:56', null, '0');
INSERT INTO `sys_role` VALUES ('99e814cbb4c04b3daed5ff996c311627', '99638f22fdc24339be61bbb549afd5ed', '业务部管理员', 'ywbly', null, '4', '0', '1', '1', '2020-06-04 11:30:06', '1', '2020-06-04 11:30:06', null, '0');
INSERT INTO `sys_role` VALUES ('addaa0f0f9ca47e6a105c84e88e615e0', 'beff3e8eb76a4ac2b710357d2e50fbe4', '深圳事务所管理员', 'szgly', null, '8', '0', '1', '1', '2020-06-04 11:31:25', '1', '2020-06-04 11:31:25', null, '0');
INSERT INTO `sys_role` VALUES ('c347881267f3459ba4964e948b1c1962', 'c3e0c232961b47f99ab2709e9087fd6d', '文保局管理员', 'wbjgly', null, '5', '0', '1', '2fd402aa382246369d9d5a3b1977618a', '2019-12-13 18:39:57', '2fd402aa382246369d9d5a3b1977618a', '2019-12-13 18:39:57', null, '0');
INSERT INTO `sys_role` VALUES ('c502468b56784460833b8c612454a00c', '1', '系统管理员', 'master', 'null', '4', '0', '1', '1', '2019-12-14 09:41:38', '1', '2020-06-13 17:05:57', 'null', '0');
INSERT INTO `sys_role` VALUES ('cbecdd5967fc476aa1f0a125afd6d527', '212ea136a5bd4c1f99d95e197e415805', '瀚瑞管理员', 'gly', 'null', '4', '0', '1', '1', '2019-12-10 11:00:29', '2fd402aa382246369d9d5a3b1977618a', '2019-12-10 14:43:49', 'null', '0');


DROP TABLE IF EXISTS `sys_user`;
CREATE TABLE `sys_user` (
  `id` varchar(64) COLLATE utf8_bin NOT NULL COMMENT '编号',
  `company_id` varchar(64) COLLATE utf8_bin DEFAULT NULL COMMENT '归属公司',
  `office_id` varchar(64) COLLATE utf8_bin NOT NULL COMMENT '归属部门',
  `login_name` varchar(100) COLLATE utf8_bin NOT NULL COMMENT '登录名',
  `password` varchar(128) COLLATE utf8_bin NOT NULL COMMENT '瀵嗙爜',
  `no` varchar(100) COLLATE utf8_bin DEFAULT NULL COMMENT '工号',
  `name` varchar(100) COLLATE utf8_bin NOT NULL COMMENT '姓名',
  `email` varchar(200) COLLATE utf8_bin DEFAULT NULL COMMENT '邮箱',
  `phone` varchar(200) COLLATE utf8_bin DEFAULT '' COMMENT '电话',
  `mobile` varchar(200) COLLATE utf8_bin DEFAULT '' COMMENT '手机',
  `user_type` char(1) COLLATE utf8_bin DEFAULT NULL COMMENT '用户类型',
  `photo` varchar(1000) COLLATE utf8_bin DEFAULT NULL COMMENT '用户头像',
  `login_ip` varchar(100) COLLATE utf8_bin DEFAULT NULL COMMENT '最后登陆IP',
  `login_date` datetime DEFAULT NULL COMMENT '最后登陆时间',
  `login_flag` varchar(64) COLLATE utf8_bin DEFAULT NULL COMMENT '是否可登录',
  `create_by` varchar(64) COLLATE utf8_bin NOT NULL COMMENT '创建者',
  `create_date` datetime NOT NULL COMMENT '创建时间',
  `update_by` varchar(64) COLLATE utf8_bin NOT NULL COMMENT '更新者',
  `update_date` datetime NOT NULL COMMENT '更新时间',
  `remarks` varchar(255) COLLATE utf8_bin DEFAULT NULL COMMENT '备注信息',
  `del_flag` char(1) COLLATE utf8_bin NOT NULL DEFAULT '0' COMMENT '删除标记',
  `birth` varchar(64) COLLATE utf8_bin NOT NULL DEFAULT '0',
  `birth_content` varchar(64) COLLATE utf8_bin DEFAULT NULL,
  `app_key` varchar(64) COLLATE utf8_bin DEFAULT NULL,
  `app_notify_url` varchar(255) COLLATE utf8_bin DEFAULT NULL,
  `openid` varchar(64) COLLATE utf8_bin DEFAULT NULL,
  PRIMARY KEY (`id`) USING BTREE,
  KEY `sys_user_office_id` (`office_id`) USING BTREE,
  KEY `create_by` (`create_by`) USING BTREE
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_bin COMMENT='用户表';

-- ----------------------------
-- Records of sys_user
-- ----------------------------
INSERT INTO `sys_user` VALUES ('0027bb77f0684a5fb5eed9ae5b420d32', null, '1', 'a', '260a37ac5dc3c4f4cd05478b5e050e5679cb4077f08533f654c17093523226f7f49687f60395f4f1', null, 'a', null, '', '', null, null, '113.87.160.90', '2020-06-15 17:57:42', '1', '1', '2020-03-31 10:45:35', '1', '2020-06-13 18:54:47', null, '0', '0', null, null, null, null);
INSERT INTO `sys_user` VALUES ('094bb89e96b64e3abb19478c772825fb', null, '678055097bbe4c52b9a2162498403a99', 'baoli', '76eb540d7e6037d5431e19abfb4fc0fc2a441f065da44af39e2511711feba3df28ca95775b23a48d', null, 'baoli', null, '', '', null, '', '0:0:0:0:0:0:0:1', '2020-06-04 10:51:15', '1', 'cac699db788c4e03b354674d953c2b3a', '2020-06-04 09:37:24', 'cac699db788c4e03b354674d953c2b3a', '2020-06-04 09:37:24', null, '1', '0', null, null, null, null);
INSERT INTO `sys_user` VALUES ('1', null, '1', 'admin', '12ae7fcd4e257423fb45ba904db620f2f45e0fa3c04451bb555e114db3397d77ab32203f2594db80', '0001', '系统管理员', 'thinkgem@163.com', '8675', '8675', null, '/cultureFiles/05c7442d88204ce778f632a31cd1fcdd86af5aa91c84b9cba3a4d96b33e43440.jpg', '0:0:0:0:0:0:0:1', '2020-06-16 15:24:07', '1', '1', '2013-05-27 08:00:00', '1', '2019-12-13 18:01:05', '最高管理员', '0', '0', null, '5555', '6666', 'op2ewv5vcBe-hcK1DwYR5sk6kyG0');
INSERT INTO `sys_user` VALUES ('111bbc2ad481406b941b6c749c5dee58', null, '095de765897a42ad9cf0b26eb5c24d28', 'zwj', '02ae7fcd4e2574978013cc6bd4dafe3dae49b77298388aca9f263af1be29a2dab8171b16b1b8e6df', null, '朱文俊', null, '', '', null, '', '192.168.1.10', '2020-06-13 18:28:42', '1', '1', '2020-03-23 17:24:27', '1', '2020-03-23 17:24:27', null, '0', '0', null, null, null, null);
INSERT INTO `sys_user` VALUES ('5b99d05e6c9a4d138e336ea9a1dac73e', null, '1', 'zzw', 'a9e8ef8c3bba8593b2c3036c7c3708493d59e5c9caf8a07b9783ff4f0b56382bec0e151f1a9b7ea7', null, 'zzw', null, '', '', null, null, '113.87.160.90', '2020-06-15 09:02:42', '1', '1', '2020-05-22 17:50:37', '1', '2020-06-13 18:55:05', null, '0', '0', null, null, null, null);
INSERT INTO `sys_user` VALUES ('93a00a65fc18405a9c503f145ad01539', null, '29e3f1b983914f91a0e7c00ba98ef026', 'yybgly', 'f5dc68281a9d2a0bc6bbd95fdbc6e965d976e44ad89e5ed230a319c2e56dacecc4781d8a6dbc2655', null, '运营部管理员', null, '', '', null, null, '192.168.1.19', '2020-06-04 19:50:28', '1', 'cac699db788c4e03b354674d953c2b3a', '2020-06-04 18:51:20', '1', '2020-06-13 18:54:39', null, '0', '0', null, null, null, null);
INSERT INTO `sys_user` VALUES ('bc0820e34e8a4ccfb2ab5907a23834d5', null, '1', 'qqq', '7f693b7bd48c5234479d9d1aca95c96a96f6a81db05ade28f416783ada2664cec989840ecc97814a', 'qqq', 'qq', null, '', '', null, null, null, null, '1', '1', '2020-06-12 19:02:11', '1', '2020-06-13 18:54:58', null, '0', '0', null, null, null, null);
INSERT INTO `sys_user` VALUES ('ca88770d88b84d9cbd9629cae793e056', null, 'beff3e8eb76a4ac2b710357d2e50fbe4', 'szgly', '2c47a0f5d2e0f3d700cae4e03ec2fb8cb2d23ce373fe24d62da80f4be5e4f6b989139c06735d9b6f', null, '深圳事务所用户', null, 'q', '', null, null, null, null, '1', '1', '2020-06-04 11:32:14', '1', '2020-06-13 18:54:32', null, '0', '0', null, null, null, null);
INSERT INTO `sys_user` VALUES ('cac699db788c4e03b354674d953c2b3a', null, '1', 'q', 'a3a535934515e3aa198a5e64378506636ac890f7d3843cbd26e2b9fa736262a7c2758a64475b8065', null, 'q', null, '', '', null, null, '0:0:0:0:0:0:0:1', '2020-06-05 18:37:26', '1', '1', '2020-05-19 16:15:42', '1', '2020-06-12 19:07:39', null, '0', '0', null, null, null, null);
INSERT INTO `sys_user` VALUES ('cdf22c53925445de910ef95803e59d58', null, '901c0a13eb814e92a916f87b52015ee9', 'hngly', '594b357a3273ee6a6826f47f1827e5ea4fbf13bfa3a2df659b3311b58c620618f9f4d0e6714e0b47', null, 'hngly', null, '', '', null, null, '0:0:0:0:0:0:0:1', '2020-06-15 09:30:35', '1', '1', '2020-06-04 10:59:12', '1', '2020-06-13 18:54:15', null, '0', '0', null, null, null, null);
INSERT INTO `sys_user` VALUES ('d8dcfb2a1bfe4044a9ca7c41dae0868d', null, '901c0a13eb814e92a916f87b52015ee9', 'blgly', '0dd89a8d94a431240103f0636f5a6b0b688cf27664623319d15362455b4a279fc8f835ff2dba9d63', null, 'blgly', null, '', '', null, null, '0:0:0:0:0:0:0:1', '2020-06-16 15:24:26', '1', '1', '2020-06-13 09:51:42', '1', '2020-06-13 18:54:07', null, '0', '0', null, null, null, null);

````