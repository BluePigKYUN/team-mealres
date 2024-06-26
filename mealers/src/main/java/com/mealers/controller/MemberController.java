package com.mealers.controller;

import java.io.File;
import java.io.IOException;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;

import com.mealers.annotation.Controller;
import com.mealers.annotation.RequestMapping;
import com.mealers.annotation.RequestMethod;
import com.mealers.annotation.ResponseBody;
import com.mealers.dao.MemberDAO;
import com.mealers.domain.MemberDTO;
import com.mealers.domain.SessionInfo;
import com.mealers.servlet.ModelAndView;
import com.mealers.util.FileManager;
import com.mealers.util.MyMultipartFile;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import jakarta.servlet.http.Part;

@Controller
public class MemberController {
	@RequestMapping(value = "/member/login", method = RequestMethod.GET)
	public ModelAndView loginForm(HttpServletRequest req, HttpServletResponse resp)
			throws ServletException, IOException {
		return new ModelAndView("member/login");
	}

	@RequestMapping(value = "/member/login", method = RequestMethod.POST)
	public ModelAndView loginSubmit(HttpServletRequest req, HttpServletResponse resp)
			throws ServletException, IOException {
		
		HttpSession session = req.getSession();

		MemberDAO dao = new MemberDAO();

		String memberId = req.getParameter("memberId");
		String memberPwd = req.getParameter("memberPwd");

		MemberDTO dto = dao.loginMember(memberId, memberPwd);
		if (dto != null) {
			
			session.setMaxInactiveInterval(20 * 60);

			SessionInfo info = new SessionInfo();
			info.setUserId(dto.getMemberId());
			info.setUserName(dto.getMem_Nick());
			info.setUserNum(dto.getUserNum());
			
			//중요
			info.setFileName(dto.getFileName());

			
			session.setAttribute("member", info);
			
			String preLoginURI = (String)session.getAttribute("preLoginURI");
			session.removeAttribute("preLoginURI");
			if(preLoginURI != null) {
				
				return new ModelAndView(preLoginURI);
			} 
			
			return new ModelAndView("redirect:/");
		}

		ModelAndView mav = new ModelAndView("member/login");

		String msg = "아이디 또는 패스워드가 일치하지 않습니다.";
		mav.addObject("message", msg);

		return mav;
	}

	@RequestMapping(value = "/member/logout", method = RequestMethod.GET)
	public ModelAndView logout(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		HttpSession session = req.getSession();

		session.removeAttribute("member");
		session.invalidate();

		return new ModelAndView("redirect:/");
	}
	
	
	/*
	 * @RequestMapping(value="/member/login",method = RequestMethod.GET) public
	 * ModelAndView singupForm(HttpServletRequest req, HttpServletResponse resp)
	 * throws ServletException,IOException{ ModelAndView mav = new
	 * ModelAndView("member/login");
	 * 
	 * return mav; }
	 */
	
	//회원가입
	@RequestMapping(value = "/member/join", method = RequestMethod.POST)
	public ModelAndView memberSubmit(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
	    MemberDAO dao = new MemberDAO();
	    String message = "";

	    try {
	        MemberDTO dto = new MemberDTO();
	        dto.setMemberId(req.getParameter("memberId"));
	        dto.setMemberPwd(req.getParameter("memberPwd"));
	        dto.setMem_Nick(req.getParameter("nickname"));
	        dto.setMem_Email(req.getParameter("email"));

	        dao.insertMember(dto);

	        // 회원가입 후 세션에 사용자 정보 저장
	        HttpSession session = req.getSession();
	        SessionInfo sessionInfo = new SessionInfo();
	        sessionInfo.setUserId(dto.getMemberId());
	        sessionInfo.setUserName(dto.getMem_Nick());

	        session.setAttribute("member", sessionInfo);  

	        // 로그인 처리
	        session.setAttribute("isLoggedIn", true);

	        return new ModelAndView("redirect:/");
	    } catch (SQLException e) {
	        message = "회원 가입 실패";
	        e.printStackTrace();
	    }

	    ModelAndView mav = new ModelAndView("member/join");
	    mav.addObject("title", "회원 가입");
	    mav.addObject("message", message);

	    return mav;
	}




