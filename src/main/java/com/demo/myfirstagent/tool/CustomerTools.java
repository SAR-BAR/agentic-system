package com.demo.myfirstagent.tool;

import com.demo.myfirstagent.domain.Customer;
import com.demo.myfirstagent.coordinator.SupportCaseHolder;
import com.demo.myfirstagent.model.ToolError;
import com.demo.myfirstagent.model.ToolResponse;
import com.demo.myfirstagent.repository.CustomerRepository;
import dev.langchain4j.agent.tool.Tool;
import org.springframework.stereotype.Component;

@Component
public class CustomerTools {

    private final SupportCaseHolder supportCaseHolder;
    private final CustomerRepository customerRepository;

    public CustomerTools(SupportCaseHolder supportCaseHolder, CustomerRepository customerRepository) {
        this.supportCaseHolder = supportCaseHolder;
        this.customerRepository = customerRepository;
    }

    @Tool("""
            Retrive a customer profile by customerId.
            Use this tool to verify that a customer exists.
            Customerids have the format C followed by digits, for example C001.
            """)
    public ToolResponse<Customer> getcustomerRecord(String customerid){
        System.out.println("[TOOL]: getcustomer(" + customerid + ")");

        Customer customer = customerRepository.findById(customerid).orElse(null);
        if(customer == null){
            return ToolResponse.error(new ToolError("validation", false, "No customer found with id "+ customerid, null));
        }
        supportCaseHolder.current().session().verifyCustomer(customer.getCustomerId());
        return ToolResponse.success(customer);
    }
}
