package me.jackz.bungeeweb;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.sun.net.httpserver.*;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.config.ServerInfo;
import net.md_5.bungee.api.connection.ProxiedPlayer;

import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.URI;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;

public class WebServer{

    private static final Charset CHARSET = StandardCharsets.UTF_8;
    // port to listen connection
    private BungeeWeb plugin;
    private static ProxyServer proxy;
    private int PORT = 8080;
    private String HOST = "127.0.0.1";

    WebServer(BungeeWeb plugin,String IP, int PORT) {
            HOST = IP;
            this.PORT = PORT;
            this.plugin = plugin;
            proxy = plugin.getProxy();
    }

    void startServer() throws IOException {
        //todo: put 'HOST'
        HttpServer server = HttpServer.create(new InetSocketAddress(PORT), 0);

        HttpContext context = server.createContext("/");
        context.setHandler(WebServer::handleRequest);
        server.start();
    }
    private static void handleRequest(HttpExchange exchange) throws IOException {
        exchange.getResponseHeaders().set("Content-Type", String.format("application/json; charset=%s",CHARSET));

        JsonObject main_object = new JsonObject();
        JsonArray players = new JsonArray();
        JsonArray servers = new JsonArray();
        for (ProxiedPlayer player : proxy.getPlayers()) {
            JsonObject player_object = new JsonObject();
            player_object.addProperty("uuid",player.getUniqueId().toString());
            player_object.addProperty("username",player.getName());
            player_object.addProperty("server",player.getServer().getInfo().getName());
            players.add(player_object);
        }
        for (ServerInfo server : proxy.getServers().values()) {
            JsonObject server_object = new JsonObject();
            server_object.addProperty("name", server.getName());
            server_object.addProperty("player_count", server.getPlayers().size());
            server_object.addProperty("motd", server.getMotd());
            servers.add(server_object);
        }
        main_object.addProperty("total_players",proxy.getOnlineCount());
        main_object.addProperty("version",proxy.getVersion());
        main_object.add("players",players);
        main_object.add("servers",servers);

        Gson gson = new Gson();
        String json_string = gson.toJson(main_object);

        exchange.sendResponseHeaders(200,json_string.getBytes().length);

        Writer out = new OutputStreamWriter(exchange.getResponseBody(), CHARSET);
        out.write(json_string);
        out.close();
    }
}
