package moe.yuuta.server.dataverify

import io.vertx.core.logging.LoggerFactory
import java.lang.reflect.Field
import java.lang.reflect.Modifier

object DataVerifier {
    private val logger = LoggerFactory.getLogger(DataVerifier::class.simpleName)

    @JvmStatic
    fun verify (obj: Any): Boolean {
        val fields: Array<Field>? = obj::class.java.declaredFields
        if (fields == null) return true
        for (field in fields) {
            if (Modifier.isFinal(field.modifiers)) continue
            field.isAccessible = true
            try {
                val value = field.get(obj)
                // Object verification
                val nonnull = field.getAnnotation(Nonnull::class.java)
                if (nonnull != null) {
                    if (value == null) {
                        logger.error("${field.name} is required non-null but null")
                        return false
                    }
                    // String verification
                    if (field.type.equals(String::class.java) &&
                            nonnull.nonEmpty && value.toString().equals("")) {
                        logger.error("${field.name} is required non-empty but empty")
                        return false
                    }
                }
                // String verification
                if (field.type.equals(String::class.java)) {
                    val stringIn: StringIn? = field.getAnnotation(StringIn::class.java)
                    if (stringIn != null) {
                        var matched = 0
                        for (target in stringIn.targetValues) {
                            if (target.equals(value)) {
                                matched++
                            }
                        }
                        if (matched == 0) {
                            logger.error("${field.name} is required in ${stringIn.targetValues.size} values but not")
                            return false
                        }
                    }
                }
                // Number verification
                if (value != null) {
                    try {
                        val number = value.toString().toDouble()
                        val numberIn: NumberIn? = field.getAnnotation(NumberIn::class.java)
                        if (numberIn != null) {
                            var matched = 0
                            for (target in numberIn.targetValues) {
                                if (target == number) {
                                    matched++
                                }
                            }
                            if (matched == 0) {
                                logger.error("${field.name} is required in ${numberIn.targetValues.size} values but not")
                                return false
                            }
                        }
                        val greatLessGroup: GreatLessGroup? = field.getAnnotation(GreatLessGroup::class.java)
                        if (greatLessGroup != null) {
                            val greatLesses: Array<GreatLess> = greatLessGroup.targetValues
                            for (greatLess in greatLesses) {
                                if (!verifyGreatLess(field.name, number, greatLess)) return false
                            }
                        } else {
                            val single: GreatLess? = field.getAnnotation(GreatLess::class.java)
                            if (single != null && !verifyGreatLess(field.name, number, single)) return false
                        }
                    } catch (ignored: NumberFormatException) {
                        // Not a number
                    }
                }
            } catch (ignored: IllegalAccessException) {
            }
        }
        // All checks passed
        return true
    }

    private fun verifyGreatLess(name: String, given: Double, greatLess: GreatLess): Boolean {
        val equal = greatLess.equal
        val greater = greatLess.greater
        val lesser = greatLess.lesser
        logger.debug("N=$name,V=$given,T=${greatLess.targetValue},E=$equal,G=$greater,L=$lesser")
        if (greater && lesser && !equal) {
            return false
        }
        if (!greater && !lesser && !equal)
            return false
        if (equal && !greater && !lesser) {
            // Only equal
            if (given != greatLess.targetValue.toDouble()) {
                logger.error("$name is required equals to ${greatLess.targetValue} but is $given")
                return false
            }
        }
        if (greater) {
            if (equal) {
                if (/* Should >=, shouldn't < */ given < greatLess.targetValue) {
                    logger.error("$name is required >= ${greatLess.targetValue} but is $given")
                    return false
                }
            }
            else if (/* Should > */!(given > greatLess.targetValue)) {
                logger.error("$name is required > ${greatLess.targetValue} but is $given")
                return false
            }
        }
        if (lesser) {
            if (equal) {
                if (/* Should <=, shouldn't > */ given > greatLess.targetValue) {
                    logger.error("$name is required < ${greatLess.targetValue} but is $given")
                    return false
                }

            }
            else if (/* Should < */!(given < greatLess.targetValue)) {
                logger.error("$name is required < ${greatLess.targetValue} but is $given")
                return false
            }
        }
        return true
    }
}
