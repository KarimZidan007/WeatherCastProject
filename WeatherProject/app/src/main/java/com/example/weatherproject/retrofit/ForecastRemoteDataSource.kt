


import com.example.labone.retrofit.Services
import com.example.weatherproject.model.pojos.Root
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
       suspend fun getForecastOverNetwork() : Root {
           return getApiService().getForecast(59.0,-0.1,"metric" ,apiKey)
       }
    companion object {
        fun getApiService() : Services = RetroFitHelper.service
    }
 }