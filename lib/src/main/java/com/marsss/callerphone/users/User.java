package com.marsss.callerphone.users;

import com.marsss.callerphone.Response;

public class User {

/*
"id": "1234567890",
"status": "user/moderator/blacklist",
"reason": "",
"prefix": "",
"credits": 0,
"executed": 0,
"transmitted": 0
*/

    private String id;
    private UserStatus status;
    private String reason;
    private String prefix;
    private long credits;
    private long executed;
    private long transmitted;

    public User() {
    }

    public User(String id, UserStatus status, String reason, String prefix, long credits, long executed, long transmitted) {
        this.id = id;
        this.status = status;
        this.reason = reason;
        this.prefix = prefix;
        this.credits = credits;
        this.executed = executed;
        this.transmitted = transmitted;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public UserStatus getStatus() {
        return status;
    }

    public void setStatus(UserStatus status) {
        this.status = status;
    }

    public String getReason() {
        return reason;
    }

    public void setReason(String reason) {
        this.reason = reason;
    }

    public String getPrefix() {
        return prefix;
    }

    public void setPrefix(String prefix) {
        this.prefix = prefix;
    }

    public long getCredits() {
        return credits;
    }

    public void setCredits(long credits) {
        this.credits = credits;
    }

    public long getExecuted() {
        return executed;
    }

    public void setExecuted(long executed) {
        this.executed = executed;
    }

    public long getTransmitted() {
        return transmitted;
    }

    public void setTransmitted(long transmitted) {
        this.transmitted = transmitted;
    }

    public String toJSON() {
        return String.format(Response.USER_TEMPLATE.toString(), id, status, reason, prefix, credits, executed, transmitted);
    }
}
