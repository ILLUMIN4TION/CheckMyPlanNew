package com.example.a0401chkmyplan

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.a0401chkmyplan.databinding.ActivityLocationSetBinding
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions

class LocationSetActivity : AppCompatActivity(), OnMapReadyCallback {
    lateinit var binding: ActivityLocationSetBinding

    private lateinit var map: GoogleMap

    private var selectedLongitude: Double = 0.0
    private var selectedLatitude: Double = 0.0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_location_set)

        val mapFragment = supportFragmentManager
            .findFragmentById(R.id.map_fragment) as SupportMapFragment
        mapFragment.getMapAsync(this)
    }

    override fun onMapReady(googleMap: GoogleMap) {
        map = googleMap

        // 기본 위치 예: 서울
        val seoul = LatLng(37.5665, 126.9780)
        map.moveCamera(CameraUpdateFactory.newLatLngZoom(seoul, 15f))

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
            finish()  // 위치 선택 후 종료
        }
    }
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)


        if (requestCode == 1001 && resultCode == Activity.RESULT_OK) {
            val lat = data?.getDoubleExtra("latitude", 0.0) ?: 0.0
            val lng = data?.getDoubleExtra("longitude", 0.0) ?: 0.0

            // 예: UI에 표시
            binding.LocCurLongtitudeLatitude.text = "위치: ($lat, $lng)"

            // 저장용 변수에도 저장
            selectedLatitude = lat
            selectedLongitude = lng
        }
    }
}