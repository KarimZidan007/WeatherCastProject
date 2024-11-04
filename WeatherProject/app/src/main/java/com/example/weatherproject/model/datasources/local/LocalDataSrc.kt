
import com.example.weatherproject.database.event.EventDao
import com.example.weatherproject.database.forecast.ForecastDAO
import com.example.weatherproject.model.datasources.local.ILocalDataSource
import com.example.weatherproject.model.pojos.EventAlerts
import com.example.weatherproject.model.pojos.WeatherDb
import kotlinx.coroutines.flow.Flow

class LocalDataSrcImplementation(private var alertsDAO: EventDao?, private var forecastLocalSrc: ForecastDAO?):
    ILocalDataSource {

     override suspend fun getAllAlerts(): Flow<List<EventAlerts>>?
    {
        return alertsDAO?.getAllEvents()
    }
    override suspend fun insertAlert(alert: EventAlerts)
    {
        alertsDAO?.insert(alert)
    }
    override suspend fun deleteAlert(alert: EventAlerts)
    {
        alertsDAO?.delete(alert)
    }


    override suspend fun getAllFavForecast():Flow<List<WeatherDb>>?
    {
        return forecastLocalSrc?.getAllFavCityForecast()
    }
    override suspend fun insertFavForecast(city: WeatherDb):Long?
    {
        return forecastLocalSrc?.insertFavFullCityForecast(city)
    }

    override suspend fun deleteFavForecast(lat:Double, long:Double):Int?
    {
        return forecastLocalSrc?.deleteFavFullCityForecast(lat,long)
    }

    override suspend fun updateFavForecast(city:WeatherDb)
    {
        forecastLocalSrc?.updateFavFullCityForecast(city)
    }

}