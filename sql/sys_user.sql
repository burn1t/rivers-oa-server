/*
 Navicat Premium Data Transfer

 Source Server         : MySQL 3.7
 Source Server Type    : MySQL
 Source Server Version : 50737
 Source Host           : localhost:3306
 Source Schema         : rivers_oa_sys

 Target Server Type    : MySQL
 Target Server Version : 50737
 File Encoding         : 65001

 Date: 07/07/2024 00:00:46
*/

SET NAMES utf8mb4;
SET FOREIGN_KEY_CHECKS = 0;

-- ----------------------------
-- Table structure for sys_user
-- ----------------------------
DROP TABLE IF EXISTS `sys_user`;
CREATE TABLE `sys_user`  (
  `id` varchar(40) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL COMMENT '用户 ID',
  `username` varchar(60) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '用户名',
  `password` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '密码，加密存储, admin/1234',
  `nick_name` varchar(60) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '昵称',
  `image_url` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '头像url',
  `authority` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL,
  `token` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL,
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE INDEX `username`(`username`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8 COLLATE = utf8_general_ci COMMENT = '用户信息表' ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of sys_user
-- ----------------------------
INSERT INTO `sys_user` VALUES ('11', 'meng', '$2a$10$bUVGBErZjBKrNjeJX34i..v.k.goCKMW0I5WbYYXStqIf.FScl9Em', '梦', 'group1/M00/00/00/J2y7ZGA2IheAESCfAABBqgX_-Lk92.jpeg', NULL, NULL);
INSERT INTO `sys_user` VALUES ('12', 'xue', '$2a$10$bUVGBErZjBKrNjeJX34i..v.k.goCKMW0I5WbYYXStqIf.FScl9Em', '学', 'group1/M00/00/00/J2y7ZGA2IheAESCfAABBqgX_-Lk92.jpeg', NULL, NULL);
INSERT INTO `sys_user` VALUES ('13', 'Floyd', '$2a$10$bUVGBErZjBKrNjeJX34i..v.k.goCKMW0I5WbYYXStqIf.FScl9Em', '', 'group1/M00/00/00/J2y7ZGA2IheAESCfAABBqgX_-Lk92.jpeg', NULL, NULL);
INSERT INTO `sys_user` VALUES ('14', 'admin', '$2a$10$UhSjYP6qsf3d2s0cqznb9ePfrhT.zcpbf6UdWQiffeT2YZGAb3hVW', 'admin', 'group1/M00/00/00/J2y7ZGA2IheAESCfAABBqgX_-Lk92.jpeg', '', NULL);

SET FOREIGN_KEY_CHECKS = 1;
