package io.vertx.example;

import io.vertx.core.DeploymentOptions;
import io.vertx.core.Vertx;
import io.vertx.core.VertxOptions;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.unit.Async;
import io.vertx.ext.unit.TestContext;
import io.vertx.ext.unit.junit.VertxUnitRunner;
import io.vertx.ext.web.client.WebClient;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.io.IOException;
import java.net.ServerSocket;

@RunWith(VertxUnitRunner.class)
public class BookServiceVerticleTest {

  private Vertx vertx;
  private int port;
  private WebClient client;

  @Before
  public void setUp(TestContext context) throws IOException {
    VertxOptions vertxOptions = new VertxOptions();
    // increase check interval for blocked threads for tests
    vertxOptions.setBlockedThreadCheckInterval(60 * 1000);
    vertx = Vertx.vertx(vertxOptions);

    // try to get a random port
    ServerSocket socket = new ServerSocket(0);
    port = socket.getLocalPort();
    socket.close();

    DeploymentOptions options = new DeploymentOptions().setConfig(
      new JsonObject().put(BookServiceVerticle.CONF_HTTP_PORT, port));

    vertx.deployVerticle(new BookServiceVerticle(), options, context.asyncAssertSuccess());
    client = WebClient.create(vertx);
  }

  @After
  public void tearDown(TestContext context) {
    vertx.close(context.asyncAssertSuccess());
  }

  @Test
  public void testGetHello(TestContext context) {
    Async async = context.async();
    client.get(port, "localhost", "/")
      .send(ar -> {
        if (ar.succeeded()) {
          context.assertEquals(200, ar.result().statusCode());
          context.assertEquals("Hello world, I'm running...", ar.result().bodyAsString());
          async.complete();
        } else {
          context.fail(ar.cause());
        }
      });
  }

  @Test
  public void testGetBooks(TestContext context) {
    Async async = context.async();
    client.get(port, "localhost", "/books")
      .send(ar -> {
        if (ar.succeeded()) {
          context.assertEquals(200, ar.result().statusCode());
          // test initial data
          context.assertEquals(40, ar.result().bodyAsJsonArray().size());
          async.complete();
        } else {
          context.fail(ar.cause());
        }
      });
  }

  @Test
  public void testCreateBook(TestContext context) {
    Async async = context.async();
    JsonObject book = new JsonObject()
      .put("isbn", "1234567890")
      .put("title", "Test Book")
      .put("author", "Test Author");

    client.post(port, "localhost", "/books")
      .sendJsonObject(book, ar -> {
        if (ar.succeeded()) {
          context.assertEquals(200, ar.result().statusCode());
          context.assertEquals(book, ar.result().bodyAsJsonObject());
          async.complete();
        } else {
          context.fail(ar.cause());
        }
      });
  }

  @Test
  public void testGetBookByIsbn(TestContext context) {
    Async async = context.async();
    JsonObject book = new JsonObject()
      .put("isbn", "1234567890")
      .put("title", "Test Book")
      .put("author", "Test Author");

    client.post(port, "localhost", "/books")
      .sendJsonObject(book, ar -> {
        if (ar.succeeded()) {
          client.get(port, "localhost", "/books/isbn/1234567890")
            .send(ar2 -> {
              if (ar2.succeeded()) {
                context.assertEquals(200, ar2.result().statusCode());
                context.assertEquals(book, ar2.result().bodyAsJsonObject());
                async.complete();
              } else {
                context.fail(ar2.cause());
              }
            });
        } else {
          context.fail(ar.cause());
        }
      });
  }

  @Test
  public void testGetFeaturedBook(TestContext context) {
    Async async = context.async();
    client.get(port, "localhost", "/books/featured")
      .send(ar -> {
        if (ar.succeeded()) {
          // not yet implemented
          context.assertEquals(501, ar.result().statusCode());
          context.assertEquals("Not implemented", ar.result().bodyAsJsonObject().getString("message"));
          async.complete();
        } else {
          context.fail(ar.cause());
        }
      });
  }
}
