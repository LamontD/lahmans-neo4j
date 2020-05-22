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
package com.lamontd.lahmans.neo4j.writer.springboot;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import com.lamontd.lahmans.neo4j.core.LahmansCore;
import com.lamontd.transactionmanager.model.TransactionManagerConsts;
import java.util.HashMap;
import java.util.Map;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.common.serialization.StringSerializer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.kafka.KafkaProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.kafka.core.DefaultKafkaProducerFactory;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.core.ProducerFactory;

@SpringBootApplication(scanBasePackages = {
    LahmansCore.Consts.MODEL_BASE_PACKAGE,
    TransactionManagerConsts.SERVICES_BASE_PACKAGE,
    "com.lamontd.lahmans.neo4j.writer.springboot"
})
public class LahmansNeo4jSpringBootWriterApplication {

    @Autowired
    private KafkaProperties kafkaProperties;

    public static void main(String[] args) {
        SpringApplication.run(LahmansNeo4jSpringBootWriterApplication.class, args);
    }
    
    // TODO: It's really fucking weird that this application needs it...but the MariaDB writer doesn't.

    @Bean
    public Map<String, Object> producerConfigs() {
        Map<String, Object> props
                = new HashMap<>(kafkaProperties.buildProducerProperties());
        props.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG,
                StringSerializer.class);
        props.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG,
                StringSerializer.class);
        props.put(ProducerConfig.CLIENT_ID_CONFIG, "lahmans-neo4j-writer");
        return props;
    }

    @Bean
    public ProducerFactory<String, String> producerFactory() {
        return new DefaultKafkaProducerFactory<>(producerConfigs());
    }

    @Bean
    public KafkaTemplate<String, String> kafkaTemplate() {
        return new KafkaTemplate<>(producerFactory());
    }
}
