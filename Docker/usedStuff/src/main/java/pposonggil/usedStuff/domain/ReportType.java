package pposonggil.usedStuff.domain;

import lombok.Getter;

@Getter
public enum ReportType {
    ABUSE("욕설"),
    ADVERTISEMENT("광고"),
    NOSHOW("노쇼"),
    TRADY("지각"),
    DEFECTIVEUMBRELLA("불량우산")
    ;

    private final String krName;

    ReportType(String krName) {
        this.krName = krName;
    }
}
