package com.photex.tiktok.mp4parser

import android.app.Activity
import android.util.Log
import com.googlecode.mp4parser.FileDataSourceImpl
import com.googlecode.mp4parser.authoring.Movie
import com.googlecode.mp4parser.authoring.Track
import com.googlecode.mp4parser.authoring.builder.DefaultMp4Builder
import com.googlecode.mp4parser.authoring.container.mp4.MovieCreator
import com.googlecode.mp4parser.authoring.tracks.AACTrackImpl
import com.googlecode.mp4parser.authoring.tracks.AppendTrack
import com.googlecode.mp4parser.authoring.tracks.CroppedTrack
import com.googlecode.mp4parser.authoring.tracks.MP3TrackImpl
import com.photex.tiktok.BuildConfig
import com.photex.tiktok.utils.FileUtils
import com.photex.tiktok.utils.mp3agic.ID3v2
import com.photex.tiktok.utils.mp3agic.Mp3File
import io.reactivex.Observable
import java.io.*
import java.nio.channels.FileChannel
import java.util.*

class AppendMp4Media(private val acticity: Activity, private val filePaths: List<String>,
                     private val audioPath: String) {

    companion object {

        private val TAG = "AppendMp4Media"
    }

    fun append(): Observable<String> {
        return doAppend()
    }

    private fun doAppend(): Observable<String> {

        try {

            val fileList = ArrayList<File>()
            val movieList = ArrayList<Movie>()
            for (i in filePaths.indices) {
                fileList.add(File(filePaths[i]))
                movieList.add(MovieCreator.build(File(filePaths[i]).absolutePath))

                if (BuildConfig.DEBUG) {
                    val file = File(File(filePaths[i]).absolutePath)
                    val file_size = file.length() / 1024

                    Log.d(TAG, "file Size = " + filePaths[i] + file_size)

                }

            }

            // Create a media file name
            val filePath = FileUtils.getTempVideoFileName(acticity.applicationContext).absolutePath
            /* Utils.outputPath + File.separator + "TMP4_APP_OUT_" + Utils.getTimeStamp() + ".mp4"*/

            val videoTracks = LinkedList<Track>()
            val audioTracks = LinkedList<Track>()
            val audioDuration = longArrayOf(0)
            val videoDuration = longArrayOf(0)
            for (m in movieList) {

                for (t in m.tracks) {
                    if (t.handler == "soun") {
                        for (a in t.sampleDurations) audioDuration[0] += a
                        audioTracks.add(t)
                    } else if (t.handler == "vide") {
                        for (v in t.sampleDurations) videoDuration[0] += v
                        videoTracks.add(t)
                    }
                }
            }

            //Result movie from putting the audio and video together from the two clips
            val result = Movie()

            //Append all audio and video
            if (videoTracks.size > 0)
                result.addTrack(AppendTrack(*videoTracks.toTypedArray()))

            if (audioTracks.size > 0)
                result.addTrack(AppendTrack(*audioTracks.toTypedArray()))


            val out = DefaultMp4Builder().build(result)
            val fc = RandomAccessFile(String.format(filePath), "rw").channel
            out.writeContainer(fc)
            fc.close()

            if (BuildConfig.DEBUG) {
                val file = File(filePath)
                val file_size = file.length() / 1024


                Log.d(TAG, "file Size = " + file_size)

                Log.d(TAG, "file path = " + filePath)
            }


            return Observable.just(filePath)

        } catch (e: FileNotFoundException) {
            e.printStackTrace()
        } catch (e: IOException) {
            e.printStackTrace()
        } catch (e: NullPointerException) {
            e.printStackTrace()
        }

        return Observable.just("")
    }

    private fun adjustDurations(videoTracks: LinkedList<Track>, audioTracks: LinkedList<Track>,
                                videoDuration: LongArray, audioDuration: LongArray) {
        var diff = audioDuration[0] - videoDuration[0]

        //nothing to do
        if (diff == 0L) {
            return
        }

        //audio is longer
        var tracks = audioTracks

        //video is longer
        if (diff < 0) {
            tracks = videoTracks
            diff *= -1
        }

        var track = tracks.last
        val sampleDurations = track.sampleDurations
        var counter: Long = 0
        for (i in sampleDurations.size - 1 downTo -1 + 1) {
            if (sampleDurations[i] > diff) {
                break
            }
            diff -= sampleDurations[i]
            audioDuration[0] -= sampleDurations[i]
            counter++
        }

        if (counter == 0L) {
            return
        }

        track = CroppedTrack(track, 0, track.samples.size - counter)

        //update the original reference
        tracks.removeLast()
        tracks.addLast(track)
    }

    private fun getAudioTrack(): Track {

        var track = FileDataSourceImpl(audioPath)
        var audio: Track? = null

        if (audioPath.contains(".mp3")) {
            readTags()
            val audioPath = removeTags()
            track = FileDataSourceImpl(audioPath)
            audio = MP3TrackImpl(track)
        } else if (audioPath.contains(".aac")) {
            audio = AACTrackImpl(track)
        }
        return audio!!
    }

    private fun readTags() {
        val mp3file = Mp3File(File(audioPath))

        if (mp3file.hasId3v2Tag()) {
            val id3v2Tag = mp3file.getId3v2Tag()
            /*  println("Track: " + id3v2Tag.getTrack())
              println("Artist: " + id3v2Tag.getArtist())
              println("Title: " + id3v2Tag.getTitle())
              println("Album: " + id3v2Tag.getAlbum())
              println("Year: " + id3v2Tag.getYear())
              println("Genre: " + id3v2Tag.getGenre() + " (" + id3v2Tag.getGenreDescription() + ")")
              println("Comment: " + id3v2Tag.getComment())
              println("Composer: " + id3v2Tag.getComposer())
              println("Publisher: " + id3v2Tag.getPublisher())
              println("Original artist: " + id3v2Tag.getOriginalArtist())
              println("Album artist: " + id3v2Tag.getAlbumArtist())
              println("Copyright: " + id3v2Tag.getCopyright())
              println("URL: " + id3v2Tag.getUrl())
              println("Encoder: " + id3v2Tag.getEncoder())*/
            val albumImageData = id3v2Tag.getAlbumImage()
            if (albumImageData != null) {
                println("Have album image data, length: " + albumImageData!!.size + " bytes")
                println("Album image mime type: " + id3v2Tag.getAlbumImageMimeType())
            }
        }
    }


    private fun removeTags(): String {
        val filePath = FileUtils.getCleanedMp3File(acticity).absolutePath
        val mp3file = Mp3File(File(audioPath))

        val id3v2Tag: ID3v2
        if (mp3file.hasId3v2Tag()) {
            mp3file.removeId3v2Tag()
            /* id3v2Tag = mp3file.id3v2Tag*/
        }

        if (mp3file.hasId3v1Tag()) {
            mp3file.removeId3v1Tag()
        }

        if (mp3file.hasCustomTag()) {
            mp3file.removeCustomTag()
        }
        /*else {
            // mp3 does not have an ID3v2 tag, let's create one..
            id3v2Tag = ID3v24Tag()
            mp3file.id3v2Tag = id3v2Tag
        }*/
/*        id3v2Tag.track = "5"
        id3v2Tag.artist = "An Artist"
        id3v2Tag.title = "The Title"
        id3v2Tag.album = "The Album"
        id3v2Tag.year = "2001"
        id3v2Tag.genre = 12
        id3v2Tag.comment = "Some comment"
        id3v2Tag.composer = "The Composer"
        id3v2Tag.publisher = "A Publisher"
        id3v2Tag.originalArtist = "Another Artist"
        id3v2Tag.albumArtist = "An Artist"
        id3v2Tag.copyright = "Copyright"
        id3v2Tag.url = "http://foobar"
        id3v2Tag.encoder = "The Encoder"*/

        mp3file.save(filePath)

        return filePath
    }


}
