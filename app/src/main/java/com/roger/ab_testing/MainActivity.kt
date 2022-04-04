package com.roger.ab_testing

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import com.google.firebase.analytics.FirebaseAnalytics
import com.google.firebase.analytics.ktx.analytics
import com.google.firebase.analytics.ktx.logEvent
import com.google.firebase.installations.FirebaseInstallations
import com.google.firebase.ktx.Firebase
import com.google.firebase.remoteconfig.ktx.remoteConfig
import com.google.firebase.remoteconfig.ktx.remoteConfigSettings
import com.roger.ab_testing.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding
    private lateinit var firebaseAnalytics: FirebaseAnalytics

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        FirebaseInstallations.getInstance().getToken(/* forceRefresh */ true)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    Log.d("Installations", "Installation auth token: " + task.result?.token)
                } else {
                    Log.e("Installations", "Unable to get Installation auth token")
                }
            }
        binding.run {
            // Obtain the FirebaseAnalytics instance.
            firebaseAnalytics = Firebase.analytics
            firebaseAnalytics.setUserProperty("username", "test")
            val remoteConfig = Firebase.remoteConfig
            val configSettings = remoteConfigSettings {
                minimumFetchIntervalInSeconds = if (BuildConfig.DEBUG) 0 else 3600
            }
            remoteConfig.setConfigSettingsAsync(configSettings) // 設定遠端
            remoteConfig.setDefaultsAsync(R.xml.remote_config_default) // 設定本地預設值
            remoteConfig.fetchAndActivate() // 使⽤值
                .addOnSuccessListener {
                    if (remoteConfig.getBoolean("new_feature_enabled")){
                        btnNewFeature.setOnClickListener {
                            Toast.makeText(baseContext, "New Feature",
                                Toast.LENGTH_SHORT).show()
                        }
                    }
                }
        }
    }
}