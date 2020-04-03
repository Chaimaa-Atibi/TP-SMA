package containers;

import agents.ConsumerAgent;
import jade.core.ProfileImpl;
import jade.core.Runtime;
import jade.gui.GuiEvent;
import jade.lang.acl.ACLMessage;
import jade.wrapper.AgentContainer;
import jade.wrapper.AgentController;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

public class ConsumerContainer extends Application{
	
	protected ConsumerAgent consumerAgent;
	ObservableList<String> observableList;

	public void setConsumerAgent(ConsumerAgent consumerAgent) {
		this.consumerAgent = consumerAgent;
	}

	public static void main(String[] args) throws Exception {
		launch(args);
		
	}
	
	public void startContainer() throws Exception {
		Runtime runtime=Runtime.instance();
		ProfileImpl profileImpl=new ProfileImpl();
		profileImpl.setParameter(ProfileImpl.MAIN_HOST, "localhost");
		AgentContainer container=runtime.createAgentContainer(profileImpl);
		AgentController agentController=container
				.createNewAgent("Consumer", "agents.ConsumerAgent", new Object[] {this});
		agentController.start();
	}

	@Override
	public void start(Stage primaryStage) throws Exception {
		startContainer();
		primaryStage.setTitle("Consumer");
		HBox hbox=new HBox();
		hbox.setPadding(new Insets(10));
		hbox.setSpacing(10);
		Label label=new Label("Livre");
		TextField textField=new TextField();
		Button buttonAcheter=new Button("Acheter");
		hbox.getChildren().addAll(label,textField,buttonAcheter);
		
		VBox vBox=new VBox();
		vBox.setPadding(new Insets(10));
		
		observableList=FXCollections.observableArrayList();
		ListView<String> listViewMessages=new ListView<String>(observableList);
		
		vBox.getChildren().add(listViewMessages);
		
		BorderPane borderPane=new BorderPane();
		borderPane.setTop(hbox);
		borderPane.setCenter(vBox);
		
		Scene scene=new Scene(borderPane,600,400);
		primaryStage.setScene(scene);
		primaryStage.show();
		
		//un message ACL ne peut être envoyé qu'à travers un agent
		buttonAcheter.setOnAction(e->{
			String livre=textField.getText();
			//observableList.add(livre);
			GuiEvent event=new GuiEvent(this, 1);
			event.addParameter(livre);
			consumerAgent.onGuiEvent(event);
		});
	}
	
	public void logMessage(ACLMessage aclMessage) {
		Platform.runLater(()->{
			observableList.add(aclMessage.getContent()
					+", "+aclMessage.getSender().getName()
					);
		});
	}
}
