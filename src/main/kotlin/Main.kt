import Simulation.Simulator

object Main {
    @JvmStatic
    fun main(args: Array<String>) {

        //The real deal
        TheRealDeal(MyBot(), "<yourbot>")

        //Simulation
        Simulator(
            myBot = MyBot(),
            controlBot = ControlBot(),
        ).simulate(
            rounds = 100
        )
    }
}
