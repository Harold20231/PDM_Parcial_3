package com.example.rankeuca.data.repositories

import com.example.rankeuca.data.api.RankeUcaApi
import com.example.rankeuca.data.database.dao.OptionDao
import com.example.rankeuca.data.model.VoteItem
import com.example.rankeuca.data.model.VoteRequest
import com.example.rankeuca.data.model.VoteResponse
import javax.inject.Inject

class VoteRepositoryImpl @Inject constructor(
    private val optionDao: OptionDao,
    private val api: RankeUcaApi
) : VoteRepository {

    override suspend fun submitVotes(apiKey: String, votes: List<VoteItem>): VoteResponse {
        return api.submitVotes(apiKey, VoteRequest(votes))
    }
}