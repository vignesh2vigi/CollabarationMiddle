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

import com.niit.collabarate.model.BlogComment;
import com.niit.collabarate.model.BlogPost;
import com.niit.collabarate.model.User;
import com.niit.collabarate.model.Error;
import com.niit.collabarate.service.BlogPostService;
import com.niit.collabarate.service.UserService;

@Controller
public class BlogPostController {
@Autowired
private BlogPostService blogpostService;
 @Autowired
 private UserService userService;
 @RequestMapping(value="/addblogpost",method=RequestMethod.POST)
public ResponseEntity<?> addBlogPost(@RequestBody BlogPost blogPost,HttpSession session){
String username=(String)session.getAttribute("username");
	 if(username==null){
		Error error=new Error(6,"Unauthorized access...");
		return new ResponseEntity<Error>(error,HttpStatus.UNAUTHORIZED);
	}
	
	
	blogPost.setPostedOn(new Date());
	User postedBy=userService.getUserByUsername(username);
	blogPost.setPostedBy(postedBy);
	try{
		blogpostService.addBlogPost(blogPost);
		return new ResponseEntity<BlogPost>(blogPost,HttpStatus.OK);
	}catch(Exception e){
		Error error=new Error(9,"Unable to save blog post details...");
		return new ResponseEntity<Error>(error,HttpStatus.INTERNAL_SERVER_ERROR);
	}
	
}
 @RequestMapping(value="/getblogs/{approved}",method=RequestMethod.GET)
 public ResponseEntity<?> getBlogs(@PathVariable int approved,HttpSession session){
	 String username=(String)session.getAttribute("username");
	 if(username==null){
		Error error=new Error(6,"Unauthorized access...");
		return new ResponseEntity<Error>(error,HttpStatus.UNAUTHORIZED);
	}
	 List<BlogPost> blogs=blogpostService.getBlogs(approved);
		return new ResponseEntity<List<BlogPost>>(blogs,HttpStatus.OK);

 }
 @RequestMapping(value="/getblogbyid/{id}",method=RequestMethod.GET)
 public ResponseEntity<?> getBlogById(@PathVariable int id,HttpSession session){
	 String username=(String)session.getAttribute("username");
	 if(username==null){
		Error error=new Error(6,"Unauthorized access...");
		return new ResponseEntity<Error>(error,HttpStatus.UNAUTHORIZED);
	}
	 BlogPost blogPost=blogpostService.geBlogById(id);
	 return new ResponseEntity<BlogPost>(blogPost,HttpStatus.OK);
 }


 @RequestMapping(value="/update",method=RequestMethod.PUT)
 public ResponseEntity<?> updateBlogPost(@RequestBody BlogPost blogPost,HttpSession session){
	 String username=(String)session.getAttribute("username");
	 if(username==null){
		Error error=new Error(6,"Unauthorized access...");
		return new ResponseEntity<Error>(error,HttpStatus.UNAUTHORIZED);
	}
	 if(!blogPost.isApproved()&& blogPost.getRejectionReason()==null)
		 blogPost.setRejectionReason("Not Mentioned");
	 blogpostService.updateBlogPost(blogPost);
	 return new ResponseEntity<BlogPost>(blogPost,HttpStatus.OK);
 }
 @RequestMapping(value="/addcomment",method=RequestMethod.POST)
 public ResponseEntity<?> addBlogComment(@RequestBody BlogComment blogComment,HttpSession session){
	 String username=(String)session.getAttribute("username");
	 if(username==null){
		Error error=new Error(6,"Unauthorized access...");
		return new ResponseEntity<Error>(error,HttpStatus.UNAUTHORIZED);
	}
	 User user=userService.getUserByUsername(username);
	 blogComment.setCommentedBy(user);
	 blogComment.setCommentedOn(new Date());
	try {
	 blogpostService.addBlogComment(blogComment);
	 return new ResponseEntity<BlogComment>(blogComment,HttpStatus.OK);
	}catch(Exception e){
		Error error=new Error(9,"Unable to post comments...");
		return new ResponseEntity<Error>(error,HttpStatus.INTERNAL_SERVER_ERROR);
	}
	
	 
 }
 @RequestMapping(value="/getcomments/{blogPostId}",method=RequestMethod.GET)
	public ResponseEntity<?> getComments(@PathVariable int blogPostId,HttpSession session){
		String username=(String)session.getAttribute("username");
		 if(username==null){
			Error error=new Error(6,"Unauthorized access...");
			return new ResponseEntity<Error>(error,HttpStatus.UNAUTHORIZED);
		}
		 List<BlogComment> blogComments=blogpostService.getBlogComments(blogPostId);
		 return new ResponseEntity<List<BlogComment>>(blogComments,HttpStatus.OK);
 }
 }