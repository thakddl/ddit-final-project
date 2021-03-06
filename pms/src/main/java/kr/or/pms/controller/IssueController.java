package kr.or.pms.controller;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import kr.or.pms.command.IssueCriteria;
import kr.or.pms.command.IssueRegistCommand;
import kr.or.pms.dto.HistoryVO;
import kr.or.pms.dto.IssueVO;
import kr.or.pms.dto.ProjectVO;
import kr.or.pms.dto.PushVO;
import kr.or.pms.dto.ReplyVO;
import kr.or.pms.dto.UserVO;
import kr.or.pms.dto.WorkVO;
import kr.or.pms.service.HistoryService;
import kr.or.pms.service.IssueService;
import kr.or.pms.service.ProjectService;
import kr.or.pms.service.PushService;
import kr.or.pms.service.UserService;
import kr.or.pms.service.WorkService;

@Controller
@RequestMapping("/project/issue")
public class IssueController {

	@Resource(name="issueService")
	private IssueService service;
	
	@Resource(name="userService")
	private UserService userService;
	
	@Resource(name="pushService")
	private PushService pushService;
	
	@Resource(name="historyService")
	private HistoryService historyService;
	
	@Resource(name="projectService")
	private ProjectService projectService;
	
	@Resource(name="workService")
	private WorkService workService;
	
	@RequestMapping("/list")
	public ModelAndView list(IssueCriteria cri, ModelAndView mnv, HttpSession session) throws Exception{
		
		String url="project/issue/list";
		
		int prjNo = (int) session.getAttribute("currentPrjNo");
		cri.setPrjNo(prjNo);
		
		List<IssueVO> bmkList = service.getAllPrjBookMarkTitleByPrjNo(prjNo);
		Map<String, Object> dataMap = service.getIssueList(cri);
		
		IssueVO issue = service.getprjName(prjNo);
		String prjNm=issue.getPrjNm();

//		int bmkNo = 0;
//		Map<String, Integer> bmktitle = new HashedMap<String, Integer>();
		
		mnv.addObject("prjNo", prjNo);
		mnv.addObject("dataMap",dataMap);
		mnv.addObject("bmkList",bmkList);
		mnv.addObject("prjNm",prjNm);
		mnv.setViewName(url);
		
		return mnv; 
	}
	
	@RequestMapping(value="/modifyStt/close", produces = "text/plain;charset-utf-8")
	@ResponseBody
	public ResponseEntity<String> modifyIssueSttToClose(@RequestBody List<Integer>issueNoList, HttpSession session) throws Exception{
		ResponseEntity<String> entity = null;
		int prjNo = (int) session.getAttribute("currentPrjNo");
		
		int result = 0;
		for (int i = 0; i < issueNoList.size(); i++) {
			List<IssueVO>issue = new ArrayList<IssueVO>();
			IssueVO issueVO = new IssueVO();

			//cri.setPrjNo(prjNo);
			
			issueVO.setIssueNo(issueNoList.get(i));
			issueVO.setPrjNo(prjNo);
			
			issue.add(issueVO);
			
			result += service.modifyIssueSttToClose(issue, prjNo);
		}

		if(result > 0) {
			entity = new ResponseEntity<String>("success",HttpStatus.OK);
		} else {
			entity = new ResponseEntity<String>("",HttpStatus.OK);
		}
		
		return entity;
	}
	
	@RequestMapping(value="/modifyStt/open", produces = "text/plain;charset-utf-8")
	@ResponseBody
	public ResponseEntity<String> modifyIssueSttToOpen(@RequestBody List<Integer>issueNoList, HttpSession session) throws Exception{
		ResponseEntity<String> entity = null;
		int prjNo = (int) session.getAttribute("currentPrjNo");
		int result = 0;
		for (int i = 0; i < issueNoList.size(); i++) {
			List<IssueVO>issue = new ArrayList<IssueVO>();
			IssueVO issueVO = new IssueVO();
			
			issueVO.setIssueNo(issueNoList.get(i));
			issueVO.setPrjNo(prjNo);
			
			issue.add(issueVO);
			
			result += service.modifyIssueSttToOpen(issue, prjNo);
		}
		
		if(result > 0) {
			entity = new ResponseEntity<String>("success",HttpStatus.OK);
		} else {
			entity = new ResponseEntity<String>("",HttpStatus.OK);
		}
		
		return entity;
	}
	
