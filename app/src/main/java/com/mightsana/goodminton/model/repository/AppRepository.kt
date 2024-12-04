package com.mightsana.goodminton.model.repository

import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.QuerySnapshot
import com.mightsana.goodminton.features.main.model.League
import com.mightsana.goodminton.features.main.model.LeagueJoint
import com.mightsana.goodminton.features.main.model.LeagueParticipant
import com.mightsana.goodminton.features.main.model.LeagueParticipantJoint
import com.mightsana.goodminton.features.main.model.Match
import com.mightsana.goodminton.features.main.model.MatchJoint
import com.mightsana.goodminton.features.main.model.ParticipantStats
import com.mightsana.goodminton.model.repository.friend_requests.FriendRequest
import com.mightsana.goodminton.model.repository.friend_requests.FriendRequestJoint
import com.mightsana.goodminton.model.repository.friends.Friend
import com.mightsana.goodminton.model.repository.friends.FriendJoint
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
    suspend fun getAllUsers(): List<MyUser>
    suspend fun getUsersByIds(ids: List<String>): List<MyUser>
    suspend fun getUser(userId: String): MyUser
    fun observeUserSnapshot(userId: String, onUserSnapshotUpdate: (DocumentSnapshot) -> Unit)
    fun observeUserJoint(userId: String, onUserUpdate: (MyUser) -> Unit)
    fun observeUsersSnapshot(userIds: List<String>, onUsersUpdate: (QuerySnapshot) -> Unit)
    fun observeUsersJoint(userIds: List<String>, onUsersUpdate: (List<MyUser>) -> Unit)

    // Create League
    suspend fun addLeagueParticipant(participant: LeagueParticipant)
    suspend fun addParticipantStats(participantStats: ParticipantStats)
    suspend fun createNewLeague(league: League)

    // Retrieve League Data
    suspend fun getLeagueIdsByUserId(userId: String): List<String>
    suspend fun getLeague(leagueId: String): League
    suspend fun getLeagueJoint(leagueId: String): LeagueJoint
    suspend fun getPublicLeagueIds(): List<String>
    suspend fun getLeagueByIds(ids: List<String>): List<League>
    suspend fun getUserAndPublicLeagues(userId: String): List<League>
    fun observeLeagueSnapshot(leagueId: String, onLeagueSnapshotUpdate: (DocumentSnapshot) -> Unit)
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
    fun observeLeagueParticipantsSnapshot(leagueId: String, onParticipantsSnapshotUpdate: (QuerySnapshot) -> Unit)
    fun observeLeagueParticipantsJoint(leagueId: String, onParticipantsUpdate: (List<LeagueParticipantJoint>) -> Unit)

    // Edit League Participant Data
    suspend fun updateParticipantRole(leagueId: String, userId: String, newRole: String)

    // Retrieve Participant Stat Data
    suspend fun getParticipantStatsByParticipantIds(ids: List<String>): List<ParticipantStats>

    // Retrieve Matches Data
    fun observeMatchesSnapshot(leagueId: String, onMatchesSnapshotUpdate: (QuerySnapshot?) -> Unit)
    fun observeMatchesJoint(leagueId: String, onMatchesUpdate: (List<MatchJoint>) -> Unit)

    // Retrieve Friends Data
    fun observeFriendsSnapshot(userId: String, onFriendsSnapshotUpdate: (QuerySnapshot) -> Unit)
    fun observeFriendsJoint(userId: String, onFriendsUpdate: (List<FriendJoint>) -> Unit)
    fun observeFriends(userId: String, onFriendsUpdate: (List<Friend>) -> Unit)

    // Retrieve Friend Requests Data
    fun observeFriendRequests(
        userId: String,
        onFriendRequestsUpdate: (List<FriendRequest>) -> Unit,
        onFriendRequestsReceivedUpdate: (List<FriendRequest>) -> Unit
    )
    fun observeFriendRequestsSnapshot(
        userId: String,
        onFriendRequestsSentSnapshotUpdate: (QuerySnapshot) -> Unit,
        onFriendRequestsReceivedSnapshotUpdate: (QuerySnapshot) -> Unit
    )
    fun observeFriendRequestsJoint(
        userId: String,
        onFriendRequestsSentUpdate: (List<FriendRequestJoint>) -> Unit,
        onFriendRequestsReceivedUpdate: (List<FriendRequestJoint>) -> Unit
    )
}