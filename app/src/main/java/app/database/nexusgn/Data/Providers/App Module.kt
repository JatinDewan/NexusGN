package app.database.nexusgn.Data.Providers

import android.app.Application
import app.database.nexusgn.Data.Implementations.ContextProviderImpl
import app.database.nexusgn.Data.Implementations.RawgApi
import app.database.nexusgn.Data.Implementations.RepositoryImpl
import com.jakewharton.retrofit2.converter.kotlinx.serialization.asConverterFactory
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import kotlinx.serialization.json.Json
import okhttp3.MediaType.Companion.toMediaType
import retrofit2.Retrofit
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DatabaseObject {

    private val json = Json { ignoreUnknownKeys = true }
    private const val BASE_URL = "https://api.rawg.io/api/"

    @Provides
    @Singleton
    fun providesApi(): RawgApi {
        return Retrofit.Builder()
            .addConverterFactory(json.asConverterFactory("application/json".toMediaType()))
            .baseUrl(BASE_URL)
            .build()
            .create(RawgApi::class.java)
    }

    @Provides
    @Singleton
    fun providesRepository(api: RawgApi): RepositoryImpl = RepositoryImpl(api)

    @Provides
    @Singleton
    fun providesContext(app: Application): ContextProviderImpl = ContextProviderImpl(app)

}