	@RequestMapping("/registForm")
	public ModelAndView registForm(HttpSession session, ModelAndView mnv) throws Exception{
		String url="project/issue/registForm";
		int prjNo = (int) session.getAttribute("currentPrjNo");
		
//		List<IssueVO> prjMemList = service.getProjectMember(prjNo);
		List<IssueVO> workList = service.getWorkInfoByPrjNo(prjNo);
		
		IssueVO issue = service.getprjName(prjNo);
		String prjNm = issue.getPrjNm();
		
		mnv.addObject("prjNo", prjNo);
		mnv.addObject("prjNm",prjNm);
		mnv.addObject("workList", workList);
		mnv.setViewName(url);
		
		return mnv; 
	};
	
    	
	@RequestMapping(value = "/regist",method = RequestMethod.POST, produces = "text/plain;charset=utf-8") 
	public String regist(String from, @RequestParam(value="bookmarkNo", defaultValue="0") int bookmarkNo,
			@ModelAttribute IssueRegistCommand issueReq, HttpSession session, HttpServletRequest req, RedirectAttributes rttr) throws Exception{
    	String url = "redirect:/project/issue/list";
    	String workListReturnURL = "redirect:/project/work/list";
    	String ganttListReturnURL = "redirect:/project/gantt/list.do";
    	String bmkDetailReturnURL = "redirect:/project/bookmark/detail.do?bookmarkNo="+bookmarkNo;
//    	String url = "redirect:/project/issue/list";
    	String open = issueReq.getOpen();
		if(open != null && open.equals("1") ) {
			url = "redirect:/project/work/detail.open?open=1&workNo="+issueReq.getWorkNo();
		}
    	
    	UserVO loginUser = (UserVO) session.getAttribute("loginUser");
    	int prjNo = (int) session.getAttribute("currentPrjNo");
    	int issueNo = service.getIssueNoSeq();
    	//String userNm = null;
    	
    	IssueVO issue = issueReq.toIssueVO();
    	
    	issue.setPrjNo(prjNo);
    	issue.setIssueNo(issueNo);
    	
    	service.registIssue(issue);
    	
    	// ???????????? ??????
    	HistoryVO history = new HistoryVO();
    	history.setUserId(issue.getIssueRegister());
    	history.setPrjNo(prjNo);
    	history.setFromWhere("ISSUE");
    	history.setUrl("/project/issue/detail.do?issueNo="+issueNo);
    	history.setTitle(loginUser.getUserNm()+" ?????? ["+issue.getTitle()+"] ????????? ?????????????????????.");
    	history.setContent(issue.getContent());
    	historyService.regist(history);
    	
    	//?????? ?????? ??????
    	List<IssueVO>prjMem = service.getProjectMember(prjNo);
    	PushVO push = new PushVO();
    	ProjectVO prj=projectService.getProject(prjNo);
    	String prjNm = prj.getPrjNm();
    	
    	String registerNm = userService.getUserNm(issue.getIssueRegister()); 
    	if(prjMem!=null) {
    		for (IssueVO mem : prjMem) {
    			push.setFromWhere("??????");
    			push.setReceiver(mem.getUserId());
    			push.setPrjNo(Integer.toString(prjNo));
    			push.setMessage(prjNm+" ??????????????? "+registerNm+"?????? ????????? ????????? ??????????????????.");
    			push.setUrl("/project/issue/detail.do?prjNo="+session.getAttribute("currentPrjNo")+"&issueNo="+issueNo+"&from=push");
    			pushService.regist(push);
    		}
    	}
    	
    	// ????????????????????? ?????? ?????? ??? ?????? ???????????? redirect
    	if(from.equals("workList")) {
    		rttr.addFlashAttribute("from", "issueRegist");
    		return workListReturnURL;
		// ???????????? ?????? ?????? ??? ?????? ???????????? redirect
    	} else if(from.equals("gantt")) {
    		rttr.addFlashAttribute("from", "ganttRegist");
    		return ganttListReturnURL;
    	} else if(bookmarkNo>0) {
    		rttr.addFlashAttribute("from", "bmkRegist");
    		return bmkDetailReturnURL; 
    	} else {
			rttr.addFlashAttribute("from", "regist");
			return url; 
    	}
    }
    	
