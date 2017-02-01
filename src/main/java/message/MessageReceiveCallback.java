package message;

/**
 * An callback interface for message receiving
 */
interface MessageReceiveCallback {
    void handleMessage(Message message);
}
