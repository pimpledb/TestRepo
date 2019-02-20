package json;

import io.restassured.builder.ResponseSpecBuilder;
import io.restassured.response.ExtractableResponse;
import io.restassured.specification.ResponseSpecification;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;
import io.restassured.RestAssured;

import java.util.HashMap;
import java.util.List;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.*;

public class JsonTest {
    String isbn = "isbn:9781451648546";

    @BeforeClass
    public static void setUp(){

        RestAssured.baseURI="https://www.googleapis.com/books/v1";
    }

    public static ResponseSpecification assertBadRequest(final int statusCode, final String errorMsg) {
        ResponseSpecBuilder builder = new ResponseSpecBuilder();
        builder.expectStatusCode(statusCode);
        builder.expectBody("error.message", equalTo(errorMsg));
        return builder.build();
    }

    @Test
    public  void test001(){

        //RestAssured.baseURI = "https://www.googleapis.com/books/v1";
        String booktype = given().
                param("q", isbn).
                when().
                get("/volumes")
                 //get()
                .then().extract().path("kind");

        System.out.println("\n *** Extractable  booktype = "+booktype);

        //GETTING Extractable Response



        ExtractableResponse extrResp = given().
                param("q", isbn).
                when().
                get("/volumes")
                //get()
                .then().extract();

        Assert.assertEquals(extrResp.statusCode(), 200);

        System.out.println("\n *** EInner booktype = "+extrResp.path("items[0].kind"));

        HashMap<String, Object> map = extrResp.path("items[0].volumeInfo");
        System.out.println(" title :"+map.get("title").toString());

        List<Object> list = (List)map.get("authors");

        System.out.println("\n**** wait here  Authors :"+list.get(0).toString());
    }

    /**
     *
     */
    @Test
    public  void test001_hamcrest_matcher(){

        //ExtractableResponse extrResp =
                given().
                param("q", isbn).
                when().
                get("/volumes")
                .then()
                .body("kind", equalTo("books#volumes"));

                given().
                param("q", isbn).
                when().
                get("/volumes")
                .then()
                .body("items[0].volumeInfo.industryIdentifiers.type", hasItems("ISBN_13","ISBN_10"));

                //Find the key is present
                given().
                param("q", isbn).
                when().
                get("/volumes")
                .then()
                .body("items[0].volumeInfo", hasKey("publisher"));

    }

    /**
     * Test GPATH and multiple asserstion in
     */
    @Test
    public void testGPath_Multiple_Hard_Assetion()
    {
        given().
                param("q", isbn).
                when().
                get("/volumes")
                .then()
                .body("items[0].volumeInfo.industryIdentifiers.type", hasItems("ISBN_13","ISBN_10"))
                .body("items[0].volumeInfo", hasKey("publisher"));
    }

    @Test
    public void testSoftAssertion()
    {
        given().
                param("q", isbn).
                when().
                get("/volumes")
                .then()
                .body("items[0].volumeInfo", hasKey("publisher"),
                        "items[0].volumeInfo.industryIdentifiers.type", hasItems("ISBN_13","ISBN_10"));

        //Assert.assertThat();
    }

    @Test
    public void testAsserThroughResponseSpec()
    {
        given().
                param("q1", isbn).
                when().
                get("/volumes")
                .then()
                .log()
                .all()
                .spec(assertBadRequest(400,"Required parameter: q"));

    }


}
