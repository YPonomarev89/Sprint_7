package original;

import io.qameta.allure.Description;
import io.restassured.response.Response;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import original.requestbodies.RequestBodyForCreatingCourier;
import original.requestbodies.RequestBodyForLoginCourier;
import original.stepsfortests.CreatingCourierSteps;
import original.stepsfortests.LoginCourierSteps;

import java.io.File;

import static org.apache.http.HttpStatus.*;

public class LoginCourierTest extends BaseTest {

    private Boolean isNeedToDeleteCourier;

    public Boolean getIsNeedToDeleteCourier() {
        return isNeedToDeleteCourier;
    }

    public void setIsNeedToDeleteCourier(Boolean needToDeleteCourier) {
        isNeedToDeleteCourier = needToDeleteCourier;
    }

    static CreatingCourierSteps creatingCourierSteps = new CreatingCourierSteps();
    LoginCourierSteps loginCourierSteps = new LoginCourierSteps();

    @Before
    public void setUp() {
        super.setUp(); // Вызов метода setUp() из BaseTest
        RequestBodyForCreatingCourier requestBodyForCreatingCourier = new RequestBodyForCreatingCourier("Yury_P", "1234", "Yury");
        creatingCourierSteps.createCourier(requestBodyForCreatingCourier);
    }

    @Test
    @Description("Проверка успешного логина курьера с корректными данными")
    public void loginCourier() {
        setIsNeedToDeleteCourier(false);
        RequestBodyForLoginCourier requestBodyForLoginCourier = new RequestBodyForLoginCourier("Yury_P", "1234");
        Response responseAfterLoginCourier = creatingCourierSteps.loginCourier(requestBodyForLoginCourier);

        creatingCourierSteps.verifyStatus(responseAfterLoginCourier, SC_OK);

        String courierId = creatingCourierSteps.extractCourierId(responseAfterLoginCourier);

        String expectedJson = loginCourierSteps.creatingExpectedJson(courierId);
        loginCourierSteps.assertLoginResponseBody(responseAfterLoginCourier, expectedJson);

        creatingCourierSteps.deleteCourierById(courierId);
    }

    @Test
    @Description("Проверка логина курьера с неверным логином")
    public void tryToLoginCourierWithWrongLogin() {
        setIsNeedToDeleteCourier(true);
        RequestBodyForLoginCourier requestBodyForLoginCourier = new RequestBodyForLoginCourier("Yury_PPP", "1234");
        Response responseAfterLoginCourier = creatingCourierSteps.loginCourier(requestBodyForLoginCourier);
        creatingCourierSteps.verifyStatus(responseAfterLoginCourier, SC_NOT_FOUND);

        loginCourierSteps.assertErrorResponseBody(responseAfterLoginCourier, Constants.ERROR_RESPONSE_BODY_FOR_INVALID_LOGIN_DATA);
    }

    @Test
    @Description("Проверка логина курьера с неверным паролем")
    public void tryToLoginCourierWithWrongPassword() {
        setIsNeedToDeleteCourier(true);
        RequestBodyForLoginCourier requestBodyForLoginCourier = new RequestBodyForLoginCourier("Yury_P", "1235");
        Response responseAfterLoginCourier = creatingCourierSteps.loginCourier(requestBodyForLoginCourier);
        creatingCourierSteps.verifyStatus(responseAfterLoginCourier, SC_NOT_FOUND);

        loginCourierSteps.assertErrorResponseBody(responseAfterLoginCourier, Constants.ERROR_RESPONSE_BODY_FOR_INVALID_LOGIN_DATA);
    }

    @Test
    @Description("Проверка логина курьера без логина")
    public void tryToLoginCourierWithoutLogin() {
        setIsNeedToDeleteCourier(true);
        File json = new File("src/test/resources/loginCourierWithoutLogin.json");
        Response responseAfterLoginCourier = loginCourierSteps.loginCourierWithJson(json);
        creatingCourierSteps.verifyStatus(responseAfterLoginCourier, SC_BAD_REQUEST);

        loginCourierSteps.assertErrorResponseBody(responseAfterLoginCourier, Constants.ERROR_RESPONSE_BODY_FOR_MISSING_LOGIN_DATA);
    }

    @Test
    @Description("Проверка логина курьера без пароля")
    public void tryToLoginCourierWithoutPassword() {
        setIsNeedToDeleteCourier(true);
        File json = new File("src/test/resources/loginCourierWithoutPassword.json");
        Response responseAfterLoginCourier = loginCourierSteps.loginCourierWithJson(json);

        creatingCourierSteps.verifyStatus(responseAfterLoginCourier, SC_BAD_REQUEST);

        loginCourierSteps.assertErrorResponseBody(responseAfterLoginCourier, Constants.ERROR_RESPONSE_BODY_FOR_MISSING_LOGIN_DATA);
    }

    @Test
    @Description("Проверка логина несуществующего курьера")
    public void tryToLoginCourierLikeNonExistentCourier() {
        setIsNeedToDeleteCourier(true);
        RequestBodyForLoginCourier requestBodyForLoginCourier = new RequestBodyForLoginCourier("NonExistentUser", "1235");
        Response responseAfterLoginCourier = creatingCourierSteps.loginCourier(requestBodyForLoginCourier);

        creatingCourierSteps.verifyStatus(responseAfterLoginCourier, SC_NOT_FOUND);

        loginCourierSteps.assertErrorResponseBody(responseAfterLoginCourier, Constants.ERROR_RESPONSE_BODY_FOR_INVALID_LOGIN_DATA);
    }

    @After
    public void setDown() {
        if (getIsNeedToDeleteCourier()) {
            RequestBodyForLoginCourier correctRequestBody = new RequestBodyForLoginCourier("Yury_P", "1234");
            Response correctLoginResponse = creatingCourierSteps.loginCourier(correctRequestBody);
            String courierId = creatingCourierSteps.extractCourierId(correctLoginResponse);
            creatingCourierSteps.deleteCourierById(courierId);
        } else {
            System.out.println("Wrong");
        }
    }
}