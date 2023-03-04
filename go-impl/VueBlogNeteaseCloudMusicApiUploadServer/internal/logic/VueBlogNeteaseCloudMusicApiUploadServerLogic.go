package logic

import (
	"context"

	"VueBlogNeteaseCloudMusicApiUploadServer/internal/svc"
	"VueBlogNeteaseCloudMusicApiUploadServer/internal/types"

	"github.com/zeromicro/go-zero/core/logx"
)

type VueBlogNeteaseCloudMusicApiUploadServerLogic struct {
	logx.Logger
	ctx    context.Context
	svcCtx *svc.ServiceContext
}

func NewVueBlogNeteaseCloudMusicApiUploadServerLogic(ctx context.Context, svcCtx *svc.ServiceContext) *VueBlogNeteaseCloudMusicApiUploadServerLogic {
	return &VueBlogNeteaseCloudMusicApiUploadServerLogic{
		Logger: logx.WithContext(ctx),
		ctx:    ctx,
		svcCtx: svcCtx,
	}
}

func (l *VueBlogNeteaseCloudMusicApiUploadServerLogic) VueBlogNeteaseCloudMusicApiUploadServer(req *types.Request) (resp *types.Response, err error) {
	// todo: add your logic here and delete this line

	return
}
