package com.parcial_unidad1.ordenes_service.controller;

import java.util.Collections;
import java.util.UUID;

import org.springframework.stereotype.Service;

import com.amazonaws.auth.AWSStaticCredentialsProvider;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.client.builder.AwsClientBuilder;
import com.amazonaws.services.logs.AWSLogs;
import com.amazonaws.services.logs.AWSLogsClientBuilder;
import com.amazonaws.services.logs.model.CreateLogStreamRequest;
import com.amazonaws.services.logs.model.InputLogEvent;
import com.amazonaws.services.logs.model.PutLogEventsRequest;

@Service
public class CloudWatchService {

    public void enviarLog(String mensaje) {
        String endpoint = System.getenv("AWS_ENDPOINT") != null ? System.getenv("AWS_ENDPOINT") : "http://localhost:4566";
        // 1. Conectarnos a tu LocalStack directamente
        AWSLogs awsLogs = AWSLogsClientBuilder.standard()
                .withEndpointConfiguration(new AwsClientBuilder.EndpointConfiguration(endpoint, "us-east-1"))
                .withCredentials(new AWSStaticCredentialsProvider(new BasicAWSCredentials("test", "test")))
                .build();

        // 2. Crear un archivo único para este evento
        String streamName = "orden-" + UUID.randomUUID().toString().substring(0, 8);
        awsLogs.createLogStream(new CreateLogStreamRequest("ordenes-log-group", streamName));

        // 3. Empaquetar y enviar el mensaje
        InputLogEvent evento = new InputLogEvent()
                .withMessage(mensaje)
                .withTimestamp(System.currentTimeMillis());

        PutLogEventsRequest request = new PutLogEventsRequest()
                .withLogGroupName("ordenes-log-group")
                .withLogStreamName(streamName)
                .withLogEvents(Collections.singletonList(evento));

        awsLogs.putLogEvents(request);
    }
}