package millich.michael.myphoneandi.DI

import android.app.Application
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ViewModelComponent
import millich.michael.myphoneandi.database.ScreenEventDatabase
import millich.michael.myphoneandi.database.ScreenEventDatabaseDAO


@Module
@InstallIn(ViewModelComponent::class)
object DatabaseModule {
    @Provides
    fun provideUnlockDatabaseDAO(application: Application): ScreenEventDatabaseDAO {
        return ScreenEventDatabase.getInstance(application).screenEventDatabaseDAO
    }
}