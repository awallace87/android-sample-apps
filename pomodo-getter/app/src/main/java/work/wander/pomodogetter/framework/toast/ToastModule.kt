package work.wander.pomodogetter.framework.toast

import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

/**
 * Module to bind the [DefaultToaster] to the [Toaster] interface
 */
@Module
@InstallIn(SingletonComponent::class)
abstract class ToastBindsModule {

    @Binds
    abstract fun bindToaster(toaster: DefaultToaster): Toaster

}