	@RequestMapping("/detail")
	public ModelAndView detail(ModelAndView mnv, @RequestParam(defaultValue = "") String from,
								@RequestParam(defaultValue = "0") int issueNo, HttpSession session) throws Exception{
		String url = "project/issue/detail";
		int prjNum = (int) session.getAttribute("currentPrjNo");

		IssueVO issue = new IssueVO();
		
		issue.setPrjNo(prjNum);
		issue.setIssueNo(issueNo);
		
		issueNo = issue.getIssueNo();
		int prjNo = issue.getPrjNo();
		
		issue = service.getissue(issueNo,prjNo);
		
		WorkVO work = workService.getWork(issue.getWorkNo());
		String workTitle = work.getWorkTitle();
		
		IssueVO isu = service.getprjName(prjNo);
		String prjNm=isu.getPrjNm();
		
		List<ReplyVO> issueReplyList = service.getReplyListByIssueNo(issueNo);
		
		mnv.addObject("issueReplyList", issueReplyList);
		
		mnv.addObject("issue",issue);
		mnv.addObject("prjNm",prjNm);
		mnv.addObject("workTitle",workTitle);
		mnv.setViewName(url);
		
		return mnv;
	}
	
	@RequestMapping(value="/modifyTitle",method = { RequestMethod.PUT, RequestMethod.PATCH } )
	public ResponseEntity<String>modifyTitle(@RequestBody Map<String,Object>issueTitle, HttpSession session) throws Exception{
		ResponseEntity<String>entity = null;
		service.modifyIssueTitle(issueTitle);
		
//////////////////////////////////////////
		/* ???????????? ?????? - ?????? ?????? ??? */
		int issueNo = (int) issueTitle.get("issueNo");
		int prjNo = (int) session.getAttribute("currentPrjNo");
		
		IssueVO issue = service.getissue(issueNo, prjNo);
		String userID=issue.getIssueRegister();
		
		UserVO user = userService.getUser(userID);
		
		String title = (String)issueTitle.get("title");
		
		HistoryVO history = new HistoryVO();
		history.setUserId(issue.getIssueRegister());
		history.setPrjNo(prjNo);
		history.setFromWhere("ISSUE");
		history.setUrl("/project/issue/detail.do?issueNo="+issueNo);
		history.setTitle(user.getUserNm()+" ?????? ?????? ?????????["+title+"] ??? ?????????????????????.");
		history.setContent(issue.getContent());
		historyService.regist(history);
		
		/* ?????? ?????? - ?????? ?????? ??? */
		//?????? ?????? ??????
		List<IssueVO>prjMem = service.getProjectMember(prjNo);
		PushVO push = new PushVO();
		ProjectVO prj=projectService.getProject(prjNo);
		String prjNm = prj.getPrjNm();
		
		if(prjMem!=null) {
			for (IssueVO mem : prjMem) {
				push.setFromWhere("??????");
				push.setReceiver(mem.getUserId());
				push.setPrjNo(Integer.toString(prjNo));
				push.setMessage(prjNm+" ??????????????? ????????? ?????????????????????.");
				push.setUrl("/project/issue/detail.do?prjNo="+session.getAttribute("currentPrjNo")+"&issueNo="+issueNo+"&from=push");
				pushService.regist(push);
			}
		}
		//////////////////////////////////////////
		
		
		entity = new ResponseEntity<String>(HttpStatus.OK);
		return entity;
	}
	
