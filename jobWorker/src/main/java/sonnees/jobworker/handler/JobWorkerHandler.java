package sonnees.jobworker.handler;

import io.camunda.zeebe.client.api.response.ActivatedJob;
import io.camunda.zeebe.client.api.worker.JobClient;
import io.camunda.zeebe.client.api.worker.JobHandler;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import sonnees.jobworker.service.JobWorkerService;

import java.time.Duration;
import java.util.Map;

@Service
public class JobWorkerHandler implements JobHandler {

    @Autowired
    private JobWorkerService jobWorkerService;

    @Override
    public void handle(JobClient client, ActivatedJob job) throws Exception {
        try {
            Map<String, Object> variablesAsMap = job.getVariablesAsMap();
            for (Map.Entry<String, Object> entry : variablesAsMap.entrySet()) {
                System.out.println(entry.getKey() + " : " + entry.getValue());
            }

            if (CollectionUtils.isEmpty(variablesAsMap))
                throw new Exception("Job variables is empty");

            jobWorkerService.service();

            client.newCompleteCommand(job.getKey()).variables(variablesAsMap).send().join();
        } catch (Exception e) {
            int retries = job.getRetries();
            int remainingRetries = Math.max(0, retries - 1);
            System.out.println("Retries left: " + remainingRetries);

            if(1 < retries){
                client.newFailCommand(job.getKey())
                        .retries(remainingRetries)
                        .retryBackoff(Duration.ofSeconds(2))
                        .errorMessage(e.getMessage())
                        .send()
                        .join();
                return;
            }
            client.newThrowErrorCommand(job.getKey())
                    .errorCode("sonneesError")
                    .send()
                    .join();
        }
    }
}
