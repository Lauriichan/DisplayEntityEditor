package goldenshadow.displayentityeditor.enums;

import java.util.function.Predicate;

import org.bukkit.entity.Display;

public enum LockSearchMode {

    ALL(display -> true),
    LOCKED(display -> display.getScoreboardTags().contains("dee:locked")),
    UNLOCKED(display -> !display.getScoreboardTags().contains("dee:locked"));

    private static final LockSearchMode[] MODES = LockSearchMode.values();

    private final Predicate<Display> predicate;

    private LockSearchMode(final Predicate<Display> predicate) {
        this.predicate = predicate;
    }

    public Predicate<Display> getPredicate() {
        return predicate;
    }

    public LockSearchMode previousMode() {
        int i = ordinal();
        return i == 0 ? MODES[MODES.length - 1] : MODES[i - 1];
    }

    public LockSearchMode nextMode() {
        int i = ordinal();
        return i + 1 == MODES.length ? MODES[0] : MODES[i + 1];
    }

}
