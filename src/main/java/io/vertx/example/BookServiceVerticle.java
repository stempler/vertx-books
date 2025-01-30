package io.vertx.example;

import java.io.IOException;

import io.vertx.core.AbstractVerticle;
import io.vertx.core.json.Json;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.web.Router;
import io.vertx.ext.web.RoutingContext;
import io.vertx.ext.web.handler.BodyHandler;

public class BookServiceVerticle extends AbstractVerticle {

  public static final String CONF_HTTP_PORT = "port";
  private BooksDB books;

  @Override
  public void start() throws IOException {
    books = new BooksDB(vertx);

    Router router = Router.router(vertx);

    router.route().handler(BodyHandler.create());
    router.get("/").handler(this::getHello);
    router.get("/books").handler(this::getBooks);
    router.post("/books").handler(this::createBook);
    router.get("/books/isbn/:isbn").handler(this::getBookByIsbn);
    router.get("/books/featured").handler(this::getFeaturedBook);

    int port = config().getInteger(CONF_HTTP_PORT, 8080);

    vertx.createHttpServer()
      .requestHandler(router)
      .listen(port, handler -> {
        if (handler.succeeded()) {
          System.out.println("http://localhost:" + port + "/");
        } else {
          System.err.println("Failed to listen on port " + port);
        }
      });
  }

  private void getHello(RoutingContext rc) {
    rc.response()
      .putHeader("content-type", "text/plain; charset=utf-8")
      .setStatusCode(200)
      .end("Hello world, I'm running...");
  }

  private void getBooks(RoutingContext rc) {
    books.getBooks(res -> {
      sendJsonResponse(rc, 200, res.result());
    });
  }

  private void createBook(RoutingContext rc) {
    JsonObject book = rc.body().asJsonObject();
    validateBook(book);
    books.addBook(book, res -> {
      sendJsonResponse(rc, 200, book);
    });
  }

  private void getBookByIsbn(RoutingContext rc) {
    String isbn = rc.request().getParam("isbn");
    books.getBook(isbn, res -> {
      sendJsonResponse(rc, 200, res.result());
    });
  }

  private void getFeaturedBook(RoutingContext rc) {
    sendErrorResponse(rc, 501, "Not implemented");
  }

  private void validateBook(JsonObject book) {
    if (!book.containsKey("isbn")) {
      throw new IllegalStateException("Book must have an ISBN");
    }
    if (!book.containsKey("title")) {
      throw new IllegalStateException("Book must have a title");
    }
    if (!book.containsKey("author")) {
      throw new IllegalStateException("Book must have an author");
    }
  }

  protected void sendJsonResponse(RoutingContext rc, int code, Object json) {
    rc.response()
      .putHeader("content-type", "application/json; charset=utf-8")
      .setStatusCode(code)
      .end(Json.encodePrettily(json));
  }

  protected void sendErrorResponse(RoutingContext rc, Throwable e) {
    sendErrorResponse(rc, 500, e.getMessage());
  }

  protected void sendErrorResponse(RoutingContext rc, int code, String message) {
    rc.response()
      .putHeader("content-type", "application/json; charset=utf-8")
      .setStatusCode(code)
      .end(new JsonObject().put("message", message).encodePrettily());
  }
}
