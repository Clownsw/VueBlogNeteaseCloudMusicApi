package util

import "VueBlogNeteaseCloudMusicApi/internal/config"

func StringIsEmpty(value string) bool {
	return value == config.EmptyString
}

func StringIsNotEmpty(value string) bool {
	return value != config.EmptyString
}
