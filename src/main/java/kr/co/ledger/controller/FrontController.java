package kr.co.ledger.controller;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Properties;

import jakarta.servlet.ServletConfig;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import kr.co.ledger.action.Action;

public class FrontController extends HttpServlet{
	private Map<String, Action> commandMap = new HashMap<>();

	@Override
	public void init(ServletConfig config) throws ServletException {
	    super.init(config);
	    String configFile = config.getInitParameter("configFile");
	    
	    Properties prop = new Properties();
	    
	    try (InputStream is = config.getServletContext().getResourceAsStream(configFile)) {
	        
	        if (is == null) {
	            System.out.println("FrontController - " + configFile + "파일이 프로젝트 경로에 존재하지 않음");
	            System.out.println("FrontController - src/main/webapp/WEB-INF/ 폴더 안에 command.properties 파일이 있는지 확인");
	            return;
	        }
	        
	        prop.load(is);
	        System.out.println("FrontController - command.properties 정상 로드 완료");
	        
	    } catch (Exception e) {
	        e.printStackTrace();
	    }
	    
	    Iterator<Object> keyIter = prop.keySet().iterator();
	    while(keyIter.hasNext()) {
	        String command = (String) keyIter.next();
	        String className = prop.getProperty(command);
	        
	        try {
	            Class<?> actionClass = Class.forName(className);
	            Action actionInstance = (Action) actionClass.getDeclaredConstructor().newInstance();
	            commandMap.put(command, actionInstance);
	        } catch (Exception e) {
	            e.printStackTrace();
	        }
	    }
	}
	
	@Override
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		doProcess(request, response);
	}

	@Override
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		doProcess(request,response);
	}
	
	private void doProcess(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		request.setCharacterEncoding("UTF-8");
		
		String uri = request.getRequestURI();
		String contextPath = request.getContextPath();
		String command = uri.substring(contextPath.length());
		
		Action action = commandMap.get(command);
		
		if(action == null) {
			response.sendError(HttpServletResponse.SC_NOT_FOUND, "요청하신 페이지를 찾을 수 없습니다.");
			return;
		}
		
		try {
			String viewPage = action.execute(request, response);
			if(viewPage != null){
			    if (viewPage.startsWith("redirect:")) {
			        String redirectUrl = viewPage.substring(9);
			        response.sendRedirect(redirectUrl);
			    } 
			    else {
			        if (!viewPage.startsWith("/WEB-INF")) {
			            viewPage = "/WEB-INF" + viewPage; 
			        }
			        request.getRequestDispatcher(viewPage).forward(request, response);
			    }
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
	
