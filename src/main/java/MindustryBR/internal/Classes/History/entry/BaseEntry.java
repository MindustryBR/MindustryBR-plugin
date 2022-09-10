package MindustryBR.internal.Classes.History.entry;

public interface BaseEntry {
    default String getMessage() {
        return this.getMessage(true);
    }

    default String getMessage(boolean withName) {
        return null;
    }

}
