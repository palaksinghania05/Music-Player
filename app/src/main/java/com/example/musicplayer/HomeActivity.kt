package com.example.musicplayer

import android.Manifest
import android.content.pm.PackageManager
import android.media.MediaPlayer
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.provider.MediaStore
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.SeekBar
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import java.lang.Exception
import java.util.ArrayList


class HomeActivity : AppCompatActivity() {
    private val _songs: ArrayList<SongInfo> = ArrayList<SongInfo>()
    var recyclerView: RecyclerView? = null
    var seekBar: SeekBar? = null
    var songAdapter: SongAdapter? = null
    var mediaPlayer: MediaPlayer? = null
    private val myHandler = Handler()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        recyclerView = findViewById<View>(R.id.recyclerView) as RecyclerView
        seekBar = findViewById<View>(R.id.seekBar) as SeekBar
        songAdapter = SongAdapter(this, _songs)
        recyclerView!!.adapter = songAdapter
        val linearLayoutManager = LinearLayoutManager(this)
        val dividerItemDecoration = DividerItemDecoration(
            recyclerView!!.context,
            linearLayoutManager.orientation
        )
        recyclerView!!.layoutManager = linearLayoutManager
        recyclerView!!.addItemDecoration(dividerItemDecoration)
        songAdapter!!.setOnItemClickListener(object : SongAdapter.OnItemClickListener {
            override fun onItemClick(b: Button?, view: View?, obj: SongInfo?, position: Int) {
                if (b != null) {
                    if (b.text == "Stop") {
                        mediaPlayer!!.stop()
                        mediaPlayer!!.reset()
                        mediaPlayer!!.release()
                        mediaPlayer = null
                        b.text = "Play"
                    } else {
                        val runnable = Runnable {
                            try {
                                mediaPlayer = MediaPlayer()
                                if (obj != null) {
                                    mediaPlayer!!.setDataSource(obj.songUrl)
                                }
                                mediaPlayer!!.prepareAsync()
                                mediaPlayer!!.setOnPreparedListener { mp ->
                                    mp.start()
                                    seekBar!!.progress = 0
                                    seekBar!!.max = mediaPlayer!!.duration
                                    Log.d("Prog", "run: " + mediaPlayer!!.duration)
                                }
                                b.text = "Stop"
                            } catch (e: Exception) {
                            }
                        }
                        myHandler.postDelayed(runnable, 100)
                    }
                }
            }
        })
        checkUserPermission()
        val t: Thread = runThread()
        t.start()
    }

    inner class runThread : Thread() {
        override fun run() {
            while (true) {
                try {
                    sleep(1000)
                } catch (e: InterruptedException) {
                    e.printStackTrace()
                }
                Log.d("Runwa", "run: " + 1)
                if (mediaPlayer != null) {
                    seekBar!!.post { seekBar!!.progress = mediaPlayer!!.currentPosition }
                    Log.d("Runwa", "run: " + mediaPlayer!!.currentPosition)
                }
            }
        }
    }

    private fun checkUserPermission() {
        if (Build.VERSION.SDK_INT >= 23) {
            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED
            ) {
                requestPermissions(arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE), 123)
                return
            }
        }
        loadSongs()
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<String>,
        grantResults: IntArray
    ) {
        when (requestCode) {
            123 -> if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                loadSongs()
            } else {
                Toast.makeText(this, "Permission Denied", Toast.LENGTH_SHORT).show()
                checkUserPermission()
            }
            else -> super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        }
    }

    private fun loadSongs() {
        val uri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI
        val selection = MediaStore.Audio.Media.IS_MUSIC + "!=0"
        val cursor = contentResolver.query(uri, null, selection, null, null)
        if (cursor != null) {
            if (cursor.moveToFirst()) {
                do {
                    val name =
                        cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Media.DISPLAY_NAME))
                    val artist =
                        cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Media.ARTIST))
                    val url = cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Media.DATA))
                    val s = SongInfo(name, artist, url)
                    _songs.add(s)
                } while (cursor.moveToNext())
            }
            cursor.close()
            songAdapter = SongAdapter(this@HomeActivity, _songs)
        }
    }
}