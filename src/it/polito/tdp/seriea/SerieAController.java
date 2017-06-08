/**
 * Sample Skeleton for 'SerieA.fxml' Controller Class
 */

package it.polito.tdp.seriea;

import java.net.URL;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.ResourceBundle;

import it.polito.tdp.seriea.model.Model;
import it.polito.tdp.seriea.model.Season;
import it.polito.tdp.seriea.model.Team;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.TextArea;

public class SerieAController {
	
	private Model model;
	private List <Team> classifica;
	private List <Team> sequenza;

    @FXML // ResourceBundle that was given to the FXMLLoader
    private ResourceBundle resources;

    @FXML // URL location of the FXML file that was given to the FXMLLoader
    private URL location;

    @FXML // fx:id="boxSeason"
    private ChoiceBox<Season> boxSeason; // Value injected by FXMLLoader

    @FXML // fx:id="boxTeam"
    private ChoiceBox<Team> boxTeam; // Value injected by FXMLLoader

    @FXML // fx:id="txtResult"
    private TextArea txtResult; // Value injected by FXMLLoader

    @FXML
    void handleCarica(ActionEvent event) {
    	
    	txtResult.clear();
    	// prendo la stagione dalla tendina
    	Season stagione = boxSeason.getValue();
    	if( stagione == null){
    		txtResult.appendText("ERRORE: selezionare una stagione\n");
    		return;
    	}
    	
    	// popolo la tendina delle teams in base alla stagione selezionata
    	boxTeam.getItems().clear();
    	boxTeam.getItems().addAll(model.getTeams()) ;
    	Collections.sort(boxTeam.getItems(), new Comparator<Team>() {

			@Override
			public int compare(Team o1, Team o2) {
				return o1.getTeam().compareTo(o1.getTeam());
			}
		}) ;
    	
    	// calcolo la classifica
    	classifica = model.getClassifica(stagione);
    	txtResult.appendText("CLASSIFICA per la stagione " + stagione.getDescription() + ":\n");
    	for( Team t : classifica){
    		txtResult.appendText(t.getTeam() + " " + t.getPunteggio() + "\n");
    	}
    }

    @FXML
    void handleDomino(ActionEvent event) {
    	
    	txtResult.clear();
    	
    	Season stagione = boxSeason.getValue();
    	if( stagione == null){
    		txtResult.appendText("ERRORE: selezionare una stagione\n");
    		return;
    	}
    	
    	model.creaGrafo(stagione);
    	
    	sequenza = model.getSequenza(stagione);
    	
    	txtResult.appendText("Miglior DOMINO calcolato: lunghezza "+ sequenza.size()+"\n");
    	
    	txtResult.appendText("SEQUENZA per la stagione " + stagione.getDescription() + ":\n");
     	for( Team t : sequenza){
     		txtResult.appendText(t.getTeam() +  "\n");
     	}

    }

    @FXML // This method is called by the FXMLLoader when initialization is complete
    void initialize() {
        assert boxSeason != null : "fx:id=\"boxSeason\" was not injected: check your FXML file 'SerieA.fxml'.";
        assert boxTeam != null : "fx:id=\"boxTeam\" was not injected: check your FXML file 'SerieA.fxml'.";
        assert txtResult != null : "fx:id=\"txtResult\" was not injected: check your FXML file 'SerieA.fxml'.";
    }

	public void setModel(Model model) {
		this.model = model;
		
		// Inizializzo tendina
		boxSeason.getItems().addAll(model.getStagioni());
	
	}
}
