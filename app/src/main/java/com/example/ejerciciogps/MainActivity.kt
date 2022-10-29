package com.example.ejerciciogps

import android.annotation.SuppressLint
import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import android.content.pm.PackageManager
import android.location.Geocoder
import android.location.Location
import android.location.LocationManager
import android.location.LocationRequest
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Looper
import android.print.PrintAttributes.Margins
import android.provider.Settings
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.content.getSystemService
import androidx.core.location.LocationCompat
import com.example.ejerciciogps.Constantes.INTERVAL_TIME
import com.example.ejerciciogps.databinding.ActivityMainBinding
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationCallback
import com.google.android.gms.location.LocationResult
import com.google.android.gms.location.LocationServices
import com.google.android.gms.location.Priority
import java.lang.Math.pow
import kotlin.math.cos
import kotlin.math.sqrt

class MainActivity : AppCompatActivity() {
 companion object {
     private val PERMISION_ENABLED = arrayOf(
         android.Manifest.permission.ACCESS_COARSE_LOCATION,
         android.Manifest.permission.ACCESS_FINE_LOCATION
     )
 }

    private lateinit var binding: ActivityMainBinding
    private var enabledGPS = false
    private var PERMISION_TD = 42
    private lateinit var fusedLocation: FusedLocationProviderClient
    private var latitud :Double = 0.0
    private var longitud :Double = 0.0
    private var contador = 0
    private var distancia = 0.0
    private var distTotal = 0.0
    private var velocidad = 0.0
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding= ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        binding.fabGPS.setOnClickListener{
            view -> enabledGPSService()
        }
        binding.fbpGps02.setOnClickListener {
            initGPSService()
        }
    }

    @SuppressLint("MissingPermission")
    private fun initGPSService() {
        if(hasGPSEnabled()){
            if(hasPermissionGranted()){
                fusedLocation = LocationServices.getFusedLocationProviderClient(this)
                fusedLocation.lastLocation.addOnSuccessListener {
                    location -> managelocationData()
                }

            }
            else{
                requestPermissionLocation()
            }
        }
        else{
            goToEnableGPS()
        }
    }
private fun requestPermissionLocation(){
    ActivityCompat.requestPermissions(this, PERMISION_ENABLED, PERMISION_TD)
}
    @SuppressLint("MissingPermission")
    private fun managelocationData() {
        var myLocationRequest = com.google.android.gms.location.LocationRequest.Builder(
            Priority.PRIORITY_HIGH_ACCURACY,
            INTERVAL_TIME
        ).setMaxUpdates(10)
          .build()

        //var myLocationRequest = com.google.android.gms.location.LocationRequest.create().apply {
          //  priority = Priority.PRIORITY_HIGH_ACCURACY
            //interval = 0
            //fastestInterval = 0
            //numUpdates = 1
          //}
          //fusedLocation = LocationServices.getFusedLocationProviderClient(this)
        fusedLocation.requestLocationUpdates(myLocationRequest, myLocationCallBack, Looper.myLooper())
    }
    var myLocationCallBack = object :LocationCallback(){
        override fun onLocationResult(locationResult: LocationResult) {
            super.onLocationResult(locationResult)
            var myLastLocation: Location? = locationResult.lastLocation
            if(myLastLocation != null){
                var lastLatitude = myLastLocation.latitude
                var lastLongitude = myLastLocation.longitude
                binding.txtLatitud.text = lastLatitude.toString()
                binding.txtLongitud.text = lastLongitude.toString()
             if(contador >0){
              distancia= calculateDistance(lastLatitude, lastLongitude)
                 distTotal +=distancia;
                 binding.txtTotal.text ="$distTotal mts"
              binding.txtDistancia.text="$distancia mts"
                 velocidad = calculateVelocity()
                 binding.txtVelocidad.text ="$velocidad Km/h"
             }
                latitud= myLastLocation.latitude
                longitud=myLastLocation.longitude
                contador++
                resolverAdress()
            }
            else
                Toast.makeText(applicationContext, "No fue posible activar las coordenadas", Toast.LENGTH_SHORT).show()
        }
    }

    private fun resolverAdress() {
        val geocoder = Geocoder(this)
        try {
            var direcciones = geocoder.getFromLocation(latitud, longitud, 1)
            binding.txtDireccion.text= direcciones.get(0).getAddressLine(0)
        } catch (e: java.lang.Exception){
            binding.txtDireccion.text ="Direccion no disponible"
        }

    }
private fun calculateDistance(LastLatitude: Double, LastLongitude:Double):Double{
    val radioTierra = 6371
    val diffLatitude = Math.toRadians(LastLatitude - latitud)
    val diffLongitude = Math.toRadians(LastLongitude - longitud)
    val SinLatitud = Math.sin(diffLatitude /2)
    val SinLongitud = Math.sin(diffLongitude /2)
    val resultado1 =(pow(SinLatitud, 2.0)+ (pow(SinLongitud,2.0)
            * cos(Math.toRadians(latitud))
            * cos(Math.toRadians(LastLatitude))
            ))
    val resultado2 = 2 * Math.atan2(sqrt(resultado1), sqrt(1- resultado1))
    val distancia = (radioTierra * resultado2) * 1000.0
    return distancia
}

    private fun calculateVelocity():Double =(distancia / (INTERVAL_TIME/1000.0)) * 3.6





    private fun enabledGPSService() {
        if (!hasGPSEnabled()) {
            AlertDialog.Builder(this)
                .setTitle(R.string.dialog_text_title)
                .setMessage(R.string.dialog_text_description)
                .setPositiveButton(R.string.dialog_button_acept,
                    DialogInterface.OnClickListener { dialog, wich ->
                        goToEnableGPS()
                    })
                .setNegativeButton(R.string.dialog_button_deny)
                { dialog, wich ->
                    CancelGPS()
                    enabledGPS = true
                }
                .setCancelable(true)
                .show()
        }
    }

    private fun CancelGPS() {
       enabledGPS =false
    }

    private fun goToEnableGPS() {
        val intent = Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS)
        startActivity(intent)
    }

    private fun hasGPSEnabled():Boolean{
        var locationManager : LocationManager
        = getSystemService(Context.LOCATION_SERVICE) as LocationManager
         return locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)
        || locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER)
    }

    private fun isGPSPermissionEnabled():Boolean= ContextCompat.checkSelfPermission(baseContext,android.Manifest.permission.ACCESS_COARSE_LOCATION)==PackageManager.PERMISSION_GRANTED
    && ContextCompat.checkSelfPermission(baseContext, android.Manifest.permission.ACCESS_FINE_LOCATION)==PackageManager.PERMISSION_GRANTED


    private fun hasPermissionGranted(): Boolean{
        return  PERMISION_ENABLED.all {
            ContextCompat.checkSelfPermission(baseContext, it) == PackageManager.PERMISSION_GRANTED
        }

    }
}