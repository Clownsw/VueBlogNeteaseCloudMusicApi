package config

import "errors"

const (
	RedisNilError = "redis: nil"
)

var (
	SystemError        = errors.New("system error")
	MusicLyricNotFound = errors.New("music lyric not found")
)
