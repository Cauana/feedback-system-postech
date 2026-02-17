package com.feedback.functions;


import com.azure.storage.queue.QueueClient;
import com.azure.storage.queue.QueueClientBuilder;
import com.azure.storage.queue.QueueMessageEncoding;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.enterprise.inject.Produces;
import org.eclipse.microprofile.config.inject.ConfigProperty;

@ApplicationScoped
public class AzureQueueProducer {

    @ConfigProperty(name = "azure.storage.queue.connection-string")
    String connectionString;

    @ConfigProperty(name = "azure.storage.queue.name")
    String queueName;

    @Produces
    @ApplicationScoped
    public QueueClient queueClient() {
        return new QueueClientBuilder()
                .connectionString(connectionString)
                .queueName(queueName)
                .messageEncoding(QueueMessageEncoding.BASE64)
                .buildClient();
    }
}
