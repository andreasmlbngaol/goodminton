package com.mightsana.goodminton.model.repository

import android.util.Log
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.FieldPath
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.QuerySnapshot
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.mightsana.goodminton.features.main.model.Invitation
import com.mightsana.goodminton.features.main.model.InvitationJoint
import com.mightsana.goodminton.features.main.model.League
import com.mightsana.goodminton.features.main.model.LeagueJoint
import com.mightsana.goodminton.features.main.model.LeagueParticipant
import com.mightsana.goodminton.features.main.model.LeagueParticipantJoint
import com.mightsana.goodminton.features.main.model.Match
import com.mightsana.goodminton.features.main.model.MatchJoint
import com.mightsana.goodminton.features.main.model.ParticipantStats
import com.mightsana.goodminton.features.main.model.ParticipantStatsJoint
import com.mightsana.goodminton.features.main.model.Role
import com.mightsana.goodminton.features.main.model.Status
import com.mightsana.goodminton.model.repository.friend_requests.FriendRequest
import com.mightsana.goodminton.model.repository.friend_requests.FriendRequestJoint
import com.mightsana.goodminton.model.repository.friends.Friend
import com.mightsana.goodminton.model.repository.friends.FriendJoint
import com.mightsana.goodminton.model.repository.users.MyUser
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext
import javax.inject.Inject

class AppRepositoryImpl @Inject constructor(): AppRepository {
    // Initiate
    override val db: FirebaseFirestore
        get() = Firebase.firestore
    private val appDataCollection = db.collection("appData")
    private val maintenanceDoc = appDataCollection.document("maintenance")
    private val usersCollection = db.collection("users")
    private val friendRequestsCollection = db.collection("friendRequests")
    private val friendsCollection = db.collection("friends")
    private val leaguesCollection = db.collection("leagues")
    private val leagueParticipantsCollection = db.collection("leagueParticipants")
    private val participantStatsCollection = db.collection("participantStats")
    private val matchesCollection = db.collection("matches")
    private val invitationsCollection = db.collection("leagueInvitations")

    private val batchMaxSize = 10

