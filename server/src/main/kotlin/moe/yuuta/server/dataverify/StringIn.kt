package moe.yuuta.server.dataverify

@Target(AnnotationTarget.FIELD)
@Retention(AnnotationRetention.RUNTIME)
annotation class StringIn(val targetValues: Array<String>)
