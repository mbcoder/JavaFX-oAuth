package com.mycompany.app;

import com.sun.net.httpserver.HttpContext;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpServer;
import java.io.IOException;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import javafx.application.HostServices;
import javafx.stage.Stage;

public class oAuth {
  private String clientID;
  private String redirectURI;
  private String baseUrl;
  private HostServices hostServices;
  private HttpServer httpServer;

  private Stage stage;

  public oAuth (String clientID, String redirectURI, String url, Stage stage, HostServices hostServices) {
    this.clientID = clientID;
    this.redirectURI = redirectURI;
    this.baseUrl = url;
    this.stage = stage;
    this.hostServices = hostServices;
  }

  public void authenticate() {

    // set up a http server to listen to redirect
    try {
      httpServer = HttpServer.create(new InetSocketAddress("localhost", 1234), 0);
      HttpContext context = httpServer.createContext("/auth");
      context.setHandler(exchange -> handleRequest(exchange));
      httpServer.start();

      // open up default browser with login page
      String loginURL = baseUrl + "/sharing/oauth2/authorize?client_id=" +
      clientID + "&response_type=code&redirect_uri=" + redirectURI;

      hostServices.showDocument(loginURL);

    } catch (IOException e) {
      throw new RuntimeException(e);
    }
  }

  private void handleRequest(HttpExchange exchange) throws IOException {

    // crude capture of the auth code needed to get the token
    System.out.println("message : " + exchange.getRequestURI().toString());

    // output something in the web browser to say go back to your app
    String response = "Please return to your app";
    exchange.sendResponseHeaders(200, response.getBytes().length);//response code and length
    OutputStream os = exchange.getResponseBody();
    os.write(response.getBytes());
    os.close();

    // stop the server
    this.httpServer.stop(0);

    // attempt to return focus to the JavaFX app, but this doesn't work
    this.stage.requestFocus();
  }



}
