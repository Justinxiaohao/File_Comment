-- 添加积分字段
ALTER TABLE user ADD COLUMN points INT DEFAULT 0;

-- 添加会员等级字段
ALTER TABLE user ADD COLUMN vip_level INT DEFAULT 0;

-- 更新现有用户的积分和会员等级
UPDATE user SET points = 0, vip_level = 0 WHERE points IS NULL OR vip_level IS NULL;
