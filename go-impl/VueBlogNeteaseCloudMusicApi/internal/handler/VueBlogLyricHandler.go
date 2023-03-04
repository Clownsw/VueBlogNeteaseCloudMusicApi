package handler

import (
	"net/http"

	"VueBlogNeteaseCloudMusicApi/internal/logic"
	"VueBlogNeteaseCloudMusicApi/internal/svc"
	"VueBlogNeteaseCloudMusicApi/internal/types"
	"github.com/zeromicro/go-zero/rest/httpx"
)

func VueBlogLyricHandler(svcCtx *svc.ServiceContext) http.HandlerFunc {
	return func(w http.ResponseWriter, r *http.Request) {
		var req types.VueBlogLyricRequest
		if err := httpx.Parse(r, &req); err != nil {
			httpx.ErrorCtx(r.Context(), w, err)
			return
		}

		l := logic.NewVueBlogLyricLogic(r.Context(), svcCtx)
		resp, err := l.VueBlogLyric(&req)
		if err != nil {
			httpx.ErrorCtx(r.Context(), w, err)
		} else {
			httpx.OkJsonCtx(r.Context(), w, resp)
		}
	}
}
