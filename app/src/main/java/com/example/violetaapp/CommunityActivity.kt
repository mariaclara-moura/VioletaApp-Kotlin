package com.example.violetaapp

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.violetaapp.adapter.PlaceAdapter
import com.example.violetaapp.data.Place
import com.example.violetaapp.databinding.ActivityCommunityBinding
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.auth
import com.google.firebase.firestore.FirebaseFirestore

class CommunityActivity : Activity() {

    private lateinit var binding: ActivityCommunityBinding
    private lateinit var adapter: PlaceAdapter
    private lateinit var userId: String
    private val db = FirebaseFirestore.getInstance()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityCommunityBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val userName = intent.getStringExtra("USER") ?: "Usuário"
        binding.welcomeText.text = "Olá, $userName!"

        userId = FirebaseAuth.getInstance().currentUser?.uid ?: ""

        setupRecycler()
        loadUserFavorites()

        binding.btnGastronomia.setOnClickListener {
            startActivity(Intent(this, GastronomyActivity::class.java))
        }

        binding.btnTodosLocais.setOnClickListener {
            startActivity(Intent(this, AllPlacesActivity::class.java))
        }
        binding.btnOutrosLocais.setOnClickListener {
            startActivity(Intent(this, OtherPlacesActivity::class.java))
        }
        binding.btnLogout.setOnClickListener {
            Firebase.auth.signOut()
            val intent = Intent(this, MainActivity::class.java) // substitua pelo nome correto da tela de login
            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            startActivity(intent)
        }

    }

    private fun setupRecycler() {
        adapter = PlaceAdapter(emptyList(), userId, isFavoritesPage = true)
        binding.recyclerPlaces.layoutManager = LinearLayoutManager(this)
        binding.recyclerPlaces.adapter = adapter
    }

    private fun loadUserFavorites() {
        db.collection("favorites")
            .whereEqualTo("userId", userId)
            .addSnapshotListener { snapshot, error ->
                if (error != null || snapshot == null) {
                    Toast.makeText(this, "Erro ao carregar favoritos", Toast.LENGTH_SHORT).show()
                    return@addSnapshotListener
                }

                val favoritePlaces = snapshot.documents.mapNotNull { doc ->
                    Place(
                        id = doc.getString("placeId") ?: "",
                        name = doc.getString("placeName") ?: "",
                        tipo = doc.getString("tipo") ?: "",
                        address = doc.getString("address") ?: "",
                        rating = doc.getDouble("rating") ?: 0.0,
                        sunday = doc.getString("sunday") ?: "",
                        monday = doc.getString("monday") ?: "",
                        tuesday = doc.getString("tuesday") ?: "",
                        wednesday = doc.getString("wednesday") ?: "",
                        thursday = doc.getString("thursday") ?: "",
                        friday = doc.getString("friday") ?: "",
                        saturday = doc.getString("saturday") ?: "",
                        latitude = 0.0,
                        longitude = 0.0
                    )
                }

                adapter = PlaceAdapter(favoritePlaces, userId, isFavoritesPage = true)
                binding.recyclerPlaces.adapter = adapter
            }
    }
}
