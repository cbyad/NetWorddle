

package com.cb.client.graphic;

public interface SocketListener {
     void onMessage(String line);
     void onClosedStatus(boolean isClosed);
}
