package com.niit.collabarate.restcontroller;

import java.util.Date;
import java.util.List;

import javax.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import com.niit.collabarate.model.Job;
import com.niit.collabarate.model.User;
import com.niit.collabarate.service.JobService;
import com.niit.collabarate.service.UserService;
import com.niit.collabarate.model.Error;

@Controller
public class JobController {
	@Autowired
	 private JobService jobService;
	@Autowired
	private UserService userService;
	
	@RequestMapping(value="/addjob",method=RequestMethod.POST)
	public ResponseEntity<?> addJob(@RequestBody Job job,HttpSession session){
	String username=(String)session.getAttribute("username");
	 if(username==null){
		Error error=new Error(6,"Unauthorized access...");
		return new ResponseEntity<Error>(error,HttpStatus.UNAUTHORIZED);
	}
	 User user=userService.getUserByUsername(username);
	 if(!user.getRole().equals("ADMIN")){
		 Error error=new Error(11,"Access Denied");
			return new ResponseEntity<Error>(error,HttpStatus.UNAUTHORIZED);

	 }
	 try{
		 job.setPostedOn(new Date());
			System.out.println("Job is  created"+ job.getJobTitle());
			jobService.addJob(job);
			return new ResponseEntity<Job>(job,HttpStatus.OK);
		}catch(Exception e){
			Error error=new Error(9,"Unable to save job details...");
			return new ResponseEntity<Error>(error,HttpStatus.NOT_ACCEPTABLE);
		}
		
	}
	
	
	@RequestMapping(value="/getalljobs",method=RequestMethod.GET)
		public ResponseEntity<?> getAllJobs(HttpSession session){
			String username=(String)session.getAttribute("username");
			 if(username==null){
				Error error=new Error(6,"Unauthorized access...");
				return new ResponseEntity<Error>(error,HttpStatus.UNAUTHORIZED);
			}
			 List<Job> jobs=jobService.getAllJobs();
			 return new ResponseEntity<List<Job>>(jobs,HttpStatus.OK);
	}
	
	@RequestMapping(value="/getjob/{jobId}",method=RequestMethod.GET)
	public ResponseEntity<?> getJobDetails(@PathVariable int jobId,HttpSession session ){
		String username=(String)session.getAttribute("username");
		 if(username==null){
			Error error=new Error(6,"Unauthorized access...");
			return new ResponseEntity<Error>(error,HttpStatus.UNAUTHORIZED);
		}
		 Job job=jobService.getJob(jobId);
		 return new ResponseEntity<Job>(job,HttpStatus.OK);
	}
}
