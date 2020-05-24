/*
 * Copyright 2020 lamontdozierjr.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.lamontd.lahmans.neo4j.writer.springboot.services;

import com.lamontd.lahmans.neo4j.core.handlers.TransportObjectHandler;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.lamontd.transactionmanager.service.ComponentTransactionKafkaSender;
import com.lamontd.utils.jackson.JacksonMapper;
import com.lamontd.utils.transport.MappedTransportObject;
import com.lamontd.utils.transport.StorageException;
import com.lamontd.utils.transport.TransportConversionException;
import java.time.Duration;
import java.time.Instant;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.kafka.streams.kstream.KStream;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Service;

/**
 *
 * @author lamontdozierjr
 */
@Service
public class KafkaMessageReader {

    private static final Log logger = LogFactory.getLog(KafkaMessageReader.class);

    @Autowired
    private TransportObjectHandler transportObjectHandler;
    @Autowired
    private ComponentTransactionKafkaSender transactionSender;

    public KafkaMessageReader() {
        logger.info("I have the power of Greyskull!");
    }

    private void handleMessage(String key, String message) {
        try {
            Instant start = Instant.now();
            final ObjectMapper jacksonMapper = JacksonMapper.getStandardMapper();
            MappedTransportObject incomingObject = jacksonMapper.readValue(message, MappedTransportObject.class);

            if (StringUtils.isEmpty(incomingObject.getObjectType())) {
                logger.warn("Received unexpected message that is not a TransportObject: " + message);
            } else if (incomingObject.getAttributes() == null || incomingObject.getAttributes().isEmpty()) {
                logger.warn("Found message of type " + incomingObject.getObjectType() + " but no attributes");
            } else {
                try {
                    boolean handledSuccessfully = transportObjectHandler.process(incomingObject);
                    if (StringUtils.isNotEmpty(incomingObject.getTransactionId())) {
                        if (handledSuccessfully) {
                            transactionSender.publishAck(incomingObject.getTransactionId(), Duration.between(start, Instant.now()));
                        } else {
                            transactionSender.publishNak(incomingObject.getTransactionId(), Duration.between(start, Instant.now()));
                        }
                    }
                } catch (TransportConversionException | StorageException ex) {
                    logger.error("Failed to convert and storage incpoming transport object", ex);
                    if (StringUtils.isNotEmpty(incomingObject.getTransactionId())) {
                        transactionSender.publishNak(incomingObject.getTransactionId(), Duration.between(start, Instant.now()));
                    }
                }
            }
        } catch (JsonProcessingException ex) {
            logger.warn("Error processing incoming message", ex);
        } catch (UnsupportedOperationException unsup) {
            logger.error("Could not process incoming message -> " + message, unsup);
        }
    }

    @Bean
    public java.util.function.Consumer<KStream<String, String>> process() {
        return input
                -> input.foreach((key, value) -> {
                    handleMessage(key, value);
                });
    }

}
