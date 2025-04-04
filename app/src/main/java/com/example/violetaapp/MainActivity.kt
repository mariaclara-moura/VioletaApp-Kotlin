package com.example.violetaapp

import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.result.contract.ActivityResultContracts
import androidx.lifecycle.lifecycleScope
import com.example.violetaapp.databinding.ActivityMainBinding
import android.content.Intent


class MainActivity : ComponentActivity() {

    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val launcher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { _ ->
            GoogleSignInUtils.doGoogleSignIn(
                context = this,
                scope = lifecycleScope,
                launcher = null,
                login = {
                }
            )
        }

        binding.loginButton.setOnClickListener {
            GoogleSignInUtils.doGoogleSignIn(
                context = this,
                scope = lifecycleScope,
                launcher = launcher,
                login = { accountName ->
                    val name = accountName ?: "Usuário desconhecido"
                    Toast.makeText(this, "Bem-vindo(a), $name!", Toast.LENGTH_SHORT).show()
                    val intent = Intent(this, CommunityActivity:: class.java)
                    intent.putExtra("USER", name)
                    startActivity(intent)
                    finish()
                }
            )
        }
    }
}
