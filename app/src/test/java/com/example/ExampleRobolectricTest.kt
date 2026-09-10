package com.example

import android.content.Context
import androidx.test.core.app.ApplicationProvider
import com.example.ai.LifeOsAiService
import kotlinx.coroutines.runBlocking
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [34])
class ExampleRobolectricTest {

    @Test
    fun `read string from context`() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        val appName = context.getString(R.string.app_name)
        assertEquals("Kriti AI", appName)
    }

    @Test
    fun `ai service parses expense correctly`() = runBlocking {
        val aiService = LifeOsAiService()
        val text = "Swiggy Delivery Dinner Paid ₹420 on 10 Sept"
        val result = aiService.analyzeContent(null, text)

        assertEquals("EXPENSES", result.suggestedSection)
        assertNotNull(result.expense)
        assertEquals(420.0, result.expense!!.amount, 0.01)
    }

    @Test
    fun `ai service parses travel ticket correctly`() = runBlocking {
        val aiService = LifeOsAiService()
        val text = "IRCTC E-Ticket PNR 2849-201948 Rajdhani Express confirmed"
        val result = aiService.analyzeContent(null, text)

        assertEquals("BOOKINGS", result.suggestedSection)
        assertNotNull(result.booking)
        assertTrue(result.booking!!.pnr.contains("2849"))
    }

    @Test
    fun `ai service parses loan record correctly`() = runBlocking {
        val aiService = LifeOsAiService()
        val text = "Rahul borrowed 5000 due on 15 Sept"
        val result = aiService.analyzeContent(null, text)

        assertEquals("LOANS", result.suggestedSection)
        assertNotNull(result.loan)
        assertEquals(5000.0, result.loan!!.amount, 0.01)
    }

    @Test
    fun `ai service parses weight scale correctly`() = runBlocking {
        val aiService = LifeOsAiService()
        val text = "Scale log: 68.5 kg morning fasted check"
        val result = aiService.analyzeContent(null, text)

        assertEquals("HEALTH", result.suggestedSection)
        assertNotNull(result.health)
        assertEquals(68.5, result.health!!.weight, 0.01)
    }
}
