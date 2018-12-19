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
                                boolean equal = (greatLess.flags() & GreatLess.EQUAL) != 0;
                                boolean greater = (greatLess.flags() & GreatLess.GREATER) != 0;
                                boolean lesser = (greatLess.flags() & GreatLess.LESSER) != 0;
                                if (equal && !greater && !lesser) {
                                    // Only equal
                                    if (number != greatLess.value()) {
                                        logger.error(field.getName() + " is required equals to " + greatLess.value() + " but is " + number);
                                        return false;
                                    }
                                }
                                if (greater) {
                                    if (equal && /* Should >=, shouldn't < */ number < greatLess.value()) {
                                        logger.error(field.getName() + " is required >= " + greatLess.value() + " but is " + number);
                                        return false;
                                    }
                                    else if (/* Should > */!(number > greatLess.value())) {
                                        logger.error(field.getName() + " is required > " + greatLess.value() + " but is " + number);
                                        return false;
                                    }
                                }
                                if (lesser) {
                                    if (equal && /* Should <=, shouldn't > */ number > greatLess.value()) {
                                        logger.error(field.getName() + " is required < " + greatLess.value() + " but is " + number);
                                        return false;
                                    }
                                    else if (/* Should < */!(number < greatLess.value())) {
                                        logger.error(field.getName() + " is required < " + greatLess.value() + " but is " + number);
                                        return false;
                                    }
                                }
                            }
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
}
