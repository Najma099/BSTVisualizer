package application;

import java.net.URL;
import java.util.ResourceBundle;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.layout.Pane;

public class Controller implements Initializable {
	private BinarySearchTree bst; // bst object to hold the binary search tree
	@FXML
	private TextField rootVal; // TextField for getting text from user
	
	@FXML
	private Button insertBtn, searchBtn, deleteBtn; // Buttons to do operations
	
	@FXML
	private Pane pane;  // Pane to hold the binary search tree
	public void insertInBST() {
		System.out.println("Insert: " + rootVal.getText());
		try {
			bst.insert(Integer.parseInt(rootVal.getText())); // Calling insert function in bst
			rootVal.clear();
		} catch(NumberFormatException nfe) {
			showAlert("Please input an integer number.", "Invalid Input", AlertType.ERROR);
		}
	}	
	
	public void searchInBST() {
		System.out.println("Search: " + rootVal.getText());
		try {
			bst.search(Integer.parseInt(rootVal.getText())); // Calling search function in binary search tree
			rootVal.clear();
		} catch(NumberFormatException nfe) {
			showAlert("Please input an integer number.", "Invalid Input", AlertType.ERROR);
		}
	}
	
	public void deleteInBST() {
		System.out.println("Delete: " + rootVal.getText());
		try {
			bst.remove(Integer.parseInt(rootVal.getText())); // Calling search function in binary search tree
			rootVal.clear();
		} catch(NumberFormatException nfe) {
			showAlert("Please input an integer number.", "Invalid Input", AlertType.ERROR);
		}
	}

	@Override
	public void initialize(URL arg0, ResourceBundle arg1) {
		bst = new BinarySearchTree(pane); // initializing empty bst
	}
	
	public void inorder() {
		System.out.println("Inorder");
		bst.inorder();
	}
	
	public void clearBST() {
		pane.getChildren().clear(); // To clear all the circle and text
		bst.clear(); // To empty the bst
	}
	
	/*
	 * Method to create and show alert
	 * */
	public static void showAlert(String msg, String title, AlertType type) {
		Alert alert = new Alert(type);
		alert.setTitle(title);
		alert.setContentText(msg);
		alert.setHeaderText(null);
		alert.show();
	}
}
