package com.mightsana.goodminton.model.repository

import android.util.Log
import com.google.firebase.firestore.FieldPath
import com.google.firebase.firestore.ListenerRegistration
import com.google.firebase.firestore.QuerySnapshot
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.firestore.toObject
import com.google.firebase.ktx.Firebase
import com.mightsana.goodminton.features.main.model.League
import com.mightsana.goodminton.features.main.model.LeagueParticipants
import com.mightsana.goodminton.features.main.model.Role
import com.mightsana.goodminton.features.main.model.Status
import com.mightsana.goodminton.model.repository.friends.Friend
import com.mightsana.goodminton.model.repository.friend_requests.FriendRequest
import com.mightsana.goodminton.model.repository.friends.Friendship
import com.mightsana.goodminton.model.repository.users.MyUser
import kotlinx.coroutines.async
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.tasks.await
import javax.inject.Inject

@Suppress("unused")
open class AppRepository @Inject constructor() {
    private val db = Firebase.firestore
    private val usersCollection = db.collection("users")
    private val friendRequestsCollection = db.collection("friendRequests")
    private val friendsCollection = db.collection("friends")
    private val leaguesCollection = db.collection("leagues")
    private val leagueParticipantsCollection = db.collection("leagueParticipants")

    fun createUser(userData: MyUser) {
        usersCollection
            .document(userData.uid)
            .set(userData)
    }

    fun updateUser(
        uid: String,
        updates: Map<String, Any?>
    ) {
        usersCollection
            .document(uid)
            .update(updates)
    }

    suspend fun getUsernameByUid(uid: String): String {
        val query = usersCollection
            .whereEqualTo("uid", uid)
            .get()
            .await()
        return query.documents[0].getString("username")!!
    }

    suspend fun searchByUsername(
        currentUsername: String,
        usernameToSearch: String
    ): List<MyUser>? {
        val usernameFiltered = usernameToSearch.lowercase().trim()
        Log.d("OneRepository", usernameFiltered)
        Log.d("OneRepository", currentUsername)
        val query =  usersCollection
            .whereGreaterThanOrEqualTo("username", usernameFiltered) // Pencarian awalan
            .whereLessThanOrEqualTo("username", "$usernameFiltered\uf8ff") // Batas pencarian
            .whereNotEqualTo("username", currentUsername)
            .get()
            .await()

        Log.d("OneRepository", "masuk")

        if(query.isEmpty || usernameFiltered.isEmpty()) return null

        Log.d("OneRepository", "not null")

        return query.documents.mapNotNull {
            it.toObject(MyUser::class.java)
        }
    }
    suspend fun isUserRegistered(userId: String): Boolean {
        return usersCollection
            .document(userId)
            .get()
            .await()
            .exists()
    }
    suspend fun isUsernameAvailable(username: String): Boolean {
        return usersCollection
            .whereEqualTo("username", username)
            .get()
            .await()
            .isEmpty
    }
    suspend fun getUserProfile(uid: String): MyUser {
        return usersCollection
            .document(uid)
            .get()
            .await()
            .toObject(MyUser::class.java)!!
    }
    suspend fun isFriendRequestSent(
        currentUserId: String,
        uid: String,
        onDocumentUpdate: suspend (String?, Boolean) -> Unit
    ) {
        val query =  friendRequestsCollection
            .whereEqualTo("senderId", currentUserId)
            .whereEqualTo("receiverId", uid)
            .get()
            .await()

        val isFriendRequestSent = !query.isEmpty

        onDocumentUpdate(
            if(isFriendRequestSent) query.documents[0].id else null,
            isFriendRequestSent
        )
    }
    suspend fun isFriendRequestReceived(
        currentUserId: String,
        uid: String,
        onDocumentUpdate: (String?, Boolean) -> Unit
    ) {
        val query =  friendRequestsCollection
            .whereEqualTo("senderId", uid)
            .whereEqualTo("receiverId", currentUserId)
            .get()
            .await()

        val isFriendRequestReceived = !query.isEmpty

        onDocumentUpdate(
            if(isFriendRequestReceived) query.documents[0].id else null,
            isFriendRequestReceived
        )
    }
    suspend fun getFriendList(uid: String): List<Friend>? {
        val friendshipSnapshot = getFriendshipSnapshot(uid)

        if(friendshipSnapshot.isEmpty) return null

        return friendshipSnapshot.documents.mapNotNull { document ->
            val users = document.get("users") as List<*>
            val friendUid = users.first { it != uid }
            Friend(
                friendUid as String,
                document.getTimestamp("startedAt")!!
            )
        }
    }
    suspend fun getFriendCount(uid: String): Int = getFriendshipSnapshot(uid).size()

