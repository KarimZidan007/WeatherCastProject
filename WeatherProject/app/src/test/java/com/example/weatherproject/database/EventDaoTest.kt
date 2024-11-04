package com.example.weatherproject.database

import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import com.example.weatherproject.database.event.EventDao
import com.example.weatherproject.model.pojos.EventAlerts
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner


@RunWith(RobolectricTestRunner::class)
class EventDaoTest {
    private lateinit var eventDao: EventDao
    private lateinit var database: AppDatabase

    @Before
    fun setup() {
        database = Room.inMemoryDatabaseBuilder(
            ApplicationProvider.getApplicationContext(),
            AppDatabase::class.java
        ).build()
        eventDao = database.eventDao()
    }

    @Test
    fun insertEvent() = runBlocking {
        val event = EventAlerts(
            eventTime = System.currentTimeMillis(),
            title = "Test Event",
            lat = 37.7749,
            lng = -122.4194,
            date = "2024-01-01",
            alarm = true
        )
        eventDao.insert(event)

        val events = eventDao.getAllEvents().first()
        assertEquals(1, events.size)
        assertEquals(event.eventTime, events[0].eventTime)
        assertEquals(event.title, events[0].title)
        assertEquals(event.lat, events[0].lat, 0.001)
        assertEquals(event.lng, events[0].lng, 0.001)
        assertEquals(event.date, events[0].date)
        assertEquals(event.alarm, events[0].alarm)
    }

    @Test
    fun deleteEvent() = runBlocking {
        // Create an instance of EventAlerts
        val event = EventAlerts(
            eventTime = System.currentTimeMillis(),
            title = "Test Event",
            lat = 37.7749,
            lng = -122.4194,
            date = "2024-01-01",
            alarm = true
        )
        // Insert the event
        eventDao.insert(event)

        val insertedEvents = eventDao.getAllEvents().first()
        val insertedEvent = insertedEvents[0]

        eventDao.delete(insertedEvent)

        val events = eventDao.getAllEvents().first()
        assertEquals(0, events.size)
    }

    @Test
    fun getAllEvents() = runBlocking {
        val event1 = EventAlerts(
            eventTime = System.currentTimeMillis(),
            title = "Event 1",
            lat = 37.7749,
            lng = -122.4194,
            date = "2024-01-01",
            alarm = true
        )
        val event2 = EventAlerts(
            eventTime = System.currentTimeMillis() + 1000,
            title = "Event 2",
            lat = 34.0522,
            lng = -118.2437,
            date = "2024-01-02",
            alarm = false
        )
        eventDao.insert(event1)
        eventDao.insert(event2)

        val events = eventDao.getAllEvents().first()
        assertEquals(2, events.size)

        assertEquals(listOf(event1.eventTime, event2.eventTime).sorted(),
            events.map { it.eventTime }.sorted())
    }

    @After
    fun tearDown() {
        database.close()
    }
}
