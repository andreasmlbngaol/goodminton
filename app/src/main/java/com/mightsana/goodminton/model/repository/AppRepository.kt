package com.mightsana.goodminton.model.repository

import com.google.firebase.firestore.FirebaseFirestore
import com.mightsana.goodminton.features.main.model.League
import com.mightsana.goodminton.features.main.model.LeagueJoint
import com.mightsana.goodminton.features.main.model.LeagueParticipant
import com.mightsana.goodminton.features.main.model.LeagueParticipantJoint
import com.mightsana.goodminton.features.main.model.Match
import com.mightsana.goodminton.features.main.model.ParticipantStats
import com.mightsana.goodminton.model.repository.friend_requests.FriendRequest
import com.mightsana.goodminton.model.repository.friends.Friend
import com.mightsana.goodminton.model.repository.users.MyUser

interface AppRepository {
    // Initiate
    val db: FirebaseFirestore
//    val batchSize: Int

    // Register
    suspend fun createNewUser(user: MyUser)
    suspend fun isUserRegistered(userId: String): Boolean
    suspend fun isUsernameAvailable(username: String): Boolean

    // Retrieve User Data
    suspend fun getUsersByIds(ids: List<String>): List<MyUser>
    fun observeUser(userId: String, onUserUpdate: (MyUser) -> Unit)

    // Create League
    suspend fun addLeagueParticipant(participant: LeagueParticipant)
    suspend fun addParticipantStats(participantStats: ParticipantStats)
    suspend fun createNewLeague(league: League)

    // Retrieve League Data
    suspend fun getLeagueIdsByUserId(userId: String): List<String>
    suspend fun getPublicLeagueIds(): List<String>
    suspend fun getLeagueByIds(ids: List<String>): List<League>
    suspend fun getUserAndPublicLeagues(userId: String): List<League>
    fun observeLeague(leagueId: String, onLeagueUpdate: (League) -> Unit)
    fun observeLeagueJoint(leagueId: String, onLeagueUpdate: (LeagueJoint) -> Unit)

    // Edit League Data
    suspend fun updateLeagueDiscipline(leagueId: String, double: Boolean)
    suspend fun updateLeagueFixedDouble(leagueId: String, fixedDouble: Boolean?)
    suspend fun updateLeagueDeuceEnabled(leagueId: String, deuceEnabled: Boolean)
    suspend fun updateLeagueVisibility(leagueId: String, private: Boolean)
    suspend fun updateLeagueName(leagueId: String, newName: String)
    suspend fun updateLeagueMatchPoints(leagueId: String, newMatchPoints: Int)
    suspend fun deleteLeague(leagueId: String)

    // Retrieve League Participant Data
    fun observeLeagueParticipants(leagueId: String, onParticipantsUpdate: (List<LeagueParticipant>) -> Unit)
    fun observeLeagueParticipantsJoint(leagueId: String, onParticipantsUpdate: (List<LeagueParticipantJoint>) -> Unit)

    // Edit League Participant Data
    suspend fun updateParticipantRole(leagueId: String, userId: String, newRole: String)

    // Retrieve Participant Stat Data
    suspend fun getParticipantStatsByParticipantIds(ids: List<String>): List<ParticipantStats>

    // Retrieve Matches Data
    fun observeMatches(leagueId: String, onMatchesUpdate: (List<Match>) -> Unit)

    // Retrieve Friends Data
    fun observeFriends(userId: String, onFriendsUpdate: (List<Friend>) -> Unit)

    // Retrieve Friend Requests Data
    fun observeFriendRequests(
        userId: String,
        onFriendRequestsUpdate: (List<FriendRequest>) -> Unit,
        onFriendRequestsReceivedUpdate: (List<FriendRequest>) -> Unit
    )
}