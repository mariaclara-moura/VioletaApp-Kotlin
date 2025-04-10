package com.example.violetaapp.adapter

import android.content.Intent
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import com.example.violetaapp.PlaceDetailActivity
import com.example.violetaapp.R
import com.example.violetaapp.data.Place
import com.example.violetaapp.databinding.ItemPlaceBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore

class PlaceAdapter(
    private val places: List<Place>,
    private val userId: String,
    private val isFavoritesPage: Boolean = false // ✅ agora incluído corretamente
) : RecyclerView.Adapter<PlaceAdapter.PlaceViewHolder>() {

    inner class PlaceViewHolder(val binding: ItemPlaceBinding) : RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PlaceViewHolder {
        val binding = ItemPlaceBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return PlaceViewHolder(binding)
    }

    override fun onBindViewHolder(holder: PlaceViewHolder, position: Int) {
        val place = places[position]
        val context = holder.itemView.context
        val db = FirebaseFirestore.getInstance()

        holder.binding.placeName.text = place.name
        holder.binding.placeRating.text = "⭐ ${place.rating}"
        holder.binding.placeAddress.text = place.address

        // Inicializar o estado do favorito
        val docId = "${userId}_${place.id}"
        val favoriteRef = db.collection("favorites").document(docId)

        // Verificar se o local já está favoritado
        favoriteRef.get().addOnSuccessListener { document ->
            if (document.exists()) {
                // Já é favorito → coração preenchido
                holder.binding.btnFavorite.setImageResource(R.drawable.baseline_favorite_24)
            } else {
                // Não é favorito → coração não preenchido
                holder.binding.btnFavorite.setImageResource(R.drawable.baseline_favorite_24_not_filled_24)
            }
        }

        // Abrir detalhes
        holder.itemView.setOnClickListener {
            val intent = Intent(context, PlaceDetailActivity::class.java).apply {
                putExtra("name", place.name)
                putExtra("id", place.id)
                putExtra("tipo", place.tipo)
                putExtra("address", place.address)
                putExtra("rating", place.rating)

                putExtra("sunday", place.sunday)
                putExtra("monday", place.monday)
                putExtra("tuesday", place.tuesday)
                putExtra("wednesday", place.wednesday)
                putExtra("thursday", place.thursday)
                putExtra("friday", place.friday)
                putExtra("saturday", place.saturday)
            }
            context.startActivity(intent)
        }

        // Favoritar / remover favorito
        holder.binding.btnFavorite.setOnClickListener {
            val placeId = place.id
            if (placeId.isBlank()) {
                Toast.makeText(context, "ID do local ausente. Ação cancelada.", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            val docId = "${userId}_$placeId"
            val favoritesRef = db.collection("favorites").document(docId)

            favoritesRef.get().addOnSuccessListener { document ->
                if (document.exists()) {
                    // Já favoritado → remover
                    favoritesRef.delete().addOnSuccessListener {
                        holder.binding.btnFavorite.setImageResource(R.drawable.baseline_favorite_24_not_filled_24) // Coração não preenchido
                        Toast.makeText(context, "Removido dos favoritos", Toast.LENGTH_SHORT).show()
                    }
                } else {
                    // Ainda não favoritado → adicionar
                    val currentUser = FirebaseAuth.getInstance().currentUser
                    val userName = currentUser?.displayName ?: "Usuário"

                    val data = hashMapOf(
                        "userId" to userId,
                        "userName" to userName,
                        "placeId" to placeId,
                        "placeName" to place.name,
                        "tipo" to place.tipo,
                        "address" to place.address,
                        "rating" to place.rating,
                        "sunday" to place.sunday,
                        "monday" to place.monday,
                        "tuesday" to place.tuesday,
                        "wednesday" to place.wednesday,
                        "thursday" to place.thursday,
                        "friday" to place.friday,
                        "saturday" to place.saturday
                    )

                    favoritesRef.set(data).addOnSuccessListener {
                        holder.binding.btnFavorite.setImageResource(R.drawable.baseline_favorite_24) // Coração preenchido
                        Toast.makeText(context, "Adicionado aos favoritos", Toast.LENGTH_SHORT).show()
                    }
                }
            }
        }


        // Comentário fixo (pode ser substituído depois por um diálogo)
//        holder.binding.btnComment.setOnClickListener {
//            val comment = "Lugar top!"
//            val data = hashMapOf(
//                "userId" to userId,
//                "placeId" to place.id,
//                "placeName" to place.name,
//                "comment" to comment,
//                "timestamp" to FieldValue.serverTimestamp()
//            )
//            db.collection("comments").add(data)
//                .addOnSuccessListener {
//                    Toast.makeText(context, "Comentário enviado!", Toast.LENGTH_SHORT).show()
//                }
//        }
    }

    override fun getItemCount(): Int = places.size
}
