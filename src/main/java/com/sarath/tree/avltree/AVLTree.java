package com.sarath.tree.avltree;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.Scanner;

public class AVLTree {

	private AVLTreeNode root = null;

	public void insert(int value) {
		AVLTreeNode newNode = AVLTreeNode.createNode(value);
		if (root == null) {
			root = newNode;
		} else {
			insertBST(newNode, root);
			trackUnbalancedNode(newNode);
		}
	}

	/**
	 * Recursive binary search insertion
	 * 
	 * @param newNode
	 * @param node
	 * @return
	 */
	private AVLTreeNode insertBST(AVLTreeNode newNode, AVLTreeNode node) {
		if (node.getValue() > newNode.getValue()) {
			if (node.getLeftSubTree() == null) {
				node.setLeftSubTree(newNode);
				newNode.setParent(node);
				return newNode;
			} else {
				return insertBST(newNode, node.getLeftSubTree());
			}
		} else {
			if (node.getRightSubTree() == null) {
				node.setRightSubTree(newNode);
				newNode.setParent(node);
				return newNode;
			} else {
				return insertBST(newNode, node.getRightSubTree());
			}
		}
	}

	private void delete(int value) {
		AVLTreeNode actionPositionNode = deleteBST(value, root);
		traverseUpAndDoTriNodeRestructing(actionPositionNode);
	}

	/**
	 * Deletion on a binary search tree
	 * 
	 * @param value
	 * @param node
	 * @return
	 */
	private AVLTreeNode deleteBST(int value, AVLTreeNode node) {
		if (node == null)
			return null;
		if (node.getValue() > value) {
			return deleteBST(value, node.getLeftSubTree());
		} else if (node.getValue() < value) {
			return deleteBST(value, node.getRightSubTree());
		} else {
			AVLTreeNode actionPositionNode = null;
			if (node.getLeftSubTree() == null && node.getRightSubTree() == null) {
				actionPositionNode = deleteLeafNode(node);
			} else {
				if (node.getLeftSubTree() != null && node.getRightSubTree() != null) {
					AVLTreeNode inOrderSuccessor = getInOrderSuccessor(node);
					actionPositionNode = inOrderSuccessor.getParent();
					substituteInOrderSuccessorWithItsRightChild(inOrderSuccessor);
					replceDeletingNodeWithInOrderSuccessor(node, inOrderSuccessor);
				} else {
					actionPositionNode = replaceDeletingWithItsOnlyChild(node);
				}
			}
			return actionPositionNode;
		}
	}

	private AVLTreeNode deleteLeafNode(AVLTreeNode node) {
		AVLTreeNode parent = node.getParent();
		if (isLeftChild(node)) {
			makeAsLeftChild(parent, null);
		} else if (isRightChild(node)) {
			makeAsRightChild(parent, null);
		} else {
			root = null;
		}
		updateHeight(parent);
		node.setParent(null);
		return parent;
	}

	private AVLTreeNode getInOrderSuccessor(AVLTreeNode node) {
		// go to right child
		node = node.getRightSubTree();
		while (node.getLeftSubTree() != null) {
			node = node.getLeftSubTree();
		}
		return node;
	}

	/**
	 * Move up along path to root and balance node if any
	 * 
	 * @param node
	 */
	private void trackUnbalancedNode(AVLTreeNode node) {
		AVLTreeNode z = null, y = null, x = null;
		while (node != null) {
			x = y;
			y = z;
			z = node;
			updateHeight(z);
			if (!z.isBalanced()) {
				if (z.getLeftSubTree() == y && y.getLeftSubTree() == x) {
					leftLeftCase(z);
				} else if (z.getLeftSubTree() == y && y.getRightSubTree() == x) {
					leftRightCase(z);
				} else if (z.getRightSubTree() == y && y.getRightSubTree() == x) {
					rightRightCase(z);
				} else if (z.getRightSubTree() == y && y.getLeftSubTree() == x) {
					rightLeftCase(z);
				}
				node = node.getParent().getParent();
			} else {
				node = node.getParent();
			}
		}
	}

