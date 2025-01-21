package original;

import io.qameta.allure.Description;
import io.restassured.response.Response;
import org.junit.After;
import org.junit.Test;
import original.requestbodies.RequestBodyForCreatingCourier;
import original.requestbodies.RequestBodyForLoginCourier;
import original.stepsfortests.CreatingCourierSteps;

import static org.apache.http.HttpStatus.*;

public class CreatingCourierTest extends BaseTest {

    CreatingCourierSteps creatingCourierSteps = new CreatingCourierSteps();

    @Test
    @Description("Проверка успешного создания курьера с корректными данными")
    public void creatingCourier() {
        RequestBodyForCreatingCourier requestBodyForCreatingCourier = new RequestBodyForCreatingCourier("Yury_P", "1234", "Yury");
        Response responseAfterCreatingCourier = creatingCourierSteps.createCourier(requestBodyForCreatingCourier);
        creatingCourierSteps.verifyStatus(responseAfterCreatingCourier, SC_CREATED);

        String actualJson = creatingCourierSteps.getFormattedResponseBody(responseAfterCreatingCourier);
        creatingCourierSteps.verifyResponseBody(Constants.SUCCESS_RESPONSE_BODY_AFTER_CREATING_COURIER, actualJson);
    }

    @Test
    @Description("Проверка создания двух одинаковых курьеров")
    public void tryToCreateTwoIdenticalCouriers() {
        RequestBodyForCreatingCourier requestBodyForCreatingCourier = new RequestBodyForCreatingCourier("Yury_P", "1234", "Yury");

        creatingCourierSteps.createCourier(requestBodyForCreatingCourier);

        Response secondResponseAfterCreatingCourier = creatingCourierSteps.createCourier(requestBodyForCreatingCourier);
        creatingCourierSteps.verifyStatus(secondResponseAfterCreatingCourier, SC_CONFLICT);

        String actualJson = creatingCourierSteps.getFormattedErrorResponseBody(secondResponseAfterCreatingCourier);
        creatingCourierSteps.verifyResponseBody(Constants.ERROR_RESPONSE_BODY_FOR_DUPLICATE_COURIER, actualJson);
    }

    @Test
    @Description("Проверка создания курьера без пароля")
    public void tryToCreateCourierWithoutPassword() {
        // Создаем объект RequestBodyForCreatingCourier без password
        RequestBodyForCreatingCourier requestBody = new RequestBodyForCreatingCourier("Yury_P", null, "Yury");

        Response responseAfterCreatingCourier = creatingCourierSteps.createCourier(requestBody);
        creatingCourierSteps.verifyStatus(responseAfterCreatingCourier, SC_BAD_REQUEST);

        String actualJson = creatingCourierSteps.getFormattedErrorResponseBody(responseAfterCreatingCourier);
        creatingCourierSteps.verifyResponseBody(Constants.ERROR_RESPONSE_BODY_FOR_INVALID_COURIER_DATA, actualJson);
    }

    @Test
    @Description("Проверка создания курьера без логина")
    public void tryToCreateCourierWithoutLogin() {
        // Создаем объект RequestBodyForCreatingCourier без login
        RequestBodyForCreatingCourier requestBody = new RequestBodyForCreatingCourier(null, "1234", "Yury");

        Response responseAfterCreatingCourier = creatingCourierSteps.createCourier(requestBody);
        creatingCourierSteps.verifyStatus(responseAfterCreatingCourier, SC_BAD_REQUEST);

        String actualJson = creatingCourierSteps.getFormattedErrorResponseBody(responseAfterCreatingCourier);
        creatingCourierSteps.verifyResponseBody(Constants.ERROR_RESPONSE_BODY_FOR_INVALID_COURIER_DATA, actualJson);
    }

    @Test
    @Description("Проверка создания курьера без имени")
    public void tryToCreateCourierWithoutFirstName() {
        // Создаем объект RequestBodyForCreatingCourier без firstName
        RequestBodyForCreatingCourier requestBody = new RequestBodyForCreatingCourier("Yury_P", "1234", null);

        Response responseAfterCreatingCourier = creatingCourierSteps.createCourier(requestBody);

        creatingCourierSteps.verifyStatus(responseAfterCreatingCourier, SC_BAD_REQUEST);
        String actualJson = creatingCourierSteps.getFormattedErrorResponseBody(responseAfterCreatingCourier);
        creatingCourierSteps.verifyResponseBody(Constants.ERROR_RESPONSE_BODY_FOR_INVALID_COURIER_DATA, actualJson);
    }

    @After
    public void setDown() {
        RequestBodyForLoginCourier requestBodyForLoginCourier = new RequestBodyForLoginCourier("Yury_P", "1234");
        Response responseAfterLoginCourier = creatingCourierSteps.loginCourier(requestBodyForLoginCourier);
        if (responseAfterLoginCourier.getStatusCode() == SC_OK) {
            String courierId = creatingCourierSteps.extractCourierId(responseAfterLoginCourier);
            creatingCourierSteps.deleteCourierById(courierId);
        } else {
            System.out.println("Wrong");
        }
    }
}