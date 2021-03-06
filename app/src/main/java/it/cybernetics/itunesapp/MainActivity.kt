package it.cybernetics.itunesapp

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.cardview.widget.CardView
import coil.api.load
import coil.transform.CircleCropTransformation
import com.google.gson.Gson
import com.xwray.groupie.GroupAdapter
import com.xwray.groupie.GroupieViewHolder
import com.xwray.groupie.Item
import io.reactivex.Completable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import it.cybernetics.itunesapp.dao.DatabaseClient
import it.cybernetics.itunesapp.dao.MediaTrack
import it.cybernetics.itunesapp.service.Api
import it.cybernetics.itunesapp.service.NetworkClient
import kotlinx.android.synthetic.main.activity_main.*
import org.json.JSONObject
import java.util.*
import kotlin.collections.ArrayList
import kotlin.collections.HashMap

class MainActivity : AppCompatActivity(), SelectMediaListener {

    companion object {
        const val TAG = "MainActivity"
    }

    lateinit var db: DatabaseClient

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // initialize db
        db = DatabaseClient.accessDb(this)
        db.mediaTrackDao().listAll().observeOn(AndroidSchedulers.mainThread()).subscribeOn(Schedulers.io())
                    .subscribe({ tracks  ->
                        /**
                         * Check if the tracks are already loaded in the DB, if not load it from the internet
                         * then save it to the DB
                         */
                        if ( tracks.isEmpty() ){
                            loadFromInternet()
                        }else{
                            displayMedias(tracks)
                        }
                    },{
                        Toast.makeText(applicationContext,"Something went wrong...\n${it.localizedMessage}", Toast.LENGTH_LONG).show()
                    })

        var lastAccess = Utils.readPrefs(this, Utils.LAST_ACCESS)
        if(lastAccess == null){
            lastAccess = Utils.formatDate(Date(), Utils.DATE_PATTERN_1)
            Utils.writePrefsString(this, Utils.LAST_ACCESS, lastAccess)
            tv_last_access.visibility = View.GONE
        }else{
            val newlastAccess = Utils.formatDate(Date(), Utils.DATE_PATTERN_1)
            Utils.writePrefsString(this, Utils.LAST_ACCESS, newlastAccess)
            tv_last_access.text = "Last accessed: $lastAccess"
            tv_last_access.visibility = View.VISIBLE
        }
    }

    /**
     * load the data from the internet, i.e. itunes.apple.com
     */
    private fun loadFromInternet(){
        val api = NetworkClient.createRxService(Api::class.java)
        val params = HashMap<String, String>()
        params["term"] = "star"
        params["country"] = "au"
        params["media"] = "movie"
        params["all"] = ""

        progress_loading.visibility = View.VISIBLE

        api.getSearchResults(params).observeOn(AndroidSchedulers.mainThread()).subscribeOn(
            Schedulers.io())
            .subscribe({
                val json = JSONObject(it.string())
                val results = json.getJSONArray("results")
                val mediaTracks = ArrayList<MediaTrack>()
                for ( i in 0 until results.length() ){
                    val obj = results.getJSONObject(i)
                    val media = MediaTrack()
                    media.trackName = obj.getString("trackName")
                    media.collectionPrice = obj.getDouble("collectionPrice")
                    media.mediaImage = obj.getString("artworkUrl100")
                    media.primaryGenreName = obj.getString("primaryGenreName")
                    if( obj.has("longDescription") ){
                        media.longDescription = obj.getString("longDescription")
                    }
                    mediaTracks.add(media)
                }
                // save to database so that next load, it will just get the data from the DB
                saveToDatabase(mediaTracks)
                // display the results on RecyclerView
                runOnUiThread {
                    displayMedias(mediaTracks)
                    progress_loading.visibility = View.GONE
                }
            },{
                progress_loading.visibility = View.GONE
                Toast.makeText(applicationContext,"Something went wrong...\n${it.localizedMessage}", Toast.LENGTH_LONG).show()
            })
    }

    /**
     * Save tracks to DB
     */
    private fun saveToDatabase(tracks: List<MediaTrack>){
        Completable.fromAction {
            db.mediaTrackDao().insert(tracks)
        }.observeOn(AndroidSchedulers.mainThread()).subscribeOn(Schedulers.io())
                    .subscribe({
                    },{
                        Toast.makeText(applicationContext,"Something went wrong...\n${it.localizedMessage}", Toast.LENGTH_LONG).show()
                    })
    }

    /**
     * Display to the RecyclerView
     */
    private fun displayMedias(tracks: List<MediaTrack>){
        val groupAdapter = GroupAdapter<GroupieViewHolder>()
        groupAdapter.clear()
        tracks.forEach {track ->
            groupAdapter.add( MediaTrackItem(track, this@MainActivity) )
        }
        rv_media_tracks.adapter = groupAdapter
    }

    /**
     * Display the details on the selected track and/or movie
     * Serialize the @MediaTrack object to pass to the details activity
     */
    override fun onSelectedMedia(track: MediaTrack) {
        val movie = Gson().toJson(track)
        val detailIntent = Intent(this, DetailsActivity::class.java)
        detailIntent.putExtra("selected_movie", movie)
        startActivity(detailIntent)
    }
}

/**
 * listener for click
 */
interface SelectMediaListener {
    fun onSelectedMedia(track: MediaTrack)
}

/**
 * Item for the RecyclerView
 */
class MediaTrackItem(val track: MediaTrack, val listener: SelectMediaListener) : Item<GroupieViewHolder>() {
    override fun getLayout() = R.layout.media_track_item
    override fun bind(viewHolder: GroupieViewHolder, position: Int) {
        val layoutView = viewHolder.itemView.findViewById<CardView>(R.id.cv_media_item_layout)
        val image = viewHolder.itemView.findViewById<ImageView>(R.id.iv_media_image)
        val name = viewHolder.itemView.findViewById<TextView>(R.id.tv_media_track_name)
        val genre = viewHolder.itemView.findViewById<TextView>(R.id.tv_media_track_genre)
        val price = viewHolder.itemView.findViewById<TextView>(R.id.tv_media_track_price)

        image.load(track.mediaImage){
            placeholder(R.drawable.ic_movie_filter_black_24dp)
            crossfade(true)
            transformations(CircleCropTransformation())
        }
        name.text = track.trackName
        genre.text = "Genre: ${track.primaryGenreName}"
        price.text = "Price: ${track.collectionPrice}"
        layoutView.setOnClickListener {
            listener.onSelectedMedia(track)
        }
    }

}