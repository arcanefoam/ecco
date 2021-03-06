package at.jku.isse.ecco.tree;

import at.jku.isse.ecco.util.Trees;

import java.util.Map;
import java.util.Optional;

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkNotNull;

public class NodeOperator {

	private Node.Op node;

	public NodeOperator(Node.Op node) {
		this.node = node;
	}


	public String getLabel() {
		if (this.node instanceof RootNode)
			return "root";
		else if (this.node.getArtifact() != null)
			return this.node.getArtifact().toString();
		else
			return "null";
	}

	@Override
	public String toString() {
		return this.getLabel();
	}

	@Override
	public int hashCode() {
		return this.node.getArtifact() != null ? this.node.getArtifact().hashCode() : 0;
	}

	@Override
	public boolean equals(Object other) {
		if (this.node == other) return true;
		if (other == null) return false;
		if (!(other instanceof Node)) return false;

		Node otherNode = (Node) other;

		if (this.node.getArtifact() == null)
			return otherNode.getArtifact() == null;

		return this.node.getArtifact().equals(otherNode.getArtifact());
	}


	// # OPERATIONS ####################################################################################################

	/**
	 * See {@link at.jku.isse.ecco.util.Trees#slice(Node.Op, Node.Op)}
	 */
	public void slice(Node.Op node) {
		Trees.slice(this.node, node);
	}

	/**
	 * See {@link at.jku.isse.ecco.util.Trees#merge(Node.Op, Node.Op)}
	 */
	public void merge(Node.Op node) {
		Trees.merge(this.node, node);
	}

	/**
	 * See {@link at.jku.isse.ecco.util.Trees#sequence(Node)}
	 */
	public void sequence() {
		Trees.sequence(this.node);
	}

	/**
	 * See {@link at.jku.isse.ecco.util.Trees#updateArtifactReferences(Node.Op)}
	 */
	public void updateArtifactReferences() {
		Trees.updateArtifactReferences(this.node);
	}

	/**
	 * See {@link at.jku.isse.ecco.util.Trees#extractMarked(Node.Op)}
	 */
	public Node.Op extractMarked() {
		return Trees.extractMarked(this.node);
	}

	/**
	 * See {@link at.jku.isse.ecco.util.Trees#countArtifacts(Node)}
	 */
	public int countArtifacts() {
		return Trees.countArtifacts(this.node);
	}

	/**
	 * See {@link at.jku.isse.ecco.util.Trees#computeDepth(Node)}
	 */
	public int computeDepth() {
		return Trees.computeDepth(this.node);
	}

	/**
	 * See {@link at.jku.isse.ecco.util.Trees#countArtifactsPerDepth(Node)}
	 */
	public Map<Integer, Integer> countArtifactsPerDepth() {
		return Trees.countArtifactsPerDepth(this.node);
	}

	/**
	 * See {@link at.jku.isse.ecco.util.Trees#print(Node)}
	 */
	public void print() {
		Trees.print(this.node);
	}

	/**
	 * See {@link at.jku.isse.ecco.util.Trees#checkConsistency(Node.Op)}
	 */
	public void checkConsistency() {
		Trees.checkConsistency(this.node);
	}


	// # PROPERTIES ####################################################################################################

	public <T> Optional<T> getProperty(final String name) {
		checkNotNull(name);
		checkArgument(!name.isEmpty(), "Expected non-empty name, but was empty.");

		Optional<T> result = Optional.empty();
		if (this.node.getProperties().containsKey(name)) {
			final Object obj = this.node.getProperties().get(name);
			try {
				@SuppressWarnings("unchecked")
				final T item = (T) obj;
				result = Optional.of(item);
			} catch (final ClassCastException e) {
				System.err.println("Expected a different type of the property.");
			}
		}

		return result;
	}

	public <T> void putProperty(final String name, final T property) {
		checkNotNull(name);
		checkArgument(!name.isEmpty(), "Expected non-empty name, but was empty.");
		checkNotNull(property);

		this.node.getProperties().put(name, property);
	}

	public void removeProperty(String name) {
		checkNotNull(name);

		this.node.getProperties().remove(name);
	}

}