	/**
	 * On deletion move up along the path to root and perform Trinode restructuring
	 * for balancing
	 * 
	 * @param node
	 */
	private void traverseUpAndDoTriNodeRestructing(AVLTreeNode node) {
		AVLTreeNode x, y, z;
		while (node != null) {
			z = node;
			updateHeight(z);
			if (!z.isBalanced()) {
				int zLeftHeight = z.getLeftSubTree() == null ? -1 : z.getLeftSubTree().getHeight();
				int zRightHeight = z.getRightSubTree() == null ? -1 : z.getRightSubTree().getHeight();
				y = zLeftHeight >= zRightHeight ? z.getLeftSubTree() : z.getRightSubTree();
				int yLeftHeight = y.getLeftSubTree() == null ? -1 : y.getLeftSubTree().getHeight();
				int yRightHeight = y.getRightSubTree() == null ? -1 : y.getRightSubTree().getHeight();
				x = yLeftHeight >= yRightHeight ? y.getLeftSubTree() : y.getRightSubTree();
				node = triNodeRestructuring(z, y, x);
			}
			node = node.getParent();
		}
	}

	/**
	 * Tri node restructuring algorithm
	 * 
	 * @param z
	 * @param y
	 * @param x
	 * @return
	 */
	private AVLTreeNode triNodeRestructuring(AVLTreeNode z, AVLTreeNode y, AVLTreeNode x) {
		AVLTreeNode a, b, c;
		AVLTreeNode t1, t2, t3,t4;
		if (z.getLeftSubTree() == y && y.getLeftSubTree() == x) {
			c = z;
			b = y;
			a = x;
			t1 = a.getLeftSubTree();
			t2  = a.getRightSubTree();
			t3 = b.getRightSubTree();
			t4 = c.getRightSubTree();
		} else if (z.getLeftSubTree() == y && y.getRightSubTree() == x) {
			c = z;
			b = x;
			a = y;
			t1 = a.getLeftSubTree();
			t2  = b.getLeftSubTree();
			t3 = b.getRightSubTree();
			t4 = c.getRightSubTree();
		} else if (z.getRightSubTree() == y && y.getRightSubTree() == x) {
			c = x;
			b = y;
			a = z;
			t1 = a.getLeftSubTree();
			t2  = b.getLeftSubTree();
			t3 = c.getLeftSubTree();
			t4 = c.getRightSubTree();
		} else {
			c = y;
			b = x;
			a = z;
			t1 = a.getLeftSubTree();
			t2  = b.getLeftSubTree();
			t3 = b.getRightSubTree();
			t4 = c.getRightSubTree();
		}

		if (isRightChild(z)) {
			makeAsRightChild(z.getParent(), b);
		} else if (isLeftChild(z)) {
			makeAsLeftChild(z.getParent(), b);
		} else {
			b.setParent(z.getParent());
		}
		if (b.getParent() == null) {
			root = b;
		}
		// make sub trees t1, t2, t3, t4 as children of a & c
		makeAsLeftChild(a,t1);
		makeAsRightChild(a, t2);
		makeAsLeftChild(c, t3);
		makeAsRightChild(c, t4);
		// make a & c as children of b
		makeAsLeftChild(b, a);
		makeAsRightChild(b, c);

		updateHeight(a);
		updateHeight(c);
		updateHeight(b);
		return b;
	}

	private AVLTreeNode replaceDeletingWithItsOnlyChild(AVLTreeNode deletingNode) {
		AVLTreeNode rightChild = deletingNode.getRightSubTree();
		AVLTreeNode leftChild = deletingNode.getLeftSubTree();
		AVLTreeNode nonNullChild = rightChild != null ? rightChild : leftChild;
		AVLTreeNode parent = deletingNode.getParent();
		if (nonNullChild != null) {
			nonNullChild.setParent(parent);
		}
		if (isLeftChild(deletingNode)) {
			makeAsLeftChild(parent, nonNullChild);
		} else {
			makeAsRightChild(parent, nonNullChild);
		}
		if (nonNullChild.getParent() == null) {
			root = nonNullChild;
		}
		updateHeight(parent);
		return nonNullChild;
	}

