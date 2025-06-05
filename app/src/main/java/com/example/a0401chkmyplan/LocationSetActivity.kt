package com.example.a0401chkmyplan

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.example.a0401chkmyplan.databinding.ActivityLocationSetBinding
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions

class LocationSetActivity : AppCompatActivity(), OnMapReadyCallback {
    private lateinit var binding: ActivityLocationSetBinding
    private lateinit var map: GoogleMap

    private var selectedLongitude: Double = 0.0
    private var selectedLatitude: Double = 0.0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLocationSetBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // 저장된 위치가 있다면 인텐트로부터 받기
        selectedLatitude = intent.getDoubleExtra("latitude", 0.0)
        selectedLongitude = intent.getDoubleExtra("longitude", 0.0)

        val mapFragment = supportFragmentManager
            .findFragmentById(R.id.map_fragment) as SupportMapFragment
        mapFragment.getMapAsync(this)
    }

    override fun onMapReady(googleMap: GoogleMap) {
        map = googleMap

        val defaultLatLng = LatLng(37.5665, 126.9780) // 서울 기본값
        val initialLatLng = if (selectedLatitude != 0.0 && selectedLongitude != 0.0) {
            LatLng(selectedLatitude, selectedLongitude)
        } else {
            defaultLatLng
        }

        map.moveCamera(CameraUpdateFactory.newLatLngZoom(initialLatLng, 15f))

        // 초기 마커 표시 (수정모드일 경우)
        if (initialLatLng != defaultLatLng) {
            map.addMarker(MarkerOptions().position(initialLatLng).title("기존 저장된 위치"))
        }

        // 지도 클릭 시 마커 찍고 결과 반환
        map.setOnMapClickListener { latLng ->
            map.clear()
            map.addMarker(MarkerOptions().position(latLng).title("선택된 위치"))

            // 선택된 위도/경도 반환
            val intent = Intent().apply {
                putExtra("latitude", latLng.latitude)
                putExtra("longitude", latLng.longitude)
            }
            setResult(RESULT_OK, intent)
            finish()
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == 1001 && resultCode == Activity.RESULT_OK) {
            val lat = data?.getDoubleExtra("latitude", 0.0) ?: 0.0
            val lng = data?.getDoubleExtra("longitude", 0.0) ?: 0.0

            binding.LocCurLongtitudeLatitude.text = "위치: ($lat, $lng)"
            selectedLatitude = lat
            selectedLongitude = lng
        }
    }
}
