package com.example.violetaapp


import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.violetaapp.adapter.PlaceAdapter
import com.example.violetaapp.api.RetrofitClient
import com.example.violetaapp.databinding.ActivityAllPlacesBinding
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class AllPlacesActivity : ComponentActivity() {

    private lateinit var adapter: PlaceAdapter
    private lateinit var userId: String
    private lateinit var recyclerView: RecyclerView
    private lateinit var binding: ActivityAllPlacesBinding


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAllPlacesBinding.inflate(layoutInflater)
        setContentView(binding.root)

        userId = FirebaseAuth.getInstance().currentUser?.uid ?: ""
        recyclerView = findViewById(R.id.recyclerPlaces)
        binding.buttonBack.setOnClickListener {
                onBackPressedDispatcher.onBackPressed()
        }
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
                val sorted = response.sortedBy { it.name }
                adapter = PlaceAdapter(sorted, userId)
                recyclerView.adapter = adapter
            } catch (e: Exception) {
                e.printStackTrace()
                Toast.makeText(this@AllPlacesActivity, "Erro ao carregar locais", Toast.LENGTH_SHORT).show()
            }
        }
    }
    override fun onResume() {
        super.onResume()
        loadAllPlaces() // Carrega novamente os lugares e atualiza o estado dos favoritos
    }
}
