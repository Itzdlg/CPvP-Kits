package us.cpvp.kits.util;

import org.bukkit.profile.PlayerProfile;
import org.bukkit.profile.PlayerTextures;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;

public class SkullProfile implements PlayerProfile {
    private final String url;

    public SkullProfile(String url) {
        this.url = url;
    }

    @Override
    public UUID getUniqueId() {
        return UUID.randomUUID();
    }

    @Override
    public String getName() {
        return "";
    }

    @Override
    public PlayerTextures getTextures() {
        return new PlayerTextures() {
            @Override
            public boolean isEmpty() {
                return false;
            }

            @Override
            public void clear() {

            }

            @Override
            public URL getSkin() {
                try {
                    return new URL(url);
                } catch (MalformedURLException e) {
                    throw new RuntimeException(e);
                }
            }

            @Override
            public void setSkin(URL url) {

            }

            @Override
            public void setSkin(URL url, SkinModel skinModel) {

            }

            @Override
            public SkinModel getSkinModel() {
                return SkinModel.CLASSIC;
            }

            @Override
            public URL getCape() {
                return null;
            }

            @Override
            public void setCape(URL url) {

            }

            @Override
            public long getTimestamp() {
                return 0;
            }

            @Override
            public boolean isSigned() {
                return false;
            }
        };
    }

    @Override
    public void setTextures(PlayerTextures playerTextures) {

    }

    @Override
    public boolean isComplete() {
        return true;
    }

    @Override
    public CompletableFuture<PlayerProfile> update() {
        return CompletableFuture.failedFuture(new UnsupportedOperationException());
    }

    @Override
    public PlayerProfile clone() {
        throw new UnsupportedOperationException();
    }

    @Override
    public Map<String, Object> serialize() {
        throw new UnsupportedOperationException();
    }
}
