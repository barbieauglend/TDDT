package de.hhu.propra16.project7.controller;

import de.hhu.propra16.project7.catalogue.*;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;
import java.util.Timer;
import java.util.TimerTask;

import javafx.fxml.*;
import javafx.scene.control.*;
import javafx.stage.Stage;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.scene.*;
import javafx.scene.text.*;
import javafx.scene.shape.*;
import javafx.scene.paint.*;

public class Project_Controller
{
	@FXML Button testButton, codeButton, refractoringButton, backButton;
	@FXML TextArea codewindow;
	@FXML Circle statusLight;
	@FXML Text statusAnweisung;
	@FXML Label lbcounter;
	@FXML Text aufgabenName;

	private Status currStatus;
	private Project project;
	private Logic projectLogic;
	private CodeTemplate ct;
	private boolean _baby;
	private Timer _timer;
	private double babyTime;

	public Project_Controller(Status currStatus, Project project, boolean baby, double minuten)
	{
		this.currStatus = currStatus;
		this.project = project;
		projectLogic = new Logic(project.getTitle(),currStatus);
		ct = (CodeTemplate) project.getTestTemplates().get(0);
		_baby = baby;
		babyTime = minuten;
	}

	public void initialize(URL url, ResourceBundle rb) {
            currStatus = projectLogic.getStatus();
            _timer = new Timer();
            _timer.schedule(new TimerTask()
		{
			@Override
			public void run()
			{
				Platform.runLater(() ->
				{

					if (_baby) SetTimerLabel();
				});
			}
		}, 1000, 1000);
        	fillWithTemplate();
    		statusLight.setFill(Color.RED);
    		statusAnweisung.setText("Write Tests");

	}



	private void SetTimerLabel(){

		double _secondsElapsed = babyTime*60;

		_secondsElapsed--;

		int minutes = (int) (_secondsElapsed / 60);
		int seconds = (int) (_secondsElapsed % 60);

		lbcounter.setText(String.format("%d:%02d", minutes, seconds));
	}

	@FXML
	private void handleTestButtonAction(ActionEvent event) throws IOException
	{
		if( projectLogic.getCounterActive() == true ) return;
		projectLogic.Input(Befehl.DoRed, ct.getFilename(), codewindow.getText());
		Status oldStatus = currStatus;
		currStatus = projectLogic.getStatus();
		if( currStatus == Status.Red && oldStatus != currStatus )
		{
			ct = (CodeTemplate) project.getTestTemplates().get(projectLogic.getAufgabe());
			fillWithTemplate();
			statusLight.setFill(Color.RED);
			statusAnweisung.setText("Write Test");
		}
	}

	@FXML
	private void handleCodeButtonAction(ActionEvent event) throws IOException
	{
		if( projectLogic.getCounterActive() == true ) return;
		projectLogic.Input(Befehl.DoGreen, ct.getFilename(), codewindow.getText());  //Gareth: sind es die korrekten Strings?
		Status oldStatus = currStatus;
		currStatus = projectLogic.getStatus();
		if( currStatus == Status.Green && oldStatus != currStatus )
		{
			ct = project.getImplementationTemplates().get(projectLogic.getAufgabe());
			fillWithTemplate();
			statusLight.setFill(Color.GREEN);
			statusAnweisung.setText("Write Code.");
		}
	}

	@FXML
	private void handleRefractoringButtonAction(ActionEvent event) throws IOException
	{
		if( projectLogic.getCounterActive() == true ) return;
		projectLogic.Input(Befehl.DoRefactoring, ct.getFilename(), codewindow.getText());  //Gareth: sind es die korrekten Strings?
		currStatus = projectLogic.getStatus();
		if( currStatus == Status.Refactoring )
		{
			statusLight.setFill(Color.BLACK);
			statusAnweisung.setText("Refactoring");
		}
	}

	@FXML
	private void handleBackButtonAction(ActionEvent event) throws IOException
	{
		// Wechselt in das Main-Fenster, wenn der Start Button geklickt wird
		Stage stage = (Stage) backButton.getScene().getWindow();
		Parent root = FXMLLoader.load(getClass().getResource("/main_window.fxml"));
		Scene scene = new Scene(root, 640, 480);
		stage.setScene(scene);
		stage.setResizable(false);
		stage.setTitle("TDDT");
		stage.show();
	}

	private void fillWithTemplate()
	{
		aufgabenName.setText(ct.getFilename());
		codewindow.setText(ct.getContent());
	}
}