	private void substituteInOrderSuccessorWithItsRightChild(AVLTreeNode inOrderSuccessor) {
		AVLTreeNode rightChild = inOrderSuccessor.getRightSubTree();
		AVLTreeNode parent = inOrderSuccessor.getParent();
		if (rightChild != null) {
			rightChild.setParent(parent);
		}
		if (isLeftChild(inOrderSuccessor)) {
			makeAsLeftChild(parent, rightChild);
		} else if (isRightChild(inOrderSuccessor)) {
			makeAsRightChild(parent, rightChild);
		}
		updateHeight(parent);
	}

	private void replceDeletingNodeWithInOrderSuccessor(AVLTreeNode deleteingNode, AVLTreeNode inOrderSuccessor) {
		AVLTreeNode parentOfDeletingNode = deleteingNode.getParent();
		AVLTreeNode rightChildOfDeletingNode = deleteingNode.getRightSubTree();
		AVLTreeNode leftChildOfDeletingNode = deleteingNode.getLeftSubTree();

		if (isLeftChild(deleteingNode)) {
			makeAsLeftChild(parentOfDeletingNode, inOrderSuccessor);
		} else if (isRightChild(deleteingNode)) {
			makeAsRightChild(parentOfDeletingNode, inOrderSuccessor);
		} else {
			inOrderSuccessor.setParent(parentOfDeletingNode);
		}
		makeAsLeftChild(inOrderSuccessor, leftChildOfDeletingNode);
		makeAsRightChild(inOrderSuccessor, rightChildOfDeletingNode);

		if (inOrderSuccessor.getParent() == null) {
			root = inOrderSuccessor;
		}
		updateHeight(inOrderSuccessor);
	}

	/**
	 * Update height
	 * 
	 * @param node
	 */
	private void updateHeight(AVLTreeNode node) {
		if (node != null) {
			node.updateHeight();
		}
	}

	/**
	 * Make b as the left child of a
	 * 
	 * @param node
	 * @param child
	 */
	private void makeAsLeftChild(AVLTreeNode node, AVLTreeNode child) {
		if (node == null)
			return;
		node.setLeftSubTree(child);
		if (child != null)
			child.setParent(node);
	}

	/**
	 * Make b as the right child of a
	 * 
	 * @param node
	 * @param child
	 */
	private void makeAsRightChild(AVLTreeNode node, AVLTreeNode child) {
		if (node == null)
			return;
		node.setRightSubTree(child);
		if (child != null)
			child.setParent(node);
	}

	private boolean isRightChild(AVLTreeNode node) {
		if (node == null || node.getParent() == null)
			return false;
		return node.getParent().getRightSubTree() == node;
	}

	private boolean isLeftChild(AVLTreeNode node) {
		if (node == null || node.getParent() == null)
			return false;
		return node.getParent().getLeftSubTree() == node;
	}

	private void leftLeftCase(AVLTreeNode node) {
		rotateRight(node);
	}

	private void leftRightCase(AVLTreeNode node) {
		rotateLeft(node.getLeftSubTree());
		rotateRight(node);
	}

	private void rightRightCase(AVLTreeNode node) {
		rotateLeft(node);
	}

	private void rightLeftCase(AVLTreeNode node) {
		rotateRight(node.getRightSubTree());
		rotateLeft(node);
	}

	private AVLTreeNode rotateRight(AVLTreeNode node) {
		AVLTreeNode parent = node.getParent();
		AVLTreeNode leftNode = node.getLeftSubTree();
		AVLTreeNode leftNodeRightChild = leftNode.getRightSubTree();

		makeAsLeftChild(node, leftNodeRightChild);

		if (isLeftChild(node)) {
			makeAsLeftChild(parent, leftNode);
		} else if (isRightChild(node)) {
			makeAsRightChild(parent, leftNode);
		} else {
			leftNode.setParent(parent);
		}

		makeAsRightChild(leftNode, node);

		updateHeight(node);
		updateHeight(leftNode);
		updateHeight(parent);

		if (leftNode.getParent() == null) {
			root = leftNode;
		}
		return leftNode;

	}

