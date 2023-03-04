package logic

import (
	"VueBlogNeteaseCloudMusicApi/internal/config"
	"VueBlogNeteaseCloudMusicApi/internal/service/impl"
	"VueBlogNeteaseCloudMusicApi/internal/svc"
	"VueBlogNeteaseCloudMusicApi/internal/types"
	"VueBlogNeteaseCloudMusicApi/internal/util"
	"context"
	"fmt"

	"github.com/zeromicro/go-zero/core/logx"
)

type VueBlogLyricLogic struct {
	logx.Logger
	ctx    context.Context
	svcCtx *svc.ServiceContext
}

func NewVueBlogLyricLogic(ctx context.Context, svcCtx *svc.ServiceContext) *VueBlogLyricLogic {
	return &VueBlogLyricLogic{
		Logger: logx.WithContext(ctx),
		ctx:    ctx,
		svcCtx: svcCtx,
	}
}

func (l *VueBlogLyricLogic) VueBlogLyric(req *types.VueBlogLyricRequest) (resp string, err error) {
	c := l.svcCtx.Config

	key := fmt.Sprintf("%s%s", c.Server.RedisLyricCachePrefix, req.Id)
	value, err := util.RedisGetString(l.svcCtx.RedisClient, key)

	if err != nil {
		goto ErrorHandler
	}

	if util.StringIsNotEmpty(value) {
		return value, nil
	}

	value, err = impl.NeteaseMusicServiceInstance.Lyric(l, req.Id)
	if err != nil {
		goto ErrorHandler
	}

	if util.StringIsEmpty(value) {
		return config.EmptyString, config.MusicLyricNotFound
	}

	goto End

ErrorHandler:
	l.Logger.Error(err)
	return config.EmptyString, config.SystemError

End:
	return value, nil
}
