package application;

import javafx.util.Duration;
import javafx.animation.FadeTransition;
import javafx.animation.KeyFrame;
import javafx.animation.KeyValue;
import javafx.animation.ParallelTransition;
import javafx.animation.ScaleTransition;
import javafx.animation.Timeline;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.effect.Glow;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Line;

public class BinarySearchTree {
	
	/*
	 * Constant Values
	 * */
	public final static int START_Y = 50; // Initial coordinate of binary search tree node in y-axis 
    public final static int DELTA_X = 50; // Horizontal spacing
    public final static int DELTA_Y = 50; // Vertical spacing between levels
	public final static double K = 13;    // Distance of chord from center, to calculate the start of edges
	public final static double DIST = Math.sqrt(Math.pow(BSTNode.RADIUS, 2) - Math.pow(K, 2));  // Distance of line from the edge point starting to perpendicular bisector
	
	
	/*
	 * Attributes of Binary Search Tree Class
	 * */
	public double startX;
	public BSTNode root; 
	public BSTNode lastNode;
	public Pane pane;
	
	/*
	 * Constructor sets root to null and initializes pane
	 * */
	BinarySearchTree(Pane newPane) {
		pane = newPane;
		root = null;
	}
	
	/*
	 * Method to insert the value in binary search tree
	 * */
	public void insert(int val) {
		
		// When binary search tree is empty
		if(root == null) { 
			this.startX = pane.getWidth() / 2; // At middle of pane
			BSTNode newNode = new BSTNode(val, startX,  START_Y); // insert at the center of the pane
			root = newNode;
			pane.getChildren().addAll(newNode.circle, newNode.text); // Add node to pane
			root.addLineToPane(pane); // Add both the left and right lines to pane
			lastNode = root;
			return; 
		}
		if(!this.contains(this.root, val)) {
			this.insertUtil(this.root, val, root.circle.getCenterX(), root.circle.getCenterY(), false); // Helper method to insert the new value
			resizeTree(); // For visually balancing the binary search Tree
		}
	}
	
	/*
	 * Method for searching given value
	 * */
	public void search(int val) {
		Duration[] delay = {Duration.seconds(1)};
		Timeline timeline = new Timeline();
		if(this.utilSearch(root, val, timeline, delay)) {
			timeline.setOnFinished(event -> {
				Controller.showAlert(val + " found in binary search tree", "Value found", AlertType.CONFIRMATION);
			});
			timeline.play();
		} else {
			timeline.setOnFinished(event -> {
				Controller.showAlert(val + " not found in binary search tree", "Value not found", AlertType.INFORMATION);
			});
			timeline.play();
		}
	}
	
	
	/*
	 * Method for deleting a particular node
	 * */
	public void remove(int val) {
		if(root == null) {
			Controller.showAlert("Cannot delete from an empty binary search tree", "Empty BST", AlertType.WARNING);
			return;
		} else {
			root = this.utilRemove(this.root, val, null);
			resizeTree();
		}
	}
	
	/*
	 * Method for in-order traversal
	 * */
	public void inorder() {
		if(root == null) {
			Controller.showAlert("Can't perform inorder traversal because binary search tree is empty", "BST is empty", AlertType.INFORMATION);
			return;
		}
		
		Duration[] delay = {Duration.seconds(1)};
		Timeline timeline = new Timeline();
		this.utilInorder(root, timeline, delay);
		timeline.play();
	}
	
	/*
	 * Method for clearing binary search tree
	 * */
	public void clear() {
		root = null;
	}
	/*
	 * Private method for checking if the node is present in Binary Search Tree or not
	 * @param 
	 * */
	private boolean contains(BSTNode root, int val) {
		if(root == null) {
			return false;
		}
		
		if(root.value == val) {
			return true;
		} 
		else if(root.value < val) {
			return contains(root.right, val);
		} else {
			return contains(root.left, val);
		}
	}
	
	/*
	 * Utility Method for inserting new node in given position
	 * */
	private BSTNode insertUtil(BSTNode root, int val, double centerX, double centerY, boolean isLeft) {
		
		if(root == null) {
			double newCenterX, newCenterY = centerY + DELTA_Y;  // Center of new node
			BSTNode newNode;
			
			if(isLeft) {
				newCenterX = centerX - DELTA_X; // center of new node
			} else {
				newCenterX = centerX + DELTA_X;
			}
			
			newNode = new BSTNode(val, newCenterX, newCenterY); // creating new node
			pane.getChildren().addAll(newNode.circle, newNode.text); // add to the pane
			newNode.addLineToPane(pane);
			nodeAppearance(newNode);
			
			lastNode = newNode;
			return newNode;
		}
		
		if(root.value < val) {
			// visit right Subtree
			root.right = insertUtil(root.right, val, root.circle.getCenterX(), root.circle.getCenterY(), false);  
		} else {
			// Visit left subtree
			root.left = insertUtil(root.left, val, root.circle.getCenterX(), root.circle.getCenterY(), true); 
		}
		return root;
	}
	
