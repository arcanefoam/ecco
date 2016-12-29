package at.jku.isse.ecco.composition;

import at.jku.isse.ecco.core.Association;
import at.jku.isse.ecco.tree.RootNode;

/**
 * A lazy composition root node.
 */
public class LazyCompositionRootNode extends LazyCompositionNode implements RootNode, RootNode.Op {

	public LazyCompositionRootNode() {
		this(new DefaultOrderSelector());
	}

	public LazyCompositionRootNode(OrderSelector orderSelector) {
		super(orderSelector);
	}


	@Override
	public boolean isUnique() {
		return true;
	}

	@Override
	public boolean isAtomic() {
		return false;
	}


	@Override
	public RootNode.Op createNode() {
		return new LazyCompositionRootNode();
	}


	@Override
	public void setContainingAssociation(Association.Op containingAssociation) {
		// do nothing
	}

	@Override
	public Association getContainingAssociation() {
		return null;
	}

	@Override
	public String toString() {
		return "root";
	}

}