	@ResponseBody
	@RequestMapping(value="/member/userIdCheck",method = RequestMethod.POST)
	public Map<String, Object> userIdCheck(HttpServletRequest req, HttpServletResponse resp) throws ServletException,IOException{
		Map<String, Object> map = new HashMap<String, Object>();
		
		MemberDAO dao = new MemberDAO();
		
		String memberId = req.getParameter("memberId");
		MemberDTO dto = dao.findById(memberId);
		
		String passed = "false";
		if(dto==null) {
			passed="true";
		}
		map.put("passed", passed);
		
		return map;
		
	}
	
	
	@RequestMapping(value = "/member/pwd", method = RequestMethod.POST)
	public ModelAndView pwdSubmit(HttpServletRequest req, HttpServletResponse resp)
			throws ServletException, IOException {
		
		MemberDAO dao = new MemberDAO();
		HttpSession session = req.getSession();

		try {
			SessionInfo info = (SessionInfo) session.getAttribute("member");
			MemberDTO dto = dao.findById(info.getUserId());
			
			if (dto == null) {
				session.invalidate();
				return new ModelAndView("redirect:/");
			}

			String userPwd = req.getParameter("memberPwd");
			String mode = req.getParameter("mode");
	        
			if (!dto.getMemberPwd().equals(userPwd)) {
				ModelAndView mav = new ModelAndView("member/mypage");

				mav.addObject("mode", mode);
				mav.addObject("message", "패스워드가 일치하지 않습니다.");

				mav.addObject("dto", dto);

				return mav;
			}

			if (mode.equals("delete")) {
				
				dao.deleteMember(info.getUserId());

				
				 session.removeAttribute("member"); 
				 session.invalidate();
				 

				return new ModelAndView("redirect:/main");
			}

			//단순 정보 변경 & 비밀번호 업데이트 
			if (mode.equals("update") || mode.equals("pwdupdate")) {

				dto.setMemberId(req.getParameter("memberId"));

			    if(mode.equals("update"))
				dto.setMemberPwd(req.getParameter("memberPwd"));
				
				if (mode.equals("pwdupdate"))
					dto.setMemberPwd(req.getParameter("confirmpassword"));

				dto.setMem_Nick(req.getParameter("mem_Nick"));
				dto.setMem_Email(req.getParameter("mem_Email"));

				dao.updateMember(dto);

				return new ModelAndView("redirect:/member/mypage");

				
			}

			// 회원정보수정 - 회원수정폼으로 이동
			/*
			 * ModelAndView mav = new ModelAndView("member/member");
			 * 
			 * mav.addObject("title", "회원 정보 수정"); mav.addObject("dto", dto);
			 * mav.addObject("mode", "update");
			 * 
			 * return mav;
			 */
		} catch (Exception e) {
			e.printStackTrace();
		}

		return new ModelAndView("redirect:/");
	}
	
	//프로필사진 업데이트 
	@RequestMapping(value = "/profile/update", method = RequestMethod.POST)
	public ModelAndView updateProfile(HttpServletRequest req, HttpServletResponse resp)
	        throws ServletException, IOException {
	    MemberDAO dao = new MemberDAO();
	    HttpSession session = req.getSession();
	    SessionInfo info = (SessionInfo) session.getAttribute("member");

	    FileManager fileManager = new FileManager();

	    String root = session.getServletContext().getRealPath("/");
	    String pathname = root + "uploads" + File.separator + "member";

	    try {
	        MemberDTO dto = new MemberDTO();

	        dto.setUserNum(info.getUserNum());
	        
	        String filename = null;
	        Part p = req.getPart("profile-picture");
	        MyMultipartFile multipart = fileManager.doFileUpload(p, pathname);

	        if (multipart != null) {
	             filename = multipart.getSaveFilename();
	            dto.setFileName(filename);
	        }

	        dao.updateProfile(dto);
	    } catch (Exception e) {
	        e.printStackTrace();
	    }

	    return new ModelAndView("redirect:/member/mypage");
	}
}
