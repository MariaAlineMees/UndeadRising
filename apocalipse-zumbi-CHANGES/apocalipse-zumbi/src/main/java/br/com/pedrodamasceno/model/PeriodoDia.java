package br.com.pedrodamasceno.model;

public enum PeriodoDia {
    MANHA("Manhã"),
    TARDE("Tarde"),
    NOITE("Noite");

    private final String rotulo;

    PeriodoDia(String rotulo) {
        this.rotulo = rotulo;
    }

    public String getRotulo() {
        return rotulo;
    }

    public PeriodoDia proximo() {
        return switch (this) {
            case MANHA -> TARDE;
            case TARDE -> NOITE;
            case NOITE -> MANHA;
        };
    }
}