package moe.yuuta.server.dataverify

/**
 * Ask the target field to obey the following rules. It it not obeys, the result will become fail.
 */
@Target(AnnotationTarget.FIELD)
@Retention(AnnotationRetention.RUNTIME)
annotation class GreatLess(val targetValue: Long,
                           val greater: Boolean = false,
                           val lesser: Boolean = false,
                           val equal: Boolean = false)