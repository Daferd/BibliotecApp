package com.daferarevalo.bibliotecapp.ui.biblioMapa

import android.Manifest
import android.content.pm.PackageManager
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.fragment.app.Fragment
import com.daferarevalo.bibliotecapp.R
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import com.google.android.gms.maps.model.PointOfInterest

class BiblioMapsFragment : Fragment(), GoogleMap.OnPoiClickListener {

    private val callback = OnMapReadyCallback { googleMap ->
        /**
         * Manipulates the map once available.
         * This callback is triggered when the map is ready to be used.
         * This is where we can add markers or lines, add listeners or move the camera.
         * In this case, we just add a marker near Sydney, Australia.
         * If Google Play services is not installed on the device, the user will be prompted to
         * install it inside the SupportMapFragment. This method will only be triggered once the
         * user has installed Google Play services and returned to the app.
         */

        setUpmap(googleMap)

        googleMap.setOnPoiClickListener(this)

        googleMap.uiSettings.isZoomControlsEnabled = true

        val samaniego = LatLng(1.336447, -77.592786)
        googleMap.addMarker(
            MarkerOptions().position(samaniego)
                .title("Institucion Educativa Policarpa Salavarrieta").snippet("Biblioteca")
        )
        googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(samaniego, 16f))

        val simonBolivar = LatLng(1.336903, -77.592024)
        googleMap.addMarker(
            MarkerOptions().position(simonBolivar).title("Institución Educativa Simón Bolivar")
                .snippet("Biblioteca")
        )
        //googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(samaniego,16f))

        val cocuyos = LatLng(1.337990, -77.593205)
        googleMap.addMarker(
            MarkerOptions().position(cocuyos).title("Biblioteca Publica Cocuyos")
                .snippet("Biblioteca")
        )
    }

    private fun setUpmap(googleMap: GoogleMap?) {
        if (ActivityCompat.checkSelfPermission(
                requireContext(),
                Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
                requireContext(),
                Manifest.permission.ACCESS_COARSE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return
        }
        googleMap?.isMyLocationEnabled = true
    }


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_biblio_maps, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val mapFragment = childFragmentManager.findFragmentById(R.id.map) as SupportMapFragment?
        mapFragment?.getMapAsync(callback)
    }

    override fun onPoiClick(poi: PointOfInterest?) {
        Toast.makeText(
            context,
            "nombre: ${poi?.name}, latitud ${poi?.latLng?.latitude},longitud ${poi?.latLng?.longitude}",
            Toast.LENGTH_LONG
        ).show()
    }
}