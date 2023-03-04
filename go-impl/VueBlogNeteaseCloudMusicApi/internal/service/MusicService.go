package service

type MusicService interface {
	Lyric(musicId string) (string, error)
	EmailLogin(email, password string) error
}
