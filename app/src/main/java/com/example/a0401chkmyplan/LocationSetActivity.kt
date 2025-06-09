package com.example.a0401chkmyplan

import android.app.Activity
import android.content.Intent
import android.location.Geocoder
import android.os.Bundle
import android.view.Gravity
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import com.example.a0401chkmyplan.databinding.ActivityLocationSetBinding
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import com.google.android.material.snackbar.Snackbar
import java.util.Locale

class LocationSetActivity : AppCompatActivity(), OnMapReadyCallback {
    private lateinit var binding: ActivityLocationSetBinding
    private lateinit var map: GoogleMap

    private var selectedLatitude: Double = 0.0
    private var selectedLongitude: Double = 0.0
    private lateinit var selectedLatLng: LatLng

    companion object {
        const val LOCATION_REQUEST_CODE = 1001
        const val AUTOCOMPLETE_REQUEST_CODE = 1002
    }



    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLocationSetBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // 저장된 위치가 있다면 인텐트로부터 가져오기
        selectedLatitude = intent.getDoubleExtra("latitude", 0.0)
        selectedLongitude = intent.getDoubleExtra("longitude", 0.0)

        val mapFragment = supportFragmentManager
            .findFragmentById(R.id.map_fragment) as SupportMapFragment
        mapFragment.getMapAsync(this)

        Toast.makeText(this, "지도를 클릭해서 위치를 지정하세요!", Toast.LENGTH_SHORT).show()

        // 저장 버튼 클릭 시 결과 전달
        binding.btnSaveLocation.setOnClickListener {
            if (this::selectedLatLng.isInitialized) {
                val resultIntent = Intent().apply {
                    putExtra("latitude", selectedLatLng.latitude)
                    putExtra("longitude", selectedLatLng.longitude)
                }
                setResult(Activity.RESULT_OK, resultIntent)
                finish()
            }
        }
        binding.btnBack.setOnClickListener {
            finish()
        }
    }

    override fun onMapReady(googleMap: GoogleMap) {
        map = googleMap

        // 초기 위치 설정
        selectedLatLng = if (selectedLatitude != 0.0 && selectedLongitude != 0.0) {
            LatLng(selectedLatitude, selectedLongitude)
        } else {
            LatLng(37.5665, 126.9780) // 서울 기본값
        }

        // 지도 이동 및 마커 표시
        map.moveCamera(CameraUpdateFactory.newLatLngZoom(selectedLatLng, 15f))
        map.addMarker(MarkerOptions().position(selectedLatLng).title("선택된 위치"))

        // 주소 표시
        val addressStr = getAddressFromLocation(selectedLatLng.latitude, selectedLatLng.longitude)
        binding.tvSelectedAddress.text = addressStr

        // 지도 클릭 시 마커 갱신 및 주소 표시
        map.setOnMapClickListener { latLng ->
            map.clear()
            map.addMarker(MarkerOptions().position(latLng).title("선택된 위치"))
            selectedLatLng = latLng

            val clickedAddress = getAddressFromLocation(latLng.latitude, latLng.longitude)
            binding.tvSelectedAddress.text = clickedAddress
        }
    }

    private fun getAddressFromLocation(latitude: Double, longitude: Double): String {
        val geocoder = Geocoder(this, Locale.getDefault())
        return try {
            val addresses = geocoder.getFromLocation(latitude, longitude, 1)
            if (!addresses.isNullOrEmpty()) {
                addresses[0].getAddressLine(0) ?: "주소 정보 없음"
            } else {
                "주소를 찾을 수 없음"
            }
        } catch (e: Exception) {
            e.printStackTrace()
            "주소 변환 실패"
        }
    }
}