    fun observeFriendRequestsReceived(
        uid: String,
        onDocumentUpdate: (List<FriendRequest>?) -> Unit
    ) {
        friendRequestsCollection
            .whereEqualTo("receiverId", uid)
            .addSnapshotListener { snapshot, exception ->
                if (exception != null) {
                    return@addSnapshotListener
                }
                if (snapshot != null && !snapshot.isEmpty) {
                    onDocumentUpdate(
                        snapshot.documents.mapNotNull {
                            it.toObject(FriendRequest::class.java)
                        }
                    )
                } else {
                    onDocumentUpdate(null)
                }
            }
    }


    fun observeFriendRequestReceivedCount(
        uid: String,
        onDocumentUpdate: (Int) -> Unit
    ) {
        friendRequestsCollection
            .whereEqualTo("receiverId", uid)
            .addSnapshotListener { snapshot, exception ->
                if (exception != null) {
                    return@addSnapshotListener
                }
                if (snapshot != null && !snapshot.isEmpty) {
                    onDocumentUpdate(snapshot.size())
                } else {
                    onDocumentUpdate(0)
                }
            }
    }

    private suspend fun getFriendshipSnapshot(uid: String): QuerySnapshot {
        return friendsCollection
            .whereArrayContains("users", uid)
            .get()
            .await()
    }
    suspend fun isFriend(
        currentUserId: String,
        targetUid: String,
        onDocumentUpdate: suspend (String?, Boolean) -> Unit
    ) {
        val query1 = getFriendshipSnapshot(currentUserId)

        val matchedDocument = query1.documents.find {doc ->
            val friendship = doc.toObject<Friendship>()
            friendship?.users?.contains(targetUid) == true
        }

        if(matchedDocument != null) {
            onDocumentUpdate(
                matchedDocument.id,
                true
            )
        } else {
            onDocumentUpdate(null, false)
        }
    }

    suspend fun addFriendRequest(currentUserId: String, uid: String) {
        val available = friendRequestsCollection
            .whereEqualTo("senderId", currentUserId)
            .whereEqualTo("receiverId", uid)
            .get()
            .await()
            .isEmpty
        if(available)
            friendRequestsCollection
                .add(
                    FriendRequest(
                        senderId = currentUserId,
                        receiverId = uid
                    )
                )
                .await()
    }

    suspend fun removeFriendRequest(friendRequestId: String) {
        friendRequestsCollection
            .document(friendRequestId)
            .delete()
            .await()
    }

    suspend fun unfriend(friendId: String) {
        friendsCollection
            .document(friendId)
            .delete()
            .await()
    }


    suspend fun getUser(uid: String, onUserUpdate: (MyUser) -> Unit) {
        onUserUpdate(
            usersCollection
                .document(uid)
                .get()
                .await()
                .toObject(MyUser::class.java)!!
        )
    }

    private var userListener: ListenerRegistration? = null

    fun observeUser(uid: String, onUserUpdate: (MyUser) -> Unit) {
        userListener?.remove()

        userListener = usersCollection
            .document(uid)
            .addSnapshotListener { snapshot, exception ->
                if (exception != null) {
                    return@addSnapshotListener
                }
                if (snapshot != null && snapshot.exists()) {
                    val user = snapshot.toObject(MyUser::class.java)!!
                    Log.d("OneRepository", user.toString())
                    onUserUpdate(user)
                } else {
                    Log.d("OneRepository", "User not found")
                    onUserUpdate(MyUser())
                }
            }
    }

