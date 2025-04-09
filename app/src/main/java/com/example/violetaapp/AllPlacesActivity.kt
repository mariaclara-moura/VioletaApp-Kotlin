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

class AllPlacesActivity : Activity() {

    private lateinit var adapter: PlaceAdapter
    private lateinit var userId: String
    private lateinit var recyclerView: RecyclerView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_all_places)

        userId = FirebaseAuth.getInstance().currentUser?.uid ?: ""
        recyclerView = findViewById(R.id.recyclerPlaces)

        setupRecycler()
        loadAllPlaces()
    }

    private fun setupRecycler() {
        adapter = PlaceAdapter(emptyList(), userId)
        recyclerView.layoutManager = LinearLayoutManager(this)
        recyclerView.adapter = adapter
    }

    private fun loadAllPlaces() {
        CoroutineScope(Dispatchers.Main).launch {
            try {
                val response = RetrofitClient.instance.getPlaces()
                val filtered = response.filterNot {
                    it.tipo.equals("Restaurante", ignoreCase = true) ||
                            it.tipo.equals("Bares e Pubs", ignoreCase = true)
                }
                adapter = PlaceAdapter(filtered, userId)
                recyclerView.adapter = adapter
            } catch (e: Exception) {
                e.printStackTrace()
                Toast.makeText(this@AllPlacesActivity, "Erro ao carregar locais", Toast.LENGTH_SHORT).show()
            }
        }
    }
}
