package com.github.doodler.common.webmvc;

import java.io.IOException;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * @Description: IndexPage
 * @Author: Fred Feng
 * @Date: 22/02/2023
 * @Version 1.0.0
 */
public class IndexPage extends HttpServlet {

	private static final long serialVersionUID = -2486646666431193517L;

	IndexPage(String servletContextPath) {
		this.servletContextPath = servletContextPath;
	}

	private final String servletContextPath;

	@Override
	public void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		resp.sendRedirect(servletContextPath + "/");
	}

	@Override
	public void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		doGet(req, resp);
	}
}