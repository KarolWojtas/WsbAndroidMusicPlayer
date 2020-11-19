package com.example.musicplayer

import android.Manifest
import android.content.pm.PackageManager
import android.os.Bundle
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts.RequestPermission
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProviders
import androidx.lifecycle.get
import com.example.musicplayer.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding
    private lateinit var viewModel: MainActivityViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        viewModel = ViewModelProviders.of(this).get()
        binding = DataBindingUtil.setContentView(this, R.layout.activity_main)
        binding.lifecycleOwner = this

        if(viewModel.audioDataListIsEmpty){
            loadDataFromAssets()
            if (ContextCompat.checkSelfPermission(
                            this, Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED ) {
                loadDataFromMediaStore()
            } else {
                val requestPermission = registerForActivityResult(RequestPermission()){ isGranted: Boolean ->
                        if(isGranted){
                            loadDataFromMediaStore()
                        } else {
                            Toast.makeText(this,
                                    "Nie można załadować muzyki z pamięci telefonu, z powodu braku pozwoleń",
                                    Toast.LENGTH_SHORT)
                                    .show()
                        }
                    }
                requestPermission.launch(
                        Manifest.permission.READ_EXTERNAL_STORAGE)
            }
        }
    }

    private fun loadDataFromAssets(){
        viewModel.loadAssetAudioData(assets)
    }

    private fun loadDataFromMediaStore(){
        viewModel.loadMediaStoreAudioData(contentResolver)
    }

}