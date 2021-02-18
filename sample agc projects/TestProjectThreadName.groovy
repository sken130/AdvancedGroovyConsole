println "script thread"

3.times{
    Thread.start{
        println "spawned thread"
    }
}