	/*
	 * Method for animation of node appearance at insertion
	 * */
	private void nodeAppearance(BSTNode newNode) {
		ScaleTransition nodeAppearance = new ScaleTransition(Duration.seconds(0.5), newNode.circle);
        nodeAppearance.setFromX(0.1);
//        nodeAppearance.setFromX(0.1);
        nodeAppearance.setToX(1);
        nodeAppearance.setToY(1);
        
        nodeAppearance.play();
	}
	
	/*
	 * Method for animation of line growing
	 * */
	private void lineGrowing(Line line, double endX, double endY) {
		Timeline timeline = new Timeline();

	    KeyFrame keyFrame = new KeyFrame(
	            Duration.seconds(1), // Animation duration
	            new KeyValue(line.endXProperty(), endX),
	            new KeyValue(line.endYProperty(), endY)
	    );
	    
	    timeline.getKeyFrames().add(keyFrame);
	    timeline.play();
	}
	
	/*
	 * Method for re-adjusting the centers of nodes after newly inserted node
	 * */
	private void resizeTree() {
		
		double startingPoint = this.startX;
		this.resizeWidths(root);  // Update the left and right width of every node
		
		if(root != null) {
			// Update starting point
			if(this.root.leftWidth > startingPoint) {
				startingPoint = this.root.leftWidth;
			} else if(this.root.rightWidth > startingPoint) {
				startingPoint = Math.max(this.root.leftWidth, 2 * startingPoint - this.root.rightWidth);
			}
			
			// Assign new positions
			root.updatePositions(startingPoint, START_Y);
			
			this.setNewPositions(root.left, startingPoint, START_Y + DELTA_Y, -1, startingPoint - DIST, START_Y + K, root.leftEdge);				
			this.setNewPositions(root.right, startingPoint, START_Y + DELTA_Y, 1, startingPoint + DIST, START_Y + K, root.rightEdge);
		}
	}

	/*
	 * Method for updating the centers of circle with new positions 
	 * */
	private void setNewPositions(BSTNode node, double xPosition, double yPosition, int side, double lineStartX, double lineStartY, Line line) {
	    if (node == null) {
	        return;
	    }
	    
	    if (side == -1) {
	        // Position the node based on the left width
	        xPosition = xPosition - node.rightWidth;
	    } else if (side == 1) {
	        // Position the node based on the right width
	        xPosition = xPosition + node.leftWidth;
	    }
	    
	    // Update the center of circle based on new positions
	    node.updatePositions(xPosition, yPosition);
	    
	    // make lines visible
	    BSTNode.showLine(line);
	    
	    // Show animation for growing line from start -> end, only for newly inserted node    
	    if(node == lastNode) {
	    	lineGrowing(line, xPosition, yPosition - BSTNode.RADIUS);	    	
	    } else {
	    	line.setEndX(xPosition);
	    	line.setEndY(yPosition - BSTNode.RADIUS);
	    }
	    

	    // Recursively set positions for left and right children
	    setNewPositions(node.left, xPosition, yPosition + DELTA_Y, -1, xPosition - DIST, yPosition + K, node.leftEdge);
	    setNewPositions(node.right, xPosition, yPosition + DELTA_Y, 1, xPosition + DIST, yPosition + K, node.rightEdge);
	}
	
	private double resizeWidths(BSTNode node) {
		if (node == null) {
			return 0;
		}
		
		// Calculate widths for left and right subtrees
		node.leftWidth = Math.max(resizeWidths(node.left), DELTA_X / 2);
		node.rightWidth = Math.max(resizeWidths(node.right), DELTA_X / 2);
		
		// Return the combined width
		return node.leftWidth + node.rightWidth;
	}

	private boolean utilSearch(BSTNode root, int val, Timeline timeline, Duration[] delay) {
		if(root == null) {
			return false;
		}
		
		if(val == root.value) {
			
			highlightNode(root, delay[0], timeline);
			delay[0] = delay[0].add(Duration.seconds(2));
			return true;
		} else if(val > root.value) {
			
			highlightNode(root, delay[0], timeline);
			delay[0] = delay[0].add(Duration.seconds(2));
			return utilSearch(root.right, val, timeline, delay);
		} else {
			
			highlightNode(root, delay[0], timeline);
			delay[0] = delay[0].add(Duration.seconds(2));
			return utilSearch(root.left, val, timeline, delay);
		}
	}
	
	
	
