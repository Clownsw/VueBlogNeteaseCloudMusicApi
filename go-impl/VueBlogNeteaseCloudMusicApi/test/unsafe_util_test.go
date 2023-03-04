package test

import (
	"VueBlogNeteaseCloudMusicApi/internal/util"
	"testing"
)

func TestStringToByteSlice(t *testing.T) {
	s := "Hello, World"
	util.StringIsEmpty(s)
}

func TestByteSliceToSting(t *testing.T) {
	s := "Hello, World"
	println(util.ByteSliceToString(util.StringToByteSlice(s)))
}
