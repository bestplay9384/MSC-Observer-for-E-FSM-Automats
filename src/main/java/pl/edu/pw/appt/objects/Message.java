package pl.edu.pw.appt.objects;

import java.util.Objects;

/**
 * @author bestp
 */
public class Message implements Comparable<Message> {

    public enum Type {
        Receive(1), Send(2);

        private final int value;

        private Type(int value) {
            this.value = value;
        }

        public int getValue() {
            return value;
        }
    }

    public final String from;
    public final String currentState;
    public final String nextState;
    public final Type messageType;
    public final String signalName;
    public final String to;
    public final int id;
    public final int globalId;

    // from-to-type-signal-i
    public Message(String FSMname_, String currentState_, String nextState_, String messageType_, String signalName_, String otherFSM_, String id_, String globalId_) {
        this.from = FSMname_;
        this.currentState = currentState_;
        this.nextState = nextState_;
        
        if (Integer.valueOf(messageType_) == 1) {
            this.messageType = Type.Receive;
        } else {
            this.messageType = Type.Send;
        }

        this.signalName = signalName_;
        this.to = otherFSM_;
        this.id = Integer.valueOf(id_);
        this.globalId = Integer.valueOf(globalId_);
    }

    @Override
    public String toString() {
        return from + "-" + currentState + "-" + nextState + "-" + messageType.getValue() + "-" + to + "-" + signalName + "-" + id + "-" + globalId;
    }

    @Override
    public int compareTo(Message m) {
        return ((Integer) this.globalId).compareTo(m.globalId);
    }
    
    @Override
    public final boolean equals(Object o) {
        Message m = (Message)o;
        return this.from.equals(m.from) &&
                this.to.equals(m.to) &&
                this.signalName.equals(m.signalName) &&
                this.id == m.id;
    }

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 67 * hash + Objects.hashCode(this.from);
        hash = 67 * hash + Objects.hashCode(this.currentState);
        hash = 67 * hash + Objects.hashCode(this.nextState);
        hash = 67 * hash + Objects.hashCode(this.messageType);
        hash = 67 * hash + Objects.hashCode(this.signalName);
        hash = 67 * hash + Objects.hashCode(this.to);
        hash = 67 * hash + this.id;
        hash = 67 * hash + this.globalId;
        return hash;
    }
}
