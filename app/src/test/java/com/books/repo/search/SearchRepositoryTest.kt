package com.books.repo.search

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import com.books.api.ApiClient
import com.books.utils.CoroutinesTestExtension
import com.books.utils.InstantExecutorExtension
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runTest
import org.junit.Rule
import org.junit.jupiter.api.Assertions.*
import com.books.repo.Result

import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.junit.rules.TestRule
import org.mockito.Mock
import org.mockito.Mockito
import org.mockito.MockitoAnnotations

@ExperimentalCoroutinesApi
@ExtendWith(InstantExecutorExtension::class, CoroutinesTestExtension::class)
internal class SearchRepositoryTest {

    @get:Rule
    var rule: TestRule = InstantTaskExecutorRule()

    @get:Rule
    var coroutineScope: CoroutinesTestExtension = CoroutinesTestExtension()

    @Mock
    private lateinit var apiClient: ApiClient

    private lateinit var repository: SearchRepository

    @BeforeEach
    fun setUp() {
        MockitoAnnotations.openMocks(this)
        repository = SearchRepository(apiClient)
    }

    @Test
    fun noOpTest() = runTest {
        repository.init("java")
        val apiResultNoOp = Books(
            "3", "1",
            arrayListOf(
                Book("java android", "sub", "1234567890", "30", "image", "url"),
                Book("Learning java kotlin", "sub", "1234567890", "30", "image", "url"),
                Book("rxjava", "sub", "1234567890", "30", "image", "url"),
            )
        )

        Mockito.`when`(apiClient.searchBook("java", 1))
            .thenReturn(apiResultNoOp)

        assertEquals(Result.Success(apiResultNoOp.books), repository.searchBook())
    }

    @Test
    fun orTest() = runTest {
        repository.init("java|kotlin")

        val javaResultsOr = Books(
            "2", "1",
            arrayListOf(
                Book("java android", "sub", "1234567890", "30", "image", "url"),
                Book("Learning java kotlin", "sub", "1234567890", "30", "image", "url"),
                Book("rxjava", "sub", "1234567890", "30", "image", "url"),
                )
        )
        val kotlinResultsOr = Books(
            "3", "1",
            arrayListOf(
                Book("Learning java kotlin", "sub", "1234567890", "30", "image", "url"),
                Book("kotlin", "sub", "1234567890", "30", "image", "url"),
                Book("android kotlin", "sub", "1234567890", "30", "image", "url"),
            )
        )

        Mockito.`when`(apiClient.searchBook("java", 1))
            .thenReturn(javaResultsOr)

        Mockito.`when`(apiClient.searchBook("kotlin", 1))
            .thenReturn(kotlinResultsOr)


        val javaKotlinResultsOr = Books(
            "5", "1",
            arrayListOf(
                Book("java android", "sub", "1234567890", "30", "image", "url"),
                Book("Learning java kotlin", "sub", "1234567890", "30", "image", "url"),
                Book("rxjava", "sub", "1234567890", "30", "image", "url"),
                Book("Learning java kotlin", "sub", "1234567890", "30", "image", "url"),
                Book("kotlin", "sub", "1234567890", "30", "image", "url"),
                Book("android kotlin", "sub", "1234567890", "30", "image", "url"),
                )
        )

        assertEquals(Result.Success(javaKotlinResultsOr.books), repository.searchBook())
    }


    @Test
    fun notTest() = runTest {
        repository.init("java-kotlin")


        val javaResults = Books(
            "6", "1",
            arrayListOf(
                Book("java android", "sub", "1234567890", "30", "image", "url"),
                Book("Learning java kotlin", "sub", "1234567890", "30", "image", "url"),
                Book("kotlin", "sub", "1234567890", "30", "image", "url"),
                Book("rxjava", "sub", "1234567890", "30", "image", "url"),
                Book("android kotlin", "sub", "1234567890", "30", "image", "url"),
                Book("android", "sub", "1234567890", "30", "image", "url")
            )
        )
        Mockito.`when`(apiClient.searchBook("java", 1))
            .thenReturn(javaResults)

        val apiResultsNot = Books(
            "2", "1",
            arrayListOf(
                Book("java android", "sub", "1234567890", "30", "image", "url"),
                Book("rxjava", "sub", "1234567890", "30", "image", "url"),
                Book("android", "sub", "1234567890", "30", "image", "url")
            )
        )

        assertEquals(Result.Success(apiResultsNot.books), repository.searchBook())
    }
}