package com.aisla.newrolit.controller;

import java.io.IOException;
import java.net.UnknownHostException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;

import com.aisla.newrolit.base.ClientDataExchange;

@Controller
public class InterfaceController implements IInterfaceController {

	ClientDataExchange cliente = new ClientDataExchange();

	
	
	
	@RequestMapping("/inicio")
	public ModelAndView getIndex(){
		return new ModelAndView("index");
	}
	public String refrescar(HttpServletRequest request, HttpServletResponse response,String sessionId) {
		try {
			//return cliente.getScreenResponseTest(true);
			return cliente.getScreenRefreshTest(request,response,sessionId);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;

	}

	@Override
	public String conectar(HttpServletRequest request) {

		try {
			cliente.logUserTest("userDefault", "default", request);
			String info ="{'fields':[],'sessionId':'"+cliente.getLastSessionIdCreated()+"','i36Session':'','aidKey':{'cursorY':'6','cursorX':'52','value':'33554432'}}";
			return enviarAServidor(info,request);

			

		} catch (UnknownHostException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		return null;
	}
 public String enviarAServidor(String info, HttpServletRequest request){
	 try {

			if (cliente.sendDataTest(info).equalsIgnoreCase("sesion valida")){
				return cliente.getScreenResponseTest(true);
			}else{
				return "sesion invalida";
			}

			
		} catch (UnknownHostException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		return null;
 }
	public String enviar(@RequestParam String info, HttpServletRequest request) {

		return enviarAServidor(info,request);
	}
	@Override
	public String cerrarSesion(String sessionId) {
		return cliente.closeSesion(sessionId);
	}

}
