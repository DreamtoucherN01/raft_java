package com.blake.common;

import org.json.JSONObject;

public class VoteInfo {

    int term;

    int vote;

    JSONObject msg;

    public VoteInfo(int term, int vote, JSONObject msg) {
        this.term = term;
        this.vote = vote;
        this.msg = msg;
    }

    public VoteInfo fromString(String obj) {

        JSONObject object = new JSONObject(obj);
        int term = object.optInt(termName);
        int vote = object.optInt(voteName);
        JSONObject msg = object.optJSONObject(msgName);
        return new VoteInfo(term, vote, msg);
    }

    @Override
    public String toString() {
        JSONObject object = new JSONObject();
        object.put(termName, term);
        object.put(voteName, vote);
        object.put(msgName, msg);
        return object.toString();
    }

    private static final String termName = "term";
    private static final String voteName = "vote";
    private static final String msgName = "msg";
}
