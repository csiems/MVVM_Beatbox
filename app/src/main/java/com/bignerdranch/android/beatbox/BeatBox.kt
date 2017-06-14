package com.bignerdranch.android.beatbox

import android.content.Context
import android.content.res.AssetManager
import android.media.AudioManager
import android.media.SoundPool
import android.util.Log
import java.io.IOException
import java.util.*

class BeatBox(context: Context) {

    private val mAssets: AssetManager = context.assets
    private val mSounds = ArrayList<Sound>()
    private val mSoundPool: SoundPool
    private var mSpeed: Float = 0.toFloat()
    private val TAG = "Beatbox"
    private val SOUNDS_FOLDER = "sample_sounds"
    private val MAX_SOUNDS = 5

    init {
        //This old constructor is deprecated, but needed for compatibility
        mSoundPool = SoundPool(MAX_SOUNDS, AudioManager.STREAM_MUSIC, 0)
        mSpeed = 1.0f
        loadSounds()
    }

    fun setSpeed(speed: Float) {
        mSpeed = (speed + 20) / 100
    }

    fun play(sound: Sound) {
        val soundId = sound.soundId ?: return
        mSoundPool.play(soundId, //sound id
                1.0f,            //vol left
                1.0f,            //vol right
                1,               //priority
                0,               //loop? yes or no
                mSpeed)          //playback rate
    }

    fun release() {
        mSoundPool.release()
    }

    private fun loadSounds() {
        val soundNames: Array<String>
        try {
            soundNames = mAssets.list(SOUNDS_FOLDER)
            Log.i(TAG, "Found " + soundNames.size + " sounds.")
        } catch (ioe: IOException) {
            Log.e(TAG, "Could not list assets", ioe)
            return
        }

        for (filename in soundNames) {
            try {
                val assetPath = SOUNDS_FOLDER + "/" + filename
                val sound = Sound(assetPath)
                load(sound)
                mSounds.add(sound)
            } catch (ioe: IOException) {
                Log.e(TAG, "Could not load sound " + filename, ioe)
            }

        }
    }

    @Throws(IOException::class)
    private fun load(sound: Sound) {
        val afd = mAssets.openFd(sound.assetPath)
        val soundId = mSoundPool.load(afd, 1)
        sound.soundId = soundId
    }

    val sounds: List<Sound>
        get() = mSounds
}
