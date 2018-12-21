package moe.yuuta.server.dataverify;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;

import io.vertx.core.logging.Logger;
import io.vertx.core.logging.LoggerFactory;

public class DataVerifier {
    private static final Logger logger = LoggerFactory.getLogger(DataVerifier.class.getSimpleName());

    public static boolean verify (Object object) {
        Field[] fields = object.getClass().getDeclaredFields();
        if (fields == null) return true;
        for (Field field : fields) {
            if (Modifier.isFinal(field.getModifiers())) continue;
            field.setAccessible(true);
            try {
                Object value = field.get(object);
                // Object verification
                Nonnull nonnull = field.getAnnotation(Nonnull.class);
                if (nonnull != null) {
                    if (value == null) {
                        logger.error(field.getName() + " is required non-null but null");
                        return false;
                    }
                    // String verification
                    if (field.getType().equals(String.class) &&
                            nonnull.nonEmpty() && value.toString().equals("")) {
                        logger.error(field.getName() + " is required non-empty but empty");
                        return false;
                    }
                }
                // String verification
                if (field.getType().equals(String.class)) {
                    StringIn stringIn = field.getAnnotation(StringIn.class);
                    if (stringIn != null) {
                        int matched = 0;
                        for (String target : stringIn.value()) {
                            if (target.equals(value)) {
                                matched++;
                            }
                        }
                        if (matched == 0) {
                            logger.error(field.getName() + " is required in " + stringIn.value().length + " values but not");
                            return false;
                        }
                    }
                }
                // Number verification
                if (value != null) {
                    try {
                        double number = Double.parseDouble(value.toString());
                        NumberIn numberIn = field.getAnnotation(NumberIn.class);
                        if (numberIn != null) {
                            int matched = 0;
                            for (double target : numberIn.value()) {
                                if (target == number) {
                                    matched++;
                                }
                            }
                            if (matched == 0) {
                                logger.error(field.getName() + " is required in " + numberIn.value().length + " values but not");
                                return false;
                            }
                        }
                        GreatLessGroup greatLessGroup = field.getAnnotation(GreatLessGroup.class);
                        if (greatLessGroup != null) {
                            GreatLess[] greatLesses = greatLessGroup.value();
                            for (GreatLess greatLess : greatLesses) {
                                if (!verifyGreatLess(field.getName(), number, greatLess)) return false;
                            }
                        } else {
                            GreatLess single = field.getAnnotation(GreatLess.class);
                            if (!verifyGreatLess(field.getName(), number, single)) return false;
                        }
                    } catch (NumberFormatException|NullPointerException ignored) {
                        // Not a number
                    }
                }
            } catch (IllegalAccessException ignored) {
            }
        }
        // All checks passed
        return true;
    }

    private static boolean verifyGreatLess (String name, double given, GreatLess greatLess) {
        boolean equal = greatLess.equal();
        boolean greater = greatLess.greater();
        boolean lesser = greatLess.lesser();
        logger.debug("N=" + name + ",V=" + given + ",T=" + greatLess.value() + ",E=" + equal + ",G=" + greater + ",L=" + lesser);
        if (greater && lesser && !equal) {
            return false;
        }
        if (!greater && !lesser && !equal)
            return false;
        if (equal && !greater && !lesser) {
            // Only equal
            if (given != greatLess.value()) {
                logger.error(name + " is required equals to " + greatLess.value() + " but is " + given);
                return false;
            }
        }
        if (greater) {
            if (equal) {
                if (/* Should >=, shouldn't < */ given < greatLess.value()) {
                    logger.error(name + " is required >= " + greatLess.value() + " but is " + given);
                    return false;
                }
            }
            else if (/* Should > */!(given > greatLess.value())) {
                logger.error(name + " is required > " + greatLess.value() + " but is " + given);
                return false;
            }
        }
        if (lesser) {
            if (equal) {
                if (/* Should <=, shouldn't > */ given > greatLess.value()) {
                    logger.error(name + " is required < " + greatLess.value() + " but is " + given);
                    return false;
                }

            }
            else if (/* Should < */!(given < greatLess.value())) {
                logger.error(name + " is required < " + greatLess.value() + " but is " + given);
                return false;
            }
        }
        return true;
    }
}
