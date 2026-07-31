package com.jobportal.controller;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.jobportal.service.CompanyService;
import com.jobportal.service.JobApplicationService;
import com.jobportal.service.JobService;
import com.jobportal.service.RecruterService;

@RestController
@RequestMapping("/employer")
public class EmployerController {

    private  CompanyService companyService;
    private  JobService jobService;
    private  RecruterService recruiterService;
    private  JobApplicationService applicationService;

}