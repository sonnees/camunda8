package sonnees.jobworker.config;

import io.camunda.zeebe.client.ZeebeClient;
import io.camunda.zeebe.client.api.worker.JobHandler;
import io.camunda.zeebe.client.api.worker.JobWorker;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.net.URI;
import java.time.Duration;

@Configuration
@EnableConfigurationProperties
public class JobWorkerConfig {

    @Bean
    ZeebeClient zeebeClient(){
        return ZeebeClient.newClientBuilder()
                .restAddress(URI.create("http://localhost:26500"))
                .usePlaintext()
                .build();
    }

    @Bean(destroyMethod = "close")
    JobWorker jobWorkeṛ(ZeebeClient zeebeClient, JobHandler jobHandler){
        return zeebeClient.newWorker()
                .jobType("jobWorker")
                .handler(jobHandler)
                .timeout(Duration.ofSeconds(10).toMillis())
                .open();
    }
}
