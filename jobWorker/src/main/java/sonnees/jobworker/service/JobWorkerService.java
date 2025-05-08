package sonnees.jobworker.service;

import org.springframework.stereotype.Service;

@Service
public class JobWorkerService {

    public void service(){
        System.out.println("CALL: service");
    }
}
