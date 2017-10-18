package core;

import org.openqa.selenium.*;
import org.openqa.selenium.safari.SafariDriver;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import java.util.Arrays;
import java.util.Base64;
import java.util.Scanner;
import java.util.concurrent.TimeUnit;
import java.util.logging.*;

public class Safari {

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

        String url = "http://facebook.com/";
        String email_address = "avalonkl@gmail.com";

        cipher = Cipher.getInstance("AES");
        String encryptedpassword = "lMDpZ33AaSST9XgN06DKow==";
        String key = myMAC();
        key = key.replaceAll("-", ":");  // 00-0c-29-3e-07-e6  =>  00:0c:29:3e:07:e6
        SecretKeySpec sk = new SecretKeySpec(Arrays.copyOf(key.getBytes("UTF-8"), 16), "AES");
        String password = decrypt(encryptedpassword, sk);

        if (!System.getProperty("os.name").contains("Mac")) {throw new IllegalArgumentException("Safari is available only on Mac");}

        driver = new SafariDriver();
        //  driver.manage().timeouts().implicitlyWait(10, TimeUnit.SECONDS);
        driver.manage().window().maximize();

        WebDriverWait wait = new WebDriverWait(driver, 15);

        driver.get(url);


        //Thread.sleep(1000); // Pause in milleseconds (1000 – 1 sec)

        String title = driver.getTitle();
        String copyright = wait.until(ExpectedConditions.presenceOfElementLocated(By.xpath("//div[2]/div/div[3]/div/span"))).getText();

        Thread.sleep(1000);

        driver.findElement(By.id("email")).sendKeys(email_address);
        driver.findElement(By.id("pass")).sendKeys(password);
        driver.findElement(By.id("u_0_5")).click();

        //Thread.sleep(3000);
        wait.until(ExpectedConditions.presenceOfElementLocated(By.xpath("//div[1]/div/div/div/div[2]/div[1]/div[1]/div/a/span"))).click();

        //Thread.sleep(5000);
        WebElement amount = wait.until(ExpectedConditions.presenceOfElementLocated(By.xpath("//div[2]/ul/li[3]/a")));
        String friends = (String) ((JavascriptExecutor) driver).executeScript("arguments[0].getText();", amount);
        System.out.println("You have " + friends + " friends");

        //Thread.sleep(5000);

        WebElement settings = wait.until(ExpectedConditions.presenceOfElementLocated(By.id("userNavigationLabel")));
        ((JavascriptExecutor) driver).executeScript("arguments[0].click();", settings);

        //Thread.sleep(5000);

        WebElement logout = wait.until(ExpectedConditions.presenceOfElementLocated(By.xpath("//*[@id=\"js_in\"]/div/div/ul/li[1]/a/span/span")));
        ((JavascriptExecutor) driver).executeScript("arguments[0].click();", logout);

        //Thread.sleep(1000);
        driver.quit();

        System.out.println("Browser is: Safari");
        System.out.println("Title of the page: " + title);
        System.out.println("Copyright: " + copyright);
        System.out.println("You have " + friends + " friends");
    }
}
