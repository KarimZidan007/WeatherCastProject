


import android.location.Location
import com.example.labone.retrofit.Services
import com.example.weatherproject.model.WeatherResponse
import com.example.weatherproject.model.pojos.Root
import com.example.weatherproject.model.pojos.Weather
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

   class ForecastRemoteDataSource  {
        val apiKey = "ac127a1cc099191e960a04cfb5f19889"

       object RetroFitHelper{
         const val BASE_URL: String =  "https://api.openweathermap.org/data/2.5/"
         var retrofit = Retrofit.Builder()
         .baseUrl(BASE_URL)
         .addConverterFactory(GsonConverterFactory.create())
         .build()
         val service :Services by lazy {
             retrofit.create(Services::class.java)
         }
     }
       suspend fun getForecastOverNetwork(location: Location,languague:String) : Root {
           return getApiService().getForecast(location.latitude,location.longitude,"metric" ,apiKey,languague)
       }
       suspend fun getCurrentWeatherOverNetwork(location: Location,languague:String) : WeatherResponse {
           return getApiService().getWeather(location.latitude,location.longitude,"metric" ,apiKey,languague)
       }
    companion object {
        fun getApiService() : Services = RetroFitHelper.service
    }
 }