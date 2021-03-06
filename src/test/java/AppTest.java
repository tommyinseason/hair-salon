import org.fluentlenium.adapter.FluentTest;
import static org.junit.Assert.*;
import org.junit.*;
import org.junit.ClassRule;
import org.junit.Test;
import org.openqa.selenium.WebDriver;
import org.sql2o.*;
import org.openqa.selenium.htmlunit.HtmlUnitDriver;
import static org.fluentlenium.core.filter.FilterConstructor.*;
import static org.assertj.core.api.Assertions.assertThat;

public class AppTest extends FluentTest {
  public WebDriver webDriver = new HtmlUnitDriver();

  @Override
   public WebDriver getDefaultDriver() {
     return webDriver;
   }

   @ClassRule
   public static ServerRule server = new ServerRule();


   @Before
   public void setUp(){
     DB.sql2o = new Sql2o("jdbc:postgresql://localhost:5432/hair_salon_test", null, null);

   }
   @After
     public void tearDown() {
     try(Connection con = DB.sql2o.open()) {
       String deleteClientsQuery = "DELETE FROM clients *;";
       String deleteStylistsQuery = "DELETE FROM stylists *;";
       con.createQuery(deleteClientsQuery).executeUpdate();
       con.createQuery(deleteStylistsQuery).executeUpdate();
     }
   }
   @Test
   public void rootTest() {
     goTo("http://localhost:4567/");
     assertThat(pageSource()).contains("Stylists and Clients");
     assertThat(pageSource()).contains("View Stylists List");
     assertThat(pageSource()).contains("Add a New Stylist");
   }

   @Test
   public void stylistIsCreatedTest() {
     goTo("http://localhost:4567/");
     click("a", withText("Add a New Stylist"));
     fill("#stylistName").with("Mary");
     submit(".btn");
     assertThat(pageSource()).contains("Your stylist has been saved.");
   }

   @Test
   public void stylistIsDisplayedTest() {
     Stylist myStylist = new Stylist("Mary");
     myStylist.save();
     String stylistPath = String.format("http://localhost:4567/stylists/%d", myStylist.getId());
     goTo(stylistPath);
     assertThat(pageSource()).contains("Mary");
   }

   @Test
   public void stylistShowPageDisplaysName() {
     goTo("http://localhost:4567/stylists/new");
     fill("#stylistName").with("Mary");
     submit(".btn");
     click("a", withText("View Stylists"));
     click("a", withText("Mary"));
     assertThat(pageSource()).contains("Mary");
   }

   @Test
     public void stylistClientsFormIsDisplayed() {
       goTo("http://localhost:4567/stylists/new");
       fill("#stylistName").with("Tami");
       submit(".btn");
       click("a", withText("View Stylists"));
       click("a", withText("Tami"));
       click("a", withText("Add a new client"));
       assertThat(pageSource()).contains("Add a new client:");
     }

   @Test
     public void clientIsAddedAndDisplayed() {
       goTo("http://localhost:4567/stylists/new");
       fill("#stylistName").with("Mary");
       submit(".btn");
       click("a", withText("View Stylists"));
       click("a", withText("Mary"));
       click("a", withText("Add a new client"));
       fill("#clientName").with("Stacy");
       submit(".btn");
       click("a", withText("View stylists"));
       click("a", withText("Mary"));
       assertThat(pageSource()).contains("Stacy");
     }

   @Test
     public void allClientsDisplayNameOnStylistPage() {
       Stylist myStylist = new Stylist("Mary");
       myStylist.save();
       Client firstClient = new Client("Trisha", myStylist.getId());
       firstClient.save();
       Client secondClient = new Client("Stacy", myStylist.getId());
       secondClient.save();
       String stylistPath = String.format("http://localhost:4567/stylists/%d", myStylist.getId());
       goTo(stylistPath);
       assertThat(pageSource()).contains("Trisha");
       assertThat(pageSource()).contains("Stacy");
     }
 }
