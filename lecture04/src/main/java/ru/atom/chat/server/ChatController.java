package ru.atom.chat.server;

import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;

import java.util.Map;
import java.util.Queue;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.stream.Collectors;

@Controller
@RequestMapping("chat")
public class ChatController {
    private Queue<String> messages = new ConcurrentLinkedQueue<>();
    private Map<String, String> usersOnline = new ConcurrentHashMap<>();
    private Map<String, String> usersBanned = new ConcurrentHashMap<>();

    /**
     * curl -X POST -i localhost:8080/chat/login -d "name=I_AM_STUPID"
     */
    @RequestMapping(
            path = "login",
            method = RequestMethod.POST,
            consumes = MediaType.APPLICATION_FORM_URLENCODED_VALUE)
    @ResponseStatus(HttpStatus.OK)
    public ResponseEntity<String> login(@RequestParam("name") String name) {
        if (usersBanned.containsKey(name)) {
            return ResponseEntity.badRequest().body("User banned");
        }

        if (name.length() < 1) {
            return ResponseEntity.badRequest().body("Too short name, sorry :(");
        }
        if (name.length() > 20) {
            return ResponseEntity.badRequest().body("Too long name, sorry :(");
        }
        if (usersOnline.containsKey(name)) {
            return ResponseEntity.badRequest().body("Already logged in:(");
        }
        usersOnline.put(name, name);
        messages.add("[" + name + "] logged in");
        return ResponseEntity.ok().body(name + " logged in");
    }

    /**
     * curl -i localhost:8080/chat/online
     */
    @RequestMapping(
            path = "online",
            method = RequestMethod.GET,
            produces = MediaType.TEXT_PLAIN_VALUE)
    public ResponseEntity online() {
        String responseBody = String.join("\n", usersOnline.keySet().stream().sorted().collect(Collectors.toList()));
        return ResponseEntity.ok(responseBody);
    }

    /**
     * curl -X POST -i localhost:8080/chat/logout -d "name=I_AM_STUPID"
     */
    @RequestMapping(
            path = "logout",
            method = RequestMethod.POST,
            consumes = MediaType.APPLICATION_FORM_URLENCODED_VALUE)
    @ResponseStatus(HttpStatus.OK)
    public ResponseEntity<String> logout(@RequestParam("name") String name) {
        if (!usersOnline.containsKey(name)) {
            return ResponseEntity.badRequest().body("No such user");
        }
        usersOnline.remove(name);
        messages.add("[" + name + "] logged out");
        return ResponseEntity.ok().body(name + " logged out");
    }

    /**
     * curl -X POST -i localhost:8080/chat/say -d "name=I_AM_STUPID&msg=Hello everyone in this chat"
     */
    @RequestMapping(
            path = "say",
            method = RequestMethod.POST,
            consumes = MediaType.APPLICATION_FORM_URLENCODED_VALUE)
    @ResponseStatus(HttpStatus.OK)
    public ResponseEntity<String> say(@RequestParam("name") String name, @RequestParam("msg") String msg) {
        if (!usersOnline.containsKey(name)) {
            return ResponseEntity.badRequest().body("No such user");
        }
        messages.add("[" + name + "] - " + msg);
        return ResponseEntity.ok().body("[" + name + "] - " + msg);
    }


    /**
     * curl -i localhost:8080/chat/chat
     */
    @RequestMapping(
            path = "chat",
            method = RequestMethod.GET,
            produces = MediaType.TEXT_PLAIN_VALUE)
    public ResponseEntity chat() {
        String responseBody = String.join("\n", messages.stream().sorted().collect(Collectors.toList()));
        return ResponseEntity.ok(responseBody);
    }

    /**
     * curl -i localhost:8080/chat/clear
     */
    @RequestMapping(
            path = "clear",
            method = RequestMethod.GET,
            produces = MediaType.TEXT_PLAIN_VALUE)
    public ResponseEntity clear() {
        messages.clear();
        return ResponseEntity.ok().body("Chat cleared");
    }

    /**
     * curl -X POST -i localhost:8080/chat/ban -d "name=I_AM_STUPID"
     */
    @RequestMapping(
            path = "ban",
            method = RequestMethod.POST,
            consumes = MediaType.APPLICATION_FORM_URLENCODED_VALUE)
    @ResponseStatus(HttpStatus.OK)
    public ResponseEntity<String> ban(@RequestParam("name") String name) {
        if (!usersOnline.containsKey(name)) {
            return ResponseEntity.badRequest().body("No such user");
        }

        if (usersBanned.containsKey(name)) {
            return ResponseEntity.badRequest().body("User already banned");
        }

        usersOnline.remove(name);
        usersBanned.put(name, name);
        messages.add("[" + name + "] banned");
        return ResponseEntity.ok().body(name + " banned");
    }

    /**
     * curl -i localhost:8080/chat/unbanAll
     */
    @RequestMapping(
            path = "unbanAll",
            method = RequestMethod.GET,
            produces = MediaType.TEXT_PLAIN_VALUE)
    public ResponseEntity unbanAll() {
        usersBanned.clear();
        return ResponseEntity.ok().body("All users were forgiven");
    }
}
