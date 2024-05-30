package pposonggil.usedStuff.dto.Color;

public enum BusColor {
    ONE("#33CC99"),
    TWO("#0085CA"),
    THREE("#ffc600"),
    FOUR("#e60012"),
    FIVE("#00a0e9"),
    SIX("#e60012"),
    ELEVEN("#0068b7"),
    TWELVE("#53b332"),
    THIRTEEN("#f2b70a"),
    FOURTEEN("#e60012"),
    FIFTEEN("#ff3300");

    private final String colorCode;

    BusColor(String colorCode) {
        this.colorCode = colorCode;
    }

    public String getColorCode() {
        return colorCode;
    }

    public static BusColor getByNumber(int number) {
        switch (number) {
            case 1:
                return ONE;
            case 2:
                return TWO;
            case 3:
                return THREE;
            case 4:
                return FOUR;
            case 5:
                return FIVE;
            case 6:
                return SIX;
            case 11:
                return ELEVEN;
            case 12:
                return TWELVE;
            case 13:
                return THIRTEEN;
            case 14:
                return FOURTEEN;
            case 15:
                return FIFTEEN;
            default:
                throw new IllegalArgumentException("Invalid bus number: " + number);
        }
    }
}
