import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import io.restassured.module.jsv.JsonSchemaValidator;
import io.restassured.response.Response;
import org.testng.annotations.Test;

import static io.restassured.RestAssured.get;
import static org.hamcrest.Matchers.equalTo;

public class Test_Capital_Get{

    @Test
    void test_get_capital_positive(){

        // Get the countries list
        Response response = get("https://restcountries.eu/rest/v2/all?fields=name;capital;currencies;latlng");

        // Get first capital name
        String capitalName = response.path("[0].capital").toString();

        // Get capital currency code
        String currencyCode = response.path("[0].currencies[0].code").toString();

        // GIVEN
        RestAssured
                .given()
                .baseUri(String.format(
                        "https://restcountries.eu/rest/v2/capital/%s?fields=name;capital;currencies;latlng;regionalBlocs",
                        capitalName))
                .contentType(ContentType.JSON)
                // WHEN
                .when()
                .get()
                // THEN
                .then()
                    .assertThat()
                    .statusCode(200)
                    .body("[0].currencies[0].code", equalTo(currencyCode))
                    .body(JsonSchemaValidator.matchesJsonSchemaInClasspath("capital_get_response.json"));
    }

    @Test
    void test_get_capital_negative(){

        // GIVEN
        RestAssured
                .given()
                .baseUri("https://restcountries.eu/rest/v2/capital/NotExist?fields=name;capital;currencies;latlng;regionalBlocs")
                .contentType(ContentType.JSON)
                // WHEN
                .when()
                .get()
                // THEN
                .then()
                .assertThat()
                .statusCode(404)
                .body(JsonSchemaValidator.matchesJsonSchemaInClasspath("capital_get_invalid_response.json"));
    }
}