package original;

import com.github.javafaker.Faker;
import io.qameta.allure.Description;
import io.restassured.response.Response;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import original.requestbodies.RequestBodyForCreatingOrder;
import original.stepsfortests.CreatingCourierSteps;
import original.stepsfortests.CreatingOrderSteps;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

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
        Faker faker = new Faker();

        return new Object[][]{
                generateOrderData(faker, new String[]{"GREY", "BLACK"}),

                generateOrderData(faker, new String[]{"GREY"}),

                generateOrderData(faker, new String[]{"BLACK"}),

                generateOrderData(faker, null)
        };
    }


    private static Object[] generateOrderData(Faker faker, String[] color) {
        return new Object[]{
                faker.name().firstName(),
                faker.name().lastName(),
                faker.address().fullAddress(),
                faker.number().digits(2),
                faker.phoneNumber().phoneNumber(),
                faker.number().numberBetween(1, 10),
                generateFutureDate(faker),
                faker.lorem().sentence(),
                color
        };
    }


    private static String generateFutureDate(Faker faker) {
        LocalDate futureDate = LocalDate.now().plusDays(faker.number().numberBetween(1, 365));
        return futureDate.format(DateTimeFormatter.ofPattern("yyyy-MM-dd"));
    }

    @Test
    @Description("Проверка успешного создания заказа")
    public void creatingOrder() {
        RequestBodyForCreatingOrder requestBodyForCreatingOrder =
                new RequestBodyForCreatingOrder(firstName, lastName, address, metroStation, phone, rentTime, deliveryDate, comment, color);

        Response responseAfterCreatingOrder = creatingOrderSteps.createOrder(requestBodyForCreatingOrder);

        creatingCourierSteps.verifyStatus(responseAfterCreatingOrder, SC_CREATED);

        String trackId = creatingOrderSteps.getTrackId(responseAfterCreatingOrder);

        String expectedJson = creatingOrderSteps.generateExpectedJson(trackId);

        creatingOrderSteps.assertCreateOrderResponseBody(responseAfterCreatingOrder, expectedJson);
    }
}