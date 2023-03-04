package main

import (
	"VueBlogNeteaseCloudMusicApi/internal/service/impl"
	"flag"
	"fmt"

	"VueBlogNeteaseCloudMusicApi/internal/config"
	"VueBlogNeteaseCloudMusicApi/internal/handler"
	"VueBlogNeteaseCloudMusicApi/internal/svc"

	"github.com/zeromicro/go-zero/core/conf"
	"github.com/zeromicro/go-zero/rest"
)

var configFile = flag.String("f", "etc/VueBlogNeteaseCloudMusicApi-Api.yaml", "the config file")

func main() {
	flag.Parse()

	conf.MustLoad(*configFile, &config.C)

	server := rest.MustNewServer(config.C.RestConf)
	defer server.Stop()

	ctx := svc.NewServiceContext(config.C)
	handler.RegisterHandlers(server, ctx)

	impl.NeteaseMusicServiceInstance.Ctx = ctx

	fmt.Printf("Starting server at %s:%d...\n", config.C.Host, config.C.Port)
	server.Start()
}
