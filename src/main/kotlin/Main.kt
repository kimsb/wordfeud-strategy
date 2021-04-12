import Simulation.Simulator

object Main {
    @JvmStatic
    fun main(args: Array<String>) {

        val myBot = MyBot("<botname>")

        //The real deal
        WFApi(bot = myBot)

        //Simulation
        Simulator(
            myBot = myBot,
            controlBot = ControlBot(),
        ).simulate(
            rounds = 100
        )
    }
}
