package test

import (
	"VueBlogNeteaseCloudMusicApi/internal/util"
	"testing"
)

func TestGetJsonPath(t *testing.T) {
	str := `{ "name": "123123", "age": 50 }`
	r1, err := util.JsonGetString(str, "name")
	if err != nil {
		t.Fatal(err)
	}
	println(r1)

	r2, err := util.JsonGetInt64(str, "age")
	if err != nil {
		t.Fatal(err)
	}
	println(r2)
}
