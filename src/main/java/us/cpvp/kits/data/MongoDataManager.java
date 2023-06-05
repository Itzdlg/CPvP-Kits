package us.cpvp.kits.data;

import com.mongodb.client.model.Filters;
import com.mongodb.client.model.Updates;
import com.mongodb.client.result.InsertOneResult;
import com.mongodb.client.result.UpdateResult;
import com.mongodb.reactivestreams.client.MongoClient;
import com.mongodb.reactivestreams.client.MongoClients;
import com.mongodb.reactivestreams.client.MongoCollection;
import com.mongodb.reactivestreams.client.MongoDatabase;
import org.bson.Document;
import org.bukkit.inventory.ItemStack;
import org.reactivestreams.Subscription;
import us.cpvp.kits.data.mongo.CompletableFutureSubscriber;
import us.cpvp.kits.data.mongo.ObservableSubscriber;
import us.cpvp.kits.entities.LoadoutConfiguration;
import us.cpvp.kits.util.ItemUtil;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.function.Consumer;

/**
 * A DataManager implementation for Mongo.
 *
 * This class is **not** null-safe. Any operation
 * may return a CompletableFuture completed with
 * the value null. No Optional data types are
 * present.
 */

public class MongoDataManager implements IDataManager<UpdateResult> {
    private final Cache cache = new Cache();

    private final MongoClient client;
    private final MongoDatabase database;

    private final MongoCollection<Document> collection;

    public MongoDataManager(String url, String databaseName, String collectionName) {
        this.client = MongoClients.create(url);
        this.database = client.getDatabase(databaseName);

        this.collection = this.database.getCollection(collectionName);
    }

    @Override
    public CompletableFuture<LoadoutConfiguration[]> getLoadoutConfigurations(UUID playerId) {
        CompletableFuture<LoadoutConfiguration[]> future = CompletableFuture.supplyAsync(() -> {
            LoadoutConfiguration[] loadoutConfigurations = new LoadoutConfiguration[] {
                getLoadoutConfiguration(playerId, 1).join(),
                getLoadoutConfiguration(playerId, 2).join(),
                getLoadoutConfiguration(playerId, 3).join(),
                getLoadoutConfiguration(playerId, 4).join(),
                getLoadoutConfiguration(playerId, 5).join()
            };

            return loadoutConfigurations;
        });

        return future;
    }

    @Override
    public CompletableFuture<LoadoutConfiguration> getLoadoutConfiguration(UUID playerId, int loadoutId) {
        CompletableFuture<LoadoutConfiguration> future = CompletableFuture.supplyAsync(() -> {
            if (doesExist(playerId, loadoutId).join()) {
                String name = getName(playerId, loadoutId).join();
                boolean isPublic = isPublic(playerId, loadoutId).join();
                ItemStack[] contents = getContents(playerId, loadoutId).join();

                return new LoadoutConfiguration(playerId, loadoutId, name, isPublic, contents);
            }

            return null;
        });

        return future;
    }

    @Override
    public CompletableFuture<Integer> getSelectedLoadoutId(UUID playerId) {
        return getValue(playerId, "selected_loadout", Integer.class);
    }

    @Override
    public CompletableFuture<UpdateResult> setSelectedLoadoutId(UUID playerId, int loadoutId) {
        return setValue(playerId, "selected_loadout", loadoutId);
    }

    @Override
    public CompletableFuture<String> getName(UUID playerId, int loadoutId) {
        return getLoadoutValue(playerId, loadoutId, "name", String.class);
    }

    @Override
    public CompletableFuture<UpdateResult> setName(UUID playerId, int loadoutId, String name) {
        return setLoadoutValue(playerId, loadoutId, "name", name);
    }

    @Override
    public CompletableFuture<Boolean> isPublic(UUID playerId, int loadoutId) {
        return getLoadoutValue(playerId, loadoutId, "isPublic", Boolean.class);
    }

    @Override
    public CompletableFuture<UpdateResult> setPublic(UUID playerId, int loadoutId, boolean isPublic) {
        return setLoadoutValue(playerId, loadoutId, "isPublic", isPublic);
    }

    @Override
    public CompletableFuture<ItemStack[]> getContents(UUID playerId, int loadoutId) {
        return CompletableFuture.supplyAsync(() -> {
            String joined = getLoadoutValue(playerId, loadoutId, "contents", String.class).join();
            String[] contents = joined.split(",");
            ItemStack[] items = new ItemStack[contents.length];
            for (int i = 0; i < contents.length; i++) {
                items[i] = ItemUtil.fromComplexString(contents[i]);
            }

            return items;
        });
    }

    @Override
    public CompletableFuture<UpdateResult> setContents(UUID playerId, int loadoutId, ItemStack[] contents) {
        String[] serializedContents = new String[contents.length];
        for (int i = 0; i < contents.length; i++) {
            serializedContents[i] = ItemUtil.toComplexString(contents[i]);
        }

        String joined = String.join(",", serializedContents);
        return setLoadoutValue(playerId, loadoutId, "contents", joined);
    }

    @Override
    public CompletableFuture<UpdateResult> deleteLoadout(UUID playerId, int loadoutId) {
        return setValue(playerId, "loadouts." + loadoutId, null);
    }

    private <T> CompletableFuture<T> getLoadoutValue(UUID playerId, int loadoutId, String key, Class<T> valueClass) {
        return getValue(playerId, "loadouts." + loadoutId + "." + key, valueClass);
    }

