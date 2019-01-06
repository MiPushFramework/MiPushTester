package moe.yuuta.server.dataverify

@Target(AnnotationTarget.FIELD)
@Retention(AnnotationRetention.RUNTIME)
annotation class Nonnull(val nonEmpty: Boolean = false)