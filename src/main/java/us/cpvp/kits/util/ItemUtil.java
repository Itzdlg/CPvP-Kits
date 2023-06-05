package us.cpvp.kits.util;

import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.util.io.BukkitObjectInputStream;
import org.bukkit.util.io.BukkitObjectOutputStream;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;

public class ItemUtil {
    private ItemUtil() {}

    public static boolean isEmpty(ItemStack item) {
        return item == null || item.getType() == Material.AIR;
    }

    public static byte[] toByteArray(ItemStack item) {
        try {
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            BukkitObjectOutputStream bout = new BukkitObjectOutputStream(baos);
            bout.writeObject(item);
            bout.flush();
            return baos.toByteArray();
        } catch (IOException e) {
            return new byte[0];
        }
    }

    public static ItemStack fromByteArray(byte[] arr) {
        try {
            ByteArrayInputStream bais = new ByteArrayInputStream(arr);
            BukkitObjectInputStream bois = new BukkitObjectInputStream(bais);
            return (ItemStack) bois.readObject();
        } catch (IOException | ClassNotFoundException e) {
            return new ItemStack(Material.AIR, 1);
        }
    }

    public static String toComplexString(ItemStack item) {
        return new String(toByteArray(item), StandardCharsets.ISO_8859_1);
    }

    public static ItemStack fromComplexString(String s) {
        return fromByteArray(s.getBytes(StandardCharsets.ISO_8859_1));
    }

    public static String toReadableString(ItemStack item) {
        String string = "";
        if (item.getItemMeta() == null) return "";
        String name = item.getItemMeta().getDisplayName();
        if (name.isEmpty()) {
            string = toReadableString(item.getType());
        } else string = name + " [" + toReadableString(item.getType()) + "]";
        return string + " x" + item.getAmount();
    }

    public static String toReadableString(Enum<?> e) {
        char[] chars = e.name().toLowerCase().replace("_", " ").toCharArray();
        boolean found = false;
        for (int i = 0; i < chars.length; i++) {
            if (!found && Character.isLetter(chars[i])) {
                chars[i] = Character.toUpperCase(chars[i]);
                found = true;
            } else if (Character.isWhitespace(chars[i])) {
                found = false;
            }
        }

        return String.valueOf(chars);
    }
}
