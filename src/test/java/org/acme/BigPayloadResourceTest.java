package org.acme;

import io.quarkus.test.junit.QuarkusTest;
import io.restassured.http.ContentType;
import io.smallrye.mutiny.Multi;
import org.junit.jupiter.api.Test;

import java.util.*;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.greaterThan;
import static org.hamcrest.Matchers.is;

@QuarkusTest
public class BigPayloadResourceTest {
    List<String> ids = new ArrayList<>();
    Random rand = new Random();
    private static final long ONE_MEGA_BYTE = 1024l * 1024l * 1l;
    private static final long TEN_MEGA_BYTE = 1024l * 1024l * 10l;

//    private static final long ONE_MEGA_BYTE = 1024l * 1l;
//    private static final long TEN_MEGA_BYTE = 1l * 1l;

    @Test
    public void testHelloEndpoint() {
        // Add 5 painters
        addPainter("picasso");
        addPainter("Goya");
        addPainter("Sorolla");
        addPainter("Velazquez");
        addPainter("Greco");
        addPainter("Dali");
        addPainter("Murillo");
        addPainter("Miro");

        // Add friends
        addFriend("picasso", "Goya");
        addFriend("picasso", "Sorolla");
        addFriend("picasso", "Velazquez");
        addFriend("picasso", "Greco");
        addFriend("picasso", "Murillo");
        addFriend("Miro", "Dali");
        addFriend("Goya", "Greco");
        addFriend("Goya", "Velazquez");
        addFriend("Dali", "Murillo");
        addFriend("Velazquez", "Greco");

        // Query: all painters older than 0
        given()
                .contentType(ContentType.JSON)
                .when().get("/big_payload/painters?age=0") // greater than 10
                .then().statusCode(200).body("size()", is(8));

        // Query: all picasso friends
        given()
                .contentType(ContentType.JSON)
                .when().get("/big_payload/picasso/friends") // greater than 10
                .then().statusCode(200).body("size()", is(5));
    }

    private void addPainter(String name) {
        HeavyObjectDTO painter = getRandomPainter(name);
        ids.add(painter.getId());
        given()
                .body(painter)
                .contentType(ContentType.JSON)
                .when().post("/big_payload")
                .then().statusCode(200);
    }

    private void addFriend(String origin, String dest) {
        given()
                .contentType(ContentType.JSON)
                .when().put("/big_payload/" + origin + "/friend/" + dest)
                .then().statusCode(200);
    }

    private HeavyObjectDTO getRandomPainter(String name) {
        HeavyObjectDTO painter = new HeavyObjectDTO(name, name, 35);
        Map<String, Map<String, Object>> metaData = new HashMap<>();
        metaData.put("lithographs", Map.of("lithograph_one", generateRandImg(ONE_MEGA_BYTE)));
        metaData.put("pictures", Map.of("picture_one", generateRandImg(TEN_MEGA_BYTE)));
        metaData.put("bibliography", Map.of("short_version", generateRandImg(ONE_MEGA_BYTE)));
        painter.setMetaData(metaData);
        return painter;
    }

    private byte[] generateRandImg(long size) {
        byte[] payload = new byte[(int)size];
        rand.nextBytes(payload);
        return payload;
    }
}