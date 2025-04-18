package com.example.violetaapp

import android.app.Activity
import android.os.Bundle
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.violetaapp.adapter.PlaceAdapter
import com.example.violetaapp.api.RetrofitClient
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import androidx.activity.ComponentActivity
import com.example.violetaapp.databinding.ActivityAllPlacesBinding
import com.example.violetaapp.databinding.ActivityGastronomyBinding

class GastronomyActivity : ComponentActivity() {

    private lateinit var adapter: PlaceAdapter
    private lateinit var userId: String
    private lateinit var recyclerView: RecyclerView
    private lateinit var binding: ActivityGastronomyBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityGastronomyBinding.inflate(layoutInflater)
        setContentView(binding.root)

        userId = FirebaseAuth.getInstance().currentUser?.uid ?: ""
        recyclerView = findViewById(R.id.recyclerPlaces)
        binding.buttonBack.setOnClickListener {
            onBackPressedDispatcher.onBackPressed()
        }
        setupRecycler()
        loadGastronomyPlaces()
    }

    private fun setupRecycler() {
        adapter = PlaceAdapter(emptyList(), userId)
        recyclerView.layoutManager = LinearLayoutManager(this)
        recyclerView.adapter = adapter
    }


    private fun loadGastronomyPlaces() {
        CoroutineScope(Dispatchers.Main).launch {
            try {
                val response = RetrofitClient.instance.getPlaces()

                val filtered = response.filter {
                    it.tipo.equals("Restaurante", ignoreCase = true) ||
                            it.tipo.equals("Bares e Pubs", ignoreCase = true)
                }
                val sorted = filtered.sortedBy { it.name }
                adapter = PlaceAdapter(sorted, userId)
                recyclerView.adapter = adapter
            } catch (e: Exception) {
                e.printStackTrace()
                Toast.makeText(this@GastronomyActivity, "Erro ao carregar locais", Toast.LENGTH_SHORT).show()
            }
        }
    }
}
