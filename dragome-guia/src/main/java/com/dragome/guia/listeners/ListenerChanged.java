package com.dragome.guia.listeners;

import java.util.EventListener;

public interface ListenerChanged extends EventListener
{
    public <T extends EventListener> void listenerAdded(Class<? extends T> type, T listener);
    public <T extends EventListener> void listenerRemoved(Class<? extends T> type, T listener);
}
