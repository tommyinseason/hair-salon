import java.util.Map;
import java.util.List;
import java.util.HashMap;
import static spark.Spark.*;
import spark.ModelAndView;
import spark.template.velocity.VelocityTemplateEngine;

public class App {

  public static void main(String[] args) {
    staticFileLocation("/public");
    String layout = "templates/layout.vtl";

    get("/", (request, response) -> {
         HashMap<String, Object> model = new HashMap<String, Object>();
         model.put("template", "templates/index.vtl");
         return new ModelAndView(model, layout);
       }, new VelocityTemplateEngine());

    post("/stylists", (request, response) -> {
        HashMap<String, Object> model = new HashMap<String, Object>();
        String stylistName = request.queryParams("stylistName");
        Stylist newStylist = new Stylist(stylistName);
        newStylist.save();
        model.put("template", "templates/stylist-success.vtl");
        return new ModelAndView(model, layout);
      }, new VelocityTemplateEngine());

    get("/stylists", (request, response) -> {
        HashMap<String, Object>model = new HashMap<String, Object>();
        model.put("stylists", Stylist.all());
        model.put("template", "templates/stylists.vtl");
        return new ModelAndView(model, layout);
      }, new VelocityTemplateEngine());

    get("/stylists/new", (request, response) -> {
        HashMap<String, Object> model = new HashMap<String, Object>();
        model.put("template", "templates/stylist-form.vtl");
        return new ModelAndView(model, layout);
      }, new VelocityTemplateEngine());

    get("/stylists/:id", (request, response) -> {
       HashMap<String, Object> model = new HashMap<String, Object>();
       Stylist stylist = Stylist.find(Integer.parseInt(request.params(":id")));
       model.put("stylist", stylist);
       model.put("template", "templates/stylist.vtl");
       return new ModelAndView(model, layout);
     }, new VelocityTemplateEngine());

     get("/stylists/:id/clients/new", (request, response) -> {
         HashMap<String, Object> model = new HashMap<String, Object>();
         Stylist stylist = Stylist.find(Integer.parseInt(request.params(":id")));
         model.put("stylist", stylist);
         model.put("template", "templates/client-form.vtl");
         return new ModelAndView(model, layout);
       }, new VelocityTemplateEngine());

     get("/stylists/:stylist_id/clients/:id", (request, response) -> {
         HashMap<String, Object> model = new HashMap<String, Object>();
         Stylist stylist = Stylist.find(Integer.parseInt(request.queryParams("stylistid")));
         Client client = Client.find(Integer.parseInt(request.params(":id")));
         model.put("stylist", stylist);
         model.put("client", client);
         model.put("template", "templates/client.vtl");
         return new ModelAndView(model, layout);
       }, new VelocityTemplateEngine());


    post("/clients", (request, response) -> {
       HashMap<String, Object> model = new HashMap<String, Object>();
       Stylist stylist = Stylist.find(Integer.parseInt(request.queryParams("stylistid")));
       String clientName = request.queryParams("clientName");
       Client newClient = new Client(clientName, stylist.getId());
       newClient.save();
       model.put("stylist", stylist);
       model.put("template", "templates/stylist-clients-success.vtl");
       return new ModelAndView(model, layout);
     }, new VelocityTemplateEngine());
  }
}
