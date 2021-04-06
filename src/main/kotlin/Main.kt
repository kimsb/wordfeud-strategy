object Main {
    @JvmStatic
    fun main(args: Array<String>) {

        //The real deal
        //WFBot(MyBot(), "kimbot")

        //Simulation
        Simulator(
            myBot = MyBot(),
            controlBot = ControlBot(),
        ).simulate(
            rounds = 100
        )
    }
}
