import business.LoginPageBO;
import business.MainPageBO;
import dataprovider.UserDataProvider;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.junit.Assert;
import org.testng.annotations.*;
import driver.DriverProvider;
import static constants.Constants.*;
import model.Email;
import java.io.IOException;

public class GmailPageTest {
    private static final Logger logger = LogManager.getLogger(GmailPageTest.class);

    @BeforeMethod
    public void setUp() {
        System.setProperty(DRIVER_NAME, PATH_TO_CHROME_DRIVER);
        DriverProvider.getWebDriver().get(URL);
    }

    @DataProvider(parallel = true)
    private Object[][] loginData() throws IOException {
        return UserDataProvider.getJsonData();
    }

    @Test(dataProvider = "loginData")
    public void correctlySavedDataTest(String email, String password, String receiverEmail, String subject, String text) {
        Email enteredEmail = new Email();
        enteredEmail.setEmailAddress(receiverEmail);
        enteredEmail.setSubject(subject);
        enteredEmail.setBody(text);
        LoginPageBO loginBO = new LoginPageBO();
        MainPageBO mainPageBO = new MainPageBO();
        loginBO.login(email, password);
        mainPageBO.composeEmail(receiverEmail, subject, text);
        mainPageBO.openSavedDraft();
        Email savedEmail = mainPageBO.getSavedEmail();
        Assert.assertTrue(enteredEmail.equals(savedEmail));
        logger.info("Saved email equals to entered");
        mainPageBO.sendLetter();
    }

    @AfterMethod
    public void tearDown() {
        DriverProvider.removeDriver();
    }
}