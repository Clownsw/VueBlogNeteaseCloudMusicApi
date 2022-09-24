/*
 Navicat Premium Data Transfer

 Source Server         : mysql8-3333
 Source Server Type    : MySQL
 Source Server Version : 80028
 Source Host           : localhost:3333
 Source Schema         : vueblog_music

 Target Server Type    : MySQL
 Target Server Version : 80028
 File Encoding         : 65001

 Date: 24/09/2022 15:16:57
*/

SET NAMES utf8mb4;
SET FOREIGN_KEY_CHECKS = 0;

-- ----------------------------
-- Table structure for music
-- ----------------------------
DROP TABLE IF EXISTS `music`;
CREATE TABLE `music`  (
  `id` bigint NOT NULL,
  `music_type` smallint NOT NULL,
  `music_id` bigint NOT NULL,
  `music_name` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL,
  `music_url` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL,
  `create_date_time` datetime NOT NULL,
  `modify_date_time` datetime NOT NULL,
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of music
-- ----------------------------

SET FOREIGN_KEY_CHECKS = 1;
