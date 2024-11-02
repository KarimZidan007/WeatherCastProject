
import com.example.labone.database.DAO
import com.example.weatherproject.database.Weather.WeatherDAO
import com.example.weatherproject.database.forecast.ForecastDAO
import com.example.weatherproject.model.pojos.FavCity
import com.example.weatherproject.model.pojos.FullWeatherDetails
import com.example.weatherproject.model.pojos.WeatherFinal
import kotlinx.coroutines.flow.Flow

class LocalDataSrcImplementation(private var favLocalSrc: DAO ,private var weatherLocalSrc:WeatherDAO , private var forecastLocalSrc:ForecastDAO) {

     suspend fun getAllFavCities(): Flow<List<FavCity>>
    {
     return favLocalSrc.getAllFavCities()
    }
    suspend fun inserFavCity(product: FavCity)
    {
        favLocalSrc.insertFavCity(product)
    }
    suspend fun deleteFavCity(product: FavCity)
    {
        favLocalSrc.deleteFavCity(product)
    }

    suspend fun getFavWeather(lat:Double , long :Double): Flow<WeatherFinal>
    {
        return weatherLocalSrc.getFavCityWeather(lat,long)
    }
    suspend fun insertFavWeather(city: WeatherFinal):Long
    {
       return weatherLocalSrc.insertFavCityWeather(city)
    }

    suspend fun deleteFavWeather(city:WeatherFinal):Int
    {
       return weatherLocalSrc.deleteFavCityWeather(city)
    }

    suspend fun updateFavWeather(city:WeatherFinal)
    {
        weatherLocalSrc.updateFavCityWeather(city)
    }




    suspend fun getAllFavForecast():Flow<List<FullWeatherDetails>>
    {
        return forecastLocalSrc.getAllFavCityForecast()
    }
    suspend fun insertFavForecast(city: FullWeatherDetails):Long
    {
        return forecastLocalSrc.insertFavFullCityForecast(city)
    }

    suspend fun deleteFavForecast(city: FullWeatherDetails):Int
    {
        return forecastLocalSrc.deleteFavFullCityForecast(city)
    }

    suspend fun updateFavForecast(city:FullWeatherDetails)
    {
        forecastLocalSrc.updateFavFullCityForecast(city)
    }

}