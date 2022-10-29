package com.example.ejerciciogps

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.lifecycle.lifecycleScope
import com.example.ejerciciogps.Coordenadas.Monticulo
import com.example.ejerciciogps.Coordenadas.ValleDeLaLuna
import com.example.ejerciciogps.Coordenadas.Zoologico
import com.example.ejerciciogps.Coordenadas.univalle

import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import com.example.ejerciciogps.databinding.ActivityMapsBinding
import com.google.android.gms.maps.model.CameraPosition
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class MapsActivity : AppCompatActivity(), OnMapReadyCallback {

    private lateinit var mMap: GoogleMap
    private lateinit var binding: ActivityMapsBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMapsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        val mapFragment = supportFragmentManager
            .findFragmentById(R.id.map) as SupportMapFragment
        mapFragment.getMapAsync(this)
    }

    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near Sydney, Australia.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */
    override fun onMapReady(googleMap: GoogleMap) {

        mMap = googleMap


        mMap.apply {
            setMinZoomPreference(14f)
            setMaxZoomPreference(18f)
        }

        // Add a marker in Sydney and move the camera

        mMap.addMarker(MarkerOptions().position(Monticulo).title("Marker in Monticulo").draggable(true))
        mMap.addMarker(MarkerOptions().position(ValleDeLaLuna).title("Marker in Valle de la Luna").draggable(true))
        mMap.addMarker(MarkerOptions().position(Zoologico).title("Marker in Zoologico").draggable(true))
        mMap.addMarker(MarkerOptions().position(univalle).title("Marker in univalle").draggable(true))


        val cameraUnivalle = CameraPosition.builder()
            .bearing(240f)
            .tilt(75f)
            .zoom(16f)
            .target(univalle)
            .build()
        mMap.moveCamera(CameraUpdateFactory.newCameraPosition(cameraUnivalle))

        lifecycleScope.launch{
            delay(5000)
            mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(Monticulo,20f))
            delay(7000)
            mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(Monticulo,20f))
        }
        mMap.setOnMapClickListener {

            mMap.addMarker(MarkerOptions().position(it).title("Mi nueva posicion").snippet("${it.latitude}, ${it.longitude}"))
            mMap.moveCamera(CameraUpdateFactory.newLatLng(it))
        }
    }
}