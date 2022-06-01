package me.modify.supersponge.objects;

import lombok.Getter;
import me.modify.supersponge.exceptions.SpongeLocationParseException;
import me.modify.supersponge.exceptions.WorldNotFoundException;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;

import java.util.Objects;

public class SuperSpongeLocation {

    /** World of this super sponge location */
    @Getter private final World world;

    /** z axis value of this super sponge location */
    @Getter private final int x;

    /** y axis value of this super sponge location */
    @Getter private final int y;

    /** z axis value of this super sponge location */
    @Getter private final int z;

    /**
     * Constructs a new super sponge location.
     * @param world world where this super sponge location is based.
     * @param x x value of the location
     * @param y y value of the location
     * @param z z value of the location
     */
    public SuperSpongeLocation(World world, int x, int y, int z) {
        this.world = world;
        this.x = x;
        this.y = y;
        this.z = z;
    }

    /**
     * Converts this location object into a string.
     * @return string representation of this object.
     */
    @Override
    public String toString() {
        return String.format("%s %d %d %d", world.getName(), x, y, z);
    }

    /**
     * Parses a location string into a SuperSpongeLocation object.
     * @param location string location to parse.
     * @return super sponge location object
     *
     * @throws WorldNotFoundException if the world in the encoded string is not a valid bukkit world on the server.
     * @throws SpongeLocationParseException if any of the x, y or z values in the encoded string are not integers.
     */
    public static SuperSpongeLocation fromString(String location) throws WorldNotFoundException, SpongeLocationParseException {
        String[] parts = location.split(" ");

        World world;
        if (Bukkit.getWorld(parts[0]) == null) {
            throw new WorldNotFoundException("Failed to parse super sponge location. World not found.");
        }
        world = Bukkit.getWorld(parts[0]);

        int x, y, z;
        try {
            x = Integer.parseInt(parts[1]);
            y = Integer.parseInt(parts[2]);
            z = Integer.parseInt(parts[3]);
        } catch (NumberFormatException e) {
            throw new SpongeLocationParseException("Failed to parse super sponge location. x, y, z value(s) are not all integers.");
        }

        return new SuperSpongeLocation(world, x, y, z);
    }

    /**
     * Returns the equivalent super sponge location from a bukkit location.
     * @param location bukkit location.
     * @return super sponge location.
     */
    public static SuperSpongeLocation fromBukkitLocation(Location location) {
        return new SuperSpongeLocation(location.getWorld(), location.getBlockX(), location.getBlockY(), location.getBlockZ());
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        SuperSpongeLocation that = (SuperSpongeLocation) o;
        return x == that.x && y == that.y && z == that.z && Objects.equals(world.getName(), that.world.getName());
    }

    @Override
    public int hashCode() {
        return Objects.hash(world, x, y, z);
    }
}
