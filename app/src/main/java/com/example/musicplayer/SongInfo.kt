package com.example.musicplayer

class SongInfo {
    var songname: String? = null
    var artistname: String? = null
    var songUrl: String? = null

    constructor() {}
    constructor(songname: String?, artistname: String?, songUrl: String?) {
        this.songname = songname
        this.artistname = artistname
        this.songUrl = songUrl
    }


}