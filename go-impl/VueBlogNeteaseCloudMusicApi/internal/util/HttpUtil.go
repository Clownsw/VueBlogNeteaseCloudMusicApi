package util

import (
	"VueBlogNeteaseCloudMusicApi/internal/config"
	"net/http"
	"strings"
)

func CookieSliceToString(cookieSlice []*http.Cookie) string {
	if cookieSlice == nil || len(cookieSlice) == 0 {
		return config.EmptyString
	}

	cookieString := new(strings.Builder)

	for _, cookie := range cookieSlice {
		cookieString.Write(StringToByteSlice(cookie.Name))
		cookieString.WriteString("=")
		cookieString.Write(StringToByteSlice(cookie.Value))
		cookieString.WriteString(";")
	}

	return cookieString.String()
}
