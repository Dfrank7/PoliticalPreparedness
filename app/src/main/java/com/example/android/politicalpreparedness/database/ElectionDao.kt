package com.example.android.politicalpreparedness.database

import androidx.room.*
import com.example.android.politicalpreparedness.network.models.Election

@Dao
interface ElectionDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(election: Election)

    @Query("SELECT * FROM election_table ORDER BY electionDay")
    suspend fun getElections():List<Election>

    @Query("SELECT * FROM election_table WHERE id = :id LIMIT 1")
    fun getElection(id:Int):Election?

    @Delete
    suspend fun delete(election: Election)

    @Query("DELETE FROM election_table WHERE id = :id")
    suspend fun deleteById(id: Int)

    @Query("DELETE FROM election_table")
    suspend fun clear()
//    suspend fun setFavorite(isFavorite:Boolean,electionId: Int)

    //TODO: Add insert query

    //TODO: Add select all election query

    //TODO: Add select single election query

    //TODO: Add delete query

    //TODO: Add clear query

}