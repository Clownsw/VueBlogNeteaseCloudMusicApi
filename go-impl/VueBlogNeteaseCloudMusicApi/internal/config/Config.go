package config

import "github.com/zeromicro/go-zero/rest"

var C Config

type RedisConfig struct {
	Addr     string `json:"addr"`
	Password string `json:"password"`
}

type ServerConfig struct {
	Url                   string `json:"url"`
	ServerUrl             string `json:"serverUrl"`
	Cookie                string
	Email                 string `json:"email"`
	PassWord              string `json:"passWord"`
	RedisLyricCachePrefix string `json:"redisLyricCachePrefix"`
}

type Config struct {
	rest.RestConf
	Redis  RedisConfig
	Server *ServerConfig
}
