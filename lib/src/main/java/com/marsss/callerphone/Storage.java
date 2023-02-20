package com.marsss.callerphone;

import com.marsss.callerphone.channelpool.ChannelPool;
import com.marsss.callerphone.channelpool.PoolConfig;
import com.marsss.callerphone.channelpool.PoolStatus;
import com.marsss.callerphone.users.BotUser;
import com.marsss.callerphone.users.UserStatus;
import net.dv8tion.jda.api.entities.User;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedList;

public class Storage {
    public static final Logger logger = LoggerFactory.getLogger(Storage.class);

    public static final LinkedList<String> filter = new LinkedList<>();
    public static final HashMap<String, BotUser> users = new HashMap<>();
    public static final HashMap<String, Long> poolChatCoolDown = new HashMap<>();

    static void readData() {
        try {
            getFilter(new File(Callerphone.parent + "/filter.txt"));
        } catch (Exception e) {
            e.printStackTrace();
            System.out.println("------------------------------");
            logger.error("Error with filter.txt");
        }

        System.out.println("------------------------------");

        try {
            importUsers(new File(Callerphone.parent + "/users.json"));
        } catch (Exception e) {
            e.printStackTrace();
            System.out.println("------------------------------");
            logger.error("Error with pools.txt");
        }

        try {
            importPools(new File(Callerphone.parent + "/pools.json"));
        } catch (Exception e) {
            e.printStackTrace();
            System.out.println("------------------------------");
            logger.error("Error with pools.txt");
        }

        System.out.println("------------------------------");
    }

