package com.example.violetaapp

import android.app.Activity
import android.os.Bundle
import com.example.violetaapp.databinding.ActivityCommunityBinding

class CommunityActivity : Activity() {

    private lateinit var binding: ActivityCommunityBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityCommunityBinding.inflate(layoutInflater)
        setContentView(binding.root)
        val user = "${getString(R.string.user)} ${intent.getStringExtra("USER")}"

    }
}