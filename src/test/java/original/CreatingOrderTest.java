package original;

import io.qameta.allure.Description;
import io.restassured.response.Response;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import original.requestbodies.RequestBodyForCreatingOrder;
import original.stepsfortests.CreatingCourierSteps;
import original.stepsfortests.CreatingOrderSteps;

import static org.apache.http.HttpStatus.*;

@RunWith(Parameterized.class)
public class CreatingOrderTest extends BaseTest {

    CreatingCourierSteps creatingCourierSteps = new CreatingCourierSteps();
    CreatingOrderSteps creatingOrderSteps = new CreatingOrderSteps();

    private final String firstName;
    private final String lastName;
    private final String address;
    private final String metroStation;
    private final String phone;
    private final Integer rentTime;
    private final String deliveryDate;
    private final String comment;
    private final String[] color;

    public CreatingOrderTest(String firstName, String lastName, String address, String metroStation, String phone, Integer rentTime, String deliveryDate, String comment, String[] color) {
        this.firstName = firstName;
        this.lastName = lastName;
        this.address = address;
        this.metroStation = metroStation;
        this.phone = phone;
        this.rentTime = rentTime;
        this.deliveryDate = deliveryDate;
        this.comment = comment;
        this.color = color;
    }

    @Parameterized.Parameters
    public static Object[][] getData() {
        return new Object[][]  {
                {"Сергей", "Иванов", "Тибетская, 23 кв.", "15", "+7 900 123 45 67", 7, "2025-06-15", "Rukia, let's save the world", new String[]{"GREY", "BLACK"}},
                {"Артем", "Пименов", "Москва, 55 кв.", "12", "+7 905 678 90 12", 6, "2025-03-21", "Everything is fine", new String[]{"GREY"}},
                {"Борис", "Бритва", "Москва, 88/7", "10", "+7 701 123 45 67", 8, "2025-05-12", "Let's move forward", new String[]{"BLACK"}},
                {"Сириус", "Блэк", "Москва, Азкабановая", "5", "+7 702 987 65 43", 9, "2025-08-08", "Apple time", null},
        };
    }

    @Test
    @Description("Проверка успешного создания заказа")
    public void CreatingOrder() {
        RequestBodyForCreatingOrder requestBodyForCreatingOrder =
                new RequestBodyForCreatingOrder(firstName, lastName, address, metroStation, phone, rentTime, deliveryDate, comment, color);

        Response responseAfterCreatingOrder = creatingOrderSteps.createOrder(requestBodyForCreatingOrder);

        creatingCourierSteps.verifyStatus(responseAfterCreatingOrder, SC_CREATED);

        String trackId = creatingOrderSteps.getTrackId(responseAfterCreatingOrder);

        String expectedJson = creatingOrderSteps.generateExpectedJson(trackId);

        creatingOrderSteps.assertCreateOrderResponseBody(responseAfterCreatingOrder, expectedJson);
    }
}