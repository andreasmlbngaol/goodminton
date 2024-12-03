package com.mightsana.goodminton.model.repository

import android.util.Log
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.FieldPath
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ListenerRegistration
import com.google.firebase.firestore.QuerySnapshot
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.mightsana.goodminton.features.main.model.League
import com.mightsana.goodminton.features.main.model.LeagueJoint
import com.mightsana.goodminton.features.main.model.LeagueParticipant
import com.mightsana.goodminton.features.main.model.LeagueParticipantJoint
import com.mightsana.goodminton.features.main.model.Match
import com.mightsana.goodminton.features.main.model.MatchJoint
import com.mightsana.goodminton.features.main.model.ParticipantStats
import com.mightsana.goodminton.features.main.model.Role
import com.mightsana.goodminton.features.main.model.Status
import com.mightsana.goodminton.model.repository.friend_requests.FriendRequest
import com.mightsana.goodminton.model.repository.friend_requests.FriendRequestJoint
import com.mightsana.goodminton.model.repository.friends.Friend
import com.mightsana.goodminton.model.repository.users.MyUser
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext
import javax.inject.Inject

class AppRepositoryImpl @Inject constructor(): AppRepository {
    // Initiate
    override val db: FirebaseFirestore
        get() = Firebase.firestore
    private val usersCollection = db.collection("users")
    private val friendRequestsCollection = db.collection("friendRequests")
    private val friendsCollection = db.collection("friends")
    private val leaguesCollection = db.collection("leagues")
    private val leagueParticipantsCollection = db.collection("leagueParticipants")
    private val participantStatsCollection = db.collection("participantStats")
    private val matchesCollection = db.collection("matches")

    private val batchMaxSize = 10

    // Register
    override suspend fun createNewUser(user: MyUser) {
        usersCollection
            .document(user.uid)
            .set(user)
            .await()
    }
    override suspend fun isUserRegistered(userId: String): Boolean =
        usersCollection
            .document(userId)
            .get()
            .await()
            .exists()
    override suspend fun isUsernameAvailable(username: String): Boolean =
        usersCollection
            .whereEqualTo("username", username)
            .get()
            .await()
            .isEmpty

    // Retrive User Data
    override suspend fun getUsersByIds(ids: List<String>): List<MyUser> =
        coroutineScope {
            val userDeferreds = ids.chunked(batchMaxSize).map { batch ->
                async {
                    usersCollection
                        .whereIn(FieldPath.documentId(), batch)
                        .get()
                        .await()
                        .toObjects(MyUser::class.java)
                }
            }
            userDeferreds.flatMap { it.await() }
        }
    override fun observeUserSnapshot(userId: String, onUserSnapshotUpdate: (DocumentSnapshot) -> Unit) {
        usersCollection
            .document(userId)
            .addSnapshotListener { snapshot, exception ->
                if (exception != null) {
                    return@addSnapshotListener
                }
                if (snapshot != null && snapshot.exists())
                    onUserSnapshotUpdate(snapshot)
                else
                    Log.d("AppRepositoryImpl", "User snapshot is null or does not exist")
            }
    }
    override fun observeUserJoint(userId: String, onUserUpdate: (MyUser) -> Unit) {
        observeUserSnapshot(userId) {
            val user = it.toObject(MyUser::class.java)!!
            onUserUpdate(user)
        }
    }
    override fun observeUsersSnapshot(userIds: List<String>, onUsersUpdate: (QuerySnapshot) -> Unit) {
        userIds.chunked(batchMaxSize).map { batch ->
            usersCollection
                .whereIn(FieldPath.documentId(), batch)
                .addSnapshotListener { snapshot, exception ->
                    if (exception != null) {
                        return@addSnapshotListener
                    }
                    if (snapshot != null && !snapshot.isEmpty)
                        onUsersUpdate(snapshot)
                }
        }
    }
    override fun observeUsersJoint(userIds: List<String>, onUsersUpdate: (List<MyUser>) -> Unit) {
        observeUsersSnapshot(userIds) { snapshot ->
            val users = mutableListOf<MyUser>()
            snapshot.forEach { userSnapshot ->
                val user = userSnapshot.toObject(MyUser::class.java)
                users.add(user)
            }
            onUsersUpdate(users.toList())
        }
    }

