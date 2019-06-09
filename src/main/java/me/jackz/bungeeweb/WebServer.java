package me.jackz.bungeeweb;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.sun.net.httpserver.HttpContext;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpServer;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.config.ServerInfo;
import net.md_5.bungee.api.connection.ProxiedPlayer;

import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

class WebServer{

    private static final Charset CHARSET = StandardCharsets.UTF_8;
    // port to listen connection
    private static ProxyServer proxy;
    private HttpServer server;

    private List<HttpContext> contexts = new ArrayList<>();


    /** Get the HttpServer for whatever purpose
     * @return Returns a HttpServer object
     */
    public HttpServer getServer() {
        return this.server;
    }

    /** Get a HttpContext to add a custom route
     * @param path The path on the server you want
     * @return Returns a HttpContext object
     */
    public HttpContext getContext(String path) {
        return server.createContext(path);
    }

    /** Removes a HttpContext by its path
     * @param path The path of the context
     */
    public void removeContext(String path) {
        this.contexts.remove(path);
    }

    void startServer(BungeeWeb plugin, String IP, int PORT) throws IOException {
        proxy = plugin.getProxy();
        String HOST = (IP.equals("")) ? null : IP;
        //todo: put 'HOST'
        InetAddress hostname = InetAddress.getByName(HOST);
        server = HttpServer.create(new InetSocketAddress(hostname,PORT), 0);
        setupInitialContexts();
        server.start();
    }
    private void setupInitialContexts() {
        // Json Parser
        HttpContext context = server.createContext("/json");
        context.setHandler(WebServer::JsonHandler);
    }
    private static void JsonHandler(HttpExchange exchange) throws IOException {
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
