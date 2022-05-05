package fi.centria.tki.lessonlocation

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.preference.PreferenceManager
import org.osmdroid.tileprovider.tilesource.TileSourceFactory
import org.osmdroid.views.MapView
import org.osmdroid.config.Configuration.*
import org.osmdroid.util.GeoPoint
import org.osmdroid.views.overlay.Marker
import org.osmdroid.views.overlay.mylocation.GpsMyLocationProvider
import org.osmdroid.views.overlay.mylocation.MyLocationNewOverlay

class MapActivity : AppCompatActivity() {

    private lateinit var map: MapView
    private lateinit var mlocationOverlay: MyLocationNewOverlay

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_map)
        getInstance().load(this, PreferenceManager.getDefaultSharedPreferences(this))
        map = findViewById(R.id.map_layout)
        map.setTileSource(TileSourceFactory.MAPNIK)

        val mapController = map.controller
        mapController.setZoom(12)
        val startPoint = GeoPoint(63.8319,23.1315)
        mapController.setCenter(startPoint)

        mlocationOverlay = MyLocationNewOverlay(GpsMyLocationProvider(this),map)
        mlocationOverlay.enableMyLocation()
        map.overlays.add(mlocationOverlay)

        val my_point: GeoPoint = GeoPoint(63.84, 23.14)
        val marker = Marker(map)
        marker.position = my_point
        marker.title = resources.getString(R.string.marker_text)
        marker.setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_BOTTOM)
        map.overlays.add(marker)
    }

    override fun onResume() {
        super.onResume()
        map.onResume()
    }

    override fun onPause() {
        super.onPause()
        map.onPause()

    }
}