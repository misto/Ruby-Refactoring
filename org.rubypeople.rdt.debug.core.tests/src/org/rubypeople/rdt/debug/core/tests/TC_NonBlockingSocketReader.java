package org.rubypeople.rdt.debug.core.tests;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

import junit.framework.TestCase;

import org.rubypeople.rdt.internal.debug.core.parsing.MultiReaderStrategy;

public class TC_NonBlockingSocketReader extends TestCase {
	private Socket socket ;
	private PrintWriter out ;
	private BufferedReader reader ;
	private Process process ;
	
	public TC_NonBlockingSocketReader(String name) {
		super(name);
	}

	protected void setUp() throws Exception {
		super.setUp();
		String binDir = this.getClass().getResource("/").getFile().replaceFirst("/","") ;
		String cmd = "ruby " +binDir+ "../ruby/testNonBlockingSocketReader.rb" ;
		System.out.println("Starting: " + cmd);
		process = Runtime.getRuntime().exec(cmd);
		Thread.sleep(1500) ;
		socket = new Socket("localhost", 12134);
		out = new PrintWriter(socket.getOutputStream(), true);
		reader = new BufferedReader(new InputStreamReader(socket.getInputStream())) ;
	}
	
	protected void tearDown() throws Exception {
		socket.close() ;
		process.destroy() ;	
	}
	
	public void testCountOfOperationsInBackground() throws Exception{
		out.println("test1") ;
		int start = Integer.parseInt(reader.readLine()) ;
		System.out.println(start) ;
		Thread.sleep(1000) ;
		out.println("test2") ;
		int afterOneSecond = Integer.parseInt(reader.readLine()) ; 
		System.out.println(afterOneSecond) ;		
		Thread.sleep(3000) ;		
		out.println("test3") ;
		int afterThreeSeconds = Integer.parseInt(reader.readLine()) ; 
		System.out.println(afterThreeSeconds) ;
		int operationsPerSecond = afterOneSecond-start ;
		int operationsInThreeSeconds = afterThreeSeconds - afterOneSecond ;
		System.out.println(operationsPerSecond) ;
		System.out.println(operationsInThreeSeconds) ;
		int diff = Math.abs(operationsInThreeSeconds - (operationsPerSecond*3)) ;
		System.out.println(diff) ;
		// allow 15 percent deviation
		this.assertTrue(diff < operationsPerSecond * 0.15) ;
	}

}
