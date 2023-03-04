package util

import (
	"VueBlogNeteaseCloudMusicApi/internal/config"
	"fmt"
	"github.com/imroc/req/v3"
)

var ReqClient = req.C()

func CommonServiceRequest(url string) (string, error) {
	response, err := ReqClient.R().Post(
		fmt.Sprintf(
			"%s&cookie=%s",
			url,
			config.C.Server.Cookie,
		),
	)

	return response.String(), err
}
