package original;

import io.qameta.allure.Description;
import io.restassured.response.Response;
import org.junit.Before;
import org.junit.Test;
import original.stepsfortests.ListOfOrdersSteps;

public class ListOfOrdersTest extends BaseTest {

    ListOfOrdersSteps listOfOrdersSteps = new ListOfOrdersSteps();

    @Test
    @Description("Проверка, что список заказов не пуст")
    public void listOfOrdersIsNotNull() {
        Response responseAfterGettingListOfOrders = listOfOrdersSteps.getListOfOrders();

        listOfOrdersSteps.assertOrdersListIsNotNull(responseAfterGettingListOfOrders);
    }
}