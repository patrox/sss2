package pl.najda.sss2;

import java.io.File;
import java.io.IOException;
import java.io.UncheckedIOException;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Stream;
import org.codehaus.jackson.JsonParser;
import org.codehaus.jackson.map.ObjectMapper;
import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.Keys;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.firefox.FirefoxBinary;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.firefox.FirefoxProfile;
import org.openqa.selenium.interactions.Actions;
import org.openqa.selenium.remote.UnreachableBrowserException;
import pl.najda.sss2.model.Site;

/**
 * @author patnaj
 */
public class Main {

    static void processSite(WebDriver driver, Site site) throws UnreachableBrowserException {
        // 1. go to the url
        // 2. if it's required (and not already done) authenticate, using the specified credentials
        // TODO: 3. if there are any other actions specified - process them
        // 4. wait for a specified duration (in seconds)

        System.out.println("Processing site: " + site);

        driver.get(site.getUrl());

        if (site.isAuthRequired()) {
            List<WebElement> elems = null;

            elems = driver.findElements(By.id(site.getAuth().getUsernameField()));
            if (!elems.isEmpty()) {

                elems.get(0).sendKeys(site.getAuth().getUsername());

                if (site.isRememberRequired()) {
                    elems = driver.findElements(By.id(site.getAuth().getRememberField()));

                    // causes  org.openqa.selenium.ElementNotVisibleException: Element is not currently visible and so may not be interacted with                    
                    //elems.get(0).click();
                    // fix thanks to http://stackoverflow.com/questions/6101461/selenium-2-0-element-is-not-currently-visible
                    ((JavascriptExecutor) driver).executeScript("arguments[0].checked = true;", elems.get(0));
                }

                elems = driver.findElements(By.id(site.getAuth().getPasswordField()));
                elems.get(0).sendKeys(site.getAuth().getPassword());
                elems.get(0).submit();
            }
        }
        try {
            Thread.sleep(site.getDuration() * 1000);
        } catch (InterruptedException ex) {
            Logger.getLogger(Main.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    static void maximize(WebDriver driver) {
        driver.manage().window().maximize();

        Actions builder = new Actions(driver);
        builder.sendKeys(Keys.F11).perform();
    }

    static WebDriver getDriver() {
        FirefoxBinary binary = new FirefoxBinary(new File("/home/patnaj/firefox/firefox"));
        FirefoxProfile profile = new FirefoxProfile();

//        if (Integer.parseInt(properties.getProperty("use_proxy")) == 1) {
//            String PROXY = String.format("%s:%s",
//                    properties.getProperty("proxy_host"),
//                    properties.getProperty("proxy_port"));
//
//            org.openqa.selenium.Proxy proxy = new org.openqa.selenium.Proxy();
//            proxy.setHttpProxy(PROXY)
//                    .setSslProxy(PROXY);
//            profile.setProxyPreferences(proxy);
//        }
        WebDriver driver = new FirefoxDriver(binary, profile);

        return driver;
    }

    public static void main(String[] args) {
        ObjectMapper mapper = new ObjectMapper();
        mapper.configure(JsonParser.Feature.ALLOW_UNQUOTED_FIELD_NAMES, true);
        mapper.configure(JsonParser.Feature.ALLOW_SINGLE_QUOTES, true);

        File sitesDir = new File("sites");

        WebDriver firefox = getDriver();
        maximize(firefox);

        while (true) {
            Stream.of(sitesDir.listFiles())
                .filter(file -> file.getName().endsWith("json"))
                .map((file) -> {
                    try {
                        return mapper.readValue(file, Site.class);
                    } catch (IOException ex) {
                        throw new UncheckedIOException(ex);
                    }
                })
                .sorted((site1, site2) -> Integer.compare(site1.getOrder(), site2.getOrder()))
                .forEach(
                    site -> {
                        try {
                            processSite(firefox, site);
                        } catch (UnreachableBrowserException ex) {
                            System.out.println("Lost connection with the browser!");
                            // the other option (probably better) is to restart the browser
                            System.exit(1);
                        }
//                            site -> System.out.println(site)
                    }
                );
        }
    }
}