    static void writeData() {
        try {
            exportUsers(new File(Callerphone.parent + "/users.json"));
        } catch (Exception e) {
            e.printStackTrace();
        }
        try {
            exportPools(new File(Callerphone.parent + "/pools.json"));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static void getFilter(File file) throws IOException {
        try (BufferedReader br = new BufferedReader(new FileReader(file))) {
            String line = br.readLine();

            while (line != null) {
                filter.add(line);
                line = br.readLine();
            }
        }
    }

    private static boolean exportUsers(File file) {
        StringBuilder sb = new StringBuilder();
        sb.append("{\n\t\"users\": [\n");
        try (PrintWriter myWriter = new PrintWriter(file)) {
            LinkedList<BotUser> valUsers = new LinkedList<>(Storage.users.values());
            Collections.sort(valUsers);
            for (int i = 0; i < valUsers.size(); i++) {
                sb.append(valUsers.get(i).toJSON());
                if (i == valUsers.size() - 1) {
                    sb.append("\n");
                } else {
                    sb.append(",\n");
                }
            }

            sb.append("\t]\n}");

            myWriter.print(sb);
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
        return true;
    }

    private static boolean importUsers(File file) {
        JSONParser parser = new JSONParser();

        try (Reader reader = new FileReader(file)) {
            JSONObject object = (JSONObject) parser.parse(reader);

            JSONArray usersArr = (JSONArray) object.get("users");

            for (Object user : usersArr) {
                try {
                    JSONObject userObj = (JSONObject) user;
                    String id = (String) userObj.get("id");
                    String statusStr = (String) userObj.get("status");
                    String reason = (String) userObj.get("reason");
                    String prefix = (String) userObj.get("prefix");
                    long credits = (long) userObj.get("credits");
                    long executed = (long) userObj.get("executed");
                    long transmitted = (long) userObj.get("transmitted");

                    UserStatus status = UserStatus.USER;

                    switch (statusStr) {
                        case "user":
                            status = UserStatus.USER;
                            break;
                        case "moderator":
                            status = UserStatus.MODERATOR;
                            break;
                        case "warned":
                            status = UserStatus.WARNED;
                            break;
                        case "blacklisted":
                            status = UserStatus.BLACKLISTED;
                            break;
                    }

                    BotUser botUser = new BotUser(id, status, reason, prefix, credits, executed, transmitted);

                    users.put(id, botUser);
                } catch (Exception e) {
                    e.printStackTrace();
                    return false;
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
        return true;
    }

    private static boolean exportPools(File file) {
        StringBuilder sb1 = new StringBuilder();
        sb1.append("{\n\t\"pools\": [\n");
        try (PrintWriter myWriter = new PrintWriter(file)) {
            LinkedList<PoolConfig> valPools = new LinkedList<>(ChannelPool.config.values());
            for (int i = 0; i < valPools.size(); i++) {
                sb1.append(valPools.get(i).toJSON());
                if (i == valPools.size() - 1) {
                    sb1.append("\n");
                } else {
                    sb1.append(",\n");
                }
            }

            sb1.append("\t]\n}");
            myWriter.print(sb1);
        } catch (Exception e) {
            return false;
        }
        return true;
    }

    private static boolean importPools(File file) {
        JSONParser parser = new JSONParser();

        try (Reader reader = new FileReader(file)) {
            JSONObject object = (JSONObject) parser.parse(reader);

            JSONArray poolsArr = (JSONArray) object.get("pools");

            for (Object pool : poolsArr) {
                try {
                    JSONObject poolObj = (JSONObject) pool;
                    String hostID = (String) poolObj.get("hostID");
                    String pwd = (String) poolObj.get("pwd");
                    long cap = (long) poolObj.get("cap");
                    boolean pub = (boolean) poolObj.get("pub");

                    PoolConfig config = new PoolConfig(hostID, pwd, (int) cap, pub);

                    JSONArray childrenArr = (JSONArray) poolObj.get("children");
                    for (Object child : childrenArr) {
                        String childStr = (String) child;
                        config.children.add(childStr);
                        if (!hostID.equals(childStr)) {
                            ChannelPool.parent.put(childStr, hostID);
                        }
                    }

                    ChannelPool.config.put(hostID, config);

                } catch (Exception e) {
                    e.printStackTrace();
                    return false;
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
        return true;
    }

    public static void createUser(String id) {
        users.put(id, new BotUser(id));
    }

    public static void reward(User user, long amount) {
        if (!users.containsKey(user.getId())) {
            createUser(user.getId());
        }
        users.get(user.getId()).addCredits(amount);
        logger.info("User: " + user.getId() + " earned: " + amount + " credits.");
    }

    public static long getCredits(User user) {
        if (!users.containsKey(user.getId())) {
            createUser(user.getId());
        }
        return users.get(user.getId()).getCredits();
    }

    public static void addExecute(User user, int amount) {
        if (!users.containsKey(user.getId())) {
            createUser(user.getId());
        }
        users.get(user.getId()).addExecuted(amount);
    }

    public static long getExecuted(User user) {
        if (!users.containsKey(user.getId())) {
            createUser(user.getId());
        }
        return users.get(user.getId()).getExecuted();
    }

    public static void addTransmit(User user, int amount) {
        if (!users.containsKey(user.getId())) {
            createUser(user.getId());
        }
        users.get(user.getId()).addTransmitted(amount);
    }

    public static long getTransmitted(User user) {
        if (!users.containsKey(user.getId())) {
            createUser(user.getId());
        }
        return users.get(user.getId()).getTransmitted();
    }

    public static long getUserCooldown(User user) {
        poolChatCoolDown.put(user.getId(), poolChatCoolDown.getOrDefault(user.getId(), 0L));

        return poolChatCoolDown.get(user.getId());
    }

    public static void updateUserCooldown(User user) {
        poolChatCoolDown.put(user.getId(), System.currentTimeMillis());
    }

    public String getPrefix(User user) {
        if (!users.containsKey(user.getId())) {
            createUser(user.getId());
        }
        return users.get(user.getId()).getPrefix();
    }

    public boolean isBlacklisted(String id) {
        if (!users.containsKey(id)) {
            createUser(id);
        }
        return users.get(id).getStatus() == UserStatus.BLACKLISTED;
    }

    public boolean isAdmin(String id) {
        if (!users.containsKey(id)) {
            createUser(id);
        }
        return users.get(id).getStatus() == UserStatus.MODERATOR;
    }

    public void addBlacklist(String id) {
        if (!users.containsKey(id)) {
            createUser(id);
        }
        users.get(id).setStatus(UserStatus.BLACKLISTED);
    }

    public void addAdmin(String id) {
        if (!users.containsKey(id)) {
            createUser(id);
        }
        users.get(id).setStatus(UserStatus.MODERATOR);
    }

    public void setPrefix(String id, String prefix) {
        if (!users.containsKey(id)) {
            createUser(id);
        }
        users.get(id).setPrefix(prefix);
    }

    public boolean hasPrefix(String id) {
        if (!users.containsKey(id)) {
            createUser(id);
        }
        return !users.get(id).getPrefix().equals("");
    }
}