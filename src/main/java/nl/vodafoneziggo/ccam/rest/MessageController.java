package nl.vodafoneziggo.ccam.rest;

import nl.vodafoneziggo.ccam.event.MessageTypeDto;
import nl.vodafoneziggo.ccam.event.RequestResponseEventKeyDto;
import nl.vodafoneziggo.ccam.event.RequestResponseEventValueDto;
import org.springframework.http.ResponseEntity;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * Controller to manually send a message. This simulates a message that would be automatically sent by a system in reality.
 */
@RestController
public class MessageController {
    /**
     * The Kafka template so send messages with.
     */
    private final KafkaTemplate<RequestResponseEventKeyDto, RequestResponseEventValueDto> messageTemplate;

    /**
     * Constructor.
     *
     * @param messageTemplate The Kafka template so send messages with.
     */
    public MessageController(final KafkaTemplate<RequestResponseEventKeyDto, RequestResponseEventValueDto> messageTemplate) {
        this.messageTemplate = messageTemplate;
    }

    /**
     * Trigger publication of a message.
     *
     * @param type   the message type
     * @param client the client
     * @param api    the api being called
     * @param size   the message size
     * @return no content
     */
    @PostMapping(path = "/messages")
    public ResponseEntity<Void> sendMessage(
            @RequestParam(name = "type") final String type,
            @RequestParam(name = "client") final String client,
            @RequestParam(name = "api") final String api,
            @RequestParam(name = "size") final int size) {
        final RequestResponseEventKeyDto key = new RequestResponseEventKeyDto(client, api);
        final RequestResponseEventValueDto value = new RequestResponseEventValueDto(client, api, size, MessageTypeDto.valueOf(type));

        messageTemplate.send("queuing.bandwith", key, value);
        return ResponseEntity.noContent().build();
    }

}
