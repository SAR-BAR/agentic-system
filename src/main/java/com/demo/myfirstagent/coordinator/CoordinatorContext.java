package com.demo.myfirstagent.coordinator;

import com.demo.myfirstagent.model.VerificationFindings;

public class CoordinatorContext {
    private VerificationFindings verificationFindings;

    public VerificationFindings getVerificationFindings(){
        return this.verificationFindings;
    }

    public void setVerificationFindings(VerificationFindings verificationFindings){
        this.verificationFindings = verificationFindings;
    }

}
