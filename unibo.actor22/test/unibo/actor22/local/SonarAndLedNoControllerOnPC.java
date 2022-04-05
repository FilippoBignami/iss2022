package unibo.actor22.local;

import it.unibo.kactor.IApplMessage;
import it.unibo.radarSystem22.domain.utils.BasicUtils;
import it.unibo.radarSystem22.domain.utils.DomainSystemConfig;
import unibo.actor22.Qak22Util;
import unibo.actor22.common.ApplData;
import unibo.actor22.common.LedActor;
import unibo.actor22.common.SonarActor;
import unibo.actor22comm.utils.ColorsOut;
import unibo.actor22comm.utils.CommSystemConfig;
import unibo.actor22comm.utils.CommUtils;

public class SonarAndLedNoControllerOnPC {

	 private LedActor led;
	 private SonarActor sonar;
	 private IApplMessage getState ;
	 private IApplMessage isActive ;
	 
		public void doJob() {
			ColorsOut.outappl("UsingSonarAndLedNoControllerOnPc | Start", ColorsOut.BLUE);
			configure();
			BasicUtils.aboutThreads("Before execute - ");
			//BasicUtils.waitTheUser();
			execute();
			terminate();
		}
		
		protected void configure() {
			DomainSystemConfig.simulation   = true;			
			DomainSystemConfig.ledGui       = true;			
			DomainSystemConfig.tracing      = false;			
			
			CommSystemConfig.tracing        = true;
			
	 		led = new LedActor( ApplData.ledName );
	 		sonar = new SonarActor(ApplData.sonarName);
	 		//otherLeds();
	 		getState = CommUtils.buildRequest("main",  "ask", ApplData.reqLedState, ApplData.ledName); 
	 		isActive = CommUtils.buildRequest("main",  "ask", ApplData.reqSonarIsActive, ApplData.sonarName); 
	 		
	   	}
		
		protected void otherLeds() {
			//Creo altri Led per verificare che il numero di thread non aumenta
			for( int i=1; i<=3; i++) {
				new LedActor(ApplData.ledName+"_"+i);
				Qak22Util.sendAMsg(ApplData.turnOnLed, ApplData.ledName+"_"+i  );
				BasicUtils.delay(500);
				Qak22Util.sendAMsg(ApplData.turnOffLed, ApplData.ledName+"_"+i  );
			}
		}
		
		//Accende-spegne il Led due volte
		protected void execute() {
			ColorsOut.outappl("SonarandLedNoControllerOnPc | execute", ColorsOut.MAGENTA);
			for( int i=1; i<=2; i++) {
				 Qak22Util.sendAMsg( ApplData.ActivateSonar  );
			 	 CommUtils.delay(500);
		 	    Qak22Util.sendAMsg( ApplData.turnOnLed  );
		 	    CommUtils.delay(500);
		 	    
	// Inviare una request richiede un attore capace di ricevere la reply	 	    
		 	    Qak22Util.sendAMsg( getState   );
		 	    //led.elabMsg(getState);   //Richiesta asincrona. Reply inviata a main
		 	    CommUtils.delay(500);
		 	    Qak22Util.sendAMsg( isActive   );
		 	    //led.elabMsg(getState);   //Richiesta asincrona. Reply inviata a main
		 	    CommUtils.delay(500); 
				 Qak22Util.sendAMsg( ApplData.DeactivateSonar  );
			 	 CommUtils.delay(500);
		 	    Qak22Util.sendAMsg( ApplData.turnOffLed  );
		 	    //led.elabMsg(ApplData.turnOffLed);  //ALTERNATIVA all'uso della utility
		 	    CommUtils.delay(500);
		 	    //Qak22Util.sendAMsg( getState   );   //Richiesta asincrona. Reply inviata a main
			}
	 	} 

		public void terminate() {
			BasicUtils.aboutThreads("Before exit - ");
			System.exit(0);
		}
		
		public static void main( String[] args) {
			BasicUtils.aboutThreads("Before start - ");
			new SonarAndLedNoControllerOnPC().doJob();
	 		BasicUtils.aboutThreads("Before end - ");
		}

	}

	/*
	 * Threads: main + Actor22 + LedGui
	 */