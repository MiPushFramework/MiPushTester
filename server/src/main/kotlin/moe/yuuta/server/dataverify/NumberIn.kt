package moe.yuuta.server.dataverify

@Target(AnnotationTarget.FIELD)
@Retention(AnnotationRetention.RUNTIME)
annotation class NumberIn(val targetValues: DoubleArray)