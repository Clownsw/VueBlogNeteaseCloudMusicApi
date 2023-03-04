package util

func AnyToT[T any](a any, b T) T {
	r, ok := a.(T)

	if ok {
		return r
	}

	return b
}
