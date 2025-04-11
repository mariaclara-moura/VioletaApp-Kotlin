package com.example.violetaapp

import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.violetaapp.adapter.PlaceAdapter
import com.example.violetaapp.api.RetrofitClient
import com.example.violetaapp.databinding.ActivityOtherPlacesBinding
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class OtherPlacesActivity : ComponentActivity() {

    private lateinit var adapter: PlaceAdapter
    private lateinit var userId: String
    private lateinit var recyclerView: RecyclerView
    private lateinit var binding: ActivityOtherPlacesBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityOtherPlacesBinding.inflate(layoutInflater)
        setContentView(binding.root)
        userId = FirebaseAuth.getInstance().currentUser?.uid ?: ""
        recyclerView = findViewById(R.id.recyclerPlaces)
        binding.buttonBack.setOnClickListener {
            onBackPressedDispatcher.onBackPressed()
        }
        setupRecycler()
        loadOtherPlaces()
    }

    private fun setupRecycler() {
        adapter = PlaceAdapter(emptyList(), userId)
        recyclerView.layoutManager = LinearLayoutManager(this)
        recyclerView.adapter = adapter
    }

    private fun loadOtherPlaces() {
        CoroutineScope(Dispatchers.Main).launch {
            try {
                val response = RetrofitClient.instance.getPlaces()

                val filtered = response.filterNot {
                    it.tipo.equals("Restaurante", ignoreCase = true) ||
                            it.tipo.equals("Bares e Pubs", ignoreCase = true)
                }

                val sorted = filtered.sortedBy { it.name }
                adapter = PlaceAdapter(sorted, userId)
                recyclerView.adapter = adapter
            } catch (e: Exception) {
                e.printStackTrace()
                Toast.makeText(this@OtherPlacesActivity, "Erro ao carregar locais", Toast.LENGTH_SHORT).show()
            }
        }
    }
}
