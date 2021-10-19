package MindustryBR.internal.classes.history.entry;

public interface BaseEntry {
    default String getMessage() {
        return this.getMessage(true);
    }

    default String getMessage(boolean withName) {
        return null;
    }

}
