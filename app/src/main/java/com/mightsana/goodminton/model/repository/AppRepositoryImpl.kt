package com.mightsana.goodminton.model.repository

import android.util.Log
import com.google.firebase.firestore.FieldPath
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ListenerRegistration
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.mightsana.goodminton.features.main.model.League
import com.mightsana.goodminton.features.main.model.LeagueJoint
import com.mightsana.goodminton.features.main.model.LeagueParticipant
import com.mightsana.goodminton.features.main.model.LeagueParticipantJoint
import com.mightsana.goodminton.features.main.model.Match
import com.mightsana.goodminton.features.main.model.ParticipantStats
import com.mightsana.goodminton.features.main.model.Role
import com.mightsana.goodminton.features.main.model.Status
import com.mightsana.goodminton.model.repository.friend_requests.FriendRequest
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
    private var userListener: ListenerRegistration? = null
    override fun observeUser(userId: String, onUserUpdate: (MyUser) -> Unit) {
        userListener?.remove()
        userListener = usersCollection
            .document(userId)
            .addSnapshotListener { snapshot, exception ->
                if (exception != null) {
                    return@addSnapshotListener
                }

                var user = MyUser()
                if (snapshot != null && snapshot.exists())
                    user = snapshot.toObject(MyUser::class.java)!!
                onUserUpdate(user)
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
    private var leagueListener: ListenerRegistration? = null
    override fun observeLeague(leagueId: String, onLeagueUpdate: (League) -> Unit) {
        leagueListener?.remove()
        leagueListener = leaguesCollection
            .document(leagueId)
            .addSnapshotListener { snapshot, exception ->
                if (exception != null) {
                    return@addSnapshotListener
                }
                var league = League()
                if (snapshot != null && snapshot.exists())
                    league = snapshot.toObject(League::class.java)!!
                onLeagueUpdate(league)
            }
    }
    override fun observeLeagueJoint(leagueId: String, onLeagueUpdate: (LeagueJoint) -> Unit) {
        leagueListener?.remove()
        leagueListener = leaguesCollection
            .document(leagueId)
            .addSnapshotListener { snapshot, exception ->
                if (exception != null) {
                    return@addSnapshotListener
                }
                if (snapshot != null && snapshot.exists()) {
                    val league = snapshot.toObject(League::class.java)!!
                    usersCollection
                        .document(league.createdById)
                        .addSnapshotListener { userSnapshot, userException ->
                            if (userException != null) {
                                return@addSnapshotListener
                            }
                            if (userSnapshot != null && userSnapshot.exists()) {
                                val leagueJoint = LeagueJoint(
                                    id = league.id,
                                    name = league.name,
                                    matchPoints = league.matchPoints,
                                    private = league.private,
                                    deuceEnabled = league.deuceEnabled,
                                    double = league.double,
                                    fixedDouble = league.fixedDouble,
                                    createdBy = userSnapshot.toObject(MyUser::class.java)!!,
                                    createdAt = league.createdAt
                                )
                                onLeagueUpdate(leagueJoint)
                            }
                        }
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
    private var leagueParticipantsListener: ListenerRegistration? = null
    override fun observeLeagueParticipants(leagueId: String, onParticipantsUpdate: (List<LeagueParticipant>) -> Unit) {
        leagueParticipantsListener?.remove()
        leagueParticipantsListener = leagueParticipantsCollection
            .whereEqualTo("leagueId", leagueId)
            .whereEqualTo("status", Status.Active)
            .addSnapshotListener { snapshot, exception ->
                if (exception != null) {
                    return@addSnapshotListener
                }
                var leagueParticipants = listOf<LeagueParticipant>()
                if (snapshot != null && !snapshot.isEmpty) {
                    leagueParticipants = snapshot.documents.mapNotNull {
                        it.toObject(LeagueParticipant::class.java)
                    }
                }
                onParticipantsUpdate(leagueParticipants)
            }
    }
    override fun observeLeagueParticipantsJoint(leagueId: String, onParticipantsUpdate: (List<LeagueParticipantJoint>) -> Unit) {
        leagueParticipantsListener?.remove()
        leagueParticipantsListener = leagueParticipantsCollection
            .whereEqualTo("leagueId", leagueId)
            .whereEqualTo("status", Status.Active)
            .addSnapshotListener { participantSnapshot, participantException ->
                if (participantException != null) {
                    Log.e("AppRepositoryImpl", "Error observing league participants", participantException)
                    return@addSnapshotListener
                }
                if (participantSnapshot != null && !participantSnapshot.isEmpty) {
                    Log.d("AppRepositoryImpl", "Participant snapshot is not empty")
                    val leagueParticipants = participantSnapshot.map { it.toObject(LeagueParticipant::class.java) }
                    val userIds = leagueParticipants.map { it.userId }.distinct()

                    leaguesCollection
                        .document(leagueId)
                        .addSnapshotListener { leagueSnapshot, leagueException ->
                            if (leagueException != null) {
                                Log.e("AppRepositoryImpl", "Error observing league", leagueException)
                                return@addSnapshotListener
                            }
                            if(leagueSnapshot != null && leagueSnapshot.exists()) {
                                Log.d("AppRepositoryImpl", "League snapshot is not null and exists")
                                val league = leagueSnapshot.toObject(League::class.java)
                                usersCollection
                                    .whereIn(FieldPath.documentId(), userIds)
                                    .addSnapshotListener { userSnapshot, userException ->
                                        if (userException != null) {
                                            Log.e("AppRepositoryImpl", "Error observing users", userException)
                                            return@addSnapshotListener
                                        }
                                        if (userSnapshot != null && !userSnapshot.isEmpty) {
                                            Log.d("AppRepositoryImpl", "User snapshot is not null and not empty")
                                            val userMap = userSnapshot
                                                .documents
                                                .associateBy { it.id }
                                                .mapValues { it.value.toObject(MyUser::class.java) }

                                            val participantsJoin = leagueParticipants.map { participant ->
                                                LeagueParticipantJoint(
                                                    id = participant.id,
                                                    league = league!!,
                                                    user = userMap[participant.userId]!!,
                                                    role = participant.role,
                                                    status = participant.status,
                                                    participateAt = participant.participateAt
                                                )
                                            }
                                            Log.d("AppRepositoryImpl", "Participants joined: $participantsJoin")
                                            onParticipantsUpdate(participantsJoin)
                                        }
                                    }
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
    private var matchesListener: ListenerRegistration? = null
    override fun observeMatches(leagueId: String, onMatchesUpdate: (List<Match>) -> Unit) {
        matchesListener?.remove()
        matchesListener = matchesCollection
            .whereEqualTo("leagueId", leagueId)
            .addSnapshotListener { snapshot, exception ->
                if (exception != null) {
                    return@addSnapshotListener
                }
                var matches = listOf<Match>()
                if (snapshot != null && !snapshot.isEmpty) {
                    matches = snapshot.documents.mapNotNull {
                        it.toObject(Match::class.java)
                    }
                }
                onMatchesUpdate(matches)
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
}