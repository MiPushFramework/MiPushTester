package moe.yuuta.server.dataverify;

public class SampleObject {
    @GreatLess(value = 0, equal = true)
    public int equalZero = 0;

    @GreatLess(value = 0, greater = true)
    public int greaterZero = 1;

    @GreatLess(value = 0, greater = true, equal = true)
    public int greaterOrEqualZero;

    @GreatLess(value = 0, lesser = true)
    public int lesserZero = -1;

    @GreatLess(value = 0, lesser = true, equal = true)
    public int lesserOrEqualZero;

    @GreatLess(value = 0, lesser = true, equal = true)
    public String lesserOrEqualInvalidNonNumber;

    @Nonnull
    public String nonNullButCanEmptyString = "";

    @Nonnull
    public Object nonNullObject = new Object();

    @Nonnull(nonEmpty = true)
    public String nonNullCannotEmptyString = "123";

    @Nonnull(nonEmpty = true)
    public Object nonNullCannotEmptyObject = new Object();

    @NumberIn(value = {1, 2, 3})
    public int mustIn123Int = 1;

    @StringIn(value = {"Apple", "Pear", "Rikka"})
    public String mustInApplePearRikkaString = "Rikka";

    @GreatLessGroup(value = { @GreatLess(value = 0, lesser = true),
                                @GreatLess(value = -3, greater = true)})
    public int shouldGreaterThanN10AndLesserThan0Int = -1;
}
