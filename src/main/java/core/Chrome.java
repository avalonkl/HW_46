package core;

import org.openqa.selenium.*;
import org.openqa.selenium.chrome.*;

import java.util.Arrays;
import java.util.Base64;
import java.util.Scanner;
import java.util.concurrent.TimeUnit;
import org.openqa.selenium.support.ui.*;

import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import java.util.logging.*;

public class Chrome {

    static WebDriver driver;

    static Cipher cipher;

    public static String myMAC () throws Exception {
        String mac_address;
        String cmd_mac = "ifconfig en0";
        String cmd_win = "cmd /C for /f \"usebackq tokens=1\" %a in (`getmac ^| findstr Device`) do echo %a";

        if (System.getProperty("os.name").toUpperCase().contains("WINDOWS")) {
            mac_address = new Scanner(Runtime.getRuntime().exec(cmd_win).getInputStream()).useDelimiter("\\A").next().split(" ")[1];}
        else {mac_address = new Scanner(Runtime.getRuntime().exec(cmd_mac).getInputStream()).useDelimiter("\\A").next().split(" ")[4];}
        mac_address = mac_address.toLowerCase().replaceAll("-", ":");
        return mac_address;
    }

    public static String encrypt(String plainText, SecretKey secretKey) throws Exception {
        cipher.init(Cipher.ENCRYPT_MODE, secretKey);
        String encryptedText = Base64.getEncoder().encodeToString(cipher.doFinal(plainText.getBytes()));
        return encryptedText;}

    public static String decrypt(String encryptedText, SecretKey secretKey) throws Exception {
        cipher = Cipher.getInstance("AES");
        cipher.init(Cipher.DECRYPT_MODE, secretKey);
        String decryptedText = new String(cipher.doFinal(Base64.getDecoder().decode(encryptedText)));
        return decryptedText;}

    public static void main(String[] args) throws Exception {
        Logger logger = Logger.getLogger("");
        logger.setLevel(Level.OFF);

        String driverPath = "";

        String url = "http://facebook.com/";
        String email_address = "avalonkl@gmail.com";

        cipher = Cipher.getInstance("AES");
        String encryptedpassword = "lMDpZ33AaSST9XgN06DKow==";
        String key = myMAC();
        key = key.replaceAll("-", ":");  // 00-0c-29-3e-07-e6  =>  00:0c:29:3e:07:e6
        SecretKeySpec sk = new SecretKeySpec(Arrays.copyOf(key.getBytes("UTF-8"), 16), "AES");
        String password = decrypt(encryptedpassword, sk);

        if (System.getProperty("os.name").toUpperCase().contains("MAC"))
            driverPath = "./resources/webdrivers/mac/chromedriver";
        else if (System.getProperty("os.name").toUpperCase().contains("WINDOWS"))
            driverPath = "./resources/webdrivers/pc/chromedriver.exe";
        else throw new IllegalArgumentException("Unknown OS");

        System.setProperty("webdriver.chrome.driver", driverPath);
        System.setProperty("webdriver.chrome.silentOutput", "true");
        ChromeOptions option = new ChromeOptions();
        option.addArguments("disable-infobars");
        option.addArguments("--disable-notifications");
        if (System.getProperty("os.name").toUpperCase().contains("MAC"))
            option.addArguments("-start-fullscreen");
        else if (System.getProperty("os.name").toUpperCase().contains("WINDOWS"))
            option.addArguments("--start-maximized");
        else throw new IllegalArgumentException("Unknown OS");
        driver = new ChromeDriver(option);
        driver.manage().timeouts().implicitlyWait(15, TimeUnit.SECONDS);
        WebDriverWait wait = new WebDriverWait(driver, 15);

        driver.get(url);


        String title = driver.getTitle();
        String copyright = wait.until(ExpectedConditions.presenceOfElementLocated(By.xpath("//div[2]/div/div[3]/div/span"))).getText();

        wait.until(ExpectedConditions.presenceOfElementLocated(By.id("email"))).sendKeys(email_address);
        wait.until(ExpectedConditions.presenceOfElementLocated(By.id("pass"))).sendKeys(password);
        wait.until(ExpectedConditions.elementToBeClickable(By.id("u_0_2"))).click();

        wait.until(ExpectedConditions.elementToBeClickable(By.xpath("//div[1]/div/div/div/div[2]/div[1]/div[1]/div/a/span"))).click();

        String friends = wait.until(ExpectedConditions.presenceOfElementLocated(By.xpath("//div[2]/ul/li[3]/a"))).getText();

        wait.until(ExpectedConditions.elementToBeClickable(By.id("userNavigationLabel"))).click();
        wait.until(ExpectedConditions.elementToBeClickable(By.xpath("//li[14]/a/span/span"))).click();


        driver.quit();

        char[] chars = friends.toCharArray();
        friends = "";
        for (int i = 0; i < chars.length; i++){
            if ((int) chars[i] > 47 && (int) chars[i] < 58){
                friends += chars[i];
            }
        }

        System.out.println("Browser is: Chrome");
        System.out.println("Title of the page: " + title);
        System.out.println("Copyright: " + copyright);
        System.out.println("You have " + friends + " friends");
    }
}