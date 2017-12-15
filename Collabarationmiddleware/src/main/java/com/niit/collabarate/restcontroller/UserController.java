package com.niit.collabarate.restcontroller;

import java.util.List;

import javax.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import com.niit.collabarate.model.Error;
import com.niit.collabarate.model.User;
import com.niit.collabarate.service.UserService;
@Controller
public class UserController {
	
	@Autowired
	private UserService userService;
	
	@RequestMapping(value="/registerUser",method=RequestMethod.POST)
	public ResponseEntity<?> registerUser(@RequestBody User user){
		if(!userService.isUsernamevalid(user.getUsername())){
			Error error=new Error(2,"Username is already exist..please enter different username");
			return new ResponseEntity<Error>(error,HttpStatus.NOT_ACCEPTABLE);
		}
		if(!userService.isEmailvalid(user.getEmail())){
			Error error=new Error(3,"Email is already exist..please enter different email");
			return new ResponseEntity<Error>(error,HttpStatus.NOT_ACCEPTABLE);
		}
		boolean result=userService.registerUser(user);
		if(result)
		{
			return new ResponseEntity<User>(user,HttpStatus.OK);
		}
		else {
			Error error=new Error(1,"Unable to registerUser details");
			return new ResponseEntity<Error>(error,HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}
	
	@RequestMapping(value="/login",method=RequestMethod.POST)
	public ResponseEntity<?> login(@RequestBody User user,HttpSession session){
		User validUser=userService.login(user);
		if(validUser==null){
			Error error=new Error(4," Invalid Username/Password...");
			return new ResponseEntity<Error>(error,HttpStatus.UNAUTHORIZED);
		}
		System.out.println("ONLINE STATUS IS"+validUser.isOnline());
		validUser.setOnline(true);
		try
		{
		userService.update(validUser);
		}catch(Exception e){
			Error error=new Error(5," Unable to update online status");
			return new ResponseEntity<Error>(error,HttpStatus.INTERNAL_SERVER_ERROR);

		}
		System.out.println("ONLINE STATUS AFTER UPDATE"+validUser.isOnline());
		session.setAttribute("username", validUser.getUsername());
		return new ResponseEntity<User>(validUser,HttpStatus.OK);
	}
	@RequestMapping(value="/logout",method=RequestMethod.GET)
	public ResponseEntity<?> logout(HttpSession session){
		String username=(String)session.getAttribute("username");
		System.out.println("Name of the user is"+ username);
		if(username==null){
			Error error=new Error(6,"Unauthorized access..please login..");
			return new ResponseEntity<Error>(error,HttpStatus.UNAUTHORIZED);
		}
		User user=userService.getUserByUsername(username);
		user.setOnline(false);
		userService.update(user);
		session.removeAttribute("username");
		session.invalidate();
		return new ResponseEntity<User>(user,HttpStatus.OK);
	}
		@RequestMapping(value="/getuser",method=RequestMethod.GET)
		public ResponseEntity<?> getUser(HttpSession session){
			String username=(String)session.getAttribute("username");
			if(username==null){
				Error error=new Error(7,"Unauthorized access..please login..");
				return new ResponseEntity<Error>(error,HttpStatus.UNAUTHORIZED);
			}
			User user=userService.getUserByUsername(username);
			return new ResponseEntity<User>(user,HttpStatus.OK);
		}
		@RequestMapping(value="/updateUser",method=RequestMethod.PUT)
		public ResponseEntity<?> updateUser(@RequestBody User user,HttpSession session){
			String username=(String)session.getAttribute("username");
			if(username==null){
				Error error=new Error(7,"Unauthorized access..please login..");
				return new ResponseEntity<Error>(error,HttpStatus.UNAUTHORIZED);
			}
			if(!userService.isUpdatedEmailValid(user.getEmail(),user.getUsername())){
				Error error=new Error(3,"Email is already exist..please enter different email");
				return new ResponseEntity<Error>(error,HttpStatus.NOT_ACCEPTABLE);
			}
			try
			{
			userService.update(user);
			return new ResponseEntity<User>(user,HttpStatus.OK);
			}catch(Exception e){
				Error error=new Error(5," Unable to update user details");
				return new ResponseEntity<Error>(error,HttpStatus.INTERNAL_SERVER_ERROR);
			}
			
			
			
		}
		@RequestMapping(value="/getuser",method=RequestMethod.GET)
		public ResponseEntity<?> getuser(HttpSession session){
			 List<User> user=userService.getuser();
			 return new ResponseEntity<List<User>>(user,HttpStatus.OK);
	}
	
	
}