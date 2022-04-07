package com.roger.ab_testing

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import com.google.android.gms.tasks.OnCompleteListener
import com.google.firebase.analytics.FirebaseAnalytics
import com.google.firebase.analytics.ktx.analytics
import com.google.firebase.analytics.ktx.logEvent
import com.google.firebase.installations.FirebaseInstallations
import com.google.firebase.ktx.Firebase
import com.google.firebase.messaging.FirebaseMessaging
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
        FirebaseMessaging.getInstance().token.addOnCompleteListener(OnCompleteListener { task ->
            if (!task.isSuccessful) {
                Log.w("TAG", "Fetching FCM registration token failed", task.exception)
                return@OnCompleteListener
            }

            val token = task.result
            Log.d("TAG", "FCM token: $token")
        })
        binding.run {
            firebaseAnalytics = Firebase.analytics

            val remoteConfig = Firebase.remoteConfig
            val configSettings = remoteConfigSettings {
                minimumFetchIntervalInSeconds = if (BuildConfig.DEBUG) 0 else 3600
            }
            remoteConfig.setConfigSettingsAsync(configSettings)
            remoteConfig.setDefaultsAsync(R.xml.remote_config_default)
            remoteConfig.fetchAndActivate()
                .addOnSuccessListener {
                    when (remoteConfig.getString("sign_up_type")) {
                        "facebook" -> {
                            clFacebook.visibility = View.VISIBLE
                            clGoogle.visibility = View.GONE
                        }
                        "google" -> {
                            clFacebook.visibility = View.GONE
                            clGoogle.visibility = View.VISIBLE
                        }
                    }
                }

            clFacebook.setOnClickListener {
                firebaseAnalytics.logEvent("sign_up_btn") {
                    param("sign_up", "click")
                }
            }
            clGoogle.setOnClickListener {
                firebaseAnalytics.logEvent("sign_up_btn") {
                    param("sign_up", "click")
                }
            }
        }
    }
}