    // Create League
    override suspend fun addLeagueParticipant(participant: LeagueParticipant) {
        val newParticipantDoc = leagueParticipantsCollection.document()
        val generatedId = newParticipantDoc.id

        newParticipantDoc
            .set(participant.copy(id = generatedId))
            .await()
    }
    override suspend fun addParticipantStats(participantStats: ParticipantStats) {
        val newStatsDoc = participantStatsCollection.document()
        val generatedId = newStatsDoc.id

        newStatsDoc
            .set(participantStats.copy(id = generatedId))
            .await()
    }
    override suspend fun createNewLeague(league: League) {
        val newLeagueDoc = leaguesCollection.document()
        val generatedId = newLeagueDoc.id

        newLeagueDoc
            .set(league.copy(id = generatedId))
            .await()

        addLeagueParticipant(
            LeagueParticipant(
                leagueId = generatedId,
                userId = league.createdById,
                role = Role.Creator
            )
        )

        addParticipantStats(
            ParticipantStats(
                leagueId = generatedId,
                userId = league.createdById
            )
        )
    }

    // Retrieve League Data
    override suspend fun getLeagueIdsByUserId(userId: String): List<String> =
        try {
            leagueParticipantsCollection
                .whereEqualTo("userId", userId)
                .whereEqualTo("status", Status.Active)
                .get()
                .await()
                .map { it.getString("leagueId")!! }
                .filter {it.isNotEmpty()}
        } catch (e: Exception) {
            emptyList()
        }
    override suspend fun getPublicLeagueIds(): List<String> =
        try {
            leaguesCollection
                .whereEqualTo("private", false)
                .get()
                .await()
                .map { it.getString("id")!! }
                .filter { it.isNotEmpty() }
        } catch (e: Exception) {
            emptyList()
        }
    override suspend fun getLeagueByIds(ids: List<String>): List<League> =
        coroutineScope {
            val leagueDeferreds = ids.chunked(batchMaxSize).map { batch ->
                async {
                    leaguesCollection
                        .whereIn(FieldPath.documentId(), batch)
                        .get()
                        .await()
                        .toObjects(League::class.java)
                }
            }
            leagueDeferreds.flatMap { it.await() }
        }
    override suspend fun getUserAndPublicLeagues(userId: String): List<League> {
        val userLeagueIds = getLeagueIdsByUserId(userId).toSet()
        val publicLeagueIds = getPublicLeagueIds().toSet()
        val unionLeagueIds = userLeagueIds.union(publicLeagueIds).toList()
        return getLeagueByIds(unionLeagueIds)
    }
    override fun observeLeagueSnapshot(leagueId: String, onLeagueSnapshotUpdate: (DocumentSnapshot) -> Unit) {
        leaguesCollection
            .document(leagueId)
            .addSnapshotListener { snapshot, exception ->
                if (exception != null) {
                    return@addSnapshotListener
                }
                if (snapshot != null && snapshot.exists())
                    onLeagueSnapshotUpdate(snapshot)
                else
                    Log.d("AppRepositoryImpl", "League snapshot is null or does not exist")
            }
    }
    override fun observeLeagueJoint(leagueId: String, onLeagueUpdate: (LeagueJoint) -> Unit) {
        observeLeagueSnapshot(leagueId) {
            val league = it.toObject(League::class.java)!!
            observeUserJoint(league.createdById) { creator ->
                onLeagueUpdate(
                    LeagueJoint(
                        id = league.id,
                        name = league.name,
                        matchPoints = league.matchPoints,
                        private = league.private,
                        deuceEnabled = league.deuceEnabled,
                        double = league.double,
                        fixedDouble = league.fixedDouble,
                        createdBy = creator,
                        createdAt = league.createdAt
                    )
                )
            }
        }
    }

