package com.niit.collabarate.restcontroller;

import javax.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import com.niit.collabarate.dao.UserDAO;
import com.niit.collabarate.model.User;
import com.niit.collabarate.model.Error;

@Controller
	public class UserController {
		@Autowired
		private UserDAO userdao;
		
		@RequestMapping(value="/register", method=RequestMethod.POST)
		public ResponseEntity<?> getregisterform(@RequestBody User user){
			if(!userdao.isvalidemail(user.getEmail()))
			{
				System.out.println(user.getEmail());
				Error error=new Error(2,"Emailid already exists");
				return new ResponseEntity<Error>(error,HttpStatus.NOT_ACCEPTABLE);
			}
			if(!userdao.isvalidusername(user.getUsername()))
			{
				System.out.println(user.getUsername());
				Error error=new Error(3,"Username already exists");
				return new ResponseEntity<Error>(error,HttpStatus.NOT_ACCEPTABLE);
				
			}
			try{
			
				userdao.registerUser(user);
				return new ResponseEntity<User>(user,HttpStatus.OK);
			}
			catch(Exception e){
				Error error=new Error(1,"Unable to register"+e.getMessage());
				return new ResponseEntity<Error>(error,HttpStatus.INTERNAL_SERVER_ERROR);
			}
			
		}

		@RequestMapping(value="/login", method=RequestMethod.POST)
		public ResponseEntity<?> login(@RequestBody User user,HttpSession session){
			User validUser=userdao.login(user);
			if(validUser==null){
				Error error=new Error(4,"Invalid Username and password");
				return new ResponseEntity<Error>(error,HttpStatus.UNAUTHORIZED);
		}
			validUser.setOnline(true);
			userdao.updateuser(validUser);
			session.setAttribute("username", validUser.getUsername());
			return new ResponseEntity<User>(validUser,HttpStatus.OK);
		}
		@RequestMapping(value="/logout", method=RequestMethod.PUT)
		public ResponseEntity<?> logout(HttpSession session){
			String username=(String) session.getAttribute("username");
			if(username==null){
				Error error=new Error(5,"please login");
				return new ResponseEntity<Error>(error,HttpStatus.UNAUTHORIZED);
		}
			User user=userdao.getUserbyUsername(username);
			user.setOnline(false);
			userdao.updateuser(user);
			session.removeAttribute("username");
			session.invalidate();
			return new ResponseEntity<User>(user,HttpStatus.OK);
		}

}
