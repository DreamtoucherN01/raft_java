package com.blake.common;


import org.json.JSONObject;

public class MessageFormat {

    private String messageType;

    // k:v
    private KVTuple messageInfo;

    private int term;

    private int vote;

    public MessageFormat(){}

    public MessageFormat(String messageType, KVTuple messageInfo,
                         int term, int vote) {
        this.messageType = messageType;
        this.messageInfo = messageInfo;
        this.term = term;
        this.vote = vote;
    }

    public String toMessage(int term, int vote, MessageType messageType) {

        JSONObject object = new JSONObject();
        object.put(messageTypeKey, messageType.info);
        object.put(messageInfoKey, messageInfo.formatKvTuple());
        object.put(termKey, term);
        object.put(voteKey, vote);
        return object.toString();
    }

    public String toMessage() {

        JSONObject object = new JSONObject();
        object.put(messageTypeKey, messageType);
        object.put(messageInfoKey, messageInfo.formatKvTuple());
        object.put(termKey, term);
        object.put(voteKey, vote);
        return object.toString();
    }

    public static MessageFormat fromString(String message) {

        MessageFormat messageFormat = new MessageFormat();
        JSONObject object = new JSONObject(message);
        KVTuple kvTuple = new KVTuple(object.optString(messageInfoKey));
        messageFormat.setMessageType(object.optString(messageTypeKey));
        messageFormat.setMessageInfo(kvTuple);
        messageFormat.setTerm(object.optInt(termKey));
        messageFormat.setVote(object.optInt(voteKey));
        return messageFormat;
    }

    public String getMessageType() {
        return messageType;
    }

    public void setMessageType(String messageType) {
        this.messageType = messageType;
    }

    public KVTuple getMessageInfo() {
        return messageInfo;
    }

    public void setMessageInfo(KVTuple messageInfo) {
        this.messageInfo = messageInfo;
    }

    public int getTerm() {
        return term;
    }

    public void setTerm(int term) {
        this.term = term;
    }

    public int getVote() {
        return vote;
    }

    public void setVote(int vote) {
        this.vote = vote;
    }

    @Override
    public String toString() {
        return toMessage();
    }

    private static String messageTypeKey = "message_type";
    private static String messageInfoKey = "message_info";
    private static String termKey = "term";
    private static String voteKey = "vote";


}
