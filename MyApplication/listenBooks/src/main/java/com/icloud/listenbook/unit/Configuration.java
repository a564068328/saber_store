package com.icloud.listenbook.unit;

public class Configuration {
	
	public static final int STANDARD_TITLE = 1;
	public static final int STANDARD_BODY = STANDARD_TITLE+1;
	public static final int STANDARD_BOTTOM = STANDARD_BODY+1;
	
	public static final int TYPE_VEDIO = 1;
	public static final int TYPE_VOICE = TYPE_VEDIO + 1;
	public static final int TYPE_BOOK = TYPE_VOICE + 1;
	public static final int TYPE_WEBVIEW = TYPE_BOOK + 1;
	public static final int TYPE_WORD = TYPE_BOOK + 1;
	public static final int TYPE_WHATTHEFUCK = TYPE_WORD + 1;
	public static final int TYPE_MASTERWROK = TYPE_WHATTHEFUCK + 1;
	public static final int TYPE_GREAT = TYPE_MASTERWROK + 1;
	public static final int TYPE_CATEGORY = TYPE_GREAT + 1;

	public static final String MUSICS_REVICE_ACTION = "com.icloud.music.MUSICS_REVICE_ACTION";
	public static final String VEDIO_REVICE_ACTION = "com.icloud.music.VEDIO_REVICE_ACTION";
	public static final String MEDIA_PLAYER_REVICE_ACTION = "com.icloud.mediaPlayer.MEDIA_PLAYER_ACTION";
	public static final String MUSIC_BOX_ACTION = "com.icloud.music.MUSIC_BOX_ACTION";
	public static final String VEDIO_BOX_ACTION = "com.icloud.vedio.VEDIO_BOX_ACTION";
	public static final String MEDIA_PLAYER_BOX_ACTION = "com.icloud.mediaPlayer.MEDIA_PLAYER_BOX_ACTION";

	public static final int STATE_NON = 1;
	public static final int STATE_PLAY = STATE_NON + 1;
	public static final int STATE_PAUSE = STATE_PLAY + 1;
	public static final int STATE_STOP = STATE_PAUSE + 1;
	public static final int STATE_SEEKTO = STATE_STOP + 1;
	public static final int STATE_END = STATE_SEEKTO + 1;

	public static final int STATE_MUSIC_BIND_END = STATE_SEEKTO + 1;
	public static final int STATE_MUSIC_LOADING_POS = STATE_MUSIC_BIND_END + 1;
	public static final int STATE_MUSIC_COMPLETION = STATE_MUSIC_LOADING_POS + 1;
	public static final int STATE_MUSIC_PREPARED = STATE_MUSIC_COMPLETION + 1;
	public static final int STATE_MUSIC_PLAY_POSTION = STATE_MUSIC_PREPARED + 1;
}
