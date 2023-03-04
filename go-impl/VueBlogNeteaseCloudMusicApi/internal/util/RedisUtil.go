package util

import (
	"VueBlogNeteaseCloudMusicApi/internal/config"
	"context"
	"github.com/go-redis/redis/v8"
)

func RedisGetString(client *redis.Client, key string) (string, error) {
	cmd := client.Get(context.Background(), key)
	err := client.Process(context.Background(), cmd)

	if err != nil && err.Error() == config.RedisNilError {
		return config.EmptyString, nil
	}

	return cmd.String(), err
}
