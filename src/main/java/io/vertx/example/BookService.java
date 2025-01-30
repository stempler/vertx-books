package io.vertx.example;

import java.io.IOException;

import io.vertx.core.Vertx;
import io.vertx.core.json.Json;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.web.Router;
import io.vertx.ext.web.RoutingContext;
import io.vertx.ext.web.handler.BodyHandler;


/**
 * Simple example book service.
 */
public class BookService {

  public static void main(String[] args) throws IOException {
    Vertx vertx = Vertx.vertx();
    vertx.deployVerticle(new BookServiceVerticle(), res -> {
      if (res.succeeded()) {
        System.out.println("Deployment id is: " + res.result());
      } else {
        System.out.println("Deployment failed!");
      }
    });
  }

}