	@RequestMapping(value="/modifyContent",method = { RequestMethod.PUT, RequestMethod.PATCH } )
	public ResponseEntity<String>modifyContent(@RequestBody Map<String,Object>issueContent,HttpSession session) throws Exception{
		ResponseEntity<String>entity = null;
		
		issueContent.get("content");
		service.modifyIssueContent(issueContent);
				
		//////////////////////////////////////////
		/* ???????????? ?????? - ?????? ?????? ??? */
		int issueNo = (int) issueContent.get("issueNo");
		int prjNo = (int) session.getAttribute("currentPrjNo");
		
		IssueVO issue = service.getissue(issueNo, prjNo);
		String userID=issue.getIssueRegister();
		
		UserVO user = userService.getUser(userID);
		
//		String content = (String)issueContent.get("content");
		
		HistoryVO history = new HistoryVO();
		history.setUserId(issue.getIssueRegister());
		history.setPrjNo(prjNo);
		history.setFromWhere("ISSUE");
		history.setUrl("/project/issue/detail.do?issueNo="+issueNo);
		history.setTitle(user.getUserNm()+" ?????? ?????? ????????? ?????????????????????.");
		history.setContent(issue.getContent());
		historyService.regist(history);
		
		/* ?????? ?????? - ?????? ?????? ??? */
		//?????? ?????? ??????
		List<IssueVO>prjMem = service.getProjectMember(prjNo);
		PushVO push = new PushVO();
		ProjectVO prj=projectService.getProject(prjNo);
		String prjNm = prj.getPrjNm();
		
		if(prjMem!=null) {
			for (IssueVO mem : prjMem) {
				push.setFromWhere("??????");
				push.setReceiver(mem.getUserId());
				push.setPrjNo(Integer.toString(prjNo));
				push.setMessage(prjNm+" ??????????????? ????????? ?????????????????????.");
				push.setUrl("/project/issue/detail.do?prjNo="+session.getAttribute("currentPrjNo")+"&issueNo="+issueNo+"&from=push");
				pushService.regist(push);
			}
		}
		//////////////////////////////////////////
				
		
		
		entity = new ResponseEntity<String>(HttpStatus.OK);
		
		return entity;
	}
	
/* ========================[ Issue Reply ??? ]===================================*/
	
	@RequestMapping(value="/reply/regist",method = {RequestMethod.PUT, RequestMethod.PATCH})
	public ResponseEntity<String> registIssueReply(@RequestBody Map<String,Object>reply, HttpSession session, String issueNo) throws Exception{

		ResponseEntity<String>entity = null;
		
		ReplyVO rpl = new ReplyVO();
		
		/* ?????? ?????? */
		rpl.setReplyNo((int) reply.get("replyNo"));
		rpl.setContent((String)reply.get("content"));
		rpl.setRegister((String) reply.get("register"));
		service.registIssueReply(rpl);

		
		/* ?????? ????????? push ?????? */
		//?????? ?????? ??????
//		int prjNo = (int)session.getAttribute("currentPrjNo");
//		int issueNum = Integer.parseInt(issueNo);
//		
//		System.err.println(issueNum);
//		
//		IssueVO issue=service.getissue(issueNum, prjNo);
//		String register = issue.getIssueRegister();
//		
//		PushVO push = new PushVO();
//		ProjectVO prj=projectService.getProject(prjNo);
//		String prjNm = prj.getPrjNm();
//		
//		push.setFromWhere("??????");
//		push.setReceiver(register);
//		push.setPrjNo(Integer.toString(prjNo));
//		push.setMessage(prjNm+" ??????????????? ????????? ?????????????????????.");
//		push.setUrl("/project/issue/detail.do?prjNo="+session.getAttribute("currentPrjNo")+"&issueNo="+issueNo+"&from=push");
//		pushService.regist(push);
		
		entity = new ResponseEntity<String>(HttpStatus.OK);
		
		return entity;
		
	}
	
	@RequestMapping(value="/reply/modify",method = {RequestMethod.PUT, RequestMethod.PATCH})
	public ResponseEntity<String> modifyIssueReply(@RequestBody Map<String,Object>reply) throws Exception{
		
		ResponseEntity<String>entity = null;
		
		ReplyVO rpl = new ReplyVO();

		/* ?????? ?????? ?????? */
		rpl.setContent((String)reply.get("content"));
		rpl.setReplySeq((int) reply.get("replySeq"));
		service.modifyIssueReply(rpl);
		
		/* ?????? ??????????????? push ??????*/
		entity = new ResponseEntity<String>(HttpStatus.OK);
		
		return entity;
		
	}
	
//	@RequestMapping(value="/reply/remove",method = { RequestMethod.PUT, RequestMethod.PATCH})
	@RequestMapping(value="/reply/remove",method = RequestMethod.GET)
	@ResponseBody
	public ResponseEntity<String> removeIssueReply(int replySeq) throws Exception{
		
		ResponseEntity<String>entity = null;
		
		service.removeIssueReply(replySeq);
		
		entity = new ResponseEntity<String>(HttpStatus.OK);
		
		return entity;
		
	}
	
	
	
}



