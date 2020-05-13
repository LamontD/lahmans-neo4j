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

@SpringBootApplication(scanBasePackages={
    LahmansCore.Consts.MODEL_BASE_PACKAGE, 
    "com.lamontd.lahmans.neo4j.writer.springboot"
})
public class LahmansNeo4jSpringBootWriterApplication {
        
    public static void main(String[] args) {
        SpringApplication.run(LahmansNeo4jSpringBootWriterApplication.class, args);
    }
    

}