package com.books.repo.detail

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import com.books.api.ApiClient
import com.books.repo.Result
import com.books.repo.search.SearchRepository
import com.books.ui.detail.DetailViewModel
import com.books.utils.CoroutinesTestExtension
import com.books.utils.InstantExecutorExtension
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.runTest
import org.junit.Rule
import org.junit.jupiter.api.Assertions.*

import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.junit.rules.TestRule
import org.mockito.Mock
import org.mockito.Mockito
import org.mockito.MockitoAnnotations

@ExperimentalCoroutinesApi
@ExtendWith(InstantExecutorExtension::class, CoroutinesTestExtension::class)
internal class DetailRepositoryTest {

    @get:Rule
    var rule: TestRule = InstantTaskExecutorRule()

    @get:Rule
    var coroutineScope: CoroutinesTestExtension = CoroutinesTestExtension()

    @Mock
    private lateinit var apiClient: ApiClient

    private lateinit var repository: DetailRepository
    @BeforeEach
    fun setUp() {
        MockitoAnnotations.openMocks(this)
        repository = DetailRepository(apiClient)
    }

    @Test
    fun getBookDetailsTest()  = runTest {
        val isbn13 = "9781617294136"
        val result = Result.Success(Detail(
            "0",
            "Securing DevOps",
            "Security in the Cloud",
            "Julien Vehent",
            "Manning",
            "1617294136",
            "9781617294136",
            "384",
            "2018",
            "5",
            "An application running in the cloud can benefit from incredible efficiencies, but they come with unique security threats too. A DevOps team's highest priority is understanding those risks and hardening the system against them.Securing DevOps teaches you the essential techniques to secure your cloud ...",
            "$26.98",
            "https://itbook.store/img/books/9781617294136.png",
            "https://itbook.store/books/9781617294136",
            null
        ))

        Mockito.`when`(apiClient.getBookDetails(isbn13))
            .thenReturn(result.data)

        advanceUntilIdle()
        assertEquals(result, repository.getBookDetails(isbn13))
    }
}