    // App Checking
    override suspend fun isMaintenance(): Boolean =
        maintenanceDoc
            .get()
            .await()
            .getBoolean("isMaintenance") == true

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
    override suspend fun getAllUsers(): List<MyUser> =
        try {
            usersCollection
                .get()
                .await()
                .toObjects(MyUser::class.java)
        } catch (_: Exception) {
            emptyList()
        }
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
    override suspend fun getUser(userId: String): MyUser =
        usersCollection
            .document(userId)
            .get()
            .await()
            .toObject(MyUser::class.java)!!
    override fun observeUserSnapshot(userId: String, onUserSnapshotUpdate: (DocumentSnapshot?) -> Unit) {
        usersCollection
            .document(userId)
            .addSnapshotListener { snapshot, exception ->
                if (exception != null) {
                    return@addSnapshotListener
                }
                onUserSnapshotUpdate(snapshot)
            }
    }
    override fun observeUserJoint(userId: String, onUserUpdate: (MyUser) -> Unit) {
        observeUserSnapshot(userId) {
            val user = it?.toObject(MyUser::class.java)
            onUserUpdate(user ?: MyUser())
        }
    }
    override fun observeUsersSnapshot(userIds: List<String>, onUsersUpdate: (QuerySnapshot?) -> Unit) {
        userIds.chunked(batchMaxSize).map { batch ->
            usersCollection
                .whereIn(FieldPath.documentId(), batch)
                .addSnapshotListener { snapshot, exception ->
                    if (exception != null) {
                        return@addSnapshotListener
                    }
                    onUsersUpdate(snapshot)
                }
        }
    }
    override fun observeUsersJoint(userIds: List<String>, onUsersUpdate: (List<MyUser>) -> Unit) {
        observeUsersSnapshot(userIds) { snapshot ->
            val users = mutableListOf<MyUser>()
            snapshot?.forEach { userSnapshot ->
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
    override suspend fun createNewLeague(league: League) {
        val newLeagueDoc = leaguesCollection.document()
        val generatedId = newLeagueDoc.id

        newLeagueDoc
            .set(league.copy(id = generatedId))
            .await()

        addParticipant(
            leagueId = generatedId,
            userId = league.createdById,
            role = Role.Creator
        )

        addParticipantStats(
            leagueId = generatedId,
            userId = league.createdById
        )
    }

    // Retrieve League Data
    override suspend fun getLeague(leagueId: String): League =
        leaguesCollection
            .document(leagueId)
            .get()
            .await()
            .toObject(League::class.java)
            ?: League()
    override suspend fun getLeagueJoint(leagueId: String): LeagueJoint {
        val league = getLeague(leagueId)
        val creator = getUser(league.createdById)
        return LeagueJoint(
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
    }
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
            e.printStackTrace()
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
            e.printStackTrace()
            emptyList()
        }
    override suspend fun getLeagueJointsByIds(ids: List<String>): List<LeagueJoint> =
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
            val leagues = leagueDeferreds.flatMap { it.await() }
            val creators = getUsersByIds(leagues.map { it.createdById })
                .associateBy { it.uid }
            leagues.map {
                LeagueJoint(
                    id = it.id,
                    name = it.name,
                    matchPoints = it.matchPoints,
                    private = it.private,
                    deuceEnabled = it.deuceEnabled,
                    double = it.double,
                    fixedDouble = it.fixedDouble,
                    createdBy = creators[it.createdById]!!,
                    createdAt = it.createdAt
                )
            }
        }
    override suspend fun getUserAndPublicLeagues(userId: String): List<LeagueJoint> {
        val userLeagueIds = getLeagueIdsByUserId(userId).toSet()
        val publicLeagueIds = getPublicLeagueIds().toSet()
        val unionLeagueIds = userLeagueIds.union(publicLeagueIds).toList()
        return getLeagueJointsByIds(unionLeagueIds)
    }
    override fun observeLeagueSnapshot(leagueId: String, onLeagueSnapshotUpdate: (DocumentSnapshot?) -> Unit) {
        leaguesCollection
            .document(leagueId)
            .addSnapshotListener { snapshot, exception ->
                if (exception != null) {
                    return@addSnapshotListener
                }
                onLeagueSnapshotUpdate(snapshot)
            }
    }
    override fun observeLeagueJoint(leagueId: String, onLeagueUpdate: (LeagueJoint) -> Unit) {
        observeLeagueSnapshot(leagueId) {
            CoroutineScope(Dispatchers.IO).launch {
                val league = it?.toObject(League::class.java)
                val creator = league?.let { it1 -> getUser(it1.createdById) }

                launch(Dispatchers.Main) {
                    val result = league?.let {
                        LeagueJoint(
                            id = league.id,
                            name = league.name,
                            matchPoints = league.matchPoints,
                            private = league.private,
                            deuceEnabled = league.deuceEnabled,
                            double = league.double,
                            fixedDouble = league.fixedDouble,
                            createdBy = creator!!,
                            createdAt = league.createdAt
                        )
                    }
                    onLeagueUpdate(result ?: LeagueJoint())
                }
            }
        }
    }

    // Edit League Data
    override suspend fun updateLeagueDiscipline(leagueId: String, double: Boolean) {
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
                val invitationsQuery = invitationsCollection.whereEqualTo("leagueId", leagueId).get().await()

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

                    // Hapus semua undangan
                    for (document in invitationsQuery.documents) {
                        batch.delete(document.reference)
                    }
                }.await()
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    // Retrieve League Participant Data
    override suspend fun getParticipantsJoint(leagueId: String): List<LeagueParticipantJoint> {
        val participants = leagueParticipantsCollection
            .whereEqualTo("leagueId", leagueId)
            .whereEqualTo("status", Status.Active)
            .get()
            .await()
            .toObjects(LeagueParticipant::class.java)
        val league = getLeagueJoint(leagueId)
        val users = getUsersByIds(participants.map { it.userId }).associateBy { it.uid }
        return participants.map {
            LeagueParticipantJoint(
                id = it.id,
                league = league,
                user = users[it.userId]!!,
                role = it.role,
                status = it.status,
                participateAt = it.participateAt
            )
        }
    }
    override fun observeLeagueParticipantsSnapshot(leagueId: String, onParticipantsSnapshotUpdate: (QuerySnapshot?) -> Unit) {
        leagueParticipantsCollection
            .whereEqualTo("leagueId", leagueId)
            .whereEqualTo("status", Status.Active)
            .addSnapshotListener { snapshot, exception ->
                if (exception != null) {
                    return@addSnapshotListener
                }
                onParticipantsSnapshotUpdate(snapshot)
            }
    }
    override fun observeLeagueParticipantsJoint(leagueId: String, onParticipantsUpdate: (List<LeagueParticipantJoint>) -> Unit) {
        observeLeagueParticipantsSnapshot(leagueId) { participantsSnapshot ->
            CoroutineScope(Dispatchers.IO).launch {
                val leagueJoint = getLeagueJoint(leagueId)
                val participants = participantsSnapshot?.map { it.toObject(LeagueParticipant::class.java) }
                val users = participants?.let { ptcs ->
                    getUsersByIds(ptcs.map { it.userId })
                        .associateBy { it.uid }
                }

                launch(Dispatchers.Main) {
                    val result = participants?.map { participant ->
                        LeagueParticipantJoint(
                            id = participant.id,
                            league = leagueJoint,
                            user = users?.get(participant.userId)!!,
                            role = participant.role,
                            status = participant.status,
                            participateAt = participant.participateAt
                        )
                    }
                    onParticipantsUpdate(result ?: emptyList())
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
    override suspend fun addParticipant(leagueId: String, userId: String, role: Role) {
        val newParticipantDoc = leagueParticipantsCollection.document()
        val generatedId = newParticipantDoc.id

        newParticipantDoc
            .set(
                LeagueParticipant(
                    id = generatedId,
                    leagueId = leagueId,
                    userId = userId,
                    role = role
                )
            )
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
    override suspend fun addParticipantStats(leagueId: String, userId: String) {
        val newStatsDoc = participantStatsCollection.document()
        val generatedId = newStatsDoc.id

        newStatsDoc
            .set(
                ParticipantStats(
                    id = generatedId,
                    leagueId = leagueId,
                    userId = userId
                )
            )
    }

    // Retrieve Matches Data
    override fun observeMatchesSnapshot(leagueId: String, onMatchesSnapshotUpdate: (QuerySnapshot?) -> Unit) {
        matchesCollection
            .whereEqualTo("leagueId", leagueId)
            .addSnapshotListener { snapshot, exception ->
                if (exception != null) {
                    return@addSnapshotListener
                }
                onMatchesSnapshotUpdate(snapshot)
            }
    }
    override fun observeMatchesJoint(leagueId: String, onMatchesUpdate: (List<MatchJoint>) -> Unit) {
        observeMatchesSnapshot(leagueId) { matchesSnapshot ->
            CoroutineScope(Dispatchers.IO).launch {
                val matches = matchesSnapshot?.map { it.toObject(Match::class.java) }
                val leagueJoint = getLeagueJoint(leagueId)

                val matchesJoint = matches?.map { match ->
                    val team1Users = getUsersByIds(match.team1Ids)
                    val team2Users = getUsersByIds(match.team2Ids)

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
                }

                launch(Dispatchers.Main) {
                    onMatchesUpdate(matchesJoint ?: emptyList())
                }
            }
        }
    }

    // Retrieve Friends Data
    override fun observeFriendsSnapshot(userId: String, onFriendsSnapshotUpdate: (QuerySnapshot?) -> Unit) {
        friendsCollection
            .whereArrayContains("usersIds", userId)
            .addSnapshotListener { snapshot, exception ->
                if (exception != null) {
                    return@addSnapshotListener
                }
                onFriendsSnapshotUpdate(snapshot)
            }
    }
    override fun observeFriendsJoint(userId: String, onFriendsUpdate: (List<FriendJoint>) -> Unit) {
        observeFriendsSnapshot(userId) { friendsSnapshot ->
            CoroutineScope(Dispatchers.IO).launch {
                val friends = friendsSnapshot?.map { it.toObject(Friend::class.java) }
                val allUserIds = friends?.let {
                    friends.flatMap { it.usersIds }.distinct().filterNot { it == userId }
                }
                val usersMap = allUserIds?.let { ids -> getUsersByIds(ids).associateBy { it.uid } }
                val friendsJoint = friends?.mapNotNull { friend ->
                    val otherUserId = friend.usersIds.firstOrNull { it != userId }
                    otherUserId?.let { otherId ->
                        val user = usersMap?.get(otherId)
                        user?.let {
                            FriendJoint(
                                id = friend.id,
                                user = it,
                                startedAt = friend.startedAt
                            )
                        }
                    }
                }
                launch(Dispatchers.Main) {
                    onFriendsUpdate(friendsJoint ?: emptyList())
                }
            }
        }
    }

    // Friend Action
    override suspend fun addFriend(userIds: List<String>) {
        val newFriendsDoc = friendsCollection.document()
        val generatedId = newFriendsDoc.id

        newFriendsDoc
            .set(
                Friend(
                    id = generatedId,
                    usersIds = userIds
                )
            )
            .await()
    }
    override suspend fun deleteFriend(id: String) {
        friendsCollection
            .document(id)
            .delete()
            .await()
    }

    // Retrieve Friend Requests Data
    override fun observeFriendRequestsSnapshot(
        userId: String,
        onFriendRequestsSentSnapshotUpdate: (QuerySnapshot?) -> Unit,
        onFriendRequestsReceivedSnapshotUpdate: (QuerySnapshot?) -> Unit
    ) {
        friendRequestsCollection
            .whereEqualTo("senderId", userId)
            .addSnapshotListener { snapshot, exception ->
                if (exception != null) {
                    return@addSnapshotListener
                }
                onFriendRequestsSentSnapshotUpdate(snapshot)
            }

        friendRequestsCollection
            .whereEqualTo("receiverId", userId)
            .addSnapshotListener { snapshot, exception ->
                if (exception != null) {
                    return@addSnapshotListener
                }
                onFriendRequestsReceivedSnapshotUpdate(snapshot)
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
                requestsSentSnapshot?.forEach { requestSentSnapshot ->
                    val requestSent = requestSentSnapshot.toObject(FriendRequest::class.java)
                    observeUserJoint(requestSent.senderId) { sender ->
                        observeUserJoint(requestSent.receiverId) { receiver ->
                            requestsSent.add(
                                FriendRequestJoint(
                                    id = requestSent.id,
                                    sender = sender,
                                    receiver = receiver,
                                    requestedAt = requestSent.requestedAt
                                )
                            )
                            if (requestsSent.size == requestsSentSnapshot.size())
                                onFriendRequestsSentUpdate(requestsSent.toList())
                        }
                    }
                }
                if(requestsSentSnapshot?.size() == 0)
                    onFriendRequestsSentUpdate(emptyList())
            },
            onFriendRequestsReceivedSnapshotUpdate = { requestsReceivedSnapshot ->
                val requestsReceived = mutableListOf<FriendRequestJoint>()
                requestsReceivedSnapshot?.forEach { requestReceivedSnapshot ->
                    val requestReceived = requestReceivedSnapshot.toObject(FriendRequest::class.java)
                    observeUserJoint(requestReceived.senderId) { sender ->
                        observeUserJoint(requestReceived.receiverId) { receiver ->
                            requestsReceived.add(
                                FriendRequestJoint(
                                    id = requestReceived.id,
                                    sender = sender,
                                    receiver = receiver,
                                    requestedAt = requestReceived.requestedAt
                                )
                            )
                            if (requestsReceived.size == requestsReceivedSnapshot.size())
                                onFriendRequestsReceivedUpdate(requestsReceived.toList())
                        }
                    }
                }
                if(requestsReceivedSnapshot?.size() == 0)
                    onFriendRequestsReceivedUpdate(emptyList())
            }
        )
    }

    override suspend fun deleteFriendRequest(requestId: String) {
        friendRequestsCollection
            .document(requestId)
            .delete()
            .await()
    }
    override suspend fun acceptFriendRequest(requestId: String, userIds: List<String>) {
        deleteFriendRequest(requestId)
        addFriend(userIds)
    }
    override suspend fun createFriendRequest(senderId: String, receiverId: String) {
        val newFriendRequestDoc = friendRequestsCollection.document()
        val generatedId = newFriendRequestDoc.id

        newFriendRequestDoc
            .set(
                FriendRequest(
                    id = generatedId,
                    senderId = senderId,
                    receiverId = receiverId
                )
            )
    }

    // Retrieve League Invitation Data
    override fun observeLeagueInvitationsSentSnapshot(leagueId: String, onInvitationsSentSnapshotUpdate: (QuerySnapshot?) -> Unit) {
        invitationsCollection
            .whereEqualTo("leagueId", leagueId)
            .addSnapshotListener { snapshot, exception ->
                if (exception != null) {
                    return@addSnapshotListener
                }
                onInvitationsSentSnapshotUpdate(snapshot)
            }
    }
    override fun observeLeagueInvitationsSentJoint(leagueId: String, onInvitationsSentUpdate: (List<InvitationJoint>) -> Unit) {
        observeLeagueInvitationsSentSnapshot(leagueId) { invitationsSnapshot ->
            CoroutineScope(Dispatchers.IO).launch {
                val leagueJoint = getLeagueJoint(leagueId)
                val invitations = invitationsSnapshot?.map { it.toObject(Invitation::class.java) }
                val senders = invitations?.let { invs ->
                    getUsersByIds(invs.map { it.senderId })
                        .associateBy { it.uid }
                }
                val receivers = invitations?.let { invs ->
                    getUsersByIds(invs.map { it.receiverId })
                        .associateBy { it.uid }
                }
                launch(Dispatchers.Main) {
                    val result = invitations?.map { invitation ->
                        InvitationJoint(
                            id = invitation.id,
                            sender = senders?.get(invitation.senderId)!!,
                            receiver = receivers?.get(invitation.receiverId)!!,
                            league = leagueJoint,
                            invitedAt = invitation.invitedAt
                        )
                    }
                    onInvitationsSentUpdate(result ?: emptyList())
                }
            }
        }
    }
    override fun observeLeagueInvitationsReceivedSnapshot(userId: String, onInvitationsReceivedSnapshotUpdate: (QuerySnapshot?) -> Unit) {
        invitationsCollection
            .whereEqualTo("receiverId", userId)
            .addSnapshotListener { snapshot, exception ->
                if (exception != null) {
                    return@addSnapshotListener
                }
                onInvitationsReceivedSnapshotUpdate(snapshot)
            }
    }
    override fun observeLeagueInvitationsReceivedJoint(userId: String, onInvitationsReceivedUpdate: (List<InvitationJoint>) -> Unit) {
        observeLeagueInvitationsReceivedSnapshot(userId) { invitationsSnapshot ->
            CoroutineScope(Dispatchers.IO).launch {
                val receiver = getUser(userId)
                val invitations = invitationsSnapshot?.map { it.toObject(Invitation::class.java) }
                Log.d("LeagueInvitation", "invitations: $invitations")
                val senders = invitations?.let { invs ->
                    getUsersByIds(invs.map { it.senderId })
                        .associateBy { it.uid }
                }
                val leagueJoints = invitations?.let { invs ->
                    getLeagueJointsByIds(invs.map { it.leagueId })
                        .associateBy { it.id }
                }
                val participants = mutableMapOf<String, List<LeagueParticipantJoint>>()
                invitations?.let { invs ->
                    invs.map { it.leagueId }.forEach { leagueId ->
                        participants[leagueId] = getParticipantsJoint(leagueId)
                    }
                }
                launch(Dispatchers.Main) {
                    val result = invitations?.map { invitation ->
                        InvitationJoint(
                            id = invitation.id,
                            sender = senders?.get(invitation.senderId)!!,
                            receiver = receiver,
                            league = leagueJoints?.get(invitation.leagueId)!!,
                            participants = participants[invitation.leagueId]!!,
                            invitedAt = invitation.invitedAt
                        )
                    }
                    onInvitationsReceivedUpdate(result ?: emptyList())
                }
            }
        }
    }

    // League Invitation Action
    override suspend fun addInvitation(leagueId: String, senderId: String, receiverId: String) {
        val newInvitationDoc = invitationsCollection.document()
        val generatedId = newInvitationDoc.id

        newInvitationDoc
            .set(
                Invitation(
                    id = generatedId,
                    senderId = senderId,
                    receiverId = receiverId,
                    leagueId = leagueId
                )
            )
            .await()
    }
    override suspend fun deleteInvitation(invitationId: String) {
        invitationsCollection
            .document(invitationId)
            .delete()
            .await()
    }
    override suspend fun acceptInvitation(invitationId: String, leagueId: String, userId: String) {
        addParticipant(leagueId, userId)
        addParticipantStats(leagueId, userId)
        deleteInvitation(invitationId)
    }

    // Retrieve League Participants Stats Data
    override fun observeParticipantsStatsSnapshot(leagueId: String, onParticipantsStatsSnapshotUpdate: (QuerySnapshot?) -> Unit) {
        participantStatsCollection
            .whereEqualTo("leagueId", leagueId)
            .addSnapshotListener { snapshot, exception ->
                if (exception != null) {
                    return@addSnapshotListener
                }
                onParticipantsStatsSnapshotUpdate(snapshot)
            }
    }
    override fun observeParticipantsStatsJoint(leagueId: String, onParticipantsStatsUpdate: (List<ParticipantStatsJoint>) -> Unit) {
        observeParticipantsStatsSnapshot(leagueId) { participantsStatsSnapshot ->
            CoroutineScope(Dispatchers.IO).launch {
                val participants = participantsStatsSnapshot?.toObjects(ParticipantStats::class.java)
                val leagueJoint = getLeagueJoint(leagueId)
                val users = participants?.let { ptc -> getUsersByIds(ptc.map { it.userId }).associateBy { it.uid } }

                launch(Dispatchers.Main) {
                    val result = participants?.map { participant ->
                        ParticipantStatsJoint(
                            id = participant.id,
                            user = users?.get(participant.userId)!!,
                            league = leagueJoint,
                            wins = participant.wins,
                            losses = participant.losses,
                            pointsScored = participant.pointsScored,
                            pointsConceded = participant.pointsConceded,
                            matches = participant.matches
                        )
                    }
                    onParticipantsStatsUpdate(result ?: emptyList())
                }
            }
        }
    }
}