package matt.model.code.jvm.args.gc

import matt.lang.anno.SeeURL

@SeeURL("https://blogs.oracle.com/javamagazine/post/java-garbage-collectors-evolution")
@SeeURL("https://docs.oracle.com/en/java/javase/18/gctuning/available-collectors.html#GUID-C7B19628-27BA-4945-9004-EC0F08C76003")
enum class GarbageCollector {
    Serial,
    Parallel,
    G1,
    ZG,
    Shenandoah
}
