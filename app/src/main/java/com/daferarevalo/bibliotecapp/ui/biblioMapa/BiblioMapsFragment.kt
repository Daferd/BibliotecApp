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
import androidx.navigation.fragment.navArgs
import com.daferarevalo.bibliotecapp.R
import com.daferarevalo.bibliotecapp.server.BibliotecaServer
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import com.google.android.gms.maps.model.PointOfInterest
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

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

        googleMap.setOnPoiClickListener(this)

        googleMap.uiSettings.isZoomControlsEnabled = true

        setUpmap(googleMap)

        val args: BiblioMapsFragmentArgs by navArgs()
        val detalleBiblioteca = args.BibliotecaSeleccionada

        ubicarBibliotecaSeleccionada(detalleBiblioteca, googleMap)

        //CargarMarkersDesdeFirebase(googleMap)
    }


    private fun CargarMarkersDesdeFirebase(googleMap: GoogleMap) {

        val database = FirebaseDatabase.getInstance()
        val myBibliotecasRef = database.getReference("bibliotecas")

        val postListener = object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                for (dato: DataSnapshot in snapshot.children) {
                    val bibliotecaServer = dato.getValue(BibliotecaServer::class.java)
                    bibliotecaServer?.let {
                        val posicionBiblio =
                            LatLng(bibliotecaServer.latitud, bibliotecaServer.longitud)
                        googleMap.addMarker(
                            MarkerOptions()
                                .position(posicionBiblio)
                                .title(bibliotecaServer.titulo)
                                .snippet(bibliotecaServer.direccion)
                        )
                        googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(posicionBiblio, 16f))
                    }
                }

            }

            override fun onCancelled(error: DatabaseError) {
            }
        }
        myBibliotecasRef.addValueEventListener(postListener)

    }

    private fun ubicarBibliotecaSeleccionada(
        detalleBiblioteca: BibliotecaServer,
        googleMap: GoogleMap
    ) {
        val posicionBiblio =
            LatLng(detalleBiblioteca.latitud, detalleBiblioteca.longitud)
        googleMap.addMarker(
            MarkerOptions()
                .position(posicionBiblio)
                .title(detalleBiblioteca.titulo)
                .snippet(detalleBiblioteca.direccion)
        )
        googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(posicionBiblio, 16f))
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

    override fun onPoiClick(poi: PointOfInterest?) {
        Toast.makeText(
            context,
            "nombre: ${poi?.name}, latitud ${poi?.latLng?.latitude},longitud ${poi?.latLng?.longitude}",
            Toast.LENGTH_LONG
        ).show()
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

}