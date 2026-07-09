package com.example.rankeuca.data.repositories

import com.example.rankeuca.data.model.VoteItem
import com.example.rankeuca.data.model.VoteResponse

interface VoteRepository {
    suspend fun submitVotes(apiKey: String, votes: List<VoteItem>): VoteResponse
}