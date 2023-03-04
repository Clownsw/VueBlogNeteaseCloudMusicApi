package impl

import (
	"VueBlogNeteaseCloudMusicApi/internal/config"
	"VueBlogNeteaseCloudMusicApi/internal/svc"
	"VueBlogNeteaseCloudMusicApi/internal/util"
	"fmt"
	"github.com/imroc/req/v3"
	"github.com/zeromicro/go-zero/core/logx"
	"strings"
)

var NeteaseMusicServiceInstance = new(NeteaseMusicService)

type NeteaseMusicService struct {
	Ctx *svc.ServiceContext
}

func (musicService *NeteaseMusicService) Lyric(logger logx.Logger, musicId string) (string, error) {
	serverConfig := musicService.Ctx.Config.Server

	result, err := util.CommonServiceRequest(
		fmt.Sprintf(
			"%slyric?id=%s",
			serverConfig.Url,
			musicId,
		),
	)

	code, err := util.JsonGetInt64(result, "code")
	if code == 20001 {
		err = musicService.EmailLogin(serverConfig.Email, serverConfig.PassWord)
		if err != nil {
			musicService.Ctx.Logger.Error(err)
			goto End
		}

		return musicService.Lyric(logger, musicId)
	}

End:
	return result, err
}

func (musicService *NeteaseMusicService) EmailLogin(email, password string) error {
	response, err := req.R().Get(fmt.Sprintf("login?email=%s&password=%s", email, password))
	if err != nil {
		return err
	}

	serverConfig := musicService.Ctx.Config.Server

	cookies := response.Cookies()
	if len(cookies) == 0 {
		serverConfig.Cookie = config.EmptyString
		return nil
	}

	cookieString := new(strings.Builder)

	for _, cookie := range cookies {
		cookieString.Write(util.StringToByteSlice(cookie.Name))
		cookieString.WriteString("=")
		cookieString.Write(util.StringToByteSlice(cookie.Value))
		cookieString.WriteString(";")
	}

	serverConfig.Cookie = cookieString.String()
	return nil
}
