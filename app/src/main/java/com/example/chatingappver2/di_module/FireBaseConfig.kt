package com.example.chatingappver2.di_module

import com.example.chatingappver2.database.repository.AccountRepository
import com.example.chatingappver2.database.repository.AuthRepository
import com.example.chatingappver2.database.repository.MessageRepository
import com.example.chatingappver2.database.repository.ProfileRepository
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object FireBaseConfig {
    @Singleton
    @Provides
    fun provideFirebaseAuth(): FirebaseAuth = FirebaseAuth.getInstance()

    @Singleton
    @Provides
    fun provideFirebaseDatabase(): FirebaseDatabase = FirebaseDatabase.getInstance()

    @Singleton
    @Provides
    fun provideStorageReference(): FirebaseStorage = FirebaseStorage.getInstance()

    @Singleton
    @Provides
    fun provideAuthRepository(auth: FirebaseAuth, database: FirebaseDatabase): AuthRepository =
        AuthRepository(auth, database)

    @Singleton
    @Provides
    fun provideProfileRepository(
        auth: FirebaseAuth,
        database: FirebaseDatabase,
        storage: FirebaseStorage
    ): ProfileRepository =
        ProfileRepository(auth, database, storage.reference)


    @Singleton
    @Provides
    fun provideAcountRepository(auth: FirebaseAuth, database: FirebaseDatabase): AccountRepository =
        AccountRepository(auth, database)

    @Singleton
    @Provides
    fun provideMessageRepository(
        auth: FirebaseAuth,
        database: FirebaseDatabase,
        storage: FirebaseStorage
    ): MessageRepository =
        MessageRepository(auth, database, storage.reference)

}