    // Edit League Data
    override suspend fun updateLeagueDiscipline(leagueId: String, double: Boolean) {
        Log.d("DetailViewModel", "leagueId: $leagueId")
        Log.d("DetailViewModel", "updateLeagueDiscipline: $double")
        leaguesCollection
            .document(leagueId)
            .update("double", double)
            .await()

        if(!double) { updateLeagueFixedDouble(leagueId, null) }
        else { updateLeagueFixedDouble(leagueId, false) }
    }
    override suspend fun updateLeagueFixedDouble(leagueId: String, fixedDouble: Boolean?) {
        leaguesCollection
            .document(leagueId)
            .update("fixedDouble", fixedDouble)
            .await()
    }
    override suspend fun updateLeagueDeuceEnabled(leagueId: String, deuceEnabled: Boolean) {
        leaguesCollection
            .document(leagueId)
            .update("deuceEnabled", deuceEnabled)
            .await()
    }
    override suspend fun updateLeagueVisibility(leagueId: String, private: Boolean) {
        leaguesCollection
            .document(leagueId)
            .update("private", private)
            .await()
    }
    override suspend fun updateLeagueName(leagueId: String, newName: String) {
        leaguesCollection
            .document(leagueId)
            .update("name", newName)
            .await()
    }
    override suspend fun updateLeagueMatchPoints(leagueId: String, newMatchPoints: Int) {
        leaguesCollection
            .document(leagueId)
            .update("matchPoints", newMatchPoints)
            .await()
    }
    override suspend fun deleteLeague(leagueId: String) {
        withContext(Dispatchers.IO) {  // Pindahkan ke thread latar belakang
            try {
                val leaguesRef = leaguesCollection.document(leagueId)
                val participantsQuery = leagueParticipantsCollection.whereEqualTo("leagueId", leagueId).get().await()
                val statsQuery = participantStatsCollection.whereEqualTo("leagueId", leagueId).get().await()
                val matchesQuery = matchesCollection.whereEqualTo("leagueId", leagueId).get().await()

                db.runBatch { batch ->
                    // Hapus league
                    batch.delete(leaguesRef)

                    // Hapus semua peserta liga
                    for (document in participantsQuery.documents) {
                        batch.delete(document.reference)
                    }

                    // Hapus semua statistik peserta
                    for (document in statsQuery.documents) {
                        batch.delete(document.reference)
                    }

                    // Hapus semua pertandingan
                    for (document in matchesQuery.documents) {
                        batch.delete(document.reference)
                    }
                }.await()
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    // Retrieve League Participant Data
    override fun observeLeagueParticipantsSnapshot(leagueId: String, onParticipantsSnapshotUpdate: (QuerySnapshot) -> Unit) {
        leagueParticipantsCollection
            .whereEqualTo("leagueId", leagueId)
            .whereEqualTo("status", Status.Active)
            .addSnapshotListener { snapshot, exception ->
                if (exception != null) {
                    return@addSnapshotListener
                }
                if (snapshot != null && !snapshot.isEmpty)
                    onParticipantsSnapshotUpdate(snapshot)
                else
                    Log.d("AppRepositoryImpl", "League participants snapshot is null or does not exist")
            }
    }
    override fun observeLeagueParticipantsJoint(leagueId: String, onParticipantsUpdate: (List<LeagueParticipantJoint>) -> Unit) {
        observeLeagueJoint(leagueId) { leagueJoint ->
            observeLeagueParticipantsSnapshot(leagueId) { participantsSnapshot ->
                val participants = mutableListOf<LeagueParticipantJoint>()
                participantsSnapshot.forEach { participantSnapshot ->
                    val participant = participantSnapshot.toObject(LeagueParticipant::class.java)
                    observeUserJoint(participant.userId) { user ->
                        participants.add(
                            LeagueParticipantJoint(
                                id = participant.id,
                                league = leagueJoint,
                                user = user,
                                role = participant.role,
                                status = participant.status,
                                participateAt = participant.participateAt
                            )
                        )
                        if (participants.size == participantsSnapshot.size())
                            onParticipantsUpdate(participants.toList())

                    }
                }
            }
        }
    }

    // Edit League Participant Data
    override suspend fun updateParticipantRole(leagueId: String, userId: String, newRole: String) {
        leagueParticipantsCollection
            .whereEqualTo("leagueId", leagueId)
            .whereEqualTo("userId", userId)
            .get()
            .await()
            .documents[0]
            .reference
            .update("role", newRole)
            .await()
    }

    // Retrieve Participant Stat Data
    override suspend fun getParticipantStatsByParticipantIds(ids: List<String>): List<ParticipantStats> =
        coroutineScope {
            val statsDeferreds = ids.chunked(batchMaxSize).map { batch ->
                async {
                    participantStatsCollection
                        .whereIn(FieldPath.documentId(), batch)
                        .get()
                        .await()
                        .toObjects(ParticipantStats::class.java)
                }
            }
            statsDeferreds.flatMap { it.await() }
        }

    // Retrieve Matches Data
    override fun observeMatchesSnapshot(leagueId: String, onMatchesSnapshotUpdate: (QuerySnapshot) -> Unit) {
        matchesCollection
            .whereEqualTo("leagueId", leagueId)
            .addSnapshotListener { snapshot, exception ->
                if (exception != null) {
                    return@addSnapshotListener
                }
                if (snapshot != null && !snapshot.isEmpty)
                    onMatchesSnapshotUpdate(snapshot)
                else
                    Log.d("AppRepositoryImpl", "Matches snapshot is null or does not exist")
            }
    }
    override fun observeMatchesJoint(leagueId: String, onMatchesUpdate: (List<MatchJoint>) -> Unit) {
        observeLeagueJoint(leagueId) { leagueJoint ->
            Log.d("AppRepositoryImpl", "leagueJoint: $leagueJoint")
            observeMatchesSnapshot(leagueId) { matchesSnapshot ->
                Log.d("AppRepositoryImpl", "matchesSnapshot: $matchesSnapshot")
                val matches = mutableListOf<MatchJoint>()
                matchesSnapshot.forEach { matchSnapshot ->
                    Log.d("AppRepositoryImpl", "matchSnapshot: $matchSnapshot")
                    val match = matchSnapshot.toObject(Match::class.java)
                    val allUserIds = match.team1Ids + match.team2Ids
                    Log.d("AppRepositoryImpl", "allUserIds: $allUserIds")

                    observeUsersJoint(allUserIds) { users ->
                        Log.d("AppRepositoryImpl", "users: $users")
                        val team1Users = users.filter { it.uid in match.team1Ids }
                        val team2Users = users.filter { it.uid in match.team2Ids }

                        matches.add(
                            MatchJoint(
                                id = match.id,
                                league = leagueJoint,
                                team1 = team1Users,
                                team2 = team2Users,
                                team1Score = match.team1Score,
                                team2Score = match.team2Score,
                                startedAt = match.startedAt,
                                finishedAt = match.finishedAt,
                                duration = match.duration,
                                status = match.status
                            )
                        )
                        Log.d("AppRepositoryImpl", "matches: $matches")

                        if (matches.size == matchesSnapshot.size())
                            onMatchesUpdate(matches.toList())

                    }
                }
            }
        }
    }

    // Retrieve Friends Data
    private var friendsListener: ListenerRegistration? = null
    override fun observeFriends(userId: String, onFriendsUpdate: (List<Friend>) -> Unit) {
        friendsListener?.remove()
        friendsListener = friendsCollection
            .whereArrayContains("ids", userId)
            .addSnapshotListener { snapshot, exception ->
                if (exception != null) {
                    return@addSnapshotListener
                }
                var friends = listOf<Friend>()
                if (snapshot != null && !snapshot.isEmpty) {
                    friends = snapshot.documents.mapNotNull {
                        it.toObject(Friend::class.java)
                    }
                }
                onFriendsUpdate(friends)
            }
    }

    // Retrieve Friend Requests Data
    private var friendRequestsListener: ListenerRegistration? = null
    private var friendRequestsReceivedListener: ListenerRegistration? = null
    override fun observeFriendRequests(
        userId: String,
        onFriendRequestsUpdate: (List<FriendRequest>) -> Unit,
        onFriendRequestsReceivedUpdate: (List<FriendRequest>) -> Unit
    ) {
        friendRequestsListener?.remove()
        friendRequestsReceivedListener?.remove()
        friendRequestsListener = friendRequestsCollection
            .whereEqualTo("senderId", userId)
            .addSnapshotListener { snapshot, exception ->
                if (exception != null) {
                    return@addSnapshotListener
                }
                var friendRequests = listOf<FriendRequest>()
                if (snapshot != null && !snapshot.isEmpty) {
                    friendRequests = snapshot.documents.mapNotNull {
                        it.toObject(FriendRequest::class.java)
                    }
                }
                onFriendRequestsUpdate(friendRequests)
            }

        friendRequestsReceivedListener = friendRequestsCollection
            .whereEqualTo("receiverId", userId)
            .addSnapshotListener { snapshot, exception ->
                if (exception != null) {
                    return@addSnapshotListener
                }
                var friendRequestsReceived = listOf<FriendRequest>()
                if (snapshot != null && !snapshot.isEmpty) {
                    friendRequestsReceived = snapshot.documents.mapNotNull {
                        it.toObject(FriendRequest::class.java)
                    }
                }
                onFriendRequestsReceivedUpdate(friendRequestsReceived)
            }
    }
    override fun observeFriendRequestsSnapshot(
        userId: String,
        onFriendRequestsSentSnapshotUpdate: (QuerySnapshot) -> Unit,
        onFriendRequestsReceivedSnapshotUpdate: (QuerySnapshot) -> Unit
    ) {
        friendRequestsCollection
            .whereEqualTo("senderId", userId)
            .addSnapshotListener { snapshot, exception ->
                if (exception != null) {
                    return@addSnapshotListener
                }
                if (snapshot != null && !snapshot.isEmpty)
                    onFriendRequestsSentSnapshotUpdate(snapshot)
                else
                    Log.d("AppRepositoryImpl", "Friend requests sent snapshot is null or does not exist")
            }

        friendRequestsCollection
            .whereEqualTo("receiverId", userId)
            .addSnapshotListener { snapshot, exception ->
                if (exception != null) {
                    return@addSnapshotListener
                }
                if (snapshot != null && !snapshot.isEmpty)
                    onFriendRequestsReceivedSnapshotUpdate(snapshot)
                else
                    Log.d("AppRepositoryImpl", "Friend requests received snapshot is null or does not exist")
            }
    }
    override fun observeFriendRequestsJoint(
        userId: String,
        onFriendRequestsSentUpdate: (List<FriendRequestJoint>) -> Unit,
        onFriendRequestsReceivedUpdate: (List<FriendRequestJoint>) -> Unit
    ) {
        observeFriendRequestsSnapshot(
            userId = userId,
            onFriendRequestsSentSnapshotUpdate = { requestsSentSnapshot ->
                val requestsSent = mutableListOf<FriendRequestJoint>()
                requestsSentSnapshot.forEach { requestSentSnapshot ->
                    val requestSent = requestSentSnapshot.toObject(FriendRequest::class.java)
                    observeUserJoint(requestSent.senderId) { sender ->
                        observeUserJoint(requestSent.receiverId) { receiver ->
                            requestsSent.add(
                                FriendRequestJoint(
                                    sender = sender,
                                    receiver = receiver,
                                    request = requestSent
                                )
                            )
                            if (requestsSent.size == requestsSentSnapshot.size())
                                onFriendRequestsSentUpdate(requestsSent.toList())
                        }
                    }
                }
            },
            onFriendRequestsReceivedSnapshotUpdate = { requestsReceivedSnapshot ->
                val requestsReceived = mutableListOf<FriendRequestJoint>()
                requestsReceivedSnapshot.forEach { requestReceivedSnapshot ->
                    val requestRecieved = requestReceivedSnapshot.toObject(FriendRequest::class.java)
                    observeUserJoint(requestRecieved.senderId) { sender ->
                        observeUserJoint(requestRecieved.receiverId) { receiver ->
                            requestsReceived.add(
                                FriendRequestJoint(
                                    sender = sender,
                                    receiver = receiver,
                                    request = requestRecieved
                                )
                            )
                            if (requestsReceived.size == requestsReceivedSnapshot.size())
                                onFriendRequestsSentUpdate(requestsReceived.toList())
                        }
                    }
                }
            }
        )
    }
}