    suspend fun acceptFriendRequest(
        currentUserId: String,
        uid: String
    ) {
        friendsCollection
            .add(
                Friendship(
                    users = listOf(currentUserId, uid)
                )
            )
            .await()

        removeFriendRequest(
            friendRequestsCollection
                .whereEqualTo("senderId", uid)
                .whereEqualTo("receiverId", currentUserId)
                .get()
                .await()
                .documents[0]
                .id
        )
    }

    suspend fun declineFriendRequest(
        currentUserId: String,
        uid: String
    ) {
        removeFriendRequest(
            friendRequestsCollection
                .whereEqualTo("senderId", uid)
                .whereEqualTo("receiverId", currentUserId)
                .get()
                .await()
                .documents[0]
                .id
        )
    }

    fun addLeagueParticipant(
        leagueId: String,
        leagueParticipant: LeagueParticipants
    ) {
        val newParticipantDoc = leagueParticipantsCollection.document()
        val generatedId = newParticipantDoc.id

        newParticipantDoc
            .set(leagueParticipant.copy(id = generatedId))
    }

    suspend fun createNewLeague(
        league: League
    ) {
        val newLeagueDoc = leaguesCollection.document()
        val generatedId = newLeagueDoc.id

        newLeagueDoc
            .set(league.copy(id = generatedId))
            .await()
        
        addLeagueParticipant(
            generatedId,
            LeagueParticipants(
                leagueId = generatedId,
                userId = league.createdById,
                role = Role.Creator
            )
        )
    }

    private suspend fun getLeagueIdsByUserId(userId: String): List<String> {
        return try {
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
    }

    private suspend fun getLeaguesByIds(leagueIds: List<String>): List<League> {
        val batchSize = 10
        val batches = leagueIds.chunked(batchSize)

        Log.d("OneRepository", leagueIds.toString())
        return coroutineScope {
            val leagueDeferreds = batches.map { batch ->
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
    }

    suspend fun getUsersByIds(userIds: List<String>): List<MyUser> {
        val batchSize = 10
        val batches = userIds.chunked(batchSize)

        return coroutineScope {
            val userDeferreds = batches.map { batch ->
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
    }

    suspend fun getLeaguesByUserId(userId: String): List<League> {
        return getLeaguesByIds(getLeagueIdsByUserId(userId))
    }

    private var leagueInfoListener: ListenerRegistration? = null

    fun observeLeagueInfo(
        leagueId: String,
        onLeagueInfoUpdate: (League) -> Unit
    ) {
        leagueInfoListener?.remove()

        leagueInfoListener = leaguesCollection
            .document(leagueId)
            .addSnapshotListener { snapshot, exception ->
                if (exception != null) {
                    return@addSnapshotListener
                }
                if (snapshot != null && snapshot.exists()) {
                    val league = snapshot.toObject(League::class.java)!!
                    onLeagueInfoUpdate(league)
                } else {
                    Log.d("OneRepository", "User not found")
                    onLeagueInfoUpdate(League())
                }
            }
    }

    private var leagueParticipantsListener: ListenerRegistration? = null

    fun observeLeagueParticipants(
        leagueId: String,
        onLeagueParticipantsUpdate: (List<LeagueParticipants>) -> Unit
    ) {
        leagueParticipantsListener?.remove()

        leagueParticipantsListener = leagueParticipantsCollection
            .whereEqualTo("leagueId", leagueId)
            .whereEqualTo("status", Status.Active)
            .addSnapshotListener { snapshot, exception ->
                if (exception != null) {
                    return@addSnapshotListener
                }
                if (snapshot != null && !snapshot.isEmpty) {
                    val leagueParticipants = snapshot.documents.mapNotNull {
                        it.toObject(LeagueParticipants::class.java)
                    }

                    onLeagueParticipantsUpdate(leagueParticipants)
                } else {
                    onLeagueParticipantsUpdate(emptyList())
                }
            }

    }
}
