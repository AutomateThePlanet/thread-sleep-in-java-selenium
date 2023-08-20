/*
 * Copyright 2021 Automate The Planet Ltd.
 * Author: Anton Angelov
 * Licensed under the Apache License, Version 2.0 (the "License");
 * You may not use this file except in compliance with the License.
 * You may obtain a copy of the License at http://www.apache.org/licenses/LICENSE-2.0
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package hardcodedpauses;

import io.github.bonigarcia.wdm.WebDriverManager;
import models.ProductDetails;
import org.apache.commons.lang3.StringUtils;
import org.junit.jupiter.api.*;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.interactions.Actions;

import java.util.concurrent.TimeUnit;

public class ProductDetailsTests {
    private WebDriver driver;
    private Actions actions;

    @BeforeEach
    public void testInit() {
        WebDriverManager.chromedriver().setup();
        driver = new ChromeDriver();
        actions = new Actions(driver);
        driver.manage().window().maximize();
    }

    @AfterEach
    public void testCleanup() throws InterruptedException {
        driver.quit();
    }

    @Test
    public void correctInformationDisplayedInCompareScreen_whenOpenProductFromSearchResults_TwoProducts() throws InterruptedException {
        // Arrange
        var expectedProduct1 = new ProductDetails();
        expectedProduct1.setName("iPod Touch");
        expectedProduct1.setId(32);
        expectedProduct1.setPrice("$194.00");
        expectedProduct1.setModel("Product 5");
        expectedProduct1.setBrand("Apple");
        expectedProduct1.setWeight("5.00kg");

        var expectedProduct2 = new ProductDetails();
        expectedProduct2.setName("iPod Shuffle");
        expectedProduct2.setId(34);
        expectedProduct2.setPrice("$182.00");
        expectedProduct2.setModel("Product 7");
        expectedProduct2.setBrand("Apple");
        expectedProduct2.setWeight("5.00kg");

        // Act
        driver.navigate().to("https://ecommerce-playground.lambdatest.io/");

        compareProduct("ip", expectedProduct1.getId());
        compareProduct("ip", expectedProduct2.getId());

        var compareButton = driver.findElement(By.xpath("//a[@aria-label='Compare']"));
        compareButton.click();

        Thread.sleep(2000);

        // Assert

        assertCompareProductDetails(expectedProduct1, 1);
        assertCompareProductDetails(expectedProduct2, 2);
    }

    private void compareProduct(String searchText, Integer productId) throws InterruptedException {
        var searchInput = driver.findElement(By.xpath("//input[@name='search']"));
        searchInput.sendKeys(searchText);

        Thread.sleep(5000);

        var autocompleteItemXPath = String.format("//ul[contains(@class, 'dropdown-menu autocomplete')]/li/div/h4/a[contains(@href, 'product_id=%d')]", productId);
        var autocompleteItem = driver.findElement(By.xpath(autocompleteItemXPath));
        autocompleteItem.click();

        var compareButton = driver.findElement(By.xpath("//button[@title='Compare this Product']"));
        compareButton.click();
    }

    private void assertCompareProductDetails(ProductDetails expectedProductDetails, int productCompareIndex) {
        var productName2 = driver.findElement(By.xpath(getCompareProductDetailsCellXPath("Product", productCompareIndex)));
        var productPrice2 = driver.findElement(By.xpath(getCompareProductDetailsCellXPath("Price", productCompareIndex)));
        var productModel2 = driver.findElement(By.xpath(getCompareProductDetailsCellXPath("Model", productCompareIndex)));
        var productBrand2 = driver.findElement(By.xpath(getCompareProductDetailsCellXPath("Brand", productCompareIndex)));
        var productWeight2 = driver.findElement(By.xpath(getCompareProductDetailsCellXPath("Weight", productCompareIndex)));

        Assertions.assertEquals(expectedProductDetails.getName(), productName2.getText());
        Assertions.assertEquals(expectedProductDetails.getPrice(), productPrice2.getText());
        Assertions.assertEquals(expectedProductDetails.getModel(), productModel2.getText());
        Assertions.assertEquals(expectedProductDetails.getBrand(), productBrand2.getText());
        Assertions.assertEquals(expectedProductDetails.getWeight(), productWeight2.getText());
    }

    private String getCompareProductDetailsCellXPath(String cellName, int productCompareIndex) {
        String cellXpath = String.format("//table/tbody/tr/td[text()='%s']/following-sibling::td[%d]", cellName, productCompareIndex);
        return cellXpath;
    }
}