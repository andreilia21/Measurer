package com.dewerro.measurer.di

import com.dewerro.measurer.data.auth.AuthService
import com.dewerro.measurer.data.auth.firebase.FirebaseAuthService
import com.dewerro.measurer.data.order.OrderService
import com.dewerro.measurer.data.order.firebase.FirebaseOrderService
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

@Module
@InstallIn(SingletonComponent::class)
object DataModule {

    @Provides
    fun providesAuthService(): AuthService {
        return FirebaseAuthService()
    }

    @Provides
    fun providesOrderService(): OrderService {
        return FirebaseOrderService()
    }

}