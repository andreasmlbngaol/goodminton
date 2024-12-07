package com.mightsana.goodminton.model.repository

import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.QuerySnapshot
import com.mightsana.goodminton.features.main.model.InvitationJoint
import com.mightsana.goodminton.features.main.model.League
import com.mightsana.goodminton.features.main.model.LeagueJoint
import com.mightsana.goodminton.features.main.model.LeagueParticipant
import com.mightsana.goodminton.features.main.model.LeagueParticipantJoint
import com.mightsana.goodminton.features.main.model.MatchJoint
import com.mightsana.goodminton.features.main.model.ParticipantStats
import com.mightsana.goodminton.features.main.model.ParticipantStatsJoint
import com.mightsana.goodminton.features.main.model.Role
import com.mightsana.goodminton.model.repository.friend_requests.FriendRequestJoint
import com.mightsana.goodminton.model.repository.friends.FriendJoint
import com.mightsana.goodminton.model.repository.users.MyUser

interface AppRepository {
    // Initiate
    val db: FirebaseFirestore

    // App Checking
    suspend fun isMaintenance(): Boolean

    // Register
    suspend fun createNewUser(user: MyUser)
    suspend fun isUserRegistered(userId: String): Boolean
    suspend fun isUsernameAvailable(username: String): Boolean

    // Retrieve User Data
    suspend fun getAllUsers(): List<MyUser>
    suspend fun getUsersByIds(ids: List<String>): List<MyUser>
    suspend fun getUser(userId: String): MyUser
    fun observeUserSnapshot(userId: String, onUserSnapshotUpdate: (DocumentSnapshot?) -> Unit)
    fun observeUserJoint(userId: String, onUserUpdate: (MyUser) -> Unit)
    fun observeUsersSnapshot(userIds: List<String>, onUsersUpdate: (QuerySnapshot?) -> Unit)
    fun observeUsersJoint(userIds: List<String>, onUsersUpdate: (List<MyUser>) -> Unit)

    // Create League
    suspend fun addLeagueParticipant(participant: LeagueParticipant)
    suspend fun createNewLeague(league: League)

    // Retrieve League Data
    suspend fun getLeagueIdsByUserId(userId: String): List<String>
    suspend fun getLeague(leagueId: String): League
    suspend fun getLeagueJoint(leagueId: String): LeagueJoint
    suspend fun getPublicLeagueIds(): List<String>
    suspend fun getLeagueJointsByIds(ids: List<String>): List<LeagueJoint>
    suspend fun getUserAndPublicLeagues(userId: String): List<LeagueJoint>
    fun observeLeagueSnapshot(leagueId: String, onLeagueSnapshotUpdate: (DocumentSnapshot?) -> Unit)
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
    suspend fun getParticipantsJoint(leagueId: String): List<LeagueParticipantJoint>
    fun observeLeagueParticipantsSnapshot(leagueId: String, onParticipantsSnapshotUpdate: (QuerySnapshot?) -> Unit)
    fun observeLeagueParticipantsJoint(leagueId: String, onParticipantsUpdate: (List<LeagueParticipantJoint>) -> Unit)

    // Edit League Participant Data
    suspend fun updateParticipantRole(leagueId: String, userId: String, newRole: String)
    suspend fun addParticipant(leagueId: String, userId: String, role: Role = Role.Player)

    // Retrieve Participant Stat Data
    suspend fun getParticipantStatsByParticipantIds(ids: List<String>): List<ParticipantStats>
    suspend fun addParticipantStats(leagueId: String, userId: String)

    // Retrieve Matches Data
    fun observeMatchesSnapshot(leagueId: String, onMatchesSnapshotUpdate: (QuerySnapshot?) -> Unit)
    fun observeMatchesJoint(leagueId: String, onMatchesUpdate: (List<MatchJoint>) -> Unit)

    // Matches Action
    suspend fun createNewMatch(leagueId: String, team1: List<String>, team2: List<String>)

    // Retrieve Friends Data
    fun observeFriendsSnapshot(userId: String, onFriendsSnapshotUpdate: (QuerySnapshot?) -> Unit)
    fun observeFriendsJoint(userId: String, onFriendsUpdate: (List<FriendJoint>) -> Unit)

    // Friend Action
    suspend fun deleteFriend(id: String)
    suspend fun addFriend(userIds: List<String>)

    // Retrieve Friend Requests Data
    fun observeFriendRequestsSnapshot(
        userId: String,
        onFriendRequestsSentSnapshotUpdate: (QuerySnapshot?) -> Unit,
        onFriendRequestsReceivedSnapshotUpdate: (QuerySnapshot?) -> Unit
    )
    fun observeFriendRequestsJoint(
        userId: String,
        onFriendRequestsSentUpdate: (List<FriendRequestJoint>) -> Unit = {},
        onFriendRequestsReceivedUpdate: (List<FriendRequestJoint>) -> Unit = {}
    )

    // Friend Request Action
    suspend fun deleteFriendRequest(requestId: String)
    suspend fun acceptFriendRequest(requestId: String, userIds: List<String>)
    suspend fun createFriendRequest(senderId: String, receiverId: String)

    // Retrieve League Invitation Data
    fun observeLeagueInvitationsSentSnapshot(leagueId: String, onInvitationsSentSnapshotUpdate: (QuerySnapshot?) -> Unit)
    fun observeLeagueInvitationsSentJoint(leagueId: String, onInvitationsSentUpdate: (List<InvitationJoint>) -> Unit)
    fun observeLeagueInvitationsReceivedSnapshot(userId: String, onInvitationsReceivedSnapshotUpdate: (QuerySnapshot?) -> Unit)
    fun observeLeagueInvitationsReceivedJoint(userId: String, onInvitationsReceivedUpdate: (List<InvitationJoint>) -> Unit)

    // League Invitation Action
    suspend fun addInvitation(leagueId: String, senderId: String, receiverId: String)
    suspend fun deleteInvitation(invitationId: String)
    suspend fun acceptInvitation(invitationId: String, leagueId: String, userId: String)

    // Retrieve League Participants Stats Data
    fun observeParticipantsStatsSnapshot(leagueId: String, onParticipantsStatsSnapshotUpdate: (QuerySnapshot?) -> Unit)
    fun observeParticipantsStatsJoint(leagueId: String, onParticipantsStatsUpdate: (List<ParticipantStatsJoint>) -> Unit)

}