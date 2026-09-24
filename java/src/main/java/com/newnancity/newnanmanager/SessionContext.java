package com.newnancity.newnanmanager;

/** Server-instance session tuple used for request fencing. */
public final class SessionContext {
    private final String id;
    private final long epoch;

    public SessionContext(String id, long epoch) {
        if (id == null || id.trim().isEmpty()) {
            throw new IllegalArgumentException("session id must not be blank");
        }
        if (id.length() < 32 || id.length() > 64) {
            throw new IllegalArgumentException("session id length must be between 32 and 64 characters");
        }
        if (epoch <= 0L) {
            throw new IllegalArgumentException("session epoch must be greater than zero");
        }
        this.id = id;
        this.epoch = epoch;
    }

    public String getId() {
        return id;
    }

    public long getEpoch() {
        return epoch;
    }
}
