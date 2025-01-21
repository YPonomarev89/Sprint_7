package original;

import io.restassured.RestAssured;
import org.junit.Before;

public class BaseTest {

    @Before
    public void setUp() {
        RestAssured.baseURI = Constants.BASIC_URL;
    }
}