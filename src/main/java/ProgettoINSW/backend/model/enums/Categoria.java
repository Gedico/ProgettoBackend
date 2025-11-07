package ProgettoINSW.backend.model.enums;

public enum Categoria {
    VENDITA,
    AFFITTO// perfuture implementazioni di affitto
    ;

    public String toUpperCase() {
        return this.name().toUpperCase();
    }
}