	/*
	 * Utility method for deleting a node from BST
	 * */
	private BSTNode utilRemove(BSTNode node, int val, Line line) {
		if(node == null) {
			Controller.showAlert(val + " is not present in BST.", "Value not present", AlertType.INFORMATION);
			return node;
		}
		
		if(val > node.value) {
			node.right = utilRemove(node.right, val, node.rightEdge);
		} else if(val < node.value) {
			node.left = utilRemove(node.left, val, node.leftEdge);
//			return node;
		} else {
			// When node to be removed is leaf node
			if(node.right == null && node.left == null) {
				this.nodeDisappearance(node);
				if(line != null) {
					line.setStrokeWidth(0);					
				}
				return null;
			}
			// When node to be removed has one child node - at left
			else if(node.right == null) {
//				deleteNodeVisuallyWithOneChild(node, node.left, true, line);
				this.nodeDisappearance(node);
				if(line != null) {
					line.setStrokeWidth(0);					
				}
				node.leftEdge.setStrokeWidth(0);
				return node.left;
			}
			// When node to be removed has one child node - at right
			else if(node.left == null) {
				
				this.nodeDisappearance(node);
				if(line != null) {
					line.setStrokeWidth(0);					
				}
				node.rightEdge.setStrokeWidth(0);
				return node.right;
			}
			// When node to be removed has two child nodes
			else {
				BSTNode inorderPredecessor = node.left;
				
				while(inorderPredecessor.right != null) {
					inorderPredecessor = inorderPredecessor.right;
				}
				node.value = inorderPredecessor.value;
				node.left = utilRemove(node.left, node.value, node.leftEdge);
//				return node;
			}
		}
		return node;
	}
	
	/*
	 * Method for showing disappearance of node
	 * */
	private void nodeDisappearance(BSTNode node) {
		ScaleTransition nodeDisappear = new ScaleTransition(Duration.seconds(1), node.circle);
        nodeDisappear.setFromX(1);
        nodeDisappear.setFromX(1);
        nodeDisappear.setToX(0);
        nodeDisappear.setToY(0);
        
        FadeTransition fadeEffect = new FadeTransition(Duration.seconds(1), node.circle);
        fadeEffect.setFromValue(1.0);
        fadeEffect.setToValue(0.0);
//        nodeDisappear.play();
        
        ParallelTransition parallelTransition = new ParallelTransition(nodeDisappear, fadeEffect);
        
        parallelTransition.setOnFinished(event -> {
        	pane.getChildren().remove(node.circle);
        	pane.getChildren().remove(node.text);
        });
        
        parallelTransition.play();
	}
	
	/*
	 * Method for deleting node with one child - visually
	 * */
	private void deleteNodeVisuallyWithOneChild(BSTNode parent, BSTNode child, boolean isLeft, Line line) {
		double x = parent.circle.getCenterX(), y = parent.circle.getCenterY();
		
		// Delete current node from pane
		this.nodeDisappearance(parent);
		pane.getChildren().removeAll(parent.text);
//		// Delete lines
//		if(line != null) {
//			line.setStrokeWidth(0);
//		}
//		
//		if(isLeft) {
//			parent.leftEdge.setStrokeWidth(0);
//		} else {
//			parent.rightEdge.setStrokeWidth(0);
//		}
	}
	
	/*
	 * Utility method for in-order traversal of binary search tree
	 * */
	private void utilInorder(BSTNode root, Timeline timeline, Duration[] delay) {
		if(root == null) {
			return;
		}
		
		utilInorder(root.left, timeline, delay);

		highlightNode(root, delay[0], timeline);
		delay[0] = delay[0].add(Duration.seconds(2));
		
		utilInorder(root.right, timeline, delay);
	}
	
	/*
	 * Method for showing current node highlighted
	 * */
	private void highlightNode(BSTNode root, Duration delay, Timeline timeline) {
		KeyFrame glowFrame = new KeyFrame(
				delay,
				event -> applyGlowEffect(root)
			);
			
	KeyFrame resetFrame = new KeyFrame(
				delay.add(Duration.seconds(1)), 
				event -> resetNodeEffect(root)
			);
			
	timeline.getKeyFrames().addAll(glowFrame, resetFrame);
	}
	
	/*
	 * for glow effect
	 * */
	private void applyGlowEffect(BSTNode node) {
        node.circle.setStroke(Color.RED);
        Glow glow = new Glow(1.0);
        node.circle.setEffect(glow);
    }
	
	/*
	 * For resetting the style of node
	 * */
	private void resetNodeEffect(BSTNode root) {
		root.circle.setStroke(Color.BLACK);
		root.circle.setEffect(null);
	}
}	