    private <T> CompletableFuture<T> getValue(UUID playerId, String key, Class<T> valueClass) {
        CompletableFuture<T> future = new CompletableFuture<>();

        Consumer<Document> readDocument = (document) -> {
            T value = document.getEmbedded(Arrays.asList(key.split("\\.")), valueClass);
            future.complete(value);
        };

        if (cache.getDocument(playerId) != null) {
            readDocument.accept(cache.getDocument(playerId));
            return future;
        }

        collection
                .find(Filters.and(
                        Filters.eq("_id", playerId.toString()),
                        Filters.exists(key)))
                .first()
                .subscribe(new ObservableSubscriber<Document>() {
                    @Override
                    public void onSubscribe(Subscription s) {
                        super.onSubscribe(s);
                        s.request(Integer.MAX_VALUE);
                    }

                    @Override
                    public void onNext(Document document) {
                        super.onNext(document);

                        cache.storeDocument(playerId, document);
                        readDocument.accept(document);

                        super.onComplete();
                    }

                    @Override
                    public void onError(Throwable t) {
                        super.onError(t);
                        future.completeExceptionally(t);
                    }
                });

        return future;
    }

    private <T> CompletableFuture<UpdateResult> setLoadoutValue(UUID playerId, int loadoutId, String key, T value) {
        return setValue(playerId, "loadouts." + loadoutId + "." + key, value);
    }

    private <T> CompletableFuture<UpdateResult> setValue(UUID playerId, String key, T value) {
        CompletableFuture<UpdateResult> future = new CompletableFuture<>();

        Runnable updateDocument = () -> collection
                .updateOne(Filters.eq("_id", playerId.toString()), value == null ? Updates.unset(key) : Updates.set(key, value))
                .subscribe(new CompletableFutureSubscriber<>(future, (result) -> {
                    cache.invalidateDocument(playerId);
                    return result;
                }));

        doesExist(playerId).thenAcceptAsync(exists -> {
            if (exists) {
                updateDocument.run();
                return;
            }

            collection.insertOne(new Document()
                    .append("_id", playerId.toString())
                    .append("selected_loadout", 0)
                    .append("loadouts", new Document()))
                    .subscribe(new ObservableSubscriber<>() {
                        @Override
                        public void onSubscribe(Subscription s) {
                            super.onSubscribe(s);
                            s.request(Integer.MAX_VALUE);
                        }

                        @Override
                        public void onNext(InsertOneResult result) {
                            super.onNext(result);

                            cache.setExists(playerId, true);
                            updateDocument.run();

                            super.onComplete();
                        }

                        @Override
                        public void onError(Throwable t) {
                            super.onError(t);
                            future.completeExceptionally(t);
                        }
                    });
        });

        return future;
    }

    private CompletableFuture<Boolean> doesExist(UUID playerId) {
        CompletableFuture<Boolean> future = new CompletableFuture<>();

        if (cache.doesExist(playerId) != null) {
            future.complete(cache.doesExist(playerId));
            return future;
        }

        collection
                .countDocuments(Filters.eq("_id", playerId.toString()))
                .subscribe(new CompletableFutureSubscriber<>(future, (count) -> count > 0));

        return future;
    }

    private CompletableFuture<Boolean> doesExist(UUID playerId, int loadoutId) {
        CompletableFuture<Boolean> future = new CompletableFuture<>();

        if (cache.getDocument(playerId) != null) {
            future.complete(cache.getDocument(playerId).containsKey("loadouts." + loadoutId));
            return future;
        }

        collection
                .countDocuments(Filters.and(
                        Filters.eq("_id", playerId.toString()),
                        Filters.exists("loadouts." + loadoutId)))
                .subscribe(new CompletableFutureSubscriber<>(future, (count) -> count > 0));

        return future;
    }

    public Cache cache() {
        return cache;
    }

    private static class Cache {
        public void invalidate(UUID player) {
            invalidateDocument(player);
            invalidateExists(player);
        }

        public void invalidate(long millis) {
            for (Map.Entry<UUID, Long> entry : loadTime.entrySet()) {
                if (entry.getValue() > millis)
                    invalidate(entry.getKey());
            }
        }

        private final Map<UUID, Document> documents = new HashMap<>();
        private final Map<UUID, Integer> publicLoadouts = new HashMap<>();
        private final Map<UUID, Boolean> existingPlayers = new HashMap<>(); // unset if not cached. That's why Map

        private final Map<UUID, Long> loadTime = new HashMap<>();

        private void storeDocument(UUID playerId, Document document) {
            documents.put(playerId, document);
            existingPlayers.put(playerId, true);
            loadTime.put(playerId, System.currentTimeMillis());
        }

        private void invalidateDocument(UUID playerId) {
            documents.remove(playerId);
        }

        private Document getDocument(UUID playerId) {
            return documents.get(playerId);
        }

        private Boolean doesExist(UUID playerId) { // internal. Usage of null wrapper type.
            if (!existingPlayers.containsKey(playerId))
                return null;

            return existingPlayers.get(playerId);
        }

        private void setExists(UUID playerId, boolean exists) {
            existingPlayers.put(playerId, exists);
        }

        private void invalidateExists(UUID playerId) {
            existingPlayers.remove(playerId);
        }
    }
}