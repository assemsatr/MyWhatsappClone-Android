package com.assemalturifi.whatsappfirebase;



//step22,all this class, the next step in mainChatActivity
class InstantMessage {

    private String message;
    private String author;

    InstantMessage(String message, String author) {
        this.message = message;
        this.author = author;
    }

    public InstantMessage() {
        //this is a requirement  by Firebase

    }

    String getMessage() {
        return message;
    }

    String getAuthor() {
        return author;
    }
}
