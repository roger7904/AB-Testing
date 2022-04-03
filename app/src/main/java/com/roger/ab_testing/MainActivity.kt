package com.roger.ab_testing

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import com.google.firebase.analytics.FirebaseAnalytics
import com.google.firebase.analytics.ktx.analytics
import com.google.firebase.analytics.ktx.logEvent
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

        binding.run {
            // Obtain the FirebaseAnalytics instance.
            firebaseAnalytics = Firebase.analytics

            val remoteConfig = Firebase.remoteConfig
            val configSettings = remoteConfigSettings {
                minimumFetchIntervalInSeconds = if (BuildConfig.DEBUG) 0 else 3600
            }
            remoteConfig.setConfigSettingsAsync(configSettings) // 設定遠端
            remoteConfig.setDefaultsAsync(R.xml.remote_config_default) // 設定本地預設值
            remoteConfig.fetchAndActivate() // 使⽤值
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
                    param("sign_up","click")
                }
            }
            clGoogle.setOnClickListener {
                firebaseAnalytics.logEvent("sign_up_btn") {
                    param("sign_up","click")
                }
            }


//            btnSendEvent.setOnClickListener {
//                Log.d("TAG", "onCreate: ")
//                firebaseAnalytics.logEvent(FirebaseAnalytics.Event.LOGIN) {
//                    param(FirebaseAnalytics.Param.METHOD, "test")
//                }
//                firebaseAnalytics.logEvent("test_event_name") {
//                    param("event_category","event_action")
//                }
//                firebaseAnalytics.logEvent("sign_up_btn") {
//                    param("sign_up","click")
//                }
//            }
        }
    }
}