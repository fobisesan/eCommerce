import com.aventstack.extentreports.ExtentReports;
import com.aventstack.extentreports.ExtentTest;
import com.aventstack.extentreports.Status;
import com.aventstack.extentreports.reporter.ExtentHtmlReporter;
import com.aventstack.extentreports.reporter.configuration.Theme;
import org.openqa.selenium.*;
import org.openqa.selenium.chrome.ChromeDriver;
import org.testng.Assert;
import org.testng.ITestResult;
import org.testng.annotations.*;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Properties;

public class OnlineStoreTest {

    public WebDriver driver;
    public ExtentHtmlReporter htmlReporter;
    public ExtentReports extent;
    public ExtentTest test;

    Properties props;

    @BeforeClass
    public void setExtent() {
        String dateNameReport = new SimpleDateFormat("yyyyMHhmmss").format(new Date());
        htmlReporter = new ExtentHtmlReporter(props.getProperty("basePath") + dateNameReport + ".html");
        htmlReporter.config().setDocumentTitle(props.getProperty("documentTitle"));
        htmlReporter.config().setReportName(props.getProperty("reportName"));
        htmlReporter.config().setTheme(Theme.DARK);

        extent = new ExtentReports();
        extent.attachReporter(htmlReporter);
    }

    @BeforeTest
    public void setup() throws IOException {
        props = new Properties();
        props.load(getClass().getClassLoader().getResourceAsStream("Configuration.properties"));
        System.setProperty(props.getProperty("browserDriver"), (props.getProperty("driverPath")));
        driver = new ChromeDriver();
        driver.manage().window().maximize();
        driver.get(props.getProperty("url"));
    }

    @Test(priority = 1)
    public void addingSonyVaioToCart() throws InterruptedException {
        test = extent.createTest(props.getProperty("testOneName"));
        driver.findElement(By.xpath("//*[@onclick=\"byCat('notebook')\"]")).click();
        Thread.sleep(3000);
        driver.findElement(By.xpath("//*[@href=\"prod.html?idp_=9\"]")).click();
        Thread.sleep(3000);
        driver.findElement(By.xpath("//*[@onclick=\"addToCart(9)\"]")).click();
        Thread.sleep(3000);
        Assert.assertTrue(driver.switchTo().alert().getText().contains(props.getProperty("addToCart")));
        //System.out.println(driver.switchTo().alert().getText());
        driver.switchTo().alert().accept();
    }

    @Test(dependsOnMethods = { "addingSonyVaioToCart" })
    public void verifyCostOfCartIsAsExpected() throws InterruptedException {
        test = extent.createTest(props.getProperty("testTwoName"));
        WebElement cart = driver.findElement(By.xpath("//*[@id=\"cartur\"]"));
        Thread.sleep(3000);
        cart.click();
        Thread.sleep(3000);
        WebElement productPrice = driver.findElement(By.xpath("//div/h3"));
        Thread.sleep(3000);
        System.out.println(productPrice.getText());
        Assert.assertTrue(productPrice.getText().contains(props.getProperty("itemAddedValidationPrice")));
    }

    @Test(dependsOnMethods = { "verifyCostOfCartIsAsExpected" })
    public void placeOrder() throws InterruptedException {
        test = extent.createTest(props.getProperty("testThreeName"));
        driver.findElement(By.xpath("//*[@class=\"btn btn-success\"]")).click();
        Thread.sleep(3000);
        WebElement nameInput = driver.findElement(By.xpath("//*[@id=\"name\"]"));
        nameInput.sendKeys("Feyi Obisesan");
        WebElement countryInput = driver.findElement(By.xpath("//*[@id=\"country\"]"));
        countryInput.sendKeys("United Kingdom");
        WebElement cityInput = driver.findElement(By.xpath("//*[@id=\"city\"]"));
        cityInput.sendKeys("London");
        WebElement creditCardInput = driver.findElement(By.xpath("//*[@id=\"card\"]"));
        creditCardInput.sendKeys("1234 5647 9101 1121");
        WebElement monthInput = driver.findElement(By.xpath("//*[@id=\"month\"]"));
        monthInput.sendKeys("Jan");
        WebElement yearInput = driver.findElement(By.xpath("//*[@id=\"year\"]"));
        yearInput.sendKeys("2020");
        Thread.sleep(3000);
        WebElement purchase = driver.findElement(By.xpath("//*[@onclick=\"purchaseOrder()\"]"));
        Thread.sleep(3000);
        purchase.click();
        Thread.sleep(3000);
        WebElement purchaseConfirmation = driver.findElement(By.xpath("//*[@class=\"sweet-alert  showSweetAlert visible\"]"));
        Assert.assertTrue(purchaseConfirmation.getText().contains(props.getProperty("purchaseConfirmed")));
        Thread.sleep(3000);
        driver.findElement(By.xpath("//*[@class=\"confirm btn btn-lg btn-primary\"]")).click();
    }

    @AfterMethod
    public void tearDown(ITestResult result) {
        if(result.getStatus() == ITestResult.FAILURE){
            test.log(Status.FAIL, "Test case failed: " + result.getName());
            test.log(Status.FAIL, "Test case failed: " + result.getThrowable());
        }
        else if(result.getStatus() == ITestResult.SKIP){
            test.log(Status.SKIP, "Test case skipped: " + result.getName());
        }
        else if(result.getStatus() == ITestResult.SUCCESS){
            test.log(Status.PASS, "Test case passed: " + result.getName());
        }
        extent.flush();
    }

    @AfterSuite
    public void closeBrowser(){
        driver.quit();
    }
}
