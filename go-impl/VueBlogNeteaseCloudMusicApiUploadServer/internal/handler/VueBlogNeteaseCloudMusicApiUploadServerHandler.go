package handler

import (
	"net/http"

	"VueBlogNeteaseCloudMusicApiUploadServer/internal/logic"
	"VueBlogNeteaseCloudMusicApiUploadServer/internal/svc"
	"VueBlogNeteaseCloudMusicApiUploadServer/internal/types"
	"github.com/zeromicro/go-zero/rest/httpx"
)

func VueBlogNeteaseCloudMusicApiUploadServerHandler(svcCtx *svc.ServiceContext) http.HandlerFunc {
	return func(w http.ResponseWriter, r *http.Request) {
		var req types.Request
		if err := httpx.Parse(r, &req); err != nil {
			httpx.ErrorCtx(r.Context(), w, err)
			return
		}

		l := logic.NewVueBlogNeteaseCloudMusicApiUploadServerLogic(r.Context(), svcCtx)
		resp, err := l.VueBlogNeteaseCloudMusicApiUploadServer(&req)
		if err != nil {
			httpx.ErrorCtx(r.Context(), w, err)
		} else {
			httpx.OkJsonCtx(r.Context(), w, resp)
		}
	}
}
