package com.example

import android.content.Context
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import com.example.data.local.SpeedTestDao
import com.example.data.local.SpeedTestDatabase
import com.example.data.model.SpeedTestRecord
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.runTest
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertNull
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config
import java.io.IOException

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [36])
class SpeedTestDatabaseTest {

    private lateinit var db: SpeedTestDatabase
    private lateinit var dao: SpeedTestDao

    @Before
    fun createDb() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        db = Room.inMemoryDatabaseBuilder(context, SpeedTestDatabase::class.java)
            .allowMainThreadQueries()
            .build()
        dao = db.speedTestDao()
    }

    @After
    @Throws(IOException::class)
    fun closeDb() {
        db.close()
    }

    @Test
    fun insertAndRetrieveSpeedTestRecord() = runTest {
        val record = SpeedTestRecord(
            timestamp = 1700000000000L,
            downloadSpeedMbps = 152.4,
            uploadSpeedMbps = 45.8,
            pingMs = 18L,
            jitterMs = 2L,
            connectionType = "Wi-Fi",
            ispOrServer = "Cloudflare Edge CDN",
            rating = "Ultra 4K & Gaming"
        )

        val insertedId = dao.insert(record)
        assertEquals(1L, insertedId)

        val records = dao.getAllRecords().first()
        assertEquals(1, records.size)

        val retrieved = records[0]
        assertEquals(1700000000000L, retrieved.timestamp)
        assertEquals(152.4, retrieved.downloadSpeedMbps, 0.001)
        assertEquals(45.8, retrieved.uploadSpeedMbps, 0.001)
        assertEquals(18L, retrieved.pingMs)
        assertEquals(2L, retrieved.jitterMs)
        assertEquals("Wi-Fi", retrieved.connectionType)
        assertEquals("Cloudflare Edge CDN", retrieved.ispOrServer)
        assertEquals("Ultra 4K & Gaming", retrieved.rating)
    }

    @Test
    fun getLatestRecordAndAggregates() = runTest {
        val record1 = SpeedTestRecord(
            timestamp = 1000L,
            downloadSpeedMbps = 50.0,
            uploadSpeedMbps = 10.0,
            pingMs = 30L,
            jitterMs = 4L,
            connectionType = "Cellular",
            ispOrServer = "Verizon",
            rating = "Standard Broadband"
        )

        val record2 = SpeedTestRecord(
            timestamp = 2000L,
            downloadSpeedMbps = 250.0,
            uploadSpeedMbps = 80.0,
            pingMs = 10L,
            jitterMs = 1L,
            connectionType = "Wi-Fi",
            ispOrServer = "Google Fiber",
            rating = "Ultra 4K & Gaming"
        )

        dao.insert(record1)
        dao.insert(record2)

        val count = dao.getRecordCount().first()
        assertEquals(2, count)

        // Latest record should be record2 (timestamp 2000L > 1000L)
        val latest = dao.getLatestRecord().first()
        assertNotNull(latest)
        assertEquals(2000L, latest!!.timestamp)
        assertEquals(250.0, latest.downloadSpeedMbps, 0.001)

        val maxDl = dao.getMaxDownload().first()
        assertEquals(250.0, maxDl!!, 0.001)

        val maxUl = dao.getMaxUpload().first()
        assertEquals(80.0, maxUl!!, 0.001)

        val avgPing = dao.getAveragePing().first()
        assertEquals(20.0, avgPing!!, 0.001)
    }

    @Test
    fun deleteRecordAndClearAll() = runTest {
        val record = SpeedTestRecord(
            timestamp = 1500L,
            downloadSpeedMbps = 100.0,
            uploadSpeedMbps = 20.0,
            pingMs = 25L,
            jitterMs = 3L,
            connectionType = "Wi-Fi",
            ispOrServer = "Fast",
            rating = "Fast HD Streaming"
        )

        val id = dao.insert(record)
        assertEquals(1, dao.getAllRecords().first().size)

        dao.deleteById(id)
        assertEquals(0, dao.getAllRecords().first().size)

        // Insert two, then clear
        dao.insert(record)
        dao.insert(record.copy(id = 0, timestamp = 1600L))
        assertEquals(2, dao.getAllRecords().first().size)

        dao.clearAll()
        assertEquals(0, dao.getAllRecords().first().size)
    }
}
