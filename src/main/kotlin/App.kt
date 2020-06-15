import javafx.application.Application
import javafx.application.Application.launch
import javafx.fxml.FXML
import javafx.fxml.FXMLLoader
import javafx.fxml.Initializable
import javafx.scene.Parent
import javafx.scene.Scene
import javafx.scene.control.Label
import javafx.stage.Stage
import java.net.URL
import java.util.*

fun main(args: Array<String>) {
    launch(App::class.java, *args)
}

class App : Application() {
    override fun start(primaryStage: Stage?) {
        if (null == primaryStage) throw IllegalStateException("Something unexpected happened. Call Alex.")

        val root = FXMLLoader.load<Parent>(javaClass.getResource("scene.fxml"))

        val scene = Scene(root)

        primaryStage.title = "Secret Santa Matchmaker"
        primaryStage.scene = scene
        primaryStage.show()
    }

    companion object App {
        /**
         * Try to find matches, otherwise return an empty list.
         */
        fun findMatches(participants: List<Participant>): List<Match> {
            return IntRange(0, participants.size - 1)
                .map {
                    val person = participants[it]

                    val next = if (it == participants.size - 1) {
                        participants[0]
                    } else {
                        participants[it + 1]
                    }

                    if (next.name in person.exclusions) return emptyList()

                    Match(person.name, next.name)
                }
                .toList()
        }
    }
}

/**
 * Most of the UI components.
 */
class MatchController : Initializable {

    @FXML
    private lateinit var label: Label

    override fun initialize(location: URL?, resources: ResourceBundle?) {
        val participants = configureParticipants()
        val matches = findMatches(participants)

        val joinedMatches = matches.joinToString("\n") { "${it.name} gets a gift for ${it.match}" }

        label.text = joinedMatches
    }

    private fun configureParticipants(): List<Participant> {
        return listOf(
            // Alex and Zahrah should not match
            Participant(name = "Alex", exclusions = listOf("Zahrah")),
            Participant(name = "Zahrah", exclusions = listOf("Alex")),

            // Bentley and Yara should not match
            // And Bentley should not get a gift for Darius
            Participant(name = "Bentley", exclusions = listOf("Yara", "Darius")),
            Participant(name = "Yara", exclusions = listOf("Bentley")),

            // Christie and Xavier should not match
            Participant(name = "Christie", exclusions = listOf("Xavier")),
            Participant(name = "Xavier", exclusions = listOf("Christie")),

            // Darius and Whitney should not match
            // And Darius should not get a gift for Bentley
            Participant(name = "Darius", exclusions = listOf("Whitney", "Bentley")),
            Participant(name = "Whitney", exclusions = listOf("Darius")),

            // Eli should not get a gift for Varun or Fionn, etc.
            Participant(name = "Eli", exclusions = listOf("Varun", "Fionn")),
            Participant(name = "Varun", exclusions = listOf("Eli", "Fionn", "Zahrah", "Yara", "Xavier", "Whitney")),
            Participant(name = "Fionn", exclusions = listOf("Varun", "Eli"))
        )
    }

    private fun findMatches(participants: List<Participant>): List<Match> {
        var matches = listOf<Match>()
        // Brute force to find matches
        while (matches.isEmpty()) {
            matches = App.findMatches(participants.shuffled())
        }

        // Give the illusion that the matches are not a cycle
        return matches.shuffled()
    }
}

/**
 * A person participating in the game. [exclusions] are the [name]s of participants that cannot match with this one.
 */
data class Participant(val name: String, var exclusions: List<String> = emptyList())

/**
 * The names of two participants who can give gifts to each other.
 */
data class Match(val name: String, val match: String)
