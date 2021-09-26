package com.example.musicplayer

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import java.util.ArrayList


class SongAdapter(private val context: Context, songs: ArrayList<SongInfo>) :
    RecyclerView.Adapter<SongAdapter.SongHolder>() {
    private var _songs: ArrayList<SongInfo> = ArrayList<SongInfo>()
    private var mOnItemClickListener: OnItemClickListener? = null

    interface OnItemClickListener {
        fun onItemClick(b: Button?, view: View?, obj: SongInfo?, position: Int)
    }

    fun setOnItemClickListener(mItemClickListener: OnItemClickListener?) {
        mOnItemClickListener = mItemClickListener
    }

    override fun onCreateViewHolder(viewGroup: ViewGroup, i: Int): SongHolder {
        val myView = LayoutInflater.from(context).inflate(R.layout.row_songs, viewGroup, false)
        return SongHolder(myView)
    }

    override fun onBindViewHolder(songHolder: SongHolder, i: Int) {
        val s: SongInfo = _songs[i]
        songHolder.tvSongName.setText(_songs[i].songname)
        songHolder.tvSongArtist.setText(_songs[i].artistname)
        songHolder.btnAction.setOnClickListener { v ->
            if (mOnItemClickListener != null) {
                mOnItemClickListener!!.onItemClick(songHolder.btnAction, v, s, i)
            }
        }
    }

    override fun getItemCount(): Int {
        return _songs.size
    }

    inner class SongHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var tvSongName: TextView
        var tvSongArtist: TextView
        var btnAction: Button

        init {
            tvSongName = itemView.findViewById<View>(R.id.tvSongName) as TextView
            tvSongArtist = itemView.findViewById<View>(R.id.tvArtistName) as TextView
            btnAction = itemView.findViewById<View>(R.id.btnPlay) as Button
        }
    }

    init {
        _songs = songs
    }
}