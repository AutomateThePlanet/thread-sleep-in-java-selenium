package utilities;

import org.openqa.selenium.*;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import javax.security.auth.callback.Callback;
import java.time.Duration;
import java.util.function.Function;
import java.util.function.Supplier;

public class Wait {
    private WebDriver driver;

    public Wait(WebDriver driver) {
        this.driver = driver;
    }

    public WebElement retryWhileLoop(By locator, int timesToRetry) {
        WebElement foundElement = null;
        int repeat = 0;
        while(repeat <= timesToRetry) {
            try {
                foundElement = driver.findElement(locator);
                break;
            } catch(StaleElementReferenceException exc) {
                exc.printStackTrace();
            }

            repeat++;
        }

        if (foundElement == null) {
            throw new NotFoundException("The web element was not located");
        }

        return foundElement;
    }

    public WebElement retryWhileLoop(By locator) {
        // retry with default value
        return retryWhileLoop(locator, 5);
    }

    public WebElement retryForLoop(By locator, int timesToRetry) {
        WebElement foundElement = null;
        for(int repeat = 0; repeat <= timesToRetry; repeat++) {
            try {
                foundElement = driver.findElement(locator);
                break;
            } catch(StaleElementReferenceException exc) {
                exc.printStackTrace();
            }
        }

        if (foundElement == null) {
            throw new NotFoundException("The web element was not located");
        }

        return foundElement;
    }

    public boolean retryUsingForLoop_TryCatch(By locator, String value) {
        boolean outcome = false;
        for(int repeat=0; repeat<=3; repeat++) {
            try {
                driver.findElement(locator).sendKeys(value);
                outcome = true;
                break;
            } catch(StaleElementReferenceException exc) {
                exc.printStackTrace();
            }
        }
        return outcome;
    }

    public WebElement retryWhileLoop(By locator, int timesToRetry, Class<? extends Throwable> ... exceptionsToIgnore) {
        WebElement foundElement = null;
        int repeat = 0;
        boolean shouldThrowException = true;
        while(repeat <= timesToRetry) {
            try {
                shouldThrowException = true;
                foundElement = driver.findElement(locator);
                break;
            } catch(Exception exc) {
                for (var currentException : exceptionsToIgnore) {
                    if (currentException.isInstance(exc)) {
                        exc.printStackTrace();
                        repeat++;
                        shouldThrowException = false;
                        break;
                    }
                }

                if (shouldThrowException) {
                    throw exc;
                }
            }
        }

        if (foundElement == null) {
            throw new NotFoundException("The web element was not located");
        }

        return foundElement;
    }

    public void retry(Supplier action, int timesToRetry, Class<? extends Throwable> ... exceptionsToIgnore) {
        int repeat = 0;
        boolean shouldThrowException = true;
        while(repeat <= timesToRetry) {
            try {
                shouldThrowException = true;
                action.get();
                break;
            } catch(Exception exc) {
                for (var currentException : exceptionsToIgnore) {
                    if (currentException.isInstance(exc)) {
                        exc.printStackTrace();
                        repeat++;
                        shouldThrowException = false;
                        break;
                    }
                }

                if (shouldThrowException) {
                    throw exc;
                }
            }
        }
    }

    public void retry(Supplier action, Duration timeout, Duration sleepInterval, Class<? extends Throwable> ... exceptionsToIgnore) throws InterruptedException {
        boolean shouldThrowException = true;
        long start = System.currentTimeMillis();
        long end = start + timeout.toMillis();
        while(System.currentTimeMillis() < end) {
            try {
                shouldThrowException = true;
                action.get();
                break;
            } catch(Exception exc) {
                for (var currentException : exceptionsToIgnore) {
                    if (currentException.isInstance(exc)) {
                        exc.printStackTrace();
                        shouldThrowException = false;
                        break;
                    }
                }

                if (shouldThrowException) {
                    throw exc;
                }
            }

            Thread.sleep(sleepInterval.toMillis());
        }
    }

    public WebElement getElement(By locator) {
        return getElement(locator, 30);
    }

    public WebElement getElement(By locator, int timeoutInSeconds) {
        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(timeoutInSeconds));
        return wait.until(ExpectedConditions.refreshed(ExpectedConditions.presenceOfElementLocated(locator)));
    }
}
