package org.node.service;

import org.telegram.telegrambots.meta.api.objects.Update;
//to read messages from the broker
public interface ConsumerService {
    void consumeTextMessageUpdates(Update update);
    void consumeDocMessageUpdates(Update update);
    void consumePhotoMessageUpdates(Update update);
}
