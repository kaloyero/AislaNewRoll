package com.aisla.newrolit.controller;



import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;



public interface IInterfaceController {


	
	@RequestMapping(value = "/refrescar", method = RequestMethod.GET)
	public @ResponseBody String refrescar(HttpServletRequest request, HttpServletResponse response,String sessionId) ;
	@RequestMapping(value = "/conectar", method = RequestMethod.GET)
	public @ResponseBody String conectar(HttpServletRequest request) ;
	@RequestMapping(value = "/enviar", method = RequestMethod.GET)
	public @ResponseBody String enviar(@RequestParam String info,HttpServletRequest request) ;
	@RequestMapping(value = "/cerrarSesion", method = RequestMethod.GET)
	public @ResponseBody String cerrarSesion(@RequestParam String sessionId) ;

}
