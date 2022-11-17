package org.acme;

import io.quarkus.vertx.http.Compressed;
import io.smallrye.mutiny.Uni;
import org.jboss.logging.Logger;
import org.jboss.resteasy.reactive.RestHeader;
import org.jboss.resteasy.reactive.RestPath;
import org.jboss.resteasy.reactive.RestQuery;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import java.util.*;

@Path("/big_payload")
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
public class BigPayloadResource {

    private static final Logger LOG = Logger.getLogger(BigPayloadResource.class);

    private final Map<HeavyObjectDTO, List<HeavyObjectDTO>> inMemoryMockDB = new HashMap<>();

    @POST
    @Compressed
    public Uni<HeavyObjectDTO> createPainter(@RestHeader("Content-Length") int contentLength, HeavyObjectDTO heavyPayload) {
        LOG.info("Heavy object received: " + contentLength);
        inMemoryMockDB.putIfAbsent(heavyPayload, new ArrayList<>());
        return Uni.createFrom().item(heavyPayload);
    }

    @PUT
    @Path("/{origin_id}/friend/{dest_id}")
    @Compressed
    public Uni<String> addFriend(@RestPath("origin_id") String originID, @RestPath("dest_id") String destID) {
        LOG.info(String.format("Request friendship between %s and %s", originID, destID));
        HeavyObjectDTO userOrigin = getUserByID(originID);
        HeavyObjectDTO userDest = getUserByID(destID);
        userOrigin.addFriend(destID);
        userDest.addFriend(originID);
        inMemoryMockDB.get(userOrigin).add(userDest);
        inMemoryMockDB.get(userDest).add(userOrigin);
        return Uni.createFrom().item(userOrigin.getId());
    }

    @GET
    @Path("/painters")
    @Compressed
    public Uni<List<HeavyObjectDTO>> getAllPainters(@RestQuery("age") int age) {
        HeavyObjectDTO root = inMemoryMockDB.keySet().stream().findFirst().orElseThrow(NotFoundException::new);
        List<HeavyObjectDTO> visited = new ArrayList<>();
        Queue<String> queue = new ArrayDeque<>();
        queue.add(root.getId());
        visited.add(root);
        while (!queue.isEmpty()) {
            HeavyObjectDTO userID = getUserByID(queue.poll());
            for (HeavyObjectDTO user : inMemoryMockDB.get(userID)) {
                if (!visited.contains(user)) {
                    if(user.getAge() >= age) visited.add(user);
                    queue.add(user.getId());
                }
            }
        }
        LOG.info(String.format("Painters retrieved %d", visited.size()));
        return Uni.createFrom().item(visited);
    }

    @GET
    @Path("/{id}/friends")
    @Compressed
    public Uni<List<HeavyObjectDTO>> getAllPainterFriends(@RestPath("id") String id) {
        HeavyObjectDTO painter = getUserByID(id);
        List<HeavyObjectDTO> friends = inMemoryMockDB.get(painter);
        LOG.info(String.format("Painters friends retrieved %d", friends.size()));
        return Uni.createFrom().item(friends);
    }

    private HeavyObjectDTO getUserByID(String id) {
        for (Map.Entry<HeavyObjectDTO,List<HeavyObjectDTO>> entry : inMemoryMockDB.entrySet()) {
            if(entry.getKey().getId().equalsIgnoreCase(id)) {
                return entry.getKey();
            }
        }
        throw new NotFoundException(String.format("ID %s not found.", id));
    }
}