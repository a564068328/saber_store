package com.icloud.listenbook.service;;

public interface playerInterface {
	void play();
	void pause();
	void continueplay();
	void exit();
	void seekTo(int msec);
}
