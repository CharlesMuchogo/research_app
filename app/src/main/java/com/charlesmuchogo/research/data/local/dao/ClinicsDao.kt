package com.charlesmuchogo.research.data.local.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.charlesmuchogo.research.domain.models.Clinic
import com.charlesmuchogo.research.domain.models.TestResult
import kotlinx.coroutines.flow.Flow

@Dao
interface ClinicsDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertClinics(clinics: List<Clinic>)

    @Query("SELECT * FROM Clinics ORDER BY name ASC")
    fun getClinics(): Flow<List<Clinic>>

    @Query("DELETE FROM Clinics")
    suspend fun deleteClinics()
}
