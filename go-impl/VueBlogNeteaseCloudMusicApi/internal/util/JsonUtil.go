package util

import "github.com/bytedance/sonic"

func JsonGetString(json string, path ...interface{}) (string, error) {
	node, _ := sonic.Get(StringToByteSlice(json), path...)
	return node.String()
}

func JsonGetInt64(json string, path ...interface{}) (int64, error) {
	node, _ := sonic.Get(StringToByteSlice(json), path...)
	return node.Int64()
}
