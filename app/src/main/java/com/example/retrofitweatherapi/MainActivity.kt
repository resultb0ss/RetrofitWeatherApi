package com.example.retrofitweatherapi

import android.annotation.SuppressLint
import android.os.Bundle
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity

import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.retrofitweatherapi.databinding.ActivityMainBinding
import com.example.retrofitweatherapi.utils.RetrofitInstance
import kotlinx.coroutines.DelicateCoroutinesApi
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import retrofit2.HttpException
import retrofit2.Retrofit
import java.io.IOException

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMainBinding.inflate(layoutInflater)

        setContentView(binding.root)

        getCurrentWeather()

    }

    @SuppressLint("SetTextI18n")
    @OptIn(DelicateCoroutinesApi::class)
    private fun getCurrentWeather() {
        GlobalScope.launch(Dispatchers.IO) {
            val response = try {
                RetrofitInstance.api.getCurrentWeather(
                    "Moscow","metric",
                    applicationContext.getString(R.string.api_key)
                )

            } catch (e:IOException){
                Toast.makeText(applicationContext,"app error ${e.message}",
                    Toast.LENGTH_SHORT).show()
                return@launch
            } catch (e: HttpException) {
                Toast.makeText(applicationContext, "http error ${e.message}",
                    Toast.LENGTH_SHORT).show()
                return@launch
            }
            if (response.isSuccessful && response.body() != null) {
                withContext(Dispatchers.Main) {
                    val data = response.body()
                    binding.mainActivityCityNameTV.text = data!!.name
                    binding.mainActivityTempTV.text = "${data.main.temp} C\u00B0"
                    binding.mainActivityWindDegreeTV.text = data.wind.deg.toString()
                    binding.mainActivityWindSpeedTV.text = data.wind.speed.toString()
                    val iconId = data.weather[0].icon
                    val imageUrl = "https://openweather.org/img/wn/$iconId@4x.png"
                    
//                    Picasso.get().load(imageUrl).into(binding.mainActivityImageViewIV)
                    val convertPressure = (data.main.pressure / 1.33).toInt()
                    binding.mainActivityBarTV.text = convertPressure.toString()

                }
            }

        }

    }
}