package millich.michael.myphoneandi.DI

import android.app.Application
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ViewModelComponent
import millich.michael.myphoneandi.database.UnlockDatabase
import millich.michael.myphoneandi.database.UnlockDatabaseDAO


@Module
@InstallIn(ViewModelComponent::class)
object DatabaseModule {
    @Provides
    fun provideUnlockDatabaseDAO(application: Application): UnlockDatabaseDAO {
        return UnlockDatabase.getInstance(application).unlockDatabaseDAO
    }
}