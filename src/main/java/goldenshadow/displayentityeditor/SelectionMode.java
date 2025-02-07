package goldenshadow.displayentityeditor;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Stream;

import org.bukkit.Location;
import org.bukkit.entity.Display;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.util.RayTraceResult;

import goldenshadow.displayentityeditor.enums.LockSearchMode;

public abstract class SelectionMode {

    private static final Predicate<Entity> DISPLAY_FILTER = entity -> entity instanceof Display;
    private static final Function<Entity, Display> DISPLAY_CAST = entity -> (Display) entity;

    private static final HashMap<String, SelectionMode> idToMode = new HashMap<>();
    private static final ArrayList<String> idOrder = new ArrayList<>();
    
    public static int amount() {
        return idOrder.size();
    }

    public static SelectionMode get(String mode) {
        return idToMode.get(mode);
    }

    public static final SelectionMode NEARBY = new SelectionMode("nearby") {

        @Override
        protected Stream<Display> select(Player p, double range) {
            return p.getWorld().getNearbyEntities(p.getLocation(), range, range, range).stream().filter(DISPLAY_FILTER).map(DISPLAY_CAST);
        }

    };

    public static final SelectionMode RAYCAST = new SelectionMode("raycast") {

        @Override
        protected Stream<Display> select(Player p, double range) {
            RayTraceResult result = p.getWorld().rayTraceEntities(p.getEyeLocation(), p.getEyeLocation().getDirection(), range, 0.1d);
            if (result.getHitEntity() == null || !(result.getHitEntity() instanceof Display display)) {
                return Stream.empty();
            }
            return Stream.of(display);
        }

    };

    private final String id;

    public SelectionMode(String id) {
        this.id = id;
        idOrder.add(id);
        idToMode.put(id, this);
    }

    public final String id() {
        return id;
    }
    
    public final int index() {
        return idOrder.indexOf(id);
    }
    
    public final SelectionMode previousMode() {
        int i = idOrder.indexOf(id);
        return idToMode.get(i == 0 ? idOrder.get(idOrder.size() - 1) : idOrder.get(i - 1));
    }
    
    public final SelectionMode nextMode() {
        int i = idOrder.indexOf(id);
        return idToMode.get(i + 1 == idOrder.size() ? idOrder.get(0) : idOrder.get(i + 1));
    }

    public final List<Display> select(Player p, LockSearchMode lockSearchMode) {
        List<Display> displays = select(p, Utilities.getToolSelectRange(p)).filter(lockSearchMode.getPredicate()).toList();
        if (displays.isEmpty()) {
            return null;
        }
        if (displays.size() == 1) {
            return displays;
        }
        if (Utilities.getToolSelectMultiple(p)) {
            return displays;
        }
        Display closest = null;
        double tmp, distance = Double.MAX_VALUE;
        Location pLoc = p.getLocation();
        for (Display display : displays) {
            tmp = pLoc.distanceSquared(display.getLocation());
            if (tmp < distance) {
                distance = tmp;
                closest = display;
            }
        }
        // Never null
        return List.of(closest);
    }

    protected abstract Stream<Display> select(Player p, double range);

}
