package com.mightsana.goodminton

import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext
import androidx.core.content.ContextCompat.getString
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.toRoute
import com.google.firebase.Timestamp
import com.mightsana.goodminton.features.auth.model.authGraph
import com.mightsana.goodminton.features.main.detail.DetailContainer
import com.mightsana.goodminton.features.main.main.MainContainer
import com.mightsana.goodminton.features.main.model.LeagueJoint
import com.mightsana.goodminton.features.main.model.LeagueParticipantJoint
import com.mightsana.goodminton.features.main.model.LeagueStatus
import com.mightsana.goodminton.features.main.model.Match
import com.mightsana.goodminton.features.main.model.MatchStatus
import com.mightsana.goodminton.features.main.model.ParticipantStatsJoint
import com.mightsana.goodminton.features.main.result.LeagueReport
import com.mightsana.goodminton.features.profile.model.profileGraph
import com.mightsana.goodminton.model.repository.users.MyUser
import com.mightsana.goodminton.ui.theme.AppTheme
import kotlinx.serialization.Serializable
import kotlin.random.Random

@Serializable object Maintenance
@Serializable object Main
@Serializable data class League(val id: String)

@Composable
fun MyNavHost(
    navController: NavHostController,
    startDestination: Any,
    authStartDestination: Any
) {
    val context = LocalContext.current

    NavHost(
        navController = navController,
        startDestination = startDestination
    ) {
        composable<Maintenance> {
//            Box(
//                modifier = Modifier.fillMaxSize(),
//                contentAlignment = Alignment.Center
//            ) {
//                Text(
//                    text = "Maintenance 😘",
//                    textAlign = TextAlign.Center,
//                    style = MaterialTheme.typography.displaySmall
//                )
//            }
//            WeatherApp()
//            ExportToPdf()
            val creator = MyUser(
                name = "Creator User",
                nickname = "Creator",
                uid = "creatorId",
                username = "creator.username",
                profilePhotoUrl = "https://lh3.googleusercontent.com/a/ACg8ocJ4pKbU0GcxcGswmbvvxL6gNzr1vL3Jubpl9DSiCcQ9f-Scjg=s96-c"
            )
            val league = LeagueJoint(
                id = "leagueId",
                name = "Dummy League",
                matchPoints = 21,
                private = true,
                deuceEnabled = true,
                double = true,
                fixedDouble = true,
                status = LeagueStatus.Finished,
                createdBy = creator
            )
            val users = mutableListOf<MyUser>()
            users.add(creator)
            for(i in 1..5) {
                users.add(
                    MyUser(
                        name = "Participant $i",
                        nickname = "Participant",
                        uid = "participantId$i",
                        username = "username_$i",
                        profilePhotoUrl = "https://lh3.googleusercontent.com/a/ACg8ocJV_56zsypTdm1mqaa7V0uRhutaOLXqa7_K8-xtpPbPZkuNtA=s96-c"
                    )
                )
            }

            // Participants Size 6
            val participants = mutableListOf<LeagueParticipantJoint>()
            val participantsStats = mutableListOf<ParticipantStatsJoint>()
//            val matchesCount = Random.nextInt(1, 15)
            val matchesCount = 4
            users.forEachIndexed { index, user ->
                participants.add(
                    LeagueParticipantJoint(
                        id = "participantId${index + 1}",
                        league = league,
                        user = user
                    )
                )
                val winsCount = Random.nextInt(0,matchesCount)
                val lossesCount = matchesCount - winsCount
                val pointsScored = matchesCount * (Random.nextInt(0, league.matchPoints))
                val pointsConceded = matchesCount * (Random.nextInt(0, league.matchPoints))
                participantsStats.add(
                    ParticipantStatsJoint(
                        id = "statsId${index + 1}",
                        user = user,
                        league = league,
                        wins = winsCount,
                        losses = lossesCount,
                        pointsScored = pointsScored,
                        pointsConceded = pointsConceded,
                        matches = matchesCount
                    )
                )
            }
            val matches = mutableListOf<Match>()
            repeat(matchesCount) { count ->
                val team1Ids = listOf(
                    participants.random().id,
                    participants.random().id
                )
                val team2Ids = listOf(
                    participants.random().id,
                    participants.random().id
                )
                matches.add(
                    Match(
                        id = "matchId$count",
                        leagueId = league.id,
                        team1Ids = team1Ids,
                        team2Ids = team2Ids,
                        team1Score = Random.nextInt(0, league.matchPoints),
                        team2Score = Random.nextInt(0, league.matchPoints),
                        startedAt = Timestamp.now(),
                        finishedAt = Timestamp.now(),
                        status = MatchStatus.Finished
                    )
                )
            }

            AppTheme {
                LeagueReport(
                    league = league,
                    participants = participants,
                    participantsStats = participantsStats,
                    matches = matches
                )
            }

        }

        authGraph(
            navController = navController,
            startDestination = authStartDestination,
            mainRoute = Main,
            defaultWebClientId = getString(context, R.string.default_web_client_id)
        )

        profileGraph(navController = navController)

        composable<Main> {
            MainContainer(navController = navController)
        }

        composable<League> {
            val league = it.toRoute<League>()
            DetailContainer(
                leagueId = league.id,
                onBack = { navController.navigateUp()}
            )
        }
    }
}