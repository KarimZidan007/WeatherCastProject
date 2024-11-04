package com.example.weatherproject.model.repository.local

import androidx.test.ext.junit.runners.AndroidJUnit4
import com.example.weatherproject.fakedatasrcs.FakeLocalDataSrc
import com.example.weatherproject.model.pojos.EventAlerts
import com.example.weatherproject.model.pojos.WeatherDb
import kotlinx.coroutines.test.runTest
import org.hamcrest.core.IsEqual
import org.junit.Assert.assertThat
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class LocalRepositoryTest {

    private lateinit var fakeLocalDataSrc: FakeLocalDataSrc
    private lateinit var localRepository: LocalRepository

    @Before
    fun setUp() {
        fakeLocalDataSrc = FakeLocalDataSrc()
        localRepository = LocalRepository(fakeLocalDataSrc)
    }

    @Test
    fun insertAlert_returnInsertedAlert(): Unit = runTest {
        // Given
        val alert = EventAlerts(1000,"testAlert",12.34,56.78,"testDate",true)

        // When
        localRepository.insertAlertDB(alert)
        val returnedAlert = fakeLocalDataSrc.alertsList[0]

        // Then
        assertThat(returnedAlert, IsEqual(alert))
    }

    @Test
    fun insertTwoAlerts_removeOneAlert_returnOneAlert(): Unit = runTest {
        // Given
        val alert1 =  EventAlerts(1000,"testAlert1",12.34,56.78,"testDate",true)
        val alert2 = EventAlerts(1000,"testAlert2",12.34,56.78,"testDate",true)

        // When
        localRepository.insertAlertDB(alert1)
        localRepository.insertAlertDB(alert2)
        localRepository.deleteAlertDB(alert1)

        // Then
        assertThat(fakeLocalDataSrc.alertsList.size, IsEqual(1))
        assertThat(fakeLocalDataSrc.alertsList[0], IsEqual(alert2))
    }

    @Test
    fun insertFavCity_returnInsertedFavCity(): Unit = runTest {
        // Given
        val city = WeatherDb()
        city.temp= 50.0

        // When
        localRepository.insertFavCityDetails(city)
        val returnedCity = fakeLocalDataSrc.forecastsList[0]

        // Then
        assertThat(returnedCity, IsEqual(city))
    }

}