	private AVLTreeNode rotateLeft(AVLTreeNode node) {
		AVLTreeNode parent = node.getParent();
		AVLTreeNode rightNode = node.getRightSubTree();
		AVLTreeNode rightNodeLeftChild = rightNode.getLeftSubTree();

		makeAsRightChild(node, rightNodeLeftChild);

		if (isLeftChild(node)) {
			makeAsLeftChild(parent, rightNode);
		} else if (isRightChild(node)) {
			makeAsRightChild(parent, rightNode);
		} else {
			rightNode.setParent(parent);
		}

		makeAsLeftChild(rightNode, node);

		updateHeight(node);
		updateHeight(rightNode);
		updateHeight(parent);
		if (rightNode.getParent() == null) {
			root = rightNode;
		}
		return rightNode;
	}

	public void display(AVLTreeNode travNode) {
		if (root != null)
			System.out.print(root.toString());
	}

	public static void main(String[] args) {
		BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(System.in));
		AVLTree avlTree = new AVLTree();
		int menuItem = 6;
		try {
			do {
				int value;
				System.out.println("Menu\n1. Insert\n2. Insert from array\n3. Delete\n5. Display\n6. Exit");
				menuItem = Integer.parseInt(bufferedReader.readLine());
				switch (menuItem) {
				case 1:
					System.out.println("Enter node value");
					value = Integer.parseInt(bufferedReader.readLine());
					avlTree.insert(value);
					break;
				case 2:
					System.out.println("Enter nodes as a list, enter a letter to stop");
					Scanner scanner = new Scanner(System.in);
					while (scanner.hasNextInt()) {
						value = scanner.nextInt();
						avlTree.insert(value);
					}
					break;
				case 3:
					System.out.println("Enter node value to delete");
					value = Integer.parseInt(bufferedReader.readLine());
					avlTree.delete(value);
					break;
				case 5:
					avlTree.display(avlTree.root);
					break;
				}

			} while (menuItem != 6);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private static class AVLTreeNode {

		private AVLTreeNode left;
		private AVLTreeNode right;
		private AVLTreeNode parent;
		private int value;
		private int height;

		public static AVLTreeNode createNode(int value) {
			return new AVLTreeNode(value);
		}

		public AVLTreeNode(int value) {
			this.value = value;
			this.height = 0;
			this.parent = null;
		}

		public int getHeight() {
			return this.height;
		}

		public void setHeight(int height) {
			this.height = height;
		}

		public AVLTreeNode getLeftSubTree() {
			return left;
		}

		public AVLTreeNode getRightSubTree() {
			return right;
		}

		public int getValue() {
			return value;
		}

		public void setParent(AVLTreeNode treeNode) {
			this.parent = treeNode;
		}

		public AVLTreeNode getParent() {
			return parent;
		}

		public void setLeftSubTree(AVLTreeNode treeNode) {
			this.left = treeNode;
		}

		public void setRightSubTree(AVLTreeNode treeNode) {
			this.right = treeNode;
		}

		public void setValue(int value) {
			this.value = value;
		}

		public boolean isBalanced() {
			int leftHeight = left == null ? -1 : left.height;
			int rightHeight = right == null ? -1 : right.height;
			return Math.abs(leftHeight - rightHeight) <= 1;
		}

		public void updateHeight() {
			int leftHeight = left == null ? -1 : left.getHeight();
			int rightHeight = right == null ? -1 : right.getHeight();
			this.height = Math.max(leftHeight, rightHeight) + 1;
		}

		public StringBuilder toString(StringBuilder prefix, boolean isTail, StringBuilder sb) {
			if (right != null) {
				right.toString(new StringBuilder().append(prefix).append(isTail ? "|   " : "    "), false, sb);
			}
			sb.append(prefix).append(isTail ? "└── " : "┌── ").append(
					value + "(" + (parent != null ? (parent.value + "{" + height + "}") : "{" + height + "}") + ")")
					.append("\n");
			if (left != null) {
				left.toString(new StringBuilder().append(prefix).append(isTail ? "    " : "|   "), true, sb);
			}
			return sb;
		}

		@Override
		public String toString() {
			return this.toString(new StringBuilder(), true, new StringBuilder()).toString();
		}

	}
}