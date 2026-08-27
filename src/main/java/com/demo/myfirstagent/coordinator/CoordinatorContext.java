package com.demo.myfirstagent.coordinator;

import com.demo.myfirstagent.model.VerificationFindings;
import org.springframework.stereotype.Component;

@Component
public class CoordinatorContext {
    private VerificationFindings verificationFindings;

    public VerificationFindings getVerificationFindings(){
        return this.verificationFindings;
    }

    public void setVerificationFindings(VerificationFindings verificationFindings){
        this.verificationFindings = verificationFindings;
    }

    public void clear(){
        this.verificationFindings = null;
    }

}
