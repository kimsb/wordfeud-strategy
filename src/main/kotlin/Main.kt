import mybot.MyBot
import simulation.Simulator
import strategies.DontUseBlank
import strategies.MaximizeBingoLeave
import strategies.MaximizeScore
import strategies.MaximizeVowelRatioLeave

object Main {
    @JvmStatic
    fun main(args: Array<String>) {

        val myBot = MyBot(
            name = "bovimbot",
            strategies = listOf(
                MaximizeScore(60.0),
                MaximizeBingoLeave(10.0),
                MaximizeVowelRatioLeave(15.0)
            ),
            penaltyStrategy = DontUseBlank(15.0)
        )

        //The real deal
        //WFApi(bot = myBot)

        //TODO lage egen package med strategier, og sende inn strategier som en liste til boten, slik kan jeg lettere teste en og en strategi.
        //strategiene burde kunne vektes..? f.eks strategi [SCORE] vektet med 100 -> bot bryr seg kun om oppnådd score
        //bør ikke finne alle moves for motstander, det tar for lang tid... burde heller sjekke de faktiske åpningene osv...
        //da kan f.eks også sjekke: har motstander W? og kan sjekke for alle mine legg, men hvordan sjekker jeg f.eks om WOK kan legges dersom motstander har WO..?

        //Simulation
        Simulator(
            bot = myBot,
            controlBot = ControlBot(listOf(MaximizeScore(100.0)))
        ).simulate(
            rounds = 100
        )
    }
}
