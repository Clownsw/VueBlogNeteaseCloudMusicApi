package svc

import (
	"VueBlogNeteaseCloudMusicApi/internal/config"
	"github.com/go-redis/redis/v8"
	"github.com/zeromicro/go-zero/core/logx"
)

type ServiceContext struct {
	logx.Logger
	Config      config.Config
	RedisClient *redis.Client
}

func NewServiceContext(c config.Config) *ServiceContext {
	return &ServiceContext{
		Config: c,
		RedisClient: redis.NewClient(
			&redis.Options{
				Addr:     c.Redis.Addr,
				Password: c.Redis.Password,
			},
		),
	}
}
