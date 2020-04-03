package agents;

import containers.ConsumerContainer;
import jade.core.AID;
import jade.core.Agent;
import jade.core.behaviours.Behaviour;
import jade.core.behaviours.CyclicBehaviour;
import jade.core.behaviours.OneShotBehaviour;
import jade.core.behaviours.ParallelBehaviour;
import jade.core.behaviours.TickerBehaviour;
import jade.gui.GuiAgent;
import jade.gui.GuiEvent;
import jade.lang.acl.ACLMessage;

public class ConsumerAgent extends GuiAgent{
	
	//transient = qui n'est pas sérializable (on dit à l'agent qd tu va migrer 
	//ce n'est pas la peine de ramener avec toi le gui
	private transient ConsumerContainer gui;
	
	//la première fct qui sera exécutée une fait l'agent est deployé
	@Override
	protected void setup() {
		
		if(getArguments().length==1) {
			gui=(ConsumerContainer) getArguments()[0];
			gui.setConsumerAgent(this);
		}
		
//		System.out.println("********************");
//		System.out.println("Agent Initialisation ..."+this.getAID().getName());
//		if(this.getArguments().length==1) {
//			System.out.println("Je vais tenter d'acher le livre "+getArguments()[0]);
//		}
//		System.out.println("********************");
		

		ParallelBehaviour parallelBehaviour=new ParallelBehaviour();
		addBehaviour(parallelBehaviour);
		
//		parallelBehaviour.addSubBehaviour(new TickerBehaviour(this,1000) {
//			
//			@Override
//			protected void onTick() {
//				System.out.println("TICK...");
//			}
//		});
//		
//		
//		parallelBehaviour.addSubBehaviour(new OneShotBehaviour() {
//			
//			@Override
//			public void action() {
//				System.out.println("Action ..........");
//				
//			}
//		});
		
		parallelBehaviour.addSubBehaviour(new CyclicBehaviour() {
			
			@Override
			public void action() {
				ACLMessage aclMessage=receive();
				if(aclMessage!=null) {
					switch (aclMessage.getPerformative()) {
					case ACLMessage.CONFIRM:
						gui.logMessage(aclMessage);
						break;

					default:
						break;
					}
//					System.out.println("*******************************");
//					System.out.println("Réception du message");
//					System.out.println(aclMessage.getContent());
//					System.out.println(aclMessage.getSender().getName());
//					System.out.println(aclMessage.getPerformative());
//					System.out.println(aclMessage.getLanguage());
//					System.out.println(aclMessage.getOntology());
//					System.out.println("********************************");
//					
//					ACLMessage reply=aclMessage.createReply();
//					reply.setContent("Hello Agent");
//					send(reply);
				}
			
				else
					block();
			}
		});
		
		
//		parallelBehaviour.addSubBehaviour(new Behaviour() {
//			private int compteur=0;
//			
//			@Override
//			public boolean done() {
//				if(compteur==10)
//					return true;
//				else
//					return false;
//			}
//			
//			//la méthode qu'on souhaite exécuter
//			@Override
//			public void action() {
//				++compteur;
//				System.out.println("Etape "+compteur);
//				
//			}
//		});
	}

	//va s'exécuter avant la migration(vers un autre container)
	@Override
	protected void beforeMove() {
		System.out.println("******************");
		System.out.println("Avant Migration...");
		System.out.println("******************");
	}
	
	@Override
	protected void afterMove() {
		System.out.println("*****************");
		System.out.println("Après Migration...");
		System.out.println("*****************");
	}
	
	//s'exécute avant destruction ou meurt de l'agent
	@Override
	protected void takeDown() {
		System.out.println("*****************");
		System.out.println("Je suis entrain de mourir...");
		System.out.println("*****************");
	}

	@Override
	public void onGuiEvent(GuiEvent params) {
		if(params.getType()==1) {
			String livre=params.getParameter(0).toString();
			ACLMessage aclMessage=new ACLMessage(ACLMessage.REQUEST);
			aclMessage.setContent(livre);
			aclMessage.addReceiver(new AID("ACHETEUR",AID.ISLOCALNAME));
			send(aclMessage);
			
		}
		
	}
}
