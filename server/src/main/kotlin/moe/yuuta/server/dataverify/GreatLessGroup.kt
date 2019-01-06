package moe.yuuta.server.dataverify

@Target(AnnotationTarget.FIELD)
@Retention(AnnotationRetention.RUNTIME)
annotation class GreatLessGroup(val targetValues: Array<GreatLess>)