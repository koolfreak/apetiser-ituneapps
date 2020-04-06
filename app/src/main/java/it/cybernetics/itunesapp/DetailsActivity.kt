package it.cybernetics.itunesapp

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import coil.api.load
import coil.transform.CircleCropTransformation
import com.google.gson.Gson
import it.cybernetics.itunesapp.dao.MediaTrack
import kotlinx.android.synthetic.main.movie_details_activity.*

class DetailsActivity: AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.movie_details_activity)

        val selectedTrack = intent.getStringExtra("selected_movie")
        val mediaTrack = Gson().fromJson<MediaTrack>(selectedTrack, MediaTrack::class.java)

        iv_details_image.load(mediaTrack.mediaImage){
            placeholder(R.drawable.ic_movie_filter_black_24dp)
            crossfade(true)
        }

        tv_media_track_name.text = mediaTrack.trackName
        tv_media_track_price.text = "Price: ${mediaTrack.collectionPrice}"
        tv_media_track_genre.text = "Genre: ${mediaTrack.primaryGenreName}"
        tv_media_track_description.text = mediaTrack.longDescription

        btn_close_details.setOnClickListener {
            finish()
        }
    }
}