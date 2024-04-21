package com.chayan.weather

import android.content.Context
import android.os.Bundle

import android.util.Log
import android.view.inputmethod.EditorInfo
import android.view.inputmethod.InputMethodManager
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity

import com.chayan.weather.databinding.ActivityMainBinding
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.sql.Timestamp
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding
    private val cities = arrayOf(
        "Mumbai", "Delhi", "Bangalore", "Hyderabad", "Ahmedabad", "Chennai", "Kolkata", "Surat",
        "Pune", "Jaipur", "Lucknow", "Kanpur", "Nagpur", "Indore", "Thane", "Bhopal", "Visakhapatnam",
        "Pimpri-Chinchwad", "Patna", "Vadodara", "Ghaziabad", "Ludhiana", "Agra", "Nashik", "Faridabad",
        "Meerut", "Rajkot", "Kalyan-Dombivli", "Vasai-Virar", "Varanasi", "Srinagar", "Aurangabad",
        "Dhanbad", "Amritsar", "Navi Mumbai", "Allahabad", "Howrah", "Ranchi", "Gwalior", "Jabalpur",
        "Coimbatore", "Vijayawada", "Jodhpur", "Madurai", "Raipur", "Kota", "Guwahati", "Chandigarh",
        "Solapur", "Hubli-Dharwad", "Bareilly", "Moradabad", "Mysore", "Gurgaon", "Aligarh", "Jalandhar",
        "Tiruchirappalli", "Bhubaneswar", "Salem", "Mira-Bhayandar", "Warangal", "Thiruvananthapuram",
        "Guntur", "Bhiwandi", "Saharanpur", "Gorakhpur", "Bikaner", "Amravati", "Noida", "Jamshedpur",
        "Bhilai", "Cuttack", "Firozabad", "Kochi", "Bhavnagar", "Dehradun", "Durgapur", "Asansol",
        "Nanded", "Kolhapur", "Ajmer", "Gulbarga", "Jamnagar", "Ujjain", "Loni", "Siliguri", "Jhansi",
        "Ulhasnagar", "Jammu", "Sangli-Miraj & Kupwad", "Mangalore", "Erode", "Belgaum", "Ambattur",
        "Tirunelveli", "Malegaon","Agartala"
    )



    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        getWeatherData("gwalior", "c7dec5bd3aeed8de41fc233ed3f736af")
        setupSearchCity()

    }

private fun setupSearchCity() {
    val adapter = ArrayAdapter(this, android.R.layout.simple_dropdown_item_1line, cities)
    binding.searchView.setAdapter(adapter)

    // Listen for when a suggestion is clicked
    binding.searchView.setOnItemClickListener { parent, _, position, _ ->
        val selectedCity = parent.adapter.getItem(position) as String
        if (selectedCity.isNotEmpty()) {
            getWeatherData(selectedCity, "c7dec5bd3aeed8de41fc233ed3f736af")
            binding.searchView.text = null
            hideKeyboard()
        }
    }

    // Listen for the search action on the soft keyboard
    binding.searchView.setOnEditorActionListener { v, actionId, _ ->
        if (actionId == EditorInfo.IME_ACTION_SEARCH) {
            val query = v.text.toString()
            if (query.isNotEmpty()) {
                getWeatherData(query, "c7dec5bd3aeed8de41fc233ed3f736af")
                v.text = null
                hideKeyboard()
            }
            true
        } else {
            false
        }
    }
}


     private fun hideKeyboard() {
        val inputMethodManager = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager

        val currentFocusedView = currentFocus
        currentFocusedView?.let {
            inputMethodManager.hideSoftInputFromWindow(it.windowToken, InputMethodManager.HIDE_NOT_ALWAYS)
        }
    }


     private  fun getWeatherData(city: String, apiKey: String) {
        RetrofitClient.instance.getWeatherData(city, apiKey, "metric").enqueue(object : Callback<weatherDataClass> {
            override fun onResponse(call: Call<weatherDataClass>, response: Response<weatherDataClass>) {
                if (response.isSuccessful) {
                    val weatherData = response.body()
                    val temp = weatherData?.main?.temp
                    val min_temp=weatherData?.main?.temp_min
                    val max_temp=weatherData?.main?.temp_max
                    val humi=weatherData?.main?.humidity
                    val sealevel=weatherData?.main?.sea_level
                    val sunsettime= weatherData?.sys?.sunset
                    val sunrisetime= weatherData?.sys?.sunrise
                    val windspeed = weatherData?.wind?.speed
                    val condition=weatherData?.weather?.firstOrNull()?.main?:"unknown"
                    val feellike= weatherData?.main?.feels_like

                    binding.weather.text=condition
                    binding.maxTemp.text=" Max Temp: $max_temp°C"
                    binding.minTemp.text="Min Temp: $min_temp°C"
                    binding.tempreture.text="$temp°C"
                    binding.sunset.text="${time(sunsettime!!.toLong())}"
                    binding.sunrise.text="${time(sunrisetime!!.toLong())}"
                    binding.sea.text= "$sealevel hPa"
                    binding.humidity.text="$humi %"
                    binding.cityname.text="$city"
                    binding.wind.text="$windspeed M/s"
                    binding.condition.text=condition
                    binding.day.text=dayName(System.currentTimeMillis())
                    binding.date.text=date()
                    binding.feel.text="feels like $feellike°C"

                    changeImage(condition)


                } else {
                    Log.e("MainActivity", "Error: ${response.message()}")
                    Toast.makeText(this@MainActivity, "Error fetching temperature", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onFailure(call: Call<weatherDataClass>, t: Throwable) {
                Log.e("MainActivity", "Failure: ${t.message}")
                Toast.makeText(this@MainActivity, "Failure: ${t.message}", Toast.LENGTH_SHORT).show()
            }
        })


        }

    private fun changeImage(condition: String) {
        when (condition) {
            "Light Rain","Drizzle","Moderate Rain","Showers","Heavy Rain" -> {
                binding.root.setBackgroundResource(R.drawable.rain) // Assuming the typo is corrected
                binding.lottieAnimationView.setAnimation(R.raw.rain)
            }
            "HAZE", "Clouds","Partly Clouds","Overcast","Mist","Foggy" -> {
                binding.root.setBackgroundResource(R.drawable.cloud_background) // Assuming the typo is corrected
                binding.lottieAnimationView.setAnimation(R.raw.cloud)
            }
            "Light Snow","Brizzard","Moderate Snow","Showers","Heavy Snow" -> {
                binding.root.setBackgroundResource(R.drawable.snow) // Assuming the typo is corrected
                binding.lottieAnimationView.setAnimation(R.raw.snow)
            }


        }
        binding.lottieAnimationView.playAnimation()
    }

    private fun date(): String {
        val sdf = SimpleDateFormat("dd MMMM yyyy", Locale.getDefault())
        return sdf.format(Date())

    }
    private fun time(timestamp: Long): String {
        val sdf = SimpleDateFormat("HH:mm", Locale.getDefault())
        return sdf.format(Date(timestamp*1000))

    }

    fun dayName(timestamp: Long): String {
        val sdf = SimpleDateFormat("EEEE", Locale.getDefault())
        return sdf.format(Date())

    }

}
